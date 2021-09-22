package com.wuji.tv.model

import io.sdvn.socket.data.SDVNDevice


data class RemoteDeviceModel(
    var title: String,
    var isTitle: Boolean = false,
    var sdvnDevice: SDVNDevice? = null,
    var sdvnNetworkId: String? = null,
    var session: String = "",
    var isGetData: Boolean = false,
    var isNewData: Boolean = true,
    var hasPoster: Boolean = true,
    var movie_poster_bg: String = "",
    var movie_poster_logo: String = "",
    var movie_poster_cover: String = "",
    var movie_poster_in: String = "",
    var name: String = "",
    var plot: String = "",
    var type: String = "",
    var price: String = "",
    var newNumber: String = "",
    var updateTime: String = "",
    var managerResId: Int = 0
)