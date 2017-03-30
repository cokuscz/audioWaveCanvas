package com.cokus.wavelibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by chenzhuo on 2017/3/30.
 * 实时绘制波形视图  有之前surfaceview 改为 view
 * 原因很直接 surfaceview 解决不了刷新卡顿问题，最好的选择只有View了
 */

public class WavaTimeView extends View {
    public WavaTimeView(Context context) {
        super(context);
    }

    public WavaTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WavaTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }





}
