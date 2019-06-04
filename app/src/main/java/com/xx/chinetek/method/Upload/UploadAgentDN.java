package com.xx.chinetek.method.Upload;

import android.text.TextUtils;

import com.android.volley.Request;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.util.Network.RequestHandler;
import com.xx.chinetek.chineteklib.util.Network.RequestThirdHandler;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.hander.MyHandler;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbDnInfo;
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
    public static void UpAgentDN(final MyHandler<BaseActivity> mHandler,final DNModel dnModel,String DetailRemark){
//        new Thread(){
//            @Override
//            public void run() {
                try {
                    List<DNDetailModel> dnDetailModels= DbDnInfo.getInstance().GetDndetailsByFlag2(dnModel.getAGENT_DN_NO());
                    List<ThirdUplodDN> thirdUplodDNList = new ArrayList<>();
                    for (DNDetailModel dndetail:dnDetailModels) {
                        ThirdUplodDN thirdUplodDN = new ThirdUplodDN();
                        thirdUplodDN.setGoodCode(dndetail.getITEM_NO());
                        thirdUplodDN.setVoucherNo(dnModel.getCUS_DN_NO());
                        thirdUplodDN.setRowNum(dndetail.getLINE_NO());
                        thirdUplodDN.setGolfaCode(dndetail.getGOLFA_CODE() == null ? "" : dndetail.getGOLFA_CODE());
                        thirdUplodDN.setType(dndetail.getITEM_NAME());
                        thirdUplodDN.setSAPCode(dndetail.getITEM_NO());
                        thirdUplodDN.setScanNum(dndetail.getSCAN_QTY());
                        thirdUplodDN.setScanTime(dndetail.getSERIALS().get(0).getDEAL_SALE_DATE());
                        thirdUplodDN.setProductData(dndetail.getSERIALS().get(0).getPACKING_DATE()==null?"":dndetail.getSERIALS().get(0).getPACKING_DATE());
                        thirdUplodDN.setPDACode(ParamaterModel.userInfoModel.getPDA_CODE());
                        thirdUplodDN.setScaner(ParamaterModel.userInfoModel.getUSER_CODE());
                        thirdUplodDN.setCptzm(DetailRemark);//dnModel.getREMARK() == null ? "" : dnModel.getREMARK());
                        thirdUplodDN.setSerialNo("");
                        thirdUplodDN.setCountry("");
                        thirdUplodDN.setPlaceCode("");
                        if (dndetail.getGOLFA_CODE() != null && !TextUtils.isEmpty(dndetail.getGOLFA_CODE())) {
                            for (DNScanModel dnscan : dndetail.getSERIALS()) {
                                if(dnscan.getFLAG()==0) continue;
                                thirdUplodDNList.add(0,thirdUplodDN.clone());
                                thirdUplodDNList.get(0).setProductData(dnscan.getPACKING_DATE()==null?"":dnscan.getPACKING_DATE());
                                thirdUplodDNList.get(0).setSerialNo(dnscan.getSERIAL_NO());
                                thirdUplodDNList.get(0).setScanNum(1);
                                thirdUplodDNList.get(0).setCountry(dnscan.getCOUNTRY());
                                thirdUplodDNList.get(0).setPlaceCode(dnscan.getREGION()==null?"":dnscan.getREGION());
                            }
                        } else {
                            thirdUplodDNList.add(thirdUplodDN);
                        }
                    }
                    if(thirdUplodDNList.size()!=0) {
                        String UplodDNList = GsonUtil.parseModelToJson(thirdUplodDNList);
                        RequestThirdHandler.addThirdRequest(Request.Method.POST, TAG_SubmitQRScan,
                                mHandler, RESULT_SubmitQRScan,
                                null, URLModel.GetURL().SubmitQRScan, UplodDNList, null);
                    }
                }catch (Exception ex){
                    LogUtil.WriteLog(UploadGPS.class, TAG_SubmitQRScan, ex.getMessage());
                }
//            }
//        }.start();
    }
}
