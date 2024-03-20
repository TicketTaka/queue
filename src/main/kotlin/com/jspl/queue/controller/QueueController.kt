package com.jspl.queue.controller

import com.jspl.queue.module3.CustomTaskScheduler
import com.jspl.queue.service.QueueService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
class QueueController(
    private val queueService: QueueService,
    private val customTaskScheduler: CustomTaskScheduler
) {

    @PostMapping("/api/queues/performances/{performanceId}/members/{memberId}")
    fun enqueue(
        @PathVariable performanceId: Long,
        @PathVariable memberId: Long
    ): SseEmitter{
        val result = queueService.enqueueAndConnectSse(performanceId.toString(), memberId.toString())
        return result
    }

    @DeleteMapping("/api/queues/performances/{performanceId}/members/{memberId}")
    fun dequeue(
        @PathVariable performanceId: Long,
        @PathVariable memberId: Long
    ) {
        queueService.dequeue(performanceId.toString(), memberId.toString())
    }


//    @PostMapping("/api/queues/performances/{performanceId}/members/{memberId}/asd")
//    fun ticketing(
//        @PathVariable performanceId: Long,
//        @PathVariable memberId: Long
//    ) : SseEmitter{
//
//        println("Im in Controller!")
//        val result = queueService.enqueueAndConnectSse(performanceId.toString(), memberId.toString())
//        return result
//    }

    //공연개시!
    @GetMapping("api/schedules/performances/{performanceId}")
    fun startTicketing(@PathVariable performanceId: Long){
//        customTaskScheduler.scheduleTaskWithVariable(performanceId.toString())
        customTaskScheduler.sseScheduler(performanceId.toString())
    }

}