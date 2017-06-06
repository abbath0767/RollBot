package com.ng.botstat

import com.ng.botstat.bot.SimpleBot
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.TelegramBotsApi
import java.io.FileInputStream
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseCredentials
import com.google.firebase.FirebaseOptions
import com.google.firebase.database.FirebaseDatabase
import com.ng.botstat.db.FirebaseRepository
import com.ng.botstat.model.DBUser


/**
 * Created by NG on 02.06.17.
 */

fun main(args: Array<String>) {

    initDb()

    initializeBot()
}

//todo first build and after init!
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
