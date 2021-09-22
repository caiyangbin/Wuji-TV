package com.wuji.tv.model
import com.google.gson.annotations.SerializedName



data class UpdateData(
    @SerializedName("app_download_url")
    val appDownloadUrl: String,
    @SerializedName("descripton_cn")
    val descriptonCn: String,
    @SerializedName("descripton_en")
    val descriptonEn: String,
    @SerializedName("new_version")
    val newVersion: Int,
    @SerializedName("update_type")
    val updateType: Int
)