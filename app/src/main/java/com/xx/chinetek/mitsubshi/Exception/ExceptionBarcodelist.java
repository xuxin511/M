package com.xx.chinetek.mitsubshi.Exception;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xx.chinetek.adapter.Exception.ExceptionScanbarcodeAdapter;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.Scan;
import com.xx.chinetek.mitsubshi.BaseIntentActivity;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.BarCodeModel;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;
import com.xx.chinetek.model.DN.DNTypeModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import static com.xx.chinetek.method.Delscan.Delscan.DelScanmodel;

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
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.ScanDetails),true);
        x.view().inject(this);
    }

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case TAG_SCAN:
                try {
                    chaeckBarcode((String) msg.obj);
                }catch (Exception ex){
                    MessageBox.Show(context,ex.getMessage());
                }
                break;
        }
    }

//    private String Flag="";
    @Override
    protected void initData() {
        super.initData();
        img_Remark.setVisibility(View.GONE);
        dnInfo=DbDnInfo.getInstance();
        dnModel=getIntent().getParcelableExtra("DNModel");
        dnModel.__setDaoSession(dnInfo.getDaoSession());
        dndetailmodel=getIntent().getParcelableExtra("DNdetailModel");
//        Flag=getIntent().getStringExtra("Flag");
        //初始化数据
        txtDnNo.setText(dnModel.getDN_SOURCE()==3?dnModel.getCUS_DN_NO().toString():dnModel.getAGENT_DN_NO().toString());
        txtItemName.setText("物料名称："+dndetailmodel.getITEM_NAME());
        txtItemNo.setText("物料编码："+dndetailmodel.getGOLFA_CODE());
        txtKUQty.setText("出库数量："+dndetailmodel.getDN_QTY());
        GetDeliveryOrderScanList();

    }

    @Event(value = R.id.edt_Barcode, type = View.OnKeyListener.class)
    private boolean edtfilterOnKey(View v, int keyCode, KeyEvent event) {
        try{
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
            {
                final String code=edtBarcode.getText().toString().trim();
                if(TextUtils.isEmpty(code)){
                    MessageBox.Show(context,getString(R.string.Msg_No_Barcode));
                    CommonUtil.setEditFocus(edtBarcode);
                    return true;
                }
                if(code.length()>50){
                    MessageBox.Show(context,getString(R.string.Msg_out_Barcode));
                    CommonUtil.setEditFocus(edtBarcode);
                    return true;
                }
                chaeckBarcode(code);
                return true;
            }
        }catch(Exception ex){
            MessageBox.Show(context, ex.getMessage());
            CommonUtil.setEditFocus(edtBarcode);
            return true;
        }
        return false;
    }

    private void chaeckBarcode(String code) throws Exception {
        BarCodeModel model = new BarCodeModel();
        model.setSerial_Number(code);
        model.setGolfa_Code(dndetailmodel.getGOLFA_CODE());
        model.setMAT_TYPE(1); //默认三菱条码
        if(ParamaterModel.baseparaModel.getCusBarcodeRule()!=null && ParamaterModel.baseparaModel.getCusBarcodeRule().getUsed()) {
            MaterialModel materialModel = DbBaseInfo.getInstance().GetItemName(dndetailmodel.getGOLFA_CODE());
            model.setMAT_TYPE(materialModel == null?0:1);//物料没有记录，非三菱条码
        }
        ArrayList<BarCodeModel> barCodeModels = new ArrayList<>();
        barCodeModels.add(model);
        if (barCodeModels != null && barCodeModels.size() != 0) {
            int isErrorStatus=Scan.ScanBarccode(dnInfo,dnModel,barCodeModels);
            txtScanQty.setText(getString(R.string.scanQty)+(DNScanModels.size()));
            if (ShowErrMag(isErrorStatus)) return;
            if(SaveScanInfo(isErrorStatus))
                edtBarcode.setText("");
            CommonUtil.setEditFocus(edtBarcode);
        }
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





    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemClickListener.class)
    private void lsvDeliveryScanonItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(position<0){
            MessageBox.Show(context,"请先选择操作的行！");
            return;
        }
        final DNScanModel Model= (DNScanModel)exceptionScanbarcodeAdapter.getItem(position);
        new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage(Model.getSTATUS().equals("0")?"扫描记录正常，是否删除扫描记录？\n":"是否删除扫描记录？\n")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 自动生成的方法
                        DelScanmodel(Model,dndetailmodel,dnModel);

                        ArrayList<DNDetailModel> dnDetailModels= DbDnInfo.getInstance().GetLoaclExceptionDetailsDN(dnModel.getAGENT_DN_NO().toString());
                        int index = dnDetailModels.indexOf(dndetailmodel);
                        if(index>0){
                            dndetailmodel=dnDetailModels.get(index);
                            txtScanQty.setText("扫描数量："+dndetailmodel.getSCAN_QTY());
                        }
                        GetDeliveryOrderScanList();
                    }
                }).setNegativeButton("取消", null).show();


    }

    void GetDeliveryOrderScanList(){
        DNScanModels= DbDnInfo.getInstance().GetLoaclDNScanModelDN(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO());
        exceptionScanbarcodeAdapter=new ExceptionScanbarcodeAdapter(context, DNScanModels);
        lsvDeliveryScan.setAdapter(exceptionScanbarcodeAdapter);
        txtScanQty.setText("扫描数量："+DNScanModels.size());
    }




}
