package com.xx.chinetek.mitsubshi;

import android.content.Context;
import android.widget.ListView;
import android.widget.TextView;

import com.xx.chinetek.adapter.BarcodeItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.model.DN.DNScanModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.activity_barcode_detail)
public class BarcodeDetail extends BaseActivity {

    Context context=BarcodeDetail.this;
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

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.barcodeDetail),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        GetBarcodeDetailList();
    }


    void GetBarcodeDetailList(){
        dnScanModels =ImportDetail();
        barcodeItemAdapter=new BarcodeItemAdapter(context, dnScanModels);
        lsvBarcodeDetail.setAdapter(barcodeItemAdapter);
    }

    ArrayList<DNScanModel> ImportDetail(){
        ArrayList<DNScanModel> dnScanModels =new ArrayList<>();
        for(int i=0;i<10;i++) {
            DNScanModel DNModel = new DNScanModel();
            DNModel.setSERIAL_NO("123333333"+i);
            dnScanModels.add(DNModel);
        }
        return dnScanModels;
    }
}
