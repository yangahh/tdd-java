package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PointHistoryService {
    private final PointHistoryTable pointHistoryTable;

    public List<PointHistory> getUserPointHistories(long userId) {
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(userId);
        return sortPointHistoriesByUpdatedAt(pointHistories);
    }

    private List<PointHistory> sortPointHistoriesByUpdatedAt(List<PointHistory> pointHistories) {
        return pointHistories.stream()
                .sorted(Comparator.comparing(PointHistory::updateMillis).reversed())
                .toList();
    }
}
