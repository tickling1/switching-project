package com.switching.study_matching_site.service;

import com.switching.study_matching_site.SecurityUtil;
import com.switching.study_matching_site.domain.Chat;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Participation;
import com.switching.study_matching_site.domain.Room;
import com.switching.study_matching_site.dto.chat.ChatCreateDto;
import com.switching.study_matching_site.dto.chat.ChatReadDto;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.repository.ChatRepository;
import com.switching.study_matching_site.repository.ParticipationRepository;
import com.switching.study_matching_site.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ParticipationRepository participationRepository;
    private final SecurityUtil securityUtil;

    /**
     * 챗 생성
     * 방에 참여하고 있지 않다면 예외를 터트려야 함.
     */
    public ChatReadDto createChat(ChatCreateDto chatCreateDto) {
        Member member = securityUtil.getMemberByUserDetails();
        Participation participation = participationRepository.findActiveParticipation(member.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PARTICIPATED_NOT_FOUND));

        Room room = participation.getRoom();
        if (room == null) {
            throw new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND);
        }

        Chat chat = chatCreateDto.toEntity();
        // 연관관계 메서드
        room.addChat(chat, member.getUsername());

        // V2에 보완할 것
        chatRepository.save(chat);
        return ChatReadDto.fromEntity(chat);
    }

    @Transactional(readOnly = true)
    public List<ChatReadDto> readChat() {

        Member member = securityUtil.getMemberByUserDetails();
        Participation participation = participationRepository.findActiveParticipation(member.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.PARTICIPATED_NOT_FOUND));

        Room room = participation.getRoom();
        if (room == null) {
            throw new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND);
        }

        List<Chat> chatHistory = chatRepository.findChatHistory(room.getId())
                .orElseThrow(() -> new EntityNotFoundException(ErrorCode.CHAT_NOT_FOUND));

        List<ChatReadDto> chatReads = new ArrayList<>();
        for (Chat chat : chatHistory) {
            ChatReadDto chatRead = ChatReadDto.fromEntity(chat);
            chatReads.add(chatRead);
            }
        return chatReads;

    }
}
