package com.wuji.tv.model
import com.google.gson.annotations.SerializedName


data class VersionResult(
  @SerializedName("context")
  val context: Version
)
data class Version(
    @SerializedName("version")
    val version: Int
)