package com.dating.common.exception;

import com.dating.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.error("!!! Business exception occurred - Code: {}, Message: {}", errorCode.getCode(), e.getMessage());
        return ResponseEntity
                .status(getHttpStatus(errorCode))
                .body(ApiResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.error("!!! Validation exception - Message: {}", message);
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.error(ErrorCode.INVALID_INPUT.getCode(), message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        log.error("!!! Unexpected exception occurred - Type: {}, Message: {}", e.getClass().getSimpleName(), e.getMessage(), e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getMessage()
                ));
    }

    private HttpStatus getHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case UNAUTHORIZED, INVALID_TOKEN, EXPIRED_TOKEN, REFRESH_TOKEN_NOT_FOUND, INVALID_REFRESH_TOKEN ->
                    HttpStatus.UNAUTHORIZED;
            case FORBIDDEN, CHAT_ACCESS_DENIED -> HttpStatus.FORBIDDEN;
            case USER_NOT_FOUND, PROFILE_NOT_FOUND, MATCH_NOT_FOUND, CHAT_ROOM_NOT_FOUND, MESSAGE_NOT_FOUND ->
                    HttpStatus.NOT_FOUND;
            case USER_ALREADY_EXISTS, ALREADY_MATCHED -> HttpStatus.CONFLICT;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
