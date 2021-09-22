package com.wuji.tv.widget

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager

class MyGridLayoutManager(context: Context,spanCount: Int) :GridLayoutManager(context,spanCount) {
    override fun onInterceptFocusSearch(focused: View, direction: Int): View? {
        try {
            val lastVisibleItemPos = findLastVisibleItemPosition()//最新的已显示的Item的位置
            var fromPos = getPosition(focused)
            when (direction) {
                View.FOCUS_UP -> {
                    fromPos = (fromPos - spanCount)
                }
                View.FOCUS_DOWN -> {
                    fromPos += spanCount
                }
                View.FOCUS_RIGHT -> {
                    fromPos++
                }
                View.FOCUS_LEFT -> {
                    fromPos--
                }
            }
            if (fromPos < 0 ||fromPos  >= itemCount ) {
                return super.onInterceptFocusSearch(focused, direction)
            } else {
                if (fromPos > lastVisibleItemPos) {
                    scrollToPosition(if(fromPos-spanCount < 0) fromPos else fromPos-spanCount)

                }
            }
        }catch (e:Exception){

        }
        return super.onInterceptFocusSearch(focused, direction)
    }
}