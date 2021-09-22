package com.wuji.tv.model

data class BaseDownloadResult<T> (
    var status:Int,
    var msg:String,
    var result: T
)