package com.storyfund.api.config;

import com.storyfund.api.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice // @ControllerAdvice + @ResponseBody
public class GlobalExceptionHandler {

    // 1. @Valid 유효성 검사 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidException(MethodArgumentNotValidException e) {

        // 어떤 필드에서 어떤 에러가 났는지 모아서 보여줘요
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream() // → 목록을 하나씩 처리
                .map(FieldError::getDefaultMessage) // → 각 에러의 메시지만 꺼냄
                .collect(Collectors.joining(", ")); // ", " 로 이어붙임
        // ex : 결과: "이메일 형식이 올바르지 않습니다., 비밀번호는 8자 이상이어야 합니다."

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(400, message));
    }

    // 2. 잘못된 요청 (중복 이메일, 권한 없음, 존재하지 않는 데이터 등) 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDto> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponseDto(400, e.getMessage()));
    }

    // 3. 권한 없음 403
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorResponseDto> handleSecurityException(SecurityException e) {
        return ResponseEntity.
                status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponseDto(403, e.getMessage()));
    }

    // 4. 그 외 모든 예외 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(500, "서버 오류가 발생했습니다."));
    }


}
