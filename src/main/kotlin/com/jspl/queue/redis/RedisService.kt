package com.jspl.queue.redis

import com.jspl.queue.MaxValue
import com.jspl.queue.Prefix
import org.redisson.api.RLock
import org.redisson.api.RScoredSortedSet
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class RedisService(
    private val redissonClient: RedissonClient,
) {
    fun dequeue(key: String, value: String) {
        val queue = getQueue(Prefix.stringWithPrefix(Prefix.WAITING, key))
        queue.removeRangeByRank(0, 0)
        getQueue(Prefix.stringWithPrefix(Prefix.PROCESS, key)).add(1.0, value)
    }

    fun contains(key: String, value: String): Boolean {
        return this.getQueue(key).contains(value)
    }

    fun getRank(key: String, value: String): Int?{
        return this.getQueue(key).rank(value)
    }
    fun getQueue(key: String): RScoredSortedSet<String> {
        return redissonClient.getScoredSortedSet(key)
    }

    fun executeWithLock(performanceId: String, memberId: String) {
        val score: Double = System.currentTimeMillis().toDouble()
        val lock: RLock = redissonClient.getLock(Prefix.stringWithPrefix(Prefix.LOCK, performanceId))
        try {
            val available = lock.tryLock(10, 5, TimeUnit.SECONDS)
            if (available) {

                this.enqueue(performanceId, memberId, score)

            } else {
                throw Exception()
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } finally {
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }

    private fun enqueue(performanceId: String, memberId: String, score: Double){
        val processQueue = this.getQueue(Prefix.stringWithPrefix(Prefix.PROCESS, performanceId))
        if (processQueue.size().toLong() >= MaxValue.PROCESS_QUEUE_MAX_SIZE.value) {
            val waitingQueue = this.getQueue(Prefix.stringWithPrefix(Prefix.WAITING, performanceId))
            if (!waitingQueue.contains(memberId)) {
                waitingQueue.add(score, memberId)
            }
        } else {
            processQueue.add(score, memberId)
        }
    }
}
