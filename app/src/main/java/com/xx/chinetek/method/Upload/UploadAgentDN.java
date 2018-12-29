package com.xx.chinetek.method.Upload;

import android.text.TextUtils;

import com.android.volley.Request;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.util.Network.RequestHandler;
import com.xx.chinetek.chineteklib.util.Network.RequestThirdHandler;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.hander.MyHandler;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.UploadGPS;
import com.xx.chinetek.mitsubshi.MitApplication;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.URLModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;
import com.xx.chinetek.model.GPSModel;
import com.xx.chinetek.model.Third.ThirdUplodDN;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xx.chinetek.chineteklib.base.BaseApplication.context;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_GetVoucherHead;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SubmitQRScan;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_UploadCusToAgent;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_UploadDN;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_UploadGPS;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SGetVoucherHead;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SubmitQRScan;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_UploadCusToAgent;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_UploadDN;

/**
 * Created by GHOST on 2018/12/28.
 */

public class UploadAgentDN {
    public static void UpAgentDN(final MyHandler<BaseActivity> mHandler,final String CUS_DN_NO ,final  String Remark,final DNDetailModel dndetail){
        new Thread(){
            @Override
            public void run() {
                try {
                   List<ThirdUplodDN> thirdUplodDNList=new ArrayList<>();
                  //  for (DNModel dn:dnModels) {
                      //  for (DNDetailModel dndetail : dn.getDETAILS()) {
                            ThirdUplodDN thirdUplodDN = new ThirdUplodDN();
                            thirdUplodDN.setGoodCode(dndetail.getITEM_NO());
                            thirdUplodDN.setVoucherNo(CUS_DN_NO);
                            thirdUplodDN.setRowNum(dndetail.getLINE_NO());
                            thirdUplodDN.setGolfaCode(dndetail.getGOLFA_CODE()==null?"":dndetail.getGOLFA_CODE());
                            thirdUplodDN.setType(dndetail.getITEM_NAME());
                            thirdUplodDN.setSAPCode(dndetail.getITEM_NO());
                            thirdUplodDN.setScanNum(dndetail.getSCAN_QTY());
                            thirdUplodDN.setScanTime(dndetail.getSERIALS().get(0).getDEAL_SALE_DATE());
                            thirdUplodDN.setProductData(dndetail.getSERIALS().get(0).getPACKING_DATE());
                            thirdUplodDN.setPDACode(ParamaterModel.userInfoModel.getPDA_CODE());
                            thirdUplodDN.setScaner(ParamaterModel.userInfoModel.getUSER_CODE());
                            thirdUplodDN.setCptzm(Remark==null?"":Remark);
                            thirdUplodDN.setSerialNo("");
                            thirdUplodDN.setCountry("");
                            thirdUplodDN.setPlaceCode("");
                            if (dndetail.getGOLFA_CODE() != null && !TextUtils.isEmpty(dndetail.getGOLFA_CODE())) {
                                for (DNScanModel dnscan : dndetail.getSERIALS()) {
                                    thirdUplodDN.setProductData(dnscan.getPACKING_DATE());
                                    thirdUplodDN.setSerialNo(dnscan.getSERIAL_NO());
                                    thirdUplodDN.setScanNum(1);
                                    thirdUplodDN.setCountry(dnscan.getCOUNTRY());
                                    thirdUplodDN.setPlaceCode(dnscan.getREGION());
                                    thirdUplodDNList.add(thirdUplodDN);
                                }
                            }else{
                                thirdUplodDNList.add(thirdUplodDN);
                            }

                        //}

                  //  }
                    String UplodDNList = GsonUtil.parseModelToJson(thirdUplodDNList);
                    RequestThirdHandler.addThirdRequest(Request.Method.POST, TAG_SubmitQRScan, mHandler, RESULT_SubmitQRScan,
                            null,  URLModel.GetURL().SubmitQRScan, UplodDNList, null);
                }catch (Exception ex){
                    LogUtil.WriteLog(UploadGPS.class, TAG_SubmitQRScan, ex.getMessage());
                }
            }
        }.start();
    }
}
