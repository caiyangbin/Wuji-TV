package com.wuji.tv.model


class DownloadParam(
    var session: String? = null,
    var file: MyFile? = null,
    var downloadPath: String? = null,
    var sharePathType: Int = 0
)