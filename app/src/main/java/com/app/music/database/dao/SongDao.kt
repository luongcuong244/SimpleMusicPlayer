package com.app.music.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.app.music.database.entity.SongModel

@Dao
interface SongDao {
    @Query("SELECT * FROM SongModel")
    fun getAll(): List<SongModel>

    fun getFavorite(): List<SongModel> {
        return getAll().filter { it.isFavourite }
    }

    @Update
    fun update(song: SongModel)

    @Insert
    fun insert(song: SongModel)

    @Delete
    fun delete(song: SongModel)
}