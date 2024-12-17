package io.hhplus.tdd.point

import org.springframework.stereotype.Component

@Component
class PointReader(
    private val userPointRepository: UserPointRepository,
    private val pointHistoryRepository: PointHistoryRepository
) {
    fun getPoint(id: Long): UserPoint {
        return userPointRepository.getById(id)
    }

    fun getHistory(id: Long): List<PointHistory> {
        return pointHistoryRepository.getAllByUserId(id)
    }
}