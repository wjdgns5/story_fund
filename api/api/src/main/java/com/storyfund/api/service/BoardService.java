package com.storyfund.api.service;

import com.storyfund.api.dto.BoardCreateRequestDto;
import com.storyfund.api.dto.BoardListResponseDto;
import com.storyfund.api.dto.BoardResponseDto;
import com.storyfund.api.entity.Board;
import com.storyfund.api.entity.User;
import com.storyfund.api.repository.BoardRepository;
import com.storyfund.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BoardService {

    private BoardRepository boardRepository;
    private UserRepository userRepository;

    public BoardService(BoardRepository boardRepository, UserRepository userRepository) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
    } // end of 생성자

    // 1. 게시글 목록 조회
    public Page<BoardListResponseDto> getBoards(int page, String keyword) {

        // 1. 최신순 정렬, 한 페이지 10개
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Board> boards;

        if(keyword != null && !keyword.isBlank()) {
            // 검색어 있으면 제목 검색
            boards = boardRepository.findByDeletedAtIsNullAndTitleContaining(keyword, pageable);
        } else {
            // 검색어 없으면 전체 조회
            boards = boardRepository.findByDeletedAtIsNull(pageable);
        }

        // Board → BoardListResponseDto 로 변환
        return boards.map(BoardListResponseDto::new);
    }

    // 2. 게시글 상세 조회
    @Transactional
    public BoardResponseDto getBoard(Long id) {

        Board board = boardRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow( ()-> new IllegalArgumentException("존재하지 않는 게시물 입니다.") );

        // 조회수 +1
        board.setViewCount(board.getViewCount() +1);

        return new BoardResponseDto(board);

        /**
         * @Transactional 이 없으면 board.setViewCount() 만 해도 DB에 저장이 안 돼요.
         * JPA 의 변경 감지 (Dirty Checking) 는 트랜잭션 안에서만 동작해요.
         *
         * @Transactional 있을 때
         *   1. 트랜잭션 시작
         *   2. board 조회
         *   3. board.setViewCount() 로 변경
         *   4. 트랜잭션 종료 시점에 변경된 것 자동 감지
         *   5. UPDATE 쿼리 자동 실행  ← save() 안 써도 됨
         */
    }

    // 3. 게시글 작성
    public BoardResponseDto createBoard(BoardCreateRequestDto dto, String email) {

        // 현재 로그인한 유저 조회
        User user = userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("존재하지 않는 유저입니다."));

        Board board = Board.builder()
                .user(user)
                .title(dto.getTitle())
                .content(dto.getContent())
                .isPaid(dto.isPaid())
                .build();

        Board saved = boardRepository.save(board);
        return new BoardResponseDto(saved);
    }

    // 4. 게시글 수정
    @Transactional
    public BoardResponseDto updateBoard(Long id, BoardCreateRequestDto dto, String email) {

        Board board = boardRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 작성자 본인인지 확인
        if (!board.getUser().getEmail().equals(email)) {
            throw new IllegalArgumentException("수정 권한이 없습니다.");
        }

        board.setTitle(dto.getTitle());
        board.setContent(dto.getContent());
        board.setPaid(dto.isPaid());

        return new BoardResponseDto(board);
    }

    // 5. 게시글 삭제 (소프트 삭제)
    @Transactional
    public void deleteBoard(Long id, String email, String role) {

        Board board = boardRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // 작성자 본인 또는 관리자만 삭제 가능
        boolean isAuthor = board.getUser().getEmail().equals(email);
        boolean isAdmin  = "ROLE_ADMIN".equals(role);

        if (!isAuthor && !isAdmin) {
            throw new IllegalArgumentException("삭제 권한이 없습니다.");
        }

        // 실제로 지우지 않고 삭제 시간만 기록
        board.setDeletedAt(LocalDateTime.now());
    }

}
