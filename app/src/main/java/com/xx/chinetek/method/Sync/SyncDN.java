package com.xx.chinetek.method.Sync;

import android.os.Message;

import com.android.volley.Request;
import com.xx.chinetek.chineteklib.base.BaseActivity;
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
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncDnDetail;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncException;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncDn;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncDnDetail;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncException;

/**
 * Created by GHOST on 2017/11/2.
 */

public class SyncDN {
    /**
     * 获取异常单据
     * @param mHandler
     * @return
     */
    public static void SyncException(MyHandler<BaseActivity> mHandler){
        //MAPS获取单据
        final Map<String, String> params = new HashMap<String, String>();
        String user= GsonUtil.parseModelToJson(ParamaterModel.userInfoModel);
        params.put("DateString", "");
        params.put("UserInfoJS", user);
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(SyncBase.class, TAG_SyncException, para);
        RequestHandler.addRequest(Request.Method.POST, TAG_SyncException, mHandler, RESULT_SyncException, null,  URLModel.GetURL().SyncException, params, null);
    }



    /**
     * 获取MAPS单据
     * @param mHandler
     * @return
     */
    public static void SyncMAPS(MyHandler<BaseActivity> mHandler){
        //MAPS获取单据
        final Map<String, String> params = new HashMap<String, String>();
        String user= GsonUtil.parseModelToJson(ParamaterModel.userInfoModel);
        params.put("DateString", "");
        params.put("UserInfoJS", user);
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(SyncBase.class, TAG_SyncDn, para);
        RequestHandler.addRequest(Request.Method.POST, TAG_SyncDn, mHandler, RESULT_SyncDn, null,  URLModel.GetURL().SyncDn, params, null);
    }

    /**
     * 获取 出库单明细
     * @param mHandler
     */
    public static void SyncMAPSDetail(String dnNo,MyHandler<BaseActivity> mHandler){
        //MAPS获取单据明细
        final Map<String, String> params = new HashMap<String, String>();
        String user= GsonUtil.parseModelToJson(ParamaterModel.userInfoModel);
        params.put("DN_NO", dnNo);
        params.put("UserInfoJS", user);
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(SyncBase.class, TAG_SyncDnDetail, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SyncDnDetail,context.getString(R.string.Dia_SyncDnDetail) ,context,mHandler, RESULT_SyncDnDetail, null,  URLModel.GetURL().SyncDnDetail, params, null);
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
                    MailUtil.GetMail(ParamaterModel.baseparaModel.getMailModel(), mHandler);
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
                    FtpUtil.FtpDownDN(ParamaterModel.baseparaModel.getFtpModel(),mHandler);
                }catch (Exception ex){
                    Message msg = mHandler.obtainMessage(NetworkError.NET_ERROR_CUSTOM, ex.getMessage());
                    mHandler.sendMessage(msg);
                }
            }
        }.start();
    }


    /**
     * 读取目录下文件获取出库单
     * @return
     * @throws Exception
     */
   public static  ArrayList<DNModel> DNFromFiles() throws Exception{
       File[] DNfiles=new File(ParamaterModel.DownDirectory).listFiles();
       ArrayList<DNModel> dnModels=new ArrayList<>();
       String ErrorDN="";
       for(int i=0;i<DNfiles.length;i++) {
           Boolean isSelfDN=true;//判断单据是否为登陆代理商所有
           Boolean isMitMaterials=false; //是否存在多条主数据记录
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
                   if (firstIndex == 0) {
                       dnModel = DbDnInfo.getInstance().GetLoaclDN(DNNo);
                       if (dnModel == null) {
                           dnModel = new DNModel();
                           dnModel.setSTATUS(1);
                           dnModel.setDN_STATUS("AC");
                       }
                       firstIndex++;

                       dnModel.setAGENT_DN_NO(DNNo);
                       String cusNo = DbBaseInfo.getInstance().GetCustomName(lines[2].trim());
                       //判断代理商导入文件是否属于该代理商所有
                       if (cusNo == null || !cusNo.equals(ParamaterModel.PartenerID)) {
                           isSelfDN = false;
                           dnModel = null;
                           ErrorDN += file.getName() + "\n";
                           break;
                       }
                       dnModel.setLEVEL_2_AGENT_NO(cusNo);
                       dnModel.setLEVEL_2_AGENT_NAME(lines[2].trim());
                       cusNo = DbBaseInfo.getInstance().GetCustomName(lines[3].trim());
                       dnModel.setCUSTOM_NO(cusNo);
                       dnModel.setCUSTOM_NAME(lines[3].trim());
                   }

                   int lineno = Integer.parseInt(lines[1].trim());
                   DNDetailModel dnDetailModel = DbDnInfo.getInstance().GetLoaclDNDetail(DNNo, lineno);
                   if (dnDetailModel == null) {
                       dnDetailModel = new DNDetailModel();
                       dnDetailModel.setDETAIL_STATUS("AC");
                       dnDetailModel.setSTATUS(0);
                   }
                   dnDetailModel.setAGENT_DN_NO(DNNo);
                   dnDetailModel.setLINE_NO(lineno);
                   dnDetailModel.setITEM_NO(lines[4].trim());
                   dnDetailModel.setITEM_NAME(lines[5].trim());
                   dnDetailModel.setGOLFA_CODE(lines[6].trim());
                   dnDetailModel.setFlag(0);
                   List<MaterialModel> materialModels = DbBaseInfo.getInstance().GetItems(lines[4].trim(), lines[5].trim(), lines[6].trim());
                   if (materialModels!=null && materialModels.size() > 0) {//materialModels.size() == 1
                       dnDetailModel.setITEM_NO(materialModels.get(0).getMATNR());
                       dnDetailModel.setITEM_NAME(materialModels.get(0).getMAKTX());
                       dnDetailModel.setGOLFA_CODE(materialModels.get(0).getBISMT());
                   }//else
                   if(materialModels.size() > 1) {
                       dnDetailModel.setFlag(1);
                       isMitMaterials = true;
                   }
                   if (dnDetailModel.getGOLFA_CODE().equals("")) {
                       //dnDetailModel.setFlag(1);
                      // isMitMaterials = true;
                       //dnDetailModel.setGOLFA_CODE("匹配主数据失败");
                       continue;
                   }
                   int dnQty = Integer.parseInt(lines[7].trim());
                   Qty += dnQty;
                   dnDetailModel.setDN_QTY(dnQty);
                   dnDetailModel.setOPER_DATE(new Date());
                   int scanQTY = DbDnInfo.getInstance().GetScanQtyInDNScanModel(DNNo, lines[6].trim(), lineno);
                   dnDetailModel.setSCAN_QTY(scanQTY);
                   dnDetailModels.add(dnDetailModel);

               }
               if(isSelfDN) {
                   dnModel.setFlag(isMitMaterials?1:0);
                   dnModel.setOPER_DATE(new Date());
                   dnModel.setDN_DATE(new Date());
                   dnModel.setDN_SOURCE(ParamaterModel.DnTypeModel.getDNType());
                   dnModel.setDETAILS(dnDetailModels);
                   dnModel.setDN_QTY(Qty);
                   if (ParamaterModel.DnTypeModel.getDNType() == 2) { //同步方式为FTP，记录文件名
                       dnModel.setFtpFileName(file.getName());
                   }
               }
           }
           reader.close();
           if(dnModel!=null)
               dnModels.add(dnModel);
       }
       for(int i=0;i<DNfiles.length;i++) {
           DNfiles[i].delete();
       }

        if(!ErrorDN.equals("")){
            MessageBox.Show(context,context.getString(R.string.Msg_ImportDNError)+ErrorDN);
        }
       return dnModels;
      // DbDnInfo.getInstance().InsertDNDB(dnModels);
   }


}
