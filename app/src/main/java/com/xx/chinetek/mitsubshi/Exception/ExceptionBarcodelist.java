package com.xx.chinetek.mitsubshi.Exception;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
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
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.mitsubshi.BarcodeDetail;
import com.xx.chinetek.mitsubshi.BaseIntentActivity;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.BarCodeModel;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.Base.ParamaterModel;
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
import java.util.Date;
import java.util.List;

@ContentView(R.layout.activity_exception_scanlist)
public class ExceptionBarcodelist extends BaseIntentActivity {

    Context context=ExceptionBarcodelist.this;
    @ViewInject(R.id.img_Remark)
    ImageView img_Remark;
    @ViewInject(R.id.textView5)
    TextView textView5;
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
        edtBarcode.setVisibility(View.GONE);
        textView5.setVisibility(View.GONE);
        img_Remark.setVisibility(View.GONE);
    }

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case TAG_SCAN:
                CheckScanBarcode((String) msg.obj);
                break;
        }
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

    /**
     * 扫描条码
     * @param barcode
     * @return
     */
    private Boolean CheckScanBarcode(String barcode) {
        if (TextUtils.isEmpty(barcode)) {
            MessageBox.Show(context, getString(R.string.Msg_No_Barcode));
            return true;
        }
        try {
            ArrayList<BarCodeModel> barCodeModels = barcode.length() < 400 ?
                    AnalyticsBarCode.AnalyticsSmall(barcode)
                    : AnalyticsBarCode.AnalyticsLarge(barcode);
            if (barCodeModels != null && barCodeModels.size() != 0) {
                MaterialModel materialModel = DbBaseInfo.getInstance().GetItemName(barCodeModels.get(0).getGolfa_Code());
                txtItemNo.setText(materialModel.getMATNR());
                txtItemName.setText(materialModel.getMAKTX());
                txtScanQty.setText(getString(R.string.scanQty)+barCodeModels.size());
                return ScanBarccode(barCodeModels);

            }
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
        }
        return true;
    }

//    @Event(value = R.id.edt_Barcode, type = View.OnKeyListener.class)
//    private boolean edtBarcodeOnkeyUp(View v, int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_ENTER) {
//            if (event.getAction() == KeyEvent.ACTION_UP) {
//                String Barcode = edtBarcode.getText().toString();
//                if (TextUtils.isEmpty(Barcode)) {
//                    MessageBox.Show(context, getString(R.string.Msg_No_Barcode));
//                    return true;
//                }
//                try {
//                    ArrayList<BarCodeModel> barCodeModels = Barcode.length() < 400 ?
//                            AnalyticsBarCode.AnalyticsSmall(Barcode)
//                            : AnalyticsBarCode.AnalyticsLarge(Barcode);
//                    if (barCodeModels != null && barCodeModels.size() != 0) {
//                        return ScanBarccode(barCodeModels);
//                    }
//                } catch (Exception ex) {
//                    MessageBox.Show(context, ex.getMessage());
//                }
//                return true;
//            }
//        }
//        return false;
//    }


    /**
     * 条码扫描（非自建方式）
     * @param barCodeModels
     * @return
     * @throws Exception
     */
    private boolean ScanBarccode(ArrayList<BarCodeModel> barCodeModels) throws Exception {
        dnModel.resetDETAILS();
        List<DNDetailModel> dnDetailModels = dnModel.getDETAILS();
        int isErrorStatus=-1;//0:物料已扫描  1：数量已超出 2：物料不存在
        DNDetailModel dnDetailModel = new DNDetailModel();
        //判断物料是否存在
        int index = getIndex(dnDetailModels, barCodeModels.get(0), dnDetailModel);
        if (index == -1) {
            isErrorStatus=2;
        }else {
            //判断扫描数量是否超过出库数量
            String condition = dnDetailModel.getGOLFA_CODE() == null ? dnDetailModel.getITEM_NO() : dnDetailModel.getGOLFA_CODE();
            DBReturnModel dbReturnModel = dnInfo.GetDNQty(dnModel.getAGENT_DN_NO(), condition);
            if (dbReturnModel.getDNQTY() < dbReturnModel.getSCANQTY() + barCodeModels.size()) {
                isErrorStatus = 1;
            }else {
                isErrorStatus = Checkbarcode(barCodeModels, dnDetailModels, index);
            }
        }
        if (ShowErrMag(isErrorStatus)) return true;
        return SaveScanInfo(isErrorStatus);
    }

    /**
     * 保存数据库
     * @param isErrorStatus
     * @return
     * @throws Exception
     */
    private boolean SaveScanInfo(int isErrorStatus) throws Exception {

        //保存至数据库
        ArrayList<DNModel> dnModels = new ArrayList<DNModel>();
        dnModels.add(dnModel);
        dnInfo.InsertDNDB(dnModels);
        //刷新listview
        GetDeliveryOrderScanList();
        return true;
    }

    /**
     * 显示错误信息
     * @param isErrorStatus
     * @return
     */
    private boolean ShowErrMag(int isErrorStatus) {
//        edtBarcode.setText("");
        if(isErrorStatus==0) {
            MessageBox.Show(context, getString(R.string.Msg_Serial_Scaned));
            return true;
        }
        if(isErrorStatus==1) {
            MessageBox.Show(context, getString(R.string.Msg_SerialNum_Finished));
            return true;
        }
        if(isErrorStatus==2) {
            MessageBox.Show(context, getString(R.string.Msg_Material_NotMatch));
            return true;
        }
        return false;
    }

    /**
     * 判断物料是否存在出库单中
     * @param dnDetailModels
     * @param barCodeModel
     * @param dnDetailModel
     * @return
     */
    private int getIndex(List<DNDetailModel> dnDetailModels, BarCodeModel barCodeModel, DNDetailModel dnDetailModel) {
        dnDetailModel.setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
        dnDetailModel.setGOLFA_CODE(barCodeModel.getGolfa_Code());
        //判断是否存在物料
        return dnDetailModels.indexOf(dnDetailModel);
    }


    /**
     * 非自建单据扫描数量和重复检查
     * @param barCodeModels
     * @param dnDetailModels
     * @param index
     * @return
     */
    private int Checkbarcode(ArrayList<BarCodeModel> barCodeModels, List<DNDetailModel> dnDetailModels, int index) {
        int isErrorStatus=-1;
        for (BarCodeModel barCodeModel : barCodeModels) {
            //判断条码是否存在
            dnDetailModels.get(index).__setDaoSession(dnInfo.getDaoSession());
            List<DNScanModel> dnScanModels = dnDetailModels.get(index).getSERIALS();
            DNScanModel dnScanModel = new DNScanModel();
            dnScanModel.setSERIAL_NO(barCodeModel.getSerial_Number());
            int barcodeIndex = dnScanModels.indexOf(dnScanModel);
            if (barcodeIndex != -1) {
                isErrorStatus = 0;
                break;
            }

            index=findIndexByGolfaCode(dnDetailModels,barCodeModel.getGolfa_Code());
            if(index==-1){
                isErrorStatus = 1;
                break;
            }
            //更新物料扫描数量
            dnDetailModels.get(index).setSCAN_QTY(dnDetailModels.get(index).getSCAN_QTY() + 1);
            //保存序列号
            dnScanModel.setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
            dnScanModel.setLINE_NO(dnDetailModels.get(index).getLINE_NO());
            dnScanModel.setPACKING_DATE(barCodeModel.getPacking_Date());
            dnScanModel.setREGION(barCodeModel.getPlace_Code());
            dnScanModel.setCOUNTRY(barCodeModel.getCountry_Code());
            dnScanModel.setGOLFA_CODE(barCodeModel.getGolfa_Code());
            dnScanModel.setITEM_STATUS("AC");
            dnScanModel.setITEM_NO(dnDetailModels.get(index).getITEM_NO());
            dnScanModel.setITEM_NAME(dnDetailModels.get(index).getITEM_NAME());
            dnScanModel.setDEAL_SALE_DATE(new Date());
            dnScanModel.setMAT_TYPE(0);
            dnDetailModels.get(index).setOPER_DATE(new Date());
            dnDetailModels.get(index).getSERIALS().add(dnScanModel);
            dnDetailModels.get(index).setDETAIL_STATUS("AC");
            dnDetailModels.get(index).setSTATUS(0);
        }
        return isErrorStatus;
    }


    /**
     * 查找可以出库行（GolfaCode相同，出库数量未完成）
     * @param GolfaCode
     * @return
     */
    private int findIndexByGolfaCode(List<DNDetailModel> dnDetailModels,String GolfaCode){
        int index=-1;
        int size=dnDetailModels.size();
        for(int i=0;i<size;i++){
            DNDetailModel dnDetailModel=dnDetailModels.get(i);
            if(dnDetailModel.getGOLFA_CODE().equals(GolfaCode)){
                if(dnDetailModel.getDN_QTY()>dnDetailModel.getSCAN_QTY()) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }



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
                                        MessageBox.Show(context,"更新表头状态失败！");
                                        return;
                                    }
                                }
                                MessageBox.Show(context,"删除成功！");
                                txtScanQty.setText("扫描数量："+(dndetailmodel.getSCAN_QTY()-1));
                                GetDeliveryOrderScanList();

                            }else{
                                MessageBox.Show(context,"更新表体扫描数量失败！");
                                return;
                            }
                        }else{
                            MessageBox.Show(context,"删除扫描明细失败！");
                        }
                    }
                }).setNegativeButton("取消", null).show();
    }

    void GetDeliveryOrderScanList(){
        DNScanModels= DbDnInfo.getInstance().GetLoaclDNScanModelDN(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO());
        exceptionScanbarcodeAdapter=new ExceptionScanbarcodeAdapter(context, DNScanModels);
        lsvDeliveryScan.setAdapter(exceptionScanbarcodeAdapter);
//        edtBarcode.setText("");
//        CommonUtil.setEditFocus(edtBarcode);
    }




}
