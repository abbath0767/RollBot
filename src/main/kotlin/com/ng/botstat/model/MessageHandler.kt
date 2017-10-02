package com.ng.botstat.model

import com.ng.botstat.model.MessageFromUser

interface MessageHandler {
    fun handleMessage(message: MessageFromUser)
}