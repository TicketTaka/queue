package com.jspl.queue.redis

import org.springframework.data.redis.core.ListOperations
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ZSetOperations
import org.springframework.stereotype.Service

@Service
class RedisService(
    private val redisTemplate: RedisTemplate<String, String>,
) {
//    private lateinit var zSetOps: ZSetOperations<String, String>

    private val zSetOps: ZSetOperations<String, String> = redisTemplate.opsForZSet()

    private val listOps: ListOperations<String, String> = redisTemplate.opsForList()

    fun enqueue(queueName: String, value: String, score: Double) {
        zSetOps.add(queueName, value, score)
    }

    fun getRank(queueName: String, value: String): Long? {
        return zSetOps.rank(queueName, value)
    }


    fun getScoreFromSortedSet(key: String, value: String): Double? {
        return zSetOps.score(key, value)
    }

    fun checkInQueue(key: String): Boolean {
        return redisTemplate.hasKey(key)
    }
}