package com.dating.chat.service;

import com.dating.chat.domain.ChatMessage;
import com.dating.chat.domain.ChatRoom;
import com.dating.chat.dto.ChatMessageRequest;
import com.dating.chat.dto.ChatMessageResponse;
import com.dating.chat.dto.ChatRoomResponse;
import com.dating.chat.repository.ChatMessageRepository;
import com.dating.chat.repository.ChatRoomRepository;
import com.dating.common.exception.BusinessException;
import com.dating.common.exception.ErrorCode;
import com.dating.match.domain.Match;
import com.dating.match.repository.MatchRepository;
import com.dating.profile.domain.Profile;
import com.dating.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MatchRepository matchRepository;
    private final ProfileRepository profileRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final RedisTemplate<String, Object> redisTemplate;

    public List<ChatRoomResponse> getMyChatRooms(Long userId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        List<ChatRoom> chatRooms = chatRoomRepository.findByProfileId(profile.getId());
        return chatRooms.stream()
                .map(ChatRoomResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatRoomResponse createChatRoom(Long userId, Long matchId) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MATCH_NOT_FOUND));

        if (!match.getIsMatched()) {
            throw new BusinessException(ErrorCode.CHAT_ACCESS_DENIED, "Match is not confirmed");
        }

        if (!match.getFromProfile().getId().equals(profile.getId()) &&
            !match.getToProfile().getId().equals(profile.getId())) {
            throw new BusinessException(ErrorCode.CHAT_ACCESS_DENIED);
        }

        ChatRoom chatRoom = chatRoomRepository.findByMatchId(matchId)
                .orElseGet(() -> {
                    ChatRoom newRoom = ChatRoom.builder()
                            .match(match)
                            .build();
                    return chatRoomRepository.save(newRoom);
                });

        return ChatRoomResponse.from(chatRoom);
    }

    @Transactional
    public ChatMessageResponse sendMessage(Long userId, Long chatRoomId, ChatMessageRequest request) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!chatRoom.getMatch().getFromProfile().getId().equals(profile.getId()) &&
            !chatRoom.getMatch().getToProfile().getId().equals(profile.getId())) {
            throw new BusinessException(ErrorCode.CHAT_ACCESS_DENIED);
        }

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .senderProfile(profile)
                .content(request.getContent())
                .type(request.getType())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);
        ChatMessageResponse response = ChatMessageResponse.from(savedMessage);

        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, response);

        return response;
    }

    public Page<ChatMessageResponse> getMessages(Long userId, Long chatRoomId, Pageable pageable) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PROFILE_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!chatRoom.getMatch().getFromProfile().getId().equals(profile.getId()) &&
            !chatRoom.getMatch().getToProfile().getId().equals(profile.getId())) {
            throw new BusinessException(ErrorCode.CHAT_ACCESS_DENIED);
        }

        Page<ChatMessage> messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtDesc(
                chatRoomId, pageable);

        return messages.map(ChatMessageResponse::from);
    }
}
