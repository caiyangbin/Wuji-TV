package com.wuji.tv.model

import com.google.gson.annotations.SerializedName



data class UserListResult(
    @SerializedName("groups")
    val groups: List<Group>,
    @SerializedName("users")
    val users: List<UserForList>
)

data class Group(
    @SerializedName("gid")
    val gid: Int,
    @SerializedName("groupname")
    val groupname: String,
    @SerializedName("members")
    val members: List<Int>
)

data class UserForList(
    @SerializedName("admin")
    val admin: Int,
    @SerializedName("gid")
    val gid: Int,
    @SerializedName("uid")
    val uid: Int,
    @SerializedName("username")
    val username: String
)