package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.entity.UserPoint;
import io.hhplus.tdd.point.domain.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ConcurrencyTest {
    @Autowired
    private PointService pointService;

    @Autowired
    private UserPointTable userPointTable;

    @DisplayName("동일한 사용자가 동시에 포인트 충전 요청을 여러번 하는 경우 한 번에 하나의 요청만 처리되어야 한다.")
    @Test
    void chargePointConcurrentlyWithSameUser() throws InterruptedException {
        // given
        long userId = 1111L;
        int requestCount = 10; // 동시에 들어오는 요청 수

        // when
        // ExecutorService로 스레드풀을 생성하여 동시 요청 처리를 재현
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);  // 다른 스레드에서 수행중인 작업이 완료될 때까지 대기
        for (int i = 0; i < requestCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargePoint(userId, 100L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();  // 모든 스레드가 종료될 때까지 대기

        // then
        long result = userPointTable.selectById(userId).point();
        assertThat(result).isEqualTo(100L * requestCount);

        executorService.shutdown();
    }

    @DisplayName("동일한 사용자가 동시에 포인트 사용 요청을 여러번 하는 경우 한 번에 하나의 요청만 처리되어야 한다.")
    @Test
    void usePointConcurrentlyWithSameUser() throws InterruptedException {
        // given
        long userId = 2222L;
        int requestCount = 10; // 동시에 들어오는 요청 수
        long initialPoint = 10_000L;
        userPointTable.insertOrUpdate(userId, initialPoint);

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        CountDownLatch countDownLatch = new CountDownLatch(requestCount);  // 다른 스레드에서 수행중인 작업이 완료될 때까지 대기
        for (int i = 0; i < requestCount; i++) {
            executorService.submit(() -> {
                try {
                    pointService.usePoint(userId, 10L);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        countDownLatch.await();  // 모든 스레드가 종료될 때까지 대기

        // then
        long result = userPointTable.selectById(userId).point();
        assertThat(result).isEqualTo(initialPoint - 10L * requestCount);

        executorService.shutdown();
    }

    @DisplayName("동일한 사용자가 동시에 포인트 충전, 사용, 조회를 하는 경우 요청은 한 번에 하나씩 처리되어야 한다.")
    @Test
    void concurrentlyChargeUseGetPointRequestwithSameUser() throws InterruptedException, ExecutionException {
        // given
        long userId = 3333L;
        int requestCount = 100; // 동시에 들어오는 요청 수
        long initialPoint = 1000L;
        userPointTable.insertOrUpdate(userId, initialPoint);

        Callable<Void> chargePointTask = () -> {
            pointService.chargePoint(userId, 200L);
            return null;
        };
        Callable<Void> usePointTask = () -> {
            pointService.usePoint(userId, 100L);
            return null;
        };
        Callable<Void> getPointTask = () -> {
            pointService.getUserPoint(userId);
            return null;
        };

        // when
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        // 3개의 작업을 동시에 실행
        Future<Void> chargePointFuture = executorService.submit(chargePointTask);
        Future<Void> usePointFuture = executorService.submit(usePointTask);
        Future<Void> getPointFuture = executorService.submit(getPointTask);
        // 작업이 완료될 때까지 대기
        chargePointFuture.get();
        usePointFuture.get();
        getPointFuture.get();

        // then
        UserPoint finalUserPoint = pointService.getUserPoint(userId);

        assertThat(finalUserPoint.point()).isEqualTo(initialPoint + 200L - 100L);
        executorService.shutdown();
    }

    @DisplayName("서로 다른 사용자가 동시에 포인트 충전을 요청하는 경우, 요청은 병렬적으로 처리되어야 한다.")
    @Test
    void chargePointConcurrentlyWithDifferentUsers() throws InterruptedException, ExecutionException {
        // given
        int requestCount = 10;  // 동시에 들어오는 요청 수
        Set<Long> threadIds = new HashSet<>();  // 병렬 처리된 요청들이 서로 다른 스레드에서 실행되는지 확인하기 위한 집합
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 0; i < requestCount; i++) {
            long userId = (long) i; // 서로 다른 사용자 ID (1부터 시작)
            tasks.add(() -> {
                pointService.chargePoint(userId, 100L);
                threadIds.add(Thread.currentThread().getId()); // 현재 스레드 ID를 집합에 추가
                return null;
            });
        }

        // when
        List<Future<Void>> futures = executorService.invokeAll(tasks); // 모든 작업을 동시에 실행
        for (Future<Void> future : futures) {
            future.get();  // 모든 작업이 완료될 때까지 대기
        }

        // then
        assertThat(threadIds.size()).isEqualTo(requestCount);  // 서로 다른 스레드에서 실행되었는지를 검증

        executorService.shutdown();
    }

    @DisplayName("서로 다른 사용자가 동시에 포인트 사용을 요청하는 경우, 요청은 병렬적으로 처리되어야 한다.")
    @Test
    void usePointConcurrentlyWithDifferentUsers() throws InterruptedException, ExecutionException {
        // given
        int requestCount = 10;  // 동시에 들어오는 요청 수
        Set<Long> threadIds = new HashSet<>();  // 병렬 처리된 요청들이 서로 다른 스레드에서 실행되는지 확인하기 위한 집합
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);
        for (int i = 1; i <= requestCount; i++) {
            long userId = (long) i * 10; // 서로 다른 사용자 ID
            userPointTable.insertOrUpdate(userId, 1000L);
        }

        List<Callable<Void>> tasks = new ArrayList<>();
        for (int i = 1; i <= requestCount; i++) {
            long userId = (long) i * 10; // 서로 다른 사용자 ID
            tasks.add(() -> {
                pointService.usePoint(userId, 10L);
                threadIds.add(Thread.currentThread().getId()); // 현재 스레드 ID를 집합에 추가
                return null;
            });
        }

        // when
        List<Future<Void>> futures = executorService.invokeAll(tasks); // 모든 작업을 동시에 실행
        for (Future<Void> future : futures) {
            future.get();  // 모든 작업이 완료될 때까지 대기
        }

        // then
        assertThat(threadIds.size()).isEqualTo(requestCount);  // 서로 다른 스레드에서 실행되었는지를 검증

        executorService.shutdown();
    }
}
