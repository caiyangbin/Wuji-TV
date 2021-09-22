package com.wuji.tv.ui.splash

import android.os.Bundle
import android.view.KeyEvent
import com.king.zxing.util.CodeUtils
import com.wuji.tv.R
import com.wuji.tv.utils.RxUtils
import io.sdvn.apigateway.data.model.SharedUserList
import io.sdvn.apigateway.protocal.ASBaseListener
import io.sdvn.apigateway.repo.AsRepo
import io.sdvn.socket.SDVNApi
import kotlinx.android.synthetic.main.fragment_bind_local_device.*
import okhttp3.Call
import org.jetbrains.anko.support.v4.runOnUiThread
import org.jetbrains.anko.support.v4.toast

class BindLocalDeviceFragment : com.wuji.tv.common.BaseFragment() {

    private var isBind = false
    private val rxUtils by lazy {
        RxUtils()
    }

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
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        previousBtn.requestFocus()
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        return@setOnKeyListener true
                    }
                }
            }
            return@setOnKeyListener false
        }

        previousBtn.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                when (keyCode) {
                    KeyEvent.KEYCODE_DPAD_LEFT -> {
                        return@setOnKeyListener true
                    }
                    KeyEvent.KEYCODE_DPAD_RIGHT -> {
                        if (isBind) {
                            nextBtn.requestFocus()
                        } else {
                            toast(getString(R.string.bind_hint))
                        }
                        return@setOnKeyListener true
                    }
                }
            }
            return@setOnKeyListener false
        }
    }


    private fun checkBind() {
        val self = SDVNApi.getInstance().getSelf()
        self?.let {
            AsRepo().getSharedUserList(it.id, object :
                ASBaseListener<SharedUserList> {
                override fun onFailure(call: Call, e: Exception?, errno: Int, errMsg: String) {
                }

                override fun onSuccess(call: Call, data: SharedUserList) {
                    isBind = data.users != null && data.users!!.isNotEmpty()
                    if (isBind) {
                        rxUtils.cancelInterval()
                        runOnUiThread {
                            nextBtn.requestFocus()
                        }
                    }
                }
            })
        }
    }

    private fun initView() {
        val devSN = SDVNApi.getInstance().getDevSN()
        val qrcode = CodeUtils.createQRCode("V2_MNRWW2_$devSN", 600, null)
        ivRqCode.setImageBitmap(qrcode)
    }


    override fun onResume() {
        super.onResume()
        previousBtn.requestFocus()
        rxUtils.executionInterval(2) {
            checkBind()
        }
    }


    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return false
    }


    override fun onStop() {
        super.onStop()
        rxUtils.cancelInterval()
    }

    override fun getLayoutID(): Int = R.layout.fragment_bind_local_device
}