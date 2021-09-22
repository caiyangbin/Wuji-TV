package com.wuji.tv.ui.adapter

import android.graphics.Color
import android.view.View
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.item_download_type_layout.view.*
import com.wuji.tv.R
import com.wuji.tv.model.LocalFile


class DownloadTypeListAdapter(devices: ArrayList<LocalFile>?) :
    com.wuji.tv.common.BaseRvAdapter<LocalFile>(devices) {

    override fun setData(view: View, data: LocalFile?, position: Int) {
        with(view) {
            tvName.setTextColor(Color.parseColor("#8a8a8a"))
            when (position) {
                0 -> {
                    tvName.text = "下载中"
                    ivIcon.setImageResource(R.mipmap.downloading_normal)
                }
                1 -> {
                    tvName.text = "下载完成"
                    ivIcon.setImageResource(R.mipmap.downloaded_normal)
                }
            }
        }
    }

    override fun setFocusData(view: View, data: LocalFile?, position: Int) {
        view.tvName.setTextColor(Color.WHITE)
        when (position) {
            0 -> {
                view.ivIcon.setImageResource(R.mipmap.downloading)
            }
            1 -> {
                view.ivIcon.setImageResource(R.mipmap.downloaded)
            }
        }
        ViewCompat.animate(view).scaleX(1.08f).scaleY(1.08f).translationZ(1.08f).setDuration(100)
            .start()
    }

    override fun setNoFocusData(view: View, data: LocalFile?, position: Int) {
        view.tvName.setTextColor(Color.parseColor("#8a8a8a"))
        when (position) {
            0 -> {
                view.ivIcon.setImageResource(R.mipmap.downloading_normal)
            }
            1 -> {
                view.ivIcon.setImageResource(R.mipmap.downloaded_normal)
            }
        }
        ViewCompat.animate(view).scaleX(1f).scaleY(1f).translationZ(1.0f).setDuration(100).start()
    }

    override fun getItemLayoutID(viewType:Int): Int = R.layout.item_download_type_layout


    override fun getItemCount(): Int = 2

}