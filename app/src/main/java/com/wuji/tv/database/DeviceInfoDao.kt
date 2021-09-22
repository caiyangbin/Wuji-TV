package com.wuji.tv.database

import androidx.room.*
import com.wuji.tv.model.DeviceInfoModel


@Dao
interface DeviceInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(device: DeviceInfoModel)

    @Query("SELECT * FROM device_info WHERE device_id = :deviceId")
    fun getDeviceInfo(deviceId: String): DeviceInfoModel?

    @Query("SELECT * FROM device_info WHERE network_id = :networkId")
    fun getDeviceInfoNetworkId(networkId: String): DeviceInfoModel?

    @Query("DELETE FROM device_info WHERE device_id = :deviceId")
    fun delete(deviceId: String)
}