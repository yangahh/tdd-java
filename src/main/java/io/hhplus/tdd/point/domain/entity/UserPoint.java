package io.hhplus.tdd.point.domain.entity;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    private static final long MAX_POINT = 1_000_000;

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }

    public UserPoint addPoint(long amount) {
        long afterPoint = point + amount;
        if (afterPoint > MAX_POINT) {
            throw new IllegalArgumentException("최대 포인트 보유량을 초과했습니다.");
        }
        return new UserPoint(id, afterPoint, System.currentTimeMillis());
    }

    public UserPoint minusPoint(long amount) {
        long afterPoint = point - amount;
        if (afterPoint < 0) {
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        return new UserPoint(id, afterPoint, System.currentTimeMillis());
    }
}
