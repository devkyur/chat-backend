package com.dating.notification.service;

import com.dating.common.exception.BusinessException;
import com.dating.common.exception.ErrorCode;
import com.dating.notification.domain.FcmToken;
import com.dating.notification.dto.NotificationRequest;
import com.dating.notification.repository.FcmTokenRepository;
import com.dating.user.domain.User;
import com.dating.user.repository.UserRepository;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    private final FcmTokenRepository fcmTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public void registerToken(Long userId, String token) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        FcmToken fcmToken = fcmTokenRepository.findByToken(token)
                .orElseGet(() -> FcmToken.builder()
                        .user(user)
                        .token(token)
                        .build());

        fcmTokenRepository.save(fcmToken);
        log.info("FCM token registered for user: {}", userId);
    }

    @Transactional
    public void deleteToken(String token) {
        fcmTokenRepository.deleteByToken(token);
        log.info("FCM token deleted: {}", token);
    }

    public void sendNotificationToUser(Long userId, NotificationRequest request) {
        List<FcmToken> tokens = fcmTokenRepository.findByUserId(userId);

        if (tokens.isEmpty()) {
            log.warn("No FCM tokens found for user: {}", userId);
            return;
        }

        tokens.forEach(fcmToken -> {
            try {
                sendNotification(fcmToken.getToken(), request);
            } catch (Exception e) {
                log.error("Failed to send notification to token: {}", fcmToken.getToken(), e);
            }
        });
    }

    private void sendNotification(String token, NotificationRequest request) {
        try {
            Notification notification = Notification.builder()
                    .setTitle(request.getTitle())
                    .setBody(request.getBody())
                    .build();

            Message.Builder messageBuilder = Message.builder()
                    .setToken(token)
                    .setNotification(notification);

            if (request.getData() != null) {
                messageBuilder.putAllData(request.getData());
            }

            String response = FirebaseMessaging.getInstance().send(messageBuilder.build());
            log.info("Successfully sent notification: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM notification", e);
            throw new BusinessException(ErrorCode.NOTIFICATION_SEND_FAILED, e.getMessage());
        }
    }
}
