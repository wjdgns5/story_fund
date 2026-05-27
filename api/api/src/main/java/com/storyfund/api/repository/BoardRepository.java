package com.storyfund.api.repository;

import com.storyfund.api.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 삭제 안 된 게시글 전체 조회 (페이지네이션)
    Page<Board> findByDeletedAtIsNull(Pageable pageable);

    // 삭제 안 된 게시글 + 제목 검색 (페이지네이션)
    Page<Board> findByDeletedAtIsNullAndTitleContaining(String keyword, Pageable pageable);

    // 삭제 안 된 게시글 단건 조회
    Optional<Board> findByIdAndDeletedAtIsNull(Long id);
}
