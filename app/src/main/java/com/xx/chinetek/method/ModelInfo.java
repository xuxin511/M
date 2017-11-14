package com.xx.chinetek.method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.model.Base.ParamaterModel;

import java.util.Date;

/**
 * Created by GHOST on 2017/11/14.
 */

public class ModelInfo {
    public final static String REQUEST = "extra.mdm.request";
    public final static String RESPONE = "extra.mdm.respone";
    public static MyReceiver myReceiver;

    public static void GetSysINfo(){

        String model = android.os.Build.MODEL;
        String serialNo=android.os.Build.SERIAL;
        if(!model.toUpperCase().equals("TC75")){
            myReceiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(RESPONE);
            BaseApplication.context.registerReceiver(myReceiver, intentFilter);
            SendBroadcast(new Date().getTime(), 0x0003, "Option", 1);
            SendBroadcast(new Date().getTime(), 0x0002, null, 1);

        }else {
            ParamaterModel.SerialNo = serialNo;
            ParamaterModel.Model = model;
        }
    }

    public static void SendBroadcast(long timestamp, int cmd, String para, int value){
        Intent _intent = new Intent(REQUEST);

        _intent.putExtra("Timestamp", timestamp);
        _intent.putExtra("Cmd", cmd);
        if(para!=null)
        _intent.putExtra(para, value);

        BaseApplication.context.sendBroadcast(_intent);
    }

    private static  class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RESPONE)){
                if ((intent.getLongExtra("Timestamp", -1) > 0) &&
                        (intent.getIntExtra("Cmd", -1) > 0)){
                    if(intent.getIntExtra("Cmd", -1)==0x0003)
                        ParamaterModel.SerialNo=intent.getStringExtra("Data");
                    if(intent.getIntExtra("Cmd", -1)==0x0002)
                        ParamaterModel.Model=intent.getStringExtra("Data");
                }
            }
        }
    }
}
