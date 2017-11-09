package com.xx.chinetek.method.FTP;

import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.util.hander.MyHandler;
import com.xx.chinetek.model.Base.ParamaterModel;

import java.io.IOException;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncFTP;

/**
 * Created by GHOST on 2017/11/8.
 */

public class FtpUtil {
    private static FtpHelper ftp;
    public static void FtpDownDN(FtpModel ftpModel, MyHandler<BaseActivity> mHandler ) throws Exception{
        int total=0;
        if (ftp == null) {
            try {
                ftp = new FtpHelper(ftpModel);
                ftp.openConnect();
                total= ftp.downloadFolder(ParamaterModel.ftpModel.getFtpDownLoad(),ParamaterModel.DownDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if(ftp.isConnect()) {
                    ftp.closeConnect();
                }

            }
        }
        android.os.Message msg = mHandler.obtainMessage(RESULT_SyncFTP,total);
        mHandler.sendMessage(msg);
    }



}
