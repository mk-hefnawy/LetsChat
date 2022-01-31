package com.example.letschat.server.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class Converters {

    @TypeConverter
    fun fromStringListToString(listOfStrings: List<String>):String{
        val type: Type = object : TypeToken<List<String>>() {}.type
        return Gson().toJson(listOfStrings, type)
    }

    @TypeConverter
    fun fromStringToStringList(string: String): List<String>{
        val type: Type = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(string, type)
    }
}