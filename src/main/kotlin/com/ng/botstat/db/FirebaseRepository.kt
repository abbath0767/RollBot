package com.ng.botstat.db

import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseCredentials
import com.google.firebase.database.*
import com.ng.botstat.model.DBUser
import com.ng.botstat.util.BotParams
import com.ng.botstat.util.DBTable
import mu.KotlinLogging
import java.io.FileInputStream

/**
 * Created by NG on 05.06.17.
 */
class FirebaseRepository private constructor(): Repository {

    private val logger = KotlinLogging.logger {}

    private object Holder {
        val INSTANCE = FirebaseRepository()
    }
    companion object {
        val instance : FirebaseRepository by lazy {Holder.INSTANCE}
    }

    val database : FirebaseDatabase

    init {
        var serviceAccount =  FileInputStream(BotParams.KEY_PATH.value)

        val options = FirebaseOptions.Builder()
                .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
                .setDatabaseUrl(BotParams.DATABASE_URL.value)
                .build()

        FirebaseApp.initializeApp(options)

        database = FirebaseDatabase.getInstance()
    }

    override fun registerPlayer(userName: String, chatId: Long) {
        val usersRef = database.getReference(DBTable.CHATS.tableName)

        usersRef
                .child(chatId.toString())
                .child(userName)
                .setValue(DBUser(chatId, 0, userName))
    }

    override fun getUser(chatId: Long, userName: String, listener: ValueEventListener){
        val userRef = database.getReference(DBTable.CHATS.tableName)

        userRef
                .child(chatId.toString())
                .child(userName)
                .addListenerForSingleValueEvent(listener)
    }

    override fun getUsers(chatId: Long, listener: ValueEventListener) {
        val usersRef = database.getReference(DBTable.CHATS.tableName)

        usersRef
                .child(chatId.toString())
                .addListenerForSingleValueEvent(listener)

    }

    override fun updateUser(newUser: DBUser) {
        val usersRef = database.getReference(DBTable.CHATS.tableName)

        val userMap = mapOf(newUser.userName to newUser)

        logger.info { "userMap to update: $userMap" }

        usersRef
                .child(newUser.cahtId.toString())
                .updateChildren(userMap)
    }

    override fun checkDate(chatId: Long, listener: ValueEventListener) {
        val userRef = database.getReference(DBTable.CHATS.tableName)

        userRef
                .child(chatId.toString())
                .child(DBTable.LAST_ROLL.tableName)
                .addListenerForSingleValueEvent(listener)
    }

    override fun updateRollDate(chatId: Long, time: Long) {
        val userRef = database.getReference(DBTable.CHATS.tableName)

        val timeMap = mapOf(DBTable.LAST_ROLL.tableName to time)

        logger.info { "update roll data: $timeMap" }

        userRef
                .child(chatId.toString())
                .updateChildren(timeMap)
    }

    override fun getCurrentRolledUser(chatId: Long, valueEventListener: ValueEventListener) {
        val userRef = database.getReference(DBTable.CHATS.tableName)

        userRef
                .child(chatId.toString())
                .child(DBTable.LAST_USER_ROLL.tableName)
                .addListenerForSingleValueEvent(valueEventListener)
    }

    override fun saveRolledUserName(chatId: Long, userName: String) {
        val userRef = database.getReference(DBTable.CHATS.tableName)

        val rollUserMap = mapOf(DBTable.LAST_USER_ROLL.tableName to userName)

        logger.info { "update last roll user: $userName" }

        userRef
                .child(chatId.toString())
                .updateChildren(rollUserMap)
    }
}