package com.admin.libcommon.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * 协程相关ext
 * Create by admin on 2020/1/8-11:35
 */

inline fun doOnUI(crossinline doInUI: suspend CoroutineScope.() -> Unit) {
    GlobalScope.launch(Dispatchers.Main) {
        doInUI()
    }
}

inline fun doOnIO(crossinline doInIO: suspend CoroutineScope.() -> Unit) {
    GlobalScope.launch(Dispatchers.IO) {
        doInIO()
    }
}