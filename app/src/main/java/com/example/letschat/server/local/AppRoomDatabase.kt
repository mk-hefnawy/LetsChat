package com.example.letschat.server.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.letschat.auth.server.local.user.AuthDao
import com.example.letschat.chatroom.chat.ChatRoom
import com.example.letschat.chatroom.data.local.ChatRoomDao
import com.example.letschat.home.server.local.ChatsDao
import com.example.letschat.user.User

@Database(entities = [User::class, ChatRoom::class], version = 3)
abstract class AppRoomDatabase : RoomDatabase() {

    abstract fun authDao(): AuthDao
    abstract fun chatRoomDao(): ChatRoomDao
    abstract fun chatsDao(): ChatsDao

    companion object {
        @Volatile
        private var instance: AppRoomDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            AppRoomDatabase::class.java,
            "AppRoomDatabase"
        ).fallbackToDestructiveMigration() // will clear the db when version updated
            .build()
    }
}