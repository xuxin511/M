package com.xx.chinetek.method;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.chineteklib.model.Paramater;
import com.xx.chinetek.method.FTP.FtpModel;
import com.xx.chinetek.method.Mail.MailModel;
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
            if(ParamaterModel.mailModel==null) ParamaterModel.mailModel=new MailModel();
            ParamaterModel.mailModel.setAccount(sharedPreferences.getString("Account",""));
            ParamaterModel.mailModel.setPassword(sharedPreferences.getString("Password",""));
            ParamaterModel.mailModel.setMailServerPort(sharedPreferences.getString("StmpPort","25"));
            ParamaterModel.mailModel.setMailServerHost(sharedPreferences.getString("Stmp",""));
            ParamaterModel.mailModel.setMailClientHost(sharedPreferences.getString("IMAP",""));
            if(ParamaterModel.ftpModel==null) ParamaterModel.ftpModel=new FtpModel();
            ParamaterModel.ftpModel.setFtpHost(sharedPreferences.getString("FtpHost",""));
            ParamaterModel.ftpModel.setFtpUserName(sharedPreferences.getString("FtpUserName","anonymous"));
            ParamaterModel.ftpModel.setFtpPassword(sharedPreferences.getString("FtpPassword","12345"));
            ParamaterModel.ftpModel.setFtpPort(sharedPreferences.getInt("FtpPort",21));
            ParamaterModel.ftpModel.setFtpDownLoad(sharedPreferences.getString("FtpDownLoad",""));
            ParamaterModel.ftpModel.setFtpUpLoad(sharedPreferences.getString("FtpUpLoad",""));
        }
    }

    public static void SetShare(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putString("IPAdress",Paramater.IPAdress);
        edit.putInt("Port",Paramater.Port);
        edit.putInt("TimeOut",Paramater.SOCKET_TIMEOUT);
        edit.putString("PartenerNo", ParamaterModel.PartenerID);
        if(ParamaterModel.mailModel!=null){
            edit.putString("Account", ParamaterModel.mailModel.getAccount());
            edit.putString("Password", ParamaterModel.mailModel.getPassword());
            edit.putString("StmpPort", ParamaterModel.mailModel.getMailServerPort());
            edit.putString("Stmp", ParamaterModel.mailModel.getMailServerHost());
            edit.putString("IMAP", ParamaterModel.mailModel.getMailClientHost());
        }
        if(ParamaterModel.ftpModel!=null){
            edit.putString("FtpHost",ParamaterModel.ftpModel.getFtpHost());
            edit.putString("FtpUserName",ParamaterModel.ftpModel.getFtpUserName());
            edit.putString("FtpPassword",ParamaterModel.ftpModel.getFtpPassword());
            edit.putInt("FtpPort",ParamaterModel.ftpModel.getFtpPort());
            edit.putString("FtpDownLoad",ParamaterModel.ftpModel.getFtpDownLoad());
            edit.putString("FtpUpLoad",ParamaterModel.ftpModel.getFtpUpLoad());
        }
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
