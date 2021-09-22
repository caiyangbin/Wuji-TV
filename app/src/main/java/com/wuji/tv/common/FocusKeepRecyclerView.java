package com.wuji.tv.common;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FocusKeepRecyclerView extends RecyclerView {

    private static final String TAG = FocusKeepRecyclerView.class.getSimpleName();
    private boolean mCanFocusOutVertical = true;
    private boolean mCanFocusOutHorizontal = true;
    private FocusLostListener mFocusLostListener;
    private FocusGainListener mFocusGainListener;
    private int mCurrentFocusPosition = 0;

    public FocusKeepRecyclerView(Context context) {
        this(context, null);
    }

    public FocusKeepRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FocusKeepRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        setChildrenDrawingOrderEnabled(true);
        setItemAnimator(null);
        this.setFocusable(true);
    }

    public boolean isCanFocusOutVertical() {
        return mCanFocusOutVertical;
    }

    public void setCanFocusOutVertical(boolean canFocusOutVertical) {
        mCanFocusOutVertical = canFocusOutVertical;
    }

    public boolean isCanFocusOutHorizontal() {
        return mCanFocusOutHorizontal;
    }

    public void setCanFocusOutHorizontal(boolean canFocusOutHorizontal) {
        mCanFocusOutHorizontal = canFocusOutHorizontal;
    }

    @Override
    public View focusSearch(int direction) {
        return super.focusSearch(direction);
    }

    @Override
    public View focusSearch(View focused, int direction) {
        Log.i(TAG, "focusSearch " + focused + ",direction= " + direction);
        View view = super.focusSearch(focused, direction);
        if (focused == null) {
            return view;
        }
        if (view != null) {
            View nextFocusItemView = findContainingItemView(view);
            if (nextFocusItemView == null) {
                if (!mCanFocusOutVertical && (direction == View.FOCUS_DOWN || direction == View.FOCUS_UP)) {
                    return focused;
                }
                if (!mCanFocusOutHorizontal && (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT)) {
                    return focused;
                }
                if (mFocusLostListener != null) {
                    mFocusLostListener.onFocusLost(focused, direction);
                }
                return view;
            }
        }
        return view;
    }

    public void setFocusLostListener(FocusLostListener focusLostListener) {
        this.mFocusLostListener = focusLostListener;
    }

    public interface FocusLostListener {
        void onFocusLost(View lastFocusChild, int direction);
    }

    public void setGainFocusListener(FocusGainListener focusListener) {
        this.mFocusGainListener = focusListener;
    }

    public interface FocusGainListener {
        void onFocusGain(View child, View focued);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        if (!hasFocus()) {
            if (mFocusGainListener != null) {
                mFocusGainListener.onFocusGain(child, focused);
            }
        }
        super.requestChildFocus(child, focused);
        mCurrentFocusPosition = getChildViewHolder(child).getAdapterPosition();
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        View view = null;
        if (this.hasFocus() || mCurrentFocusPosition < 0 || (view = getLayoutManager().findViewByPosition(mCurrentFocusPosition)) == null) {
            super.addFocusables(views, direction, focusableMode);
        } else if (view.isFocusable()) {
            views.add(view);
        } else {
            super.addFocusables(views, direction, focusableMode);
        }
    }

    @Override
    protected int getChildDrawingOrder(int childCount, int i) {
        View focusedChild = getFocusedChild();
        Log.i(TAG, "focusedChild =" + focusedChild);
        if (focusedChild == null) {
            return super.getChildDrawingOrder(childCount, i);
        } else {
            int index = indexOfChild(focusedChild);
            Log.i(TAG, " index = " + index + ",i=" + i + ",count=" + childCount);
            if (i == childCount - 1) {
                return index;
            }
            if (i < index) {
                return i;
            }
            return i + 1;
        }
    }
}
