package com.wuji.tv.model

data class BaseDownloadDbResult<T> (
    var result:Int,
    var msg:String,
    var data: T
)