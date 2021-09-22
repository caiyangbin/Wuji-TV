package com.wuji.tv.model

import androidx.room.*


@Entity(tableName = "device_info", indices = [Index(value = ["device_id"], unique = true)])
@TypeConverters(MediaConverters::class)
data class DeviceInfoModel(
    // 当前导航对应的文件夹类别
    @ColumnInfo(name = "device_id")
    val deviceId: String,
    // 圈子网络id
    @ColumnInfo(name = "network_id")
    var networkId: String = "",
    @ColumnInfo(name = "vip")
    var vip: String = "",
    @ColumnInfo(name = "user_id")
    var userid: String = "",
    @ColumnInfo(name = "dev_class")
    var devClass: Int = 0,
    @ColumnInfo(name = "is_selectable")
    var isSelectable: Boolean = false,
    @ColumnInfo(name = "sdvn_name")
    var sdvnName: String = "",
    // 设备名称
    @ColumnInfo(name = "name")
    var name: String = "",
    // 简介
    @ColumnInfo(name = "plot")
    var plot: String = "",
    // 流量单价
    @ColumnInfo(name = "price")
    var price: String = "",
    // 跟新时间
    @ColumnInfo(name = "updateTime")
    var updateTime: String = "",
    // 跟新时间
    @ColumnInfo(name = "type")
    var type: String = "",
    //
    @ColumnInfo(name = "movie_poster_bg")
    var movie_poster_bg: String = "",
    //
    @ColumnInfo(name = "movie_poster_cover")
    var movie_poster_cover: String = "",
    //
    @ColumnInfo(name = "movie_poster_logo")
    var movie_poster_logo: String = "",
    //
    @ColumnInfo(name = "movie_poster_wall")
    var movie_poster_wall: String = ""
) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var uid: Long = 0
}

/*
import java.io.Serializable

data class DeviceInfoModel(
    val name: String,
    val plot: String,
    val updateTime: String,
    val type: String,
    val movie_poster_bg: String,
    val movie_poster_cover: String,
    val movie_poster_logo: String,
    val hasPoster: Boolean
) : Serializable*/
