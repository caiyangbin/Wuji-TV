package com.wuji.tv.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wuji.tv.R;


public class SelfDialog extends Dialog {

    private Button yes;
    private Button no;
    private TextView titleTv;
    private ImageView imgTitle;
    private LinearLayout layoutManage;
    private TextView messageTv;
    private TextView messageTv2;
    private String titleStr;
    private int titleResId;
    private String messageStr;
    private String messageStr2;
    private String yesStr, noStr;
    private boolean disableCancel = false;
    private int contentGravity = Gravity.LEFT;

    private OnNoOnclickListener noOnclickListener;
    private OnYesOnclickListener yesOnclickListener;

    public void setNoOnclickListener(String str, OnNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    public void setYesOnclickListener(String str, OnYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    public SelfDialog(Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_dialog);
        setCanceledOnTouchOutside(false);

        initView();
        initData();
        initEvent();

    }


    private void initEvent() {
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                    yesOnclickListener.onYesClick();
                }
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
            }
        });
        no.requestFocus();
    }

    private void initData() {
        if (titleStr != null) {
            titleTv.setVisibility(View.VISIBLE);
            imgTitle.setVisibility(View.GONE);
            titleTv.setText(titleStr);
            layoutManage.setGravity(Gravity.CENTER_VERTICAL);
        }
        else{
            titleTv.setVisibility(View.GONE);
            imgTitle.setVisibility(View.VISIBLE);
        }
        if (titleResId != 0) {
            titleTv.setBackgroundResource(titleResId);
        }
        if (messageStr != null) {
            messageTv.setText(messageStr);
        }
        if (messageStr2 != null) {
            messageTv2.setText(messageStr2);
            messageTv2.setVisibility(View.VISIBLE);
        }
        if (yesStr != null) {
            yes.setText(yesStr);
        }
        if (noStr != null) {
            no.setText(noStr);
        }

        if(disableCancel){
            no.setVisibility(View.GONE);
        }

        messageTv.setGravity(contentGravity);
        messageTv2.setGravity(contentGravity);
    }

    public void disableCancel(){
        disableCancel = true;
    }

    private void initView() {
        layoutManage = (LinearLayout) findViewById(R.id.layoutManage);
        yes = (Button) findViewById(R.id.btn_ok);
        no = (Button) findViewById(R.id.btn_cancel);
        titleTv = (TextView) findViewById(R.id.title_tv);
        imgTitle = findViewById(R.id.imgTitle);
        messageTv = (TextView) findViewById(R.id.msg_tv);
        messageTv2 = (TextView) findViewById(R.id.msg_tv2);
    }


    public void setCenterGravity(int GRAVITY){
        contentGravity = GRAVITY;
    }


    public void setTitle(String title) {
        titleStr = title;
    }


    public void setTitleBg(int resId) {
        titleResId = resId;
    }


    public void setMessage(String message) {
        messageStr = message;
    }
    public void setMessage2(String message) {
        messageStr2 = message;
    }


    public interface OnYesOnclickListener {
        public void onYesClick();
    }

    public interface OnNoOnclickListener {
        public void onNoClick();
    }
}
