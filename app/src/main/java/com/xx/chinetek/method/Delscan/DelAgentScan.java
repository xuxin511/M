package com.xx.chinetek.method.Delscan;

import com.android.volley.Request;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.util.Network.RequestThirdHandler;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.hander.MyHandler;
import com.xx.chinetek.model.Base.URLModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNScanModel;
import com.xx.chinetek.model.Third.ThirdDeleteQR;

import java.util.ArrayList;
import java.util.List;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_DeleteQRScan;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SubmitQRScan;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_DeleteQRScan;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SubmitQRScan;

/**
 * Created by GHOST on 2018/12/28.
 */

public class DelAgentScan {

    public static void DelScan(final MyHandler<BaseActivity> mHandler, final DNDetailModel dnDetailModel, final String dnno,final   List<DNScanModel> dnScanModels){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<ThirdDeleteQR> thirdDeleteQRS=new ArrayList<>();
                    for (DNScanModel dnscanModel: dnScanModels) {
                        ThirdDeleteQR thirdDeleteQR=new ThirdDeleteQR();
                        thirdDeleteQR.setRowNo(dnscanModel.getLINE_NO());
                        thirdDeleteQR.setVoucherNo(dnno);
                        thirdDeleteQR.setType(dnscanModel.getITEM_NAME());
                        thirdDeleteQR.setGolfaCode(dnscanModel.getGOLFA_CODE()==null?"":dnscanModel.getGOLFA_CODE());
                        thirdDeleteQR.setSerialNo(dnscanModel.getMAT_TYPE()==2?"":dnscanModel.getSERIAL_NO());
                        thirdDeleteQRS.add(thirdDeleteQR);
                        if(dnscanModel.getMAT_TYPE()==2)
                            break;
                    }
                    String UplodDelList = GsonUtil.parseModelToJson(thirdDeleteQRS);
                    RequestThirdHandler.addThirdRequest(Request.Method.POST, TAG_DeleteQRScan, mHandler, RESULT_DeleteQRScan,
                            null,  URLModel.GetURL().DeleteQRScan, UplodDelList, null);
                }
            }).start();
    }
}
