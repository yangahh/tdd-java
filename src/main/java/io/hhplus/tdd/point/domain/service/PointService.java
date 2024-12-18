package io.hhplus.tdd.point.domain.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.entity.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointTable userPointTable;

    public UserPoint chargePoint(long userId, long amount) {
        validateChargeAmount(amount);

        UserPoint afterPoint = getUserPoint(userId).addPoint(amount);
        return updateUserPoint(userId, afterPoint.point());
    }

    private static void validateChargeAmount(long amount) {  // 충전 과정에서만 유효하기 때문에 PointService의 책임
        if (amount % 100 != 0) {
            throw new IllegalArgumentException("100 포인트 단위로만 충전 가능합니다.");
        }
    }

    public UserPoint usePoint(long userId, long amount) {
        UserPoint afterPoint = getUserPoint(userId).minusPoint(amount);
        return updateUserPoint(userId, afterPoint.point());
    }

    public UserPoint getUserPoint(long userId) {
        return getPointByUser(userId);
    }

    private UserPoint getPointByUser(long userId) {
        return userPointTable.selectById(userId);
    }

    private UserPoint updateUserPoint(long userId, long amount ) {
        return userPointTable.insertOrUpdate(userId, amount);
    }
}
