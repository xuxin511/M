package com.xx.chinetek.mitsubshi.Exception;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
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
import com.xx.chinetek.adapter.Exception.ExceptionScanItemAdapter;
import com.xx.chinetek.adapter.Exception.ExceptionScanbarcodeAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.method.AnalyticsBarCode;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.mitsubshi.BarcodeDetail;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.BarCodeModel;
import com.xx.chinetek.model.DBReturnModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;
import com.xx.chinetek.model.DN.DNTypeModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_exception_scanlist)
public class ExceptionBarcodelist extends BaseActivity {

    Context context=ExceptionBarcodelist.this;
    @ViewInject(R.id.edt_Barcode)
    EditText edtBarcode;
    @ViewInject(R.id.txt_KUQty)
    TextView txtKUQty;
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

    ExceptionScanbarcodeAdapter exceptionScanbarcodeAdapter;
    ArrayList<DNScanModel> DNScanModels;
    DNTypeModel dnTypeModel;
    DNModel dnModel;
    DbDnInfo dnInfo;
    DNDetailModel dndetailmodel;

    @Override
    protected void initViews() {
       super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.ExceptionScan),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        dnInfo=DbDnInfo.getInstance();
        dnModel=getIntent().getParcelableExtra("DNModel");
        dndetailmodel=getIntent().getParcelableExtra("DNdetailModel");
        //初始化数据
        txtDnNo.setText(dnModel.getAGENT_DN_NO().toString());
        txtItemName.setText("物料名称："+dndetailmodel.getGOLFA_CODE());
        txtItemNo.setText("物料编码："+dndetailmodel.getGOLFA_CODE());
        txtKUQty.setText("出库数量："+dndetailmodel.getDN_QTY());
        txtScanQty.setText("扫描数量："+dndetailmodel.getSCAN_QTY());

        GetDeliveryOrderScanList();
        if (dnModel.getDETAILS()== null)
            dnModel.setDETAILS(new ArrayList<DNDetailModel>());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan_title, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_close){
            closeActiviry();
        }
        return super.onOptionsItemSelected(item);
    }

    @Event(value = R.id.edt_Barcode, type = View.OnKeyListener.class)
    private boolean edtBarcodeOnkeyUp(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (event.getAction() == KeyEvent.ACTION_UP) {
                String Barcode = edtBarcode.getText().toString();
                if (TextUtils.isEmpty(Barcode)) {
                    MessageBox.Show(context, getString(R.string.Msg_No_Barcode));
                    return true;
                }
                try {
                    ArrayList<BarCodeModel> barCodeModels = Barcode.length() < 400 ?
                            AnalyticsBarCode.AnalyticsSmall(Barcode)
                            : AnalyticsBarCode.AnalyticsLarge(Barcode);
                    if (barCodeModels != null && barCodeModels.size() != 0) {
//                        if (dnTypeModel.getDNType() == 3) { //自建
//                            return CreateNewDN(barCodeModels);
//                        } else {
//                            return ScanBarccode(barCodeModels);
//                        }
                    }
                } catch (Exception ex) {
                    MessageBox.Show(context, ex.getMessage());
                }
                return true;
            }
        }
        return false;
    }

//    /**
//     * 条码扫描（非自建方式）
//     * @param barCodeModels
//     * @return
//     * @throws Exception
//     */
//    private boolean ScanBarccode(ArrayList<BarCodeModel> barCodeModels) throws Exception {
//        dnModel.resetDetailModels();
//        List<DNDetailModel> dnDetailModels = dnModel.getDetailModels();
//        int isErrorStatus=-1;//0:物料已扫描  1：数量已超出 2：物料不存在
//        DNDetailModel dnDetailModel = new DNDetailModel();
//        //判断物料是否存在
//        int index = getIndex(dnDetailModels, barCodeModels.get(0), dnDetailModel);
//        if (index == -1) {
//            isErrorStatus=2;
//        }else {
//            //判断扫描数量是否超过出库数量
//            String condition = dnDetailModel.getGOLFA_CODE() == null ? dnDetailModel.getITEM_NO() : dnDetailModel.getGOLFA_CODE();
//            DBReturnModel dbReturnModel = dnInfo.GetDNQty(dnModel.getAGENT_DN_NO(), condition);
//            if (dbReturnModel.getDNQTY() < dbReturnModel.getSCANQTY() + barCodeModels.size()) {
//                isErrorStatus = 1;
//            }else {
//                for (BarCodeModel barCodeModel : barCodeModels) {
//                    //判断条码是否存在
//                    dnDetailModels.get(index).__setDaoSession(dnInfo.getDaoSession());
//                    List<DNScanModel> dnScanModels = dnDetailModels.get(index).getDnScanModels();
//                    DNScanModel dnScanModel = new DNScanModel();
//                    dnScanModel.setITEM_SERIAL_NO(barCodeModel.getSerial_Number());
//                    int barcodeIndex = dnScanModels.indexOf(dnScanModel);
//                    if (barcodeIndex != -1) {
//                        isErrorStatus = 0;
//                        break;
//                    }
//
//                    index=findIndexByGolfaCode(dnDetailModels,barCodeModel.getGolfa_Code());
//                    if(index==-1){
//                        isErrorStatus = 1;
//                        break;
//                    }
//                    //更新物料扫描数量
//                    dnDetailModels.get(index).setSCAN_QTY(dnDetailModels.get(index).getSCAN_QTY() + 1);
//                    //保存序列号
//                    dnScanModel.setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
//                    dnScanModel.setLINE_NO(dnScanModels.size() + 1);
//                    dnScanModel.setPACKING_DATE(barCodeModel.getPacking_Date());
//                    dnScanModel.setREGION(barCodeModel.getPlace_Code());
//                    dnScanModel.setCOUNTRY(barCodeModel.getCountry_Code());
//                    dnScanModel.setGOLFA_CODE(barCodeModel.getGolfa_Code());
//                    dnScanModel.setITEM_STATUS("AC");
//
//                    dnDetailModels.get(index).getDnScanModels().add(dnScanModel);
//                }
//            }
//        }
//        if (ShowErrMag(isErrorStatus)) return true;
//        return SaveScanInfo(isErrorStatus);
//    }


    private int clickposition=-1;
    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemClickListener.class)
    private void lsvDeliveryScanonItemClick(AdapterView<?> parent, View view, int position, long id) {
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
                        DNScanModel Model= (DNScanModel)exceptionScanbarcodeAdapter.getItem(clickposition);
                        if(DbDnInfo.getInstance().DELscanbyserial(Model.getSERIAL_NO(),"")){
                            //判断剩余的扫描数量
                            Integer lastNum=DbDnInfo.getInstance().GetLoaclDNScanModelDNNum(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE());
                            DbDnInfo.getInstance().UpdateDetailNum(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),lastNum);
                            if(lastNum==0){
                                //需要改变主表状态
                                DbDnInfo.getInstance().UpdateDNmodelState(dndetailmodel.getAGENT_DN_NO(),"3","");
                            }
                            MessageBox.Show(context,"删除成功！");
                            txtScanQty.setText("扫描数量："+(dndetailmodel.getSCAN_QTY()-1));
                            GetDeliveryOrderScanList();
                        }else{
                            MessageBox.Show(context,"删除失败！");
                        }
                    }
                }).setNegativeButton("取消", null).show();
    }

    void GetDeliveryOrderScanList(){
        DNScanModels= DbDnInfo.getInstance().GetLoaclDNScanModelDN(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE());
        exceptionScanbarcodeAdapter=new ExceptionScanbarcodeAdapter(context, DNScanModels);
        lsvDeliveryScan.setAdapter(exceptionScanbarcodeAdapter);
        edtBarcode.setText("");
        CommonUtil.setEditFocus(edtBarcode);
    }




}
