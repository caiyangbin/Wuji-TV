package com.wuji.tv.ui.adapter

import android.view.View
import kotlinx.android.synthetic.main.item_apps.view.*
import com.wuji.tv.R
import com.wuji.tv.model.AppsModel


class AppsAdapter(apps: ArrayList<AppsModel>?) : com.wuji.tv.common.BaseRvAdapter<AppsModel>(apps) {
    override fun setData(view: View, data: AppsModel?, position: Int) {
//        view.itemRoot.setCardBackgroundColor(Color.parseColor("#171616"))
        view.tvTitle.text = data?.title
        view.ivIcon.setImageDrawable(data?.icon)
    }

    override fun setFocusData(view: View, data: AppsModel?, position: Int) {
//        view.itemRoot.setCardBackgroundColor(Color.WHITE)
//        view.tvTitle.setTextColor(Color.BLACK)
//        ViewCompat.animate(view).scaleX(1.15f).scaleY(1.15f).translationZ(1.15f).setDuration(100).start()
    }

    override fun setNoFocusData(view: View, data: AppsModel?, position: Int) {
//        view.itemRoot.setCardBackgroundColor(Color.parseColor("#171616"))
//        view.tvTitle.setTextColor(Color.WHITE)
//        ViewCompat.animate(view).scaleX(1f).scaleY(1f).translationZ(1.0f).setDuration(100).start()
    }

    override fun getItemLayoutID(viewType: Int): Int = R.layout.item_apps
}