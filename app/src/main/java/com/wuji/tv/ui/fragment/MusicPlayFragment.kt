package com.wuji.tv.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import com.wuji.tv.R
import com.wuji.tv.common.BaseFragment

class MusicPlayFragment : BaseFragment() {

    companion object{
        const val KEY_BUNDLE_FILE = "key_bundle_file"
    }
    override fun getLayoutID(): Int = R.layout.fragment_music_play
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if(!isNavigationViewInit){
            initView()
            initLiveData()
            initEvent()
            initData()
            isNavigationViewInit = true
        }
    }

    private fun initEvent() {
    }

    private fun initLiveData() {

    }

    private fun initView() {
    }

    @SuppressLint("SetTextI18n")
    private fun initData() {
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return false
    }
}