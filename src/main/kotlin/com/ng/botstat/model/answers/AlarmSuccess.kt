package com.ng.botstat.model.answers

import java.text.SimpleDateFormat
import java.util.*

class AlarmSuccess(val timeForAlarm: Long) : MessageWithText() {

    override fun getText(): String {
        val humanReadableTime = SimpleDateFormat("dd MMMM HH:mm").format(Date(timeForAlarm))
        return String.format(getString(alarmSuccess), humanReadableTime)
    }
}