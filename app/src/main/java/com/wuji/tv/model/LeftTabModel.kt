package com.wuji.tv.model

import androidx.room.*

@Entity(tableName = "left_tabs", indices = [Index(value = ["unique_index"], unique = true)])
@TypeConverters(MediaConverters::class)
data class LeftTabModel(
    @ColumnInfo(name = "folder_type")
    val folderType: Int,
    @ColumnInfo(name = "index")
    val index: Int,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "movie_type_filter")
    val movieTypeFilter: String? = null,
    @ColumnInfo(name = "movie_condition_filter")
    val movieConditionFilter: ArrayList<String>? = null,
    @ColumnInfo(name = "folder_movie_list")
    val folderMovieList: ArrayList<String>? = null,
    @ColumnInfo(name = "unique_index")
    var topNameIndexAndLeftIndex: String? = "",
    @ColumnInfo(name = "device_id")
    var deviceId: String? = null

) {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    var uid: Long = 0

    var leftTopId: String = ""

    @Ignore
    var posterData: ArrayList<MediaInfoModel>? = null
}
