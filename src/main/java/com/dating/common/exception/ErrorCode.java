package com.dating.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Common
    INVALID_INPUT("C001", "Invalid input"),
    INTERNAL_SERVER_ERROR("C002", "Internal server error"),
    UNAUTHORIZED("C003", "Unauthorized"),
    FORBIDDEN("C004", "Forbidden"),

    // User
    USER_NOT_FOUND("U001", "User not found"),
    USER_ALREADY_EXISTS("U002", "User already exists"),
    INVALID_PASSWORD("U003", "Invalid password"),

    // Auth
    INVALID_TOKEN("A001", "Invalid token"),
    EXPIRED_TOKEN("A002", "Expired token"),
    REFRESH_TOKEN_NOT_FOUND("A003", "Refresh token not found"),
    INVALID_REFRESH_TOKEN("A004", "Invalid refresh token"),

    // Profile
    PROFILE_NOT_FOUND("P001", "Profile not found"),
    INVALID_PROFILE_IMAGE("P002", "Invalid profile image"),

    // Match
    MATCH_NOT_FOUND("M001", "Match not found"),
    ALREADY_MATCHED("M002", "Already matched"),
    SELF_MATCH_NOT_ALLOWED("M003", "Self match not allowed"),

    // Chat
    CHAT_ROOM_NOT_FOUND("CH001", "Chat room not found"),
    CHAT_ACCESS_DENIED("CH002", "Chat access denied"),
    MESSAGE_NOT_FOUND("CH003", "Message not found"),

    // Notification
    NOTIFICATION_SEND_FAILED("N001", "Failed to send notification"),
    INVALID_FCM_TOKEN("N002", "Invalid FCM token");

    private final String code;
    private final String message;
}
