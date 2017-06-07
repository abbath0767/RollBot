package com.ng.botstat.model.answers

import com.ng.botstat.model.DBUser
import mu.KotlinLogging

/**
 * Created by NG on 07.06.17.
 */
class AnswerStat(users: List<DBUser>) : MessageWithText() {
    private val logger = KotlinLogging.logger {}

    val users = users

    override fun getText(): String {
        val sortedUsers = calculateAndGetUsers()
        val text = StringBuilder(String.format(getString(top) + "\n", sortedUsers.size.toString()))

        sortedUsers.forEachIndexed({ index, dbUser ->
            text.append(String.format(getString(forEach), index + 1, dbUser.userName, dbUser.pidorCount.toString()))
        })

        text.append(String.format(getString(over), sortedUsers.size.toString()))

        return text.toString()
    }

    private fun calculateAndGetUsers(): List<DBUser> = users.sortedWith(compareBy({ -it.pidorCount })).subList(0, users.size)
}