package com.wuji.tv.model


data class BaseResult<T>(
    val result: Boolean,
    val error: Error?,
    val data: T?
)

data class Error(
    val code: Int,
    val msg: String
)