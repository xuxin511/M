package com.xx.chinetek.mitsubshi;

import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.service.LocationService;

/**
 * Created by GHOST on 2018/1/29.
 */

public class MitApplication extends BaseApplication {
    public LocationService locationService;
    public static String locationModel;
    public static String gpsModel;

    @Override
    public void onCreate() {
        super.onCreate();
        /***
         * 初始化定位sdk，建议在Application中创建
         */
        locationService = new LocationService(getApplicationContext());

    }
}
