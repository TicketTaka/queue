package com.jspl.queue.module3

import com.jspl.queue.redis.RedisService
import com.jspl.queue.service.SseService
import org.springframework.scheduling.TaskScheduler
import org.springframework.stereotype.Service
import java.time.Duration

@Service
class CustomTaskScheduler(
    private val taskScheduler: TaskScheduler,
    private val redisService: RedisService,
    private val sseService: SseService
) {
    fun scheduleTaskWithVariable(performanceId: String) {
        val workingQueueKey = "w${performanceId}"
        val task = Runnable {
            println("Running task with variable $performanceId")

            //working queue 조회
            val workingQueueSize: Long? = redisService.sizeOfWorkingQueue(workingQueueKey)
            //working queue 가 비어있거나 자리가 있다면
            if (workingQueueSize == null || workingQueueSize == 0L || workingQueueSize < 2) {
                //대기큐에서 pop 하고
                val waitingElement = redisService.pop(performanceId)
                //작업 큐에 enqueue
                if (waitingElement != null) {
                    val score: Double = System.currentTimeMillis().toDouble()
                    redisService.enqueue(workingQueueKey, waitingElement, score)
                }
            }
            //만료는 요청시에 처리!
        }
//        taskScheduler.schedule(task, Trigger { Instant.now().plusSeconds(1) }) // 1초 후에 실행
        taskScheduler.scheduleAtFixedRate(task, Duration.ofSeconds(2))
    }

    fun sseScheduler(performanceId: String) {
        val task = Runnable {
            println("sse scheduler $performanceId")
            sseService.pushEventToAll(performanceId)
            //만료는 요청시에 처리!
        }
        taskScheduler.scheduleAtFixedRate(task, Duration.ofSeconds(5))
    }
}