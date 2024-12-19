package io.hhplus.tdd.impl

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.TransactionType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PointHistoryRepositoryImplIT {

    private lateinit var pointHistoryTable: PointHistoryTable
    private lateinit var pointHistoryRepositoryImpl: PointHistoryRepositoryImpl

    @BeforeEach
    fun setUp() {
        pointHistoryTable = PointHistoryTable()
        pointHistoryRepositoryImpl = PointHistoryRepositoryImpl(pointHistoryTable)
    }

    // 포인트 내역 저장 Repository 통합 테스트
    @Test
    fun `포인트 충전, 이용 내역을 저장할 수 있다`() {
        // when
        val result = pointHistoryRepositoryImpl.insert(1L, 1000L, TransactionType.CHARGE, 123L)

        // then
        val savedHistories = pointHistoryTable.selectAllByUserId(1L)
        assertThat(savedHistories).hasSize(1)
        assertThat(savedHistories[0]).isEqualTo(result)
    }

    // 포인트 내역 조회 Repository 통합 테스트
    @Test
    fun `회원의 포인트 충전, 이용 내역을 조회할 수 있다`() {
        // given
        val savedHistory = pointHistoryTable.insert(1L, 1000L, TransactionType.USE, 123L)
        pointHistoryTable.insert(2L, 2000L, TransactionType.CHARGE, 456L) // 다른 회원

        // when
        val result = pointHistoryRepositoryImpl.getAllByUserId(1L)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0]).isEqualTo(savedHistory)
    }
}