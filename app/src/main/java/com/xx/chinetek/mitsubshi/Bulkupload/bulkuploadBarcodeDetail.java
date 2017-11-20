package com.xx.chinetek.mitsubshi.Bulkupload;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.xx.chinetek.adapter.BarcodeItemAdapter;
import com.xx.chinetek.adapter.Exception.ExceptionScanbarcodeAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
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
    DNModel dnModel;

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
        dnModel=getIntent().getParcelableExtra("DNModel");
        GetBarcodeDetailList();
    }


    void GetBarcodeDetailList(){
        dnScanModels =ImportDetail();
        DNScanModel model= new DNScanModel();
        model.setSERIAL_NO("数量："+dnScanModels.size());
        dnScanModels.add(model);
        barcodeItemAdapter=new BarcodeItemAdapter(context, dnScanModels);
        lsvBarcodeDetail.setAdapter(barcodeItemAdapter);
    }

    ArrayList<DNScanModel> ImportDetail(){
        ArrayList<DNScanModel> dnScanModels =new ArrayList<>();
        dnScanModels = DbDnInfo.getInstance().GetLoaclDNScanModelDN(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO());
        return dnScanModels;
    }


    private int clickposition=-1;
    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemClickListener.class)
    private void lsvDeliveryScanonItemClick(AdapterView<?> parent, View view, int position, long id) {
        DNScanModel Modelcheck= (DNScanModel)barcodeItemAdapter.getItem(position);
//        indexOf("数量")!=-1
        if(Modelcheck.getSERIAL_NO().contains("数量")){
            return;
        }
        clickposition=position;
        new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("是否删除扫描记录？\n")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 自动生成的方法
                        //删除扫描记录，改变明细数量
                        if(clickposition==-1){
                            MessageBox.Show(context,"请先选择操作的行！");
                            return;
                        }
                        DNScanModel Model= (DNScanModel)barcodeItemAdapter.getItem(clickposition);
                        if(DbDnInfo.getInstance().DELscanbyserial(Model.getAGENT_DN_NO(),Model.getGOLFA_CODE(),Model.getLINE_NO(),Model.getSERIAL_NO(),"")){
                            //判断剩余的扫描数量
                            Integer lastNum=DbDnInfo.getInstance().GetLoaclDNScanModelDNNum(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO());
                            if(DbDnInfo.getInstance().UpdateDetailNum(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO(),lastNum,dnModel.getDN_SOURCE())){
                                if(DbDnInfo.getInstance().GetLoaclDNScanModelDNNumbyDNNO(dndetailmodel.getAGENT_DN_NO())==0){
                                    //需要改变主表状态
//                                    DNModel modeldn=dnModel;
//                                    modeldn.setSTATUS(1);
                                    if(DbDnInfo.getInstance().UpdateDNmodelState(dndetailmodel.getAGENT_DN_NO(),"1","",dnModel.getDN_SOURCE())){

                                    }else{
                                        MessageBox.Show(context,getString(R.string.Error_del_dnmodel));
                                        return;
                                    }
                                }
                                MessageBox.Show(context,getString(R.string.Msg_del_success));
                                txtScanQty.setText("扫描数量："+(dndetailmodel.getSCAN_QTY()-1));
                                GetBarcodeDetailList();

                            }else{
                                MessageBox.Show(context,getString(R.string.Error_del_dnmodeldetail));
                                return;
                            }
                        }else{
                            MessageBox.Show(context,getString(R.string.Error_del_dnmodelbarcode));
                        }
                    }
                }).setNegativeButton("取消", null).show();
    }
}
