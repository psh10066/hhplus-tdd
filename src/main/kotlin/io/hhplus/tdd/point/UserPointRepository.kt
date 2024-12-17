package io.hhplus.tdd.point

interface UserPointRepository {
    fun getById(id: Long): UserPoint
    fun save(userPoint: UserPoint): UserPoint
}