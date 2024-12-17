package io.hhplus.tdd.impl

import io.hhplus.tdd.database.PointHistoryTable
import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.PointHistoryRepository
import io.hhplus.tdd.point.TransactionType
import org.springframework.stereotype.Repository

@Repository
class PointHistoryRepositoryImpl(
    private val pointHistoryTable: PointHistoryTable
) : PointHistoryRepository {

    override fun insert(userId: Long, amount: Long, transactionType: TransactionType, updateMillis: Long): PointHistory {
        return pointHistoryTable.insert(userId, amount, transactionType, updateMillis)
    }

    override fun getAllByUserId(userId: Long): List<PointHistory> {
        return pointHistoryTable.selectAllByUserId(userId)
    }
}