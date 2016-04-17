package com.xtu.clc.mobileguard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.xtu.clc.mobileguard.R;

/**
 * Created by clc on 2016/4/16.
 */
public abstract class BaseSetupActivity extends AppCompatActivity {
    private GestureDetector gd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initGesture();//初始化手势识别器

        initData();//初始化数据
        initEvent();//初始化组件的事件
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gd.onTouchEvent(event);//绑定onTouch事件
        return super.onTouchEvent(event);
    }

    protected void initEvent() {

    }


    protected void initData() {
    }

    /**
     * 手势识别
     */
    private void initGesture() {
        gd = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (Math.abs(velocityX) > 200) {//速度的绝对值大于200pix/s
                    float dx = e2.getX() - e1.getX();
                    if (Math.abs(dx) < 100) {
                        return true;//间距小于100无效
                    }
                    if (dx < 0) {
                        next(null);
                    } else {
                        prev(null);
                    }
                }
                return true;
            }
        });
    }

    /**
     * 初始化视图
     */
    public abstract void initView();

    public void prev(View v) {
        //1.完成界面的切换
        prevActivity();
        //2.动画的播放
        prevAnimation();
    }

    public void next(View v) {
        //1.完成界面的切换
        nextActivity();
        //2.动画的播放
        nextAnimation();
    }

    /**
     * 上一个动画界面
     */
    private void prevAnimation() {
        overridePendingTransition(R.anim.prev_in,R.anim.prev_out);
    }

    /**
     * 下一个动画及界面
     */
    private void nextAnimation() {
        //param :进来的动画，出去的动画
        overridePendingTransition(R.anim.next_in,R.anim.next_out);
    }

    public void startActivity(Class type) {
        Intent next = new Intent(this,type);
        startActivity(next);
        finish();
    }

    protected abstract void prevActivity();

    protected abstract void nextActivity();
}
