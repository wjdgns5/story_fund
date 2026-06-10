package com.storyfund.api.repository;

import com.storyfund.api.entity.Board;
import com.storyfund.api.entity.UnlockHistory;
import com.storyfund.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UnlockHistoryRepository extends JpaRepository<UnlockHistory, Long> {

    // 이미 열람했는지 확인
    boolean existsByUserAndBoard(User user, Board board);
}
