package com.example.watchly.use_cases

import com.example.watchly.uils.ReusableResource.uid
import java.lang.StringBuilder
import java.util.*

class VideoId {

    private val separator = "$"
    private val channel = "Video"

    fun generate() : String {
        val sb = StringBuilder()
        val currentMilliSeconds:String = ""+ Calendar.getInstance().timeInMillis
        val randomId = randomID()
        sb.append(currentMilliSeconds)
            .append(separator)
            .append(randomId)
            .append(separator)
            .append(uid())
            .append(separator)
            .append(channel)
        return sb.toString()
    }

    private fun randomID(): String = List(16) {
        (('a'..'z') + ('A'..'Z') + ('0'..'9')).random()
    }.joinToString("")


}