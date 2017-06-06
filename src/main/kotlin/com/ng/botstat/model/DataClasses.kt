package com.ng.botstat.model

import com.ng.botstat.util.Command

/**
 * Created by NG on 02.06.17.
 */

data class User(val name: String, val messageCount: Int, val floodRatio: Int)

data class DBUser(val cahtId: Long = -1L, val pidorCount: Int = -1,  val userName: String = "emptyName") {
    fun incrementRoll(): DBUser {
        return DBUser(this.cahtId, this.pidorCount.inc(), this.userName)
    }
}

data class MessageFromUser(val type: Command, val chatId: Long, val userName: String)