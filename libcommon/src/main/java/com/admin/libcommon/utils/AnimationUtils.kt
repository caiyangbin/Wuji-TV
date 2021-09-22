package com.admin.libcommon.utils

import android.animation.ValueAnimator
import android.view.View
import com.bin.baselibrary.ext.remove
import com.bin.baselibrary.ext.show

/**
 * Create by admin on 2020/1/9-14:29
 */

/**开始呼吸动画，传入需要做动画的[view],并返回[ValueAnimator]*/
fun startBreatheAnimation(vararg view: View): ValueAnimator {
    return ValueAnimator.ofFloat(1.5f, 1.3f).apply {
        addUpdateListener {
            val value = it.animatedValue as Float
            view.forEach { childView ->
                childView.scaleX = value
                childView.scaleY = value
            }
        }
        duration = 1600
        //插值器，暂时不用
//        interpolator = BounceInterpolator()
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
        startDelay = 1000
        start()
    }
}

/**开始呼吸动画，传入需要做动画的[view],并返回[ValueAnimator]*/
fun startMenuBreatheAnimation(vararg view: View): ValueAnimator {
    return ValueAnimator.ofFloat(1.2f, 0.9f).apply {
        addUpdateListener {
            val value = it.animatedValue as Float
            view.forEach { childView ->
                childView.scaleX = value
                childView.scaleY = value
            }
        }
        duration = 1600
        //插值器，暂时不用
//        interpolator = BounceInterpolator()
        repeatMode = ValueAnimator.REVERSE
        repeatCount = ValueAnimator.INFINITE
        startDelay = 1000
        start()
    }
}

/**开始主页显示菜单栏动画，传入需要做动画的[view],并返回[ValueAnimator]*/
inline fun showMenuAnimation(vararg view: View, crossinline callback:()->Unit): ValueAnimator {
    return ValueAnimator.ofFloat(500f, 0f).apply {
        addUpdateListener {
            val value = it.animatedValue as Float
            if (value == 500f) {
                callback.invoke()
            }
            view.forEach { childView ->
                childView.translationY = value
                if (value == 500f) {
                    childView.show()
                }
            }
        }
        duration = 400
        start()
    }
}



/**开始主页隐藏菜单栏动画，传入需要做动画的[view],并返回[ValueAnimator]*/
inline fun hideMenuAnimation(vararg view: View, crossinline callback:()->Unit): ValueAnimator {
    return ValueAnimator.ofFloat(0f, 500f).apply {
        addUpdateListener {
            val value = it.animatedValue as Float
            if (value == 500f) {
                callback.invoke()
            }
            view.forEach { childView ->
                childView.translationY = value
                if (value == 500f) {
                    childView.remove()
                    callback.invoke()
                }
            }
        }
        duration = 400
        start()
    }
}

