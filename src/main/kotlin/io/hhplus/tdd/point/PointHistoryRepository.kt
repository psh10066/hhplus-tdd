package io.hhplus.tdd.point

interface PointHistoryRepository {
    fun insert(userId: Long, amount: Long, transactionType: TransactionType, updateMillis: Long): PointHistory
    fun getAllByUserId(userId: Long): List<PointHistory>
}