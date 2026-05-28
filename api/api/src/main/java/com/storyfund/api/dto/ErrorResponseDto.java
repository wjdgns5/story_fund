package com.storyfund.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponseDto {
    // 에러 응답 형태
    private int status;
    private String message;
}
