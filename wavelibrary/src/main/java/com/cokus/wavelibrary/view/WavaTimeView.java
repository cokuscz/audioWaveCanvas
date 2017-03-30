package com.cokus.wavelibrary.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by chenzhuo on 2017/3/30.
 * 实时绘制波形视图  有之前surfaceview 改为 view
 * 原因很直接 surfaceview 解决不了刷新卡顿问题，最好的选择只有View了
 */

public class WavaTimeView extends View {
    private ArrayList<Short> audioBuf = new ArrayList<>();//绘制波形的音频数据
    private int lineColor;//波形线的颜色
    private int seekColor;//进度的颜色
    private static final int draw_time = 10;//绘制时间间隔 单位毫秒



    public WavaTimeView(Context context) {
        super(context);
    }

    public WavaTimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WavaTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 开始绘制
     */
    public void startDrawWave(){

    }

    /**
     * 暂停绘制
     */
    public void pasueDrawWave(){

    }

    /**
     * 停止绘制
     */
    public void stopDrawWave(){

    }

    /**
     * 重置视图
     */
    public void resetWaveTimeView(){

    }

    /**
     * 绘制背景
     */
    private void drawBackGroud(){

    }


    /**
     * 绘制线程
     */
    class drawThread extends  Thread{
        @Override
        public void run() {
            super.run();


        }
    }






}
