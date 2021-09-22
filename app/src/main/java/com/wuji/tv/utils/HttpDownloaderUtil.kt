package com.wuji.tv.utils

import com.admin.libcommon.ext.log
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.URL
import java.util.*

class HttpDownloaderUtil {

    fun downFile(urlStr: String?, path: String, fileName: String): Int {
        try {
            var input = getInputStream(urlStr)
            if (input == null){

                return -1
            }
            else {

                try {
                    FileUtils.writeToFileFromInput(path, fileName, input) ?: return -1
                }catch (e: java.lang.Exception){
                    e.toString().log("writeToFileFromInput")
                    return downFile(urlStr,path,fileName)
                }
            }
        } catch (e: Exception) {
            e.toString().log("downFile")
            return -2
        }
        return 0
    }

    private var httpTime = -1L //连接超时，10秒自动重连
    @Throws(Exception::class)
    fun getInputStream(urlStr: String?): InputStream? {
        if (httpTime == -1L){
            httpTime = Date().time/1000
        }
        var inputStream: InputStream? = null
        try {
            val url = URL(urlStr)
            val urlConn: HttpURLConnection = url.openConnection() as HttpURLConnection
            inputStream = urlConn.inputStream
        }
        catch (e:SocketTimeoutException){
            val time = Date().time/1000
            if (time - httpTime < 5)
                return getInputStream(urlStr)
        }
        httpTime = -1L
        return inputStream
    }
}