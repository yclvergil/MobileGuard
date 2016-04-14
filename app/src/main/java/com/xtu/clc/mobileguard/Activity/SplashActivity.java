package com.xtu.clc.mobileguard.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import com.xtu.clc.mobileguard.R;
import com.xtu.clc.mobileguard.bean.UrlBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {
    private RelativeLayout rl_root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //初始化视图
        initView();
        //初始化动画
        initAnimation();
        //检查服务器版本
        checkVersion();
    }

    /**
     * 访问服务器，获取最新的版本信息
     */
    private void checkVersion() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
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
                        UrlBean bean = parseJson(jsonString);
                        Log.d("SplashActivity", "jsonString:Version" + bean.getVersionCode());
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

    }
}
