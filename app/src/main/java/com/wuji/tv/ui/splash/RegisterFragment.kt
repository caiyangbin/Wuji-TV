package com.wuji.tv.ui.splash

import android.os.Bundle
import android.view.KeyEvent
import com.king.zxing.util.CodeUtils
import com.wuji.tv.R
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : com.wuji.tv.common.BaseFragment() {


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initView()
        initEvent()
    }

    private fun initEvent() {

        previousBtn.setOnClickListener {
            (activity as SplashActivity).previousPage()
        }

        nextBtn.setOnClickListener {
            (activity as SplashActivity).nextPage()
        }


        nextBtn.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN){
                when(keyCode){
                    KeyEvent.KEYCODE_DPAD_LEFT-> {
                        previousBtn.requestFocus()
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT-> {
                        return@setOnKeyListener true
                    }
                }
            }
            return@setOnKeyListener false
        }

        previousBtn.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN){
                when(keyCode){
                    KeyEvent.KEYCODE_DPAD_LEFT-> {
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT-> {
                        nextBtn.requestFocus()
                        return@setOnKeyListener true
                    }
                }
            }
            return@setOnKeyListener false
        }

    }


    private fun initView() {

        val downloadUrl = CodeUtils.createQRCode(
            "https://www.baidu.com/",
            600,
            null
        )
        ivRqCode.setImageBitmap(downloadUrl)

    }


    override fun onResume() {
        super.onResume()
        nextBtn.requestFocus()
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return false
    }


    override fun getLayoutID(): Int = R.layout.fragment_register
}