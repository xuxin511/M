package com.xx.chinetek.mitsubshi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.model.Base.ParamaterModel;

import java.util.concurrent.ExecutionException;

/**
 * Created by GHOST on 2017/11/14.
 */

public class BaseIntentActivity extends BaseActivity {

    public static final String RES_ACTION = "android.intent.action.SCANRESULT";
    //子类中完成抽象函数赋值
    //实体中通过实现该全局接收器方法来处理接收到消息
    private IntentFilter mIntentFilter;
    public final int TAG_SCAN=9999;

    @Override
    public void onStart()
    {
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(RES_ACTION);
        super.onStart();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(MsgReceiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        registerReceiver(MsgReceiver,mIntentFilter);
        super.onResume();
    }


    private BroadcastReceiver MsgReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(RES_ACTION)){
                try {
                    //获取扫描结果value
                    String valueName = ParamaterModel.Model.equals("TC75") ? "com.symbol.datawedge.data_string" : "value";
                    final String scanResult = intent.getStringExtra(valueName);//"com.symbol.datawedge.data_string
                    android.os.Message msg = mHandler.obtainMessage(TAG_SCAN, scanResult);
                    mHandler.sendMessage(msg);

                }catch (Exception ex){

                }
            }

        }
    };




}
