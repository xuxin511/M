package com.xx.chinetek.method.Upload;

import com.android.volley.Request;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.util.Network.RequestHandler;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.hander.MyHandler;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbLogInfo;
import com.xx.chinetek.method.Sync.SyncBase;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.URLModel;
import com.xx.chinetek.model.DN.LogModel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.xx.chinetek.chineteklib.base.BaseApplication.context;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_DeleteCus;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_UploadCus;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_DeleteCus;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_UploadCus;

/**
 * Created by GHOST on 2017/11/16.
 */

public class UploadNewCus {

    /**
     * 上传新增客户
     */
    public static void AddNewCusToMaps(String CusName,String CustFunc,MyHandler<BaseActivity> mHandler){
        final Map<String, String> params = new HashMap<String, String>();
        String user= GsonUtil.parseModelToJson(ParamaterModel.userInfoModel);
        params.put("CustName", CusName);
        params.put("CustFunc", CustFunc);
        params.put("UserInfoJS", user);
        String para = (new JSONObject(params)).toString();
        DbLogInfo.getInstance().InsertLog(new LogModel("新增客户",para,""));
        LogUtil.WriteLog(SyncBase.class, TAG_UploadCus, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_UploadCus,
                context.getString(R.string.Dia_UploadCus), context, mHandler, RESULT_UploadCus, null,  URLModel.GetURL().UploadCus, params, null);

    }

    /**
     * 删除客户
     */
    public static void DeleteCusToMaps(String customer,String customeName,MyHandler<BaseActivity> mHandler){
        final Map<String, String> params = new HashMap<String, String>();
        String user= GsonUtil.parseModelToJson(ParamaterModel.userInfoModel);
        params.put("Customer", customer);
        params.put("CutomeName", customeName);
        params.put("UserInfoJS", user);
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(SyncBase.class, TAG_DeleteCus, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_DeleteCus,
                context.getString(R.string.Dia_DeleteCus), context, mHandler, RESULT_DeleteCus, null,  URLModel.GetURL().DeleteCus, params, null);

    }
}
