package com.wuji.tv.model
import com.google.gson.annotations.SerializedName



data class CompelateDownloadModel(
    @SerializedName("page")
    val page: Int,
    @SerializedName("path")
    val path: List<Path>,
    @SerializedName("total")
    val total: Long
)

data class Path(
    @SerializedName("isDir")
    val isDir: Boolean,
    @SerializedName("path")
    val path: String,
    @SerializedName("size")
    val size: Long
)