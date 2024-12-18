package io.hhplus.tdd.database;

import io.hhplus.tdd.point.PointHistory;
import io.hhplus.tdd.point.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

public class PointHistoryTableTest {
    private final PointHistoryTable pointHistoryTable = new PointHistoryTable();

    @DisplayName("정상적으로 포인트 내역을 추가한다.")
    @Test
    void shouldReturnPointHistoryWhenInsert() {
        // given
        long userId = 1L;
        long amount = 100L;
        TransactionType type = TransactionType.CHARGE;
        long updateAt = System.currentTimeMillis();

        // when
        PointHistory pointHistory = pointHistoryTable.insert(userId, amount, type, updateAt);
        // then
        assertThat(pointHistory)
                .extracting("userId", "amount", "type")
                .containsExactly(userId, amount, type);
    }

    @DisplayName("포인트 내역이 존재하지 않는 경우 빈 리스트를 반환한다.")
    @Test
    void shouldReturnEmptyListWhenNoPointHistory() {
        // when
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(1L);

        // then
        assertThat(pointHistories).isEmpty();
    }

    @DisplayName("특정 유저의 포인트 내역이 존재하는 경우 그 유저의 모든 포인트 내역을 반환한다.")
    @Test
    void shouldReturnPointHistories() {
        // given
        pointHistoryTable.insert(1L, 100L, TransactionType.CHARGE, System.currentTimeMillis());
        pointHistoryTable.insert(1L, 200L, TransactionType.USE, System.currentTimeMillis());
        pointHistoryTable.insert(2L, 300L, TransactionType.CHARGE, System.currentTimeMillis());

        // when
        List<PointHistory> pointHistories = pointHistoryTable.selectAllByUserId(1L);

        // then
        assertThat(pointHistories).hasSize(2);
        assertThat(pointHistories).extracting("amount", "type")
                .containsExactly(
                        tuple(100L, TransactionType.CHARGE),
                        tuple(200L, TransactionType.USE)
                );
    }

}
