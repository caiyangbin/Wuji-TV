package com.wuji.tv.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wuji.tv.R
import com.wuji.tv.model.Files
import kotlinx.android.synthetic.main.item_detail_tv_series.view.*

class DetailTvSeriesAdapter() : BaseQuickAdapter<Files, BaseViewHolder>(R.layout.item_detail_tv_series) {


    override fun convert(holder: BaseViewHolder, item: Files) {
        holder.itemView.tabNameTextView.text = "${getItemPosition(item)+1}"
    }

}