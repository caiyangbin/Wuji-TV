package com.wuji.tv.model
import com.google.gson.annotations.SerializedName

data class AccessResult(
    @SerializedName("session")
    val session: String,
    @SerializedName("user")
    val user: User
)

data class User(
    @SerializedName("admin")
    val admin: Int,
    @SerializedName("gid")
    val gid: Int,
    @SerializedName("uid")
    val uid: Int,
    @SerializedName("username")
    val username: String
)