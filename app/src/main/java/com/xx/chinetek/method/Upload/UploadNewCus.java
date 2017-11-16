package com.xx.chinetek.method.Upload;

import com.android.volley.Request;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.util.Network.RequestHandler;
import com.xx.chinetek.chineteklib.util.hander.MyHandler;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.Sync.SyncBase;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.URLModel;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.xx.chinetek.chineteklib.base.BaseApplication.context;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_UploadCus;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_UploadCus;

/**
 * Created by GHOST on 2017/11/16.
 */

public class UploadNewCus {

    /**
     * 上传新增客户
     */
    public static void AddNewCusToMaps(String CusName,MyHandler<BaseActivity> mHandler){
        final Map<String, String> params = new HashMap<String, String>();
        params.put("Custom", CusName);
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(SyncBase.class, TAG_UploadCus, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_UploadCus,
                context.getString(R.string.Dia_UploadCus), context, mHandler, RESULT_UploadCus, null,  URLModel.GetURL().UploadCus, params, null);

    }
}
