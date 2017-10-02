package com.ng.botstat.util

import com.ng.botstat.model.MessageHandler
import com.ng.botstat.model.MessageToRoll
import mu.KotlinLogging
import java.util.*

class AlarmTimerTask(val chatId: Long, val messageHandler: MessageHandler) : TimerTask() {

    private val logger = KotlinLogging.logger {}

    override fun run() {
        logger.info { "task is started!" }
        logger.info { "Current time: ${Date()}" }

        rollCommand()
    }

    fun rollCommand() {
        messageHandler.handleMessage(MessageToRoll(Command.ROLL, chatId))
    }
}