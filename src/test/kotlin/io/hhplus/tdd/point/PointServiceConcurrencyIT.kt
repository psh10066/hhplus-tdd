package io.hhplus.tdd.point

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import java.util.concurrent.Executors
import java.util.concurrent.Future

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class PointServiceConcurrencyIT {

    @Autowired
    private lateinit var pointService: PointService

    @Autowired
    private lateinit var userPointRepository: UserPointRepository

    // 포인트 충전 동시성 테스트
    @Test
    fun `동일한 사용자의 포인트 충전 요청이 동시에 들어온 경우 정합성을 보장할 수 있다`() {
        // when
        concurrencyTestHelper(10, Runnable {
            pointService.charge(1L, 100L)
        })

        // then
        val result = pointService.getPoint(1L)
        assertThat(result.point).isEqualTo(1000L)
    }

    // 포인트 사용 동시성 테스트
    @Test
    fun `동일한 사용자의 포인트 사용 요청이 동시에 들어온 경우 정합성을 보장할 수 있다`() {
        // given
        pointService.charge(1L, 1000L)

        // when
        concurrencyTestHelper(10, Runnable {
            pointService.use(1L, 100L)
        })

        // then
        val result = pointService.getPoint(1L)
        assertThat(result.point).isEqualTo(0L)
    }

    // 포인트 충전, 사용 혼합 동시성 테스트
    @Test
    fun `동일한 사용자의 포인트 충전, 사용 요청이 동시에 들어온 경우 정합성을 보장할 수 있다`() {
        // given
        pointService.charge(1L, 1000L)

        // when
        concurrencyTestHelper(3,
            Runnable { pointService.use(1L, 100L) },
            Runnable { pointService.charge(1L, 100L) },
            Runnable { pointService.charge(1L, 100L) },
            Runnable { pointService.use(1L, 100L) },
        )

        // then
        val result = pointService.getPoint(1L)
        assertThat(result.point).isEqualTo(1000L)
    }

    @Test
    fun `충전이나 사용 요청이 어떤 순서로 들어올 지 모를 때 잔고의 유효성을 보장할 수 있다`() {
        val executorService = Executors.newFixedThreadPool(2)

        // 두 순서 모두 대응하기 위해 여러 번 반복하여 테스트
        for (i in 1..1000) {
            // given
            userPointRepository.save(UserPoint(1L, 1000L, 123L))

            // when
            val chargeFuture = executorService.submit { pointService.charge(1, 1000) }
            val useFuture = executorService.submit { pointService.use(1, 2000) }

            chargeFuture.get()
            try {
                useFuture.get()

                // then : a요청이 성공했을때 -> (+1000원이 먼저 성공하고) -2000원 이 성공했기때문에 최종잔액 0원
                assertThat(userPointRepository.getById(1L).point).isEqualTo(0L)
            } catch (e: Exception) {
                // then: a요청이 실패했을때 -> -2000원이 실패 했기때문에 (+1000원이 이후에 성공) 최종잔액 2000원
                assertThat(userPointRepository.getById(1L).point).isEqualTo(2000L)
            }
        }
    }

    private fun concurrencyTestHelper(times: Int, vararg tasks: Runnable) {
        val executorService = Executors.newFixedThreadPool(times * tasks.size)
        try {
            val futures = mutableListOf<Future<*>>()
            repeat(times) {
                for (task in tasks) {
                    val future = executorService.submit(task)
                    futures.add(future)
                }
            }
            futures.forEach { it.get() }
        } finally {
            executorService.shutdown()
        }
    }
}