package com.example.googlemap.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class MyLocation {

    @PrimaryKey(autoGenerate = true)
    var id:Int? = null
    var latitude:Double? = null
    var longtitude:Double? = null
    var time:String? = null

}