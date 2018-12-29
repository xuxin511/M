package com.xx.chinetek.method.Log;

import com.xx.chinetek.method.DB.DbLogInfo;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNScanModel;
import com.xx.chinetek.model.DN.LogModel;

import java.util.List;

/**
 * Created by GHOST on 2018/12/29.
 */

public class DBLogUtil {
    public static void DeleteScanRecordLog(final DNDetailModel dnDetailModel,final  List<DNScanModel> dnScanModels){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DbLogInfo.getInstance().InsertLog(new LogModel("开始删除单据物料记录",dnDetailModel.getGOLFA_CODE()+"|"+dnDetailModel.getLINE_NO(),dnDetailModel.getAGENT_DN_NO()));
                for (DNScanModel dnScanModel:dnScanModels) {
                    DbLogInfo.getInstance().InsertLog(new LogModel("删除序列号",dnScanModel.getSERIAL_NO(),dnScanModel.getAGENT_DN_NO()));
                }
                DbLogInfo.getInstance().InsertLog(new LogModel("结束删除单据物料记录",dnDetailModel.getGOLFA_CODE()+"|"+dnDetailModel.getLINE_NO(),dnDetailModel.getAGENT_DN_NO()));
            }
        }).start();

    }

}
