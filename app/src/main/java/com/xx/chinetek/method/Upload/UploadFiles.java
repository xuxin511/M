package com.xx.chinetek.method.Upload;

import android.os.Message;

import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.chineteklib.util.hander.MyHandler;
import com.xx.chinetek.method.FTP.FtpUtil;
import com.xx.chinetek.method.Mail.MailUtil;
import com.xx.chinetek.model.Base.ParamaterModel;

import java.io.File;
import java.util.Date;

/**
 * Created by GHOST on 2017/11/15.
 */

public class UploadFiles {
    /**
     * FTP上传
     * @param mHandler
     * @return
     */
    public static void UploadFtp(final File[] files,final MyHandler<BaseActivity> mHandler){
        new Thread(){
            @Override
            public void run() {
                try {
                    FtpUtil.FtpUploadDN(ParamaterModel.ftpModel,files,mHandler);
                }catch (Exception ex){
                    Message msg = mHandler.obtainMessage(NetworkError.NET_ERROR_CUSTOM, ex.getMessage());
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 邮件发送
     * @param mHandler
     * @return
     */
    public static void UploadMail(final File[] list, final MyHandler<BaseActivity> mHandler){
        new Thread(){
            @Override
            public void run() {
                try {
                    ParamaterModel.mailModel.setFromAddress(ParamaterModel.mailModel.getAccount());
                    ParamaterModel.mailModel.setToAddress(ParamaterModel.mailModel.getAccount());
                    ParamaterModel.mailModel.setSubject("DN_"+ CommonUtil.DateToString(new Date(),null));
                    ParamaterModel.mailModel.setContent("QR");
                    MailUtil.SendMail(ParamaterModel.mailModel, list,mHandler);
                }catch (Exception ex){
                    Message msg = mHandler.obtainMessage(NetworkError.NET_ERROR_CUSTOM, ex.getMessage());
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }
}
