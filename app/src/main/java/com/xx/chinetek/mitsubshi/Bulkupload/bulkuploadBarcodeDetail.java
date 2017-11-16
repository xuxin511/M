package com.xx.chinetek.mitsubshi.Bulkupload;

import android.content.Context;
import android.widget.ListView;
import android.widget.TextView;

import com.xx.chinetek.adapter.BarcodeItemAdapter;
import com.xx.chinetek.adapter.Exception.ExceptionScanbarcodeAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNScanModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.activity_barcode_detail)
public class bulkuploadBarcodeDetail extends BaseActivity {

    Context context=bulkuploadBarcodeDetail.this;
    @ViewInject(R.id.txt_ItemNo)
    TextView txtItemNo;
    @ViewInject(R.id.txt_ItemName)
    TextView txtItemName;
    @ViewInject(R.id.txt_ScanQty)
    TextView txtScanQty;
    @ViewInject(R.id.lsv_barcodeDetail)
    ListView lsvBarcodeDetail;

    BarcodeItemAdapter barcodeItemAdapter;
    ArrayList<DNScanModel> dnScanModels;
    DNDetailModel dndetailmodel;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.barcodeDetail),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        dndetailmodel=getIntent().getParcelableExtra("DNdetailModel");
        GetBarcodeDetailList();
    }


    void GetBarcodeDetailList(){
        dnScanModels =ImportDetail();
        barcodeItemAdapter=new BarcodeItemAdapter(context, dnScanModels);
        lsvBarcodeDetail.setAdapter(barcodeItemAdapter);
    }

    ArrayList<DNScanModel> ImportDetail(){
        ArrayList<DNScanModel> dnScanModels =new ArrayList<>();
        dnScanModels = DbDnInfo.getInstance().GetLoaclDNScanModelDN(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO());
        return dnScanModels;
    }
}
