package com.xx.chinetek.mitsubshi.DN;

import android.content.Context;
import android.widget.ListView;
import android.widget.TextView;

import com.xx.chinetek.adapter.BarcodeItemAdapter;
import com.xx.chinetek.adapter.Exception.BarcodeexceptionAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.BarCodeModel;
import com.xx.chinetek.model.DN.DNScanModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.activity_barcode_detail)
public class DeliveryExceptionbarcode extends BaseActivity {

    Context context=DeliveryExceptionbarcode.this;
    @ViewInject(R.id.txt_ItemNo)
    TextView txtItemNo;
    @ViewInject(R.id.txt_ItemName)
    TextView txtItemName;
    @ViewInject(R.id.txt_ScanQty)
    TextView txtScanQty;
    @ViewInject(R.id.lsv_barcodeDetail)
    ListView lsvBarcodeDetail;

    BarcodeexceptionAdapter barcodeItemAdapter;
    ArrayList<BarCodeModel> barcodemodels;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.barcodeException),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        barcodemodels=getIntent().getParcelableArrayListExtra("barcodemodels");
        GetBarcodeDetailList();
    }


    void GetBarcodeDetailList(){
        barcodeItemAdapter=new BarcodeexceptionAdapter(context, barcodemodels);
        lsvBarcodeDetail.setAdapter(barcodeItemAdapter);
    }


}
