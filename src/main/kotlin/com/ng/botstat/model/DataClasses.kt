package com.ng.botstat.model

import com.ng.botstat.util.Command

/**
 * Created by NG on 02.06.17.
 */

data class User(val name: String, val messageCount: Int, val floodRatio: Int)

data class DBUser(val cahtId: Long = -1L, val pidorCount: Int = -1, val userName: String = "emptyName") {
    fun incrementRoll(): DBUser {
        return DBUser(this.cahtId, this.pidorCount.inc(), this.userName)
    }
}

open class MessageFromUser(val type: Command, val chatId: Long, val userName: String) {
    override fun toString(): String {
        return "MessageFromUser(type=$type, chatId=$chatId, userName='$userName')"
    }
}

open class MessageFromUserWithComment(type: Command, chatId: Long, userName: String, val comment: String)
    : MessageFromUser(type, chatId, userName) {
    override fun toString(): String {
        return "MessageFromUserWithComment(comment='$comment'), ${super.toString()}"
    }
}

open class MessageToRoll(type: Command, chatId: Long) : MessageFromUser(type, chatId, "bot")