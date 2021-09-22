package com.wuji.tv.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.wuji.tv.R


class CircleIndicatorLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : ViewGroup(context, attrs, defStyleAttr) {
    private val circleWidth = 20
    private val circleHeight = 20
    private val circlePadding = 10
    private var currentIndex: Int = 0

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        measureChildren(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
                getDefaultSize(suggestedMinimumHeight, heightMeasureSpec))
    }

    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        val childrenWidth = circleWidth+circlePadding*2
        var left:Int
        var top:Int
        var right:Int
        var bottom:Int
        var layoutWidth = (width-childrenWidth*childCount)/2

        var layoutHeight = (height-circleHeight)/2

        var maxChildHeight = 0

        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (layoutWidth < width) {
                left = layoutWidth+(childrenWidth-circleWidth)/2
                right = left + circleWidth
                top = layoutHeight
                bottom = top + circleHeight
            } else {
                layoutWidth = 0
                layoutHeight += maxChildHeight
                maxChildHeight = 0
                left = layoutWidth+(childrenWidth-circleWidth)/2
                right = left + circleWidth
                top = layoutHeight
                bottom = top + circleHeight
            }
            layoutWidth += childrenWidth
            if (circleHeight > maxChildHeight) {
                maxChildHeight = circleHeight
            }

            child.layout(left, top, right, bottom)
        }
    }

    fun setCount(count: Int, position: Int){
        if(count <= 1) return
        currentIndex = position
        removeAllViews()
        for (i in 0 until count) {
            val view = View(context)
            view.measure(circleWidth, circleHeight)
            view.setBackgroundResource(R.drawable.bg_circle_indicator)
            view.isEnabled = position==i
            addView(view)
        }
    }

    fun setCurrentIndex(position: Int){
        if(childCount == 0) return
        getChildAt(currentIndex).isEnabled = false
        getChildAt(position).isEnabled = true
        currentIndex = position
    }
}