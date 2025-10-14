package com.switching.study_matching_site.service;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
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
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmptyRegionService {

    private final EmptyRegionRepository emptyRegionRepository;
    private final StudyPlaceRepository studyPlaceRepository;
    private final KakaoLocalService kakaoLocalService;

    private static final String QUERY = "스터디룸";

    @Transactional
    public void updateEmptyRegions() {
        LocalDateTime threshold = LocalDateTime.now().minusMonths(1);

        List<EmptyRegion> regions = emptyRegionRepository.findByLastCheckedAtBefore(threshold);

        for (EmptyRegion region : regions) {
            try {
                String geohash = region.getGeohash();

                GeoHash hash = GeoHash.fromGeohashString(geohash);

                // 좌표로 디코딩
                WGS84Point point = hash.getBoundingBoxCenter();
                double lat = point.getLatitude();
                double lng = point.getLongitude();

                List<JsonNode> nodes = kakaoLocalService.searchStudyRooms(QUERY, lng, lat);

                if (!nodes.isEmpty()) {
                    emptyRegionRepository.delete(region);

                    JsonNode node = nodes.get(0);
                    StudyPlace place = new StudyPlace();
                    place.setPlaceName(node.get("place_name").asText());
                    place.setAddress(node.get("address_name").asText());
                    place.setRoadAddress(node.path("road_address_name").asText(""));
                    place.setPhone(node.path("phone").asText());
                    place.setLat(node.get("y").asDouble());
                    place.setLng(node.get("x").asDouble());
                    place.setPlaceUrl(node.path("place_url").asText(""));
                    place.setIsActive(true);
                    place.setLastCheckedAt(LocalDateTime.now());
                    place.setGeohash(GeoHash.withCharacterPrecision(
                            place.getLat(), place.getLng(), 6).toBase32());

                    studyPlaceRepository.save(place);
                } else {
                    region.setLastCheckedAt(LocalDateTime.now());
                    emptyRegionRepository.save(region);
                }
            } catch (JsonProcessingException e) {
                log.error("장소 갱신 실패: id={}, name={}, error={}",
                        region.getId(),region.getGeohash(), e.getMessage());            }
        }
    }
}
