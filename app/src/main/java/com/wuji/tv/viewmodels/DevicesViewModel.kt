package com.wuji.tv.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.libcommon.base.BaseViewModel
import com.admin.libcommon.ext.log
import com.wuji.tv.model.*
import com.wuji.tv.presenter.DevicesPresenter
import com.wuji.tv.repository.DevicesRepository
import io.sdvn.socket.data.SDVNDevice
import kotlinx.coroutines.CoroutineScope
import java.net.ConnectException
import java.util.concurrent.TimeoutException


class DevicesViewModel(
    private val repository: DevicesRepository,
    private val presenter: DevicesPresenter
) :
    BaseViewModel() {


    val errorLiveData by lazy {
        MutableLiveData<ErrorResult>()
    }

    val accessLiveData by lazy {
        MutableLiveData<AccessResult>()
    }

    val tokenLiveData by lazy {
        MutableLiveData<String>()
    }

    val fileListLiveData by lazy {
        MutableLiveData<ArrayList<MyFile>>()
    }

    val downloadLiveData by lazy {
        MutableLiveData<Boolean>()
    }

    val readTxtLiveData by lazy {
        MutableLiveData<ErrorResult>()
    }

    fun initApi(sdvnDevice: SDVNDevice) {
        com.wuji.tv.App.app.initRemoteRetrofit(sdvnDevice.vip)
        com.wuji.tv.App.app.setSdvnDeviceId(sdvnDevice.id)
    }
    /*fun initSdvnApi(sdvnDevice: SDVNDevice, port: String){
        App.app.initRetrofit(sdvnDevice,port)
    }*/

    fun readTxt(session: String, path: String) {
//        postMsg(CHANGE_LOADING_STATUS, SHOW_LOADING)
        handleApiRequest {
            parsingResult(repository.readTxt(session,path),isReadTxt = true)
        }
    }

    fun access(token: String) {
        postMsg(CHANGE_LOADING_STATUS, SHOW_LOADING)
        handleApiRequest {
            parsingResult(repository.access(token,false))
        }
    }

    fun access(token: String,isLocal: Boolean) {
        postMsg(CHANGE_LOADING_STATUS, SHOW_LOADING)
        handleApiTypeRequest(if(isLocal)com.wuji.tv.Constants.ACCESS_LOCAL else com.wuji.tv.Constants.ACCESS) {
            parsingResult(repository.access(token,isLocal),isLocal = isLocal,isAccess = true)
        }
    }

    fun getFileList2(path: String, session: String) {
        handleApiTypeRequest(com.wuji.tv.Constants.GET_REMOTE_FILE_LIST) {
            parsingLiveDataResult(repository.getFileList(path, session))
        }
    }

    private fun parsingResult(result: BaseResult<*>?,
                              isLocal: Boolean = false,
                              isAccess: Boolean = false,
                              isReadTxt: Boolean = false,
                              isGetFileList: Boolean = false) {
        if (result == null) {
            if(isReadTxt){
                runOnUI {
                    liveData.value = ValueModel(com.wuji.tv.Constants.TXT_RESULT, TxtResult(""))
                }
            }
            else if (isAccess){
                runOnUI {
                    liveData.value =
                        ValueModel(
                            if(isLocal)com.wuji.tv.Constants.ACCESS_LOCAL else com.wuji.tv.Constants.ACCESS,
                            AccessResult("", User(0,0,0,"")))
                }
            }
            else{
                handlerError(ErrorResult(REQUEST_ERROR, "请求失败"))
                postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
            }
            return
        }
        val data = result.data
        if (data == null) {
            if(isReadTxt){
                runOnUI {
                    liveData.value = ValueModel(com.wuji.tv.Constants.TXT_RESULT, TxtResult(""))
                }
            }
            else if (isAccess){
                runOnUI {
                    liveData.value =
                        ValueModel(
                            if(isLocal)com.wuji.tv.Constants.ACCESS_LOCAL else com.wuji.tv.Constants.ACCESS,
                            AccessResult("",User(0,0,0,"")))
                }
            }
            else if(isGetFileList){
                runOnUI {
                    val list = ArrayList<MyFile>()
                    list.add(MyFile("dir", 0, "", "", 0, 0, 0))
                    liveData.value = ValueModel(com.wuji.tv.Constants.GET_REMOTE_FILE_LIST, list)
                    postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
                }
            }
            else{
                //handlerError(ErrorResult(DATA_IS_NULL, "数据为空"))
                postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
            }
            return
        }
        handlerResult(data,isLocal)
    }

    private fun handlerError(errorResult: ErrorResult) {
        postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
        runOnUI {
            "errorResult=$errorResult".log()
            liveData.value = ValueModel(com.wuji.tv.Constants.ERROR, Exception(errorResult.msg))
        }
    }

    private fun handlerResult(data: Any,isLocal: Boolean) {
        runOnUI {
            postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
            when (data) {
                is TxtResult -> {
                    liveData.value = ValueModel(com.wuji.tv.Constants.TXT_RESULT, data)
                }
                is AccessResult -> {
                    liveData.value = ValueModel(if(isLocal)com.wuji.tv.Constants.ACCESS_LOCAL else com.wuji.tv.Constants.ACCESS, data)
                }
                is FileListResult -> {
                    //文件列表
                    val list = ArrayList<MyFile>()
                    if (data.files != null && data.files.isNotEmpty()) {
                        // 返回上一层item
                        list.add(MyFile("dir", 0, "", "", 0, 0, 0))
                        data.files.forEach {
                            list.add(it)
                        }
                        liveData.value = ValueModel(com.wuji.tv.Constants.GET_REMOTE_FILE_LIST, list)
                    } else {
                        list.add(MyFile("dir", 0, "", "", 0, 0, 0))
                        liveData.value = ValueModel(com.wuji.tv.Constants.GET_REMOTE_FILE_LIST, list)
                    }
                }
                is CreateResultModel -> {
                }
            }
        }
    }

    fun getFileList(path: String, session: String) {
        postMsg(CHANGE_LOADING_STATUS, SHOW_LOADING)
        handleApiRequest {
            parsingResult(repository.getFileList(path, session),isGetFileList = true)
        }
    }

    fun handleApiTypeRequest(type:String, parseResult: suspend CoroutineScope.() -> Unit) {
        runOnIO {
            try {
                parseResult()
            } catch (e: Exception) {
                runOnUI {
                    postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
                    val msg = when (e) {
                        is ConnectException -> "无法连接服务器，请稍后再试。"
                        is TimeoutException -> "连接超时，请稍后再试。"
                        else -> "未知错误，请稍后再试。"
                    }
                    when(type){
                        com.wuji.tv.Constants.GET_REMOTE_FILE_LIST -> {
                            liveData.value = ValueModel(type, ArrayList<MyFile>(),isException = true)
                        }
                        com.wuji.tv.Constants.ACCESS_LOCAL -> {
                            liveData.value = ValueModel(type, AccessResult("",
                                User(0,0,0,"")
                            ))
                        }
                        com.wuji.tv.Constants.ACCESS -> {
                            liveData.value = ValueModel(type, AccessResult("",User(0,0,0,"")))
                        }
                    }
                }
            }
        }
    }

    private fun parsingLiveDataResult(result: BaseResult<*>?,isLocal: Boolean = false,isReadTxt: Boolean = false) {
        runOnUI {
            if(result?.data == null){
                liveData.value = ValueModel(com.wuji.tv.Constants.GET_REMOTE_FILE_LIST, ArrayList<MyFile>())
                return@runOnUI
            }
            when(result.data){
                is FileListResult -> {
                    if (result.data.files != null && result.data.files.isNotEmpty()) {
                        liveData.value = ValueModel(com.wuji.tv.Constants.GET_REMOTE_FILE_LIST, result.data.files)
                    }
                    else{
                        liveData.value = ValueModel(com.wuji.tv.Constants.GET_REMOTE_FILE_LIST, ArrayList<MyFile>())
                    }
                }
            }
        }
    }

    fun getLocalFileList(path: String, session: String) {
        postMsg(CHANGE_LOADING_STATUS, SHOW_LOADING)
        handleApiRequest {
            parsingResult(repository.getLocalFileList(path, session),isGetFileList = true)
        }
    }

    fun download(token: String, paths: Array<String>, toUserPath: String,bt_ticket: String,
                 host_name: String?,networkId:String,context: Context,remoteId: String?) {
        postMsg(CHANGE_LOADING_STATUS, SHOW_LOADING)
        handleApiRequest {
            val isSuccess =
                if(bt_ticket.isEmpty()){
                    repository.create(token, paths, toUserPath,context,remoteId)
                } else if (host_name != null) {
                    repository.createDb(token,networkId,toUserPath,bt_ticket,host_name,context)
                } else false
            runOnUI {
                postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
                liveData.value =  ValueModel(com.wuji.tv.Constants.DOWNLOAD_RESULT, isSuccess)
            }
        }
    }

}