package com.xx.chinetek.mitsubshi.Exception;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.adapter.Exception.ExceptionScanbarcodeAdapter;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.DB.DbLogInfo;
import com.xx.chinetek.method.Delscan.DelAgentScan;
import com.xx.chinetek.method.Scan;
import com.xx.chinetek.mitsubshi.BaseIntentActivity;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.BarCodeModel;
import com.xx.chinetek.model.Base.DNStatusEnum;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;
import com.xx.chinetek.model.DN.LogModel;
import com.xx.chinetek.model.Third.ThirdReturnModel;
import com.xx.chinetek.model.Third.ReAdjust;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import static com.xx.chinetek.method.Delscan.Delscan.DelScanmodel;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_DeleteQRScan;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SubmitQRScan;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_DeleteQRScan;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_ScanBarcode;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SubmitQRScan;

@ContentView(R.layout.activity_exception_scanlist)
public class ExceptionBarcodelist extends BaseIntentActivity {

    Context context = ExceptionBarcodelist.this;
    @ViewInject(R.id.img_Remark)
    ImageView img_Remark;
    @ViewInject(R.id.txt_PickDate)
    TextView txtPickDate;
    @ViewInject(R.id.edt_Barcode)
    EditText edtBarcode;
    @ViewInject(R.id.edt_PickDate)
    EditText edtPickDate;
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
    @ViewInject(R.id.btn_DeleteException)
    Button btn_DeleteException;
    @ViewInject(R.id.txt_Custom)
    TextView txtCustom;
    @ViewInject(R.id.lsv_DeliveryScan)
    ListView lsvDeliveryScan;

    ExceptionScanbarcodeAdapter exceptionScanbarcodeAdapter;
    ArrayList<DNScanModel> DNScanModels,delDNScanModels;
    DNModel dnModel;
    DbDnInfo dnInfo;
    DNDetailModel dndetailmodel;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle = new ToolBarTitle(getString(R.string.ScanDetails), true);
        x.view().inject(this);
    }

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case TAG_SCAN:
                try {
                    LogUtil.WriteLog(ExceptionBarcodelist.class, TAG_ScanBarcode, (String) msg.obj);
                    DbLogInfo.getInstance().InsertLog(new LogModel("序列号扫描-扫描条码",(String) msg.obj,txtDnNo.getText().toString()));
                    edtBarcode.setText((String) msg.obj);
                    chaeckBarcode((String) msg.obj,edtPickDate.getText().toString().trim());
                } catch (Exception ex) {
                    ToastUtil.show(ex.getMessage());
                    LogUtil.WriteLog(ExceptionBarcodelist.class,"ExceptionBarcodelist-chaeckBarcode", ex.toString());
                }
                break;
            case RESULT_DeleteQRScan:
            case RESULT_SubmitQRScan:
                ThirdReturnModel<ReAdjust> returnMsgModel = GsonUtil.getGsonUtil().fromJson((String) msg.obj, new TypeToken<ThirdReturnModel<ReAdjust>>() {
                }.getType());
                if (returnMsgModel.getSuccess() != 1) {
                    DbLogInfo.getInstance().InsertLog(new LogModel(msg.what==RESULT_SubmitQRScan?"序列号扫描-代理商条码上传异常":"出库扫描-代理商条码删除异常",returnMsgModel.getMessage(),txtDnNo.getText().toString()));
                    MessageBox.Show(context, returnMsgModel.getMessage());
                }else{
                    if(msg.what==RESULT_SubmitQRScan){
                        DbDnInfo.getInstance().SetDndetailsFlag(dnModel.getAGENT_DN_NO(), returnMsgModel.getReAdjust());
                    }else {
                        for(ReAdjust reAdjust:returnMsgModel.getReAdjust() ){
                            DNScanModel dnScanModel=new DNScanModel();
                            dnScanModel.setSERIAL_NO(reAdjust.getSerial_No());
                            dnScanModel.setGOLFA_CODE(reAdjust.getGolfa_Code());
                            int index=delDNScanModels.indexOf(dnScanModel);
                            if(index!=-1){
                                DelScanmodel(context, delDNScanModels.get(index), dndetailmodel, dnModel);
                            }
                        }

//                        for (DNScanModel dnscamodel : delDNScanModels) {
//                            DelScanmodel(context, dnscamodel, dndetailmodel, dnModel);
//                        }
                        MessageBox.Show(context, getString(R.string.Msg_del_success));
                        GetDeliveryOrderScanList();
                    }
                }

                break;
            case NetworkError.NET_ERROR_CUSTOM:
                String result=(String)msg.obj;
                if(result.contains("NETERROR")) {
                    DbDnInfo.getInstance().ChangeDNStatusByDnNo(dnModel.getAGENT_DN_NO(), DNStatusEnum.download);
                }
                MessageBox.Show(context,"获取请求失败_____" + result);
                break;
        }
    }

    //    private String Flag="";
    @Override
    protected void initData() {
        super.initData();
        img_Remark.setVisibility(View.GONE);
        dnInfo = DbDnInfo.getInstance();
        int position=getIntent().getIntExtra("position",0);
        String  dnno=getIntent().getStringExtra("DNno");
        int winModel = getIntent().getIntExtra("WinModel", 0);
        dnModel=DbDnInfo.getInstance().GetLoaclDN(dnno);
        dnModel.__setDaoSession(dnInfo.getDaoSession());
        dndetailmodel=dnModel.getDETAILS().get(position);


        //初始化数据
        txtDnNo.setText(dnModel.getDN_SOURCE() == 3 || dnModel.getDN_SOURCE() == 5? dnModel.getCUS_DN_NO().toString() : dnModel.getAGENT_DN_NO().toString());
        txtCustom.setText(dnModel.getCUSTOM_NAME()==null || TextUtils.isEmpty(dnModel.getCUSTOM_NAME())?dnModel.getLEVEL_2_AGENT_NAME():dnModel.getCUSTOM_NAME());
        txtItemName.setText("物料名称：" + dndetailmodel.getITEM_NAME());
        txtItemNo.setText("物料编码：" + (dndetailmodel.getGOLFA_CODE()==null?dndetailmodel.getITEM_NO():dndetailmodel.getGOLFA_CODE()));
        txtKUQty.setText("出库数量：" + (dnModel.getDN_SOURCE() == 3 ?"9999":dndetailmodel.getDN_QTY()));
        GetDeliveryOrderScanList();
        if(DNScanModels!=null && winModel==1) {
            winModel=0;
            for (DNScanModel scanModel : DNScanModels) {
                if(!scanModel.getSTATUS().equals("0")){
                    winModel=1;
                    break;
                }
            }
        }
        if(ParamaterModel.IsAgentSoft ){
            txtPickDate.setVisibility(View.VISIBLE);
            edtPickDate.setVisibility(View.VISIBLE);
        }
        btn_DeleteException.setVisibility(winModel == 0 ? View.GONE : View.VISIBLE);
    }


    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        BaseApplication.getRequestQueue().cancelAll(TAG_SubmitQRScan);
        BaseApplication.getRequestQueue().cancelAll(TAG_DeleteQRScan);
    }

    @Event(R.id.btn_DeleteException)
    private void DelExceptionClick(View view) {
        try {
            new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("确认批量删除异常扫描数据？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 自动生成的方法
                            boolean isdel=true;
                            delDNScanModels=new ArrayList<>();
                            for(int i=0;i<DNScanModels.size();i++){
                                if(exceptionScanbarcodeAdapter.getStates(i)){
                                    if(dnModel.getDN_SOURCE()==5 || (ParamaterModel.IsAgentSoft && dnModel.getDN_SOURCE()==3))delDNScanModels.add(DNScanModels.get(i));
                                    else {
                                        if (!DbDnInfo.getInstance().DeleteSelectItems(DNScanModels.get(i))) {
                                            isdel = false;
                                            break;
                                        }
                                    }
                                }
                            }
                            if(dnModel.getDN_SOURCE()==5 || (ParamaterModel.IsAgentSoft && dnModel.getDN_SOURCE()==3)){
                               if(delDNScanModels!=null && delDNScanModels.size()!=0) DelAgentScan.DelScan(mHandler, dnModel.getCUS_DN_NO(), delDNScanModels);
                            }else {
                                if (isdel) {
                                    MessageBox.Show(context, getString(R.string.Msg_del_success));
                                }
                                GetDeliveryOrderScanList();
                            }
                        }
                    }).setNegativeButton("取消", null).show();
        } catch(Exception ex)
        {
            ToastUtil.show(ex.getMessage());
            LogUtil.WriteLog(ExceptionBarcodelist.class,"ExceptionBarcodelist-DelExceptionClick", ex.toString());
        }

}

    @Event(value = R.id.edt_PickDate, type = View.OnKeyListener.class)
    private boolean edtPickDateOnKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            try {
                final String code = edtPickDate.getText().toString().trim();
                chaeckBarcode(edtBarcode.getText().toString().trim(), code);
            }catch(Exception ex) {
                MessageBox.Show(context, ex.getMessage());
                CommonUtil.setEditFocus(edtPickDate);
                return true;
            }
            return true;
        }
        return false;
    }


    @Event(value = R.id.edt_Barcode, type = View.OnKeyListener.class)
    private boolean edtfilterOnKey(View v, int keyCode, KeyEvent event) {
        try{
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
            {
                final String code=edtBarcode.getText().toString().trim();
                LogUtil.WriteLog(ExceptionBarcodelist.class, TAG_ScanBarcode, code);
                DbLogInfo.getInstance().InsertLog(new LogModel("序列号扫描-输入条码",code,txtDnNo.getText().toString()));

                chaeckBarcode(code,edtPickDate.getText().toString().trim());
                return true;
            }
        }catch(Exception ex){
            MessageBox.Show(context, ex.getMessage());
            CommonUtil.setEditFocus(edtBarcode);
            return true;
        }
        return false;
    }

    private void chaeckBarcode(String code,String PickDate) throws Exception {
        if(TextUtils.isEmpty(code)){
            MessageBox.Show(context,getString(R.string.Msg_No_Barcode));
            CommonUtil.setEditFocus(edtBarcode);
            return;
        }
        if(TextUtils.isEmpty(PickDate) && ParamaterModel.IsAgentSoft){
         //   MessageBox.Show(context,getString(R.string.Msg_No_PickDate));
            CommonUtil.setEditFocus(edtPickDate);
            return;
        }

        if(dnModel.getSTATUS()== DNStatusEnum.Sumbit || dnModel.getSTATUS()== DNStatusEnum.complete){ //已提交单据无法扫描
            MessageBox.Show(context, getString(R.string.Msg_DnScan_Finished));
            return;
        }
        if(code.length()>ParamaterModel.baseparaModel.getSerialMaxLength()){
            MessageBox.Show(context,getString(R.string.Msg_out_Barcode));
            CommonUtil.setEditFocus(edtBarcode);
            return;
        }

        BarCodeModel model = new BarCodeModel();
        model.setSerial_Number(code);
        model.setPacking_Date(PickDate);
        model.setGolfa_Code(dndetailmodel.getGOLFA_CODE());
        model.setLINE_NO(dndetailmodel.getLINE_NO());
        model.setMAT_TYPE(1); //默认三菱条码
        if(ParamaterModel.baseparaModel.getCusBarcodeRule()!=null && ParamaterModel.baseparaModel.getCusBarcodeRule().getUsed()) {
            MaterialModel materialModel = DbBaseInfo.getInstance().GetItemName(dndetailmodel.getGOLFA_CODE());
            model.setMAT_TYPE(materialModel == null?0:1);//物料没有记录，非三菱条码
        }
        ArrayList<BarCodeModel> barCodeModels = new ArrayList<>();
        barCodeModels.add(model);
        if (barCodeModels != null && barCodeModels.size() != 0) {
            int isErrorStatus=Scan.ScanBarccode(mHandler,dnInfo,dnModel,barCodeModels);
            txtScanQty.setText(getString(R.string.scanQty)+(DNScanModels.size()));
            if (ShowErrMag(isErrorStatus)){
                return;
            }
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
        CommonUtil.setEditFocus(edtBarcode);
        if(isErrorStatus==0) {
            DbLogInfo.getInstance().InsertLog(new LogModel("序列号扫描-扫描错误",getString(R.string.Msg_Serial_Scaned),txtDnNo.getText().toString()));
            MessageBox.Show(context, getString(R.string.Msg_Serial_Scaned));
            return true;
        }
        if(isErrorStatus==1) {
            DbLogInfo.getInstance().InsertLog(new LogModel("序列号扫描-扫描错误",getString(R.string.Msg_SerialNum_Finished),txtDnNo.getText().toString()));

            MessageBox.Show(context, getString(R.string.Msg_SerialNum_Finished));
            return true;
        }
        if(isErrorStatus==2) {
            DbLogInfo.getInstance().InsertLog(new LogModel("序列号扫描-扫描错误",getString(R.string.Msg_Material_NotMatch),txtDnNo.getText().toString()));

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
        if(dnModel.getSTATUS()== DNStatusEnum.Sumbit || dnModel.getSTATUS()== DNStatusEnum.complete){ //已提交单据无法扫描
            MessageBox.Show(context, getString(R.string.Msg_DnScan_Finished));
            return;
        }
        final DNScanModel Model= (DNScanModel)exceptionScanbarcodeAdapter.getItem(position);
        new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage(Model.getSTATUS().equals("0")?"扫描记录正常，是否删除扫描记录？\n":"是否删除扫描记录？\n")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 自动生成的方法
                        if(dnModel.getDN_SOURCE()==5 || (ParamaterModel.IsAgentSoft && dnModel.getDN_SOURCE()==3)) {
                            delDNScanModels = new ArrayList<>();
                            delDNScanModels.add(Model);
                            DelAgentScan.DelScan(mHandler, dnModel.getCUS_DN_NO(), delDNScanModels);
                        }else {
                            Integer LeftNum = DelScanmodel(context, Model, dndetailmodel, dnModel);
                            if (LeftNum == 0 && dnModel.getDN_SOURCE() == 3)
                                closeActiviry();

                            ArrayList<DNDetailModel> dnDetailModels = DbDnInfo.getInstance().GetLoaclExceptionDetailsDN(dnModel.getAGENT_DN_NO().toString());
                            int index = dnDetailModels.indexOf(dndetailmodel);
                            if (index > 0) {
                                dndetailmodel = dnDetailModels.get(index);
                                txtScanQty.setText("扫描数量：" + dndetailmodel.getSCAN_QTY());
                            }
                            GetDeliveryOrderScanList();
                        }
                    }
                }).setNegativeButton("取消", null).show();


    }

    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemLongClickListener.class)
    private boolean lsv_DeliveryScanItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        exceptionScanbarcodeAdapter.modifyStates(position);
        return true;
    }

    void GetDeliveryOrderScanList(){
        DNScanModels= DbDnInfo.getInstance().GetLoaclDNScanModelDN(dndetailmodel.getAGENT_DN_NO(),
                dndetailmodel.getGOLFA_CODE()==null?dndetailmodel.getITEM_NAME():dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO());
        exceptionScanbarcodeAdapter=new ExceptionScanbarcodeAdapter(context, DNScanModels);
        lsvDeliveryScan.setAdapter(exceptionScanbarcodeAdapter);
        txtScanQty.setText("扫描数量："+DNScanModels.size());
    }




}
