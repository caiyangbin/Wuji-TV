package com.admin.libcommonui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.widget.TextView;

import static com.admin.libcommonui.CommonUIConfig.sTypeface;


/**
 * 自动textView 引用外部字体
 */
@SuppressLint("AppCompatCustomView")
public class CustomFontsTextView extends TextView {

    private boolean mEnabled = true;

    public CustomFontsTextView(Context context) {
        super(context);
        init(context);
    }

    public CustomFontsTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public CustomFontsTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {

        this.setTypeface(sTypeface);
    }

    public void setAutoSplitEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY
                && MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY && getWidth() > 0 && getHeight() > 0
                && mEnabled && TruncateAt.MARQUEE != getEllipsize()) {
            String newText = autoSplitText(this);
            if (!TextUtils.isEmpty(newText)) {
                setText(newText);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 根据控件的尺寸自动拆分文本
     *
     * @param tv
     * @return
     */
    private String autoSplitText(final TextView tv) {
        final String rawText = tv.getText().toString(); // 原始文本
        final Paint tvPaint = tv.getPaint(); // paint，包含字体等信息
        final float tvWidth = tv.getWidth() - tv.getPaddingLeft() - tv.getPaddingRight(); // 控件可用宽度

        // 将原始文本按行拆分
        String[] rawTextLines = rawText.replaceAll("\r", "").split("\n");
        StringBuilder sbNewText = new StringBuilder();
        for (String rawTextLine : rawTextLines) {
            if (tvPaint.measureText(rawTextLine) <= tvWidth) {
                // 如果整行宽度在控件可用宽度之内，就不处理了
                sbNewText.append(rawTextLine);
            } else {
                // 如果整行宽度超过控件可用宽度，则按字符测量，在超过可用宽度的前一个字符处手动换行
                float lineWidth = 0;
                for (int cnt = 0; cnt != rawTextLine.length(); ++cnt) {
                    char ch = rawTextLine.charAt(cnt);
                    lineWidth += tvPaint.measureText(String.valueOf(ch));
                    if (lineWidth <= tvWidth) {
                        sbNewText.append(ch);
                    } else {
                        sbNewText.append("\n");
                        lineWidth = 0;
                        --cnt;
                    }
                }
            }
            sbNewText.append("\n");
        }

        // 把结尾多余的\n去掉
        if (!rawText.endsWith("\n")) {
            sbNewText.deleteCharAt(sbNewText.length() - 1);
        }

        return sbNewText.toString();
    }

}
