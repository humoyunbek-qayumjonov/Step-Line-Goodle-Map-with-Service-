package com.example.googlemap.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MyDao {
    @Query("select * from MyLocation")
    fun getAllTime():List<MyLocation>

    @Insert
    fun addTime(myTime:MyLocation)
}