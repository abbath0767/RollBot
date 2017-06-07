package com.ng.botstat.model.answers

import mu.KotlinLogging
import java.util.*

/**
 * Created by NG on 06.06.17.
 */
class AnswerRolled(userName: String) {

    private val logger = KotlinLogging.logger {}

    private val bundle: ResourceBundle
    private var rand = Random()
    private val messageArray: MutableList<String> = mutableListOf()
    private val answerName = "ANSWER_"
    private var userName = userName
    val rollMessageId: Int

    init {
        //todo need another way to get count of properties files
        rollMessageId = rand.nextInt(5)
        logger.info { "roll : $rollMessageId" }
        bundle = ResourceBundle.getBundle("rolled_answer_" + rollMessageId.toString(), Locale("ru", "RU"))
        initText()

        logger.info { "username: $userName, rollMessage: $rollMessageId, " }
    }

    private fun initText() {
        (1..4).mapTo(messageArray) {
            if (it == 4) {
                String.format(getString(answerName + it.toString()), userName)
            } else {
                getString(answerName + it.toString())
            }
        }
        logger.info { "message: ${messageArray.forEach({ logger.info { it } })}" }
    }

    private fun getString(prefName: String): String {
        return String(bundle.getString(prefName).toByteArray(charset("ISO-8859-1")), charset("UTF-8"))
    }

    fun size(): Int = messageArray.size

    fun getPart(position: Int): String = messageArray[position]
}