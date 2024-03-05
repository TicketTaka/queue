package com.jspl.queue.controller

import com.jspl.queue.service.TicketService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter

@RestController
class RedisController(
    private val ticketService: TicketService
) {
    @PostMapping("api/redis/test/{memberId}")
    fun ticket(@PathVariable memberId: Long) : SseEmitter{
        println("Im in Controller!")
        val result = ticketService.test(1, memberId)
        return result
    }
}