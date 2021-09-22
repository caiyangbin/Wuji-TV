package com.wuji.tv.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import java.net.URLEncoder

fun play(session: String, ip: String, path: String, name: String, context: Context){
    val encode = URLEncoder.encode(path, "utf-8")
    val filePath = "http://${ip}/file/download?session=${session}&path=${encode}"
    Intent().apply {
        putExtra("video.play.nocontro", false)
        putExtra("video.vidonme.play.file",filePath)
        putExtra("video.vidonme.original.play.file", filePath)
        putExtra("vidoe.mode", 1)
        putExtra("name", name)
        putExtra("video.play.name", name)
        context.startActivity(this)
    }
}