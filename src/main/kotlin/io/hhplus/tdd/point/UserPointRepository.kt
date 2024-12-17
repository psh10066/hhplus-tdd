package io.hhplus.tdd.point

interface UserPointRepository {
    fun getById(id: Long): UserPoint
    fun save(id: Long, amount: Long): UserPoint
}