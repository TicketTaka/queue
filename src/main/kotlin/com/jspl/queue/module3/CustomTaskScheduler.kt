//package com.jspl.queue.module3
//
//import org.springframework.scheduling.TaskScheduler
//import org.springframework.scheduling.Trigger
//import org.springframework.scheduling.config.FixedDelayTask
//import org.springframework.scheduling.support.PeriodicTrigger
//import org.springframework.stereotype.Component
//import java.util.concurrent.ScheduledFuture
//import java.util.concurrent.TimeUnit
//
//@Component
//class CustomTaskScheduler(
//    private val taskScheduler: TaskScheduler
//) {
//
//    private var scheduledTask: ScheduledFuture<*>? = null
//
//    fun startTask() {
//        val task = Runnable { println("Task is running!") }
//        val trigger = PeriodicTrigger(2000, )
//
//        // 스케줄링 작업 생성 및 시작
//        scheduledTask = taskScheduler.schedule(task, trigger)
//    }
//
//    fun stopTask() {
//        // 스케줄링 작업 중지
//        scheduledTask?.cancel(true)
//    }
//}