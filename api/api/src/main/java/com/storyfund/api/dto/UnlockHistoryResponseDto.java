package com.storyfund.api.dto;

import com.storyfund.api.entity.UnlockHistory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UnlockHistoryResponseDto {

    private Long   boardId;
    private String boardTitle;
    private LocalDateTime unlockedAt;

    public UnlockHistoryResponseDto(UnlockHistory history) {
        this.boardId    = history.getBoard().getId();
        this.boardTitle = history.getBoard().getTitle();
        this.unlockedAt = history.getUnlockedAt();
    }

}
