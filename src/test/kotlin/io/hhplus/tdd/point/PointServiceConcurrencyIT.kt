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