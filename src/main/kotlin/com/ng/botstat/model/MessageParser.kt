package com.ng.botstat.model

import com.ng.botstat.util.Command
import com.ng.botstat.util.CommandRegister
import mu.KotlinLogging
import org.telegram.telegrambots.api.objects.Message

/**
 * Created by NG on 05.06.17.
 */
class MessageParser private constructor() {

    private val logger = KotlinLogging.logger {}

    private object Holder {
        val INSTANCE = MessageParser()
    }

    companion object {
        val instance: MessageParser by lazy { Holder.INSTANCE }
    }

    fun parseMessage(message: Message): MessageFromUser {
//        logger.info { "message: $message" }
        logger.info { "message: ${message.text}" }
        val parsedCommand = message.text.split(" ")[0]
        val comment = message.text.substringAfter(" ")
        logger.info { "message comment: $comment" }

        val type =
                when (parsedCommand) {
                    Command.HELP.value, CommandRegister.HELP.value -> Command.HELP
                    Command.REG.value, CommandRegister.REG.value -> Command.REG
                    Command.ROLL.value, CommandRegister.ROLL.value -> Command.ROLL
                    Command.STAT.value, CommandRegister.STAT.value -> Command.STAT
                    Command.ALARM.value, CommandRegister.ALARM.value -> Command.ALARM
                    else -> Command.UNKNOWN_COMMAND
                }

        return MessageFromUser(type, message.chatId, message.from.firstName + " " + message.from.lastName, comment)
    }
}