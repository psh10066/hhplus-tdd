package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock

class PointServiceTest {

    private lateinit var pointHistoryTable: PointHistoryTable
    private lateinit var userPointTable: UserPointTable
    private lateinit var pointService: PointService

    @BeforeEach
    fun setUp() {
        pointHistoryTable = mock(PointHistoryTable::class.java)
        userPointTable = mock(UserPointTable::class.java)
        pointService = PointService(pointHistoryTable, userPointTable)
    }

    @Test
    fun `포인트를 조회할 수 있다`() {
        given(userPointTable.selectById(1L)).willReturn(UserPoint(1L, 100L, 1000L))

        val result = pointService.getPoint(1L)

        assertThat(result.point).isEqualTo(100L)
    }

    @Test
    fun `포인트 충전, 이용 내역을 조회할 수 있다`() {
        given(pointHistoryTable.selectAllByUserId(1L)).willReturn(listOf(
            PointHistory(1L, 1L, TransactionType.CHARGE, 2000L, System.currentTimeMillis()),
            PointHistory(2L, 1L, TransactionType.USE, 1000L, System.currentTimeMillis()),
        ))

        val result = pointService.getHistory(1L)

        assertThat(result[0].type).isEqualTo(TransactionType.CHARGE)
        assertThat(result[0].amount).isEqualTo(2000L)
        assertThat(result[1].type).isEqualTo(TransactionType.USE)
        assertThat(result[1].amount).isEqualTo(1000L)
    }

    @Test
    fun `포인트를 충전할 수 있다`() {
        given(userPointTable.selectById(1L)).willReturn(UserPoint(1L, 100L, System.currentTimeMillis()))
        given(userPointTable.insertOrUpdate(1L, 1100L)).willReturn(UserPoint(1L, 1100L, System.currentTimeMillis()))

        val result = pointService.charge(1L, 1000L)

        assertThat(result.point).isEqualTo(1100L)
    }

    @Test
    fun `포인트를 사용할 수 있다`() {
        given(userPointTable.selectById(1L)).willReturn(UserPoint(1L, 100L, System.currentTimeMillis()))
        given(userPointTable.insertOrUpdate(1L, 90L)).willReturn(UserPoint(1L, 90L, System.currentTimeMillis()))

        val result = pointService.use(1L, 10L)

        assertThat(result.point).isEqualTo(90L)
    }
}