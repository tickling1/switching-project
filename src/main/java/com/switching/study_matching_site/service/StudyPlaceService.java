package com.switching.study_matching_site.service;

import ch.hsr.geohash.GeoHash;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.switching.study_matching_site.domain.EmptyRegion;
import com.switching.study_matching_site.domain.StudyPlace;

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
    private final EmptyRegionRepository emptyRegionRepository;
    private static final int GEOHASH_PRECISION = 6;

    // 테이블 풀스캔
    @Transactional
    public List<StudyPlace> searchNearbyRooms(double lat, double lng) throws JsonProcessingException {
        final int maxCandidates = 20;
        final int minCandidates = 5;
        LocalDateTime threshold = LocalDateTime.now().minusWeeks(1);

        // 1) DB에서 최근 1주일 내 데이터만 가져오기 (위치 필터링은 제외)
        List<StudyPlace> candidates = studyPlaceRepository.findRecentPlaces(threshold);
        System.out.println("candidates = " + candidates);

        // 2) 자바에서 하버사인 계산
        List<StudyPlace> nearbyPlaces = filterWithinRadius(candidates, lat, lng, 1.0);

        // 3) 후보 부족 시 API 호출
        if (nearbyPlaces.size() < minCandidates) {
            System.out.println("nearbyPlaces = " + nearbyPlaces);
            // API 호출
            fetchAndUpdatePlaces(lat, lng);
            List<StudyPlace> recentPlaces = studyPlaceRepository.findRecentPlaces(threshold);
            List<StudyPlace> studyPlaces = filterWithinRadius(recentPlaces, lat, lng, 1.0);
            nearbyPlaces.addAll(studyPlaces);

        }
        System.out.println("nearbyPlaces = " + nearbyPlaces);
        nearbyPlaces.sort(Comparator.comparingDouble(
                p -> haversineDistance(lat, lng, p.getLat(), p.getLng())
        ));

        // 4) 최대 후보 제한
        return nearbyPlaces.size() > maxCandidates
                ? nearbyPlaces.subList(0, maxCandidates)
                : nearbyPlaces;
    }

    // 위 경도 반올림
    @Transactional
    public List<StudyPlace> getNearbyWithRounding(double lat, double lng) throws JsonProcessingException {
        final int maxCandidates = 20;
        final int minCandidates = 5;

        // 1) 위도/경도 반올림 (예: 소수점 둘째 자리)
        double latRange = 1.0 / 111.0;
        double lngRange = 1.0 / (111.0 * Math.cos(Math.toRadians(lat)));

        LocalDateTime cutoff = LocalDateTime.now().minusWeeks(1);
        // 1) DB에서 최근 1주일 내 데이터만 가져오기 (위치 필터링은 제외)

        // 2) DB에서 캐시된 후보 조회 (1주일 내 데이터만)
        List<StudyPlace> candidates = studyPlaceRepository.findRecentNearby(
                lat, lng, latRange, lngRange, cutoff
        );

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
        return nearbyPlaces.size() > maxCandidates
                ? nearbyPlaces.subList(0, maxCandidates)
                : nearbyPlaces;
    }

    // 지오 해시
    @Transactional
    public List<StudyPlace> getNearbyOrFetch(double lng, double lat) throws JsonProcessingException {
        final int maxCandidates = 20;
        final int minCandidates = 5;
        final int minPerCell = 1;

        LocalDateTime now = LocalDateTime.now();
        int weeksToSkip = 1; // 1주일

        String userHash = GeoHash.withCharacterPrecision(lat, lng, GEOHASH_PRECISION).toBase32();
        List<String> surroundingHashes = getSurroundingGeohashes(userHash);

        // 1) 후보 격자 필터링 (쿨다운 고려)
        List<String> candidateHashes = surroundingHashes.stream()
                .filter(hash -> shouldIncludeHash(hash, now, weeksToSkip))
                .collect(Collectors.toList());


        if (candidateHashes.isEmpty()) {
            return Collections.emptyList();
        }

        // 2) DB 조회 + 거리 필터링
        List<StudyPlace> candidates = studyPlaceRepository.findByGeohashIn(candidateHashes);
        List<StudyPlace> nearbyPlaces = filterWithinRadius(candidates, lat, lng, 1.0);

        // 3) 격자 단위 검사
        boolean needApiCall = false;
        for (String hash : candidateHashes) {
            needApiCall |= checkCellAndUpdateEmptyRegion(hash, minPerCell, now);
        }

        // 4) 후보 부족 또는 API 호출 필요 시
        if (nearbyPlaces.size() < minCandidates || needApiCall) {
            fetchAndUpdatePlaces(lat, lng);

            // API 호출 후 DB 재조회
            candidates = studyPlaceRepository.findByGeohashIn(candidateHashes);
            nearbyPlaces = filterWithinRadius(candidates, lat, lng, 1.0);

            // API 호출 후 빈 영역 갱신
            updateEmptyRegions(candidateHashes, now);
        }

        // 5) 거리순 정렬 + 최대 후보 제한
        nearbyPlaces.sort(Comparator.comparingDouble(
                p -> haversineDistance(lat, lng, p.getLat(), p.getLng())
        ));
        return nearbyPlaces.size() > maxCandidates
                ? nearbyPlaces.subList(0, maxCandidates)
                : nearbyPlaces;
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
    // 후보 격자 포함 여부 판단 (쿨다운 고려)
    private boolean shouldIncludeHash(String hash, LocalDateTime now, int weeksToSkip) {
        return emptyRegionRepository.findByGeohash(hash)
                .map(er -> er.getIsEmpty() && (er.getLastCheckedAt() == null ||
                        er.getLastCheckedAt().isBefore(now.minusWeeks(weeksToSkip))))
                .orElse(true); // 기록 없으면 포함
    }
    // 격자 단위 검사 + EMPTY_REGION 갱신/삭제

    private boolean checkCellAndUpdateEmptyRegion(String hash, int minPerCell, LocalDateTime now) {
        long count = studyPlaceRepository.countByGeohash(hash);

        if (count == 0) {
            // 데이터 없음 → 빈 영역 저장
            saveOrUpdateEmptyRegion(hash, true, now);
            return true; // API 호출 필요
        }

        if (count < minPerCell) {
            // 데이터는 있으나 최소 기준 미달 → API 호출 필요
            return true;
        }

        // 데이터 충분 → 기존 빈 영역 기록 삭제
        emptyRegionRepository.findByGeohash(hash).ifPresent(emptyRegionRepository::delete);
        return false; // API 호출 불필요
    }

    private void saveOrUpdateEmptyRegion(String hash, boolean isEmpty, LocalDateTime now) {
        EmptyRegion region = emptyRegionRepository.findByGeohash(hash)
                .orElseGet(() -> {
                    EmptyRegion er = new EmptyRegion();
                    er.setGeohash(hash);
                    return er;
                });
        region.setIsEmpty(isEmpty);
        region.setLastCheckedAt(now);
        emptyRegionRepository.save(region);
    }

    private void updateEmptyRegions(List<String> hashes, LocalDateTime now) {
        for (String hash : hashes) {
            long count = studyPlaceRepository.countByGeohash(hash);
            if (count == 0) {
                saveOrUpdateEmptyRegion(hash, true, now);
            } else {
                // 데이터 충분 → 빈 영역 삭제
                emptyRegionRepository.findByGeohash(hash).ifPresent(emptyRegionRepository::delete);
            }
        }
    }

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
}
