package com.wuji.tv.ui.adapter

import android.graphics.Color
import android.view.View
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.item_files_grid.view.*
import com.wuji.tv.R
import com.wuji.tv.model.MyFile
import java.util.*


class FilesGridAdapter(devices: ArrayList<MyFile>?) :
    com.wuji.tv.common.BaseRvAdapter<MyFile>(devices) {
    override fun setData(view: View, data: MyFile?, position: Int) {
        with(view) {
            setBackgroundColor(Color.TRANSPARENT)
            tvName.setTextColor(Color.WHITE)
            if (position == 0) {
                tvName.text = ".."
                ivIcon.setImageResource(R.mipmap.dir)
            } else {
                tvName.text = data?.name
            }
            when (data?.ftype) {
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

    override fun setFocusData(view: View, data: MyFile?, position: Int) {
        ViewCompat.animate(view).scaleX(1.02f).scaleY(1.02f).translationZ(1.02f).setDuration(200)
            .start()
        with(view) {
            setBackgroundResource(R.drawable.shape_round_corner_item_bg_focus)
            tvName.setTextColor(Color.BLACK)
        }
    }

    override fun setNoFocusData(view: View, data: MyFile?, position: Int) {
        ViewCompat.animate(view).scaleX(1f).scaleY(1f).translationZ(1f).setDuration(200)
            .start()
        with(view) {
            setBackgroundColor(Color.TRANSPARENT)
            tvName.setTextColor(Color.WHITE)
        }
    }

    override fun getItemLayoutID(viewType: Int): Int = R.layout.item_files_grid
}