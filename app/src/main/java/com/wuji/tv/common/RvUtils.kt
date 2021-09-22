package com.wuji.tv.common

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

fun requestFocusForPosition(
    rv: RecyclerView,
    manager: LinearLayoutManager,
    position: Int = 0,
    offset: Int = 0,
    currentPosition: Int = 0
) {
    manager.scrollToPositionWithOffset(position, offset)
    rv.post{
        manager.findViewByPosition(currentPosition)?.apply {
            requestFocusFromTouch()
            requestFocus()
        }
    }
}

fun LinearLayoutManager.setItemFocus(focus: Boolean) {
    for (i in 0..this.childCount) {
        this.getChildAt(i)?.apply {
            post {
                this.isFocusable = focus
            }
        }
    }
}


fun LinearLayoutManager.setCurrentFocus(currentIndex: Int) {
    findViewByPosition(currentIndex)?.apply {
        post {
            requestFocus()
            requestFocusFromTouch()
        }
    }
}

