package com.xx.chinetek.mitsubshi.DN;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.adapter.DN.DeliveryListItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.model.ReturnMsgModelList;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.LoadingDialog;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.Sync.SyncDN;
import com.xx.chinetek.mitsubshi.BaseIntentActivity;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import static com.xx.chinetek.method.Delscan.Delscan.DelDNmodel;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncDn;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncDnDetail;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncFTP;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncMail;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncUSB;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncDn;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncDnDetail;

@ContentView(R.layout.activity_delivery_list)
public class DeliveryList extends BaseIntentActivity implements SwipeRefreshLayout.OnRefreshListener {

    Context context = DeliveryList.this;
    @ViewInject(R.id.edt_DeleveryNoFuilter)
    EditText edtDeleveryNoFuilter;
    @ViewInject(R.id.Lsv_DeliveryList)
    ListView LsvDeliveryList;
    @ViewInject(R.id.mSwipeLayout)
    SwipeRefreshLayout mSwipeLayout;

    DeliveryListItemAdapter deliveryListItemAdapter;
    ArrayList<DNModel> DNModels; //所有未完成出库单
    LoadingDialog dialog;

    @Override
    public void onHandleMessage(Message msg) {
        try {
            switch (msg.what) {
                case RESULT_SyncDn:
                    AnalysisSyncMAPSDNJson((String) msg.obj);
                    break;
                case RESULT_SyncDnDetail:
                    AnalysisSyncMAPSDNDetailJson((String) msg.obj);
                    break;
                case RESULT_SyncUSB:
                    ArrayList<DNModel>  dnModels= SyncDN.DNFromFiles();
                    DbDnInfo.getInstance().InsertDNDB(dnModels);
                    break;
                case RESULT_SyncMail:
                case RESULT_SyncFTP:
                    if ((int) msg.obj > 0) {
                        Intent intent = new Intent(context, FTPsync.class);
                        startActivityLeft(intent);

                    }
                    break;
                case TAG_SCAN:
                    CheckDNByDnNo((String) msg.obj);
                    break;
                case NetworkError.NET_ERROR_CUSTOM:
                    ToastUtil.show("获取请求失败_____" + msg.obj);
                    break;
            }
            if(dialog!=null)
                dialog.dismiss();
            DNModels= DbDnInfo.getInstance().GetLoaclDN();
            BindListView();
        } catch (Exception ex) {
            MessageBox.Show(context,ex.getMessage());
            if(dialog!=null)
                dialog.dismiss();
        }
    }

    /**
     * MAPS同步出库单
     * @param result
     */
    public void AnalysisSyncMAPSDNJson(String result) throws Exception {
        LogUtil.WriteLog(DeliveryList.class, TAG_SyncDn, result);
        ReturnMsgModelList<DNModel> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<DNModel>>() {
        }.getType());
        if (returnMsgModel.getHeaderStatus().equals("S")) {
            ArrayList<DNModel> dnModels = returnMsgModel.getModelJson();
            ArrayList<DNModel> SelectdnModels=new ArrayList<>();
            int size=dnModels.size();
            for(int i=0;i<size;i++){
                if(DbDnInfo.getInstance().CheckDNInDB(dnModels.get(i).getAGENT_DN_NO()))
                    SelectdnModels.add(dnModels.get(i));
            }
            if(SelectdnModels.size()!=0) {
                Intent intent = new Intent(context, DNsync.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("DNModels", SelectdnModels);
                intent.putExtras(bundle);
                startActivityLeft(intent);
            }
        } else {
            MessageBox.Show(context, returnMsgModel.getMessage());
        }
    }


    private void AnalysisSyncMAPSDNDetailJson(String result) throws Exception {
        LogUtil.WriteLog(DeliveryList.class, TAG_SyncDnDetail, result);
        ReturnMsgModelList<DNDetailModel> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<DNDetailModel>>() {
        }.getType());
        if (returnMsgModel.getHeaderStatus().equals("S")) {
            ArrayList<DNDetailModel> dnDetailModels = returnMsgModel.getModelJson();
            int size=dnDetailModels.size();
            for(int i=0;i<size;i++){
                int scanQTY=DbDnInfo.getInstance().GetScanQtyInDNScanModel(dnDetailModels.get(i).getAGENT_DN_NO()
                        ,dnDetailModels.get(i).getGOLFA_CODE(),dnDetailModels.get(i).getLINE_NO());
                dnDetailModels.get(i).setSCAN_QTY(scanQTY);
            }
            //插入数据
            DbDnInfo.getInstance().InsertDNDetailDB(dnDetailModels);
            DbDnInfo.getInstance().ChangeDNStatusByDnNo(dnDetailModels.get(0).getAGENT_DN_NO(),1);
            StartScan(dnModel);
        } else {
            MessageBox.Show(context, returnMsgModel.getMessage());
        }
    }


    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle = new ToolBarTitle(getResources().getString(R.string.outputlist), true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        edtDeleveryNoFuilter.addTextChangedListener(DeleveryNoTextWatcher);
        mSwipeLayout.setOnRefreshListener(this); //下拉刷新
    }

    @Override
    public void onRefresh() {
        ImportDelivery();
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DNModels= DbDnInfo.getInstance().GetLoaclDN();
        BindListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_title, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_QR){
            Intent intent=new Intent(context,QRScan.class);
            ParamaterModel.DnTypeModel.setDNType(5);
            startActivityLeft(intent);
        }
//        if(item.getItemId()==R.id.action_New){
//            Intent intent=new Intent(context,DeliveryScan.class);
//            ParamaterModel.DnTypeModel.setDNType(3);
//            Bundle bundle=new Bundle();
//            DNModel dnModel=new DNModel();
//            CreateDnNo.GetDnNo(context,dnModel);
//            dnModel.setDN_QTY(0);
//            bundle.putParcelable("DNModel",dnModel);
//            intent.putExtras(bundle);
//            startActivityLeft(intent);
//        }
        if(item.getItemId()==R.id.action_sync){
            ImportDelivery();
        }
        return super.onOptionsItemSelected(item);
    }

    DNModel  dnModel;
    @Event(value = R.id.Lsv_DeliveryList,type = AdapterView.OnItemClickListener.class)
    private void LsvDeliveryListonItemClick(AdapterView<?> parent, View view, int position, long id) {
         dnModel=(DNModel)deliveryListItemAdapter.getItem(position);
        ParamaterModel.DnTypeModel.setDNType(dnModel.getDN_SOURCE());
        if(dnModel.getDN_SOURCE()==0){
            SyncDN.SyncMAPSDetail(dnModel.getAGENT_DN_NO(),mHandler);
        }else {
            StartScan(dnModel);
        }
    }


    @Event(value = R.id.Lsv_DeliveryList,type = AdapterView.OnItemLongClickListener.class)
    private boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i < 0) {
            MessageBox.Show(context, "请先选择操作的行！");
            return false;
        }
        final DNModel Model = (DNModel) deliveryListItemAdapter.getItem(i);
        new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("确认删除扫描记录？\n")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 自动生成的方法
                        DelDNmodel(Model);
                        BindListView();

                    }
                }).setNegativeButton("取消", null).show();
        return true;
    }


    private void StartScan(DNModel dnModel) {
        Intent intent=new Intent(context,DeliveryScan.class);
        Bundle bundle=new Bundle();
        bundle.putParcelable("DNModel",dnModel);
        intent.putExtras(bundle);
        startActivityLeft(intent);
    }

    void CheckDNByDnNo(String DNNo){
        if(DNModels!=null) {
            DNModel dnModel=new DNModel();
            dnModel.setAGENT_DN_NO(DNNo);
            int index=DNModels.indexOf(dnModel);
            if(index!=-1) {
                dnModel=DNModels.get(index);
                StartScan(dnModel);
            }else{
                MessageBox.Show(context,getString(R.string.Msg_No_DNno));
                edtDeleveryNoFuilter.setText(DNNo);
                CommonUtil.setEditFocus(edtDeleveryNoFuilter);
            }
        }
    }


    /**
     * 文本变化事件
     */
    TextWatcher DeleveryNoTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String filterContent=edtDeleveryNoFuilter.getText().toString();
            if(!filterContent.equals(""))
                deliveryListItemAdapter.getFilter().filter(filterContent);
            else{
                BindListView();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    void BindListView(){
        if(DNModels!=null) {
            deliveryListItemAdapter = new DeliveryListItemAdapter(context, DNModels);
            LsvDeliveryList.setAdapter(deliveryListItemAdapter);
        }
    }

    /**
     * 同步并导入DN单据
     * @return
     */
   private void ImportDelivery(){

        switch (ParamaterModel.DnTypeModel.getDNType()){
            case 0://MAPS
                BaseApplication.DialogShowText = getString(R.string.Dia_SyncDn);
                dialog =new LoadingDialog(context);
                dialog.show();
                SyncDN.SyncMAPS(mHandler);
                break;
            case 1://邮件
                BaseApplication.DialogShowText = getString(R.string.Dia_SyncMail);
                dialog =new LoadingDialog(context);
                dialog.show();
                SyncDN.SyncMail(mHandler);
                break;
            case 2://FTP
                BaseApplication.DialogShowText = getString(R.string.Dia_SyncFTP);
                dialog =new LoadingDialog(context);
                dialog.show();
                SyncDN.SyncFtp(mHandler);
                break;
            case 4://USB
                BaseApplication.DialogShowText = getString(R.string.Dia_SyncUSB);
                dialog =new LoadingDialog(context);
                dialog.show();
                android.os.Message msg = mHandler.obtainMessage(RESULT_SyncUSB,null);
                mHandler.sendMessage(msg);
                break;
            case 3:
            case 5:
                DNModels= DbDnInfo.getInstance().GetLoaclDN();
                BindListView();
                break;
        }

    }




}
