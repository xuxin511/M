package com.xx.chinetek.mitsubshi.DN;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xx.chinetek.adapter.DN.DeliveryScanItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.method.Sync.SyncDNDatail;
import com.xx.chinetek.mitsubshi.BarcodeDetail;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNTypeModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.activity_delivery_scan)
public class DeliveryScan extends BaseActivity {

    Context context=DeliveryScan.this;
    @ViewInject(R.id.edt_Barcode)
    EditText edtBarcode;
    @ViewInject(R.id.img_Remark)
    ImageView imgRemark;
    @ViewInject(R.id.txt_ItemNo)
    TextView txtItemNo;
    @ViewInject(R.id.txt_ItemName)
    TextView txtItemName;
    @ViewInject(R.id.txt_ScanQty)
    TextView txtScanQty;
    @ViewInject(R.id.txt_DnNo)
    TextView txtDnNo;
    @ViewInject(R.id.lsv_DeliveryScan)
    ListView lsvDeliveryScan;

    DeliveryScanItemAdapter deliveryScanItemAdapter;
    ArrayList<DNDetailModel> dnDetailModels;
    DNTypeModel dnTypeModel;
    DNModel dnModel;

    @Override
    protected void initViews() {
       super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.deliveryScan),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        txtDnNo.setText(getIntent().getStringExtra("DNNo"));
        dnTypeModel=getIntent().getParcelableExtra("DNType");
        dnModel=getIntent().getParcelableExtra("DNModel");
        GetDeliveryOrderScanList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan_title, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_submit){

        }
        return super.onOptionsItemSelected(item);
    }

    @Event(value = R.id.edt_Barcode, type = View.OnKeyListener.class)
    private boolean edtBarcodeOnkeyUp(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

        }
        return false;
    }

    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemClickListener.class)
    private void lsvDeliveryScanonItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(context,BarcodeDetail.class);
        startActivityLeft(intent);
    }

    void GetDeliveryOrderScanList(){
        dnDetailModels =ImportDnDetail();
        deliveryScanItemAdapter=new DeliveryScanItemAdapter(context, dnDetailModels);
        lsvDeliveryScan.setAdapter(deliveryScanItemAdapter);
    }

    ArrayList<DNDetailModel> ImportDnDetail(){
        ArrayList<DNDetailModel> dnDetailModels=null;
        dnDetailModels= SyncDNDatail.GetDNDetailByDNNo(dnModel.getAGENT_DN_NO());
        for(int i=0;i<10;i++) {
            DNDetailModel dnDetailModels1 = new DNDetailModel();
            dnDetailModels1.setITEM_NO("1234556666"+i);
            dnDetailModels1.setITEM_NAME("物料名称：xxxxxxx");
            dnDetailModels1.setLINE_NO(Float.parseFloat(i+""));
            dnDetailModels1.setDN_QTY(Float.parseFloat(i+"123"));
            dnDetailModels1.setSCAN_QTY(Float.parseFloat(i+"123"));
            dnDetailModels.add(dnDetailModels1);
        }
        return dnDetailModels;
    }
}
