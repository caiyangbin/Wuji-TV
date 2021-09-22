package com.admin.libcommon.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.ConnectException
import java.util.concurrent.TimeoutException

/**
 * ViewModel基类 定义一些基础的东西
 * Create by admin on 2020/1/8-9:59
 */
abstract class BaseViewModel : ViewModel() {

    data class ValueModel(
        val dataType: String,
        val data: Any,
        val isException: Boolean = false
    )

    companion object {
        const val CHANGE_LOADING_STATUS = "change_loading_status"
        const val SHOW_LOADING = "show_loading"
        const val HIDE_LOADING = "hide_loading"
    }

//    enum class LoadingStatus {
//        LOADING,
//        LOAD_SUCCESS,
//        LOAD_ERROR
//    }

    val liveData by lazy {
        MutableLiveData<ValueModel>()
    }

//    val exceptionLiveData by lazy {
//        MutableLiveData<Exception>()
//    }
//
//    val loadingStatusLiveData by lazy {
//        MutableLiveData<LoadingStatus>()
//    }

    protected fun runOnUI(doInUI: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.Main) {
            doInUI()
        }
    }

    protected fun runOnIO(doInIO: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            doInIO()
        }
    }

    protected fun postMsg(key: String, value: Any = "") {
        LiveEventBus.get(key).post(value)
    }

    suspend fun <T> withIO(doInIO: suspend CoroutineScope.() -> T): T {
        return withContext(Dispatchers.IO) { doInIO() }
    }

    fun handleApiRequest(parseResult: suspend CoroutineScope.() -> Unit) {
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
                    liveData.value = ValueModel("error", java.lang.Exception(msg))
                }
            }
        }
    }

}