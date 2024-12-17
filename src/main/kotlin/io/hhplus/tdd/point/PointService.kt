package io.hhplus.tdd.point

import org.springframework.stereotype.Service

@Service
class PointService(
    private val pointReader: PointReader,
    private val pointUpdater: PointUpdater,
) {
    fun getPoint(id: Long): UserPoint {
        return pointReader.getPoint(id)
    }

    fun getHistories(id: Long): List<PointHistory> {
        return pointReader.getHistories(id)
    }

    fun charge(id: Long, amount: Long): UserPoint {
        val userPoint = pointUpdater.charge(id, amount)
        pointUpdater.insertHistory(id, userPoint.point, TransactionType.CHARGE, userPoint.updateMillis)
        return userPoint
    }

    fun use(id: Long, amount: Long): UserPoint {
        val userPoint = pointUpdater.use(id, amount)
        pointUpdater.insertHistory(id, userPoint.point, TransactionType.USE, userPoint.updateMillis)
        return userPoint
    }
}