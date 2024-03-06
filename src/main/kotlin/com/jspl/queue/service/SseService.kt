package com.jspl.queue.service

import com.jspl.queue.redis.RedisService
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Service
class SseService(
    private val redisService: RedisService
) {
//    private val emitterList = ConcurrentHashMap<String, MutableMap<String, SseEmitter>>()
    private val emitterList = ConcurrentHashMap<String, SseEmitter>()
    fun createEmitter(concertId: String, waitingId: String): SseEmitter {
        val emitter = SseEmitter(10000L * 60 * 60)
//        this.emitterList[concertId] = mutableMapOf(waitingId to emitter)
        this.emitterList[waitingId] = emitter
        emitter.onTimeout {
            this.emitterList.remove(waitingId)
        }
        return emitter
    }

//    fun deleteEmitter(waitingId: String) {
//        val emitter = this.emitterList[waitingId]
//        emitter!!.complete()
//        emitterList.remove(waitingId)
//    }

    fun pushEventToAll() {
        emitterList.forEach { emitter ->
            //emitter 의 키가 redis 의 유저 id
            var num = redisService.getRank("콘서트1", emitter.key)
                ?: -2
            when (num) {
                -2L -> {
                    if (redisService.isValueInSortedSet("working",emitter.key)) {
                        num = -1L
                        emitter.value.complete()
                        emitterList.remove(emitter.key)
                    }
                }
            }
            try {
                emitter.value.send(num)
            }catch (e: Exception) {
                println("익쩹쪈!")
                emitter.value.completeWithError(e)
                emitterList.remove(emitter.key)
            }
        }
    }
}