package com.ng.botstat.model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.ng.botstat.bot.MessageSender
import com.ng.botstat.db.Repository
import com.ng.botstat.model.answers.*
import com.ng.botstat.model.strategy.*
import com.ng.botstat.util.AlarmManager
import com.ng.botstat.util.Command
import com.ng.botstat.util.DBTable
import mu.KotlinLogging
import org.telegram.telegrambots.api.methods.send.SendMessage
import java.util.*

/**
 * Created by NG on 05.06.17.
 */
class MessageHandlerImpl private constructor(private val bot: MessageSender,
                                             private val repo: Repository,
                                             private val alarmManager: AlarmManager) : MessageHandler {

    private val logger = KotlinLogging.logger {}

    companion object {
        private var instance: MessageHandlerImpl? = null

        fun getInstance(bot: MessageSender, repo: Repository, alarmManager: AlarmManager): MessageHandlerImpl {
            if (instance == null)
                instance = MessageHandlerImpl(bot, repo, alarmManager)

            return instance!!
        }
    }

    override fun handleMessage(message: MessageFromUser) {
        val messageToSend = SendMessage()
        messageToSend.setChatId(message.chatId)

        logger.info { "handle message: $message" }

        when (message.type) {
            Command.HELP -> {
                HelpStrategy(messageToSend, this).invoke()
            }

            Command.REG -> {
                RegStrategy(repo, message, messageToSend, this).invoke()
            }

            Command.ROLL -> {
                RollStrategy(repo, message, messageToSend, this).invoke()
            }

            Command.STAT -> {
                StatStrategy(repo, message, messageToSend, this).invoke()
            }

            Command.ALARM -> {
                AlarmStategy(message, messageToSend, alarmManager, this).invoke()
            }

            else -> {
                //ignore messages
//                messageToSend.text = AnswerUnknown().getText()
//                messageToSend.send()
            }
        }
    }

    override fun sendMessage(messageToSend: SendMessage) {
        messageToSend.send()
    }

    //wtf? more extension function for god of extension functions?!
    private fun SendMessage.send() {
        bot.sendMessageMsg(this)
    }
}
