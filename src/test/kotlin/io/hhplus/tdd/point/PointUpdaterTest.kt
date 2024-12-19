package io.hhplus.tdd.point

import io.hhplus.tdd.stub.PointHistoryRepositoryStub
import io.hhplus.tdd.stub.UserPointRepositoryStub
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PointUpdaterTest {

    private lateinit var userPointRepository: UserPointRepository
    private lateinit var pointHistoryRepository: PointHistoryRepository
    private lateinit var pointUpdater: PointUpdater

    @BeforeEach
    fun setUp() {
        userPointRepository = UserPointRepositoryStub()
        pointHistoryRepository = PointHistoryRepositoryStub()
        pointUpdater = PointUpdater(userPointRepository, pointHistoryRepository)
    }

    // 포인트 충전 Component 단위 테스트
    @Test
    fun `포인트를 충전할 수 있다`() {
        // given
        userPointRepository.save(UserPoint(1L, 1000L, 123L))

        // when
        val result = pointUpdater.charge(1L, 100L)

        // then
        assertThat(result.point).isEqualTo(1100L)
        assertThat(result).isEqualTo(userPointRepository.getById(1L))
    }

    // 포인트 사용 Component 단위 테스트
    @Test
    fun `포인트를 사용할 수 있다`() {
        // given
        userPointRepository.save(UserPoint(1L, 1000L, 123L))

        // when
        val result = pointUpdater.use(1L, 100L)

        // then
        assertThat(result.point).isEqualTo(900L)
        assertThat(result).isEqualTo(userPointRepository.getById(1L))
    }

    // 포인트 내역 저장 Component 단위 테스트
    @Test
    fun `포인트 충전 이력을 저장할 수 있다`() {
        // when
        val result = pointUpdater.insertHistory(1L, 2000L, TransactionType.CHARGE, 123L)

        // then
        assertThat(result.userId).isEqualTo(1L)
        assertThat(result.amount).isEqualTo(2000L)
        assertThat(result.type).isEqualTo(TransactionType.CHARGE)
        assertThat(result.timeMillis).isEqualTo(123L)

        val savedHistories = pointHistoryRepository.getAllByUserId(1L)
        assertThat(savedHistories).hasSize(1)
        assertThat(result).isEqualTo(savedHistories[0])
    }
}