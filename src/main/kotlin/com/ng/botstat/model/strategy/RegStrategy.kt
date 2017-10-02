package com.ng.botstat.model.strategy

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.ng.botstat.db.Repository
import com.ng.botstat.model.MessageFromUser
import com.ng.botstat.model.MessageHandler
import com.ng.botstat.model.answers.AnswerRegister
import org.telegram.telegrambots.api.methods.send.SendMessage

class RegStrategy(val repo: Repository, val message: MessageFromUser, val messageToSend: SendMessage, val sender: MessageHandler) : HandleStrategy {

    override fun invoke() {
        repo.getUser(message.chatId, message.userName, object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot?) {

                if (snapshot?.exists() == false) {
                    repo.registerPlayer(message.userName, message.chatId)

                    messageToSend.text = AnswerRegister(message.userName, false).getText()
                } else {
                    messageToSend.text = AnswerRegister(message.userName, true).getText()
                }
                sender.sendMessage(messageToSend)
            }

            override fun onCancelled(error: DatabaseError?) {
                messageToSend.text = "error: $error"
                sender.sendMessage(messageToSend)
            }
        })
    }
}