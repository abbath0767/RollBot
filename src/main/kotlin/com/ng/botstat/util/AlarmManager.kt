package com.ng.botstat.util

import com.ng.botstat.model.MessageHandler
import mu.KotlinLogging
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmManager(val messageHandler: MessageHandler) {

    private val logger = KotlinLogging.logger {}
    private var timer: Timer? = null

    fun timeIsValid(comment: String): Boolean {
        try {
            comment.split(":")[0]
            comment.split(":")[1]
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun generateTimeForNextAlarm(comment: String): Long {
        val hour = comment.split(":")[0]
        val minute = comment.split(":")[1]

        var dateNow = Calendar.getInstance()
        var dateAlarm = Calendar.getInstance()
        dateAlarm.set(Calendar.HOUR_OF_DAY, hour.toInt())
        dateAlarm.set(Calendar.MINUTE, minute.toInt())
        val relation = dateNow.before(dateAlarm)

        logger.info { "generate: ${dateAlarm.time}" }

        return dateAlarm.time.time
    }

    fun timeIsAfter(comment: String): Boolean {
        val hour = comment.split(":")[0]
        val minute = comment.split(":")[1]

        var dateNow = Calendar.getInstance()
        var dateAlarm = Calendar.getInstance()
        dateAlarm.set(Calendar.HOUR, hour.toInt())
        dateAlarm.set(Calendar.MINUTE, minute.toInt())

        return dateNow.before(dateAlarm)
    }

    fun setUpAlarmIn(timeForAlarm: Long, chatId: Long, nextDay: Boolean) {
        if (timer == null) {
            timer = Timer("my timer")
        }

        var dateForAlarm = Date(timeForAlarm)

        if (nextDay) {
            val cal = Calendar.getInstance()
            cal.time = dateForAlarm
            cal.add(Calendar.DATE, 1)
            dateForAlarm = cal.time
        }

        logger.info { "time now: ${Date()}, date alarm: ${dateForAlarm} "}

        timer?.schedule(AlarmTimerTask(chatId, messageHandler), dateForAlarm, TimeUnit.HOURS.toMillis(24))
    }
}