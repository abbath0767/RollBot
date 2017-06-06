package com.ng.botstat.bot

import org.telegram.telegrambots.api.methods.send.SendMessage

/**
 * Created by NG on 05.06.17.
 */
interface MessageSender {
    fun sendMessageMsg(message: SendMessage)
}