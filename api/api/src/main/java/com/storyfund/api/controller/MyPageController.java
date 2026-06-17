package com.storyfund.api.controller;

import com.storyfund.api.dto.BoardListResponseDto;
import com.storyfund.api.dto.UnlockHistoryResponseDto;
import com.storyfund.api.dto.UserInfoResponseDto;
import com.storyfund.api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "MyPage", description = "마이페이지 API")
public class MyPageController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> getMyInfo(
            @AuthenticationPrincipal String email) {
        return ResponseEntity.ok(userService.getMyInfo(email));
    }

    @Operation(summary = "내 게시글 목록")
    @GetMapping("/me/boards")
    public ResponseEntity<Page<BoardListResponseDto>> getMyBoards(
            @AuthenticationPrincipal String email,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(userService.getMyBoards(email, page));
    }

    @Operation(summary = "열람 내역")
    @GetMapping("/me/unlocked")
    public ResponseEntity<Page<UnlockHistoryResponseDto>> getMyUnlocked(
            @AuthenticationPrincipal String email,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(userService.getMyUnlocked(email, page));
    }
}