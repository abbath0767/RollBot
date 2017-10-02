package com.ng.botstat.model.answers

import java.util.*

/**
 * Created by NG on 05.06.17.
 */
abstract class MessageWithText {

    internal var bundle =
            ResourceBundle.getBundle("strings", Locale("ru", "RU"))

    val already = "ALREADY_REGISTER"
    val success = "REGISTER_CUSSES"
    val help = "help"
    val unknown = "UNKNOWN"
    val already_rolled = "ALREADY_ROLLED"
    val top = "TOP"
    val forEach = "FOR_EACH"
    val over = "OVER"
    val alarmError = "ALARM_ERROR"
    val alarmSuccess = "ALARM_SUCCESS"

    abstract fun getText(): String

    open fun getString(prefName: String) =
            String(bundle.getString(prefName).toByteArray(charset("ISO-8859-1")), charset("UTF-8"))
}