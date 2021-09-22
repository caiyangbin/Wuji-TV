package com.wuji.tv.model
import com.google.gson.annotations.SerializedName


data class DownloadResultModel(
    @SerializedName("exist_ticket")
    val existTicket: String,
    @SerializedName("ticket")
    val ticket: String
)