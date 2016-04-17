package com.xtu.clc.mobileguard.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by clc on 2016/4/17.
 */
public class ServiceUtils {

    public static boolean isServiceRunning(Context context, String serviceName) {
        boolean isRunning = false;

        //判断运行中的服务状态
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取Android手机中运行的所有服务
        List<ActivityManager.RunningServiceInfo> runningServiceInfos = am.getRunningServices(50);
        for (ActivityManager.RunningServiceInfo info : runningServiceInfos) {
            //判断服务的名字是否包含我们指定的服务名
            if (info.service.getClassName().equals(serviceName)) {
                isRunning = true;
                //已经找到，退出循环
                break;
            }
        }
        return isRunning;
    }
}
