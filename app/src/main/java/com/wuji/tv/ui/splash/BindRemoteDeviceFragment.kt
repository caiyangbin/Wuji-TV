package com.wuji.tv.ui.splash

import android.os.Bundle
import android.view.KeyEvent
import com.king.zxing.util.CodeUtils
import com.wuji.tv.R
import com.wuji.tv.ui.MainActivity
import kotlinx.android.synthetic.main.fragment_remote_device.*
import org.jetbrains.anko.support.v4.startActivity

class BindRemoteDeviceFragment : com.wuji.tv.common.BaseFragment() {


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initView()
        initEvent()
    }

    private fun initEvent() {
        nextBtn.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN){
                when(keyCode){
                    KeyEvent.KEYCODE_DPAD_LEFT-> {
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT-> {
                        return@setOnKeyListener true
                    }
                }
            }
            return@setOnKeyListener false
        }

    }


    private fun initView() {

        val qrcode = CodeUtils.createQRCode("ver=2_appId=CN6SDL3H5K4UL55YP77L_SN=MCSM3A860257.SH4YCX", 600, null)
        ivRqCode.setImageBitmap(qrcode)

        nextBtn.setOnClickListener {
            activity?.finish()
            startActivity<MainActivity>()
        }
    }

    override fun onResume() {
        super.onResume()
        nextBtn.requestFocus()
    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return false
    }


    override fun getLayoutID(): Int = R.layout.fragment_remote_device
}