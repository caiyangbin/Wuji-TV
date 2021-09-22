package com.wuji.tv.model

import java.io.Serializable

const val NFO_SET = "set"
const val NFO_TITLE = "title"
const val NFO_SHOW_TITLE = "showtitle"
const val NFO_PREMIERED = "premiered"
const val NFO_YEAR = "year"
const val NFO_RUNTIME = "runtime"
//const val NFO_COUNTRY = "country"
const val NFO_DIRECTOR = "director"
const val NFO_PLOT = "plot"
const val NFO_ORIGINAL_FILENAME = "original_filename"
const val NFO_VIDEO = "video"
const val NFO_RATINGS = "ratings"

data class Files (
    var positionOneTab: Int=-1,//拉数据时，标记下标，快速精准定位，不用再去遍历数组，拉数据线程必须单线程一层一层查
    var positionTwoTab: Int=-1,//拉数据时，标记下标，快速精准定位，不用再去遍历数组，拉数据线程必须单线程一层一层查
    var layoutType: Int=1,
    var type: String="",
    var id: String="",
    var name: String="",
    var path: String="",
    var rootPath: String="",
    var session: String="",
    var sessionLocal: String="",
    var ip: String="",
    var deviceId: String="",
    var uid: Int=0,
    var gid: Int=0,
    var size: Long=0,
    var time: Int=0,
    var ftype: String="",
    var perm: String="",
    var share_path_type: Int=0,
    var path_pic_poster: String="",
    var path_pic_fanart: String="",
    var map:HashMap<String, String> = HashMap(),
    var genre:ArrayList<String> = arrayListOf(),
    var country:ArrayList<String> = arrayListOf(),
    var actor:ArrayList<String> = arrayListOf(),
    var fileList:ArrayList<Files> = arrayListOf(),
    var fanartList:ArrayList<Files> = arrayListOf(),
    var trailerList:ArrayList<Files> = arrayListOf(),
    var sampleList:ArrayList<Files> = arrayListOf()
) : Serializable