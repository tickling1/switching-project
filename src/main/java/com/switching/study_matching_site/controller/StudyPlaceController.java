package com.switching.study_matching_site.controller;

import ch.hsr.geohash.GeoHash;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.util.concurrent.RateLimiter;
import com.switching.study_matching_site.domain.StudyPlace;
import com.switching.study_matching_site.dto.studyplace.LocationRequestDto;
import com.switching.study_matching_site.dto.studyplace.LocationResponseDto;
import com.switching.study_matching_site.exception.ErrorResponse;
import com.switching.study_matching_site.repository.StudyPlaceRepository;
import com.switching.study_matching_site.service.KakaoLocalService;
import com.switching.study_matching_site.service.StudyPlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @Operation(summary = "주변 스터디 카페 조회", description = "위도와 경도를 받아 주변의 스터디 카페를 조회합니다. DB에 없으면 카카오 API에서 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 (JWT 토큰 누락/만료)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/study-places")
    public List<LocationResponseDto> getNearbyStudyPlaces(@RequestBody @Validated LocationRequestDto location) throws JsonProcessingException {
        return studyPlaceService.getNearbyOrFetch(location.getLng(), location.getLat());
    }

    @Operation(summary = "전국 데이터 강제 수집", description = "전국 주요 지역의 스터디 카페 데이터를 병렬로 수집합니다.")
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
    @Operation(
            summary = "주변 스터디 카페 조회 (성능 테스트 - 테이블 풀 스캔)",
            description = "[테스트용] DB의 모든 데이터를 전수 조사하여 반환합니다. 데이터가 많을 경우(예: 100만 건) 매우 느려질 수 있습니다."
    )
    @PostMapping("/study-places-example1")
    public List<LocationResponseDto> getNearbyStudyPlaces1(@RequestBody @Validated LocationRequestDto location) throws JsonProcessingException {
        return studyPlaceService.searchNearbyRooms(location.getLat(), location.getLng());
    }

    // 위 경도
    @Operation(
            summary = "주변 스터디 카페 조회 (성능 테스트 - 위경도 범위 검색)",
            description = "[테스트용] 위경도 값에 반올림을 적용하여 특정 범위 내의 데이터를 조회합니다. 인덱스 활용 여부에 따라 풀 스캔보다 빠를 수 있습니다."
    )
    @PostMapping("/study-places-example2")
    public List<LocationResponseDto> getNearbyStudyPlaces2(@RequestBody @Validated LocationRequestDto location) throws JsonProcessingException {
        return studyPlaceService.getNearbyWithRounding(location.getLat(), location.getLng());
    }


    @Operation(
            summary = "대규모 더미 데이터 생성 (100만 건)",
            description = "전국 범위(위도 33~43, 경도 124~132) 내에 랜덤한 위치의 스터디 카페 데이터를 100만 개 생성합니다. " +
                    "Batch Size 1000 단위로 저장하며, 완료까지 상당한 시간이 소요됩니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "데이터 생성 성공"),
            @ApiResponse(responseCode = "500", description = "서버 타임아웃 또는 DB 용량 초과")
    })
    @PostMapping("/study-places/dummy")
    @Transactional
    public String generateDummyData() {
        int nationwideCount = 1000000; // 전국 데이터 수

        Faker faker = new Faker(new Locale("ko")); // 한국어 Faker
        Random random = new Random();

        final int batchSize = 1000;
        List<StudyPlace> batch = new ArrayList<>(batchSize);
        LocalDateTime now = LocalDateTime.now();


        // 전국 위도·경도 범위 (대한민국 전체)
        double nationwideLatStart = 33.0;
        double nationwideLatEnd = 43.0;
        double nationwideLngStart = 124.0;
        double nationwideLngEnd = 132.0;


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

        return nationwideCount + "개의 대한민국 전체 더미 데이터를 생성했습니다.";
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
