package io.hhplus.tdd.impl

import io.hhplus.tdd.database.UserPointTable
import io.hhplus.tdd.point.UserPoint
import io.hhplus.tdd.point.UserPointRepository
import org.springframework.stereotype.Repository

@Repository
class UserPointRepositoryImpl(
    private val userPointTable: UserPointTable,
) : UserPointRepository {

    override fun getById(id: Long): UserPoint {
        return userPointTable.selectById(id)
    }

    override fun save(id: Long, amount: Long): UserPoint {
        return userPointTable.insertOrUpdate(id, amount)
    }
}