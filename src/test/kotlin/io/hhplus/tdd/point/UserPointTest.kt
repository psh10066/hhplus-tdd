package io.hhplus.tdd.point

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test

class UserPointTest {

    // UserPoint 생성 시 포인트 상한 경곗값 테스트
    @Test
    fun `최대 10,000,000 포인트까지 생성할 수 있다`() {
        // when
        UserPoint(1L, 10_000_000L, 123L)
    }

    // UserPoint 충전 시 포인트 상한 경곗값 테스트
    @Test
    fun `최대 10,000,000 포인트까지 충전할 수 있다`() {
        // given
        val userPoint = UserPoint(1L, 9_000_000L, 123L)

        // when
        val result = userPoint.chargePoint(1_000_000L)

        // then
        assertThat(result.point).isEqualTo(10_000_000L)
    }

    // UserPoint 생성 시 포인트 상한 경곗값 초과 테스트
    @Test
    fun `10,000,000 포인트를 초과하여 생성할 수 없다`() {
        // when then
        assertThatThrownBy({
            UserPoint(1L, 10_000_001L, 123L)
        })
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("최대 잔고는 10,000,000 포인트 입니다.")
    }

    // UserPoint 충전 시 포인트 상한 경곗값 초과 테스트
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

    // UserPoint 생성 시 포인트 하한 경곗값 테스트
    @Test
    fun `최소 0 포인트까지 생성될 수 있다`() {
        // when
        UserPoint(1L, 0L, 123L)
    }

    // UserPoint 사용 시 포인트 하한 경곗값 테스트
    @Test
    fun `최소 0 포인트가 될 때까지 사용할 수 있다`() {
        // given
        val userPoint = UserPoint(1L, 1000L, 123L)

        // when
        val result = userPoint.usePoint(1000L)

        // then
        assertThat(result.point).isEqualTo(0L)
    }

    // UserPoint 생성 시 포인트 하한 경곗값 미만 테스트
    @Test
    fun `포인트가 0 미만으로 내려가도록 생성할 수 없다`() {
        // when then
        assertThatThrownBy({
            UserPoint(1L, -1L, 123L)
        })
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("포인트 잔고가 부족합니다.")
    }

    // UserPoint 사용 시 포인트 하한 경곗값 미만 테스트
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