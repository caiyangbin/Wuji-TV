package com.wuji.tv.ui.fragment

import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.KeyEvent
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import kotlinx.android.synthetic.main.fragment_options.*
import com.wuji.tv.R

class OptionsFragment : com.wuji.tv.common.BaseFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initView()
        initData()
    }


    private fun initView() {
        tvPrivacy.paint.flags = Paint.UNDERLINE_TEXT_FLAG
        tvProtocol.paint.flags = Paint.UNDERLINE_TEXT_FLAG

        btnUpdate.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                btnUpdate.setBackgroundColor(Color.parseColor("#ff00ddff"))
                btnUpdate.setTextColor(Color.BLACK)
            } else {
                btnUpdate.setBackgroundColor(Color.parseColor("#8a8a8a"))
                btnUpdate.setTextColor(Color.parseColor("#ff00ddff"))
            }
        }

        tvProtocol.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                tvProtocol.setTextColor(Color.BLACK)
            } else {
                tvProtocol.setTextColor(Color.parseColor("#ff00ddff"))
            }
        }

        tvPrivacy.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                tvPrivacy.setTextColor(Color.BLACK)
            } else {
                tvPrivacy.setTextColor(Color.parseColor("#ff00ddff"))
            }
        }
    }

    private fun initData() {
        LiveEventBus.get("options_request_focus").observe(this, Observer {
            btnUpdate.post {
                btnUpdate.requestFocus()
            }
        })
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return false
    }
    override fun getLayoutID(): Int = R.layout.fragment_options
}