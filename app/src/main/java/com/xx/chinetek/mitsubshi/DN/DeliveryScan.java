package com.xx.chinetek.mitsubshi.DN;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.xx.chinetek.adapter.DN.DeliveryScanItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.AnalyticsBarCode;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.PlaySound;
import com.xx.chinetek.method.Scan;
import com.xx.chinetek.method.Upload.UploadDN;
import com.xx.chinetek.mitsubshi.BaseIntentActivity;
import com.xx.chinetek.mitsubshi.Exception.ExceptionBarcodelist;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.BarCodeModel;
import com.xx.chinetek.model.Base.BarcodeRule;
import com.xx.chinetek.model.Base.DNStatusEnum;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DBReturnModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xx.chinetek.method.Delscan.Delscan.DelDNDetailmodel;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_UploadDN;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_ScanBarcode;

@ContentView(R.layout.activity_delivery_scan)
public class DeliveryScan extends BaseIntentActivity {

    Context context=DeliveryScan.this;


    @ViewInject(R.id.img_exception)
    ImageView imgexception;
    @ViewInject(R.id.img_Remark)
    ImageView imgRemark;
    @ViewInject(R.id.txt_ItemNo)
    TextView txtItemNo;
    @ViewInject(R.id.txt_Remark)
    TextView txtRemark;
    @ViewInject(R.id.txt_ItemName)
    TextView txtItemName;
    @ViewInject(R.id.txt_ScanQty)
    TextView txtScanQty;
    @ViewInject(R.id.txt_ScanDnQty)
    TextView itemScanQty;
    @ViewInject(R.id.txt_SumDnQty)
    TextView itemDNQty;
    @ViewInject(R.id.txt_BarRule)
    TextView txtBarRule;
    @ViewInject(R.id.txt_DnNo)
    EditText txtDnNo;
    @ViewInject(R.id.txt_Custom)
    TextView txtCustom;
    @ViewInject(R.id.CBCloseDN)
    CheckBox CBCloseDN;
    @ViewInject(R.id.spin_barRule)
    Spinner spinbarRule;
    @ViewInject(R.id.lsv_DeliveryScan)
    ListView lsvDeliveryScan;

    DeliveryScanItemAdapter deliveryScanItemAdapter;
    ArrayList<DNDetailModel> dnDetailModels;
    ArrayList<BarCodeModel> ErrorBarcodes;//错误消息集合
    DNModel dnModel;
    DbDnInfo dnInfo;
    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_UploadDN:
                final DBReturnModel dbReturnModel=UploadDN.AnalysisUploadDNToMapsJson(context,(String) msg.obj,dnModel);
                if(dbReturnModel.getReturnCode()==-1 || dbReturnModel.getReturnCode()==-2){
                    new AlertDialog.Builder(this).setTitle("提示")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setMessage(dbReturnModel.getReturnMsg())
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    if( dbReturnModel.getReturnCode()==-2){
                                        txtDnNo.setFocusable(true);
                                        dnModel.setSTATUS(DNStatusEnum.Repert);
                                        CommonUtil.setEditFocus(txtDnNo);
                                    }else {
                                        closeActiviry();
                                    }
                                }
                            })
                            .show();
                }
                else{
                    new AlertDialog.Builder(this).setTitle("提示")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setMessage("出库单提交成功！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    closeActiviry();
                                }
                            })
                            .show();
                }
                break;
            case TAG_SCAN:
                LogUtil.WriteLog(DeliveryScan.class, TAG_ScanBarcode, (String) msg.obj);
                try {
                    CheckScanBarcode((String) msg.obj);
                }catch (Exception ex){
                    ToastUtil.show(ex.getMessage());
                    LogUtil.WriteLog(DeliveryScan.class,"DeliveryScan-CheckScanBarcode", ex.toString());
                }
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                String result=(String)msg.obj;
                if(result.contains("NETERROR")) {
                    DbDnInfo.getInstance().ChangeDNStatusByDnNo(dnModel.getAGENT_DN_NO(), DNStatusEnum.complete);
                }
                ToastUtil.show("获取请求失败_____" + result);
                break;
        }
    }



    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.deliveryScan),true);
        x.view().inject(this);
        //显示备注栏
        int visable=ParamaterModel.baseparaModel.getUseRemark()?View.VISIBLE:View.GONE;
        imgRemark.setVisibility(visable);
    }

    @Override
    protected void initData() {
        super.initData();
        ErrorBarcodes=new ArrayList<>();
        dnInfo=DbDnInfo.getInstance();
        dnModel=getIntent().getParcelableExtra("DNModel");
        txtDnNo.setText(ParamaterModel.DnTypeModel.getDNType()==3?dnModel.getCUS_DN_NO():dnModel.getAGENT_DN_NO());
        if(ParamaterModel.DnTypeModel.getDNType()==3)
            CBCloseDN.setVisibility(View.GONE);
        txtCustom.setText(ParamaterModel.DnTypeModel.getDNType()==3 && ParamaterModel.DnTypeModel.getCustomModel()!=null?ParamaterModel.DnTypeModel.getCustomModel().getNAME():
                dnModel.getCUSTOM_NAME()==null?dnModel.getLEVEL_2_AGENT_NAME():dnModel.getCUSTOM_NAME());
//        txtDnNo.setText(dnModel.getDN_SOURCE()==3?dnModel.getCUS_DN_NO():dnModel.getAGENT_DN_NO());
//        txtCustom.setText(dnModel.getCUSTOM_NAME()==null?dnModel.getLEVEL_2_AGENT_NAME():dnModel.getCUSTOM_NAME());
        ShowRemark();
        if(ParamaterModel.baseparaModel.getCusBarcodeRule()!=null && ParamaterModel.baseparaModel.getCusBarcodeRule().getUsed()){
            txtBarRule.setVisibility(View.VISIBLE);
            spinbarRule.setVisibility(View.VISIBLE);
            ArrayList<String> barRules=new ArrayList();
            if(ParamaterModel.baseparaModel.getCusBarcodeRule().getBarcodeRules()!=null) {
                ArrayList<BarcodeRule> barcodeRules=ParamaterModel.baseparaModel.getCusBarcodeRule().getBarcodeRules();
                for (BarcodeRule rule:barcodeRules) {
                    barRules.add(rule.getRuleName());
                }
            }
            ArrayAdapter<String> adapter =new  ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            adapter.addAll(barRules);
            spinbarRule.setAdapter(adapter);
            spinbarRule.setSelection(ParamaterModel.DnTypeModel.getSelectRule());
            spinbarRule.setPrompt(getString(R.string.choiceBarRules));
        }

        dnModel.__setDaoSession(dnInfo.getDaoSession());

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan_title, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_submit){
            if(dnDetailModels.size()==0){
                MessageBox.Show(context,getString(R.string.Msg_ScanMaterial));
                return false;
            }

            boolean canUpload=true;
            boolean isFlag=true;
            for(int i=0;i<dnDetailModels.size();i++){
                if(dnDetailModels.get(i).getFlag()!=null && dnDetailModels.get(i).getFlag()==1){
                    isFlag=false;
                    break;
                }
                if(dnDetailModels.get(i).getSCAN_QTY()!=null &&
                        dnDetailModels.get(i).getDN_QTY()<dnDetailModels.get(i).getSCAN_QTY()){
                    canUpload=false;
                    break;
                }
                if(dnDetailModels.get(i).getSERIALS()==null){
                    List<DNScanModel>  DNScanModels= DbDnInfo.getInstance().GetLoaclDNScanModelDN(dnDetailModels.get(i).getAGENT_DN_NO(),dnDetailModels.get(i).getGOLFA_CODE(),dnDetailModels.get(i).getLINE_NO());
                    dnDetailModels.get(i).setSERIALS(DNScanModels);
                }
            }
            if(!isFlag){
                MessageBox.Show(context, getString(R.string.Msg_miltuMaterial));
                return false;
            }
            if(!canUpload) {
                MessageBox.Show(context,getString(R.string.Msg_ScnaQtyError));
                return false;
            }
            //重复出库单修改单号
            if(dnModel!=null  && dnModel.getSTATUS()==DNStatusEnum.Repert){
                //修改出库单号
                String dnno=txtDnNo.getText().toString();
                DNModel model = DbDnInfo.getInstance().GetLoaclDN(dnno);
                if (model != null) {
                    MessageBox.Show(context, getString(R.string.Msg_HaveSameDN));
                    CommonUtil.setEditFocus(txtDnNo);
                    return true;
                }
                dnModel.setSTATUS(DNStatusEnum.download);
                DbDnInfo.getInstance().ChangeDNNoByRepertDnNo(dnno,dnModel);
                if(dnModel.getDN_SOURCE()==3)
                    dnModel.setCUS_DN_NO(dnno);
                else {
                    dnModel.setCUS_DN_NO(dnno);
                    dnModel.setAGENT_DN_NO(dnno);
                    for (DNDetailModel dnDetailModel : dnModel.getDETAILS()) {
                        dnDetailModel.setAGENT_DN_NO(dnno);
                        for (DNScanModel dnScanModel : dnDetailModel.getSERIALS()) {
                            dnScanModel.setAGENT_DN_NO(dnno);
                        }
                    }
                }
            }

            UploadDN.SumbitDN(context,dnModel,CBCloseDN.isChecked()?"F":"N",mHandler);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();
        dnDetailModels= DbDnInfo.getInstance().GetDNDetailByDNNo(dnModel.getAGENT_DN_NO());
        qty=0;
        scanqty=0;
        if(dnDetailModels!=null&&dnDetailModels.size()>=1){
            for(int i=0;i<dnDetailModels.size();i++){
                qty +=dnDetailModels.get(i).getDN_QTY()==null?0:dnDetailModels.get(i).getDN_QTY();
                scanqty+=dnDetailModels.get(i).getSCAN_QTY()==null?0:dnDetailModels.get(i).getSCAN_QTY();
            }
        }
        GetDeliveryOrderScanList();
    }


    /**
     * 错误信息
     * @param view
     */
    @Event(R.id.img_exception)
    private  void ImgexceptionClick(View view){
        if(ErrorBarcodes==null||ErrorBarcodes.size()==0){
            return;
        }else{
            Intent intent=new Intent(context,DeliveryExceptionbarcode.class);
            Bundle bundle=new Bundle();
            bundle.putParcelableArrayList("barcodemodels",ErrorBarcodes);
            intent.putExtras(bundle);
            startActivityLeft(intent);
        }
    }

    /**
     * 备注
     * @param view
     */
    @Event(R.id.img_Remark)
    private  void ImgRemarkClick(View view){
//        new Thread() {
//            @Override
//            public void run() {
//                int k=0;
//                for(int i=0;i<2000;i++) {
//                    if (i % 900 == 0) k++;
//                    String ser = CommonUtil.PadRight(i + "", "0", 4);
//                    String barcode = "30JT40" + k + "            796723002" + ser + "    20170913SBDCNHG-KN23J-S100                  0001";
//                    android.os.Message msg = mHandler.obtainMessage(TAG_SCAN,barcode);
//                    mHandler.sendMessage(msg);
//                    Log.i("scan",i+"");
//                    try {
//                        Thread.sleep(100);
//                    }catch (Exception ex){}
//                }
//            }
//        }.start();


        final EditText et = new EditText(this);
        et.setTextColor(getResources().getColor(R.color.black));
        new AlertDialog.Builder(this).setTitle("备注")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        if (!TextUtils.isEmpty(input)) {
                            dnModel.setREMARK(input);
                            try {
                                ArrayList<DNModel> dnModels = new ArrayList<DNModel>();
                                dnModels.add(dnModel);
                                dnInfo.InsertDNDB(dnModels);
                                ShowRemark();
                            }catch (Exception ex){
                                MessageBox.Show(context,ex.getMessage());
                            }
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }


    /**
     * 扫描条码
     * @param barcode
     * @return
     */
    private Boolean CheckScanBarcode(String barcode) {
        if (TextUtils.isEmpty(barcode)) {
           // MessageBox.Show(context, getString(R.string.Msg_No_Barcode));
            return true;
        }

        try {
            if(dnModel.getSTATUS()== DNStatusEnum.Sumbit || dnModel.getSTATUS()== DNStatusEnum.complete){ //已提交单据无法扫描
                MessageBox.Show(context, getString(R.string.Msg_DnScan_Finished));
                return true;
            }

            if((ParamaterModel.DnTypeModel.getDNType() == 3 && dnModel.getDN_SOURCE()==null )){
                DNModel model = DbDnInfo.getInstance().GetLoaclDN(txtDnNo.getText().toString());
                if (model != null) {
                    MessageBox.Show(context, getString(R.string.Msg_HaveSameDN));
                    CommonUtil.setEditFocus(txtDnNo);
                    return true;
                }
                txtDnNo.setFocusable(false);
            }
            dnModel.setCUS_DN_NO(txtDnNo.getText().toString().trim());
//            ParamaterModel.DnTypeModel.setSelectRule(spinbarRule.getSelectedItemPosition());
//            SharePreferUtil.SetDNTypeShare(context,ParamaterModel.DnTypeModel);
            ArrayList<BarCodeModel> barCodeModels =AnalyticsBarCode.CheckBarcode(barcode,spinbarRule.getSelectedItemPosition());
            if (barCodeModels != null && barCodeModels.size() != 0) {
                List<MaterialModel> materialModels = DbBaseInfo.getInstance().GetItemNames(barCodeModels.get(0).getGolfa_Code());
                if(materialModels!=null && materialModels.size()==1) {
                    txtItemNo.setText(materialModels.get(0).getMATNR());
                    txtItemName.setText(materialModels.get(0).getMAKTX());
                    txtScanQty.setText(getString(R.string.scanQty) + barCodeModels.size());
                }
//                MaterialModel materialModels = DbBaseInfo.getInstance().GetItemName(barCodeModels.get(0).getGolfa_Code());
//                if(materialModels!=null ) {
//                    txtItemNo.setText(materialModels.getMATNR());
//                    txtItemName.setText(materialModels.getMAKTX());
//                    txtScanQty.setText(getString(R.string.scanQty) + barCodeModels.size());
//                }
                if (ParamaterModel.DnTypeModel.getDNType() == 3) { //自建
                    return CreateNewDN(barCodeModels,materialModels);
                } else {
                    int isErrorStatus= Scan.ScanBarccode(dnInfo,dnModel,barCodeModels);
                    dnDetailModels=(ArrayList<DNDetailModel>) dnModel.getDETAILS();
                    if (ShowErrMag(isErrorStatus,barCodeModels.get(0))) return true;
                    scanqty+=barCodeModels.size();
                    return SaveScanInfo();

                }
            }
        } catch (Exception ex) {
            PlaySound.getInstance().PlayError();
            BarCodeModel Errorbarcode=new BarCodeModel();
            Errorbarcode.setSerial_Number("异常");
            Errorbarcode.setPlace_Code(ex.getMessage());
            ErrorBarcodes.add(Errorbarcode);
            imgexception.setVisibility(View.VISIBLE);
        }
        return true;
    }


    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemClickListener.class)
    private void lsvDeliveryScanonItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int flagposition=position;
        if (flagposition < 0) {
            MessageBox.Show(context, "请先选择操作的行！");
            return;
        }

        Intent intent=new Intent(context,ExceptionBarcodelist.class);
        intent.putExtra("position",position);
        intent.putExtra("DNno",dnModel.getAGENT_DN_NO());
        intent.putExtra("WinModel",0);
        startActivityLeft(intent);
//        DNDetailModel DNdetailModel= (DNDetailModel)deliveryScanItemAdapter.getItem(flagposition);
//        Intent intent=new Intent(context,ExceptionBarcodelist.class);
//        Bundle bundle=new Bundle();
//        bundle.putParcelable("DNdetailModel",DNdetailModel);
//        bundle.putParcelable("DNModel",dnModel);
//        bundle.putInt("WinModel",0);
//        intent.putExtras(bundle);
//        startActivityLeft(intent);
    }

    Integer qty=0;
    Integer scanqty=0;
    /**
     * 绑定列表
     */
    void GetDeliveryOrderScanList(){
        if(dnDetailModels==null) dnDetailModels=new ArrayList<>();
       // dnDetailModels= DbDnInfo.getInstance().GetDNDetailByDNNo(dnModel.getAGENT_DN_NO());
        dnModel.setDETAILS(dnDetailModels);
        txtDnNo.setFocusable(dnDetailModels.size()==0 || dnModel.getSTATUS()==DNStatusEnum.Repert?true:false);
        int source=dnModel.getDN_SOURCE()==null?ParamaterModel.DnTypeModel.getDNType():dnModel.getDN_SOURCE();
        deliveryScanItemAdapter=new DeliveryScanItemAdapter(context, dnDetailModels,source);
        lsvDeliveryScan.setAdapter(deliveryScanItemAdapter);
// Integer qty=0;
 ///       Integer scanqty=0;
//        if(dnDetailModels!=null&&dnDetailModels.size()>=1){
//            for(int i=0;i<dnDetailModels.size();i++){
//                qty +=dnDetailModels.get(i).getDN_QTY()==null?0:dnDetailModels.get(i).getDN_QTY();
//                scanqty+=dnDetailModels.get(i).getSCAN_QTY()==null?0:dnDetailModels.get(i).getSCAN_QTY();
//            }
//        }
        itemScanQty.setText("总扫描数："+scanqty);
       if(source!=3)  itemDNQty.setText("总出库数："+qty);
    }



    /**
     * 保存数据库
     * @return
     * @throws Exception
     */
    private boolean SaveScanInfo() throws Exception {

//        //保存至数据库
//        ArrayList<DNModel> dnModels = new ArrayList<DNModel>();
//        dnModel.setOPER_DATE(new Date());
//        dnModels.add(dnModel);
//        dnInfo.InsertDNDB(dnModels);
        //刷新listview
        GetDeliveryOrderScanList();
        return true;
    }

    private  void ShowRemark(){
        if(dnModel.getREMARK()!=null && !TextUtils.isEmpty(dnModel.getREMARK())){
            txtRemark.setVisibility(View.VISIBLE);
            txtRemark.setText(dnModel.getREMARK());
        }
    }

    int selectIndex=0;
    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemLongClickListener.class)
    private boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i < 0) {
            MessageBox.Show(context, "请先选择操作的行！");
            return false;
        }
        if(dnModel.getSTATUS()== DNStatusEnum.Sumbit || dnModel.getSTATUS()== DNStatusEnum.complete){ //已提交单据无法扫描
            MessageBox.Show(context, getString(R.string.Msg_DnScan_Finished));
            return true;
        }
        final DNDetailModel detailModel= (DNDetailModel)deliveryScanItemAdapter.getItem(i);
        final  int position=i;
        if(detailModel.getFlag()!=null && detailModel.getFlag()==1){ //多条物料主数据
                final List<MaterialModel> materialModels = DbBaseInfo.getInstance().GetItemNames(detailModel.getGOLFA_CODE());
                if (materialModels.size() > 1) {
                    String[] items = new String[materialModels.size()];
                    for (int j = 0; j < materialModels.size(); j++) {
                        String item = "SAP号:" + materialModels.get(j).getMATNR() + "\n" + materialModels.get(j).getBISMT() + "\n"
                                + materialModels.get(j).getMAKTX();
                        items[j] = item;
                    }
                    selectIndex = 0;
                    new AlertDialog.Builder(this)
                            .setTitle("选择物料")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            //设置普通文本格式的对话框，设置的是普通的Item；
                            .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    selectIndex = i;
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dnModel.getDETAILS().get(position).setITEM_NO(materialModels.get(selectIndex).getMATNR());
                                    dnModel.getDETAILS().get(position).setITEM_NAME(materialModels.get(selectIndex).getMAKTX());
                                    dnModel.getDETAILS().get(position).setGOLFA_CODE(materialModels.get(selectIndex).getBISMT());
                                    dnModel.getDETAILS().get(position).setFlag(0);
                                    try {
                                        Boolean isExcecption=false;
                                        for(int k=0;k<dnModel.getDETAILS().size();k++) {
                                            if (dnModel.getDETAILS().get(k).getFlag()!=null && dnModel.getDETAILS().get(k).getFlag() == 1) {
                                                isExcecption=true;
                                                break;
                                            }
                                        }
                                        if(!isExcecption) {
                                            dnModel.setFlag(0);
                                        }
                                        DbDnInfo.getInstance().InsertDNDetailDB(dnModel.getDETAILS().get(position));
                                        DbDnInfo.getInstance().InsertDNModel(dnModel);
                                        dnModel=DbDnInfo.getInstance().GetLoaclDN(dnModel.getAGENT_DN_NO());
                                        dnDetailModels=(ArrayList<DNDetailModel>) dnModel.getDETAILS();
                                        SaveScanInfo();
                                    }catch (Exception ex){
                                        MessageBox.Show(context,ex.getMessage());
                                    }
                                }
                            })
                            .setNegativeButton("取消", null).show();
                }else{
                    new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("确认删除扫描记录？\n")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO 自动生成的方法
                                    DelDNDetailmodel(context, detailModel, dnModel);
                                    scanqty-=detailModel.getSCAN_QTY();
                                    DNModel dnModelt=DbDnInfo.getInstance().GetLoaclDN(dnModel.getAGENT_DN_NO());
                                    if(dnModelt==null) {
                                        dnDetailModels=null;
                                    }
                                    else {
                                        dnModel=dnModelt;
                                        dnDetailModels = (ArrayList<DNDetailModel>) dnModelt.getDETAILS();
                                    }

                                    GetDeliveryOrderScanList();

                                }
                            }).setNegativeButton("取消", null).show();
                }
        }else {
            new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("确认删除扫描记录？\n")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 自动生成的方法
                            DelDNDetailmodel(context, detailModel, dnModel);
                            scanqty-=detailModel.getSCAN_QTY();
                            DNModel dnModelt=DbDnInfo.getInstance().GetLoaclDN(dnModel.getAGENT_DN_NO());
                            if(dnModelt==null) {
                                dnDetailModels=null;
                            }
                            else {
                                dnModel=dnModelt;
                                dnDetailModels = (ArrayList<DNDetailModel>) dnModel.getDETAILS();
                            }
                            GetDeliveryOrderScanList();

                        }
                    }).setNegativeButton("取消", null).show();
        }
        return true;
    }

    /**
     * 显示错误信息
     * @param isErrorStatus
     * @return
     */
    private boolean ShowErrMag(int isErrorStatus,BarCodeModel materialModel) {
        //edtBarcode.setText("");
        String GolfCode=materialModel==null?"":materialModel.getGolfa_Code();
        BarCodeModel Errorbarcode=new BarCodeModel();
        Errorbarcode.setSerial_Number(GolfCode);
        boolean isError=false;
        if(isErrorStatus==0) {
            isError=true;
            Errorbarcode.setPlace_Code(getString(R.string.Msg_Serial_Scaned));
           // MessageBox.Show(context, getString(R.string.Msg_Serial_Scaned));
           // return true;
        }
        if(isErrorStatus==1) {
            isError=true;
            Errorbarcode.setPlace_Code(getString(R.string.Msg_SerialNum_Finished));
           // MessageBox.Show(context, getString(R.string.Msg_SerialNum_Finished));
           // return true;
        }
        if(isErrorStatus==2) {
            isError=true;
            Errorbarcode.setPlace_Code(getString(R.string.Msg_Material_NotMatch));
            //MessageBox.Show(context, getString(R.string.Msg_Material_NotMatch));
           // return true;
        }
        if(isError) {
            PlaySound.getInstance().PlayError();
            ErrorBarcodes.add(Errorbarcode);
            imgexception.setVisibility(View.VISIBLE);
        }
        return isError;
    }


    /**
     * 自建出库单
     * @param barCodeModels
     * @return
     * @throws Exception
     */
    private boolean CreateNewDN(ArrayList<BarCodeModel> barCodeModels,List<MaterialModel> materialModels) throws Exception {
//    private boolean CreateNewDN(ArrayList<BarCodeModel> barCodeModels,MaterialModel materialModels) throws Exception {
        //保存扫描数据
        int isErrorStatus=-1;//0:物料已扫描  1：数量已超出
       // dnModel.resetDETAILS();
       // List<DNDetailModel> dnDetailModels =dnModel.getDETAILS();
        for (BarCodeModel barCodeModel : barCodeModels) {
            DNDetailModel dnDetailModel = new DNDetailModel();
            //判断是否存在物料
            int index = Scan.getIndex(dnModel,dnDetailModels, barCodeModel, dnDetailModel);
            if (index != -1) {
                isErrorStatus=AddSerialByMaterialNo(dnDetailModels, barCodeModel, index);
                if(isErrorStatus!=-1) break;
            } else {
                NewSerialByMaterialNo(dnDetailModels, barCodeModel, dnDetailModel,materialModels);
            }
            dnModel.setDN_QTY(dnModel.getDN_QTY() + 1);
        }
        if (ShowErrMag(isErrorStatus,barCodeModels.get(0))) return true;
        scanqty+=barCodeModels.size();
        dnModel.setDN_STATUS("AC");
        dnModel.setSTATUS(1);
        dnModel.setLEVEL_2_AGENT_NO(ParamaterModel.PartenerID);
        dnModel.setLEVEL_2_AGENT_NAME(ParamaterModel.PartenerName);
        if(ParamaterModel.DnTypeModel.getDNCusType()!=null) {
          //  if (ParamaterModel.DnTypeModel.getCustomModel().getPARTNER_FUNCTION().equals("Z3")) {
                dnModel.setCUSTOM_NO(ParamaterModel.DnTypeModel.getCustomModel().getCUSTOMER());
                dnModel.setCUSTOM_NAME(ParamaterModel.DnTypeModel.getCustomModel().getNAME());
           // }
        }
        dnModel.setDN_DATE(new Date());
        dnModel.setDN_SOURCE(ParamaterModel.DnTypeModel.getDNType());
        DbDnInfo.getInstance().InsertDNModel(dnModel);
        return SaveScanInfo();
    }


    /**
     * 自建新增序列号
     * @param dnDetailModels
     * @param barCodeModel
     * @param dnDetailModel
     */
    private void NewSerialByMaterialNo(List<DNDetailModel> dnDetailModels,
                                       BarCodeModel barCodeModel, DNDetailModel dnDetailModel,List<MaterialModel> materialModels) throws  Exception{
//   private void NewSerialByMaterialNo(ArrayList<DNDetailModel> dnDetailModels,
//                                       BarCodeModel barCodeModel, DNDetailModel dnDetailModel,MaterialModel materialModels) {
//        //保存物料信息
        int Line_no=1;
        if(dnModel.getDETAILS()!=null && dnModel.getDETAILS().size()!=0){
            Line_no=dnModel.getDETAILS().get(dnModel.getDETAILS().size()-1).getLINE_NO()+1;
        }
        dnDetailModel.setLINE_NO(Line_no);//行号
        dnDetailModel.setDN_QTY(9999);
        dnDetailModel.setSCAN_QTY(1);
        dnDetailModel.setDETAIL_STATUS("AC");
        dnDetailModel.setSTATUS(0);
        dnDetailModel.setOPER_DATE(new Date());
        //多条主数据
        boolean isError=(materialModels!=null && materialModels.size()==1) || barCodeModel.getMAT_TYPE()==0;
        dnDetailModel.setFlag(isError?0:1);
        dnModel.setFlag(isError?0:1);


        if(materialModels!=null && materialModels.size()>0) {
            MaterialModel materialModel = materialModels.get(0);
            dnDetailModel.setITEM_NAME(materialModel == null ? "" : materialModel.getMAKTX());
            dnDetailModel.setITEM_NO(materialModel == null ? "" : materialModel.getMATNR());
        }
//       dnDetailModel.setFlag(0);
//       dnModel.setFlag(0);
//       dnDetailModel.setITEM_NAME(materialModels == null ? "" : materialModels.getMAKTX());
//       dnDetailModel.setITEM_NO(materialModels == null ? "" : materialModels.getMATNR());
        //保存序列号
        DNScanModel dnScanModel=new DNScanModel();
        dnScanModel.setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
        dnScanModel.setLINE_NO(dnDetailModel.getLINE_NO());
        dnScanModel.setITEM_NO( dnDetailModel.getITEM_NO());
        dnScanModel.setITEM_NAME( dnDetailModel.getITEM_NAME());
        dnScanModel.setSERIAL_NO(barCodeModel.getSerial_Number());
        dnScanModel.setPACKING_DATE(barCodeModel.getPacking_Date());
        dnScanModel.setREGION(barCodeModel.getPlace_Code());
        dnScanModel.setCOUNTRY(barCodeModel.getCountry_Code());
        dnScanModel.setGOLFA_CODE(barCodeModel.getGolfa_Code());
        dnScanModel.setITEM_STATUS("AC");
        dnScanModel.setDEAL_SALE_DATE(CommonUtil.DateToString(new Date(),"yyyy/MM/dd"));
        dnScanModel.setMAT_TYPE(barCodeModel.getMAT_TYPE());
        dnScanModel.setSTATUS("0");
        Scan.setOtherColumn(barCodeModel, dnScanModel);

        DbDnInfo.getInstance().InsertDNScanModel(dnScanModel);

        dnDetailModel.__setDaoSession(dnInfo.getDaoSession());
        if(dnDetailModel.getSERIALS()==null)
            dnDetailModel.setSERIALS(new ArrayList<DNScanModel>());
        dnDetailModel.getSERIALS().add(0,dnScanModel);
        dnDetailModels.add(dnDetailModel);

        DbDnInfo.getInstance().InsertDNDetailDB(dnDetailModel);
        //更新DN数据

    }




    /**
     * 自建添加序列号
     * @param dnDetailModels
     * @param barCodeModel
     * @param index
     * @return
     */
    private int  AddSerialByMaterialNo(List<DNDetailModel> dnDetailModels, BarCodeModel barCodeModel, int index) throws  Exception{
        //判断条码是否存在
        dnDetailModels.get(index).__setDaoSession(dnInfo.getDaoSession());
        List<DNScanModel> dnScanModels=dnDetailModels.get(index).getSERIALS();
        DNScanModel dnScanModel=new DNScanModel();
        dnScanModel.setSERIAL_NO(barCodeModel.getSerial_Number());
        int barcodeIndex=dnScanModels.indexOf(dnScanModel);
        if(barcodeIndex!=-1) return 0;

        //保存序列号
        dnScanModel.setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
        dnScanModel.setLINE_NO( dnDetailModels.get(index).getLINE_NO());
        dnScanModel.setITEM_NO( dnDetailModels.get(index).getITEM_NO());
        dnScanModel.setITEM_NAME( dnDetailModels.get(index).getITEM_NAME());
        dnScanModel.setPACKING_DATE(barCodeModel.getPacking_Date());
        dnScanModel.setREGION(barCodeModel.getPlace_Code());
        dnScanModel.setCOUNTRY(barCodeModel.getCountry_Code());
        dnScanModel.setGOLFA_CODE(barCodeModel.getGolfa_Code());
        dnScanModel.setITEM_STATUS("AC");
        dnScanModel.setDEAL_SALE_DATE(CommonUtil.DateToString(new Date(),"yyyy/MM/dd"));
        dnScanModel.setMAT_TYPE(barCodeModel.getMAT_TYPE());
        dnScanModel.setSTATUS("0");
        dnDetailModels.get(index).getSERIALS().add(0,dnScanModel);
        Scan.setOtherColumn(barCodeModel, dnScanModel);

        DbDnInfo.getInstance().InsertDNScanModel(dnScanModel);
        //更新物料扫码数量
        int qty = dnDetailModels.get(index).getSCAN_QTY() + 1;
       // dnDetailModels.get(index).setDN_QTY(qty);
        dnDetailModels.get(index).setSCAN_QTY(qty);
        DbDnInfo.getInstance().InsertDNDetailDB(dnDetailModels.get(index));
        return -1;
    }

}
