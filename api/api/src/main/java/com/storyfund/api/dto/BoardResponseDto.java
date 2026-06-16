package com.storyfund.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    private String authorEmail;

    @JsonProperty("isPaid")
    private boolean isPaid;

    private boolean locked; // true 면 잠긴 상태
    private int viewCount;
    private LocalDateTime createdAt;

    // Board Entity → BoardResponseDto 변환 생성자
    // 기본 생성자 (전체 내용)
    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.authorNickname = board.getUser().getNickname();
        this.authorEmail    = board.getUser().getEmail();
        this.isPaid         = board.isPaid();
        this.locked         = false;
        this.viewCount      = board.getViewCount();
        this.createdAt      = board.getCreatedAt();
    }

    // 미리보기 생성자 (잠긴 상태)
    public BoardResponseDto(Board board, boolean locked) {
        this.id             = board.getId();
        this.title          = board.getTitle();
        this.authorNickname = board.getUser().getNickname();
        this.authorEmail    = board.getUser().getEmail();
        this.isPaid         = board.isPaid();
        this.locked         = locked;
        this.viewCount      = board.getViewCount();
        this.createdAt      = board.getCreatedAt();

        // 잠긴 상태면 100자 미리보기만
        if (locked) {
            String raw = board.getContent();
            this.content = raw.length() > 100
                    ? raw.substring(0, 100) + "..."
                    : raw;
        } else {
            this.content = board.getContent();
        }
    }


}
