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

    override fun save(userPoint: UserPoint): UserPoint {
        return userPointTable.insertOrUpdate(userPoint.id, userPoint.point)
    }
}