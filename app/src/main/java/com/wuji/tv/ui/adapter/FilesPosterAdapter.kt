package com.wuji.tv.ui.adapter

import android.graphics.Color
import android.text.TextUtils
import android.view.View
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.item_files_poster.view.*
import com.wuji.tv.R
import com.wuji.tv.model.MyFile
import com.wuji.tv.utils.RxUtils

class FilesPosterAdapter(devices: ArrayList<MyFile>?) :
    com.wuji.tv.common.BaseRvAdapter<MyFile>(devices) {

    private val rxUtils by lazy {
        RxUtils()
    }

    override fun setData(view: View, data: MyFile?, position: Int) {
        with(view) {
            setBackgroundResource(R.drawable.shape_round_corner_item_bg_normal)
            tvTitle.setFocusedFlag(false)
            tvTitle.ellipsize = TextUtils.TruncateAt.END
            tvTitle.setTextColor(Color.WHITE)
            tvTitle.text = data?.name
        }
    }

    override fun setFocusData(view: View, data: MyFile?, position: Int) {
        ViewCompat.animate(view).scaleX(1.02f).scaleY(1.02f).translationZ(1.02f).setDuration(200)
            .start()
        with(view) {
            setBackgroundResource(R.drawable.shape_round_corner_item_bg_focus)
            rxUtils.executionTimer(1) {
                tvTitle.setFocusedFlag(true)
                tvTitle.ellipsize = TextUtils.TruncateAt.MARQUEE
            }
            tvTitle.setTextColor(Color.BLACK)
        }
    }

    override fun setNoFocusData(view: View, data: MyFile?, position: Int) {
        ViewCompat.animate(view).scaleX(1f).scaleY(1f).translationZ(1f).setDuration(200)
            .start()
        with(view) {
            rxUtils.cancelTimer()
            setBackgroundResource(R.drawable.shape_round_corner_item_bg_normal)
            tvTitle.setFocusedFlag(false)
            tvTitle.ellipsize = TextUtils.TruncateAt.END
            tvTitle.setTextColor(Color.WHITE)
        }
    }

    override fun getItemLayoutID(viewType: Int): Int = R.layout.item_files_poster
}