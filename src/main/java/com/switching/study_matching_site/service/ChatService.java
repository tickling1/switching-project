package com.switching.study_matching_site.service;

import com.switching.study_matching_site.domain.Chat;
import com.switching.study_matching_site.domain.Member;
import com.switching.study_matching_site.domain.Room;
import com.switching.study_matching_site.dto.chat.ChatCreate;
import com.switching.study_matching_site.dto.chat.ChatRead;
import com.switching.study_matching_site.exception.EntityNotFoundException;
import com.switching.study_matching_site.exception.ErrorCode;
import com.switching.study_matching_site.repository.ChatRepository;
import com.switching.study_matching_site.repository.MemberRepository;
import com.switching.study_matching_site.repository.RoomRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChatService {

    // 순환 참조
    private final ChatRepository chatRepository;
    private final RoomRepository roomRepository;
    private final MemberRepository memberRepository;
    
    // 챗 생성 후 채팅 발송
    public ChatRead createChat(ChatCreate chatCreateDto, Long memberId, Long roomId) {
        Chat savedChat = chatCreateDto.toEntity();
        Optional<Room> findRoom = roomRepository.findById(roomId);
        Optional<Member> findMember = memberRepository.findById(memberId);

        if (findRoom.isPresent() && findMember.isPresent()) {
            savedChat.setRoom(findRoom.get());
            savedChat.setWriter(findMember.get().getUsername());
            Chat entity = chatRepository.save(savedChat);
            return ChatRead.fromEntity(entity);
        } else {
            throw new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND);
        }
    }

    @Transactional(readOnly = true)
    public List<ChatRead> readChat(Long roomId) {
        Optional<Room> findRoom = roomRepository.findById(roomId);

        if (findRoom.isPresent()) {
            Optional<List<Chat>> findChatList = chatRepository.findByRoom(roomId);
            if (findChatList.isPresent()) {
                List<Chat> chatList = findChatList.get();
                List<ChatRead> chatReads = new ArrayList<>();
                for (Chat chat : chatList) {
                    ChatRead chatRead = ChatRead.fromEntity(chat);
                    chatReads.add(chatRead);
                }
                return chatReads;
            } else {
                throw new EntityNotFoundException(ErrorCode.CHAT_NOT_FOUND);
            }
        } else {
            throw new EntityNotFoundException(ErrorCode.ROOM_NOT_FOUND);
        }
    }
}
