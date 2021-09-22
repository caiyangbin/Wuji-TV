package com.wuji.tv.ui.adapter

import android.graphics.Color
import android.view.View
import kotlinx.android.synthetic.main.item_path_list_layout.view.*
import com.wuji.tv.R
import com.wuji.tv.model.LocalFile


class SelectPathListAdapter(devices: ArrayList<LocalFile>?) :
    com.wuji.tv.common.BaseRvAdapter<LocalFile>(devices) {

    override fun setData(view: View, data: LocalFile?, position: Int) {
        with(view) {
            itemRoot.setBackgroundResource(R.drawable.shape_round_corner_item_bg)
            tvName.setTextColor(Color.WHITE)
            tvPath.setTextColor(Color.WHITE)
            tvTotal.setTextColor(Color.WHITE)
            tvAvailable.setTextColor(Color.WHITE)
            tvName.text = data?.name
            tvPath.text = data?.path
            tvTotal.text = "总空间：${data?.total}"
            tvAvailable.text = "可用空间：${data?.available}"
            ivIcon.setImageResource(R.mipmap.store_normal)
        }
    }

    override fun setFocusData(view: View, data: LocalFile?, position: Int) {
        with(view) {
            itemRoot.setBackgroundResource(R.drawable.shape_round_corner_item_bg_focus)
            tvName.setTextColor(Color.BLACK)
            tvPath.setTextColor(Color.BLACK)
            tvTotal.setTextColor(Color.BLACK)
            tvAvailable.setTextColor(Color.BLACK)
            ivIcon.setImageResource(R.mipmap.store_focus)
        }
    }

    override fun setNoFocusData(view: View, data: LocalFile?, position: Int) {
        with(view) {
            itemRoot.setBackgroundResource(R.drawable.shape_round_corner_item_bg)
            tvName.setTextColor(Color.WHITE)
            tvPath.setTextColor(Color.WHITE)
            tvTotal.setTextColor(Color.WHITE)
            tvAvailable.setTextColor(Color.WHITE)
            ivIcon.setImageResource(R.mipmap.store_normal)
        }
    }

    override fun getItemLayoutID(viewType:Int): Int = R.layout.item_path_list_layout

}