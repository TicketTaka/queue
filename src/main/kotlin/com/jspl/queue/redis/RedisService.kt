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

    fun pop(queueName: String): String? {
        val range = zSetOps.range(queueName, 0, 0)
        val firstElement = range?.firstOrNull()

        if (firstElement != null) {
            zSetOps.remove(queueName, firstElement)
        }

        return firstElement
    }

    fun getRank(queueName: String, value: String): Long? {
        return zSetOps.rank(queueName, value)
    }


    fun getScoreFromSortedSet(key: String, value: String): Double? {
        return zSetOps.score(key, value)
    }

    fun isValueInSortedSet(key: String, value: String): Boolean {
        val rank = zSetOps.rank(key, value)

        return rank != null
    }

//    fun checkInQueue(key: String): Boolean {
//        return redisTemplate.hasKey(key)
//    }

    fun sizeOfWorkingQueue(key: String): Long?{
        return zSetOps.zCard(key)
    }
}