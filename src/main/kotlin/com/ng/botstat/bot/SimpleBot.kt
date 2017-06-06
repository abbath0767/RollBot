package com.ng.botstat.bot

import com.ng.botstat.db.FirebaseRepository
import com.ng.botstat.model.MessageHandler
import com.ng.botstat.model.MessageParser
import com.ng.botstat.util.BotParams
import mu.KLogging
import org.telegram.telegrambots.api.methods.send.SendMessage
import org.telegram.telegrambots.api.objects.Message
import org.telegram.telegrambots.api.objects.Update
import org.telegram.telegrambots.bots.TelegramLongPollingCommandBot

/**
 * Created by NG on 02.06.17.
 */
class SimpleBot : TelegramLongPollingCommandBot(), MessageSender {

    companion object : KLogging()
    var parser = MessageParser.instance
    var repository = FirebaseRepository.instance
    val messageHandler = MessageHandler.getInstence(this, repository)
    var emptyMessage: Message = Message()

    override fun getBotUsername(): String {
        logger.info { "getBotUserName" }
        return BotParams.BOT_NAME.value
    }

    override fun getBotToken(): String {
        logger.info { "getBotToken" }
        return BotParams.BOT_KEY.value
    }

    override fun processNonCommandUpdate(update: Update?) {
        logger.info { "onUpdateReceived" }

        var message = update?.message ?: emptyMessage

        var pMessage = parser.parseMessage(message)

        messageHandler.handleMessage(pMessage)
    }

    override fun sendMessageMsg(message: SendMessage) {
        logger.info { "send message: $message" }
        sendMessage(message)
    }
}