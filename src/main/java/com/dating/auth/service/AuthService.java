package com.dating.auth.service;

import com.dating.auth.dto.LoginRequest;
import com.dating.auth.dto.SignupRequest;
import com.dating.auth.dto.TokenResponse;
import com.dating.common.exception.BusinessException;
import com.dating.common.exception.ErrorCode;
import com.dating.common.security.JwtTokenProvider;
import com.dating.user.domain.User;
import com.dating.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh:";
    private static final long REFRESH_TOKEN_EXPIRE_DAYS = 14;

    @Transactional
    public TokenResponse signup(SignupRequest request) {
        log.info(">>> Signup service called - email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn(">>> Signup failed - email already exists: {}", request.getEmail());
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());
        log.debug(">>> Password encoded - length: {}", encodedPassword.length());

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        User savedUser = userRepository.save(user);
        log.info(">>> User saved to DB - id: {}, email: {}", savedUser.getId(), savedUser.getEmail());

        return generateTokens(savedUser.getId());
    }

    public TokenResponse login(LoginRequest request) {
        log.info(">>> Login service called - email: {}", request.getEmail());

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> {
                    log.warn(">>> Login failed - user not found: {}", request.getEmail());
                    return new BusinessException(ErrorCode.USER_NOT_FOUND);
                });

        log.info(">>> User found in DB - id: {}, email: {}", user.getId(), user.getEmail());
        log.debug(">>> Stored password starts with: {}", user.getPassword().substring(0, Math.min(10, user.getPassword().length())));
        log.debug(">>> Input password length: {}", request.getPassword().length());

        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        log.info(">>> Password match result: {}", passwordMatches);

        if (!passwordMatches) {
            log.warn(">>> Login failed - invalid password for email: {}", request.getEmail());
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        log.info(">>> Login validation passed - generating tokens for user: {}", user.getId());
        return generateTokens(user.getId());
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(refreshToken);
        String storedToken = (String) redisTemplate.opsForValue()
                .get(REFRESH_TOKEN_PREFIX + userId);

        if (storedToken == null || !storedToken.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        return generateTokens(userId);
    }

    @Transactional
    public void logout(Long userId) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }

    private TokenResponse generateTokens(Long userId) {
        String accessToken = jwtTokenProvider.createAccessToken(userId);
        String refreshToken = jwtTokenProvider.createRefreshToken(userId);

        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + userId,
                refreshToken,
                REFRESH_TOKEN_EXPIRE_DAYS,
                TimeUnit.DAYS
        );

        return new TokenResponse(accessToken, refreshToken);
    }
}
