package com.wuji.tv.model


data class LocalFile(
    val name: String,
    val path: String,
    var isTitle: Boolean = false,
    var total: String? = "",
    var available: String? = "",
    var type: String? = "",
    var time: String? = "",
    var fileSize: String? = "",
    var fileCount: Int = 0
)