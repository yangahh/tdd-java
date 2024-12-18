package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointService sut;

    @Mock
    private UserPointTable userPointTable;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @DisplayName("포인트 충전 검증: 충전 포인트 단위가 100이 아닌 경우 실패한다.")
    @Test
    void shouldFailWhenChargeAmountIsNotMultipleOf100() {
        // given
        long wrongAmount = 50L;

        // when  // then
        assertThatThrownBy(() -> sut.chargePoint(1L, wrongAmount))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("100 포인트 단위로만 충전 가능합니다.");
    }


    @DisplayName("포인트 충전 검증: 충전 후 포인트가 최대로 보유할 수 있는 포인트를 초과하는 경우 실패한다.")
    @Test
    void shouldFailWhenChargeExceedsMaxPoint() {
        // given
        UserPoint existingUserPoint = new UserPoint(1L, 999_000L,  System.currentTimeMillis());
        given(userPointTable.selectById(1L)).willReturn(existingUserPoint);

        // when // then
        assertThatThrownBy(() -> sut.chargePoint(1L, 2000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("최대 포인트 보유량을 초과했습니다.");
    }

    @DisplayName("포인트 충전 검증: 정상적인 금액을 충전하는 경우 요청에 성공하고 포인트 내역이 추가된다.")
    @Test
    void shouldChargePointsSuccessfully() {
        // given
        long userId = 1L;
        long originPoint = 1000L;
        long chargeAmount = 100L;
        long currentTime = System.currentTimeMillis();
        UserPoint originUserPoint = new UserPoint(userId, originPoint, currentTime);
        UserPoint updatedUserPoint = new UserPoint(userId, originPoint + chargeAmount, currentTime);

        given(userPointTable.selectById(anyLong())).willReturn(originUserPoint);
        given(userPointTable.insertOrUpdate(anyLong(), anyLong())).willReturn(updatedUserPoint);

        // when
        UserPoint result = sut.chargePoint(userId, chargeAmount);

        // then
        then(userPointTable).should().insertOrUpdate(eq(userId), eq(originPoint + chargeAmount));
        assertThat(result.point()).isEqualTo(1100L);

    }

    @DisplayName("포인트 사용 검증: 사용할 포인트가 보유한 포인트보다 많은 경우 실패한다.")
    @Test
    void shouldFailToUsePointsWhenNotEnough() {
        // given
        UserPoint existingUserPoint = new UserPoint(1L, 1000L,  System.currentTimeMillis());
        given(userPointTable.selectById(1L)).willReturn(existingUserPoint);

        // when  // then
        assertThatThrownBy(() -> sut.usePoint(1L, 2000L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("포인트가 부족합니다.");
    }

    @DisplayName("포인트 사용 검증: 정상적인 금액을 사용하는 경우 요청에 성공하고 포인트 내역이 추가된다.")
    @Test
    void shouldUsePointsSuccessfully() {
        // given
        long userId = 1L;
        long originPoint = 1000L;
        long useAmount = 100L;
        long currentTime = System.currentTimeMillis();
        UserPoint originUserPoint = new UserPoint(userId, originPoint, currentTime);
        UserPoint updatedUserPoint = new UserPoint(userId,  originPoint - useAmount, currentTime);

        given(userPointTable.selectById(anyLong())).willReturn(originUserPoint);
        given(userPointTable.insertOrUpdate(anyLong(), anyLong())).willReturn(updatedUserPoint);

        // when
        UserPoint result = sut.usePoint(userId, useAmount);

        // then
        then(userPointTable).should().insertOrUpdate(eq(userId), eq(originPoint - useAmount));
        assertThat(result.point()).isEqualTo(originPoint - useAmount);

    }

    @DisplayName("포인트 조회 검증: 포인트를 한번도 충전하지 않은 사용자의 경우 0으로 초기화된 포인트를 반환한다.")
    @Test
    void shouldReturnZeroPointForInitUser() {
        // given
        long userId = 1L;
        given(userPointTable.selectById(userId)).willReturn(UserPoint.empty(userId));

        // when
        UserPoint result = sut.getUserPoint(userId);

        // then
        assertThat(result.point()).isZero();
    }

    @DisplayName("포인트 조회 검증: 정상적으로 요청하는 경우 조회 성공")
    @Test
    void shouldGetUserPointSuccessfully() {
        // given
        UserPoint existingUserPoint = new UserPoint(1L, 1000L,  System.currentTimeMillis());
        given(userPointTable.selectById(1L)).willReturn(existingUserPoint);

        // when
        UserPoint result = sut.getUserPoint(1L);

        // then
        assertThat(result.point()).isEqualTo(1000L);
    }
}
