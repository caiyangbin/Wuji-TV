package com.wuji.tv.utils

import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.text.TextUtils
import android.text.format.Formatter
import androidx.annotation.RequiresApi
import java.io.File
import java.text.SimpleDateFormat


@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
fun getInternalTotalSpace(): String {
    val path = Environment.getDataDirectory().path
    val statFs = StatFs(path)
    return Formatter.formatFileSize(com.wuji.tv.App.app, statFs.totalBytes)
}

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
fun getInternalAvailableSpace(): String {
    val path = Environment.getDataDirectory().path
    val sf = StatFs(path)
    return Formatter.formatFileSize(com.wuji.tv.App.app, sf.availableBytes)
}

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
fun getPathTotalSpace(path: String): String {
    val statFs = StatFs(path)
    return Formatter.formatFileSize(com.wuji.tv.App.app, statFs.totalBytes)
}

@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
fun getPathAvailableSpace(path: String): String {
    val sf = StatFs(path)
    return Formatter.formatFileSize(com.wuji.tv.App.app, sf.availableBytes)
}


fun getDirList(parent: String?): List<File>? {
    if (TextUtils.isEmpty(parent)) {
        return null
    }
    val parentFile = File(parent)
    if (parentFile.exists() && parentFile.isFile) {
        return null
    }
    return parentFile.listFiles()?.toList()
}

fun getDirLength(file: File): String {
    val length = file.length()
    return Formatter.formatFileSize(com.wuji.tv.App.app, length)
}

fun getFileLastEditTime(file: File): String {
    val lastModified = file.lastModified()
    val formatter = SimpleDateFormat("MM-dd HH:mm")
    return formatter.format(lastModified)
}