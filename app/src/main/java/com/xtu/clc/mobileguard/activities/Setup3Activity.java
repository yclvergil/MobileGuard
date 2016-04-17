package com.xtu.clc.mobileguard.activities;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.xtu.clc.mobileguard.R;
import com.xtu.clc.mobileguard.utils.MyConstants;
import com.xtu.clc.mobileguard.utils.SpTools;

/**
 * Created by clc on 2016/4/16.
 */
public class Setup3Activity extends BaseSetupActivity {

    private EditText mEt_safeNumber;

    /**
     * 子类覆盖此方法完成界面的显示
     */
    @Override
    public void initView() {
        setContentView(R.layout.activity_setup3);
        //安全号码的编辑框
        mEt_safeNumber = (EditText) findViewById(R.id.et_setup3_safenumber);
    }

    /**
     * 子类覆盖此方法完成组件数据的初始化
     */
    @Override
    protected void initData() {
        mEt_safeNumber.setText(SpTools.getString(this, MyConstants.SAFENUMBER,""));
        super.initData();
    }

    @Override
    protected void prevActivity() {
        startActivity(Setup2Activity.class);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String phone = data.getStringExtra(MyConstants.SAFENUMBER);
            //显示安全号码
            mEt_safeNumber.setText(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void next(View v) {
        String safeNumber = mEt_safeNumber.getText().toString().trim();

        if (TextUtils.isEmpty(safeNumber)) {
            Toast.makeText(this, "安全号码不能为空", Toast.LENGTH_SHORT).show();
            return;
        } else {
            // 保存安全号码
            SpTools.putString(getApplicationContext(), MyConstants.SAFENUMBER, safeNumber);
        }
        super.next(v);
    }

    @Override
    protected void nextActivity() {
        startActivity(Setup4Activity.class);

    }

    /**
     * 点击事件，从联系人中获取安全号码
     * @param view
     */
    public void selectSafeNumber(View view) {
        Intent friends = new Intent(this, FriendsActivity.class);
        startActivityForResult(friends, 1);
    }
}
