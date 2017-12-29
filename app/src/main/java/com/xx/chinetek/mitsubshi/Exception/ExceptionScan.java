package com.xx.chinetek.mitsubshi.Exception;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.xx.chinetek.adapter.Exception.ExceptionScanItemAdapter;
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
import com.xx.chinetek.mitsubshi.DN.DeliveryExceptionbarcode;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.BarCodeModel;
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

@ContentView(R.layout.activity_exception_scan)
public class ExceptionScan extends BaseIntentActivity {

    Context context=ExceptionScan.this;
    @ViewInject(R.id.img_exception)
    ImageView imgexception;
    @ViewInject(R.id.txt_DnNo)
    TextView txtDnNo;
    @ViewInject(R.id.txt_Custom)
    TextView txtCustom;
    @ViewInject(R.id.lsv_DeliveryScan)
    ListView lsvDeliveryScan;

    ExceptionScanItemAdapter exceptionScanItemAdapter;
    ArrayList<DNDetailModel> dnDetailModels;
    DNModel dnModel;
    DbDnInfo dnInfo;

    ArrayList<BarCodeModel> ErrorBarcodes;//错误消息集合

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_UploadDN:
                final DBReturnModel dbReturnModel=UploadDN.AnalysisUploadDNToMapsJson(context,(String) msg.obj,dnModel);
                if(dbReturnModel.getReturnCode()==-1){
                    new AlertDialog.Builder(this).setTitle("提示")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setMessage(dbReturnModel.getReturnMsg())
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    closeActiviry();
                                }
                            })
                            .show();
                }
                else{
                    new AlertDialog.Builder(this).setTitle("提示")
                            .setIcon(android.R.drawable.ic_dialog_info)
                            .setMessage("异常出库单提交成功！")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    closeActiviry();
                                }
                            })
                            .show();
                }
                break;
            case TAG_SCAN:
                try {
                    LogUtil.WriteLog(ExceptionBarcodelist.class, TAG_ScanBarcode, (String) msg.obj);
                    CheckScanBarcode((String) msg.obj);
                } catch (Exception ex) {
                    MessageBox.Show(context, ex.getMessage());
                }
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____" + msg.obj);
                break;
        }
    }

    @Override
    protected void initViews() {
       super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.BarcodeScan),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        ErrorBarcodes=new ArrayList<>();
        dnInfo=DbDnInfo.getInstance();
        dnModel=getIntent().getParcelableExtra("DNModel");
        txtDnNo.setText(dnModel.getDN_SOURCE()==3?dnModel.getCUS_DN_NO():dnModel.getAGENT_DN_NO());
        txtCustom.setText(dnModel.getCUSTOM_NAME()==null?dnModel.getLEVEL_2_AGENT_NAME():dnModel.getCUSTOM_NAME());
        dnModel.__setDaoSession(dnInfo.getDaoSession());
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetDeliveryOrderScanList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan_title, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_submit) {
//            if(!DbDnInfo.getInstance().CheckPostDate(dnModel)){
//                MessageBox.Show(context,"提交的扫描数据存在异常状态！");
//                return false;
//            }
            boolean canUpload = true;
            for (int i = 0; i < dnDetailModels.size(); i++) {
                if (dnDetailModels.get(i).getSCAN_QTY() != null &&
                        dnDetailModels.get(i).getDN_QTY() < dnDetailModels.get(i).getSCAN_QTY()) {
                    canUpload = false;
                    break;
                }
            }

            if (!canUpload) {
                MessageBox.Show(context, getString(R.string.Msg_ScnaQtyError));
                return false;
            }
            DbDnInfo.getInstance().UpdateDNScanState(dnModel.getAGENT_DN_NO());
            DNModel postmodel = DbDnInfo.getInstance().AllPostDate(dnModel);
            if (postmodel == null) {
                MessageBox.Show(context, "提交失败！");
            } else {
                UploadDN.SumbitDN(context, postmodel, mHandler);
            }


        }
        return super.onOptionsItemSelected(item);
    }


    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemClickListener.class)
    private void lsvDeliveryScanonItemClick(AdapterView<?> parent, View view, int position, long id) {
        final int flagposition=position;
        if (flagposition < 0) {
            MessageBox.Show(context, "请先选择操作的行！");
            return;
        }
//        new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("确认进入序列号扫描界面？\n")
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO 自动生成的方法
                        Intent intent=new Intent(context,ExceptionBarcodelist.class);
                        Bundle bundle=new Bundle();
                        DNDetailModel DNdetailModel= (DNDetailModel)exceptionScanItemAdapter.getItem(flagposition);
                        bundle.putParcelable("DNdetailModel",DNdetailModel);
                        bundle.putParcelable("DNModel",dnModel);
                         bundle.putInt("WinModel",1);
                        intent.putExtras(bundle);
                        startActivityLeft(intent);

//                    }
//                }).setNegativeButton("取消", null).show();

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


//    private int clickpositionlong=-1;
    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemLongClickListener.class)
    private boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i < 0) {
            MessageBox.Show(context, "请先选择操作的行！");
            return false;
        }
        final DNDetailModel detailModel= (DNDetailModel)exceptionScanItemAdapter.getItem(i);
        new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("确认删除扫描记录？\n")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 自动生成的方法
                        DelDNDetailmodel(context,detailModel,dnModel);
                        GetDeliveryOrderScanList();

                    }
                }).setNegativeButton("取消", null).show();
        return true;
    }


    void GetDeliveryOrderScanList(){
      //  DbDnInfo.getInstance().DeleteRepertItems(dnModel.getAGENT_DN_NO(),1); //序列号重复项
        dnDetailModels= DbDnInfo.getInstance().GetLoaclExceptionDetailsDN(dnModel.getAGENT_DN_NO().toString());
        int size=dnDetailModels.size();
        for(int i=0;i<size;i++){
            int scanQTY=DbDnInfo.getInstance().GetScanQtyInDNScanModel(dnDetailModels.get(i).getAGENT_DN_NO()
                    ,dnDetailModels.get(i).getGOLFA_CODE(),dnDetailModels.get(i).getLINE_NO());
            dnDetailModels.get(i).setSCAN_QTY(scanQTY);
        }
        dnModel.setDETAILS(dnDetailModels);
        exceptionScanItemAdapter=new ExceptionScanItemAdapter(context, dnDetailModels,dnModel.getDN_SOURCE());
        lsvDeliveryScan.setAdapter(exceptionScanItemAdapter);
    }

    private Boolean CheckScanBarcode(String barcode) {
        try {
            if(dnModel.getSTATUS()== DNStatusEnum.Sumbit || dnModel.getSTATUS()== DNStatusEnum.complete){ //已提交单据无法扫描
                MessageBox.Show(context, getString(R.string.Msg_DnScan_Finished));
                return true;
            }

            ArrayList<BarCodeModel> barCodeModels = AnalyticsBarCode.CheckBarcode(barcode);
            if (barCodeModels != null && barCodeModels.size() != 0) {
                MaterialModel materialModel = DbBaseInfo.getInstance().GetItemName(barCodeModels.get(0).getGolfa_Code());
                if (dnModel.getDN_SOURCE() == 3) { //自建
                    return CreateNewDN(barCodeModels,materialModel);
                } else {
                    int isErrorStatus= Scan.ScanBarccode(dnInfo,dnModel,barCodeModels);
                    if (ShowErrMag(isErrorStatus,barCodeModels.get(0))) return true;
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

    /**
     * 自建出库单
     * @param barCodeModels
     * @return
     * @throws Exception
     */
    private boolean CreateNewDN(ArrayList<BarCodeModel> barCodeModels,MaterialModel materialModel) throws Exception {
        //保存扫描数据
        int isErrorStatus=-1;//0:物料已扫描  1：数量已超出
        dnModel.resetDETAILS();
        List<DNDetailModel> dnDetailModels = dnModel.getDETAILS();
        for (BarCodeModel barCodeModel : barCodeModels) {
            DNDetailModel dnDetailModel = new DNDetailModel();
            //判断是否存在物料
            int index = Scan.getIndex(dnModel,dnDetailModels, barCodeModel, dnDetailModel);
            if (index != -1) {
                isErrorStatus=AddSerialByMaterialNo(dnDetailModels, barCodeModel, index);
                if(isErrorStatus!=-1) break;
            } else {
                NewSerialByMaterialNo(dnDetailModels, barCodeModel, dnDetailModel,materialModel);
            }
            dnModel.setDN_QTY(dnModel.getDN_QTY() + 1);
        }
        if (ShowErrMag(isErrorStatus,barCodeModels.get(0))) return true;
        dnModel.setDN_STATUS("AC");
        dnModel.setSTATUS(1);
        dnModel.setLEVEL_2_AGENT_NO(ParamaterModel.PartenerID);
        dnModel.setLEVEL_2_AGENT_NAME(ParamaterModel.PartenerName);
        if(ParamaterModel.DnTypeModel.getDNCusType()!=null) {
            if (ParamaterModel.DnTypeModel.getCustomModel().getPARTNER_FUNCTION().equals("Z3")) {
                dnModel.setCUSTOM_NO(ParamaterModel.DnTypeModel.getCustomModel().getCUSTOMER());
                dnModel.setCUSTOM_NAME(ParamaterModel.DnTypeModel.getCustomModel().getNAME());
            }
        }
        dnModel.setDN_DATE(new Date());
        //添加客户
        if(ParamaterModel.DnTypeModel.getDNCusType()!=null) {
            if (ParamaterModel.DnTypeModel.getDNCusType() == 0) {
                dnModel.setLEVEL_2_AGENT_NO(ParamaterModel.DnTypeModel.getCustomModel().getCUSTOMER());
                dnModel.setLEVEL_2_AGENT_NAME(ParamaterModel.DnTypeModel.getCustomModel().getNAME());
            } else if (ParamaterModel.DnTypeModel.getDNCusType() == 1) {
                dnModel.setCUSTOM_NO(ParamaterModel.DnTypeModel.getCustomModel().getCUSTOMER());
                dnModel.setCUSTOM_NAME(ParamaterModel.DnTypeModel.getCustomModel().getNAME());
            }
        }
        dnModel.setDN_SOURCE(ParamaterModel.DnTypeModel.getDNType());
        return SaveScanInfo();
    }

    /**
     * 保存数据库
     * @return
     * @throws Exception
     */
    private boolean SaveScanInfo() throws Exception {

        //保存至数据库
        ArrayList<DNModel> dnModels = new ArrayList<DNModel>();
        dnModel.setOPER_DATE(new Date());
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
        return false;
    }

    /**
     * 自建新增序列号
     * @param dnDetailModels
     * @param barCodeModel
     * @param dnDetailModel
     */
    private void NewSerialByMaterialNo(List<DNDetailModel> dnDetailModels,
                                       BarCodeModel barCodeModel, DNDetailModel dnDetailModel,MaterialModel materialModel) {
        //保存物料信息
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

        dnDetailModel.setITEM_NAME(materialModel==null?"":materialModel.getMAKTX());
        dnDetailModel.setITEM_NO(materialModel==null?"":materialModel.getMATNR());
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

        dnDetailModel.__setDaoSession(dnInfo.getDaoSession());
        if(dnDetailModel.getSERIALS()==null)
            dnDetailModel.setSERIALS(new ArrayList<DNScanModel>());
        dnDetailModel.getSERIALS().add(0,dnScanModel);
        dnDetailModels.add(dnDetailModel);
        //更新DN数据

    }




    /**
     * 自建添加序列号
     * @param dnDetailModels
     * @param barCodeModel
     * @param index
     * @return
     */
    private int  AddSerialByMaterialNo(List<DNDetailModel> dnDetailModels, BarCodeModel barCodeModel, int index) {
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

        //更新物料扫码数量
        int qty = dnDetailModels.get(index).getDN_QTY() + 1;
        dnDetailModels.get(index).setDN_QTY(qty);
        dnDetailModels.get(index).setSCAN_QTY(qty);
        return -1;
    }




}
