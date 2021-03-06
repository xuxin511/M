package com.xx.chinetek.mitsubshi;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.xx.chinetek.adapter.GridViewItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.model.ReturnMsgModelList;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.LoadingDialog;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.FTP.FtpUtil;
import com.xx.chinetek.method.SharePreferUtil;
import com.xx.chinetek.method.Sync.SyncBase;
import com.xx.chinetek.mitsubshi.Bulkupload.Bulkupload;
import com.xx.chinetek.mitsubshi.DN.DeliveryStart;
import com.xx.chinetek.mitsubshi.Query.QueryList;
import com.xx.chinetek.model.Base.BaseparaModel;
import com.xx.chinetek.model.Base.CustomModel;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.SyncParaModel;
import com.xx.chinetek.model.DN.DNTypeModel;
import com.xx.chinetek.model.DN.DeletedDN;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncCus;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncDeleteDn;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncMaterial;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncPara;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncCus;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncDeleteDn;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncMaterial;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncPara;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewInject(R.id.gv_Function)
    GridView gridView;
    GridViewItemAdapter adapter;
    Context context=MainActivity.this;
    int startIndex=0;
    LoadingDialog dialog;

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_SyncMaterial:
                AnalysisSyncMaterialJson((String) msg.obj);
                break;
            case RESULT_SyncCus:
                AnalysisSyncCustomJson((String) msg.obj);
                break;
            case RESULT_SyncPara:
                AnalysisSyncParamaterJson((String) msg.obj);
                break;
            case RESULT_SyncDeleteDn:
                AnalysisSyncDeleteDnJson((String) msg.obj);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                if(dialog!=null){
                    dialog.dismiss();
                }
                ToastUtil.show("获取请求失败_____"+ msg.obj);
                break;
        }
    }


    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.main),false);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        List<Map<String, Object>> data_list = getData();
        adapter = new GridViewItemAdapter(context,data_list);
        gridView.setAdapter(adapter);
        ParamaterModel.DnTypeModel= SharePreferUtil.ReadDNTypeShare(context);
        if(ParamaterModel.DnTypeModel==null) {
            ParamaterModel.DnTypeModel=new DNTypeModel();
            ParamaterModel.DnTypeModel.setSelectRule(0);
        }
    }

    @Event(value = R.id.gv_Function,type = AdapterView.OnItemClickListener.class)
    private void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LinearLayout linearLayout = (LinearLayout) gridView.getAdapter().getView(position, view, null);
        TextView textView = (TextView) linearLayout.getChildAt(1);
        if(!textView.getText().toString().equals(getString(R.string.sync))){
            if(!DbBaseInfo.getInstance().HasMaterialInfo()){
                MessageBox.Show(context,getString(R.string.Msg_No_Sync));
                return;
            }
        }

        Intent intent = new Intent();
        if (textView.getText().toString().equals(getString(R.string.outputscan))){
            intent.setClass(context, DeliveryStart.class);
            startActivityLeft(intent);
        }
        else if (textView.getText().toString().equals(getString(R.string.exceptionList))) {
            intent.setClass(context, ExceptionList.class);
            startActivityLeft(intent);
        }
        else if (textView.getText().toString().equals(getString(R.string.Query))) {
            intent.setClass(context, QueryList.class);
            startActivityLeft(intent);
        }
        else if (textView.getText().toString().equals(getString(R.string.Bulkupload))) {
            intent.setClass(context, Bulkupload.class);
            startActivityLeft(intent);
        }
        else if (textView.getText().toString().equals(getString(R.string.sync))) {
            BaseApplication.DialogShowText = getString(R.string.Dia_SyncMaterial);
            dialog =new LoadingDialog(context);
            dialog.show();
            startIndex=0;
            //同步物料
            SyncBase.getInstance().SyncMaterialRang(mHandler,startIndex,startIndex+10000);
        }

    }

    /**
     * 同步物料
     * @param result
     */
    void AnalysisSyncMaterialJson(String result){
        LogUtil.WriteLog(MainActivity.class,TAG_SyncMaterial,result);
        try {

            JsonReader jsonReader = new JsonReader(new StringReader(result));//其中jsonContext为String类型的Json数据
            jsonReader.setLenient(true);
            ReturnMsgModelList<MaterialModel> returnMsgModel = GsonUtil.getGsonUtil().fromJson(jsonReader, new TypeToken<ReturnMsgModelList<MaterialModel>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
               ArrayList<MaterialModel> materialModels = returnMsgModel.getModelJson();
               if(materialModels!=null && materialModels.size()!=0) {
                   //插入数据
                   DbBaseInfo.getInstance().InsertMaterialDB(materialModels);
                   startIndex+=10000;
                   SyncBase.getInstance().SyncMaterialRang(mHandler,startIndex,startIndex+10000);
                    return;
               }
                ParamaterModel.MaterialSyncTime=returnMsgModel.getMessage();
                //保存同步时间
                SharePreferUtil.SetSyncTimeShare("MaterialSyncTime",ParamaterModel.MaterialSyncTime);
               //同步客户代理商
                SyncBase.getInstance().SyncCus(mHandler);
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
            dialog.dismiss();
        }catch (Exception ex) {
            dialog.dismiss();
            ToastUtil.show(ex.getMessage());
            LogUtil.WriteLog(MainActivity.class,"MainActivity-SyncMaterial", ex.toString());
        }
    }


    /**
     * 同步客户代理商
     * @param result
     */
    void AnalysisSyncCustomJson(String result){
        LogUtil.WriteLog(MainActivity.class,TAG_SyncCus,result);
        try {
            ReturnMsgModelList<CustomModel> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<CustomModel>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                ArrayList<CustomModel> customModels = returnMsgModel.getModelJson();
                //删除客户表
                DbBaseInfo.getInstance().DeleteCustomDB();
                //插入数据
                DbBaseInfo.getInstance().InsertCustomDB(customModels);
                if(ParamaterModel.PartenerName==null ||  ParamaterModel.PartenerName.equals("")) {
                    ParamaterModel.PartenerName = DbBaseInfo.getInstance().GetCustomNameById(ParamaterModel.PartenerID);
                    SharePreferUtil.SetShare(context);
                }
                ParamaterModel.CustomSyncTime=returnMsgModel.getMessage();;
                //保存同步时间
                SharePreferUtil.SetSyncTimeShare("CustomSyncTime",ParamaterModel.CustomSyncTime);
                //同步参数
                SyncBase.getInstance().SyncPara(mHandler);
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
        }catch (Exception ex) {
            ToastUtil.show(ex.getMessage());
            LogUtil.WriteLog(MainActivity.class,"MainActivity-SyncCustom", ex.toString());
        }
    }

    /**
     * 同步参数
     * @param result
     */
    void AnalysisSyncParamaterJson(String result){
        LogUtil.WriteLog(MainActivity.class,TAG_SyncPara,result);
        try {
            ReturnMsgModelList<SyncParaModel> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<SyncParaModel>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                ArrayList<SyncParaModel> syncParaModels = returnMsgModel.getModelJson();
                //插入数据
                DbBaseInfo.getInstance().InsertParamaterDB(syncParaModels);
                ParamaterModel.ParamaterSyncTime=returnMsgModel.getMessage();
                if(syncParaModels.size()!=0){
                    String ParaString=syncParaModels.get(0).getValue().replace("!","\"")
                            .replace("#","{").replace("?",":");
                    Type type = new TypeToken<BaseparaModel>(){}.getType();
                    ParamaterModel.baseparaModel=GsonUtil.parseJsonToModel(ParaString,type);
                    FtpUtil.ftp=null;
                    SharePreferUtil.SetShare(context);
                }
                //保存同步时间
                SharePreferUtil.SetSyncTimeShare("ParamaterSyncTime",ParamaterModel.ParamaterSyncTime);
                SyncBase.getInstance().SyncDeleteDN(mHandler);
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
        }catch (Exception ex) {
            ToastUtil.show(ex.getMessage());
            LogUtil.WriteLog(MainActivity.class,"MainActivity-SyncParamater", ex.toString());
        }
    }

    /**
     * 同步删除DN单据
     * @param result
     */
    void  AnalysisSyncDeleteDnJson(String result) {
        LogUtil.WriteLog(MainActivity.class, TAG_SyncDeleteDn, result);
        try {
            ReturnMsgModelList<DeletedDN> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<DeletedDN>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                ArrayList<DeletedDN> deletedDNS = returnMsgModel.getModelJson();
                for (DeletedDN deletedDN: deletedDNS) {
                    String dnNo=deletedDN.getCUS_DN_NO()==null || !deletedDN.getCUS_DN_NO().trim().equals("")?
                            deletedDN.getAGENT_DN_NO():deletedDN.getCUS_DN_NO();
                    DbDnInfo.getInstance().DeleteDN(dnNo);
                }

                MessageBox.Show(context,getString(R.string.Dia_SyncSuccess));
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
        }catch (Exception ex) {
            ToastUtil.show(ex.getMessage());
            LogUtil.WriteLog(MainActivity.class,"MainActivity-SyncDeleteDn", ex.toString());
        }
    }


    /**
     * 界面元素
     * @return
     */
    public List<Map<String, Object>> getData(){
        List<Map<String, Object>> data_list = new ArrayList<Map<String, Object>>();
        ArrayList<Integer>  itemIconList=new ArrayList<>();
        ArrayList<String>  itemNamesList=new ArrayList<>();
        itemIconList.add(R.drawable.delivery);
        itemNamesList.add(getString(R.string.outputscan));
        itemIconList.add(R.drawable.exception);
        itemNamesList.add(getString(R.string.exceptionList));
        itemIconList.add(R.drawable.query);
        itemNamesList.add(getString(R.string.Query));
        itemIconList.add(R.drawable.upload);
        itemNamesList.add(getString(R.string.Bulkupload));
        itemIconList.add(R.drawable.sync);
        itemNamesList.add(getString(R.string.sync));

            //cion和iconName的长度是相同的，这里任选其一都可以
            for (int i = 0; i < itemIconList.size(); i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("image", itemIconList.get(i));
                map.put("text", itemNamesList.get(i));
                data_list.add(map);
            }


        return data_list;
    }
}
