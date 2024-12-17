package io.hhplus.tdd.point

import io.hhplus.tdd.lock.LockManager
import org.springframework.stereotype.Service

@Service
class PointService(
    private val pointReader: PointReader,
    private val pointUpdater: PointUpdater,
) {
    private val lockManager = LockManager()

    fun getPoint(id: Long): UserPoint {
        return lockManager.withLock(id) {
            pointReader.getPoint(id)
        }
    }

    fun getHistories(id: Long): List<PointHistory> {
        return pointReader.getHistories(id)
    }

    fun charge(id: Long, amount: Long): UserPoint {
        val userPoint = lockManager.withLock(id) {
            pointUpdater.charge(id, amount)
        }
        pointUpdater.insertHistory(id, userPoint.point, TransactionType.CHARGE, userPoint.updateMillis)
        return userPoint
    }

    fun use(id: Long, amount: Long): UserPoint {
        val userPoint = lockManager.withLock(id) {
            pointUpdater.use(id, amount)
        }
        pointUpdater.insertHistory(id, userPoint.point, TransactionType.USE, userPoint.updateMillis)
        return userPoint
    }
}