package com.xtu.clc.mobileguard.activities;

import android.content.Context;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.xtu.clc.mobileguard.R;
import com.xtu.clc.mobileguard.utils.MyConstants;
import com.xtu.clc.mobileguard.utils.SpTools;

/**
 * Created by clc on 2016/4/16.
 */
public class Setup2Activity extends BaseSetupActivity {
    private Button bt_bind;
    private ImageView iv_isBind;

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup2);
        //获取bind SIM卡按钮
        bt_bind = (Button) findViewById(R.id.bt_setup2_bindsim);
        //是否绑定sim卡的图标
        iv_isBind = (ImageView) findViewById(R.id.iv_setup2_isbind);
        if (TextUtils.isEmpty(SpTools.getString(getApplicationContext(), MyConstants.SIM, ""))) {
            //未绑定
            iv_isBind.setImageResource(R.drawable.unlock);
        } else {
            iv_isBind.setImageResource(R.drawable.lock);

        }

    }

    @Override
    public void next(View v) {
        if (TextUtils.isEmpty(SpTools.getString(getApplicationContext(), MyConstants.SIM, ""))) {
            //没有绑定Sim卡
            Toast.makeText(this, "请先绑定Sim卡", Toast.LENGTH_SHORT).show();
            return;
        }
        super.next(v);
    }

    @Override
    protected void initEvent() {
        //添加自己的事件
        bt_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(SpTools.getString(getApplicationContext(), MyConstants.SIM, ""))) {
                    //没有绑定SIM卡，点击按钮执行绑定
                    //获取SIM卡信息
                    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String simSerialNumber = tm.getSimSerialNumber();
                    //保存到sp中
                    SpTools.putString(getApplicationContext(), MyConstants.SIM, simSerialNumber);
                    //设置加锁图片
                    iv_isBind.setImageResource(R.drawable.lock);
                } else {
                    //解绑Sim卡
                    SpTools.putString(getApplicationContext(), MyConstants.SIM, "");
                    iv_isBind.setImageResource(R.drawable.unlock);
                }
            }
        });
    }

    @Override
    protected void prevActivity() {
        startActivity(Setup1Activity.class);

    }

    @Override
    protected void nextActivity() {
        startActivity(Setup3Activity.class);
    }
}
