package io.hhplus.tdd.point.domain.entity;

import io.hhplus.tdd.point.domain.vo.TransactionType;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
}
