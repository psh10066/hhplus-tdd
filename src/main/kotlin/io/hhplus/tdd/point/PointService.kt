package io.hhplus.tdd.point

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.database.UserPointTable
import org.springframework.stereotype.Service

@Service
class PointService(
    private val pointHistoryTable: PointHistoryTable,
    private val userPointTable: UserPointTable
) {
    fun getPoint(id: Long): UserPoint {
        return userPointTable.selectById(id)
    }

    fun getHistory(id: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(id)
    }

    fun charge(id: Long, amount: Long): UserPoint {
        val userPoint = userPointTable.selectById(id)
        val newPoint = userPoint.point + amount
        return userPointTable.insertOrUpdate(id, newPoint)
    }

    fun use(id: Long, amount: Long): UserPoint {
        val userPoint = userPointTable.selectById(id)
        val newPoint = userPoint.point - amount
        return userPointTable.insertOrUpdate(id, newPoint)
    }
}