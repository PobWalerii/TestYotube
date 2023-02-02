package com.example.testyoutube.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.testyoutube.data.database.dao.VideoDao
import com.example.testyoutube.data.database.entity.ItemVideo

@Database(entities = [ItemVideo::class], version = 3, exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun videoDao(): VideoDao
}