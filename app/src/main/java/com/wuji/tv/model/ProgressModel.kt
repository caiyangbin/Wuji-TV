package com.wuji.tv.model

import com.google.gson.annotations.SerializedName



data class ProgressModel(
    @SerializedName("current_files")
    val currentFiles: List<CurrentFile>,
    @SerializedName("current_size",alternate = ["download_len"])
    val currentSize: Long,
    @SerializedName("download_path_num")
    val downloadPathNum: Int,
    @SerializedName("err")
    val err: Int,
    @SerializedName("err_files_num")
    val errFilesNum: Int,
    @SerializedName("speed")
    val speed: Long,
    @SerializedName("status")
    val status: Int,
    @SerializedName("total_size",alternate = ["total_len"])
    val totalSize: Long,
    @SerializedName("ticket",alternate = ["dl_ticket"])
    var ticket: String
)

data class CurrentFile(
    @SerializedName("current_size")
    val currentSize: Long,
    @SerializedName("path")
    val path: String,
    @SerializedName("total_size")
    val totalSize: Long
)

data class ProgressDbModel(val progress: List<ProgressModel>)