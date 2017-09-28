package com.ng.botstat.util

class AlarmManager {

    fun timeIsValid(comment: String) : Boolean {
        try{
            comment.split(":")[0]
            comment.split(":")[1]
            return true
        } catch (e: Exception) {
            return false
        }
    }
}