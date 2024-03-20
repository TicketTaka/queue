package com.jspl.queue.service

import com.jspl.queue.Prefix
import com.jspl.queue.redis.RedisService
import org.springframework.stereotype.Service
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.ConcurrentHashMap

@Service
class SseService(
    private val redisService: RedisService,
) {
    private val emitterList = ConcurrentHashMap<String, ConcurrentHashMap<String, SseEmitter>>()
    fun createEmitter(performanceId: String, memberId: String): SseEmitter {
        val emitter = SseEmitter(10000L * 60 * 60)
        val performanceEmitterList = this.emitterList.getOrPut(performanceId) { ConcurrentHashMap() }
        performanceEmitterList[memberId] = emitter
//        emitter.onTimeout {
//            this.emitterList[performanceId]!!.remove(memberId)
//        }
        emitter.send("Connect!")
        return emitter
    }
    fun pushEventToAll(performanceId: String) {
        val processQueueKey = Prefix.stringWithPrefix(Prefix.PROCESS, performanceId)
        val waitingQueueKey = Prefix.stringWithPrefix(Prefix.WAITING, performanceId)
        val performanceEmitterList = emitterList[performanceId] ?: return
        performanceEmitterList.forEach { emitter ->
            //emitter 의 키가 redis 의 유저 id
            //대기큐에 없다면 -2
            var turn = redisService.getRank(waitingQueueKey, emitter.key)
                ?: -2
            when (turn) {
                -2 -> {
                    //대기큐에 없고 작업큐에는 있다면 -1
                    if (redisService.contains(processQueueKey, emitter.key)) {
                        turn = -1
                    }
                    try {
                        emitter.value.send(turn)
                    } catch (e: Exception) {
//                        e.printStackTrace()
                    } finally {
                        emitter.value.complete()
                        performanceEmitterList.remove(emitter.key)
                    }

                }
            }
            try {
                emitter.value.send(turn)
            } catch (e: IllegalStateException) {
                emitter.value.complete()
                performanceEmitterList.remove(emitter.key)
            }
        }
    }
}