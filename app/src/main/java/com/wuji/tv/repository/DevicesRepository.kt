package com.wuji.tv.repository

import android.content.Context
import com.wuji.tv.App
import com.wuji.tv.database.DownloadListDao
import com.wuji.tv.database.MySQLiteOpenHelper
import com.wuji.tv.model.*
import java.util.*
import kotlin.collections.HashMap


class DevicesRepository(
    private val context: Context,
    private val downloadListDao: DownloadListDao
) {


    suspend fun readTxt(session: String, path: String): BaseResult<TxtResult>? {
        val params = HashMap<String, Any>()
        params["path"] = path
        params["cmd"] = "readtxt"
        val body = HashMap<String, Any>()
        body["method"] = "manage"
        body["session"] = session
        body["params"] = params
        val time = Date().time
//        return App.sdvnApi?.readTxt(getBaseUrl(App.ip!!, PORT_URL),body)
        return App.api?.readTxt(body)
    }

    suspend fun access(token: String,isLocal: Boolean): BaseResult<AccessResult>? {
        val params = HashMap<String, Any>()
        params["token"] = token
        val body = HashMap<String, Any>()
        body["method"] = "access"
        body["session"] = ""
        body["params"] = params
        return if(isLocal) App.localApi?.access(body) else App.api?.access(body)
//        return App.sdvnApi?.access(getBaseUrl(App.ip!!, PORT_URL),body)
    }

    suspend fun getFileList(path: String, session: String, page: Int = 0): BaseResult<FileListResult>? {
        val params = HashMap<String, Any>()
        params["path"] = path
        params["share_path_type"] = 2 //2 是public
        params["page"] = page
        val body = HashMap<String, Any>()
        body["method"] = "list"
        body["session"] = session
        body["params"] = params
        return App.api?.fileList(body)
//        return App.sdvnApi?.fileList(getBaseUrl(App.ip!!, PORT_URL),body)
    }

    suspend fun getLocalFileList(path: String, session: String): BaseResult<FileListResult>? {
        val params = HashMap<String, Any>()
        params["path"] = path
        params["share_path_type"] = 2 //2 是public
        val body = HashMap<String, Any>()
        body["method"] = "list"
        body["session"] = session
        body["params"] = params
        return App.localApi?.fileList(body)
//        return App.sdvnApi?.fileList(getBaseUrl(App.ip!!, PORT_URL),body)
    }

    suspend fun create(
        token: String,
        paths: Array<String>,
        toUserPath: String,
        context: Context,
        remoteId: String?
    ): String {
        val map = HashMap<String, Any>()
        map["token"] = token
        map["path"] = paths
        map["period"] = 24
        map["download"] = 1
        map["share_path_type"] = 2
        //圈子
        if (remoteId != null) {
            map["is_direct_connect"] = true
        }
        val createResult = App.createApi?.create(map)
        if (createResult == null || createResult.status != 0) {
            return if(createResult == null) "createResult:null" else "status:${createResult.status}"
        }

        val downloadMap = HashMap<String, Any>()
        downloadMap["token"] = token
        downloadMap["download_path"] = arrayOf("/")
        if (remoteId != null){
            downloadMap["ticket_1"] = createResult.result.ticket1
            downloadMap["remote_id"] = remoteId
        }
        else{
            downloadMap["ticket_2"] = createResult.result.ticket2
        }
        downloadMap["to_user_path"] = toUserPath
        downloadMap["is_public_path"] = 1
        val downloadResult = App.downloadApi?.download(downloadMap)
        if (downloadResult == null || downloadResult.status != 0) {
            return if(downloadResult == null) "downloadResult:null" else "status:${downloadResult.status}"
        }
        /*downloadListDao.insert(
            DownloadDBModel(
                downloadResult.result.ticket,
                paths[0].substring(1, paths[0].length)
            )
        )*/
        val mySQLiteOpenHelper = MySQLiteOpenHelper(context)
        mySQLiteOpenHelper.updateTicket(downloadResult.result.ticket,paths[0].substring(1))
        return "0"
    }

    suspend fun createDb(
        token: String,
        networkId: String,
        toUserPath: String,
        bt_ticket: String,
        host_name: String,
        context: Context
    ): String {
        val authMap = HashMap<String, Any>()
        authMap["token"] = token
        val authResult = App.downloadDbApi?.auth(authMap)
        if (authResult == null || authResult.result != 0) {
            return "0"
        }
        val mySQLiteOpenHelper = MySQLiteOpenHelper(context)
        mySQLiteOpenHelper.updateTicket(bt_ticket,networkId)
        val downloadMap = HashMap<String, Any>()
        downloadMap["session"] = authResult.data.session
        downloadMap["bt_ticket"] = bt_ticket
        downloadMap["remote_server"] = host_name
        downloadMap["save_dir"] = toUserPath
        downloadMap["path_type"] = 2
        val downloadResult = App.downloadDbApi?.downloadDb(downloadMap)
        if (downloadResult == null || downloadResult.result != 0) {
            return downloadResult?.msg ?: "${downloadResult?.result}"
        }
        /*downloadListDao.insert(
            DownloadDBModel(
                downloadResult.data.dl_ticket,
                paths[0].substring(1, paths[0].length)
            )
        )
        val mySQLiteOpenHelper = MySQLiteOpenHelper(context)
        mySQLiteOpenHelper.updateTicket(bt_ticket,paths[0].substring(1))
        */
        return "0"
    }
}