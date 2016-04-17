package com.xtu.clc.mobileguard.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xtu.clc.mobileguard.R;
import com.xtu.clc.mobileguard.utils.Md5Utils;
import com.xtu.clc.mobileguard.utils.MyConstants;
import com.xtu.clc.mobileguard.utils.SpTools;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;


public class HomeActivity extends AppCompatActivity {

    @Bind(R.id.gv_home_menu)
    GridView mGvHomeMenu;
    private int icons[] = {R.drawable.safe, R.drawable.callmsgsafe, R.drawable.item_gv_selector_app,
            R.drawable.taskmanager, R.drawable.netmanager, R.drawable.trojan,
            R.drawable.sysoptimize, R.drawable.atools, R.drawable.settings};

    private String names[] = {"手机防盗", "通讯卫士", "软件管家", "进程管理", "流量统计",
            "病毒查杀", "缓存清理", "高级工具", "设置中心"};
    private AlertDialog dialog;

    @OnItemClick(R.id.gv_home_menu)
    public void onItemClick(int position) {
        switch (position) {
            case 0://手机防盗，自定义对话框
                if (TextUtils.isEmpty(SpTools.getString(getApplicationContext(),
                        MyConstants.PASSWORD, ""))) {
                    showSettingPassDialog();
                } else {
                    showEnterPassDialog();
                }
                break;
        }
    }

    /**
     * 登录密码的对话框
     */
    private void showEnterPassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_enter_password, null);
        final EditText et_password = (EditText) view.findViewById(R.id.et_dialog_enter_password);
        Button bt_login = (Button) view.findViewById(R.id.bt_dialog_enter_password_login);
        Button bt_cancel = (Button) view.findViewById(R.id.bt_dialog_enter_password_cancel);
        builder.setView(view);
        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = et_password.getText().toString();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    password = Md5Utils.md5(password);
                    if (password.equals(SpTools.getString(HomeActivity.this, MyConstants.PASSWORD, ""))) {
                        //密码匹配，进入手机防盗界面
                        Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    }

                }
                dialog.dismiss();
            }
        });
        bt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    /**
     * 设置密码的对话框
     */
    private void showSettingPassDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_setting_password, null);
        final EditText et_password = (EditText) view.findViewById(R.id.et_dialog_password);
        final EditText et_passwordAgain = (EditText) view.findViewById(R.id.et_dialog_passwordAgain);
        Button bt_setPassword = (Button) view.findViewById(R.id.bt_dialog_setting_password_setting);
        Button bt_setCancel = (Button) view.findViewById(R.id.bt_dialog_setting_password_cancel);
        builder.setView(view);
        bt_setPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passOne = et_password.getText().toString();
                String passTwo = et_passwordAgain.getText().toString();
                if (TextUtils.isEmpty(passOne) || TextUtils.isEmpty(passTwo)) {
                    Toast.makeText(HomeActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
                } else if (!passOne.equals(passTwo)) {
                    Toast.makeText(HomeActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
                } else {
                    //保存密码到sharedPreference
                    Log.d("HomeActivity", "保存密码");
                    passOne = Md5Utils.md5(passOne);
                    SpTools.putString(HomeActivity.this, MyConstants.PASSWORD, passOne);
                    dialog.dismiss();
                }
            }
        });
        bt_setCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        ButterKnife.bind(this);
        initData();

    }

    /**
     * 初始化组件的数据
     */
    private void initData() {
        MyAdapter adapter = new MyAdapter(this);
        mGvHomeMenu.setAdapter(adapter);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        setContentView(R.layout.activity_home);
    }

    /**
     * 自定义gridView的适配器
     */
    class MyAdapter extends BaseAdapter {
        private Context mContext;

        public MyAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return icons.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_home_gridview, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mIvItemHomeGvIcon.setImageResource(icons[position]);
            holder.mTvItemHomeGvName.setText(names[position]);
            return convertView;
        }

        class ViewHolder {
            @Bind(R.id.iv_item_home_gv_icon)
            ImageView mIvItemHomeGvIcon;
            @Bind(R.id.tv_item_home_gv_name)
            TextView mTvItemHomeGvName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

}
