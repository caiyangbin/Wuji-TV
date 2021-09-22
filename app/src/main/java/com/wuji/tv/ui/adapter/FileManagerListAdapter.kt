package com.wuji.tv.ui.adapter

import android.graphics.Color
import android.view.View
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.item_file_list_layout.view.*
import com.wuji.tv.R
import com.wuji.tv.model.LocalFile
import java.util.*

class FileManagerListAdapter(devices: ArrayList<LocalFile>?) :
    com.wuji.tv.common.BaseRvAdapter<LocalFile>(devices) {

    override fun setData(view: View, data: LocalFile?, position: Int) {
        with(view) {
            itemRoot.setBackgroundColor(Color.TRANSPARENT)
            tvName.setTextColor(Color.WHITE)
            tvTime.setTextColor(Color.WHITE)

            if (position == 0) {
                tvName.text = ".."
                tvTime.text = ""
                ivIcon.setImageResource(R.mipmap.dir)
            } else {
                tvName.text = data?.name
                tvTime.text = data?.time
            }
            when (data?.type) {
                "all", "txt", "doc", "pdf", "xls", "ppt", "zip", "bt" -> {
                    ivIcon.setImageResource(R.mipmap.file)
                }
                "dir" -> {
                    ivIcon.setImageResource(R.mipmap.dir)
                }
                "pic" -> {
                    ivIcon.setImageResource(R.mipmap.image_file)
                }
                "video" -> {
                    ivIcon.setImageResource(R.mipmap.video_file)
                }
                "audio" -> {
                    ivIcon.setImageResource(R.mipmap.music_file)
                }
                else -> {
                    ivIcon.setImageResource(R.mipmap.file)
                }
            }
            if (data?.name!!.toLowerCase(Locale.getDefault()).endsWith(".apk")) {
                ivIcon.setImageResource(R.mipmap.apk)
            }
        }
    }

    override fun setFocusData(view: View, data: LocalFile?, position: Int) {
        ViewCompat.animate(view).scaleX(1.02f).scaleY(1.02f).translationZ(1.02f).setDuration(100)
            .start()
//        with(view) {
//            itemRoot.setBackgroundColor(Color.WHITE)
//            tvName.setTextColor(Color.BLACK)
//            tvTime.setTextColor(Color.BLACK)
//        }
    }

    override fun setNoFocusData(view: View, data: LocalFile?, position: Int) {

        ViewCompat.animate(view).scaleX(1f).scaleY(1f).translationZ(1.0f).setDuration(100).start()
//        with(view) {
//            itemRoot.setBackgroundColor(Color.TRANSPARENT)
//            tvName.setTextColor(Color.WHITE)
//            tvTime.setTextColor(Color.WHITE)
//        }
    }

    override fun getItemLayoutID(viewType:Int): Int = R.layout.item_file_list_layout

}