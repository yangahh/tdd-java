package io.hhplus.tdd.point.domain.service;

import io.hhplus.tdd.point.domain.vo.TransactionType;
import io.hhplus.tdd.point.domain.entity.PointHistory;
import io.hhplus.tdd.point.domain.entity.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointApplicationService {
    private final PointService pointService;
    private final PointHistoryService pointHistoryService;

    public UserPoint chargePoint(long userId, long amount) {
        UserPoint updatedPoint = pointService.chargePoint(userId, amount);
        pointHistoryService.recordUserPointHistory(userId, amount, TransactionType.CHARGE);

        return updatedPoint;
    }

    public UserPoint usePoint(long userId, long amount) {
        UserPoint updatedPoint = pointService.usePoint(userId, amount);
        pointHistoryService.recordUserPointHistory(userId, amount, TransactionType.USE);

        return updatedPoint;
    }

    public UserPoint getPoint(long userId) {
        return pointService.getUserPoint(userId);
    }

    public List<PointHistory> getPointHistories(long userId) {
        return pointHistoryService.getUserPointHistories(userId);
    }
}
