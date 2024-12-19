package io.hhplus.tdd.point.domain.service;

import io.hhplus.tdd.point.domain.vo.TransactionType;
import io.hhplus.tdd.point.domain.entity.PointHistory;
import io.hhplus.tdd.point.domain.entity.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/*
 * 포인트 및 포인트 내역 관련 비즈니스 로직을 처리하는 서비스 클래스
 * - Facade 패턴을 적용하여 PointService와 PointHistoryService를 조합하여 사용
 * - PointService에서 PointHistoryTable에 대한 의존성을 제거하기 위해 생성
 * - PointController에서는 해당 클래스만 주입받아서 포인트 관련 기능을 제공받는다.
 */
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
