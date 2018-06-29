package com.xx.chinetek.method.Upload;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;

import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.model.ReturnMsgModel;
import com.xx.chinetek.chineteklib.util.CompressUtil;
import com.xx.chinetek.chineteklib.util.Network.RequestHandler;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.function.DESUtil;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.hander.MyHandler;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.FTP.FtpUtil;
import com.xx.chinetek.method.FileUtils;
import com.xx.chinetek.method.UploadGPS;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.DNStatusEnum;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.URLModel;
import com.xx.chinetek.model.DBReturnModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.xx.chinetek.chineteklib.base.BaseApplication.context;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_ExceptionDNList;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_UploadDN;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_ExceptionDNList;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_UploadDN;

/**
 * Created by GHOST on 2017/11/16.
 */

public class UploadDN {
    /**
     * 单个单据提交
     *
     * @param context
     * @param dnModel
     * @param mHandler
     */
    public static void SumbitDN(final Context context, final DNModel dnModel,final String DNStstus,final MyHandler<BaseActivity> mHandler) {
        if (dnModel.getSTATUS() != 3) {
            //判断出库单是否完成
            boolean isFinished = true;
            if (dnModel.getDETAILS().size() == 0) {
                isFinished = false;
            } else {
                for (DNDetailModel dnDetailModel : dnModel.getDETAILS()) {
                    if (dnDetailModel.getSCAN_QTY() != dnDetailModel.getDN_QTY()) {
                        isFinished = false;
                        break;
                    }
                }
            }
            if (!isFinished || dnModel.getDN_SOURCE() == 3) {
//                DbDnInfo.getInstance().ChangeDNStatusByDnNo(dnModel.getAGENT_DN_NO(), DNStatusEnum.complete);
                String msg = dnModel.getDN_SOURCE() == 3 ? context.getResources().getString(R.string.Msg_Upload_DNSelf) :
                        context.getResources().getString(R.string.Msg_Upload_DN);
                new AlertDialog.Builder(context).setTitle("提示")// 设置对话框标题
                        .setIcon(android.R.drawable.ic_dialog_info)// 设置对话框图
                        .setMessage(msg)
//                        .setPositiveButton("提交并关闭", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                UploadDN.UploadDNToMaps(dnModel, "F", mHandler);
//                            }
//                        })
                        .setNegativeButton("提交", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                UploadDN.UploadDNToMaps(dnModel, DNStstus, mHandler);
                            }
                        })
                        .show();
            } else {
                //提交成功修改单据状态
                DbDnInfo.getInstance().ChangeDNStatusByDnNo(dnModel.getAGENT_DN_NO(), DNStatusEnum.complete);
                UploadDN.UploadDNToMaps(dnModel, "F", mHandler);
            }
            DbDnInfo.getInstance().UpdateOperaterData(dnModel);
        } else {
            MessageBox.Show(context, context.getString(R.string.Msg_Dn_Finished));
        }
    }


    /**
     * 上传出库单到MAPS
     *
     * @param result
     */
    public static DBReturnModel AnalysisUploadDNToMapsJson(Context context, String result,
                                                           final DNModel subdnModel) {
        DBReturnModel dbReturnModel = new DBReturnModel();
        dbReturnModel.setReturnCode(1);
        try {
            LogUtil.WriteLog(UploadDN.class, TAG_UploadDN, result);
            ReturnMsgModel<DNModel> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<DNModel>>() {
            }.getType());

            if (!returnMsgModel.getHeaderStatus().equals("E")) {
                if (subdnModel.getDN_SOURCE() == 2) {//ftp需要移动文件之BAK
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                LogUtil.WriteLog(UploadDN.class, TAG_UploadDN, "ftp_moveFile:" + subdnModel.getFtpFileName());
                                FtpUtil.FtpMoveFile(ParamaterModel.baseparaModel.getFtpModel(), new String[]{subdnModel.getFtpFileName()});
                            } catch (Exception ex) {
                                LogUtil.WriteLog(UploadDN.class, TAG_UploadDN, "ftp_moveFile:" + ex.getMessage());
                            }
                        }
                    }.start();
                }

                DNModel dnModel = returnMsgModel.getModelJson();
                DbDnInfo.getInstance().ChangeDNStatusByDnNo(subdnModel.getAGENT_DN_NO(), DNStatusEnum.complete);
                //保留原有数据
                DNModel tempdnModel = DbDnInfo.getInstance().GetLoaclDN(subdnModel.getAGENT_DN_NO());
                if (tempdnModel != null && dnModel != null) {
                    dnModel.setOPER_DATE(tempdnModel.getOPER_DATE());
                    dnModel.setCUS_DN_NO(tempdnModel.getCUS_DN_NO());
                    dnModel.setREMARK(tempdnModel.getREMARK());
                    if (subdnModel.getDN_SOURCE() == 3) {
                        DbDnInfo.getInstance().DeleteDN(subdnModel.getAGENT_DN_NO());
                    }
                }
                if (returnMsgModel.getHeaderStatus().equals("R")) { //单据重复
                    DbDnInfo.getInstance().ChangeDNStatusByDnNo(subdnModel.getAGENT_DN_NO(), DNStatusEnum.Repert);
                    dbReturnModel.setReturnCode(-2);
                    dbReturnModel.setReturnMsg(context.getString(R.string.Msg_DNNORepert));
                }
                if (returnMsgModel.getHeaderStatus().equals("N") && dnModel.getDN_SOURCE() == 3) {
                    ArrayList<DNModel> dnModels = new ArrayList<>();
                    dnModel.setSTATUS(DNStatusEnum.complete);
                    dnModels.add(dnModel);
                    //插入数据
                    DbDnInfo.getInstance().InsertDNDB(dnModels);
                }
                if (returnMsgModel.getHeaderStatus().equals("S") && dnModel != null) { //有异常
                    ArrayList<DNModel> dnModels = new ArrayList<>();
                    dnModels.add(dnModel);
                    //删除异常数据，以下载为准
                    DbDnInfo.getInstance().DeleteDN(dnModel.getAGENT_DN_NO());
                    //插入数据
                    DbDnInfo.getInstance().InsertDNDB(dnModels);
                    //更新出库单状态(异常)
                    DbDnInfo.getInstance().ChangeDNStatusByDnNo(dnModel.getAGENT_DN_NO(), DNStatusEnum.exeption);

                    dbReturnModel.setReturnCode(-1);
                    dbReturnModel.setReturnMsg(context.getString(R.string.Msg_ExceptionDN));
                }
                if (returnMsgModel.getHeaderStatus().equals("K") && dnModel != null) { //后台单据已关闭
                    DbDnInfo.getInstance().DeleteDN(subdnModel.getAGENT_DN_NO());
                    int status = dnModel.getSTATUS() == DNStatusEnum.download ? DNStatusEnum.complete : DNStatusEnum.Sumbit;
                    dnModel.setSTATUS(status);
                    // DbDnInfo.getInstance().DELscanbyagent(subdnModel.getAGENT_DN_NO());
                    ArrayList<DNModel> dnModels = new ArrayList<>();
                    dnModels.add(dnModel);
                    //插入数据
                    DbDnInfo.getInstance().InsertDNDB(dnModels);
                    dbReturnModel.setReturnCode(-1);
                    dbReturnModel.setReturnMsg(returnMsgModel.getMessage());
                }
                if (returnMsgModel.getHeaderStatus().equals("Z")) { //后台单据有异常
                    DbDnInfo.getInstance().ChangeDNStatusByDnNo(subdnModel.getAGENT_DN_NO(), DNStatusEnum.download);
                    dbReturnModel.setReturnCode(-1);
                    dbReturnModel.setReturnMsg(returnMsgModel.getMessage());
                }
                if (returnMsgModel.getHeaderStatus().equals("F")) {
                    DbDnInfo.getInstance().DeleteDN(subdnModel.getAGENT_DN_NO());
                    ArrayList<DNModel> dnModels = new ArrayList<>();
                    dnModels.add(dnModel);
                    DbDnInfo.getInstance().InsertDNDB(dnModels);
                    DNModel upDnmodel = DbDnInfo.getInstance().GetLoaclDN(dnModel.getAGENT_DN_NO());
                    dnModels = new ArrayList<>();
                    dnModels.add(upDnmodel);
                    //更新出库单状态
                    DbDnInfo.getInstance().ChangeDNStatusByDnNo(dnModel.getAGENT_DN_NO(), DNStatusEnum.Sumbit);

                    final ArrayList<DNModel> ExportdnModels = dnModels;
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(5000);
                                ExportDN(ExportdnModels, 0);
                                ExportDN(ExportdnModels, 1);
                            } catch (Exception ex) {
                                LogUtil.WriteLog(UploadDN.class, TAG_UploadDN, "ExportDN:" + ex.getMessage());
                            }
                        }
                    }.start();



                }
            } else {
                dbReturnModel.setReturnCode(-1);
                dbReturnModel.setReturnMsg(returnMsgModel.getMessage());
            }

        } catch (Exception ex) {
            dbReturnModel.setReturnCode(-1);
            dbReturnModel.setReturnMsg(ex.getMessage());
        }
        return dbReturnModel;
    }

    public static void ExportDN(ArrayList<DNModel> selectDnModels, int Index) throws Exception {
        FileUtils.DeleteFiles(Index);
        FileUtils.ExportDNFile(selectDnModels, Index); //导出文件至本地目录
        File dirFile = new File(FileUtils.GetDirectory(Index));
        if (dirFile.isDirectory()) {
            File[] Files = dirFile.listFiles();
            if (Files.length > 0) {
                switch (Index) {
                    case 0: //邮件
                        if (ParamaterModel.baseparaModel.getMailModel() != null && (
                                ParamaterModel.baseparaModel.getMailModel().getToAddress() == null
                                        || ParamaterModel.baseparaModel.getMailModel().getToAddress().size() == 0)) {
                            break;
                        }
                        UploadFiles.UploadMail(Files, null);
                        break;
                    case 1: //FTP
                        if (ParamaterModel.baseparaModel.getFtpModel() != null && (
                                ParamaterModel.baseparaModel.getFtpModel().getFtpHost() == null)) {
                            break;
                        }
                        UploadFiles.UploadFtp(Files, null);
                        break;
                }
            }
        }
    }


    public static void UploadDNToMaps(DNModel dnModel, String isCloseDN, MyHandler<BaseActivity> mHandler) {

        final Map<String, String> params = new HashMap<String, String>();
        String dnModelJson = GsonUtil.parseModelToJson(dnModel);
        String user = GsonUtil.parseModelToJson(ParamaterModel.userInfoModel);
        params.put("UserInfoJS", user);
        params.put("DNJS", CompressUtil.compressForZip(DESUtil.encode(dnModelJson)));
        params.put("IsFinish", isCloseDN); //F.关闭 N:不关闭
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(UploadDN.class, TAG_UploadDN, para);
        String method = dnModel.getSTATUS() == -1 ? URLModel.GetURL().ExceptionDN : URLModel.GetURL().UploadNDN;
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_UploadDN,
                context.getString(R.string.Dia_UploadDN), context, mHandler, RESULT_UploadDN, null,
                method, params, null);
        ArrayList<DNModel> dnModels = new ArrayList<DNModel>();
        dnModels.add(dnModel);
        UploadGPS.UpGPS(mHandler, dnModels);
        UploadThirdInterface(mHandler,params);//第三方接口
    }

    public static void UploadDNListToMaps(ArrayList<DNModel> dnModels, String isCloseDN, MyHandler<BaseActivity> mHandler) {

        final Map<String, String> params = new HashMap<String, String>();
        String dnModelJson = GsonUtil.parseModelToJson(dnModels);
        String user = GsonUtil.parseModelToJson(ParamaterModel.userInfoModel);
        params.put("UserInfoJS", user);
        params.put("DNListJS", CompressUtil.compressForZip(DESUtil.encode(dnModelJson)));
        params.put("IsFinish", isCloseDN); //F.关闭 N:不关闭
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(UploadDN.class, TAG_ExceptionDNList, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_ExceptionDNList,
                context.getString(R.string.Dia_UploadDN), context, mHandler, RESULT_ExceptionDNList, null,
                URLModel.GetURL().ExceptionDNList, params, null);
        UploadGPS.UpGPS(mHandler, dnModels);
    }


    private static void UploadThirdInterface(MyHandler<BaseActivity> mHandler,Map<String, String> params){
        if(ParamaterModel.baseparaModel.getThirdInterfaceModel()==null) {
            return;
        }
        if(TextUtils.isEmpty(ParamaterModel.baseparaModel.getThirdInterfaceModel().getInterfaceIP())
                    || TextUtils.isEmpty(ParamaterModel.baseparaModel.getThirdInterfaceModel().getPart()))
        {
            return;
        }
        String URL="http://"+ParamaterModel.baseparaModel.getThirdInterfaceModel().getInterfaceIP()+":"+
                ParamaterModel.baseparaModel.getThirdInterfaceModel().getPort()+"/"+
                ParamaterModel.baseparaModel.getThirdInterfaceModel().getPart();
        RequestHandler.addRequest(Request.Method.POST, TAG_ExceptionDNList,
              mHandler, RESULT_ExceptionDNList, null,URL
               , params, null);
    }

}
