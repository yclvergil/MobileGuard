package com.xtu.clc.mobileguard.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.xtu.clc.mobileguard.activities.HomeActivity;

/**
 * Created by clc on 2016/4/16.
 */
public class SpTools {

    public static void putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.SPFILE, Context.MODE_PRIVATE);
        sp.edit().putString(key, value).apply();//保存数据

    }

    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.SPFILE, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    public static void setBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.SPFILE, Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).apply();//保存数据

    }

    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(MyConstants.SPFILE, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }
}
