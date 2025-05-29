package com.switching.study_matching_site.service;

import com.switching.study_matching_site.domain.Room;
import com.switching.study_matching_site.domain.type.*;
import com.switching.study_matching_site.dto.condition.RoomSearchCond;
import com.switching.study_matching_site.dto.room.RoomDetailDto;
import com.switching.study_matching_site.dto.room.RoomInfoResponseDto;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.repository.RoomRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomSearchServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomSearchService roomSearchService;

    private static Room createRoomWithJava() {
        Room room = new Room(
                "자바 프로젝트 인원 모집",
                RoomStatus.ON,
                1,
                6,
                LocalTime.now(),
                LocalTime.now().plusHours(3),
                Goal.STUDY,
                TechSkill.JAVA,
                3,
                Region.SEOUL,
                OfflineStatus.OFFLINE
        );
        return room;
    }

    private static Room createRoomWithPython() {
        Room room = new Room(
                "파이썬 프로젝트 인원 모집",
                RoomStatus.ON,
                1,
                3,
                LocalTime.now(),
                LocalTime.now().plusHours(2),
                Goal.STUDY,
                TechSkill.PYTHON,
                2,
                Region.INCHEON,
                OfflineStatus.OFFLINE
        );
        return room;
    }

    @Test
    void 방_상세_조회_성공() {
        /**
         * 어느 방 번호를 입력하던지 언제든 성공
         */
        // given
        Room room = createRoomWithJava();
        when(roomRepository.findById(any())).thenReturn(Optional.of(room));

        // when
        RoomDetailDto roomDetailDto = roomSearchService.findRoomById(1L);

        // then
        Assertions.assertEquals(roomDetailDto.getRoomTitle(), room.getRoomTitle());
        Assertions.assertEquals(roomDetailDto.getMaxCount(), room.getMaxCount());
        Assertions.assertEquals(roomDetailDto.getProjectGoal(), room.getProjectGoal());
    }

    @Test
    void 방_상세_조회_실패() {
        /**
         * 어느 방 번호를 입력하던지 언제든 실패
         */
        // given
        when(roomRepository.findById(any())).thenReturn(Optional.empty());

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> roomSearchService.findRoomById(1L));
        assertEquals(ex.getErrorCode(), ErrorCode.ROOM_NOT_FOUND);

    }

    @Test
    void 방_조건_검색_성공() {
        // given
        Room javaRoom = createRoomWithJava();
        Room pythonRoom = createRoomWithPython();

        RoomSearchCond cond = new RoomSearchCond(
                "자바",                  // 제목에 자바 포함
                TechSkill.JAVA,         // 기술스택 자바
                Goal.STUDY,             // 목표 STUDY
                Region.SEOUL,           // 지역 서울
                OfflineStatus.OFFLINE   // 오프라인
        );

        // 결과에 자바방 하나만 들어가도록 구성
        List<RoomInfoResponseDto> expectedRoomList = List.of(
                new RoomInfoResponseDto(
                        javaRoom.getRoomTitle(),
                        javaRoom.getCurrentCount(),
                        javaRoom.getMaxCount(),
                        javaRoom.getUuid())
        );

        Page<RoomInfoResponseDto> resultPage = new PageImpl<>(expectedRoomList);

        // when
        when(roomRepository.searchRoom(cond, PageRequest.of(0, 10))).thenReturn(resultPage);

        // then
        Page<RoomInfoResponseDto> result = roomSearchService.roomSearchCondList(cond);
        assertEquals(1, result.getContent().size());
        assertEquals("자바 프로젝트 인원 모집", result.getContent().get(0).getRoomTitle());
    }


    @Test
    void 방_조건_검색_결과없음() {

        // given
        RoomSearchCond cond = new RoomSearchCond(
                "JAVASCRIPT 언어",
                TechSkill.JAVASCRIPT,
                Goal.STUDY,
                Region.BUSAN,
                OfflineStatus.ONLINE
        );

        // 빈 Page 리턴
        Page<RoomInfoResponseDto> emptyPage = Page.empty();
        when(roomRepository.searchRoom(cond, PageRequest.of(0, 10))).thenReturn(emptyPage);

        // when
        Page<RoomInfoResponseDto> result = roomSearchService.roomSearchCondList(cond);

        // then
        assertTrue(result.isEmpty());  // 결과가 비어 있는지 검증
    }
}