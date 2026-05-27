package com.storyfund.api.dto;

import com.storyfund.api.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BoardListResponseDto {
    // 목록 응답 (내용 제외)

    private Long id;
    private String title;
    private String authorNickname;
    private boolean isPaid;
    private int viewCount;
    private LocalDateTime createdAt;

    public BoardListResponseDto(Board board) {
        this.id             = board.getId();
        this.title          = board.getTitle();
        this.authorNickname = board.getUser().getNickname();
        this.isPaid         = board.isPaid();
        this.viewCount      = board.getViewCount();
        this.createdAt      = board.getCreatedAt();
    }

    /** 목록에서는 content 를 안 줘요.
        내용까지 전부 주면 데이터가 너무 많아서 느려지거든요.
        상세 조회할 때만 content 를 줘요.
     **/
}
