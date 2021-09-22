package com.wuji.tv.ui.adapter

import android.annotation.SuppressLint
import android.view.View
import kotlinx.android.synthetic.main.item_local_device.view.*
import com.wuji.tv.Constants.Companion.ITEM_VIEW_TYPE_NORMAL
import com.wuji.tv.Constants.Companion.ITEM_VIEW_TYPE_TITLE
import com.wuji.tv.R
import com.wuji.tv.model.LocalFile
import kotlinx.android.synthetic.main.item_loacl_device_title.view.*
import org.jetbrains.anko.backgroundResource


class LocalDeviceAdapter(devices: ArrayList<LocalFile>?) :
    com.wuji.tv.common.BaseRvAdapter<LocalFile>(devices) {

    @SuppressLint("SetTextI18n")
    override fun setData(view: View, data: LocalFile?, position: Int) {
        data?.apply {
            if (isTitle) {
                with(view) {
                    tvTitle.text = data.name
//                    tvTitleDown.text = "直接访问本地存储设备和资源"
                    tvTitleDown.visibility = View.GONE
                }
            } else {
//                view.cardView.setCardBackgroundColor(Color.parseColor("#8a8a8a"))
//                view.tvName.setTextColor(Color.WHITE)
                view.tvName.text = data.name
                view.tvContent.text = "总大小：${data.total}"
                view.tvContent2.text = "可用：${data.available}"
                view.ivIcon.setImageResource(R.mipmap.ic_local_device)
                view.cardView.backgroundResource = R.drawable.bg_app_unselected
            }
        }
    }

    override fun setFocusData(view: View, data: LocalFile?, position: Int) {
        data?.apply {
            if (!isTitle) {
                view.cardView.backgroundResource = R.drawable.bg_tab_left_selected
//                view.cardView.setCardBackgroundColor(Color.WHITE)
//                view.tvName.setTextColor(Color.BLACK)
//                view.tvContent.setTextColor(Color.BLACK)
//                view.ivIcon.setImageResource(R.mipmap.local_device_focus)
//                ViewCompat.animate(view).scaleX(1.1f).scaleY(1.1f).translationZ(1.1f)
//                    .setDuration(100)
//                    .start()
            }
        }
    }

    override fun setNoFocusData(view: View, data: LocalFile?, position: Int) {
        data?.apply {
            if (!isTitle) {
                view.cardView.backgroundResource = R.drawable.bg_app_unselected
//                view.cardView.setCardBackgroundColor(Color.parseColor("#8a8a8a"))
//                view.tvName.setTextColor(Color.WHITE)
//                view.tvContent.setTextColor(Color.WHITE)
//                view.ivIcon.setImageResource(R.mipmap.local_device_normal)
//                ViewCompat.animate(view).scaleX(1f).scaleY(1f).translationZ(1f).setDuration(100)
//                    .start()
            }
        }
    }

    override fun getItemLayoutID(viewType: Int): Int {
        return when (viewType) {
            ITEM_VIEW_TYPE_NORMAL -> {
                R.layout.item_local_device
            }
            ITEM_VIEW_TYPE_TITLE -> {
                R.layout.item_loacl_device_title
            }
            else -> {
                R.layout.item_local_device
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