package io.hhplus.tdd.point

import org.springframework.stereotype.Component

@Component
class PointUpdater(
    private val userPointRepository: UserPointRepository,
    private val pointHistoryRepository: PointHistoryRepository
) {

    fun charge(id: Long, amount: Long): UserPoint {
        val userPoint = userPointRepository.getById(id)
        return userPointRepository.save(id, userPoint.point + amount)
    }

    fun use(id: Long, amount: Long): UserPoint {
        val userPoint = userPointRepository.getById(id)
        return userPointRepository.save(id, userPoint.point - amount)
    }

    fun insertHistory(userId: Long, amount: Long, transactionType: TransactionType, updateMillis: Long): PointHistory {
        return pointHistoryRepository.insert(userId, amount, transactionType, updateMillis)
    }
}