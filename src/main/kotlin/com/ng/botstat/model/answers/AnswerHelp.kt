package com.ng.botstat.model.answers

/**
 * Created by NG on 05.06.17.
 */
class AnswerHelp : MessageWithText() {
    override fun getText(): String {
        return getString(help)
    }
}