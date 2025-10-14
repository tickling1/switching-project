package com.switching.study_matching_site.controller;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.RateLimiter;
import com.switching.study_matching_site.domain.StudyPlace;
import com.switching.study_matching_site.dto.studyplace.LocationDto;
import com.switching.study_matching_site.repository.StudyPlaceRepository;
import com.switching.study_matching_site.service.KakaoLocalService;
import com.switching.study_matching_site.service.StudyPlaceService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class StudyPlaceController {

    private final StudyPlaceService studyPlaceService;
    private final KakaoLocalService kakaoLocalService;
    private final StudyPlaceRepository studyPlaceRepository;
    private final EntityManager em;

    private static final int GEOHASH_PRECISION = 6;
    private static final int BATCH_SIZE = 50;
    private static final int SLEEP_MS = 300; // QPS 제한

    @PostMapping("/study-places")
    public List<StudyPlace> getNearbyStudyPlaces(@RequestBody LocationDto location) throws JsonProcessingException {
        return studyPlaceService.getNearbyOrFetch(location.getLng(), location.getLat());
    }

    @PostMapping("/study-places-fetch")
    public ResponseEntity<String> fetchByRegionParallel() throws InterruptedException {
        LocalDateTime now = LocalDateTime.now();

        // 서울 집중 탐색 범위 (latStart, latEnd, lngStart, lngEnd)
        List<double[]> regions = List.of(
                // ========================
                // 서울
                // ========================
                new double[]{37.500, 37.600, 126.900, 127.000},
                new double[]{37.600, 37.700, 126.900, 127.000},
                new double[]{37.500, 37.600, 127.000, 127.100},
                new double[]{37.600, 37.700, 127.000, 127.100},

                // ========================
                // 경기
                // ========================
                new double[]{37.700, 37.800, 126.800, 126.900},
                new double[]{37.800, 37.900, 126.800, 126.900},
                new double[]{37.700, 37.800, 126.900, 127.000},
                new double[]{37.800, 37.900, 126.900, 127.000},

                // ========================
                // 강원
                // ========================
                new double[]{37.900, 38.000, 127.500, 127.600},
                new double[]{38.000, 38.100, 127.500, 127.600},
                new double[]{37.900, 38.000, 127.600, 127.700},
                new double[]{38.000, 38.100, 127.600, 127.700},

                // ========================
                // 충청
                // ========================
                new double[]{36.900, 37.000, 127.300, 127.400},
                new double[]{37.000, 37.100, 127.300, 127.400},
                new double[]{36.900, 37.000, 127.400, 127.500},
                new double[]{37.000, 37.100, 127.400, 127.500},

                // ========================
                // 전라
                // ========================
                new double[]{35.000, 35.100, 126.700, 126.800},
                new double[]{35.100, 35.200, 126.700, 126.800},
                new double[]{35.000, 35.100, 126.800, 126.900},
                new double[]{35.100, 35.200, 126.800, 126.900},

                // ========================
                // 경상
                // ========================
                new double[]{35.500, 35.600, 129.300, 129.400},
                new double[]{35.600, 35.700, 129.300, 129.400},
                new double[]{35.500, 35.600, 129.400, 129.500},
                new double[]{35.600, 35.700, 129.400, 129.500},

                // ========================
                // 제주
                // ========================
                new double[]{33.300, 33.400, 126.200, 126.300},
                new double[]{33.400, 33.500, 126.200, 126.300},
                new double[]{33.300, 33.400, 126.300, 126.400},
                new double[]{33.400, 33.500, 126.300, 126.400}
        );

        ExecutorService executor = Executors.newFixedThreadPool(8); // 병렬 쓰레드 개수 조정
        RateLimiter rateLimiter = RateLimiter.create(10.0); // 초당 호출 제한 (QPS)

        for (double[] r : regions) {
            double latStart = r[0];
            double latEnd = r[1];
            double lngStart = r[2];
            double lngEnd = r[3];

            double stepKm = 2.0; // 10km 단위
            double stepLat = stepKm / 111.0;

            for (double lat = latStart; lat <= latEnd; lat += stepLat) {
                double stepLng = stepKm / (111.0 * Math.cos(Math.toRadians(lat)));

                for (double lng = lngStart; lng <= lngEnd; lng += stepLng) {
                    double finalLat = lat;
                    double finalLng = lng;

                    executor.submit(() -> {
                        rateLimiter.acquire(); // QPS 제한 적용
                        try {
                            fetchAndSaveBatchTransactional(finalLat, finalLng, now);
                        } catch (JsonProcessingException | InterruptedException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        }

        executor.shutdown();
        executor.awaitTermination(6, TimeUnit.HOURS); // 충분한 대기 시간
        return ResponseEntity.ok("Parallel nationwide study places fetch started");
    }


    @Transactional
    public void fetchAndSaveBatchTransactional(double lat, double lng, LocalDateTime now) throws JsonProcessingException, InterruptedException {
        List<StudyPlace> batch = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (int page = 1; page <= 3; page++) {
            List<JsonNode> nodes = kakaoLocalService.searchStudyRooms("스터디카페", lng, lat);
            if (nodes.isEmpty()) break;

            for (JsonNode node : nodes) {
                String placeName = node.get("place_name").asText();
                String address = node.get("address_name").asText();
                String uniqueKey = placeName + "|" + address;

                if (!seen.add(uniqueKey)) continue; // 배치 내부 중복 제거

                List<StudyPlace> places = studyPlaceRepository.findDistinctByPlaceNameAndAddress(placeName, address);
                StudyPlace place = places.isEmpty() ? new StudyPlace() : places.get(0);

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

                batch.add(place);

                if (batch.size() >= BATCH_SIZE) {
                    saveBatch(batch);
                    batch.clear();
                }
            }

            Thread.sleep(SLEEP_MS); // 페이지 호출 간 휴식
        }

        if (!batch.isEmpty()) saveBatch(batch);
    }


    @Transactional
    public void saveBatch(List<StudyPlace> batch) {
        studyPlaceRepository.saveAll(batch);
        studyPlaceRepository.flush();
        em.clear();
    }

    // 테이블 풀 스캔
    @PostMapping("/study-places-example1")
    public List<StudyPlace> getNearbyStudyPlaces1(@RequestBody LocationDto location) throws JsonProcessingException {
        return studyPlaceService.searchNearbyRooms(location.getLat(), location.getLng());
    }

    // 위 경도
    @PostMapping("/study-places-example2")
    public List<StudyPlace> getNearbyStudyPlaces2(@RequestBody LocationDto location) throws JsonProcessingException {
        return studyPlaceService.getNearbyWithRounding(location.getLat(), location.getLng());
    }



    @PostMapping("/geohash")
    public ResponseEntity<Map<String, Object>> testGeohash(@RequestBody Map<String, List<String>> request) throws JsonProcessingException {
        List<String> geohashStrings = request.get("geohashes");
        if (geohashStrings == null || geohashStrings.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "geohashes 필드가 필요합니다."));
        }

        Map<String, Object> result = new HashMap<>();
        Map<String, List<String>> gridPlaces = new LinkedHashMap<>();
        Map<String, Integer> gridCounts = new LinkedHashMap<>();

        for (String gh : geohashStrings) {
            GeoHash hash = GeoHash.fromGeohashString(gh);
            WGS84Point point = hash.getBoundingBoxCenter();
            double lat = point.getLatitude();
            double lng = point.getLongitude();

            List<JsonNode> nodes = kakaoLocalService.searchStudyRooms("스터디룸", lng, lat);

            // 이름 중복 제거
            List<String> placeNames = nodes.stream()
                    .map(n -> n.get("place_name").asText())
                    .distinct()
                    .toList();

            gridPlaces.put(gh, placeNames);
            gridCounts.put(gh, placeNames.size());
        }

        result.put("counts", gridCounts);
        result.put("places", gridPlaces);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/dummy")
    @Transactional
    public String generateDummyData() {
        int totalCount = 57000;
        int seoulCount = 12000; // 서울 데이터 수
        int nationwideCount = totalCount - seoulCount; // 전국 데이터 수

        Faker faker = new Faker(new Locale("ko")); // 한국어 Faker
        Random random = new Random();

        final int batchSize = 1000;
        List<StudyPlace> batch = new ArrayList<>(batchSize);
        LocalDateTime now = LocalDateTime.now();

        // 서울 위도·경도 범위 (대략)
        double seoulLatStart = 37.413;
        double seoulLatEnd = 37.713;
        double seoulLngStart = 126.734;
        double seoulLngEnd = 127.034;

        // 전국 위도·경도 범위 (대한민국 전체)
        double nationwideLatStart = 33.0;
        double nationwideLatEnd = 43.0;
        double nationwideLngStart = 124.0;
        double nationwideLngEnd = 132.0;

        // 1) 서울 데이터 생성
        for (int i = 0; i < seoulCount; i++) {
            double lat = seoulLatStart + random.nextDouble() * (seoulLatEnd - seoulLatStart);
            double lng = seoulLngStart + random.nextDouble() * (seoulLngEnd - seoulLngStart);

            StudyPlace place = createStudyPlace(faker, lat, lng, now);
            batch.add(place);

            if (batch.size() >= batchSize) {
                studyPlaceRepository.saveAll(batch);
                batch.clear();
            }
        }

        // 2) 전국 데이터 생성
        for (int i = 0; i < nationwideCount; i++) {
            double lat = nationwideLatStart + random.nextDouble() * (nationwideLatEnd - nationwideLatStart);
            double lng = nationwideLngStart + random.nextDouble() * (nationwideLngEnd - nationwideLngStart);

            StudyPlace place = createStudyPlace(faker, lat, lng, now);
            batch.add(place);

            if (batch.size() >= batchSize) {
                studyPlaceRepository.saveAll(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            studyPlaceRepository.saveAll(batch);
        }

        return totalCount + "개의 대한민국 전체 더미 데이터를 생성했습니다. (서울 " + seoulCount + "개 포함)";
    }

    private StudyPlace createStudyPlace(Faker faker, double lat, double lng, LocalDateTime now) {
        StudyPlace place = new StudyPlace();
        place.setPlaceName(faker.company().name() + " 스터디룸");
        place.setAddress(faker.address().fullAddress());
        place.setRoadAddress(faker.address().streetAddress());
        place.setPhone(faker.phoneNumber().phoneNumber());
        place.setLat(lat);
        place.setLng(lng);
        place.setPlaceUrl(faker.internet().url());
        place.setSource("dummy");
        place.setIsActive(true);
        place.setLastCheckedAt(now);
        place.setGeohash(GeoHash.withCharacterPrecision(lat, lng, 6).toBase32());
        return place;
    }
}
