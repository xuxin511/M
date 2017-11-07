package com.xx.chinetek.method.Sync;

import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.model.ReturnMsgModelList;
import com.xx.chinetek.chineteklib.util.Network.RequestHandler;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.hander.MyHandler;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.SharePreferUtil;
import com.xx.chinetek.mitsubshi.MainActivity;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.URLModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNTypeModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
     * @param dnTypeModel
     * @return
     */
    public static Boolean SyncMail(DNTypeModel  dnTypeModel){
        return false;
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
     * 获取USB
     * @param dnTypeModel
     * @return
     */
    public static Boolean SyncUsb(DNTypeModel  dnTypeModel){
        return false;
    }


    /**
     * MAPS同步单据返回值
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

}
