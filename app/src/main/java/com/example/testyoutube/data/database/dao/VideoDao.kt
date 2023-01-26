package com.example.testyoutube.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.testyoutube.data.database.entity.ItemVideo

@Dao
interface VideoDao {
    @Query("SELECT * FROM ItemVideo")
    suspend fun getVideoList(): List<ItemVideo>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(item: ItemVideo): Long

    @Query("DELETE FROM ItemVideo")
    suspend fun deleteAll()
}