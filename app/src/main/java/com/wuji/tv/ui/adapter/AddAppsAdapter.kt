package com.wuji.tv.ui.adapter

import android.graphics.Color
import android.view.View
import kotlinx.android.synthetic.main.item_add_apps.view.*
import com.wuji.tv.R
import com.wuji.tv.model.AppsModel


class AddAppsAdapter(private val apps: ArrayList<AppsModel>?) : com.wuji.tv.common.BaseRvAdapter<AppsModel>(apps) {

    override fun setData(view: View, data: AppsModel?, position: Int) {
        view.tvTitle.text = data?.title
        view.ivIcon.setImageDrawable(data?.icon)
    }

    override fun setFocusData(view: View, data: AppsModel?, position: Int) {
        view.setBackgroundColor(Color.parseColor("#8a8a8a"))
        view.tvTitle.setTextColor(Color.WHITE)
    }

    override fun setNoFocusData(view: View, data: AppsModel?, position: Int) {
        view.setBackgroundColor(Color.TRANSPARENT)
        view.tvTitle.setTextColor(Color.BLACK)
    }

    override fun getItemLayoutID(viewType:Int): Int = R.layout.item_add_apps


}