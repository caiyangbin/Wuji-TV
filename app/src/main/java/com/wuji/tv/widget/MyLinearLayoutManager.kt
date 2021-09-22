package com.wuji.tv.widget

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.Exception

class MyLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        try {
            super.onLayoutChildren(recycler, state)
        }catch (e:Exception){

        }
    }
}