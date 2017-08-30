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

    //todo change this shit! by Ivan {}
    companion object {
        val instance: MessageParser by lazy { Holder.INSTANCE }
    }

    fun parseMessage(message: Message): com.ng.botstat.model.MessageFromUser {
        logger.info { "message: $message" }
        val type =
                when (message.text) {
                    Command.HELP.value, CommandRegister.HELP.value -> Command.HELP
                    Command.REG.value, CommandRegister.REG.value -> Command.REG
                    Command.ROLL.value, CommandRegister.ROLL.value -> Command.ROLL
                    Command.STAT.value, CommandRegister.STAT.value -> Command.STAT
                    else -> Command.UNKNOWN_COMMAND
                }

        return MessageFromUser(type, message.chatId, message.from.firstName + " " + message.from.lastName)
    }
}