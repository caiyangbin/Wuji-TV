package com.wuji.tv.ui.adapter

import android.graphics.Color
import android.view.View
import kotlinx.android.synthetic.main.item_file_menu_layout.view.*
import com.wuji.tv.R

class FileMenuListAdapter(devices: ArrayList<String>?) :
    com.wuji.tv.common.BaseRvAdapter<String>(devices) {

    override fun setData(view: View, data: String?, position: Int) {
        with(view) {
            tvName.text = data
            itemRoot.setBackgroundResource(R.drawable.shape_round_corner_item_bg)
            tvName.setTextColor(Color.WHITE)
            when (position) {
                0 -> {
                    ivIcon.setImageResource(R.mipmap.refresh)
                }
                1 -> {
                    ivIcon.setImageResource(R.mipmap.download)
                }
                2 -> {
                    ivIcon.setImageResource(R.mipmap.progress)
                }
                3 -> {
                    ivIcon.setImageResource(R.mipmap.store_normal)
                }
            }
        }

    }

    override fun setFocusData(view: View, data: String?, position: Int) {
        with(view) {
            itemRoot.setBackgroundResource(R.drawable.shape_round_corner_item_bg_focus)
            tvName.setTextColor(Color.BLACK)
            when (position) {
                0 -> {
                    ivIcon.setImageResource(R.mipmap.refresh_focus)
                }
                1 -> {
                    ivIcon.setImageResource(R.mipmap.download_focus)
                }
                2 -> {
                    ivIcon.setImageResource(R.mipmap.progress_focus)
                }
                3 -> {
                    ivIcon.setImageResource(R.mipmap.store_black)
                }
            }
        }
    }

    override fun setNoFocusData(view: View, data: String?, position: Int) {
        with(view) {
            itemRoot.setBackgroundResource(R.drawable.shape_round_corner_item_bg)
            tvName.setTextColor(Color.WHITE)
            when (position) {
                0 -> {
                    ivIcon.setImageResource(R.mipmap.refresh)
                }
                1 -> {
                    ivIcon.setImageResource(R.mipmap.download)
                }
                2 -> {
                    ivIcon.setImageResource(R.mipmap.progress)
                }
                3 -> {
                    ivIcon.setImageResource(R.mipmap.store_normal)
                }
            }
        }
    }

    override fun getItemLayoutID(viewType:Int): Int = R.layout.item_file_menu_layout

}