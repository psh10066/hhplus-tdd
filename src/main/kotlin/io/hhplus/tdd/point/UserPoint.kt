package io.hhplus.tdd.point

data class UserPoint(
    val id: Long,
    val point: Long,
    val updateMillis: Long,
) {
    init {
        require(point >= 0) { "포인트 잔고가 부족합니다." }
        require(point <= 10_000_000) { "최대 잔고는 10,000,000 포인트 입니다." }
    }

    fun chargePoint(amount: Long): UserPoint {
        return UserPoint(id = id, point = point + amount, updateMillis = updateMillis)
    }

    fun usePoint(amount: Long): UserPoint {
        return UserPoint(id = id, point = point - amount, updateMillis = updateMillis)
    }
}
