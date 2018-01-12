package com.xx.chinetek.method.FTP;

import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.util.hander.MyHandler;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.ParamaterModel;

import java.io.File;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncFTP;

/**
 * Created by GHOST on 2017/11/8.
 */

public class FtpUtil {
    public static FtpHelper ftp;
    public static void FtpDownDN(FtpModel ftpModel, MyHandler<BaseActivity> mHandler ) throws Exception {
        int total = 0;
        if (ftp == null) {
            ftp = new FtpHelper(ftpModel);
        }
        ftp.openConnect();
        total = ftp.downloadFolder(ParamaterModel.baseparaModel.getFtpModel().getFtpDownLoad(), ParamaterModel.DownDirectory);
//        if (total != 0) {
//            total=ftp.deleteFolder(ParamaterModel.ftpModel.getFtpDownLoad());
//        }
        if (ftp.isConnect()) {
            ftp.closeConnect();
        }

        android.os.Message msg = mHandler.obtainMessage(RESULT_SyncFTP, total);
        mHandler.sendMessage(msg);
    }

    public static void FtpMoveFile(FtpModel ftpModel,String[] moveFiles) throws Exception{
            if (ftp == null) {
                ftp = new FtpHelper(ftpModel);
            }
            ftp.openConnect();
            for (String moveFile:moveFiles){
                ftp.MoveFile(ParamaterModel.baseparaModel.getFtpModel().getFtpDownLoad(),moveFile,"/BAK/");
            }
            if (ftp.isConnect()) {
                ftp.closeConnect();
            }

    }

    public static void FtpUploadDN(FtpModel ftpModel,File[] files, MyHandler<BaseActivity> mHandler ) throws Exception{
        try {
            int total = 0;
            if (ftp == null) {
                ftp = new FtpHelper(ftpModel);
            }
            ftp.openConnect();
            for (File file : files) {
                boolean flag = false;
                flag = ftp.uploadFile(file.getAbsolutePath(), ParamaterModel.baseparaModel.getFtpModel().getFtpUpLoad());
                if (flag) {
                    total++;
                }
            }
            if (total == files.length) {
                for (int i = 0; i < files.length; i++) {
                    File f = files[i];
                    f.delete();
                }
            }
            if (ftp.isConnect()) {
                ftp.closeConnect();
            }
            if (mHandler != null) {
                android.os.Message msg = mHandler.obtainMessage(RESULT_SyncFTP, BaseApplication.context.getString(R.string.Msg_UploadSuccess) + total/2);
                mHandler.sendMessage(msg);
            }
        } catch (Exception ex) {
            if (mHandler != null) {
                android.os.Message msg = mHandler.obtainMessage(RESULT_SyncFTP, BaseApplication.context.getString(R.string.Msg_UploadFailue) + ex.getMessage());
                mHandler.sendMessage(msg);
            }
        }
    }



}
