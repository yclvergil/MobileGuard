package com.xtu.clc.mobileguard.activities;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.xtu.clc.mobileguard.R;
import com.xtu.clc.mobileguard.service.LostFindService;
import com.xtu.clc.mobileguard.utils.MyConstants;
import com.xtu.clc.mobileguard.utils.ServiceUtils;
import com.xtu.clc.mobileguard.utils.SpTools;

/**
 * Created by clc on 2016/4/16.
 */
public class Setup4Activity extends BaseSetupActivity {

    private CheckBox cb_isprotected;
    private TextView tv_protected;

    /**
     * 完成界面显示
     */
    @Override
    public void initView() {
        setContentView(R.layout.activity_setup4);

        //复选框
        cb_isprotected = (CheckBox) findViewById(R.id.cb_setup4_isprotected);
        tv_protected = (TextView) findViewById(R.id.tv_protected);

    }
    /**
     * 初始化复选框的值
     */
    @Override
    protected void initData() {
        //根据服务是否开启初始化复选框
        if (ServiceUtils.isServiceRunning(getApplicationContext(),
                "com.xtu.clc.mobileguard.service.LostFindService")) {
            //服务正在运行
            cb_isprotected.setChecked(true);
            tv_protected.setText("防盗保护已开启");
        } else {
            cb_isprotected.setChecked(false);
            tv_protected.setText("防盗保护已关闭");

        }
        super.initData();
    }

    /**
     * 初始化复选框的事件
     */
    @Override
    protected void initEvent() {
        cb_isprotected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //选择为勾
                if (isChecked) {
                    Toast.makeText(Setup4Activity.this, "check true", Toast.LENGTH_SHORT).show();
                    tv_protected.setText("防盗保护已开启");
                    Intent service = new Intent(Setup4Activity.this, LostFindService.class);
                    startService(service);
                } else {
                    Toast.makeText(Setup4Activity.this, "check false", Toast.LENGTH_SHORT).show();
                    tv_protected.setText("防盗保护已关闭");
                    Intent service = new Intent(Setup4Activity.this, LostFindService.class);
                    stopService(service);
                }
            }
        });
        super.initEvent();
    }

    @Override
    public void next(View v) {
        if (!cb_isprotected.isChecked()) {
            Toast.makeText(this, "开启防盗保护才能完成", Toast.LENGTH_SHORT).show();
            return;
        }
        super.next(v);
    }

    @Override
    protected void prevActivity() {
        startActivity(Setup3Activity.class);

    }

    @Override
    protected void nextActivity() {
        SpTools.setBoolean(getApplicationContext(), MyConstants.ISSETUP, true);
        startActivity(LostFindActivity.class);
    }
}
