package com.example.watchly.db

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.watchly.models.Channel
import com.example.watchly.models.Search
import com.example.watchly.models.SubUser
import com.example.watchly.models.UploadVideo
import com.example.watchly.uils.Constants.DB_NAME
import com.example.watchly.uils.Constants.DbVersion

@androidx.room.Database(entities = [SubUser::class, Channel::class, UploadVideo::class, Search::class], version = DbVersion)
abstract class Database : RoomDatabase() {

    abstract fun dbDao() : Dao

    companion object {
        @Volatile
        private var instance: Database? = null

        fun database(context: Context): Database{
            return instance ?: synchronized(this){
                val buildDBInstance = Room.databaseBuilder(
                    context.applicationContext,
                    Database::class.java,
                    DB_NAME
                ).fallbackToDestructiveMigration().build()
                instance = buildDBInstance
                buildDBInstance
            }
        }
    }
}