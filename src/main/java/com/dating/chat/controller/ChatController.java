package com.dating.chat.controller;

import com.dating.chat.dto.ChatMessageRequest;
import com.dating.chat.dto.ChatMessageResponse;
import com.dating.chat.dto.ChatRoomResponse;
import com.dating.chat.service.ChatService;
import com.dating.common.dto.ApiResponse;
import com.dating.common.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/rooms")
    public ApiResponse<List<ChatRoomResponse>> getMyChatRooms() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<ChatRoomResponse> rooms = chatService.getMyChatRooms(userId);
        return ApiResponse.success(rooms);
    }

    @PostMapping("/rooms")
    public ApiResponse<ChatRoomResponse> createChatRoom(@RequestParam Long matchId) {
        Long userId = SecurityUtil.getCurrentUserId();
        ChatRoomResponse room = chatService.createChatRoom(userId, matchId);
        return ApiResponse.success(room);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ApiResponse<Page<ChatMessageResponse>> getMessages(
            @PathVariable Long roomId,
            Pageable pageable) {
        Long userId = SecurityUtil.getCurrentUserId();
        Page<ChatMessageResponse> messages = chatService.getMessages(userId, roomId, pageable);
        return ApiResponse.success(messages);
    }

    @MessageMapping("/chat/{roomId}/send")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload ChatMessageRequest request,
            Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        chatService.sendMessage(userId, roomId, request);
    }
}
