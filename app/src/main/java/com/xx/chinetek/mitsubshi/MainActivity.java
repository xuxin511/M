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
import com.xx.chinetek.adapter.GridViewItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.model.ReturnMsgModelList;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.SharePreferUtil;
import com.xx.chinetek.method.Sync.SyncBase;
import com.xx.chinetek.mitsubshi.Bulkupload.Bulkupload;
import com.xx.chinetek.mitsubshi.DN.DeliveryStart;
import com.xx.chinetek.model.Base.CustomModel;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.SyncParaModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncCus;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncMaterial;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncPara;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncCus;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncMaterial;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncPara;

@ContentView(R.layout.activity_main)
public class MainActivity extends BaseActivity {

    @ViewInject(R.id.gv_Function)
    GridView gridView;
    GridViewItemAdapter adapter;
    Context context=MainActivity.this;


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
            case NetworkError.NET_ERROR_CUSTOM:
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
        else if (textView.getText().toString().equals(getString(R.string.Bulkupload))) {
            intent.setClass(context, Bulkupload.class);
            startActivityLeft(intent);
        }
        else if (textView.getText().toString().equals(getString(R.string.sync))) {
//            //测试
            ArrayList<MaterialModel> materialModels = new ArrayList<>();
            for(int i=0;i<20;i++) {
                MaterialModel materialModel = new MaterialModel();
                materialModel.setMATNR("1120000001");
                materialModel.setBISMT("1W4A31");
                materialModel.setMAKTX("物料名称1");
                materialModel.setZMAKTX("物料名称1-长");
                materialModels.add(materialModel);
                materialModel = new MaterialModel();
                materialModel.setMATNR("1120000002");
                materialModel.setBISMT("09M473");
                materialModel.setMAKTX("物料名称2");
                materialModel.setZMAKTX("物料名称2-长");
                materialModels.add(materialModel);
            }
            ArrayList<CustomModel> customModels = new ArrayList<>();
            for(int i=0;i<25;i++){
                CustomModel customModel =new CustomModel();
                customModel.setCUSTOMER("1"+i+"2345");
                customModel.setNAME("客户名称"+i);
                customModel.setPARTNER_FUNCTION(i%2==0?"Z2":"Z3");
                customModels.add(customModel);
            }
            ArrayList<SyncParaModel> syncParaModels = new ArrayList<>();
            SyncParaModel syncParaModel = new SyncParaModel();
            syncParaModel.setKey("rule");
            syncParaModel.setValue("211#***#");
            syncParaModels.add(syncParaModel);

            //插入数据
            try {
                DbBaseInfo.getInstance().InsertMaterialDB(materialModels);
                DbBaseInfo.getInstance().InsertCustomDB(customModels);
                DbBaseInfo.getInstance().InsertParamaterDB(syncParaModels);
            }catch (Exception ex){
                String str=ex.getMessage();
            }
            //获取同步时间
            SharePreferUtil.ReadSyncTimeShare(context);
            //同步物料
            SyncBase.getInstance().SyncMaterial(mHandler);
        }


    }

    /**
     * 同步物料
     * @param result
     */
    void AnalysisSyncMaterialJson(String result){
        LogUtil.WriteLog(MainActivity.class,TAG_SyncMaterial,result);
        try {
            ReturnMsgModelList<MaterialModel> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<MaterialModel>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
               ArrayList<MaterialModel> materialModels = returnMsgModel.getModelJson();
               //插入数据
                DbBaseInfo.getInstance().InsertMaterialDB(materialModels);
                ParamaterModel.MaterialSyncTime="";
                //保存同步时间
                SharePreferUtil.SetSyncTimeShare(context);
               //同步客户代理商
                SyncBase.getInstance().SyncCus(mHandler);
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
        }catch (Exception ex) {
            MessageBox.Show(context,ex.getMessage());
            LogUtil.WriteLog(MainActivity.class,TAG_SyncMaterial,result);
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
                //插入数据
                DbBaseInfo.getInstance().InsertCustomDB(customModels);
                ParamaterModel.CustomSyncTime="";
                //保存同步时间
                SharePreferUtil.SetSyncTimeShare(context);
                //同步参数
                SyncBase.getInstance().SyncPara(mHandler);
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
        }catch (Exception ex) {
            MessageBox.Show(context,ex.getMessage());
            LogUtil.WriteLog(MainActivity.class,TAG_SyncMaterial,result);
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
                ParamaterModel.ParamaterSyncTime="";
                //保存同步时间
                SharePreferUtil.SetSyncTimeShare(context);
                MessageBox.Show(context,getString(R.string.Dia_SyncSuccess));
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
        }catch (Exception ex) {
            MessageBox.Show(context,ex.getMessage());
            LogUtil.WriteLog(MainActivity.class,TAG_SyncMaterial,result);
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
        itemIconList.add(R.drawable.ico);
        itemNamesList.add(getString(R.string.outputscan));
        itemIconList.add(R.drawable.ico);
        itemNamesList.add(getString(R.string.exceptionList));
        itemIconList.add(R.drawable.ico);
        itemNamesList.add("出库查询");
        itemIconList.add(R.drawable.ico);
        itemNamesList.add("批量上报");
        itemIconList.add(R.drawable.ico);
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