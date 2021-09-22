package com.wuji.tv.viewmodels

import androidx.lifecycle.MutableLiveData
import com.admin.libcommon.base.BaseViewModel
import com.wuji.tv.model.*
import com.wuji.tv.presenter.FilePresenter
import com.wuji.tv.repository.FileRepository

class FileViewModel(private val repository: FileRepository, private val presenter: FilePresenter) :
    BaseViewModel() {

    val errorLiveData by lazy {
        MutableLiveData<ErrorResult>()
    }

    val fileListLiveData by lazy {
        MutableLiveData<ArrayList<MyFile>>()
    }

    fun getFileList(path: String, session: String) {
        handleApiRequest {
            parsingResult(repository.getFileList(path, session))
        }
    }


    private fun parsingResult(result: BaseResult<*>?) {
        if (result == null || !result.result) {
            handlerError(ErrorResult(REQUEST_ERROR, "请求失败"))
            return
        }
        val data = result.data
        if (data == null) {
            handlerError(ErrorResult(DATA_IS_NULL, "请求数据为空"))
            return
        }
        handlerResult(data)
    }

    private fun handlerError(errorResult: ErrorResult) {
        runOnUI {
            errorLiveData.value = errorResult
        }
    }

    private fun handlerResult(data: Any) {
        runOnUI {
            when (data) {
                is FileListResult -> {
                    //文件列表
                    if (data.files != null && data.files.isNotEmpty()) {
                        val list = ArrayList<MyFile>()
                        // 返回上一层item
                        list.add(MyFile("dir", 0, "", "", 0, 0, 0))
                        data.files.forEach {
                            list.add(it)
                        }
                        fileListLiveData.value = list
                    } else {
                        errorLiveData.value = ErrorResult(FILE_IS_NULL, "文件列表为空")
                    }
                }
            }
        }
    }


    fun copy() {

    }

    fun past() {

    }

    fun move() {

    }

    fun delete() {


    }

    fun create() {

    }

    fun rename() {

    }
}