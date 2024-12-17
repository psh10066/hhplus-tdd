package io.hhplus.tdd.point

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class UserPointTest {

    @Test
    fun `최대 10,000,000 포인트까지 생성할 수 있다`() {
        // when
        UserPoint(1L, 10_000_000L, 123L)
    }

    @Test
    fun `최대 10,000,000 포인트까지 충전할 수 있다`() {
        // given
        val userPoint = UserPoint(1L, 9_000_000L, 123L)

        // when
        val result = userPoint.chargePoint(1_000_000L)

        // then
        assertThat(result.point).isEqualTo(10_000_000L)
    }

    @Test
    fun `10,000,000 포인트를 초과하여 생성할 수 없다`() {
        // when then
        assertThatThrownBy({
            UserPoint(1L, 10_000_001L, 123L)
        })
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("최대 잔고는 10,000,000 포인트 입니다.")
    }

    @Test
    fun `10,000,000 포인트를 초과하여 충전할 수 없다`() {
        // given
        val userPoint = UserPoint(1L, 9_000_000L, 123L)

        // when then
        assertThatThrownBy({
            userPoint.chargePoint(1_000_001L)
        })
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("최대 잔고는 10,000,000 포인트 입니다.")
    }

    @Test
    fun `최소 0 포인트까지 생성될 수 있다`() {
        // when
        UserPoint(1L, 0L, 123L)
    }

    @Test
    fun `최소 0 포인트가 될 때까지 사용할 수 있다`() {
        // given
        val userPoint = UserPoint(1L, 1000L, 123L)

        // when
        val result = userPoint.usePoint(1000L)

        // then
        assertThat(result.point).isEqualTo(0L)
    }

    @Test
    fun `포인트가 0 미만으로 내려가도록 생성할 수 없다`() {
        // when then
        assertThatThrownBy({
            UserPoint(1L, -1L, 123L)
        })
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("포인트 잔고가 부족합니다.")
    }

    @Test
    fun `포인트가 0 미만으로 내려가도록 사용할 수 없다`() {
        // given
        val userPoint = UserPoint(1L, 1000L, 123L)

        // when then
        assertThatThrownBy({
            userPoint.usePoint(1001L)
        })
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("포인트 잔고가 부족합니다.")
    }
}