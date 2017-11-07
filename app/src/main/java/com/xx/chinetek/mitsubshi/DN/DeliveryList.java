package com.xx.chinetek.mitsubshi.DN;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
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
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.method.Sync.SyncDN;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNTypeModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncDn;

@ContentView(R.layout.activity_delivery_list)
public class DeliveryList extends BaseActivity {

    Context context = DeliveryList.this;
    @ViewInject(R.id.edt_DeleveryNoFuilter)
    EditText edtDeleveryNoFuilter;
    @ViewInject(R.id.Lsv_DeliveryList)
    ListView LsvDeliveryList;

    DeliveryListItemAdapter deliveryListItemAdapter;
    ArrayList<DNModel> DNModels;
    DNTypeModel dnTypeModel;

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_SyncDn:
                DNModels= SyncDN.AnalysisSyncMAPSDNJson((String) msg.obj);
                BindListView();
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____"+ msg.obj);
                break;
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
        dnTypeModel=getIntent().getParcelableExtra("DNType");
        ImportDelivery();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_title, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Event(value = R.id.edt_DeleveryNoFuilter, type = View.OnKeyListener.class)
    private boolean edtDeleveryNoFuilterOnkeyUp(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

        }
        return false;
    }

    @Event(value = R.id.Lsv_DeliveryList,type = AdapterView.OnItemClickListener.class)
    private void LsvDeliveryListonItemClick(AdapterView<?> parent, View view, int position, long id) {
        DNModel  dnModel=(DNModel)deliveryListItemAdapter.getItem(position);
        Intent intent=new Intent(context,DeliveryScan.class);
        Bundle bundle=new Bundle();
        bundle.putParcelable("DNModel",dnModel);
        bundle.putParcelable("DNType",dnTypeModel);
        intent.putExtras(bundle);
        intent.putExtra("DNNo",dnModel.getAGENT_DN_NO());
        startActivityLeft(intent);
    }

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
        Boolean isSyncCompleted=false;
        switch (dnTypeModel.getDNType()){
            case 0://MAPS
                SyncDN.SyncMAPS(mHandler);
                break;
            case 1://邮件
                isSyncCompleted= SyncDN.SyncMail(dnTypeModel);
                break;
            case 2://FTP
                isSyncCompleted= SyncDN.SyncFtp(dnTypeModel);
                break;
            case 4://USB
                isSyncCompleted= SyncDN.SyncUsb(dnTypeModel);
                break;
        }

        //测试
        for(int i=0;i<10;i++) {
            DNModel DNModel = new DNModel();
            DNModel.setAGENT_DN_NO("ck12345678"+i);
            DNModel.setDN_STATUS(i%2==0?"未下载":"已下载");
            DNModel.setCUSTOM_NAME("收货方xxxxxxx");
            DNModel.setUPDATE_DATE(CommonUtil.dateStrConvertDate("2017-10-30",null));
            DNModel.setDN_SOURCE("MAPS");
            DNModels.add(DNModel);
        }
        BindListView();
    }




}
