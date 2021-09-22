package com.wuji.tv.ui.adapter

import android.graphics.Color
import android.view.View
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.item_navigation.view.*
import com.wuji.tv.R
import com.wuji.tv.model.NavigationModel


class DeviceNavigationAdapter(private val items: ArrayList<NavigationModel>?) :
    com.wuji.tv.common.BaseRvAdapter<NavigationModel>(items) {

    override fun setData(view: View, data: NavigationModel?, position: Int) {
        view.tvTitle.text = data?.name
        view.tvTitle.setTextColor(Color.WHITE)
        view.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun setFocusData(view: View, data: NavigationModel?, position: Int) {
        view.tvTitle.setTextColor(Color.BLACK)
        view.setBackgroundResource(R.drawable.shape_round_corner_item_navigation_bg)
        ViewCompat.animate(view).scaleX(1.1f).scaleY(1.1f).translationZ(1.1f).setDuration(100)
            .start()
    }

    override fun setNoFocusData(view: View, data: NavigationModel?, position: Int) {
        view.tvTitle.setTextColor(Color.WHITE)
        view.setBackgroundColor(Color.TRANSPARENT)
        ViewCompat.animate(view).scaleX(1f).scaleY(1f).translationZ(1f).setDuration(100)
            .start()
    }

    override fun getItemLayoutID(viewType: Int): Int = R.layout.item_navigation
}