package com.xtu.clc.mobileguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by clc on 2016/4/17.
 */
public class LostFindService extends Service {

    private SmsReceiver mSmsReceiver;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class SmsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //实现短信拦截
            Bundle extras = intent.getExtras();

            Object[] datas = (Object[]) extras.get("pdus");
            for (Object data : datas) {
                SmsMessage sm = SmsMessage.createFromPdu((byte[]) data);
                Log.d("SmsReceiver", (sm.getMessageBody() + " : " + sm.getOriginatingAddress()));
            }
        }
    }

    @Override
    public void onCreate() {
        //短信广播接受者
        mSmsReceiver = new SmsReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(1000);
        //注册 短信监听
        registerReceiver(mSmsReceiver, filter);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mSmsReceiver);
        super.onDestroy();
    }
}
