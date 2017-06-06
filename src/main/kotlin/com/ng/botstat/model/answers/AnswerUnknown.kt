package com.ng.botstat.model.answers

/**
 * Created by NG on 05.06.17.
 */
class AnswerUnknown : MessageWithText() {
    override fun getText(): String {
        return getString(unknown)
    }
}