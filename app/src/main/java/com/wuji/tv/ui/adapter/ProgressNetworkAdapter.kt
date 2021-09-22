package com.wuji.tv.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wuji.tv.R
import kotlinx.android.synthetic.main.item_progress_network.view.*

class ProgressNetworkAdapter() : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_progress_network) {

    override fun convert(holder: BaseViewHolder, item: String) {
        holder.itemView.tabNameTextView.text = item
    }

}