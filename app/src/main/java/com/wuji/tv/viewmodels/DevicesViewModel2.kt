package com.wuji.tv.viewmodels

import com.admin.libcommon.base.BaseViewModel
import com.admin.libcommon.ext.log
import com.wuji.tv.App
import com.wuji.tv.model.*
import com.wuji.tv.presenter.DevicesPresenter
import com.wuji.tv.repository.DevicesRepository
import io.sdvn.socket.data.SDVNDevice
import kotlinx.coroutines.CoroutineScope
import java.net.ConnectException
import java.util.concurrent.TimeoutException

class DevicesViewModel2(
    private val repository: DevicesRepository,
    private val presenter: DevicesPresenter
) : BaseViewModel() {

    private lateinit var mLiveDataListener:LiveDataListener

    interface LiveDataListener {
        fun liveData(valueModel: ValueModel)
    }

    fun setLiveDataListener(liveDataListener: LiveDataListener){
        mLiveDataListener = liveDataListener
    }

    fun initApi(sdvnDevice: SDVNDevice) {
        App.app.initRemoteRetrofit(sdvnDevice.vip)
        App.app.setSdvnDeviceId(sdvnDevice.id)
    }

    fun readTxt(session: String, path: String) {
        handleApiRequest {
            parsingResult(repository.readTxt(session,path),isReadTxt = true)
        }
    }

    fun access(token: String) {
//        postMsg(CHANGE_LOADING_STATUS, SHOW_LOADING)
        handleApiRequest {
            parsingResult(repository.access(token,false))
        }
    }

    fun access(token: String,isLocal: Boolean) {
//        postMsg(CHANGE_LOADING_STATUS, SHOW_LOADING)
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
                    mLiveDataListener.liveData(ValueModel(com.wuji.tv.Constants.TXT_RESULT, TxtResult("")))
                }
            }
            else if (isAccess){
                runOnUI {
                    mLiveDataListener.liveData(
                        ValueModel(
                            if(isLocal)com.wuji.tv.Constants.ACCESS_LOCAL else com.wuji.tv.Constants.ACCESS,
                            AccessResult("", User(0,0,0,"")))
                    )
                }
            }
            else{
                handlerError(ErrorResult(REQUEST_ERROR, "请求失败"))
//                postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
            }
            return
        }
        val data = result.data
        if (data == null) {
            if(isReadTxt){
                runOnUI {
                    mLiveDataListener.liveData(ValueModel(com.wuji.tv.Constants.TXT_RESULT, TxtResult("")))
                }
            }
            else if (isAccess){
                runOnUI {
                    mLiveDataListener.liveData(
                        ValueModel(
                            if(isLocal)com.wuji.tv.Constants.ACCESS_LOCAL else com.wuji.tv.Constants.ACCESS,
                            AccessResult("",User(0,0,0,"")))
                    )
                }
            }
            else if(isGetFileList){
                runOnUI {
                    val list = ArrayList<MyFile>()
                    list.add(MyFile("dir", 0, "", "", 0, 0, 0))
                    mLiveDataListener.liveData(ValueModel(com.wuji.tv.Constants.GET_REMOTE_FILE_LIST, list))
//                    postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
                }
            }
            else{
                //handlerError(ErrorResult(DATA_IS_NULL, "数据为空"))
//                postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
            }
            return
        }
        handlerResult(data,isLocal)
    }

    private fun handlerError(errorResult: ErrorResult) {
//        postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
        runOnUI {
            "errorResult=$errorResult".log()
            mLiveDataListener.liveData(ValueModel(com.wuji.tv.Constants.ERROR, Exception(errorResult.msg)))
        }
    }

    private fun handlerResult(data: Any,isLocal: Boolean) {
        runOnUI {
//            postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
            when (data) {
                is TxtResult -> {
                    mLiveDataListener.liveData(ValueModel(com.wuji.tv.Constants.TXT_RESULT, data))
                }
                is AccessResult -> {
                    mLiveDataListener.liveData(ValueModel(if(isLocal)com.wuji.tv.Constants.ACCESS_LOCAL else com.wuji.tv.Constants.ACCESS, data))
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
                        mLiveDataListener.liveData(ValueModel(com.wuji.tv.Constants.GET_REMOTE_FILE_LIST, list))
                    } else {
                        list.add(MyFile("dir", 0, "", "", 0, 0, 0))
                        mLiveDataListener.liveData(ValueModel(com.wuji.tv.Constants.GET_REMOTE_FILE_LIST, list))
                    }
                }
                is CreateResultModel -> {
                    "下载列表 = $data".log()
                }
            }
        }
    }

    fun getFileList(path: String, session: String) {
//        postMsg(CHANGE_LOADING_STATUS, SHOW_LOADING)
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
//                    postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
                    val msg = when (e) {
                        is ConnectException -> "无法连接服务器，请稍后再试。"
                        is TimeoutException -> "连接超时，请稍后再试。"
                        else -> "未知错误，请稍后再试。"
                    }
                    when(type){
                        com.wuji.tv.Constants.GET_REMOTE_FILE_LIST -> {
                            mLiveDataListener.liveData(ValueModel(type, ArrayList<MyFile>()))
                        }
                        com.wuji.tv.Constants.ACCESS_LOCAL -> {
                            mLiveDataListener.liveData(ValueModel(type, AccessResult("",
                                User(0,0,0,"")
                            )))
                        }
                        com.wuji.tv.Constants.ACCESS -> {
                            mLiveDataListener.liveData(ValueModel(type, AccessResult("",
                                User(0,0,0,""))))
                        }
                    }
                }
            }
        }
    }

    private fun parsingLiveDataResult(result: BaseResult<*>?,isLocal: Boolean = false,isReadTxt: Boolean = false) {
        runOnUI {
            if(result?.data == null){
                mLiveDataListener.liveData(ValueModel(com.wuji.tv.Constants.GET_REMOTE_FILE_LIST, ArrayList<MyFile>()))
                return@runOnUI
            }
            when(result.data){
                is FileListResult -> {
                    if (result.data.files != null && result.data.files.isNotEmpty()) {
                        mLiveDataListener.liveData(ValueModel(com.wuji.tv.Constants.GET_REMOTE_FILE_LIST, result.data.files))
                    }
                    else{
                        mLiveDataListener.liveData(ValueModel(com.wuji.tv.Constants.GET_REMOTE_FILE_LIST, ArrayList<MyFile>()))
                    }
                }
            }
        }
    }

    fun getLocalFileList(path: String, session: String) {
//        postMsg(CHANGE_LOADING_STATUS, SHOW_LOADING)
        handleApiRequest {
            parsingResult(repository.getLocalFileList(path, session),isGetFileList = true)
        }
    }

    /*fun download(token: String, paths: Array<String>, toUserPath: String,bt_ticket: String) {
//        postMsg(CHANGE_LOADING_STATUS, SHOW_LOADING)
        handleApiRequest {
            val isSuccess = repository.create(token, paths, toUserPath)
            runOnUI {
//                postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
                mLiveDataListener.liveData(ValueModel(com.wuji.tv.Constants.DOWNLOAD_RESULT, isSuccess))
            }
        }
    }*/

}