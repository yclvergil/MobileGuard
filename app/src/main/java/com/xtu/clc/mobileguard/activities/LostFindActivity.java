package com.xtu.clc.mobileguard.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xtu.clc.mobileguard.R;
import com.xtu.clc.mobileguard.utils.MyConstants;
import com.xtu.clc.mobileguard.utils.SpTools;

/**
 * Created by clc on 2016/4/16.
 */
public class LostFindActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SpTools.getBoolean(this, MyConstants.ISSETUP, false)) {
            //进入过设置向导界面，直接显示本界面

            initView();//手机防盗界面
        } else {
            //进入设置向导界面
            Intent intent = new Intent(this,Setup1Activity.class);
            startActivity(intent);
            finish();//关闭自己
        }
    }

    private void initView() {
        setContentView(R.layout.activity_lostfind);
    }
}
