package com.xx.chinetek.method.Sync;

import android.os.Message;
import android.text.TextUtils;

import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.model.ReturnMsgModelList;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.Network.RequestHandler;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.function.FileUtil;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.hander.MyHandler;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.FTP.FtpUtil;
import com.xx.chinetek.method.Mail.MailUtil;
import com.xx.chinetek.method.SharePreferUtil;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.URLModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xx.chinetek.chineteklib.base.BaseApplication.context;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncDn;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncDn;

/**
 * Created by GHOST on 2017/11/2.
 */

public class SyncDN {

    /**
     * 获取MAPS单据
     * @param mHandler
     * @return
     */
    public static void SyncMAPS(MyHandler<BaseActivity> mHandler){
        //MAPS获取单据
        final Map<String, String> params = new HashMap<String, String>();
        params.put("Synctime", ParamaterModel.DNSyncTime);
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(SyncBase.class, TAG_SyncDn, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SyncDn,
                context.getString(R.string.Dia_SyncDn), context, mHandler, RESULT_SyncDn, null,  URLModel.GetURL().SyncDn, params, null);
    }

    /**
     * 获取邮件
     * @param mHandler
     * @return
     */
    public static void SyncMail(final MyHandler<BaseActivity> mHandler){
        new Thread(){
            @Override
            public void run() {
                try {
                    MailUtil.GetMail(ParamaterModel.mailModel, mHandler);
                }catch (Exception ex){
                    Message msg = mHandler.obtainMessage(NetworkError.NET_ERROR_CUSTOM, ex.getMessage());
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    /**
     * 获取FTP
     * @param mHandler
     * @return
     */
    public static void SyncFtp(final MyHandler<BaseActivity> mHandler){
        new Thread(){
            @Override
            public void run() {
                try {
                    FtpUtil.FtpDownDN(ParamaterModel.ftpModel,mHandler);
                }catch (Exception ex){
                    Message msg = mHandler.obtainMessage(NetworkError.NET_ERROR_CUSTOM, ex.getMessage());
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }




    /**
     * MAPS同步出库单
     * @param result
     */
   public static void AnalysisSyncMAPSDNJson(String result) throws Exception {
       LogUtil.WriteLog(SyncDN.class, TAG_SyncDn, result);
       ReturnMsgModelList<DNModel> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<DNModel>>() {
       }.getType());
       if (returnMsgModel.getHeaderStatus().equals("S")) {
           ArrayList<DNModel> dnModels = returnMsgModel.getModelJson();
           //插入数据
           DbDnInfo.getInstance().InsertDNDB(dnModels);
           ParamaterModel.DNSyncTime = "";
           //保存同步时间
           SharePreferUtil.SetSyncTimeShare(context);
       } else {
           MessageBox.Show(context, returnMsgModel.getMessage());
       }
   }


    /**
     * 读取目录下文件获取出库单
     * @return
     * @throws Exception
     */
   public static void DNFromFiles() throws Exception{
       File[] DNfiles=new File(ParamaterModel.DownDirectory).listFiles();
       ArrayList<DNModel> dnModels=new ArrayList<>();
       for(int i=0;i<DNfiles.length;i++) {
           DNModel dnModel = new DNModel();
           File file = DNfiles[i];
           String charSetName= FileUtil.GetCharSetName(file);
           InputStream inputStream = new FileInputStream(file);
           InputStreamReader inputStreamReader = new InputStreamReader(inputStream, charSetName);
           BufferedReader reader = new BufferedReader(inputStreamReader);
           String line;
           if (reader.readLine() != null) {
               List<DNDetailModel> dnDetailModels = new ArrayList<>();
               int Qty = 0;
               int firstIndex=0;
               while ((line = reader.readLine()) != null) {
                   String[] lines = line.split(",");
                   String DNNo = lines[0].trim();
                   if(firstIndex==0) {
                       dnModel = DbDnInfo.getInstance().GetLoaclDN(DNNo);
                       if(dnModel==null){
                           dnModel=new DNModel();
                           dnModel.setDN_STATUS(1);
                       }
                       firstIndex++;

                       dnModel.setAGENT_DN_NO(DNNo);
                       String cusNo = DbBaseInfo.getInstance().GetCustomName(lines[2].trim());
                       dnModel.setLEVEL_2_AGENT_NO(cusNo);
                       dnModel.setLEVEL_2_AGENT_NAME(lines[2].trim());
                       cusNo = DbBaseInfo.getInstance().GetCustomName(lines[3].trim());
                       dnModel.setCUSTOM_NO(cusNo);
                       dnModel.setCUSTOM_NAME(lines[3].trim());
                  }
                   int lineno=Integer.parseInt(lines[1].trim());
                   DNDetailModel dnDetailModel =DbDnInfo.getInstance().GetLoaclDNDetail(DNNo,lineno);
                   if(dnDetailModel==null) {
                       dnDetailModel = new DNDetailModel();
                       dnDetailModel.setDETAIL_STATUS("1");
                       dnDetailModel.setSTATUS(0);
                   }
                   dnDetailModel.setAGENT_DN_NO(DNNo);
                   dnDetailModel.setLINE_NO(lineno);
                   dnDetailModel.setITEM_NO(lines[4].trim());
                   dnDetailModel.setGOLFA_CODE(lines[6].trim());
                   if(TextUtils.isEmpty(lines[5].trim())) {
                       String condition = dnDetailModel.getGOLFA_CODE() == null || TextUtils.isEmpty(dnDetailModel.getGOLFA_CODE()) ?
                               dnDetailModel.getITEM_NO() : dnDetailModel.getGOLFA_CODE();
                       MaterialModel materialModel = DbBaseInfo.getInstance().GetItemName(condition);
                       dnDetailModel.setITEM_NAME(materialModel==null?"":materialModel.getMAKTX());
                   }else {
                       dnDetailModel.setITEM_NAME(lines[5].trim());
                   }
                   int dnQty = Integer.parseInt(lines[7].trim());
                   Qty += dnQty;
                   dnDetailModel.setDN_QTY(dnQty);
                   dnDetailModel.setOPER_DATE(new Date());
                   int scanQTY=DbDnInfo.getInstance().GetScanQtyInDNScanModel(DNNo,lines[6].trim(),lineno);
                   dnDetailModel.setSCAN_QTY(scanQTY);
                   dnDetailModels.add(dnDetailModel);
               }
               dnModel.setOPER_DATE(new Date());
               dnModel.setDN_SOURCE(ParamaterModel.DnTypeModel.getDNType());
               dnModel.setDETAILS(dnDetailModels);
               dnModel.setDN_QTY(Qty);
           }
           reader.close();
           dnModels.add(dnModel);
       }
       for(int i=0;i<DNfiles.length;i++) {
           DNfiles[i].delete();
       }
       DbDnInfo.getInstance().InsertDNDB(dnModels);
   }


}