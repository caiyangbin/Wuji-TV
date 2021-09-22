package com.wuji.tv.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wuji.tv.model.LeftTabModel


@Dao
interface LeftTabDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(list: List<LeftTabModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg tabs: LeftTabModel)

    @Query("SELECT * FROM left_tabs")
    fun getAll(): List<LeftTabModel>

    @Query("DELETE FROM left_tabs WHERE device_id = :deviceId")
    fun delete(deviceId: String)
}