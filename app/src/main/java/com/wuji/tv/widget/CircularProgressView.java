package com.wuji.tv.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import androidx.annotation.ColorRes;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.wuji.tv.R;


public class CircularProgressView extends View {

    private Paint mBackPaint, mProgressPaint;
    private RectF mRectF;
    private int[] mColorArray;
    private int mProgress;

    public CircularProgressView(Context context) {
        this(context, null);
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircularProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        @SuppressLint("Recycle")
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircularProgressView);

        mBackPaint = new Paint();
        mBackPaint.setStyle(Paint.Style.STROKE);
        mBackPaint.setStrokeCap(Paint.Cap.ROUND);
        mBackPaint.setAntiAlias(true);
        mBackPaint.setDither(true);
        mBackPaint.setStrokeWidth(typedArray.getDimension(R.styleable.CircularProgressView_backWidth, 5));
        mBackPaint.setColor(typedArray.getColor(R.styleable.CircularProgressView_backColor, Color.LTGRAY));

        // 初始化进度圆环画笔
        mProgressPaint = new Paint();
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeCap(Paint.Cap.ROUND);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setDither(true);
        mProgressPaint.setStrokeWidth(typedArray.getDimension(R.styleable.CircularProgressView_progressWidth, 10));
        mProgressPaint.setColor(typedArray.getColor(R.styleable.CircularProgressView_progressColor, Color.BLUE));

        // 初始化进度圆环渐变色
        int startColor = typedArray.getColor(R.styleable.CircularProgressView_progressStartColor, -1);
        int firstColor = typedArray.getColor(R.styleable.CircularProgressView_progressFirstColor, -1);
        if (startColor != -1 && firstColor != -1) mColorArray = new int[]{startColor, firstColor};
        else mColorArray = null;

        mProgress = typedArray.getInteger(R.styleable.CircularProgressView_progress, 0);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int viewWide = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int viewHigh = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int mRectLength = (int) ((Math.min(viewWide, viewHigh)) - (mBackPaint.getStrokeWidth() > mProgressPaint.getStrokeWidth() ? mBackPaint.getStrokeWidth() : mProgressPaint.getStrokeWidth()));
        int mRectL = getPaddingLeft() + (viewWide - mRectLength) / 2;
        int mRectT = getPaddingTop() + (viewHigh - mRectLength) / 2;
        mRectF = new RectF(mRectL, mRectT, mRectL + mRectLength, mRectT + mRectLength);

        // 设置进度圆环渐变色
        if (mColorArray != null && mColorArray.length > 1)
            mProgressPaint.setShader(new LinearGradient(0, 0, 0, getMeasuredWidth(), mColorArray, null, Shader.TileMode.MIRROR));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawArc(mRectF, 0, 360, false, mBackPaint);
        canvas.drawArc(mRectF, 275, 360 * mProgress / 100, false, mProgressPaint);
    }

    // ---------------------------------------------------------------------------------------------


    public int getProgress() {
        return mProgress;
    }


    public void setProgress(int progress) {
        this.mProgress = progress;
        invalidate();
    }


    public void setProgress(int progress, long animTime) {
        if (animTime <= 0) setProgress(progress);
        else {
            ValueAnimator animator = ValueAnimator.ofInt(mProgress, progress);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mProgress = (int) animation.getAnimatedValue();
                    invalidate();
                }
            });
            animator.setInterpolator(new OvershootInterpolator());
            animator.setDuration(animTime);
            animator.start();
        }
    }


    public void setBackWidth(int width) {
        mBackPaint.setStrokeWidth(width);
        invalidate();
    }


    public void setBackColor(@ColorRes int color) {
        mBackPaint.setColor(ContextCompat.getColor(getContext(), color));
        invalidate();
    }


    public void setProgressWidth(int width) {
        mProgressPaint.setStrokeWidth(width);
        invalidate();
    }


    public void setProgressColorRes(@ColorRes int color) {
        int c = ContextCompat.getColor(getContext(), color);
        setProgressColor(c);
    }
    public void setProgressColor(int color) {
        mProgressPaint.setColor(color);
        mProgressPaint.setShader(null);
        invalidate();
    }

    public void setProgressColorRes(@ColorRes int startColor, @ColorRes int firstColor) {
        mColorArray = new int[]{ContextCompat.getColor(getContext(), startColor), ContextCompat.getColor(getContext(), firstColor)};
        mProgressPaint.setShader(new LinearGradient(0, 0, 0, getMeasuredWidth(), mColorArray, null, Shader.TileMode.MIRROR));
        invalidate();
    }


    public void setProgressColorRes(@ColorRes int[] colorArray) {
        if (colorArray == null || colorArray.length < 2) return;
        mColorArray = new int[colorArray.length];
        for (int index = 0; index < colorArray.length; index++)
            mColorArray[index] = ContextCompat.getColor(getContext(), colorArray[index]);
        mProgressPaint.setShader(new LinearGradient(0, 0, 0, getMeasuredWidth(), mColorArray, null, Shader.TileMode.MIRROR));
        invalidate();
    }
}
