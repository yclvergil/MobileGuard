package com.xtu.clc.mobileguard.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xtu.clc.mobileguard.R;
import com.xtu.clc.mobileguard.bean.ContentBean;
import com.xtu.clc.mobileguard.engine.ReadContactsEngine;
import com.xtu.clc.mobileguard.utils.MyConstants;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by clc on 2016/4/17.
 */
public class FriendsActivity extends AppCompatActivity {

    private static final int LOADING = 1;
    private static final int FINISH = 2;
    private List<ContentBean> datas = new ArrayList<>();
    private ListView lv_Datas;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        lv_Datas = (ListView) findViewById(R.id.friends_list_view);
        mAdapter = new MyAdapter();
        //设置适配器，读取数据显示
        lv_Datas.setAdapter(mAdapter);
        //填充数据
        initData();
        //初始化事件
        initEvent();
    }

    /**
     * 初始化ListView的条目点击事件
     */
    private void initEvent() {
        lv_Datas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContentBean contentBean = datas.get(position);
                //获取号码
                String phone = contentBean.getPhone();
                Intent datas = new Intent();
                datas.putExtra(MyConstants.SAFENUMBER, phone);//保存安全号码
                //设置数据
                setResult(1,datas);
                //关闭自己
                finish();
            }
        });
    }

    /**
     * 消息处理
     */
    private Handler mHandler = new Handler(new Handler.Callback() {

        private ProgressDialog pd;

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case LOADING:
                    pd = new ProgressDialog(FriendsActivity.this);
                    pd.setTitle("注意");
                    pd.setMessage("正在玩命加载数据");
                    pd.show();//显示对话框
                    break;
                case FINISH:
                    if (pd != null) {
                        pd.dismiss();
                        pd = null;
                    }
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    /**
     * 自定义ListView适配器类
     */
     class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return datas.size();
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
            ContentBean bean = datas.get(position);
            ViewHolder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_friend_listview, null);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mTvFriendItemName.setText(bean.getName());
            holder.mTvFriendItemPhone.setText(bean.getPhone());
            return convertView;
        }

         class ViewHolder {
            @Bind(R.id.tv_item_name_listview)
            TextView mTvFriendItemName;
            @Bind(R.id.tv_item_phone_listview)
            TextView mTvFriendItemPhone;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }

    /**
     * 数据初始化
     */
    private void initData() {
        //子线程访问数据
        new Thread() {
            @Override
            public void run() {
                //显示获取数据的进度
                Message msg = Message.obtain();
                msg.what = LOADING;
                mHandler.sendMessage(msg);
                //为了展示进度条，休眠两秒
                SystemClock.sleep(2000);
                //获取数据
                datas = ReadContactsEngine.readContacts(getApplicationContext());
                Log.d("FriendsActivity", "datas.size():" + datas.size());
                //数据获取完成，发送数据加载完成的消息
                msg = Message.obtain();
                msg.what = FINISH;
                mHandler.sendMessage(msg);
            }
        }.start();
    }
}
