package com.xx.chinetek.model.Base;

import com.xx.chinetek.method.FTP.FtpModel;
import com.xx.chinetek.method.Mail.MailModel;
import com.xx.chinetek.model.DN.DNTypeModel;

import java.io.File;

/**
 * Created by GHOST on 2017/11/1.
 */

public class ParamaterModel {

    /**
     * 用户集合
     */
    public static UserInfoModel userInfoModel;

    /**
     * 操作人编号
     */
    public static String Operater;

    /**
     * 序列号
     */
    public static String SerialNo=null;
    /**
     * 管理员密码
     */
    public static String SysPassword;

    /**
     * DN单据本地保存时间
     */
    public static int DNSaveTime=30;

    /**
     * 是否启用备注栏
     */
    public static Boolean IsUseRemark=false;

    /**
     * 是否启用三菱条码
     */
    public static CusBarcodeRule cusBarcodeRule=null;

    /**
     * 自定义出库单规则
     */
    public static CusDnnoRule cusDnnoRule=null;

    /**
     * 设备型号
     */
    public static String Model=null;

    /**
     * 代理商编号
     */
    public static  String  PartenerID="";

    /**
     * 发货方式，发货方
     */
    public static DNTypeModel DnTypeModel;

    /**
     * 邮件服务器
     */
    public static MailModel mailModel=new MailModel();

    /**
     * 邮件过滤内容
     */
    public static String FilterContent="QR";

    /**
     * FTP服务器
     */
    public static FtpModel ftpModel;

    /**
     * 物料同步时间
     */
    public static String MaterialSyncTime;

    /**
     * 客户同步时间
     */
    public static String CustomSyncTime;

    /**
     * 参数同步时间
     */
    public static String ParamaterSyncTime;

    /**
     * 出库单据同步时间
     */
    public static String DNSyncTime;

    /**
     *   文件存放目录
     */
    public static String Directory="/sdcard/mitsubshi";


    /**
     * 数据库存放目录
     */
    public static String DBDirectory=Directory+ File.separator+"db";

    /**
     * 下载文件存放目录
     */
    public static String DownDirectory=Directory+ File.separator+"down";

    /**
     *    上传文件存放目录
     */
    public static String UpDirectory=Directory+ File.separator+"Up";


}
