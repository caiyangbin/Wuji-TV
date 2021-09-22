package com.wuji.tv.widget

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.viewpager.widget.ViewPager

/**
 *
 *  1.禁止滑动切换item：也就是不拦截不处理触摸事件，onInterceptTouchEvent和onTouchEvent都返回false即可。
 *     dispatchKeyEvent返回false同理
 *
 *  2.去除切换时动画：两个参数的setCurrentItem第二个参数就是是否需要动画，一般我们调用的都是一个参数的setCurrentItem，
 *    所以直接让它调用无动画的切换方法即可。
 *
 * Create by admin on 2020/9/11-15:10
 */
class TvViewPager(context: Context, attributeSet: AttributeSet? = null) :
    ViewPager(context, attributeSet) {


    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return super.dispatchKeyEvent(event)
    }


    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item, false)
    }

}
