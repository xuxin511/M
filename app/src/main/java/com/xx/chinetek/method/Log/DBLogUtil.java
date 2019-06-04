package com.xx.chinetek.method.Log;

import com.xx.chinetek.method.DB.DbLogInfo;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;
import com.xx.chinetek.model.DN.LogModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GHOST on 2018/12/29.
 */

public class DBLogUtil {

    /**
     * 按明细行删除单据扫描记录
     * @param dnDetailModel
     * @param dnScanModels
     */
    public static void DeleteScanRecordLog(final DNDetailModel dnDetailModel,final  List<DNScanModel> dnScanModels){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DbLogInfo.getInstance().InsertLog(new LogModel("开始删除单据物料记录",dnDetailModel.getGOLFA_CODE()+"|"+dnDetailModel.getLINE_NO(),dnDetailModel.getAGENT_DN_NO()));
                ArrayList<LogModel> logModels=new ArrayList<>();
                for (DNScanModel dnScanModel:dnScanModels) {
                    logModels.add(new LogModel("删除序列号",dnDetailModel.getLINE_NO()+"|"+dnScanModel.getSERIAL_NO(),dnScanModel.getAGENT_DN_NO()));
                }
                DbLogInfo.getInstance().InsertLog(logModels);
                DbLogInfo.getInstance().InsertLog(new LogModel("结束删除单据物料记录",dnDetailModel.getGOLFA_CODE()+"|"+dnDetailModel.getLINE_NO(),dnDetailModel.getAGENT_DN_NO()));
            }
        }).start();

    }




    public static void UploadDNLog(final DNModel dnModel,final String method,final  String isClose){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String dnno=dnModel.getDN_SOURCE()==3 ||dnModel.getDN_SOURCE()==5?dnModel.getCUS_DN_NO():dnModel.getAGENT_DN_NO();
                DbLogInfo.getInstance().InsertLog(new LogModel("开始提交出库单据记录",method+"|"+isClose, dnno));
                ArrayList<LogModel> logModels=new ArrayList<>();
                for (DNDetailModel dnDetailModel:dnModel.getDETAILS()) {
//                    logModels.add(new LogModel("物料",dnDetailModel.getGOLFA_CODE()+"|"+dnDetailModel.getLINE_NO()+
//                            dnDetailModel.getITEM_NO()+"|"+dnDetailModel.getITEM_NAME(),dnno));
                    for (DNScanModel dnScanModel:dnDetailModel.getSERIALS()) {
                        logModels.add(new LogModel("序列号",dnDetailModel.getGOLFA_CODE()+"|"+dnDetailModel.getLINE_NO()+
                               "|"+dnDetailModel.getITEM_NAME()+"|"+dnScanModel.getSERIAL_NO()+"|"+dnScanModel.getMAT_TYPE(),dnno));
                    }
                }
                DbLogInfo.getInstance().InsertLog(logModels);
                DbLogInfo.getInstance().InsertLog(new LogModel("结束提交出库单据记录","",dnno));

            }
        }).start();

    }

    public static void UploadDNLog(final List<DNModel> dnModels,final String method){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(DNModel dnModel:dnModels) {
                    String dnno = dnModel.getDN_SOURCE() == 3 || dnModel.getDN_SOURCE() == 5 ? dnModel.getCUS_DN_NO() : dnModel.getAGENT_DN_NO();
                    DbLogInfo.getInstance().InsertLog(new LogModel("开始提交出库单据记录", method , dnno));
                    ArrayList<LogModel> logModels = new ArrayList<>();
                    for (DNDetailModel dnDetailModel : dnModel.getDETAILS()) {
//                        logModels.add(new LogModel("物料", dnDetailModel.getGOLFA_CODE() + "|" + dnDetailModel.getLINE_NO() +
//                                dnDetailModel.getITEM_NO() + "|" + dnDetailModel.getITEM_NAME(), dnno));
                        for (DNScanModel dnScanModel : dnDetailModel.getSERIALS()) {
                            logModels.add(new LogModel("序列号",dnDetailModel.getGOLFA_CODE()+"|"+dnDetailModel.getLINE_NO()+
                                    "|"+dnDetailModel.getITEM_NAME()+"|"+dnScanModel.getSERIAL_NO()+"|"+dnScanModel.getMAT_TYPE(),dnno));
                        }
                    }
                    DbLogInfo.getInstance().InsertLog(logModels);
                    DbLogInfo.getInstance().InsertLog(new LogModel("结束提交出库单据记录", "", dnno));
                }

            }
        }).start();

    }

}
