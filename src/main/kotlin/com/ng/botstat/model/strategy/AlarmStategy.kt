package com.ng.botstat.model.strategy

import com.ng.botstat.model.MessageFromUser
import com.ng.botstat.model.MessageFromUserWithComment
import com.ng.botstat.model.MessageHandler
import com.ng.botstat.model.answers.AlarmError
import com.ng.botstat.model.answers.AlarmSuccess
import com.ng.botstat.util.AlarmManager
import mu.KotlinLogging
import org.telegram.telegrambots.api.methods.send.SendMessage
import java.util.*

class AlarmStategy(val message: MessageFromUser,
                   val messageToSend: SendMessage,
                   val alarmManager: AlarmManager,
                   val sender: MessageHandler) : HandleStrategy {

    private val logger = KotlinLogging.logger {}

    override fun invoke() {
        message as MessageFromUserWithComment
        logger.info { "alarm command: $message" }
        logger.info { "alarm command: ${message.comment}" }

        if (alarmManager.timeIsValid(message.comment)) {
            val timeForAlarm = alarmManager.generateTimeForNextAlarm(message.comment)
            logger.info { "time is correct" }

            alarmManager.setUpAlarmIn(timeForAlarm, message.chatId, !alarmManager.timeIsAfter(message.comment), sender)

            messageToSend.text = AlarmSuccess(timeForAlarm).getText()
            sender.sendMessage(messageToSend)

            logger.info { "time for alarm: ${Date(timeForAlarm)}" }

        } else {
            messageToSend.text = AlarmError(message.userName, message.comment).getText()
            sender.sendMessage(messageToSend)
        }
    }
}