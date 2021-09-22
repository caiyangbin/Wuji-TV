package com.wuji.tv.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.StatFs
import android.text.format.Formatter
import com.admin.libcommon.ext.log
import com.wuji.tv.App
import com.wuji.tv.database.DownloadListDao
import com.wuji.tv.database.MySQLiteOpenHelper
import com.wuji.tv.model.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class FileManagerRepository(
  private val context: Context,
  private val downloadListDao: DownloadListDao
) {

//    private val netScanner: NetScanner by lazy {
//        NetScanner()
//    }

  fun getLocalDevices(): ArrayList<LocalFile> {
    return ArrayList<LocalFile>().apply {
      //获取nfs和smb路径
      if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N) {
        val nfs = File("/mnt/nfsShare/")
        if (nfs.exists()) {
          nfs.listFiles()?.let {
            forEach { nfsResult ->
              val available =
                Formatter.formatFileSize(context, getAvailableBytesByPath(nfsResult.path))
              val total =
                Formatter.formatFileSize(context, getTotalBytesByPath(nfsResult.path))
              this.add(LocalFile(nfsResult.name, nfsResult.path, false, total, available))
            }
          }
        }
      }
    }
  }


  fun getFiles(path: String): ArrayList<LocalFile> {
    return ArrayList<LocalFile>().apply {
      // 获取具体路径下的文件列表
      val file = File(path)
      if (file.exists() && file.canRead()) {
        file.listFiles()?.let { tempFile ->
          tempFile.forEach {
            if (!it.name.startsWith(".")) {
              val type: String
              val fileCount: Int
              val fileSize: String
              if (it.isDirectory) {
                type = "dir"
                fileCount = it.listFiles()?.size ?: 0
                fileSize = ""
              } else {
                type = "file"
                fileCount = 0
                fileSize = Formatter.formatFileSize(context, it.length())
              }
              val time =
                SimpleDateFormat(
                  "yyyy-MM-dd",
                  Locale.getDefault()
                ).format(
                  it.lastModified()
                )

              val localFile =
                LocalFile(
                  it.name,
                  it.path,
                  false,
                  "",
                  "",
                  type,
                  time,
                  fileSize,
                  fileCount
                )
              this.add(localFile)
            }
          }
          this.add(0, LocalFile("..", "", type = "dir"))
        }
      }
    }
  }


  @SuppressLint("NewApi", "ObsoleteSdkInt")
  private fun getAvailableBytesByPath(path: String): Long {
    val statFs = StatFs(path)
    statFs.restat(path)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      statFs.availableBytes
    } else {
      val availableBlocks = statFs.availableBlocks
      val blockSize = statFs.blockSize
      (availableBlocks * blockSize).toLong()
    }
  }

  @SuppressLint("NewApi", "ObsoleteSdkInt")
  private fun getTotalBytesByPath(path: String): Long {
    val statFs = StatFs(path)
    statFs.restat(path)
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
      statFs.totalBytes
    } else {
      val availableBlocks = statFs.totalBytes
      val blockSize = statFs.blockSize
      availableBlocks * blockSize
    }
  }

  suspend fun cancel(tickets: String, token: String): Boolean {
    val para = HashMap<String, Any>()
    para["ticket_2"] = tickets
    para["token"] = token
    val result = App.createApi?.cancel(para)
    if (result == null || result.status != 0) {
      return false
    }
    return true
  }

  suspend fun cancelDb(tickets: Array<String>, session: String): Boolean {
    val para = HashMap<String, Any>()
    para["dl_tickets"] = tickets
    para["session"] = session
    val result = App.downloadDbApi?.cancelDb(para)
    if (result == null || result.result != 0) {
      return false
    }
    return true
  }

  suspend fun stopDb(tickets: Array<String>, session: String): Boolean {
    val para = HashMap<String, Any>()
    para["dl_tickets"] = tickets
    para["session"] = session
    val result = App.downloadDbApi?.stopDb(para)
    if (result == null || result.result != 0) {
      return false
    }
    return true
  }

  suspend fun resumeDb(ticket: String, session: String): Boolean {
    val para = HashMap<String, Any>()
    para["dl_ticket"] = ticket
    para["session"] = session
    val result = App.downloadDbApi?.resumeDb(para)
    if (result == null || result.result != 0) {
      return false
    }
    return true
  }

  /*suspend fun download(
      token: String?,
      paths: Array<String>,
      shareType: Int,
      toUserPath: String
  ): Boolean {
      val map = HashMap<String, Any>()
//        map["password"] = "0tpT"
//        map["to_user_id"] = arrayOf("admin5052")
      map["token"] = token!!
      map["path"] = paths
      map["period"] = 24
      map["download"] = 1
      map["share_path_type"] = shareType
      val createPara = FileServerHelper.encrypt(map) ?: return false
      val createResult = App.createApi?.create(createPara)
      if (createResult == null || createResult.status != 0) {
          return false
      }

      val downloadMap = HashMap<String, Any>()
      downloadMap["token"] = token
      downloadMap["download_path"] = paths
      downloadMap["ticket_2"] = createResult.result.ticket2
      downloadMap["to_user_path"] = toUserPath
      downloadMap["is_public_path"] = 1
      val downloadPara = FileServerHelper.encrypt(downloadMap) ?: return false
      val downloadResult = App.downloadApi?.download(downloadPara)
      if (downloadResult == null || downloadResult.status != 0) {
          return false
      }
      downloadListDao.insert(
          DownloadDBModel(
              downloadResult.result.ticket,
              paths[0].substring(1, paths[0].length)
          )
      )
      return true
  }*/

  suspend fun getDownloadingList(networkId: String, context: Context): ArrayList<Download>? {
    val downloads = ArrayList<Download>()
    if (App.token == null) {
      return downloads
    }
    val mySQLiteOpenHelper = MySQLiteOpenHelper(context)
    if (networkId.isEmpty()) {
      val para = HashMap<String, Any>()
      para["token"] = App.token!!
      val list = App.downloadApi?.getList(para)
      if (list?.result?.downloadList != null) {
        list.result.downloadList.forEach { result ->
          val queryForTicket =
            mySQLiteOpenHelper.queryTicket(result.ticket)//downloadListDao.queryForTicket(result.ticket)
          if (queryForTicket.isNullOrEmpty()) {
            result.name = "未知"
          } else {
            result.name = queryForTicket
          }
          downloads.add(result)
        }
      }
    } else {
      val authMap = HashMap<String, Any>()
      authMap["token"] = App.token!!
      val authResult = App.downloadDbApi?.auth(authMap)
      if (authResult != null && authResult.result == 0) {
        val dbListResult = App.downloadDbApi?.listDb()
        if (dbListResult?.data != null) {
          dbListResult.data.list_items.forEach { item ->
            if (!item.is_main_seed) {
              val queryNetworkId = mySQLiteOpenHelper.queryTicket(item.bt_ticket)
              if (queryNetworkId == networkId) {
                downloads.add(
                  Download(
                    item.dl_ticket,
                    "",
                    "",
                    0,
                    "",
                    "",
                    item.name,
                    authResult.data.session
                  )
                )
              }
            }
          }
        }
      }
    }

    return downloads
  }

  suspend fun getProgress(ticket: String): ProgressModel? {
    val para = HashMap<String, Any>()
    para["ticket"] = ticket
    val result = App.downloadApi?.progress(para)?.result
    if (result != null) {
      result.ticket = ticket
    }
    return result
  }

  suspend fun getDbProgress(tickets: Array<String>, session: String): ProgressModel? {
    val para = HashMap<String, Any>()
    para["dl_tickets"] = tickets
    para["session"] = session
    val result = App.downloadDbApi?.dbProgress(para)?.data?.progress
    if (result == null || result.isEmpty()) return null
    return result[0]
  }

  suspend fun getCompleteFileList(ticket: String) {
    val para = HashMap<String, Any>()
    para["ticket"] = ticket
    para["path"] = "/"
    para["page"] = 1
    para["pages"] = 100
    val result = App.downloadApi?.getCompleteFileList(para)
//        val result = App.sdvnApi?.getCompleteFileList(encrypt!!)
    "result=${result}".log()
  }

  suspend fun getDownloadInfo(ticket: String) {
    val para = HashMap<String, Any>()
    para["ticket"] = ticket
    para["info_id"] = 1
    para["page"] = 1
    para["pages"] = 100
    val result = App.downloadApi?.getDownloadInfo(para)
//        val result = App.sdvnApi?.getDownloadInfo(encrypt!!)
    "result=${result?.string()}".log()
  }


  suspend fun cancelDownload(tickets: Array<String>) {
    val para = HashMap<String, Any>()
    para["tickets"] = tickets
    val result = App.downloadApi?.cancelDownload(para!!)
//        val result = App.sdvnApi?.cancelDownload(encrypt!!)
    "result=${result?.string()}".log()
  }

  suspend fun stopDownload(ticket: String) {
    val para = HashMap<String, Any>()
    para["ticket"] = ticket
    val result = App.downloadApi?.stopDownload(para!!)
//        val result = App.sdvnApi?.stopDownload(encrypt!!)
    "result=${result?.string()}".log()
  }

  suspend fun resumeDownload(ticket: String) {
    val para = HashMap<String, Any>()
    para["ticket"] = ticket
    val result = App.downloadApi?.resumeDownload(para!!)
//        val result = App.sdvnApi?.resumeDownload(encrypt!!)
    "result=${result?.string()}".log()
  }


  suspend fun access(token: String)/*BaseResult<AccessResult>?*/ {
    val params = HashMap<String, Any>()
    params["token"] = token
    val body = HashMap<String, Any>()
    body["method"] = "access"
    body["session"] = ""
    body["params"] = params
    val access = App.localApi?.access(body)
    getFileList("/", access!!.data!!.session)
  }

  suspend fun getFileList(path: String, session: String)/*: BaseResult<FileListResult>?*/ {
    val params = HashMap<String, Any>()
    params["path"] = path
    params["share_path_type"] = 2 //2 是public
    val body = HashMap<String, Any>()
    body["method"] = "list"
    body["session"] = session
    body["params"] = params
    val fileList = App.localApi?.fileList(body)
    "fileList=$fileList".log()
  }

  fun getDownloadedRootFiles(): java.util.ArrayList<LocalFile> {
    return ArrayList<LocalFile>().apply {
      // 获取所有本地设备根目录
      val file = File("/mnt/public")
      if (file.exists() && !file.listFiles().isNullOrEmpty()) {
        val available =
          Formatter.formatFileSize(context, getAvailableBytesByPath(file.path))
        val total =
          Formatter.formatFileSize(context, getTotalBytesByPath(file.path))
        val localFile = LocalFile(file.path, file.path, false, total, available)
        localFile.type = "dir"
        localFile.time =
          SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
            File(file.path).lastModified()
          )
        this.add(localFile)
        if (this.size > 0) {
          this.add(0, LocalFile("..", "", type = "dir"))
        }
      }
    }
  }
}