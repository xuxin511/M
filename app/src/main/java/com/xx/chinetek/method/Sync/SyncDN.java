package com.xx.chinetek.method.Sync;

import android.os.Message;

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
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.Mail.MailUtil;
import com.xx.chinetek.method.SharePreferUtil;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.URLModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNTypeModel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xx.chinetek.chineteklib.base.BaseApplication.context;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncDn;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncDn;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncMaterial;

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
     * @param dnTypeModel
     * @return
     */
    public static Boolean SyncFtp(DNTypeModel  dnTypeModel){
        return false;
    }




    /**
     * MAPS同步出库单
     * @param result
     */
   public static ArrayList<DNModel> AnalysisSyncMAPSDNJson(String result){
        LogUtil.WriteLog(SyncDN.class,TAG_SyncDn,result);
        try {
            ReturnMsgModelList<DNModel> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<DNModel>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                ArrayList<DNModel> dnModels = returnMsgModel.getModelJson();
                //插入数据
                DbDnInfo.getInstance().InsertDNDB(dnModels);
                ParamaterModel.DNSyncTime="";
                //保存同步时间
                SharePreferUtil.SetSyncTimeShare(context);
                //获取DN表头
                return DbDnInfo.getInstance().GetLoaclDN();
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
        }catch (Exception ex) {
            MessageBox.Show(context,ex.getMessage());
            LogUtil.WriteLog(SyncDN.class,TAG_SyncMaterial,result);
        }
       return  new ArrayList<>();
   }


    /**
     * 读取目录下文件获取出库单
     * @return
     * @throws Exception
     */
   public static ArrayList<DNModel> DNFromFiles() throws Exception{
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
               while ((line = reader.readLine()) != null) {
                   String[] lines = line.split(",");
                   String DNNo = lines[0].trim();
                   dnModel.setAGENT_DN_NO(DNNo);
                   dnModel.setLEVEL_2_AGENT_NO(lines[2].trim());
                   dnModel.setCUSTOM_NO(lines[3].trim());
                   DNDetailModel dnDetailModel = new DNDetailModel();
                   dnDetailModel.setAGENT_DN_NO(DNNo);
                   dnDetailModel.setLINE_NO(Integer.parseInt(lines[1].trim()));
                   dnDetailModel.setITEM_NO(lines[4].trim());
                   dnDetailModel.setGOLFA_CODE(lines[6].trim());
                   dnDetailModel.setITEM_ZMAKTX(lines[5].trim());
                   int dnQty = Integer.parseInt(lines[7].trim());
                   Qty += dnQty;
                   dnDetailModel.setDN_QTY(dnQty);
                   dnDetailModel.setDETAIL_STATUS("1");
                   dnDetailModel.setSCAN_QTY(0);
                   dnDetailModels.add(dnDetailModel);
               }
               dnModel.setDN_STATUS(1);
               dnModel.setDetailModels(dnDetailModels);
               dnModel.setDN_QTY(Qty);
           }
           reader.close();
           file.delete();
           dnModels.add(dnModel);
       }
       DbDnInfo.getInstance().InsertDNDB(dnModels);
       return DbDnInfo.getInstance().GetLoaclDN();
   }

}
