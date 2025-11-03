package com.switching.study_matching_site.service;

import ch.hsr.geohash.GeoHash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.EmptyRegion;
import com.switching.study_matching_site.domain.StudyPlace;

import com.switching.study_matching_site.dto.studyplace.CellCountDto;
import com.switching.study_matching_site.dto.studyplace.LocationResponseDto;
import com.switching.study_matching_site.repository.EmptyRegionRepository;
import com.switching.study_matching_site.repository.StudyPlaceRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class StudyPlaceService {

    private final StudyPlaceRepository studyPlaceRepository;
    private final KakaoLocalService kakaoLocalService;
    private static final int GEOHASH_PRECISION = 6;

    @Transactional
    public List<LocationResponseDto> searchNearbyRooms(double lat, double lng) throws JsonProcessingException {
        final int maxCandidates = 15; // 최대 15개
        final double radiusKm = 1.0;   // 하버사인 필터링 반경

        // 1) API 호출 (DB 저장 없이 바로 가져오기)
        List<JsonNode> apiResults = kakaoLocalService.searchStudyRooms("스터디룸", lng, lat);

        // 2) JsonNode → StudyPlace DTO 변환
        List<StudyPlace> candidates = apiResults.stream()
                .map(node -> {
                    StudyPlace sp = new StudyPlace();
                    sp.setPlaceName(node.get("place_name").asText());
                    sp.setLat(node.get("y").asDouble());
                    sp.setLng(node.get("x").asDouble());
                    sp.setAddress(node.get("address_name").asText());
                    sp.setRoadAddress(node.get("road_address_name").asText());
                    sp.setPhone(node.get("phone").asText());
                    return sp;
                })
                .collect(Collectors.toList());

        // 3) 하버사인 필터링 (실제 1km 내)
        List<StudyPlace> nearbyPlaces = filterWithinRadius(candidates, lat, lng, radiusKm);

        // 4) 거리순 정렬 + 최대 후보 제한
        nearbyPlaces.sort(Comparator.comparingDouble(
                p -> haversineDistance(lat, lng, p.getLat(), p.getLng())
        ));
        nearbyPlaces = nearbyPlaces.stream()
                .limit(maxCandidates)
                .collect(Collectors.toList());

        // 5) DTO 변환
        return convertToLocationResponseDtos(nearbyPlaces, lat, lng, maxCandidates);
    }

    // 위 경도 반올림
    @Transactional
    public List<LocationResponseDto> getNearbyWithRounding(double lat, double lng) throws JsonProcessingException {
        final int maxCandidates = 20;
        final int minCandidates = 5;

        // 1) 위도/경도 반올림 (예: 소수점 둘째 자리)
        double radiusKm = 1.2; // 원하는 탐색 반경
        double latRange = radiusKm / 111.0; // 위도: 1도 ≈ 111km
        double lngRange = radiusKm / (111.0 * Math.cos(Math.toRadians(lat))); // 경도: 위도 보정


        LocalDateTime cutoff = LocalDateTime.now().minusWeeks(1);
        // 1) DB에서 최근 1주일 내 데이터만 가져오기 (위치 필터링은 제외)

        // 2) DB에서 캐시된 후보 조회 (1주일 내 데이터만)
        List<StudyPlace> candidates = studyPlaceRepository.findRecentNearby(
                lat, lng, latRange, lngRange, cutoff
        );

        System.out.println("candidates.size() = " + candidates.size());
        // 3) 하버사인 필터링 (실제 1km 내)
        List<StudyPlace> nearbyPlaces = filterWithinRadius(candidates, lat, lng, 1.0);

        // 4) 후보 부족 시 API 호출
        if (nearbyPlaces.size() < minCandidates) {
            fetchAndUpdatePlaces(lat, lng);

            // API 호출 후 DB 재조회
            candidates = studyPlaceRepository.findRecentNearby(
                    lat, lng, latRange, lngRange, cutoff
            );

            nearbyPlaces = candidates.stream()
                    .filter(p -> haversineDistance(lat, lng, p.getLat(), p.getLng()) <= 1.0)
                    .collect(Collectors.toList());
        }

        // 5) 거리순 정렬 + 최대 후보 제한
        nearbyPlaces.sort(Comparator.comparingDouble(
                p -> haversineDistance(lat, lng, p.getLat(), p.getLng())
        ));
        // DTO 변환
        return convertToLocationResponseDtos(nearbyPlaces, lat, lng, maxCandidates);
    }

    // 지오 해시
    @Transactional
    public List<LocationResponseDto> getNearbyOrFetch(double lng, double lat) throws JsonProcessingException {
        final int maxCandidates = 20;
        final int minCandidates = 5;

        // 1) 사용자 위치 지오해시 생성
        String userHash = GeoHash.withCharacterPrecision(lat, lng, GEOHASH_PRECISION).toBase32();
        List<String> surroundingHashes = getSurroundingGeohashes(userHash);

        if (surroundingHashes.isEmpty()) {
            return Collections.emptyList();
        }

        // 2) DB 조회
        List<StudyPlace> candidates = studyPlaceRepository.findByGeohashIn(surroundingHashes);
        System.out.println("candidates.size() = " + candidates.size());

        // 3) 실제 거리 필터링
        List<StudyPlace> nearbyPlaces = filterWithinRadius(candidates, lat, lng, 1.0);

        // 4) 후보 부족 시 API 호출
        if (nearbyPlaces.size() < minCandidates) {
            fetchAndUpdatePlaces(lat, lng);

            // API 호출 후 DB 재조회
            candidates = studyPlaceRepository.findByGeohashIn(surroundingHashes);
            nearbyPlaces = filterWithinRadius(candidates, lat, lng, 1.0);
        }

        // 5) 거리순 정렬 + 최대 후보 제한
        nearbyPlaces.sort(Comparator.comparingDouble(
                p -> haversineDistance(lat, lng, p.getLat(), p.getLng())
        ));

        return convertToLocationResponseDtos(nearbyPlaces, lat, lng, maxCandidates);
    }


    @Transactional
    public void fetchAndUpdatePlaces(double lat, double lng) throws JsonProcessingException {
        List<JsonNode> nodes = kakaoLocalService.searchStudyRooms("스터디룸", lng, lat);
        if (nodes.isEmpty()) return;

        LocalDateTime now = LocalDateTime.now();

        for (JsonNode node : nodes) {
            String placeName = node.get("place_name").asText();
            String address = node.get("address_name").asText();

            StudyPlace place = studyPlaceRepository.findByPlaceNameAndAddress(placeName, address)
                    .orElse(new StudyPlace());

            place.setPlaceName(placeName);
            place.setAddress(address);
            place.setRoadAddress(node.path("road_address_name").asText(""));
            place.setPhone(node.path("phone").asText(""));
            place.setLat(node.get("y").asDouble());
            place.setLng(node.get("x").asDouble());
            place.setPlaceUrl(node.path("place_url").asText(""));
            place.setSource("kakao");
            place.setIsActive(true);
            place.setLastCheckedAt(now);
            place.setGeohash(GeoHash.withCharacterPrecision(place.getLat(), place.getLng(), GEOHASH_PRECISION).toBase32());

            studyPlaceRepository.save(place);
        }
    }

    @Transactional
    public void updateOldPlaces() {
        LocalDateTime threshold = LocalDateTime.now().minusWeeks(1);
        log.info("[updateOldPlaces] 갱신 시작, 기준일: {}", threshold);

        // 오래된 장소 모두 조회
        List<StudyPlace> places = studyPlaceRepository.findByLastCheckedAtBefore(threshold);
        log.info("갱신 대상 장소 수: {}", places.size());
        updatePlacesInBatch(places);

    }

    @Transactional
    protected void updatePlacesInBatch(List<StudyPlace> places) {
        LocalDateTime now = LocalDateTime.now();

        for (StudyPlace place : places) {
            try {
                log.info("갱신 시작: id={}, name={}", place.getId(), place.getPlaceName());

                // RateLimiter 적용된 API 호출
                JsonNode matchedNode = kakaoLocalService.searchByNameWithRateLimit(
                        place.getPlaceName(),
                        place.getAddress(),
                        place.getLng(),
                        place.getLat()
                );

                if (matchedNode != null) {
                    // 바뀔 수 있는 필드만 업데이트
                    place.setPlaceName(matchedNode.path("place_name").asText(place.getPlaceName()));
                    place.setRoadAddress(matchedNode.path("road_address_name").asText(place.getRoadAddress()));
                    place.setPhone(matchedNode.path("phone").asText(place.getPhone()));
                    place.setPlaceUrl(matchedNode.path("place_url").asText(place.getPlaceUrl()));
                    place.setLat(matchedNode.path("y").asDouble(place.getLat()));
                    place.setLng(matchedNode.path("x").asDouble(place.getLng()));
                    place.setIsActive(true);
                } else {
                    // 주소 불일치 또는 결과 없음 → 폐점 처리
                    place.setIsActive(false);
                }

                place.setLastCheckedAt(now);
                studyPlaceRepository.save(place);

                log.info("갱신 완료: id={}, name={}, isActive={}", place.getId(), place.getPlaceName(), place.getIsActive());

            } catch (JsonProcessingException e) {
                log.error("갱신 실패: id={}, name={}, error={}", place.getId(), place.getPlaceName(), e.getMessage(), e);
            }
        }
    }


    /* ---------------- Helper Methods ---------------- */

    private List<StudyPlace> filterWithinRadius(List<StudyPlace> candidates, double lat, double lng, double radiusKm) {

        return candidates.stream()
                .peek(p -> {
                    double dist = haversineDistance(lat, lng, p.getLat(), p.getLng());
                    System.out.printf("Place=%s, lat=%.6f, lng=%.6f, dist=%.2fkm%n",
                            p.getPlaceName(), p.getLat(), p.getLng(), dist);
                })
                .filter(p -> haversineDistance(lat, lng, p.getLat(), p.getLng()) <= radiusKm)
                .collect(Collectors.toList());
    }

    private double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private List<String> getSurroundingGeohashes(String userHash) {
        GeoHash hash = GeoHash.fromGeohashString(userHash);
        List<GeoHash> adjacent = new ArrayList<>();
        adjacent.add(hash);
        adjacent.addAll(List.of(hash.getAdjacent()));
        for (GeoHash geoHash : adjacent) {
            System.out.println("geoHash = " + geoHash);
            System.out.println("geoHash.getBoundingBoxCenter() = " + geoHash.getBoundingBoxCenter());

        }
        return adjacent.stream().map(GeoHash::toBase32).collect(Collectors.toList());
    }

    private List<LocationResponseDto> convertToLocationResponseDtos(List<StudyPlace> places, double lat, double lng, int limit) {
        return places.stream()
                .limit(limit)
                .map(place -> {
                    int distance = (int) Math.round(haversineDistance(lat, lng, place.getLat(), place.getLng()) * 1000); // m 단위
                    return new LocationResponseDto(
                            place.getPlaceName(),
                            place.getAddress(),
                            place.getRoadAddress(),
                            place.getPhone(),
                            distance + "m"
                    );
                })
                .collect(Collectors.toList());
    }
}
