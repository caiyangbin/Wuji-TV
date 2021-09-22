package com.wuji.tv.widget

import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import java.util.*


class ScaleRecyclerView : RecyclerView {

    private var mCanFocusOutVertical = true

    private var mCanFocusOutHorizontal = true

    private var mFocusLostListener: FocusLostListener? = null

    private var mFocusGainListener: FocusGainListener? = null

    private var mOnFocusChangeListener: OnFocusChangeListener? = null

    private var mLastFocusPosition = -1
    var requestFocus = true

    constructor(mContext: Context) : this(mContext, null)

    constructor(mContext: Context, attrs: AttributeSet?) : this(mContext, attrs!!, 0)

    constructor(mContext: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        mContext,
        attrs,
        defStyleAttr
    ) {
        isChildrenDrawingOrderEnabled = true
        descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        itemAnimator = null
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            this.isFocusable = true
        } else {
            this.focusable = View.FOCUSABLE
        }
    }


    fun isCanFocusOutHorizontal(): Boolean {
        return mCanFocusOutHorizontal
    }

    fun isCanFocusOutVertical(): Boolean {
        return mCanFocusOutVertical
    }

    fun setCanFocusOutVertical(canFocusOutVertical: Boolean) {
        mCanFocusOutVertical = canFocusOutVertical
    }

    fun getLastFocusPosition(): Int {
        return mLastFocusPosition
    }

    fun setLastFocusPosition(position: Int) {
        mLastFocusPosition = position
    }

    fun setCanFocusOutHorizontal(canFocusOutHorizontal: Boolean) {
        mCanFocusOutHorizontal = canFocusOutHorizontal
    }

    fun setFocusLostListener(focusLostListener: FocusLostListener?) {
        mFocusLostListener = focusLostListener
    }

    interface FocusLostListener {
        fun onFocusLost(lastFocusChild: View?, direction: Int)
    }

    fun setGainFocusListener(focusListener: FocusGainListener?) {
        mFocusGainListener = focusListener
    }


    interface FocusGainListener {
        fun onFocusGain(child: View?, focued: View?)
    }

    interface OnFocusChangeListener{
        fun onFocusChange(position: Int)
    }

    fun setOnFocusChangeListener(onClickListener: OnFocusChangeListener){
        mOnFocusChangeListener = onClickListener
    }

    override fun focusSearch(focused: View?, direction: Int): View? {
        val view = super.focusSearch(focused, direction)
        if (focused == null) {
            return view
        }
        if (view != null) {
            val nextFocusItemView = findContainingItemView(view)
            if (nextFocusItemView == null) {
                if (!mCanFocusOutVertical && (direction == FOCUS_DOWN || direction == FOCUS_UP)) {
                    return focused
                }
                if (!mCanFocusOutHorizontal && (direction == FOCUS_LEFT || direction == FOCUS_RIGHT)) {
                    return focused
                }
                mFocusLostListener?.onFocusLost(focused, direction)
                return view
            }
        }
        return view
    }

    override fun addFocusables(views: ArrayList<View>?, direction: Int, focusableMode: Int) {
        val view = layoutManager?.findViewByPosition(mLastFocusPosition)
        if (hasFocus() || mLastFocusPosition < 0 || view == null) {
            super.addFocusables(views, direction, focusableMode)
        } else if (view.isFocusable) {
            views?.add(view)
        } else {
            super.addFocusables(views, direction, focusableMode)
        }
    }

    override fun requestChildFocus(child: View?, focused: View?) {
        val hasFocus = hasFocus()
        if (!hasFocus) {
            mFocusGainListener?.onFocusGain(child, focused)
        }
        super.requestChildFocus(child, focused)
        if(null != child){
            mLastFocusPosition = getChildViewHolder(child).adapterPosition
            if(hasFocus){
                mOnFocusChangeListener?.onFocusChange(mLastFocusPosition)
            }
        }
    }

    override fun requestFocus(direction: Int, previouslyFocusedRect: Rect?): Boolean {
        if(mLastFocusPosition == -1){
            return super.requestFocus(direction, previouslyFocusedRect)
        }
        val lastFocusedView = layoutManager?.findViewByPosition(mLastFocusPosition)
        lastFocusedView?.requestFocus()
        return false
    }

}