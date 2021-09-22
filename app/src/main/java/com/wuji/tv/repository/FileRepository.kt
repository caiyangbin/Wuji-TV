package com.wuji.tv.repository

import android.content.Context
import com.wuji.tv.App
import com.wuji.tv.model.BaseResult
import com.wuji.tv.model.FileListResult


class FileRepository(private val context: Context) {

    suspend fun getFileList(path: String, session: String): BaseResult<FileListResult>? {
        val params = HashMap<String, Any>()
        params["path"] = path
        params["share_path_type"] = 2 //2 是public
        val body = HashMap<String, Any>()
        body["method"] = "list"
        body["session"] = session
        body["params"] = params
        return App.api?.fileList(body)
//        return App.sdvnApi?.fileList(getBaseUrl(App.ip!!, PORT_URL),body)
    }

}