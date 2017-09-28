package com.ng.botstat.model.answers

class AlarmError(val userName: String, val incorrectTime: String) : MessageWithText() {

    override fun getText(): String {
        return String.format(getString(alarmError), userName, incorrectTime)
    }
}