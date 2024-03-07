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
    fun waitingEnqueue(performanceId: String, memberId: String): SseEmitter{
        println("Im in Service!")
        val score:Double = System.currentTimeMillis().toDouble()
        redisService.enqueue(performanceId, memberId, score)
        var result: SseEmitter
        try {
            result = sseService.createEmitter(performanceId, memberId)
        } catch (e: Exception) {
            println("익쪠ㅃ쪠뼤쪠뼤쪈")
            result = SseEmitter(100000)
            result.send("EXCEPTION!")
        }
        return result
    }

//    @Scheduled(fixedRate = 5000)
//    fun pushNotifications() {
//        sseService.pushEventToAll()
//    }

}
