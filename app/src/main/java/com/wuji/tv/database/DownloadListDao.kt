package com.wuji.tv.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wuji.tv.model.DownloadDBModel

@Dao
interface DownloadListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(downloadDBModel: DownloadDBModel): Long

    @Query("SELECT * FROM table_download_list")
    fun getDownloadList(): List<DownloadDBModel>

    @Query("SELECT * FROM table_download_list WHERE ticket = :ticket")
    fun queryForTicket(ticket: String): DownloadDBModel
}