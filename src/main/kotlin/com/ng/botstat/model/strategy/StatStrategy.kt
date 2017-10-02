package com.ng.botstat.model.strategy

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.ng.botstat.db.Repository
import com.ng.botstat.model.DBUser
import com.ng.botstat.model.MessageFromUser
import com.ng.botstat.model.MessageHandler
import com.ng.botstat.model.MessageHandlerImpl
import com.ng.botstat.model.answers.AnswerStat
import com.ng.botstat.util.DBTable
import mu.KotlinLogging
import org.telegram.telegrambots.api.methods.send.SendMessage

class StatStrategy(val repo: Repository,
                   val message: MessageFromUser,
                   val messageToSend: SendMessage,
                   val sender: MessageHandler) : HandleStrategy {

    private val logger = KotlinLogging.logger {}

    override fun invoke() {
        repo.getUsers(message.chatId, object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot?) {
                val users = getUsers(snapshot)
                logger.info { "users: $users" }

                calculateAndSendTop(users, messageToSend)
            }

            override fun onCancelled(error: DatabaseError?) {
            }
        })
    }

    private fun getUsers(snapshot: DataSnapshot?): List<DBUser> {
        val users = mutableListOf<DBUser>()

        if (snapshot != null) {
            snapshot.children.forEach {
                logger.info { "children key: ${it.key}" }
                if (it.key != DBTable.LAST_ROLL.tableName && it.key != DBTable.LAST_USER_ROLL.tableName) {
                    val user = it.getValue(DBUser::class.java)
                    logger.info { "add user $user" }
                    users.add(user)
                }
            }
        }

        logger.info { "users:" }
        users.forEach { logger.info { "$it" } }

        return users
    }

    private fun calculateAndSendTop(users: List<DBUser>, messageToSend: SendMessage) {
        logger.info { "calculateAndSendTop" }

        messageToSend.text = AnswerStat(users).getText()
        sender.sendMessage(messageToSend)
    }
}