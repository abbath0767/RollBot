package com.ng.botstat.model.answers

/**
 * Created by NG on 06.06.17.
 */
class AnswerErrorRolled(val userName: String,val  currentRolledUser: String) : MessageWithText() {
    override fun getText(): String {
        return String.format(getString(already_rolled), userName, currentRolledUser)
    }
}