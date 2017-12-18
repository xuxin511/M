package com.xx.chinetek.method;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.chineteklib.model.Paramater;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.FTP.FtpModel;
import com.xx.chinetek.method.Mail.MailModel;
import com.xx.chinetek.model.Base.BaseparaModel;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.SyncParaModel;
import com.xx.chinetek.model.DN.DNTypeModel;

import java.lang.reflect.Type;
import java.util.List;

import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_DNtype;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_Para;


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
            Paramater.Port=sharedPreferences.getInt("Port", 80);
            Paramater.SOCKET_TIMEOUT=sharedPreferences.getInt("TimeOut", 20000);
            ParamaterModel.PartenerID=sharedPreferences.getString("PartenerNo","");
            ParamaterModel.PartenerName=sharedPreferences.getString("PartenerName","");
            ParamaterModel.SysPassword=sharedPreferences.getString("SysPassword","123456");
            Gson gson = new Gson();
            Type type = new TypeToken<BaseparaModel>(){}.getType();
            ParamaterModel.baseparaModel= gson.fromJson(sharedPreferences.getString("BaseparaModel", ""), type);
            if(ParamaterModel.baseparaModel==null) ParamaterModel.baseparaModel=new BaseparaModel();
            if(ParamaterModel.baseparaModel.getFtpModel()==null){
                ParamaterModel.baseparaModel.setFtpModel(new FtpModel());
                ParamaterModel.baseparaModel.getFtpModel().setFtpPort(21);
                ParamaterModel.baseparaModel.getFtpModel().setFtpDownLoad("/ftp/down/");
                ParamaterModel.baseparaModel.getFtpModel().setFtpUpLoad("/ftp/up/");
                ParamaterModel.baseparaModel.getFtpModel().setFtpUserName("anonymous");
                ParamaterModel.baseparaModel.getFtpModel().setFtpPassword("anonymous");
            }
            if(ParamaterModel.baseparaModel.getMailModel()==null){
                ParamaterModel.baseparaModel.setMailModel(new MailModel());
                ParamaterModel.baseparaModel.getMailModel().setMailServerPort("25");
            }
          }
    }

    public static void SetShare(Context context){
        SharedPreferences sharedPreferences=context.getSharedPreferences("Setting", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=sharedPreferences.edit();
        edit.putString("IPAdress",Paramater.IPAdress);
        edit.putInt("Port",Paramater.Port);
        edit.putInt("TimeOut",Paramater.SOCKET_TIMEOUT);
        edit.putString("PartenerNo", ParamaterModel.PartenerID);
        edit.putString("PartenerName", ParamaterModel.PartenerName);
        edit.putString("SysPassword", ParamaterModel.SysPassword);
        Gson gson=new Gson();
        Type type = new TypeToken<BaseparaModel>() {}.getType();
        String para=gson.toJson(ParamaterModel.baseparaModel,type);
        edit.putString("BaseparaModel",para);
        edit.apply();
        LogUtil.WriteLog(SharePreferUtil.class,TAG_Para,para);
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
        String dntype=gson.toJson(dnTypeModel,type);
        edit.putString("DNType",dntype);
        edit.apply();
        LogUtil.WriteLog(SharePreferUtil.class,TAG_DNtype,dntype);
    }

    /**
     * 数据同步时间
     * @return
     */
    public static void ReadSyncTimeShare() {
        List<SyncParaModel> syncParaModels = DbBaseInfo.getInstance().GetSysPara();
        ParamaterModel.Register=null;
        ParamaterModel.MaterialSyncTime="";
        ParamaterModel.CustomSyncTime="";
        ParamaterModel.ParamaterSyncTime="";
        if(syncParaModels!=null) {
            for (int i = 0; i < syncParaModels.size(); i++) {
                switch (syncParaModels.get(i).getKey()) {
                    case "MaterialSyncTime":
                        ParamaterModel.MaterialSyncTime = syncParaModels.get(i).getValue();
                        break;
                    case "CustomSyncTime":
                        ParamaterModel.CustomSyncTime = syncParaModels.get(i).getValue();
                        break;
                    case "ParamaterSyncTime":
                        ParamaterModel.ParamaterSyncTime = syncParaModels.get(i).getValue();
                        break;
                    case "Register":
                        ParamaterModel.Register=syncParaModels.get(i).getValue();
                            break;
                }
            }
        }
    }

    public static void SetSyncTimeShare(String Key,String Value){
        SyncParaModel syncParaModel=new SyncParaModel();
        syncParaModel.setKey(Key);
        syncParaModel.setValue(Value);
        DbBaseInfo.getInstance().UpdateSysPara(syncParaModel);

    }


}
