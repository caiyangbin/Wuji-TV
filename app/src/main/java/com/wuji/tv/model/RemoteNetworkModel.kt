package com.wuji.tv.model

import io.sdvn.socket.data.SDVNDevice
import io.sdvn.socket.data.SDVNNetwork

data class RemoteNetworkModel(
    var title: String,
    var isTitle: Boolean = false,
    var sdvnNetwork: SDVNNetwork? = null,
    var sdvnDevice: SDVNDevice? = null,
    var session: String = "",
    var isGetData: Boolean = false,
    var movie_poster_bg: String = "",
    var movie_poster_logo: String = "",
    var movie_poster_cover: String = "",
    var movie_poster_in: String = "",
    var name: String = "",
    var plot: String = "",
    var type: String = "",
    var price: String = "",
    var newNumber: String = ""
)