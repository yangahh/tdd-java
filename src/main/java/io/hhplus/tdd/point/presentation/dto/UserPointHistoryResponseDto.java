package io.hhplus.tdd.point.presentation.dto;

import io.hhplus.tdd.point.domain.entity.PointHistory;
import io.hhplus.tdd.point.domain.vo.TransactionType;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public record UserPointHistoryResponseDto(
        long userId,
        long amount,
        TransactionType type,
        LocalDateTime updatedAt
) {
    public static UserPointHistoryResponseDto of(PointHistory pointHistory) {
        return new UserPointHistoryResponseDto(
                pointHistory.userId(),
                pointHistory.amount(),
                pointHistory.type(),
                convertToDateTime(pointHistory.updateMillis())
        );
    }

    private static LocalDateTime convertToDateTime(long epochTimeMillis) {
        Instant instant = Instant.ofEpochMilli(epochTimeMillis);
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
