package com.wuji.tv.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.View
import kotlinx.android.synthetic.main.item_loacl_device_layout.view.*
import kotlinx.android.synthetic.main.item_loacl_device_title_layout.view.*
import com.wuji.tv.Constants.Companion.ITEM_VIEW_TYPE_NORMAL
import com.wuji.tv.Constants.Companion.ITEM_VIEW_TYPE_TITLE
import com.wuji.tv.R
import com.wuji.tv.model.LocalFile


class LocalDeviceListAdapter(devices: ArrayList<LocalFile>?) :
    com.wuji.tv.common.BaseRvAdapter<LocalFile>(devices) {

    @SuppressLint("SetTextI18n")
    override fun setData(view: View, data: LocalFile?, position: Int) {
        data?.apply {
            if (isTitle) {
                with(view) {
                    tvTitle.text = data.name
                    when (data.name) {
                        "本机存储" -> ivTypeIcon.setImageResource(R.mipmap.device)
                        "已下载" -> ivTypeIcon.setImageResource(R.mipmap.downloaded)
                    }
                }
            } else {
                with(view) {
                    this.setBackgroundColor(Color.BLACK)
                    tvName.setTextColor(Color.parseColor("#8a8a8a"))
                    tvIp.setTextColor(Color.parseColor("#8a8a8a"))
//                    ivIcon.setImageResource(R.mipmap.device)
                    tvName.text = name
                    tvIp.text = "$available / $total"
                }
            }
        }
    }

    override fun setFocusData(view: View, data: LocalFile?, position: Int) {
        if (!data!!.isTitle) {
            view.tvName.setTextColor(Color.BLACK)
            view.tvIp.setTextColor(Color.BLACK)
//            view.ivIcon.setImageResource(R.mipmap.device_normal)
            view.setBackgroundColor(Color.WHITE)
        }

    }

    override fun setNoFocusData(view: View, data: LocalFile?, position: Int) {
        if (!data!!.isTitle) {
            view.tvName.setTextColor(Color.parseColor("#8a8a8a"))
            view.tvIp.setTextColor(Color.parseColor("#8a8a8a"))
//            view.ivIcon.setImageResource(R.mipmap.device_normal)
            view.setBackgroundColor(Color.BLACK)
        }

    }

    override fun getItemLayoutID(viewType: Int): Int {
        return when (viewType) {
            ITEM_VIEW_TYPE_NORMAL -> {
                R.layout.item_loacl_device_layout
            }
            ITEM_VIEW_TYPE_TITLE -> {
                R.layout.item_loacl_device_title_layout
            }
            else -> {
                R.layout.item_loacl_device_layout
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (position in 0 until itemCount) {
            return if (getDatas()[position].isTitle) ITEM_VIEW_TYPE_TITLE else ITEM_VIEW_TYPE_NORMAL
        }
        return ITEM_VIEW_TYPE_NORMAL
    }
}