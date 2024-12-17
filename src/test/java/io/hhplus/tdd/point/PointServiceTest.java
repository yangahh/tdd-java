package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
public class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @DisplayName("포인트 충전 검증: 충전 후 포인트가 최대로 보유할 수 있는 포인트를 초과하는 경우 실패한다.")
    @Test
    void chargePointOverMaximunPoint() {
        // given

        // when

        // then

    }

    @DisplayName("포인트 충전 검증: 충전 포인트 단위가 100이 아닌 경우 실패한다.")
    @Test
    void chargePointNotMultipleOf100() {
        // given


        // when


        // then

    }

    @DisplayName("포인트 충전 검증: 정상적인 금액을 충전하는 경우 요청에 성공하고 포인트 내역이 추가된다.")
    @Test
    void chargePointSuccess() {
        // given

        // when

        // then

    }

    @DisplayName("포인트 사용 검증: 사용할 포인트가 보유한 포인트보다 많은 경우 실패한다.")
    @Test
    void usePointOverCurrentPoint() {
        // given


        // when


        // then

    }

    @DisplayName("포인트 사용 검증: 정상적인 금액을 사용하는 경우 요청에 성공하고 포인트 내역이 추가된다.")
    @Test
    void usePointSuccess() {
        // given


        // when


        // then

    }

    @DisplayName("포인트 조회 검증: 정상적으로 요청하는 경우 조회 성공")
    @Test
    void getPointSuccess() {
        // given


        // when


        // then

    }

    @DisplayName("포인트 내역 조회 검증: 내역이 없는 경우 비어있는 리스트를 반환한다.")
    @Test
    void getPointHistoryEmpty() {
        // given


        // when


        // then

    }

    @DisplayName("포인트 내역 조회 검증: 내역이 있는 경우 시간을 기준으로 내림차순으로 정렬하여 반환한다.")
    @Test
    void getPointHistorySuccess() {
        // given


        // when


        // then

    }

}
