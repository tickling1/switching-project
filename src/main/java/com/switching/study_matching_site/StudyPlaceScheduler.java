package com.switching.study_matching_site;

import com.switching.study_matching_site.service.EmptyRegionService;
import com.switching.study_matching_site.service.StudyPlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class StudyPlaceScheduler {

    private final StudyPlaceService studyPlaceService;
    private final EmptyRegionService emptyRegionService;

    @Scheduled(cron = "0 0 3 * * MON") // 매주 월요일 새벽 3시
    public void mondayUpdate() {
        // 오래된 장소들에 대해 외부 API를 호출하여 최신 데이터 갱신 (여전히 확인되지 않은 장소들은 폐점 처리)
        studyPlaceService.updateOldPlaces();
    }

    @Scheduled(cron = "0 0 3 ? * WED#1") // 한 달에 한 번 (첫째 수요일 새벽 3시 실행)
    public void wednesdayUpdate() {
        // 빈 지역으로 분류되었던 장소 갱신
        emptyRegionService.updateEmptyRegions();
    }
}

