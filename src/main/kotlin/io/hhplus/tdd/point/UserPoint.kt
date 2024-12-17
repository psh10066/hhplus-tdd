package io.hhplus.tdd.point

data class UserPoint(
    val id: Long,
    val point: Long,
    val updateMillis: Long,
) {
    fun chargePoint(amount: Long): UserPoint {
        return UserPoint(id = id, point = point + amount, updateMillis = updateMillis)
    }

    fun usePoint(amount: Long): UserPoint {
        return UserPoint(id = id, point = point - amount, updateMillis = updateMillis)
    }
}
