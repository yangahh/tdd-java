package io.hhplus.tdd.point.domain.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.point.domain.vo.TransactionType;
import io.hhplus.tdd.point.domain.entity.PointHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointHistoryService {
    private final PointHistoryTable pointHistoryTable;

    public PointHistory recordUserPointHistory(long userId, long amount, TransactionType transactionType) {
        return pointHistoryTable.insert(userId, amount, transactionType, System.currentTimeMillis());
    }

    public List<PointHistory> getUserPointHistories(long userId) {
        return sortByUpdatedAt(pointHistoryTable.selectAllByUserId(userId));
    }

    private List<PointHistory> sortByUpdatedAt(List<PointHistory> pointHistories) {
        return pointHistories.stream()
                .sorted(Comparator.comparing(PointHistory::updateMillis).reversed())
                .toList();
    }
}