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

    private static String  LastContent="Service.svc/";

    String  GetWCFAdress(){
        return  "http://"+IPAdress+":"+Port+"/"+LastContent;
    }

    public String ValidateEquip = GetWCFAdress()+"ValidateEquip"; //设备验证
    public String SyncMaterial = GetWCFAdress()+"GetMatrials"; //同步物料基础数据
    public String SyncMaterialRang = GetWCFAdress()+"GetMatrialsRange"; //同步物料基础数据
    public String SyncCus = GetWCFAdress()+"GetCustomers"; //同步代理商和客户基础数据
    public String UploadCus = GetWCFAdress()+"AddNewCustomer"; //上传新增客户
    public String UploadNDN = GetWCFAdress()+"UploadDN";//"UploadDN"; //上传出库单
    public String ExceptionDN = GetWCFAdress()+"ExceptionDN"; //上传异常出库单
    public String ExceptionDNList = GetWCFAdress()+"UploadMultipleDNs"; //上传异常出库单
    public String GetDNDetailItems = GetWCFAdress()+"GetDNDetailItems"; //获取出库单完整数据
    public String SyncPara = GetWCFAdress()+"GetCustomerSettings"; //同步参数配置
    public String SyncDeleteDn = GetWCFAdress()+"GetDeletedDN"; //同步删除单据
    public String UploadPara = GetWCFAdress()+"SetCustomerSettings"; //同步参数配置
    public String SyncDn = GetWCFAdress()+"GetDNHeaders"; //同步DN表头
    public String SyncDnDetail = GetWCFAdress()+"GetDNDetails"; //同步DN表体
    public String SyncException = GetWCFAdress()+"GetExceptionDNs"; //同步DN异常的单子

}
