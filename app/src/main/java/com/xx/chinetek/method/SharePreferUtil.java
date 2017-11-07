package com.xx.chinetek.method;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.chineteklib.model.Paramater;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DN.DNTypeModel;

import java.lang.reflect.Type;


/**
 * Created by GHOST on 2017/2/3.
 */

public class SharePreferUtil {

    /**
     * 配置文件
     * @param context
     */
    public static void ReadShare(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
        if(sharedPreferences!=null) {
            Paramater.IPAdress=sharedPreferences.getString("IPAdress", "");
            Paramater.Port=sharedPreferences.getInt("Port", 9000);
            Paramater.SOCKET_TIMEOUT=sharedPreferences.getInt("TimeOut", 20000);
            ParamaterModel.PartenerID=sharedPreferences.getString("PartenerNo","");
        }
    }

    public static void SetShare(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putString("IPAdress",Paramater.IPAdress);
        edit.putInt("Port",Paramater.Port);
        edit.putInt("TimeOut",Paramater.SOCKET_TIMEOUT);
        edit.putString("PartenerNo", ParamaterModel.PartenerID);
        edit.apply();

    }

    /**
     *操作人
     * @param context
     */
    public static void ReadUserShare(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("User", Context.MODE_PRIVATE);
        if(sharedPreferences!=null) {
           ParamaterModel.Operater=sharedPreferences.getString("operater","");
        }
    }

    public static void SetUserShare(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putString("operater",ParamaterModel.Operater);
        edit.apply();
    }

    /**
     * 发货方式
     * @param context
     * @return
     */
    public static DNTypeModel ReadDNTypeShare(Context context){
        DNTypeModel dnTypeModel=null;
        SharedPreferences sharedPreferences=context.getSharedPreferences("DNTypeModel", Context.MODE_PRIVATE);
        if(sharedPreferences!=null) {
            Gson gson = new Gson();
            Type type = new TypeToken<DNTypeModel>(){}.getType();
            dnTypeModel= gson.fromJson(sharedPreferences.getString("DNType", ""), type);
        }
        return dnTypeModel;
    }

    public static void SetDNTypeShare(Context context,DNTypeModel dnTypeModel){
        SharedPreferences sharedPreferences=context.getSharedPreferences("DNTypeModel", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sharedPreferences.edit();
        Gson gson=new Gson();
        Type type = new TypeToken<DNTypeModel>() {}.getType();
        edit.putString("DNType",gson.toJson(dnTypeModel,type));
        edit.apply();
    }

    /**
     * 数据同步时间
     * @param context
     * @return
     */
    public static void ReadSyncTimeShare(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("SyncTime", Context.MODE_PRIVATE);
        if(sharedPreferences!=null) {
            ParamaterModel.MaterialSyncTime= sharedPreferences.getString("MaterialSyncTime","");
            ParamaterModel.CustomSyncTime= sharedPreferences.getString("CustomSyncTime","");
            ParamaterModel.ParamaterSyncTime= sharedPreferences.getString("ParamaterSyncTime","");
            ParamaterModel.DNSyncTime= sharedPreferences.getString("DNSyncTime","");
        }
    }

    public static void SetSyncTimeShare(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("SyncTime", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putString("MaterialSyncTime",ParamaterModel.MaterialSyncTime);
        edit.putString("CustomSyncTime",ParamaterModel.CustomSyncTime);
        edit.putString("ParamaterSyncTime",ParamaterModel.ParamaterSyncTime);
        edit.putString("DNSyncTime",ParamaterModel.DNSyncTime);
        edit.apply();
    }


}
