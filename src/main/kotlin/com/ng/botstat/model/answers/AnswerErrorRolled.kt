package com.ng.botstat.model.answers

/**
 * Created by NG on 06.06.17.
 */
class AnswerErrorRolled(userName: String) : MessageWithText() {

    private var userName = userName

    override fun getText(): String {
        return String.format(getString(already_rolled), userName)
    }
}