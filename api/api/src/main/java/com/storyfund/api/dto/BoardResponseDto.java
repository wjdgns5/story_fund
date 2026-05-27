package com.storyfund.api.dto;

import com.storyfund.api.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardResponseDto {
    // 게시글 응답

    private Long id;
    private String title;
    private String content;
    private String authorNickname;   // 작성자 닉네임
    private boolean isPaid;
    private int viewCount;
    private LocalDateTime createdAt;

    // Board Entity → BoardResponseDto 변환 생성자
    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.authorNickname = board.getUser().getNickname();
        this.isPaid         = board.isPaid();
        this.viewCount      = board.getViewCount();
        this.createdAt      = board.getCreatedAt();
    }


}
