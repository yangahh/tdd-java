package io.hhplus.tdd.point.domain.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.entity.UserPoint;
import io.hhplus.tdd.utils.ReentrantLockManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
 * 포인트 관련 비즈니스 로직을 처리하는 서비스 클래스
 * - ReentrantLock을 사용하여 동시성 문제 해결
 * - ConcurrentHashMap을 사용하여 사용자별 Lock 객체 관리
 */
@Service
@RequiredArgsConstructor
public class PointService {
    private final UserPointTable userPointTable;
    private final ReentrantLockManager lockManager;

    public UserPoint chargePoint(long userId, long amount) {
        lockManager.aquireLock(userId);
        try {
            validateChargeAmount(amount);

            UserPoint afterPoint = getUserPoint(userId).addPoint(amount);
            return updateUserPoint(userId, afterPoint.point());
        } finally {
            lockManager.releaseLock(userId);
        }
    }

    private static void validateChargeAmount(long amount) {  // 충전 과정에서만 유효하기 때문에 PointService의 책임
        if (amount % 100 != 0) {
            throw new IllegalArgumentException("100 포인트 단위로만 충전 가능합니다.");
        }
    }

    public UserPoint usePoint(long userId, long amount) {
        lockManager.aquireLock(userId);
        try {
            UserPoint afterPoint = getUserPoint(userId).minusPoint(amount);
            return updateUserPoint(userId, afterPoint.point());
        } finally {
            lockManager.releaseLock(userId);
        }
    }

    public UserPoint getUserPoint(long userId) {
        return userPointTable.selectById(userId);
    }

    private UserPoint updateUserPoint(long userId, long amount ) {
        return userPointTable.insertOrUpdate(userId, amount);
    }
}
