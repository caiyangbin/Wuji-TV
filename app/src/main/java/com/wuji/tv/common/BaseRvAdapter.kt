package com.wuji.tv.common

import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.admin.libcommon.ext.isNotNullOrEmpty
import com.wuji.tv.Constants.Companion.ITEM_VIEW_TYPE_NORMAL

abstract class BaseRvAdapter<T> constructor(private var mDatas: ArrayList<T>?) :
    RecyclerView.Adapter<com.wuji.tv.common.BaseRvAdapter.BaseHolder>() {

    protected lateinit var mContext: Context
    private var itemClickListener: ((View, Int, T?) -> Unit)? = null
    private var itemClickListenerForHolder: ((com.wuji.tv.common.BaseRvAdapter.BaseHolder, Int, T?) -> Unit)? = null
    private var itemLongClickListener: ((View, Int, T?) -> Boolean)? = null
    private var itemFocusChangeListener: ((View, Boolean, Int) -> Unit)? = null
    private var itemSelectListener: ((View, Int, T?) -> Unit)? = null
    private var itemKeyListener: ((View, Int, KeyEvent, T?, Int) -> Boolean)? = null
    private var currentPosition = 0
    protected var mIsScroll = false


    fun setOnItemClickListener(listener: (View, Int, T?) -> Unit) {
        this.itemClickListener = listener
    }

    fun setOnItemClickListenerForHolder(listener: (com.wuji.tv.common.BaseRvAdapter.BaseHolder, Int, T?) -> Unit) {
        this.itemClickListenerForHolder = listener
    }

    fun setOnItemLongClickListener(listener: (View, Int, T?) -> Boolean) {
        this.itemLongClickListener = listener
    }

    fun setOnItemFocusChangeListener(listener: (View, Boolean, Int) -> Unit) {
        this.itemFocusChangeListener = listener
    }

    fun setOnItemSelectListener(listener: (View, Int, T?) -> Unit) {
        this.itemSelectListener = listener
    }

    fun setOnItemKeyListener(listener: (View, Int, KeyEvent, T?, Int) -> Boolean) {
        this.itemKeyListener = listener
    }

    fun getCurrentPosition(): Int {
        return currentPosition
    }

    init {
        this.setHasStableIds(true)
    }

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {

        with(holder.itemView) {
            var t: T? = null
            if (mDatas.isNotNullOrEmpty()) {
                t = mDatas!![position]
            }
            setData(this, t, position)
            this.setOnClickListener {
                itemClickListener?.invoke(it, position, t)
                itemClickListenerForHolder?.invoke(holder, position, t)
            }
            this.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    currentPosition = position
                    this.setOnKeyListener { v, keyCode, event ->
                        if (itemKeyListener == null) {
                            return@setOnKeyListener false
                        } else {
                            itemKeyListener!!.invoke(v, keyCode, event, t, position)
                        }
                    }
                    itemSelectListener?.invoke(v, position, t)
                    this.setOnLongClickListener {
                        if (itemLongClickListener == null) {
                            return@setOnLongClickListener false
                        }
                        return@setOnLongClickListener itemLongClickListener!!.invoke(
                            it,
                            position,
                            t
                        )
                    }
                    setFocusData(this, t, position)
                } else {
                    setNoFocusData(this, t, position)
                }
                itemFocusChangeListener?.invoke(v, hasFocus, position)
            }
        }
    }

    abstract fun setData(view: View, data: T?, position: Int)

    abstract fun setFocusData(view: View, data: T?, position: Int)

    abstract fun setNoFocusData(view: View, data: T?, position: Int)

    fun setScrollStatus(isScroll: Boolean) {
        this.mIsScroll = isScroll
        if (!isScroll) {
            notifyDataSetChanged()
        }
    }

    fun setDatas(datas: ArrayList<T>?) {
        if (datas.isNotNullOrEmpty()) {
            this.mDatas = datas
        }
    }

    fun setDatasAndRefresh(datas: ArrayList<T>?) {
        if (datas.isNotNullOrEmpty()) {
            this.mDatas = datas
            notifyDataSetChanged()
        }
    }

    fun getDatas(): ArrayList<T> {
        return this.mDatas!!
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): com.wuji.tv.common.BaseRvAdapter.BaseHolder {
        mContext = parent.context
        return BaseHolder(
            LayoutInflater.from(mContext).inflate(
                getItemLayoutID(viewType),
                parent,
                false
            )
        )
    }


    override fun getItemCount(): Int = if (mDatas.isNotNullOrEmpty()) mDatas!!.size else 0

    abstract fun getItemLayoutID(viewType: Int = ITEM_VIEW_TYPE_NORMAL): Int

    override fun getItemViewType(position: Int): Int {
        return ITEM_VIEW_TYPE_NORMAL
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class BaseHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}