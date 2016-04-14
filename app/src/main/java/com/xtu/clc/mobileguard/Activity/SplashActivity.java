package com.xtu.clc.mobileguard.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xtu.clc.mobileguard.R;
import com.xtu.clc.mobileguard.bean.UrlBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.OkHttpClient;

public class SplashActivity extends AppCompatActivity {
    private static final int LOADMAIN = 1;
    private static final int SHOW_UPDATE_DIALOG = 2;
    private RelativeLayout rl_root;
    private int versionCode;//版本号
    private String versionName;//版本名
    private TextView tv_versionName;
    private UrlBean bean;
    private long startTimeMillis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化视图
        initView();
        //初始化版本信息
        initData();
        //初始化动画
        initAnimation();
        //检查服务器版本
        checkVersion();
    }

    /**
     * 初始化版本名
     */
    private void initData() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
            versionCode = info.versionCode;
            versionName = info.versionName;
            tv_versionName.setText(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 访问服务器，获取最新的版本信息
     */
    private void checkVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    startTimeMillis = System.currentTimeMillis();
                    URL url = new URL("http://10.0.2.2:8087/guardversion.json");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);//读取数据超时
                    conn.setConnectTimeout(5000);//网络连接超时
                    conn.setRequestMethod("GET");
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream in = conn.getInputStream();
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String line = br.readLine();
                        StringBuilder jsonString = new StringBuilder();
                        while (line != null) {
                            jsonString.append(line);
                            line = br.readLine();

                        }
                        //解析
                        bean = parseJson(jsonString);
                        isNewVersion(bean);
                        br.close();
                        conn.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 在UI线程Handler处理消息
     */
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOADMAIN:
                    loadMain();//进入主界面
                    break;
                case SHOW_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
            }
        }
    };

    /**
     * 创建更新对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提醒")
                .setMessage("是否更新新版本？新版本具有如下特性：" + bean.getDesc())
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("SplashActivity", "更新APK");
                        downloadNewApk();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loadMain();
                    }
                });
        builder.show();
    }

    /**
     * 下载新版本Apk
     */
    private void downloadNewApk() {

    }

    /**
     * 加载主界面
     */
    private void loadMain() {
        Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
        startActivity(intent);
    }

    /**
     * 检查是否有新版本
     */
    private void isNewVersion(UrlBean bean) {
        int serverCode = bean.getVersionCode();//获取的服务器版本
        long endTimeMillis = System.currentTimeMillis();
        if (endTimeMillis - startTimeMillis < 3000) {
            //设置休眠的时间保证至少休眠三秒
            SystemClock.sleep(3000 - (endTimeMillis - startTimeMillis));
        }
        if (serverCode == versionCode) {
            //发送进入主界面的消息
            mHandler.sendEmptyMessage(LOADMAIN);
        } else {
            //有新版本，弹出更新对话框的消息
            mHandler.sendEmptyMessage(SHOW_UPDATE_DIALOG);
        }
    }

    /**
     *
     * @param jsonString 解析json
     */
    private UrlBean parseJson(StringBuilder jsonString) {
        UrlBean bean = new UrlBean();
        try {
            //解析json后封装到UrlBean中
            JSONObject jsonObject = new JSONObject(jsonString+"");
            int version = jsonObject.getInt("version");
            String apkPath = jsonObject.getString("url");
            String desc = jsonObject.getString("desc");
            bean.setVersionCode(version);
            bean.setUrl(apkPath);
            bean.setDesc(desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bean;
    }

    /**
     * 动画
     */
    private void initAnimation() {
        //渐变动画，透明到不透明,持续时间,结束状态
        AlphaAnimation aa = new AlphaAnimation(0.0f,1.0f);
        aa.setDuration(3000);
        aa.setFillAfter(true);

        //旋转动画,锚点,持续时间,结束状态
        RotateAnimation ra = new RotateAnimation(0,360,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        ra.setDuration(3000);
        ra.setFillAfter(true);

        //比例动画
        ScaleAnimation sa = new ScaleAnimation(0.0f,1.0f, 0.0f,1.0f,
                Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        sa.setDuration(3000);
        sa.setFillAfter(true);

        //动画集合
        AnimationSet as = new AnimationSet(true);
        as.addAnimation(aa);
        as.addAnimation(ra);
        as.addAnimation(sa);
        //显示动画
        rl_root.startAnimation(as);
    }


    /**
     * 视图
     */
    private void initView() {
        setContentView(R.layout.activity_splash);
        rl_root = (RelativeLayout) findViewById(R.id.rl_splash_root);
        tv_versionName = (TextView) findViewById(R.id.tv_splash_version_name);
    }
}
