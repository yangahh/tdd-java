package io.hhplus.tdd.point.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChargePointRequestDto (
        @NotNull(message = "충전하실 포인트 금액을 입력해주세요.")
        @Positive(message = "올바른 금액을 입력해주세요.")
        long amount
) {
}
