package com.xx.chinetek.method.Upload;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

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
import com.xx.chinetek.method.Sync.SyncBase;
import com.xx.chinetek.method.Sync.SyncDN;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.DNStatusEnum;
import com.xx.chinetek.model.Base.URLModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.xx.chinetek.chineteklib.base.BaseApplication.context;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_UploadDN;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_UploadDN;

/**
 * Created by GHOST on 2017/11/16.
 */

public class UploadDN {


    public static void SumbitDN(final Context context,final DNModel  dnModel,final MyHandler<BaseActivity> mHandler) {
        if(dnModel.getSTATUS()!=3) {
            //判断出库单是否完成
            boolean isFinished=true;
            for(DNDetailModel dnDetailModel:dnModel.getDETAILS()){
                if(dnDetailModel.getSCAN_QTY()!=dnDetailModel.getDN_QTY()){
                    isFinished=false;
                    break;
                }
            }
            if(!isFinished){
                new AlertDialog.Builder(context).setTitle("提示")// 设置对话框标题
                        .setIcon(android.R.drawable.ic_dialog_info)// 设置对话框图
                        .setMessage(context.getResources().getString(R.string.Msg_Upload_DN))
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                DbDnInfo.getInstance().ChangeDNStatusByDnNo(dnModel.getAGENT_DN_NO(), DNStatusEnum.complete);
                                UploadDN.UploadDNToMaps(dnModel,mHandler);
                            }
                        })
                        .setNegativeButton("取消",null)
                        .show();
            }else{
                //提交成功修改单据状态
                DbDnInfo.getInstance().ChangeDNStatusByDnNo(dnModel.getAGENT_DN_NO(), DNStatusEnum.complete);
                UploadDN.UploadDNToMaps(dnModel,mHandler);
            }

        }else{
            MessageBox.Show(context,context.getString(R.string.Msg_Dn_Finished));
        }
    }


    /**
     * 上传出库单到MAPS
     * @param result
     */
    public static boolean AnalysisUploadDNToMapsJson(Context context,String result,String Dnno) {
        try {
            LogUtil.WriteLog(SyncDN.class, TAG_UploadDN, result);
            ReturnMsgModelList<DNModel> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<DNModel>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                //更新出库单状态
                DbDnInfo.getInstance().ChangeDNStatusByDnNo(Dnno, DNStatusEnum.Sumbit);
            } else {
                ArrayList<DNModel> dnModels = returnMsgModel.getModelJson();
                //插入数据
                DbDnInfo.getInstance().InsertDNDB(dnModels);
                MessageBox.Show(context, returnMsgModel.getMessage());
            }
            return false;
        }catch (Exception ex){
            MessageBox.Show(context, ex.getMessage());
        }
        return false;
    }

    public static void UploadDNToMaps(DNModel dnModel, MyHandler<BaseActivity> mHandler){
        final Map<String, String> params = new HashMap<String, String>();
        String dnModelJson= GsonUtil.parseModelToJson(dnModel);
        params.put("DNMODEL", dnModelJson);
        String para = (new JSONObject(params)).toString();
        LogUtil.WriteLog(SyncBase.class, TAG_UploadDN, para);
        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_UploadDN,
                context.getString(R.string.Dia_UploadDN), context, mHandler, RESULT_UploadDN, null,  URLModel.GetURL().UploadNDN, params, null);

    }
}