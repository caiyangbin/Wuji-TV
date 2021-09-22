package com.wuji.tv.model
import com.google.gson.annotations.SerializedName

data class AuthResult(
    @SerializedName("session")
    val session: String
)
