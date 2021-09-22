package com.wuji.tv.ui.adapter

import android.graphics.Color
import android.text.TextUtils
import android.text.format.Formatter
import android.view.View
import kotlinx.android.synthetic.main.item_file_list_layout.view.*
import com.wuji.tv.R
import com.wuji.tv.model.MyFile
import java.text.SimpleDateFormat
import java.util.*

class RemoteFileListAdapter(devices: ArrayList<MyFile>?) :
    com.wuji.tv.common.BaseRvAdapter<MyFile>(devices) {

    override fun setData(view: View, data: MyFile?, position: Int) {
        with(view) {
            itemRoot.setCardBackgroundColor(Color.TRANSPARENT)
            itemRoot.cardElevation = 0f
            itemRoot.radius = 0f

            tvName.setFocusedFlag(false)
            tvName.ellipsize = TextUtils.TruncateAt.END
            tvName.setTextColor(Color.WHITE)
            tvTime.setTextColor(Color.WHITE)

            if (position == 0) {
                tvName.text = ".."
                tvTime.text = ""
                ivIcon.setImageResource(R.mipmap.dir)
            } else {
                tvName.text = data?.name
                val time = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
                    (data?.time?.times(
                        1000
                    ))
                )
                if (data?.ftype == "dir") {
                    tvTime.text = "文件夹  $time"
                } else {
                    val size = Formatter.formatFileSize(mContext, data!!.size)
                    tvTime.text = "$size  $time"
                }
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
                    ivIcon.setImageResource(R.mipmap.else_file)
                }
            }
            if (data?.name!!.toLowerCase(Locale.getDefault()).endsWith(".apk")) {
                ivIcon.setImageResource(R.mipmap.apk)
            }
        }
    }

    override fun setFocusData(view: View, data: MyFile?, position: Int) {
//        ViewCompat.animate(view).scaleX(1.02f).scaleY(1.02f).translationZ(1.02f).setDuration(200)
//            .start()
        with(view) {
//            itemRoot.setCardBackgroundColor(Color.WHITE)
//            itemRoot.cardElevation = 15f
//            itemRoot.radius = 15f
//            tvName.setTextColor(Color.BLACK)
            tvName.setFocusedFlag(true)
            tvName.ellipsize = TextUtils.TruncateAt.MARQUEE
//            tvTime.setTextColor(Color.BLACK)
        }
    }

    override fun setNoFocusData(view: View, data: MyFile?, position: Int) {

//        ViewCompat.animate(view).scaleX(1f).scaleY(1f).translationZ(1.0f).setDuration(200).start()
        with(view) {
//            itemRoot.setCardBackgroundColor(Color.TRANSPARENT)
//            itemRoot.cardElevation = 0f
//            itemRoot.radius = 0f
//            tvName.setTextColor(Color.WHITE)
            tvName.setFocusedFlag(false)
            tvName.ellipsize = TextUtils.TruncateAt.END
//            tvTime.setTextColor(Color.WHITE)
        }
    }

    override fun getItemLayoutID(viewType: Int): Int = R.layout.item_file_list_layout

}