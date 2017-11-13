package com.xx.chinetek.mitsubshi.DN;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.xx.chinetek.adapter.DN.DeliveryListItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.LoadingDialog;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.Sync.SyncDN;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DN.DNModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncDn;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncFTP;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncMail;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncUSB;

@ContentView(R.layout.activity_delivery_list)
public class DeliveryList extends BaseActivity {

    Context context = DeliveryList.this;
    @ViewInject(R.id.edt_DeleveryNoFuilter)
    EditText edtDeleveryNoFuilter;
    @ViewInject(R.id.Lsv_DeliveryList)
    ListView LsvDeliveryList;

    DeliveryListItemAdapter deliveryListItemAdapter;
    ArrayList<DNModel> DNModels; //所有未完成出库单

    LoadingDialog dialog;

    @Override
    public void onHandleMessage(Message msg) {
        try {
            switch (msg.what) {
                case RESULT_SyncDn:
                    SyncDN.AnalysisSyncMAPSDNJson((String) msg.obj);
                    break;
                case RESULT_SyncUSB:
                    SyncDN.DNFromFiles();
                    dialog.dismiss();
                    break;
                case RESULT_SyncMail:
                case RESULT_SyncFTP:
                    if ((int) msg.obj > 0) {
                        SyncDN.DNFromFiles();
                    }
                    dialog.dismiss();
                    //导入文件至数据库
                    break;
                case NetworkError.NET_ERROR_CUSTOM:
                    ToastUtil.show("获取请求失败_____" + msg.obj);
                    break;
            }
            DNModels= DbDnInfo.getInstance().GetLoaclDN();
            BindListView();
        } catch (Exception ex) {
            MessageBox.Show(context,ex.getMessage());
            dialog.dismiss();
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

    }

    @Override
    protected void onResume() {
        super.onResume();
        ImportDelivery();
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
        if(item.getItemId()==R.id.action_New){
            Intent intent=new Intent(context,DeliveryScan.class);
            String Dnno="CS1233333";
            intent.putExtra("DNNo",Dnno);
            ParamaterModel.DnTypeModel.setDNType(3);
            Bundle bundle=new Bundle();
            DNModel dnModel=new DNModel();
            dnModel.setAGENT_DN_NO(Dnno);
            dnModel.setDN_QTY(0);
            bundle.putParcelable("DNModel",dnModel);
            intent.putExtras(bundle);
            startActivityLeft(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    @Event(value = R.id.Lsv_DeliveryList,type = AdapterView.OnItemClickListener.class)
    private void LsvDeliveryListonItemClick(AdapterView<?> parent, View view, int position, long id) {
        DNModel  dnModel=(DNModel)deliveryListItemAdapter.getItem(position);
        Intent intent=new Intent(context,DeliveryScan.class);
        Bundle bundle=new Bundle();
        bundle.putParcelable("DNModel",dnModel);
        intent.putExtras(bundle);
        intent.putExtra("DNNo",dnModel.getAGENT_DN_NO());
        startActivityLeft(intent);
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
            if(!edtDeleveryNoFuilter.getText().toString().equals(""))
                deliveryListItemAdapter.getFilter().filter(edtDeleveryNoFuilter.getText().toString());
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
        }

    }




}
