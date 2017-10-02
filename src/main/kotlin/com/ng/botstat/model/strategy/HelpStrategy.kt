package com.ng.botstat.model.strategy

import com.ng.botstat.model.MessageHandlerImpl
import com.ng.botstat.model.answers.AnswerHelp
import org.telegram.telegrambots.api.methods.send.SendMessage

class HelpStrategy(val messageToSend: SendMessage,val messageHandlerImpl: MessageHandlerImpl) : HandleStrategy {

    override fun invoke() {
        messageToSend.text = AnswerHelp().getText()
        messageHandlerImpl.sendMessage(messageToSend)
    }
}