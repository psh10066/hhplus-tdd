package io.hhplus.tdd.impl

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.UserPoint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UserPointRepositoryImplIT {

    private lateinit var userPointTable: UserPointTable
    private lateinit var userPointRepositoryImpl: UserPointRepositoryImpl

    @BeforeEach
    fun setUp() {
        userPointTable = UserPointTable()
        userPointRepositoryImpl = UserPointRepositoryImpl(userPointTable)
    }

    @Test
    fun `포인트를 조회할 수 있다`() {
        // given
        val savedPoint = userPointTable.insertOrUpdate(1L, 1000L)

        // when
        val result = userPointRepositoryImpl.getById(1L)

        // then
        assertThat(result).isEqualTo(savedPoint)
    }

    @Test
    fun `포인트를 저장할 수 있다`() {
        // when
        val result = userPointRepositoryImpl.save(UserPoint(1L, 1000L, 123L))

        // then
        val savedPoint = userPointTable.selectById(1L)
        assertThat(result).isEqualTo(savedPoint)
    }
}