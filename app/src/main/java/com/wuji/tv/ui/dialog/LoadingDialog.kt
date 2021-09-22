package com.wuji.tv.ui.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.WindowManager


class LoadingDialog constructor(context: Context) :
    Dialog(context, com.wuji.tv.R.style.DialogFullscreen) {


    init {
        setContentView(com.wuji.tv.R.layout.dialog_loading)
    }


    override fun show() {

        val lp = this.window?.attributes
        lp?.gravity = Gravity.CENTER
        this.window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
        this.window?.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND or WindowManager.LayoutParams.FLAG_DIM_BEHIND)

        super.show()
    }
}