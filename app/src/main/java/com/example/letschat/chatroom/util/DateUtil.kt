package com.example.letschat.chatroom.util

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.*

object DateUtil {

    fun dateToString(date: Date, format: String): String{
        val dateFormatter =  SimpleDateFormat(format)
        return dateFormatter.format(date)
    }

    fun isYesterday(date: Date): Boolean{
        return DateUtils.isToday(date.time + DateUtils.DAY_IN_MILLIS)
    }
}