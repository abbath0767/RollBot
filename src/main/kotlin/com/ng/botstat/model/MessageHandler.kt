package com.ng.botstat.model

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.ng.botstat.bot.MessageSender
import com.ng.botstat.db.Repository
import com.ng.botstat.model.answers.*
import com.ng.botstat.util.Command
import com.ng.botstat.util.DBTable
import mu.KotlinLogging
import org.telegram.telegrambots.api.methods.send.SendMessage
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.Nullable

/**
 * Created by NG on 05.06.17.
 */
class MessageHandler private constructor(bot: MessageSender, repo: Repository) {

    private val repo = repo
    private val bot = bot

    private val logger = KotlinLogging.logger {}

    companion object {
        private var instance: MessageHandler? = null

        fun getInstence(bot: MessageSender, repo: Repository): MessageHandler {
            if (instance == null)
                instance = MessageHandler(bot, repo)

            return instance!!
        }
    }

    fun handleMessage(message: MessageFromUser) {
        val messageToSend = SendMessage()
        messageToSend.setChatId(message.chatId)

        logger.info { "handle message: $message" }

        when (message.type) {
            Command.HELP -> {
                messageToSend.text = AnswerHelp().getText()
                messageToSend.send()
            }

            Command.REG -> {
                repo.getUser(message.chatId, message.userName, object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot?) {

                        if (snapshot?.exists() == false) {
                            repo.registerPlayer(message.userName, message.chatId)

                            messageToSend.text = AnswerRegister(message.userName, false).getText()
                        } else {
                            messageToSend.text = AnswerRegister(message.userName, true).getText()
                        }
                        messageToSend.send()
                    }

                    override fun onCancelled(error: DatabaseError?) {
                        messageToSend.text = "error: $error"
                        messageToSend.send()
                    }
                })
            }

            Command.ROLL -> {

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
                                    messageToSend.text = AnswerErrorRolled(message.userName).getText()
                                    messageToSend.send()
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
                        messageToSend.send()
                        return
                    }

                })
            }

            Command.STAT -> {
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

            else -> {
                messageToSend.text = AnswerUnknown().getText()
                messageToSend.send()
            }
        }
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

                publishRollResult(messageToSend, rolledUser.userName)
            }

            override fun onCancelled(error: DatabaseError?) {
                logger.info { "error: $error" }
                messageToSend.text = "error: $error"
                messageToSend.send()
            }

        })
    }

    private fun publishRollResult(messageToSend: SendMessage, userName: String) {
        logger.info { "publish result for $userName" }

        val answerRolled = AnswerRolled(userName)

        for (i in 0..answerRolled.size()) {
            messageToSend.text = answerRolled.getPart(i)
            messageToSend.send()

            Thread.sleep(1500)
        }
    }

    private fun getUsers(snapshot: DataSnapshot?): List<DBUser> {
        val users = mutableListOf<DBUser>()

        if (snapshot != null) {
            snapshot.children.forEach {
                if (it.key != DBTable.LAST_ROLL.tableName) {
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

    private fun rolled(size: Int): Int {
        val rand = Random()

        return rand.nextInt(size)
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

    private fun calculateAndSendTop(users: List<DBUser>, messageToSend: SendMessage) {
        logger.info { "calculateAndSendTop" }

        messageToSend.text = AnswerStat(users).getText()
        messageToSend.send()
    }

    private fun SendMessage.send() {
        bot.sendMessageMsg(this)
    }
}
