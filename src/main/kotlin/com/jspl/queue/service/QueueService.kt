package com.jspl.queue.service

import com.jspl.queue.redis.RedisService
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class QueueService(
    private val redisService: RedisService,
    private val sseService: SseService
) {
    fun enqueueAndConnectSse(performanceId: String, memberId: String): SseEmitter{
        redisService.executeWithLock(performanceId, memberId)
        val result: SseEmitter = sseService.createEmitter(performanceId, memberId)
        return result
    }

    fun dequeue(key: String, memberId: String) {
        redisService.dequeue(key, memberId)
    }

}
