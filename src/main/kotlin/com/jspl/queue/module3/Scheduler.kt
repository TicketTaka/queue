package com.jspl.queue.module3

import com.jspl.queue.redis.RedisService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class Scheduler(
    private val redisService: RedisService
) {
//    @Scheduled (fixedRate = 1000)
    fun waitingToWorking(){
        //working queue 조회 후 비어있으면 넣기 !
        val size:Long? = redisService.sizeOfWorkingQueue("working")
        if (size == null || size == 0L || size < 2) {
            val element = redisService.pop("콘서트1")
                ?: "null"
            val score:Double = System.currentTimeMillis().toDouble()
            redisService.enqueue("working", element, score)
        }
    }
}