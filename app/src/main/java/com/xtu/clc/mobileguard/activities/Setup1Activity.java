package com.xtu.clc.mobileguard.activities;

import com.xtu.clc.mobileguard.R;

/**
 * Created by clc on 2016/4/16.
 */
public class Setup1Activity extends BaseSetupActivity {
    @Override
    public void initView() {
        setContentView(R.layout.activity_setup1);
    }

    @Override
    protected void prevActivity() {

    }

    @Override
    protected void nextActivity() {
        startActivity(Setup2Activity.class);

    }
}
