package com.dating.chat.dto;

import com.dating.chat.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponse {
    private Long id;
    private Long chatRoomId;
    private Long senderProfileId;
    private String content;
    private ChatMessage.MessageType type;
    private Boolean isRead;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage message) {
        return new ChatMessageResponse(
                message.getId(),
                message.getChatRoom().getId(),
                message.getSenderProfile().getId(),
                message.getContent(),
                message.getType(),
                message.getIsRead(),
                message.getCreatedAt()
        );
    }
}
