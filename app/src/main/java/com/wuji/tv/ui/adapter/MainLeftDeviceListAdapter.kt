package com.wuji.tv.ui.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import kotlinx.android.synthetic.main.item_loacl_device_title_layout.view.*
import kotlinx.android.synthetic.main.item_main_left_device_list_layout.view.*
import com.wuji.tv.Constants.Companion.ITEM_VIEW_TYPE_NORMAL
import com.wuji.tv.Constants.Companion.ITEM_VIEW_TYPE_TITLE
import com.wuji.tv.R
import com.wuji.tv.model.SdvnDeviceModel


class MainLeftDeviceListAdapter(devices: ArrayList<SdvnDeviceModel>?) :
    com.wuji.tv.common.BaseRvAdapter<SdvnDeviceModel>(devices) {

    @SuppressLint("SetTextI18n")
    override fun setData(view: View, data: SdvnDeviceModel?, position: Int) {
        data?.apply {
            if (isTitle) {
                with(view) {
                    tvTitle.text = data.title
                    when (data.title) {
                        "我的" -> ivTypeIcon.setImageResource(R.mipmap.person_focus)
                        "朋友的" -> ivTypeIcon.setImageResource(R.mipmap.famaly)
                        "圈子" -> ivTypeIcon.setImageResource(R.mipmap.friend)
                    }
                }
            } else {
                with(view) {
                    this.setBackgroundColor(Color.BLACK)
                    ivIcon.setImageResource(R.mipmap.icon1)
                    tvName.setTextColor(Color.parseColor("#8a8a8a"))
                    tvIp.setTextColor(Color.parseColor("#8a8a8a"))
                    tvName.text = data?.sdvnDevice?.name
                    if (TextUtils.isEmpty(data!!.sdvnDevice!!.owner)) {
                        tvIp.text = "unknown"
                    } else {
                        tvIp.text = "${data?.sdvnDevice?.owner}"
                    }
                }
            }
        }
    }

    override fun setFocusData(view: View, data: SdvnDeviceModel?, position: Int) {
        if (!data!!.isTitle) {
            view.setBackgroundColor(Color.WHITE)
            view.tvName.setTextColor(Color.BLACK)
            view.tvIp.setTextColor(Color.BLACK)
        }
    }

    override fun setNoFocusData(view: View, data: SdvnDeviceModel?, position: Int) {
        if (!data!!.isTitle) {
            view.setBackgroundColor(Color.BLACK)
            view.tvName.setTextColor(Color.parseColor("#8a8a8a"))
            view.tvIp.setTextColor(Color.parseColor("#8a8a8a"))
        }
    }

    override fun getItemLayoutID(viewType: Int): Int {
        return when (viewType) {
            ITEM_VIEW_TYPE_NORMAL -> {
                R.layout.item_main_left_device_list_layout
            }
            ITEM_VIEW_TYPE_TITLE -> {
                R.layout.item_loacl_device_title_layout
            }
            else -> {
                R.layout.item_main_left_device_list_layout
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