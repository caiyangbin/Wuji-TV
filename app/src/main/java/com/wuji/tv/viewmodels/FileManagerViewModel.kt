package com.wuji.tv.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.admin.libcommon.base.BaseViewModel
import com.admin.libcommon.ext.isNotNullOrEmpty
import com.admin.libcommon.ext.log
import com.wuji.tv.App
import com.wuji.tv.Constants.Companion.ERROR
import com.wuji.tv.Constants.Companion.GET_DEVICE
import com.wuji.tv.R
import com.wuji.tv.model.Download
import com.wuji.tv.model.LocalFile
import com.wuji.tv.model.ProgressModel
import com.wuji.tv.presenter.FileManagerPresenter
import com.wuji.tv.repository.FileManagerRepository


class FileManagerViewModel(
    private val repository: FileManagerRepository,
    private val presenter: FileManagerPresenter
) : BaseViewModel() {

    val fileListLiveData by lazy {
        MutableLiveData<ArrayList<LocalFile>>()
    }
    val downloadListLiveData by lazy {
        MutableLiveData<ArrayList<Download>>()
    }

    val progressLiveData by lazy {
        MutableLiveData<ProgressModel>()
    }
    val deviceListLiveData by lazy {
        MutableLiveData<ArrayList<LocalFile>>()
    }

    val downloadStatuLiveData by lazy {
        MutableLiveData<Int>()
    }


    fun getFiles(path: String) {
//        loadingStatusLiveData.value = LoadingStatus.LOADING
        fileListLiveData.apply {
            runOnUI {
                val devices = withIO { repository.getFiles(path) }
                if (devices.isNotNullOrEmpty()) {
//                    loadingStatusLiveData.value = LoadingStatus.LOAD_SUCCESS
                    value = devices
                } else {
//                    loadingStatusLiveData.value = LoadingStatus.LOAD_ERROR
//                    exceptionLiveData.value = Exception("当前没有存储设备")
                }
            }
        }
    }

    fun getDevices() {
        runOnUI {
            postMsg(CHANGE_LOADING_STATUS, SHOW_LOADING)
            val devices = withIO { repository.getLocalDevices() }
            if (devices.isNullOrEmpty()) {
                liveData.value = ValueModel(ERROR, Exception(App.app.getString(R.string.meiyouyinpan)))
                postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
            } else {
                liveData.value = ValueModel(GET_DEVICE, devices)
                postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
            }
        }
    }

    fun getDownloadedFiles(path: String) {
        runOnUI {
            postMsg(CHANGE_LOADING_STATUS, SHOW_LOADING)
            val files = withIO {
                if (path == "/") {
                    repository.getDownloadedRootFiles()
                } else {
                    repository.getFiles(path)
                }
            }
            if (files.isNullOrEmpty()) {
                liveData.value = ValueModel(ERROR, Exception(App.app.getString(R.string.now_nothing_file)))
                postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
            } else {
                fileListLiveData.value = files
                postMsg(CHANGE_LOADING_STATUS, HIDE_LOADING)
            }
        }
    }

    fun getDownloadingList(networkId: String,context: Context) {
        runOnUI {
            downloadListLiveData.value = withIO { repository.getDownloadingList(networkId,context) }
        }
    }

    fun cancel(ticket_2: String,token: String) {
        runOnUI {
            repository.cancel(ticket_2,token)
        }
    }

    fun cancelDb(tickets: Array<String>,session: String) {
        runOnUI {
            repository.cancelDb(tickets,session)
        }
    }

    fun getProgress(ticket: String) {
        runOnUI {
            try {
                progressLiveData.value = withIO { repository.getProgress(ticket) }
            }catch (e: java.lang.Exception){
                e.printStackTrace()
            }
        }
    }

    fun getDbProgress(ticket: String, session: String) {
        runOnUI {
            try {
                progressLiveData.value = withIO { repository.getDbProgress(arrayOf(ticket),session) }
            }catch (e: java.lang.Exception){
                e.printStackTrace()
            }
        }
    }

    fun cancelDownload(tickets: Array<String>) {
        runOnUI {
            repository.cancelDownload(tickets)
            /*downloadListLiveData.value = withIO {
                repository.cancelDownload(tickets)
                if (token != null) {
                    for (t in tickets){
                        repository.cancel(t,token)
                    }
                }
                repository.getDownloadingList()
            }*/
        }
    }

    fun stopDownload(ticket: String) {
        runOnUI {
            withIO { repository.stopDownload(ticket) }
        }
    }

    fun resumeDownload(ticket: String) {
        runOnUI {
            withIO { repository.resumeDownload(ticket) }
        }
    }

    fun access() {
        runOnUI {
            withIO { repository.access(App.token!!) }
        }
    }

    fun toggleDownload(ticket: String, session: String) {
        runOnIO {
            val progress =
                if (session.isNullOrEmpty())
                    repository.getProgress(ticket)
                else
                    repository.getDbProgress(arrayOf(ticket),session)
            "${progress?.status}".log("hfuwioejffaeew")
            runOnUI {
                downloadStatuLiveData.value = progress?.status
            }
            when (progress?.status) {
                1 -> {
                    if (session.isNullOrEmpty())
                        repository.stopDownload(ticket)
                    else
                        repository.stopDb(arrayOf(ticket),session)
                }
                2 -> {
                    if (session.isNullOrEmpty())
                        repository.resumeDownload(ticket)
                    else
                        repository.resumeDb(ticket,session)
                }
                4 -> {
                    if (session.isNullOrEmpty())
                        repository.resumeDownload(ticket)
                    else
                        repository.resumeDb(ticket,session)
                }
            }
        }
    }
}