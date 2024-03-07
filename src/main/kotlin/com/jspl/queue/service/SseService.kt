package com.jspl.queue.service

import com.jspl.queue.redis.RedisService
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Service
class SseService(
    private val redisService: RedisService,
) {
    //    private val emitterList = ConcurrentHashMap<String, MutableMap<String, SseEmitter>>()
    private val emitterList = ConcurrentHashMap<String, ConcurrentHashMap<String, SseEmitter>>()
    fun createEmitter(performanceId: String, memberId: String): SseEmitter {
        val emitter = SseEmitter(10000L * 60 * 60)
//        this.emitterList[concertId] = mutableMapOf(waitingId to emitter)
        val performanceEmitterList = this.emitterList.getOrPut(performanceId) { ConcurrentHashMap() }
        performanceEmitterList[memberId] = emitter
//        emitter.onTimeout {
//            this.emitterList[performanceId].remove(memberId)
//        }
        emitter.send("Connect!")
        return emitter
    }

//    fun deleteEmitter(waitingId: String) {
//        val emitter = this.emitterList[waitingId]
//        emitter!!.complete()
//        emitterList.remove(waitingId)
//    }

    fun pushEventToAll(performanceId: String) {
        val workingQueueKey = "w${performanceId}"
        println("여기타느뇨?")
        val performanceEmitterList = emitterList[performanceId] ?: return
        println(performanceEmitterList.size)
        performanceEmitterList.forEach { emitter ->
            //emitter 의 키가 redis 의 유저 id
            //대기큐에 없다면 -2
            var num = redisService.getRank(performanceId, emitter.key)
                ?: -2L
            when (num) {
                -2L -> {
                    //대기큐에 없고 작업큐에는 있다면 -1
                    if (redisService.isValueInSortedSet(workingQueueKey, emitter.key)) {
                        num = -1L
                    }
                    try {
                        emitter.value.send(num)
                    } catch (e: Exception) {
                        println("없는데 익셉션!")
                    } finally {
                        emitter.value.complete()
                        performanceEmitterList.remove(emitter.key)
                    }

                }
            }
            try {
                emitter.value.send(num)
            } catch (e: IllegalStateException) {
                e.printStackTrace()
                println("익쩹쪈!")
                emitter.value.complete()
                performanceEmitterList.remove(emitter.key)
            }
        }
    }
}