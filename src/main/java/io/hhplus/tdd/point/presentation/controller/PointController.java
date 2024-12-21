package io.hhplus.tdd.point.presentation.controller;

import io.hhplus.tdd.point.domain.entity.UserPoint;
import io.hhplus.tdd.point.domain.service.PointApplicationService;
import io.hhplus.tdd.point.presentation.dto.ChargePointRequestDto;
import io.hhplus.tdd.point.presentation.dto.UsePointRequestDto;
import io.hhplus.tdd.point.presentation.dto.UserPointHistoryResponseDto;
import io.hhplus.tdd.point.presentation.dto.UserPointResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/point")
@RequiredArgsConstructor
@Validated
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final PointApplicationService pointService;

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserPointResponseDto> point(
            @PathVariable @Positive(message = "잘못된 형식의 ID입니다.") long id
    ) {
        UserPoint userPoint = pointService.getPoint(id);
        return new ResponseEntity<>(UserPointResponseDto.of(userPoint), HttpStatus.OK);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("/{id}/histories")
    public ResponseEntity<List<UserPointHistoryResponseDto>> history(
            @PathVariable @Positive(message = "잘못된 형식의 ID입니다.") long id
    ) {
        List<UserPointHistoryResponseDto> res = pointService.getPointHistories(id).stream()
                .map(UserPointHistoryResponseDto::of)
                .toList();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("/{id}/charge")
    public ResponseEntity<UserPointResponseDto> charge(
            @PathVariable @Positive(message = "잘못된 형식의 ID입니다.") long id,
            @RequestBody @Valid ChargePointRequestDto request
    ) {
        UserPoint userPoint = pointService.chargePoint(id, request.amount());
        log.info("User [{}] successfully charged {} points.", id, request.amount());

        return new ResponseEntity<>(UserPointResponseDto.of(userPoint), HttpStatus.OK);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("/{id}/use")
    public ResponseEntity<UserPointResponseDto> use(
            @PathVariable @Positive(message = "잘못된 형식의 ID입니다.") long id,
            @RequestBody @Valid UsePointRequestDto request
    ) {
        UserPoint userPoint = pointService.usePoint(id, request.amount());
        log.info("User [{}] successfully used {} points.", id, request.amount());

        return new ResponseEntity<>(UserPointResponseDto.of(userPoint), HttpStatus.OK);
    }
}
