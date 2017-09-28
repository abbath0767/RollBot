package com.ng.botstat

import com.ng.botstat.bot.SimpleBot
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import com.ng.botstat.db.FirebaseRepository


/**
 * Created by NG on 02.06.17.
 */

fun main(args: Array<String>) {

    initDb()

    initializeBot()
}

fun initDb() {
    FirebaseRepository.instance.database
}

fun initializeBot() {
    ApiContextInitializer.init()
    val telegramApi = TelegramBotsApi()

    try {
        telegramApi.registerBot(SimpleBot())
    } catch(e: Exception) {
    }
}
