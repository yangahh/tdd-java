package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public UserPoint chargePoint(long userId, long amount) {
        validateChargeAmount(amount);

        UserPoint afterPoint = getUserPoint(userId).addPoint(amount);

        updateUserPoint(userId, afterPoint.point());
        recordPointHistory(userId, amount, TransactionType.CHARGE);

        return afterPoint;
    }

    private static void validateChargeAmount(long amount) {  // 충전 과정에서만 유효하기 때문에 PointService의 책임
        if (amount % 100 != 0) {
            throw new IllegalArgumentException("100 포인트 단위로만 충전 가능합니다.");
        }
    }

    public UserPoint usePoint(long userId, long amount) {
        UserPoint afterPoint = getUserPoint(userId).minusPoint(amount);

        updateUserPoint(userId, afterPoint.point());
        recordPointHistory(userId, amount, TransactionType.USE);

        return afterPoint;
    }

    public UserPoint getPoint(long userId) {
        return getUserPoint(userId);
    }

    private UserPoint getUserPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    private void updateUserPoint(long userId, long amount ) {
        userPointTable.insertOrUpdate(userId, amount);
    }

    private void recordPointHistory(long userId, long amount, TransactionType transactionType) {
        pointHistoryTable.insert(userId, amount, transactionType, System.currentTimeMillis());
    }
}
