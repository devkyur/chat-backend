package com.dating.chat.controller;

import com.dating.chat.dto.ChatMessageRequest;
import com.dating.chat.dto.ChatMessageResponse;
import com.dating.chat.dto.ChatRoomResponse;
import com.dating.chat.service.ChatService;
import com.dating.common.dto.ApiResponse;
import com.dating.common.util.SecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "채팅", description = "실시간 채팅 API - 채팅방, 메시지 관리")
@SecurityRequirement(name = "JWT")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/rooms")
    @Operation(summary = "내 채팅방 목록", description = "내가 참여한 채팅방 목록을 조회합니다.")
    public ApiResponse<List<ChatRoomResponse>> getMyChatRooms() {
        Long userId = SecurityUtil.getCurrentUserId();
        List<ChatRoomResponse> rooms = chatService.getMyChatRooms(userId);
        return ApiResponse.success(rooms);
    }

    @PostMapping("/rooms")
    @Operation(summary = "채팅방 생성", description = "매칭된 상대와 채팅방을 생성합니다.")
    public ApiResponse<ChatRoomResponse> createChatRoom(@RequestParam Long matchId) {
        Long userId = SecurityUtil.getCurrentUserId();
        ChatRoomResponse room = chatService.createChatRoom(userId, matchId);
        return ApiResponse.success(room);
    }

    @GetMapping("/rooms/{roomId}/messages")
    @Operation(summary = "메시지 목록 조회", description = "특정 채팅방의 메시지 목록을 페이징하여 조회합니다.")
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
