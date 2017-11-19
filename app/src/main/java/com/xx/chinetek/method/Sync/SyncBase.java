package com.xx.chinetek.method.Sync;

import com.android.volley.Request;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.util.Network.RequestHandler;
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
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncMaterial;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncPara;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncCus;
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
        params.put("value", ParamaterModel.MaterialSyncTime);
//        params.put("DateString", ParamaterModel.MaterialSyncTime);
//        params.put("UserInfoJS", "");
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(SyncBase.class, TAG_SyncMaterial, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SyncMaterial,
                context.getString(R.string.Dia_SyncMaterial), context, mHandler, RESULT_SyncMaterial, null,  URLModel.GetURL().SyncMaterial, params, null);
    }

    public void SyncCus(MyHandler<BaseActivity> mHandler){
        final Map<String, String> params = new HashMap<String, String>();
        params.put("Synctime", ParamaterModel.CustomSyncTime);
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(SyncBase.class, TAG_SyncCus, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SyncCus,
                context.getString(R.string.Dia_SyncCus), context, mHandler, RESULT_SyncCus, null,  URLModel.GetURL().SyncCus, params, null);

    }

    public void  SyncPara(MyHandler<BaseActivity> mHandler){
        final Map<String, String> params = new HashMap<String, String>();
        params.put("Synctime", ParamaterModel.ParamaterSyncTime);
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(SyncBase.class, TAG_SyncPara, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SyncPara,
                context.getString(R.string.Dia_SyncPara), context, mHandler, RESULT_SyncPara, null,  URLModel.GetURL().SyncPara, params, null);

    }


}
