package com.ng.botstat.model.answers

/**
 * Created by NG on 06.06.17.
 */
class AnswerErrorRolled(userName: String, currentRolledUser: String) : MessageWithText() {

    private val userName = userName
    private val currentRolledUser = currentRolledUser

    override fun getText(): String {
        return String.format(getString(already_rolled), userName, currentRolledUser)
    }
}