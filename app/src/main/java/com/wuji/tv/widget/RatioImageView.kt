package com.wuji.tv.widget

import android.content.Context
import android.graphics.Path
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import com.wuji.tv.R
import com.wuji.tv.utils.BitmapUtils


class RatioImageView : ImageView {

    private var mDrawableSizeRatio = -1f

    private var mIsWidthFitDrawableSizeRatio = false
    private var mIsHeightFitDrawableSizeRatio = false

    private var mWidthRatio = -1f

    private var mHeightRatio = -1f

    private val mRadius = 15f
    private val mRoundedRectPath = Path()
    private var width = 0f
    private var height = 0f
    private var isClip = false


    constructor(mContext: Context) : this(mContext, null)

    constructor(mContext: Context, attrs: AttributeSet?) : this(mContext, attrs!!, 0)

    constructor(mContext: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        mContext,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
        if (drawable != null) {
            mDrawableSizeRatio = (1f * drawable.intrinsicWidth
                    / drawable.intrinsicHeight)
        }

    }


    private fun init(attrs: AttributeSet) {
        val a = context.obtainStyledAttributes(
            attrs,
            R.styleable.RatioImageView
        )
        mIsWidthFitDrawableSizeRatio = a.getBoolean(
            R.styleable.RatioImageView_is_width_fix_drawable_size_ratio,
            mIsWidthFitDrawableSizeRatio
        )
        mIsHeightFitDrawableSizeRatio = a.getBoolean(
            R.styleable.RatioImageView_is_height_fix_drawable_size_ratio,
            mIsHeightFitDrawableSizeRatio
        )
        mHeightRatio = a.getFloat(
            R.styleable.RatioImageView_height_to_width_ratio, mHeightRatio
        )
        mWidthRatio = a.getFloat(
            R.styleable.RatioImageView_width_to_height_ratio, mWidthRatio
        )
        a.recycle()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        if (drawable != null) {
            mDrawableSizeRatio = (1f * drawable.intrinsicWidth
                    / drawable.intrinsicHeight)
            if (mDrawableSizeRatio > 0
                && (mIsWidthFitDrawableSizeRatio || mIsHeightFitDrawableSizeRatio)
            ) {
                requestLayout()
            }
        }
    }

    override fun setImageDrawable(drawable: Drawable?) {
        var mDrawable = drawable
       drawable?.let {
           mDrawable = BitmapUtils().roundDrawableByDrawable(drawable,width.toInt(),height.toInt(),15,resources)
       }
        super.setImageDrawable(mDrawable)
        if (getDrawable() != null) {
            mDrawableSizeRatio = (1f * getDrawable().intrinsicWidth
                    / getDrawable().intrinsicHeight)
            if (mDrawableSizeRatio > 0
                && (mIsWidthFitDrawableSizeRatio || mIsHeightFitDrawableSizeRatio)
            ) {
                requestLayout()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mDrawableSizeRatio > 0) {
            if (mIsWidthFitDrawableSizeRatio) {
                mWidthRatio = mDrawableSizeRatio
            } else if (mIsHeightFitDrawableSizeRatio) {
                mHeightRatio = 1 / mDrawableSizeRatio
            }
        }

        if (mHeightRatio > 0 && mWidthRatio > 0) {
            throw RuntimeException("高度和宽度不能同时设置百分比！！")
        }

        if (mWidthRatio > 0) {
            val height = MeasureSpec.getSize(heightMeasureSpec)
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(
                    (height * mWidthRatio).toInt(), MeasureSpec.EXACTLY
                ),
                MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
            )
        } else if (mHeightRatio > 0) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            super.onMeasure(
                MeasureSpec.makeMeasureSpec(
                    width,
                    MeasureSpec.EXACTLY
                ), MeasureSpec.makeMeasureSpec(
                    (width * mHeightRatio).toInt(), MeasureSpec.EXACTLY
                )
            )
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
        width = measuredWidth * 1.0f
        height = measuredHeight * 1.0f
    }
}