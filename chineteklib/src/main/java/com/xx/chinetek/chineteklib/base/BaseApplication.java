package com.xx.chinetek.chineteklib.base;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.xutils.x;

/**
 * 指定自定义Application类，用于存放全局变量
 * 在 AndroidManifest.xml中指定android:name="com.example.demo.application.BaseApplication" 来启用
 */

public class BaseApplication extends Application {


    public static BaseApplication instance;

    public static Context context;  //activity中context对象

    public static String DialogShowText;

    public static ToolBarTitle toolBarTitle;

    private RequestQueue mRequestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        x.Ext.init(this);
        mRequestQueue = Volley.newRequestQueue(this);
        /***
         * 初始化定位sdk，建议在Application中创建
         */
    }


    public static BaseApplication getInstance() {
        return (BaseApplication) instance;
    }

    public static RequestQueue getRequestQueue() {
        return instance.mRequestQueue;
    }
}
