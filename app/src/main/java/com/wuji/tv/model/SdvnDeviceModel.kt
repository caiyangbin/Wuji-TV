package com.wuji.tv.model

import io.sdvn.socket.data.SDVNDevice


data class SdvnDeviceModel(
    var sdvnDevice: SDVNDevice?,
    var isTitle: Boolean = false,
    var title: String = ""
)