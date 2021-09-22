package com.wuji.tv.model

data class FilesData(
    var total: Int = 0,
    var page: Int = 0,
    var pages: Int = 0,
    var files: ArrayList<Files> = arrayListOf(),
    var session: String = "",
    var ip: String = "",
    var path: String = "",
    var isAll: Boolean = false
)