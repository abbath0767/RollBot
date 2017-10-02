package com.ng.botstat.model

import org.telegram.telegrambots.api.methods.send.SendMessage

interface MessageHandler {
    fun handleMessage(message: MessageFromUser)
    fun sendMessage(messageToSend: SendMessage)
}