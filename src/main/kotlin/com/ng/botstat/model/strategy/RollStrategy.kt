package com.ng.botstat.model.strategy

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.ng.botstat.db.Repository
import com.ng.botstat.model.DBUser
import com.ng.botstat.model.MessageFromUser
import com.ng.botstat.model.MessageHandler
import com.ng.botstat.model.answers.AnswerErrorRolled
import com.ng.botstat.model.answers.AnswerRolled
import com.ng.botstat.util.DBTable
import mu.KotlinLogging
import org.telegram.telegrambots.api.methods.send.SendMessage
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class RollStrategy(val repo: Repository,
                   val message: MessageFromUser,
                   val messageToSend: SendMessage,
                   val sender: MessageHandler) : HandleStrategy {

    private val logger = KotlinLogging.logger {}

    override fun invoke() {
        repo.checkDate(message.chatId, object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot?) {

                if (snapshot?.exists() == false) {
                    logger.info { "onDataChange. not exists! $snapshot" }

                    repo.updateRollDate(message.chatId, Date().time)

                    rolling(message, messageToSend)

                } else {
                    logger.info { "onDataChange. exists. $snapshot" }

                    if (snapshot != null) {
                        val timeStamp = snapshot.getValue(Long::class.java)
                        val currentTimeStamp = Date().time
                        val differenceMs = currentTimeStamp - timeStamp
                        val days = TimeUnit.DAYS.convert(differenceMs, TimeUnit.MILLISECONDS)

                        val lastRoll = SimpleDateFormat("yyyy-MM-dd  HH-mm-ss").format(Date(timeStamp))
                        logger.info { "difference: $days\nlastRoll: $lastRoll" }

                        if (days == 0L) {

                            repo.getCurrentRolledUser(message.chatId, object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot?) {
                                    var currentRolledUser = "currentRolledUser. empty value"

                                    if (snapshot?.exists() == false) {
                                        logger.info { "not exists currentRollUser" }
                                    } else {
                                        currentRolledUser = snapshot?.getValue(String::class.java) ?: "snapshot is null"
                                    }

                                    messageToSend.text = AnswerErrorRolled(message.userName, currentRolledUser).getText()

                                    sender.sendMessage(messageToSend)
                                }

                                override fun onCancelled(error: DatabaseError?) {
                                    messageToSend.text = error.toString()
                                    sender.sendMessage(messageToSend)
                                }

                            })
                            return
                        } else {
                            logger.info { "update roll date" }
                            repo.updateRollDate(message.chatId, Date().time)

                            rolling(message, messageToSend)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError?) {
                logger.info { "error: $error" }
                messageToSend.text = "error $error"
                sender.sendMessage(messageToSend)
                return
            }

        })
    }

    private fun rolling(message: MessageFromUser, messageToSend: SendMessage) {
        repo.getUsers(message.chatId, object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot?) {
                logger.info { "rolling" }

                val users = getUsers(snapshot)

                val roll = rolled(users.size)
                logger.info { "rolled count: $roll" }

                val rolledUser = users[roll]
                logger.info { "rolled user: $rolledUser" }

                incrementRolled(rolledUser)

                saveCurrentRollUser(message.chatId, rolledUser)

                publishRollResult(messageToSend, rolledUser.userName)
            }

            override fun onCancelled(error: DatabaseError?) {
                logger.info { "error: $error" }
                messageToSend.text = "error: $error"
                sender.sendMessage(messageToSend)
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

    private fun incrementRolled(rolledUser: DBUser) {
        repo.getUser(rolledUser.cahtId, rolledUser.userName, object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot?) {
                logger.info { "increment rolled. snapshot: $snapshot" }
                if (snapshot != null) {
                    val oldUser = snapshot.getValue(DBUser::class.java)
                    val newUser = oldUser.incrementRoll()

                    logger.info { "new user: $newUser" }

                    repo.updateUser(newUser)
                }
            }

            override fun onCancelled(error: DatabaseError?) {
                logger.info { "error: $error" }
            }
        })
    }

    private fun publishRollResult(messageToSend: SendMessage, userName: String) {
        logger.info { "publish result for $userName" }

        val answerRolled = AnswerRolled(userName)

        for (i in 0..answerRolled.size()) {
            messageToSend.text = answerRolled.getPart(i)

            sender.sendMessage(messageToSend)

            Thread.sleep(1500)
        }
    }

    private fun saveCurrentRollUser(chatId: Long, rolledUser: DBUser) =
        repo.saveRolledUserName(chatId, rolledUser.userName)

    private fun rolled(size: Int): Int = Random().nextInt(size)
}