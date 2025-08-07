package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.Chat;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Participation;
import com.switching.study_matching_site.domain.Room;
import com.switching.study_matching_site.domain.type.*;
import com.switching.study_matching_site.dto.chat.ChatCreateDto;
import com.switching.study_matching_site.dto.chat.ChatReadDto;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.repository.ChatRepository;
import com.switching.study_matching_site.repository.ParticipationRepository;
import com.switching.study_matching_site.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private SecurityUtil securityUtil;

    @Mock
    private ParticipationRepository participationRepository;

    @Mock
    private ChatRepository chatRepository;

    @InjectMocks
    private ChatService chatService;

    private static Room createRoomWithJava() {
        Room room = new Room(
                "자바 프로젝트 인원 모집",
                RoomStatus.ON,
                0,
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

    private static Member createMember() {
        Member member = new Member();
        member.setId(100L);
        member.setLoginId("ksw7777");
        member.setUsername("코딩은둔고수");
        member.setEmail("xuni1234@gmail.com");
        member.setPhoneNumber("010-2222-3333");
        member.setEnterStatus(EnterStatus.OUT);
        return member;
    }

    private static Member createOtherMember() {
        Member member = new Member();
        member.setId(500L);
        member.setLoginId("kqk1234");
        member.setUsername("코딩초보");
        member.setEmail("dasdw@gmail.com");
        member.setPhoneNumber("010-1111-1111");
        member.setEnterStatus(EnterStatus.ENTER);
        return member;
    }

    @Test
    void 채팅_발송_성공() {
        // given
        Member member1 = createMember();
        Member member2 = createOtherMember();

        Room room = createRoomWithJava();
        Participation participation = new Participation(room, RoleType.ADMIN, member1);
        new Participation(room, RoleType.USER, member2);

        ChatCreateDto chatCreateDto = new ChatCreateDto("안녕하세요!");

        when(securityUtil.getMemberByUserDetails()).thenReturn(member1);
        when(participationRepository.findActiveParticipation(member1.getId())).thenReturn(Optional.of(participation));
        when(chatRepository.save(any())).thenReturn(chatCreateDto.toEntity());

        // when
        ChatReadDto chatReadDto = chatService.createChat(chatCreateDto);

        // then
        ArgumentCaptor<Chat> chatArgumentCaptor = ArgumentCaptor.forClass(Chat.class);
        verify(chatRepository).save(chatArgumentCaptor.capture());
        Chat chat = chatArgumentCaptor.getValue();
        room.addChat(chat, member1.getUsername());

        assertEquals(chat.getWriter(), member1.getUsername());
        assertEquals(chat.getChatContent(), chatCreateDto.getChatContent());
    }

    @Test
    void 방_참여X_챗_실패() {
        // given
        Member member1 = createMember();
        Member member2 = createOtherMember();

        Room room = createRoomWithJava();
        Participation participation = new Participation(room, RoleType.ADMIN, member1);
        new Participation(room, RoleType.USER, member2);

        ChatCreateDto chatCreateDto = new ChatCreateDto("안녕하세요!");

        when(securityUtil.getMemberByUserDetails()).thenReturn(member1);
        when(participationRepository.findActiveParticipation(member1.getId())).thenReturn(Optional.empty());

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> chatService.createChat(chatCreateDto));
        assertEquals(ErrorCode.PARTICIPATED_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void 참여중인_방이_null이면_채팅_실패() {
        // given
        Member member = createMember();
        Participation participation = mock(Participation.class);
        when(participation.getRoom()).thenReturn(null);

        when(securityUtil.getMemberByUserDetails()).thenReturn(member);
        when(participationRepository.findActiveParticipation(member.getId())).thenReturn(Optional.of(participation));

        ChatCreateDto chatCreateDto = new ChatCreateDto("안녕하세요");

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> chatService.createChat(chatCreateDto));
        assertEquals(ErrorCode.ROOM_NOT_FOUND, ex.getErrorCode());
    }



    @Test
    void 챗_모두_읽기() {
        // given
        Member member1 = createMember();
        Member member2 = createOtherMember();

        Room room = createRoomWithJava();
        Participation participation = new Participation(room, RoleType.ADMIN, member1);
        new Participation(room, RoleType.USER, member2);

        Chat chat1 = createChat1();
        Chat chat2 = createChat2();

        room.addChat(chat1, member1.getUsername());
        room.addChat(chat2, member2.getUsername());

        when(securityUtil.getMemberByUserDetails()).thenReturn(member1);
        when(participationRepository.findActiveParticipation(member1.getId())).thenReturn(Optional.of(participation));
        when(chatRepository.findChatHistory(room.getId())).thenReturn(Optional.of(room.getChat_history()));

        // when
        List<ChatReadDto> chatHistory = chatService.readChat();

        // then
        assertEquals(2, chatHistory.size());
        assertEquals(chat1.getWriter(), member1.getUsername());
        assertEquals(chat2.getWriter(), member2.getUsername());

        ChatReadDto dto1 = chatHistory.get(0);
        ChatReadDto dto2 = chatHistory.get(1);

        System.out.println("dto1 = " + dto1);
        System.out.println("dto2 = " + dto2);
    }

    @Test
    void 챗_없음_읽기_실패() {
        // given
        Member member1 = createMember();
        Member member2 = createOtherMember();

        Room room = createRoomWithJava();
        Participation participation = new Participation(room, RoleType.ADMIN, member1);
        new Participation(room, RoleType.USER, member2);


        when(securityUtil.getMemberByUserDetails()).thenReturn(member1);
        when(participationRepository.findActiveParticipation(member1.getId())).thenReturn(Optional.of(participation));
        when(chatRepository.findChatHistory(room.getId())).thenReturn(Optional.empty());

        // when
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () -> chatService.readChat());
        assertEquals(ErrorCode.CHAT_NOT_FOUND, ex.getErrorCode());

    }

    @Test
    void 참여중인_방이_null이면_채팅_조회_실패() {
        // given
        Member member = createMember();
        Participation participation = mock(Participation.class);
        when(participation.getRoom()).thenReturn(null);

        when(securityUtil.getMemberByUserDetails()).thenReturn(member);
        when(participationRepository.findActiveParticipation(member.getId())).thenReturn(Optional.of(participation));

        // when & then
        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> chatService.readChat());
        assertEquals(ErrorCode.ROOM_NOT_FOUND, ex.getErrorCode());
    }


    private static Chat createChat1() {
        Chat chat = new Chat();
        chat.setChatDateTime(LocalDateTime.now());
        chat.setChatContent("안녕하세요");
        return chat;
    }

    private static Chat createChat2() {
        Chat chat = new Chat();
        chat.setChatDateTime(LocalDateTime.now());
        chat.setChatContent("반갑습니다.");
        return chat;
    }

}