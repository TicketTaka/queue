package com.jspl.queue.service

import com.jspl.queue.redis.RedisService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@Service
class TicketService(
    private val redisService: RedisService,
    private val sseService: SseService
) {
    fun test(concertId: Long, memberId: Long): SseEmitter{
        println("Im in Service!")
        val score:Double = System.currentTimeMillis().toDouble()
        redisService.enqueue("콘서트1", memberId.toString(), score)
        return sseService.createEmitter(concertId.toString(), memberId.toString())
    }

    @Scheduled(fixedRate = 5000)
    fun pushNotifications() {
        sseService.pushEventToAll()
    }

}
