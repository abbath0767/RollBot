package com.ng.botstat.db

import com.google.firebase.database.ValueEventListener
import com.ng.botstat.model.DBUser

/**
 * Created by NG on 05.06.17.
 */
interface Repository {
    fun registerPlayer(userName: String, chatId: Long)
    fun getUser(chatId: Long, userName: String, listener: ValueEventListener)
    fun getUsers(chatId: Long, listener: ValueEventListener)
    fun updateUser(newUser: DBUser)
    fun checkDate(chatId: Long, listener: ValueEventListener)
    fun updateRollDate(chatId: Long, time: Long)
    fun getCurrentRolledUser(chatId: Long, valueEventListener: ValueEventListener)
    fun saveRolledUserName(chatId: Long, userName: String)
}