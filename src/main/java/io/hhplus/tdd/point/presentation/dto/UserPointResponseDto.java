package io.hhplus.tdd.point.presentation.dto;

import io.hhplus.tdd.point.domain.entity.UserPoint;

public record UserPointResponseDto (
        long userId,
        long point
) {
    public static UserPointResponseDto of(UserPoint userPoint) {
        return new UserPointResponseDto(userPoint.id(), userPoint.point());
    }
}
