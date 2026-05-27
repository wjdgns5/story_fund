package com.storyfund.api.controller;

import com.storyfund.api.dto.BoardCreateRequestDto;
import com.storyfund.api.dto.BoardListResponseDto;
import com.storyfund.api.dto.BoardResponseDto;
import com.storyfund.api.service.BoardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private BoardService boardService;
    
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    } // end of 생성자

    // 1. 게시글 목록 조회 (비회원 가능)
    // @RequestParam : URL 쿼리 ? & 파라미터에서 값 꺼내기
    @GetMapping
    public ResponseEntity<Page<BoardListResponseDto>> getBoards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required = false) String keyword) {

        return ResponseEntity.ok(boardService.getBoards(page, keyword));
    }

    // 2. 게시글 상세 조회 (비회원 가능)
    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDto> getBoard(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.getBoard(id));
    }

    // 3. 게시글 작성 (로그인 필요)
    // @AuthenticationPrincipal : Spring Security에서 사용자의 인증 정보(신원, 권한 등)를 담고 있는 객체
    @PostMapping
    public ResponseEntity<BoardResponseDto> createBoard(
            @Valid @RequestBody BoardCreateRequestDto dto,
            @AuthenticationPrincipal String email) {

        return ResponseEntity.ok(boardService.createBoard(dto, email));
    }

    // 4. 게시글 수정 (작성자만)
    // @AuthenticationPrincipal : Spring Security에서 사용자의 인증 정보(신원, 권한 등)를 담고 있는 객체
    @PutMapping("/{id}")
    public ResponseEntity<BoardResponseDto> updateBoard(
            @PathVariable Long id,
            @Valid @RequestBody BoardCreateRequestDto dto,
            @AuthenticationPrincipal String email) {

        return ResponseEntity.ok(boardService.updateBoard(id, dto, email));
    }

    // 5. 게시글 삭제 (작성자 or 관리자)
    // @AuthenticationPrincipal : Spring Security에서 사용자의 인증 정보(신원, 권한 등)를 담고 있는 객체
    // SecurityContextHolder : Spring Security에서 현재 인증된 사용자의 정보(인증 객체)를 보관하고 제공하는 핵심 저장소
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(
            @PathVariable Long id,
            @AuthenticationPrincipal String email) {

        // 현재 유저의 role 꺼내기
        String role = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities()
                .iterator().next()
                .getAuthority();

        boardService.deleteBoard(id, email, role);
        return ResponseEntity.ok("삭제 완료");
    }
}