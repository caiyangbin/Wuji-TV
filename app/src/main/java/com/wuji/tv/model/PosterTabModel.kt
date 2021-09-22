package com.wuji.tv.model

import java.io.Serializable

data class PosterTabModel(
    var filesAll: ArrayList<Files> = arrayListOf(),
    var files: ArrayList<Files> = arrayListOf(),
    var tabTwo: ArrayList<PosterTabModel> = arrayListOf(),
    var tabTwoPosition: Int = 0,
    var rank: Int = 100000,
    var flag: String = "",
    var lists: String = "",
    var name: String = ""
) : Serializable