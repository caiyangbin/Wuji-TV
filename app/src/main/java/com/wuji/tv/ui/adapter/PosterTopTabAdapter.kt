package com.wuji.tv.ui.adapter

import android.os.Build
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wuji.tv.R
import com.wuji.tv.model.TopWithLeftTabModel
import kotlinx.android.synthetic.main.item_poster_tab.view.*

class PosterTopTabAdapter() : BaseQuickAdapter<TopWithLeftTabModel, BaseViewHolder>(R.layout.item_poster_tab) {

    private var mLastFocusPosition = 0
    private var mOnFocusChangeListener: OnFocusChangeListener? = null

    fun setOnFocusChangeListener(onFocusChangeListener: OnFocusChangeListener) {
        mOnFocusChangeListener = onFocusChangeListener
    }

    fun getLastFocusPosition(): Int {
        return mLastFocusPosition
    }

    fun setLastFocusPosition(position: Int) {
        mLastFocusPosition = position
    }

    interface OnFocusChangeListener{
        fun onFocusChange(position: Int)
    }

    override fun convert(holder: BaseViewHolder, item: TopWithLeftTabModel) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            holder.itemView.isFocusable = true
        } else {
            holder.itemView.focusable = View.FOCUSABLE
        }
        holder.itemView.tabNameTextView.text = item.topTabModel.name
        holder.itemView.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) mOnFocusChangeListener?.onFocusChange(getItemPosition(item))
        }
    }

}