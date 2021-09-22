package com.wuji.tv.model


const val REQUEST_ERROR = 0x00
const val DATA_IS_NULL = 0x01
const val FILE_IS_NULL = 0x02

data class ErrorResult(
    val code:Int,
    val msg:String
)