package io.hhplus.tdd.point

import org.assertj.core.api.AssertionsForClassTypes.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class PointServiceTest {

    private lateinit var pointReader: PointReader
    private lateinit var pointUpdater: PointUpdater
    private lateinit var pointService: PointService

    @BeforeEach
    fun setUp() {
        pointReader = mock(PointReader::class.java)
        pointUpdater = mock(PointUpdater::class.java)
        pointService = PointService(pointReader, pointUpdater)
    }

    @Test
    fun `포인트를 조회할 수 있다`() {
        // given
        given(pointReader.getPoint(1L)).willReturn(UserPoint(1L, 100L, 1000L))

        // when
        val result = pointService.getPoint(1L)

        // then
        assertThat(result.point).isEqualTo(100L)
    }

    @Test
    fun `포인트 충전, 이용 내역을 조회할 수 있다`() {
        // given
        given(pointReader.getHistory(1L)).willReturn(
            listOf(
                PointHistory(1L, 1L, TransactionType.CHARGE, 2000L, System.currentTimeMillis()),
                PointHistory(2L, 1L, TransactionType.USE, 1000L, System.currentTimeMillis()),
            )
        )

        // when
        val result = pointService.getHistory(1L)

        // then
        assertThat(result[0].type).isEqualTo(TransactionType.CHARGE)
        assertThat(result[0].amount).isEqualTo(2000L)
        assertThat(result[1].type).isEqualTo(TransactionType.USE)
        assertThat(result[1].amount).isEqualTo(1000L)
    }

    @Test
    fun `포인트를 충전할 수 있다`() {
        // given
        given(pointUpdater.charge(1L, 100L)).willReturn(UserPoint(1L, 1100L, 123L))

        // when
        val result = pointService.charge(1L, 100L)

        // then
        assertThat(result.point).isEqualTo(1100L)
        verify(pointUpdater).insertHistory(1L, 1100L, TransactionType.CHARGE, 123L)
    }

    @Test
    fun `포인트를 사용할 수 있다`() {
        // given
        given(pointUpdater.use(1L, 100L)).willReturn(UserPoint(1L, 900L, 123L))

        // when
        val result = pointService.use(1L, 100L)

        // then
        assertThat(result.point).isEqualTo(900L)
        verify(pointUpdater).insertHistory(1L, 900L, TransactionType.USE, 123L)
    }
}