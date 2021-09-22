package com.wuji.tv.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.wuji.tv.model.TopWithLeftTabModel


@Dao
interface TopWithLeftTabDao {
    @Transaction
    @Query("SELECT * FROM top_tabs")
    fun geTopWithLeftTabs(): List<TopWithLeftTabModel>

    @Transaction
    @Query("SELECT * FROM top_tabs WHERE device_id = :deviceId")
    fun getTopWithLeftTabsWithDeviceId(deviceId: String): List<TopWithLeftTabModel>
}