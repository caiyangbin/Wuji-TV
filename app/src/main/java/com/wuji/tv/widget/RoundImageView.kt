package com.wuji.tv.widget

import android.content.Context
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import com.wuji.tv.R
import com.wuji.tv.utils.BitmapUtils

class RoundImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ImageView(context, attrs, defStyleAttr) {
    private val mRoundedRectPath = Path()
    private var width = 0f
    private var height = 0f
    private var isClip = false
    private var typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView)
    private val mRadius = typedArray.getInteger(R.styleable.RoundImageView_radius, 10)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        width = measuredWidth * 1.0f
        height = measuredHeight * 1.0f
    }

    override fun setImageDrawable(drawable: Drawable?) {
        var mDrawable = drawable
        drawable?.let {
            mDrawable = BitmapUtils().roundDrawableByDrawable(
                drawable,
                width.toInt(),
                height.toInt(),
                mRadius,
                resources
            )
        }
        super.setImageDrawable(mDrawable)
    }

}