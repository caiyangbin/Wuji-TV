package com.wuji.tv.model
import com.google.gson.annotations.SerializedName


data class CreateResultModel(
    @SerializedName("ticket_1")
    val ticket1: String,
    @SerializedName("ticket_2")
    val ticket2: String
)