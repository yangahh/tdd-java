package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PointHistoryServiceTest {
    @InjectMocks
    private PointHistoryService sut;

    @Mock
    private PointHistoryTable pointHistoryTable;

    @DisplayName("포인트 내역 조회 검증: 내역이 없는 경우 비어있는 리스트를 반환한다.")
    @Test
    void shouldReturnEmptyListForNoPointHistories() {
        // given
        given(pointHistoryTable.selectAllByUserId(1L)).willReturn(List.of());

        // when
        List<PointHistory> result = sut.getUserPointHistories(1L);

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("포인트 내역 조회 검증: 내역이 있는 경우 시간을 기준으로 내림차순으로 정렬하여 반환한다.")
    @Test
    void shouldGetPointHistoriesSuccessfully() {
        // given
        long updateMillis = System.currentTimeMillis();
        PointHistory pointHistory1 = new PointHistory(1L, 1L, 1000L, TransactionType.CHARGE, updateMillis);
        PointHistory pointHistory2 = new PointHistory(2L, 1L, 200L, TransactionType.USE, updateMillis + 1000L);
        PointHistory pointHistory3 = new PointHistory(3L, 1L, 500L, TransactionType.USE, updateMillis + 2000L);
        given(pointHistoryTable.selectAllByUserId(1L)).willReturn(List.of(pointHistory1, pointHistory2, pointHistory3));

        // when
        List<PointHistory> result = sut.getUserPointHistories(1L);

        // then
        assertThat(result).hasSize(3);
        assertThat(result.get(0)).isEqualTo(pointHistory3);
        assertThat(result.get(1)).isEqualTo(pointHistory2);
        assertThat(result.get(2)).isEqualTo(pointHistory1);
    }
}