package com.wuji.tv.ui.adapter

import android.os.Build
import android.view.View
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.wuji.tv.R
import com.wuji.tv.model.LeftTabModel
import kotlinx.android.synthetic.main.item_poster_left_tab.view.*

class PosterLeftTabAdapter : BaseQuickAdapter<LeftTabModel, BaseViewHolder>(R.layout.item_poster_left_tab) {

    private var selectedPosition = -1

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

    fun setSelectedPosition(position: Int){
        selectedPosition = position
    }

    interface OnFocusChangeListener{
        fun onFocusChange(position: Int)
    }

    override fun convert(holder: BaseViewHolder, item: LeftTabModel) {
        holder.itemView.tabNameTextView.text = item.name
        if(getItemPosition(item) == selectedPosition){
            holder.itemView.requestFocus()
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            holder.itemView.isFocusable = true
        } else {
            holder.itemView.focusable = View.FOCUSABLE
        }
        holder.itemView.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus) mOnFocusChangeListener?.onFocusChange(getItemPosition(item))
        }
    }

}