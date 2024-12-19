package io.hhplus.tdd.point

import io.hhplus.tdd.stub.PointHistoryRepositoryStub
import io.hhplus.tdd.stub.UserPointRepositoryStub
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PointReaderTest {

    private lateinit var userPointRepository: UserPointRepository
    private lateinit var pointHistoryRepository: PointHistoryRepository
    private lateinit var pointReader: PointReader

    @BeforeEach
    fun setUp() {
        userPointRepository = UserPointRepositoryStub()
        pointHistoryRepository = PointHistoryRepositoryStub()
        pointReader = PointReader(userPointRepository, pointHistoryRepository)
    }

    // 포인트 조회 Component 단위 테스트
    @Test
    fun `포인트를 조회할 수 있다`() {
        // given
        val savedPoint = userPointRepository.save(UserPoint(1L, 100L, 123L))

        // when
        val result = pointReader.getPoint(1L)

        // then
        assertThat(result).isEqualTo(savedPoint)
    }

    // 포인트 내역 조회 Component 단위 테스트
    @Test
    fun `회원의 포인트 충전, 이용 내역을 조회할 수 있다`() {
        // given
        pointHistoryRepository.insert(1L, 2000L, TransactionType.CHARGE, 123L)
        pointHistoryRepository.insert(1L, 1000L, TransactionType.USE, 456L)
        pointHistoryRepository.insert(2L, 3000L, TransactionType.CHARGE, 789L) // 다른 회원

        // when
        val result = pointReader.getHistories(1L)

        // then
        assertThat(result).hasSize(2)
        assertThat(result[0].type).isEqualTo(TransactionType.CHARGE)
        assertThat(result[0].amount).isEqualTo(2000L)
        assertThat(result[1].type).isEqualTo(TransactionType.USE)
        assertThat(result[1].amount).isEqualTo(1000L)
    }
}