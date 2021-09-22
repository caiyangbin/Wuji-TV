package com.admin.libcommonui.widget;

import android.content.Context;
import android.util.AttributeSet;

public class AlwaysMarqueeTextView extends CustomFontsTextView {

    private boolean mFocused = true;

    public AlwaysMarqueeTextView(Context context) {
        super(context);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlwaysMarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isFocused() {
        return mFocused;
    }

    public void setFocusedFlag(boolean focused) {
        this.mFocused = focused;
    }
}