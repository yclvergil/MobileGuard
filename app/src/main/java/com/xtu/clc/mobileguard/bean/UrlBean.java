package com.xtu.clc.mobileguard.bean;

/**
 * Created by clc on 2016/4/14.
 * json封装
 */
public class UrlBean {
    private String url;
    private int versionCode;
    private String desc;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
