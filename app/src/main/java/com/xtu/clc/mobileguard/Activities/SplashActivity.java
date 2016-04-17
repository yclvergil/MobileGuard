package com.xtu.clc.mobileguard.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import android.widget.Toast;

import com.xtu.clc.mobileguard.R;
import com.xtu.clc.mobileguard.bean.UrlBean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SplashActivity extends AppCompatActivity {
    private static final int LOADMAIN = 1;
    private static final int SHOW_UPDATE_DIALOG = 2;
    private static final int ERROR = 3;
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
            public BufferedReader br = null;
            public HttpURLConnection conn = null;
            int errorCode = -1;

            @Override
            public void run() {
                try {
                    //http:192.168.1.112:8087/xxx.apk
                    startTimeMillis = System.currentTimeMillis();
                    URL url = new URL("http://192.168.1.112:8087/guardversion.json");
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(5000);//读取数据超时
                    conn.setConnectTimeout(5000);//网络连接超时
                    conn.setRequestMethod("GET");
                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        InputStream in = conn.getInputStream();
                        br = new BufferedReader(new InputStreamReader(in));
                        String line = br.readLine();
                        StringBuilder jsonString = new StringBuilder();
                        while (line != null) {
                            jsonString.append(line);
                            line = br.readLine();

                        }
                        //解析
                        bean = parseJson(jsonString);
                    } else {
                        errorCode = 404;//找不到文件
                    }
                } catch (MalformedURLException e) {//URL格式问题
                    errorCode = 4002;
                    e.printStackTrace();
                } catch (IOException e) {//网络连接问题
                    errorCode = 4001;
                    e.printStackTrace();
                } catch (JSONException e) {//Json格式问题
                    errorCode = 4003;
                    e.printStackTrace();
                }finally {
                    Message msg = Message.obtain();
                    if (errorCode == -1) {
                        msg.what = isNewVersion(bean);//检查是否为新版本
                    } else {
                        msg.what = ERROR;
                        msg.arg1 = errorCode;
                    }
                    long endTimeMillis = System.currentTimeMillis();
                    if (endTimeMillis - startTimeMillis < 3000) {
                        //设置休眠的时间保证至少休眠三秒
                        SystemClock.sleep(3000 - (endTimeMillis - startTimeMillis));
                    }
                    mHandler.sendMessage(msg);//发送消息
                    //关闭资源
                    try {
                        if (br != null) {
                            br.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (conn !=null) {
                        conn.disconnect();
                    }
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
                case ERROR:
                    switch (msg.arg1) {
                        case 404:
                            //资源找不到
                            Toast.makeText(getApplicationContext(),
                                    "404资源找不到", Toast.LENGTH_SHORT).show();
                            break;
                        case 4001:
                            //找不到网络
                            Toast.makeText(getApplicationContext(),
                                    "4001没有网络", Toast.LENGTH_SHORT).show();
                            break;
                        case 4003:
                            Toast.makeText(getApplicationContext(),
                                    "4003Json格式错误", Toast.LENGTH_SHORT).show();
                            //Json格式错误
                    }
                    loadMain();
                    break;
                case SHOW_UPDATE_DIALOG:
                    showUpdateDialog();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 创建更新对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                loadMain();
            }
        });
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
     * 安装新版APK
     */
    private void installApk() {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        String type = "application/vnd.android.package-archive";
        Uri data = Uri.fromFile(new File("/mnt/sdcard/xxx.apk"));
        intent.setDataAndType(data, type);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        loadMain();//如果用户取消更新apk，那么直接进入主界面
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 下载新版本APK
     */
    private void downloadNewApk() {
        Log.d("SplashActivity", bean.getUrl());
        //创建一个okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();
        //创建一个Request
        final Request request = new Request.Builder()
                .url("http:192.168.1.112:8087/xxx.apk")
                .build();
        //new Call
        Call call = mOkHttpClient.newCall(request);
        //请求加入调度
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SplashActivity.this, "下载新版本失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    is = response.body().byteStream();
                    File file = new File("/mnt/sdcard/xxx.apk");
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        fos.write(buf, 0, len);
                    }
                    fos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if (fos != null) {
                        fos.close();
                    }
                    if (is != null) {
                        is.close();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SplashActivity.this, "下载新版本成功", Toast.LENGTH_SHORT).show();
                        installApk();
                    }
                });
            }
        });
    }

    /**
     * 加载主界面
     */
    private void loadMain() {
        Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
        startActivity(intent);
        finish();//closeSelf
    }

    /**
     * 检查是否有新版本
     */
    private int isNewVersion(UrlBean bean) {
        int serverCode = bean.getVersionCode();//获取的服务器版本
        if (serverCode == versionCode) {
            //返回进入主界面的消息码
            return LOADMAIN;
        } else {
            //有新版本，返回更新对话框的消息码
            return SHOW_UPDATE_DIALOG;
        }
    }

    /**
     *
     * @param jsonString 解析json
     */
    private UrlBean parseJson(StringBuilder jsonString) throws JSONException {
        UrlBean bean = new UrlBean();

            //解析json后封装到UrlBean中
            JSONObject jsonObject = new JSONObject(jsonString+"");
            int version = jsonObject.getInt("version");
            String apkPath = jsonObject.getString("url");
            String desc = jsonObject.getString("desc");
            bean.setVersionCode(version);
            bean.setUrl(apkPath);
            bean.setDesc(desc);

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
