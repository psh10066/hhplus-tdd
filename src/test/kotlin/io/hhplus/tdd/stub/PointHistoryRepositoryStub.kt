package io.hhplus.tdd.stub

import io.hhplus.tdd.point.PointHistory
import io.hhplus.tdd.point.PointHistoryRepository
import io.hhplus.tdd.point.TransactionType

class PointHistoryRepositoryStub : PointHistoryRepository {

    private val table = mutableListOf<PointHistory>()
    private var cursor: Long = 1L

    override fun insert(
        userId: Long,
        amount: Long,
        transactionType: TransactionType,
        updateMillis: Long
    ): PointHistory {
        val history = PointHistory(
            id = cursor++,
            userId = userId,
            amount = amount,
            type = transactionType,
            timeMillis = updateMillis,
        )
        table.add(history)
        return history
    }

    override fun getAllByUserId(userId: Long): List<PointHistory> {
        return table.filter { it.userId == userId }
    }
}