package com.example.googlemap.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [MyLocation::class], version = 2)
abstract class MyDatabase:RoomDatabase() {
    abstract fun myDao():MyDao

    companion object{
        private var instance:MyDatabase? = null

        @Synchronized
        fun getInstance(context:Context):MyDatabase{
            if (instance == null){
                instance = Room.databaseBuilder(context,MyDatabase::class.java,"map_db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }
    }
}