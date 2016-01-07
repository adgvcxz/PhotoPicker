package com.adgvcxz.photopicker.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * zhaowei
 * Created by zhaowei on 16/1/7.
 */
public class InterceptLayout extends RelativeLayout {
    public InterceptLayout(Context context) {
        super(context);
    }

    public InterceptLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InterceptLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public InterceptLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }
}
