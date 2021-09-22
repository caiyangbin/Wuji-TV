package com.wuji.tv.ui.adapter

import android.graphics.Color
import android.view.View
import androidx.core.view.ViewCompat
import kotlinx.android.synthetic.main.item_main_page_list.view.*
import com.wuji.tv.R
import com.wuji.tv.model.PageItem


class MainPageListAdapter(pageItems: ArrayList<PageItem>?) : com.wuji.tv.common.BaseRvAdapter<PageItem>(pageItems) {

    override fun setData(view: View, data: PageItem?, position: Int) {
        view.tvTitle.text = data?.title
        when (position) {
//            0 -> view.icon.setImageResource(R.mipmap.quanzi_normal)
            0 -> view.icon.setImageResource(R.mipmap.cloud)
            1 -> view.icon.setImageResource(R.mipmap.file_normal)
            2 -> view.icon.setImageResource(R.mipmap.app_normal)
//            4 -> view.icon.setImageResource(R.mipmap.options_normal)
            3 -> view.icon.setImageResource(R.mipmap.settings_norml)
        }
    }

    override fun setFocusData(view: View, data: PageItem?, position: Int) {
        ViewCompat.animate(view).scaleX(1.3f).scaleY(1.3f).translationZ(1.3f).setDuration(100)
            .start()
        view.tvTitle.setTextColor(Color.WHITE)
        when (position) {
//            0 -> view.icon.setImageResource(R.mipmap.quanzi_focus)
            0 -> view.icon.setImageResource(R.mipmap.cloud_focus)
            1 -> view.icon.setImageResource(R.mipmap.file_icon)
            2 -> view.icon.setImageResource(R.mipmap.app_focus)
//            4 -> view.icon.setImageResource(R.mipmap.options_focus)
            3 -> view.icon.setImageResource(R.mipmap.settings)
        }
    }

    override fun setNoFocusData(view: View, data: PageItem?, position: Int) {
        ViewCompat.animate(view).scaleX(1.0f).scaleY(1.0f).translationZ(1.0f).setDuration(100)
            .start()
        view.tvTitle.setTextColor(Color.parseColor("#8a8a8a"))
        when (position) {
//            0 -> view.icon.setImageResource(R.mipmap.quanzi_normal)
            0 -> view.icon.setImageResource(R.mipmap.cloud)
            1 -> view.icon.setImageResource(R.mipmap.file_normal)
            2 -> view.icon.setImageResource(R.mipmap.app_normal)
//            4 -> view.icon.setImageResource(R.mipmap.options_normal)
            3 -> view.icon.setImageResource(R.mipmap.settings_norml)
        }
    }

    override fun getItemLayoutID(viewType:Int): Int = R.layout.item_main_page_list
}