package com.xx.chinetek.method;

import com.android.volley.Request;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.util.Network.RequestHandler;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.hander.MyHandler;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.mitsubshi.MitApplication;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.URLModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.GPSModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_UploadGPS;

/**
 * Created by GHOST on 2018/2/2.
 */

public class UploadGPS {

   static  String TAG_UploadGPS="UploadGPS";

    public static void UpGPS(final  MyHandler<BaseActivity> mHandler,final ArrayList<DNModel> dnModels){
        //上报GPS
        new Thread(){
            @Override
            public void run() {
                try {
                    ArrayList<GPSModel> gpsModels=new ArrayList<>();
                    for (DNModel dnModel:dnModels) {
                        GPSModel gpsModel=new GPSModel();
                        gpsModel.setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
                        gpsModel.setCUS_DN_NO(dnModel.getCUS_DN_NO());
                        gpsModel.setAGENT_NO(ParamaterModel.PartenerID);
                        gpsModel.setPDA_CODE(ParamaterModel.SerialNo);
                        gpsModel.setUSER_CODE(ParamaterModel.Operater);
                        gpsModel.setGPS_DESC(MitApplication.locationModel);
                        gpsModel.setGPS_LOCATION(MitApplication.gpsModel);
                        gpsModels.add(gpsModel);
                    }
                    final Map<String, String> params = new HashMap<String, String>();
                    String gpsModelString = GsonUtil.parseModelToJson(gpsModels);
                    params.put("GpsModel", gpsModelString);
                    String para = (new JSONObject(params)).toString();
                    LogUtil.WriteLog(UploadGPS.class, TAG_UploadGPS, para);
                    RequestHandler.addRequest(Request.Method.POST, TAG_UploadGPS,
                            mHandler, RESULT_UploadGPS, null, URLModel.GetURL().UploadGPS, params, null);
                }catch (Exception ex){
                    LogUtil.WriteLog(UploadGPS.class, TAG_UploadGPS, ex.getMessage());
                }
            }
        }.start();
    }
}
