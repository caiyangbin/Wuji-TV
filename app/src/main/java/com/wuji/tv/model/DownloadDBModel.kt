package com.wuji.tv.model

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "table_download_list")
data class DownloadDBModel(
    @PrimaryKey @ColumnInfo(name = "ticket") @NonNull var ticket: String,
    @ColumnInfo(name = "name") @NonNull var name: String
)