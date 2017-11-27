package com.xx.chinetek.mitsubshi.Bulkupload;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.TextView;

import com.xx.chinetek.adapter.bulkupload.BulkuploadScanItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.mitsubshi.BaseIntentActivity;
import com.xx.chinetek.mitsubshi.Exception.ExceptionBarcodelist;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.activity_bulkupload_scan)
public class BulkuploadScan extends BaseIntentActivity {

    Context context=BulkuploadScan.this;
    @ViewInject(R.id.txt_DnNo)
    TextView txtDnNo;
    @ViewInject(R.id.lsv_DeliveryScan)
    ListView lsvDeliveryScan;

    BulkuploadScanItemAdapter bulkuploadScanItemAdapter;
    ArrayList<DNDetailModel> dnDetailModels;
    DNModel dnModel;
    DbDnInfo dnInfo;




    @Override
    protected void initViews() {
       super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.Bulkupload),true);
        x.view().inject(this);

    }

    @Override
    protected void initData() {
        super.initData();
        dnInfo=DbDnInfo.getInstance();
        dnModel=getIntent().getParcelableExtra("DNModel");
        txtDnNo.setText(getIntent().getStringExtra("DNNo"));
        dnModel.__setDaoSession(dnInfo.getDaoSession());
        GetDeliveryOrderScanList();
        if (dnModel.getDETAILS() == null)
            dnModel.setDETAILS(new ArrayList<DNDetailModel>());
        dnModel.getDETAILS().addAll(dnDetailModels);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetDeliveryOrderScanList();
    }

    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemClickListener.class)
    private void lsvDeliveryScanonItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Intent intent=new Intent(context,bulkuploadBarcodeDetail.class);
//        Bundle bundle=new Bundle();
//        DNDetailModel DNdetailModel= (DNDetailModel)bulkuploadScanItemAdapter.getItem(position);
//        bundle.putParcelable("DNdetailModel",DNdetailModel);
//        bundle.putParcelable("DNModel",dnModel);
//        intent.putExtras(bundle);
//        startActivityLeft(intent);

        Intent intent=new Intent(context,ExceptionBarcodelist.class);
        Bundle bundle=new Bundle();
        DNDetailModel DNdetailModel= (DNDetailModel)bulkuploadScanItemAdapter.getItem(position);
        bundle.putParcelable("DNdetailModel",DNdetailModel);
        bundle.putParcelable("DNModel",dnModel);
        intent.putExtras(bundle);
        startActivityLeft(intent);



    }

    /**
     * 绑定列表
     */
    void GetDeliveryOrderScanList(){
        dnDetailModels= DbDnInfo.getInstance().GetDNDetailByDNNo(txtDnNo.getText().toString());
        bulkuploadScanItemAdapter=new BulkuploadScanItemAdapter(context, dnDetailModels,dnModel.getDN_SOURCE());
        lsvDeliveryScan.setAdapter(bulkuploadScanItemAdapter);
    }

}
