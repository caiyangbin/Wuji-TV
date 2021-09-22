package com.wuji.tv.model

import com.google.gson.annotations.SerializedName



data class SharedListModel(
    @SerializedName("download_list")
    val downloadList: List<Download>,
    @SerializedName("shared_list")
    val sharedList: List<Shared>
)

data class Download(
    @SerializedName("ticket")
    val ticket: String,
    @SerializedName("ticket_1")
    val ticket1: String,
    @SerializedName("ticket_2")
    val ticket2: String,
    @SerializedName("timestamp")
    val timestamp: Int,
    @SerializedName("to_path")
    val toPath: String,
    @SerializedName("user_id")
    val userId: String,
    var name: String,
    var session: String
)

data class Shared(
    @SerializedName("max_download")
    val maxDownload: Int,
    @SerializedName("password")
    val password: String,
    @SerializedName("path")
    val path: List<String>,
    @SerializedName("remain_download")
    val remainDownload: Int,
    @SerializedName("remain_period")
    val remainPeriod: Int,
    @SerializedName("share_path_type")
    val sharePathType: Int,
    @SerializedName("timestamp")
    val timestamp: Int,
    @SerializedName("to_user_id")
    val toUserId: List<String>,
    @SerializedName("user_id")
    val userId: String
)