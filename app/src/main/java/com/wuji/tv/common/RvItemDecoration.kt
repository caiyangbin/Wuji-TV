package com.wuji.tv.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

val deviceItemDecoration: RecyclerView.ItemDecoration by lazy {
    object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.set(0, 0, 0, 1)
        }
    }
}

val fileListItemDecoration: RecyclerView.ItemDecoration by lazy {
    object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.set(0, 10, 0, 0)
        }
    }
}


val appsDecoration: RecyclerView.ItemDecoration by lazy {
    object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.set(22, 22, 22, 22)
        }
    }
}

val discoveryDecoration: RecyclerView.ItemDecoration by lazy {
    object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.set(0, 10, 0, 10)
        }
    }
}

val navigationDecoration: RecyclerView.ItemDecoration by lazy {
    object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.set(10, 0, 10, 0)
        }
    }
}

val deviceDecoration: RecyclerView.ItemDecoration by lazy {
    object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.set(30, 30, 30, 30)
        }
    }
}

val posterDecoration: RecyclerView.ItemDecoration by lazy {
    object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.set(10, 10, 10, 10)
        }
    }
}

val deviceHomeDecoration: RecyclerView.ItemDecoration by lazy {
    object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.set(15, 15, 15, 15)
        }
    }
}

val deviceManageDecoration: RecyclerView.ItemDecoration by lazy {
    object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            outRect.set(30, 30, 30, 30)
        }
    }
}
