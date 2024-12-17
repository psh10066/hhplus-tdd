package io.hhplus.tdd.stub

import io.hhplus.tdd.point.UserPoint
import io.hhplus.tdd.point.UserPointRepository

class UserPointRepositoryStub : UserPointRepository {

    private val table = HashMap<Long, UserPoint>()

    override fun getById(id: Long): UserPoint {
        return table[id] ?: UserPoint(id = id, point = 0, updateMillis = System.currentTimeMillis())
    }

    override fun save(id: Long, amount: Long): UserPoint {
        val userPoint = UserPoint(id = id, point = amount, updateMillis = System.currentTimeMillis())
        table[id] = userPoint
        return userPoint
    }
}