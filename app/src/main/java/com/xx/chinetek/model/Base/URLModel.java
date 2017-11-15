package com.xx.chinetek.model.Base;

import static com.xx.chinetek.chineteklib.model.Paramater.IPAdress;
import static com.xx.chinetek.chineteklib.model.Paramater.Port;

/**
 * Created by GHOST on 2017/6/9.
 */

public class URLModel {


    public static URLModel  GetURL() {
        return  new URLModel();
    }

    private static String  LastContent="AndroidService.svc/";

    String  GetWCFAdress(){
        return  "http://"+IPAdress+":"+Port+"/"+LastContent;
    }

    public String SyncMaterial = GetWCFAdress()+"SyncMaterial"; //同步物料基础数据
    public String SyncCus = GetWCFAdress()+"SyncCus"; //同步代理商和客户基础数据
    public String SyncPara = GetWCFAdress()+"SyncPara"; //同步参数配置
    public String SyncDn = GetWCFAdress()+"SyncDn"; //同步DN表头


}