package com.wuji.tv.model

import java.io.Serializable

data class PosterTabDataModel(
    var tabs: ArrayList<PosterTabModel> = arrayListOf(),
    var imgWallPath: String? = null,
    var imgLogoPath: String? = null,
    var updateTime: String? = null
) : Serializable