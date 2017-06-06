package com.ng.botstat.model.answers

import java.util.*

/**
 * Created by NG on 05.06.17.
 */
class AnswerRegister constructor(val name: String = "empty", val userIsExists: Boolean = false) : MessageWithText() {

    override fun getText(): String {
        if (userIsExists) {
            //todo update with parameters
            return name + getString(already)
        } else {
            return String.format(getString(success), name)
        }
    }
}