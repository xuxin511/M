package com.xx.chinetek.method.Sync;

import com.android.volley.Request;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.util.Network.RequestHandler;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.hander.MyHandler;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbManager;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.URLModel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.xx.chinetek.chineteklib.base.BaseApplication.context;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncCus;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncDeleteDn;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncMaterial;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncPara;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncCus;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncDeleteDn;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncMaterial;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncPara;

/**
 * Created by GHOST on 2017/11/6.
 */

public class SyncBase {



    private static SyncBase mSyncBase;


    public static SyncBase getInstance() {
        if (null == mSyncBase) {
            synchronized (DbManager.class) {
                if (null == mSyncBase) {
                    mSyncBase = new SyncBase();
                }
            }
        }
        return mSyncBase;
    }

     public void SyncMaterial(MyHandler<BaseActivity> mHandler){
        final Map<String, String> params = new HashMap<String, String>();
        String user= GsonUtil.parseModelToJson(ParamaterModel.userInfoModel);
        params.put("DateString", ParamaterModel.MaterialSyncTime);
        params.put("UserInfoJS", user);
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(SyncBase.class, TAG_SyncMaterial, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SyncMaterial,
                context.getString(R.string.Dia_SyncMaterial), context, mHandler, RESULT_SyncMaterial, null,  URLModel.GetURL().SyncMaterial, params, null);
    }


    public void SyncMaterialRang(MyHandler<BaseActivity> mHandler,int StartIndex,int EndIndex){
        final Map<String, String> params = new HashMap<String, String>();
        String user= GsonUtil.parseModelToJson(ParamaterModel.userInfoModel);
        params.put("DateString", ParamaterModel.MaterialSyncTime);
        params.put("UserInfoJS", user);
        params.put("StartIndex", StartIndex+"");
        params.put("EndIndex", EndIndex+"");
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(SyncBase.class, TAG_SyncMaterial, para);
        RequestHandler.addRequest(Request.Method.POST, TAG_SyncMaterial, mHandler, RESULT_SyncMaterial, null,  URLModel.GetURL().SyncMaterialRang, params, null);
    }

    public void SyncCus(MyHandler<BaseActivity> mHandler){
        final Map<String, String> params = new HashMap<String, String>();
        String user= GsonUtil.parseModelToJson(ParamaterModel.userInfoModel);
        params.put("DateString", ParamaterModel.CustomSyncTime);
        params.put("UserInfoJS", user);
      //  params.put("cusNo", ParamaterModel.PartenerID);
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(SyncBase.class, TAG_SyncCus, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SyncCus,
                context.getString(R.string.Dia_SyncCus), context, mHandler, RESULT_SyncCus, null,  URLModel.GetURL().SyncCus, params, null);
    }

    public void  SyncPara(MyHandler<BaseActivity> mHandler){
        final Map<String, String> params = new HashMap<String, String>();
        String user= GsonUtil.parseModelToJson(ParamaterModel.userInfoModel);
        params.put("DateString", ParamaterModel.ParamaterSyncTime);
        params.put("UserInfoJS", user);
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(SyncBase.class, TAG_SyncPara, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SyncPara,
                context.getString(R.string.Dia_SyncPara), context, mHandler, RESULT_SyncPara, null,  URLModel.GetURL().SyncPara, params, null);

    }

    public void  SyncDeleteDN(MyHandler<BaseActivity> mHandler){
        final Map<String, String> params = new HashMap<String, String>();
        String user= GsonUtil.parseModelToJson(ParamaterModel.userInfoModel);
        params.put("UserInfoJS", user);
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(SyncBase.class, TAG_SyncDeleteDn, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SyncDeleteDn,
                context.getString(R.string.Dia_SyncDeleteDN), context, mHandler, RESULT_SyncDeleteDn, null,  URLModel.GetURL().SyncDeleteDn, params, null);

    }


}
