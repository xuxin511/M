package com.xx.chinetek.mitsubshi.DN;

import android.content.Context;
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
    DNModel dnModel;
    DbDnInfo dnInfo;

    @Override
    protected void initViews() {
       super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.deliveryScan),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        dnInfo=DbDnInfo.getInstance();
        txtDnNo.setText(getIntent().getStringExtra("DNNo"));
        dnModel=getIntent().getParcelableExtra("DNModel");
        dnModel.__setDaoSession(dnInfo.getDaoSession());
        GetDeliveryOrderScanList();
        if (dnModel.getDetailModels() == null)
            dnModel.setDetailModels(new ArrayList<DNDetailModel>());

        dnModel.getDetailModels().addAll(dnDetailModels);
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
                        if (ParamaterModel.DnTypeModel.getDNType() == 3) { //自建
                            return CreateNewDN(barCodeModels);
                        } else {
                            return ScanBarccode(barCodeModels);
                        }
                    }
                } catch (Exception ex) {
                    MessageBox.Show(context, ex.getMessage());
                }
                return true;
            }
        }
        return false;
    }



    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemClickListener.class)
    private void lsvDeliveryScanonItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(context,BarcodeDetail.class);
        startActivityLeft(intent);
    }

    void GetDeliveryOrderScanList(){
        dnDetailModels= DbDnInfo.getInstance().GetDNDetailByDNNo(txtDnNo.getText().toString());
        deliveryScanItemAdapter=new DeliveryScanItemAdapter(context, dnDetailModels);
        lsvDeliveryScan.setAdapter(deliveryScanItemAdapter);
        edtBarcode.setText("");
        CommonUtil.setEditFocus(edtBarcode);
    }


    /**
     * 自建出库单
     * @param barCodeModels
     * @return
     * @throws Exception
     */
    private boolean CreateNewDN(ArrayList<BarCodeModel> barCodeModels) throws Exception {
        //保存扫描数据

        int isErrorStatus=-1;//0:物料已扫描  1：数量已超出
        dnModel.resetDetailModels();
        List<DNDetailModel> dnDetailModels = dnModel.getDetailModels();
        for (BarCodeModel barCodeModel : barCodeModels) {
            DNDetailModel dnDetailModel = new DNDetailModel();
            //判断是否存在物料
            int index = getIndex(dnDetailModels, barCodeModel, dnDetailModel);
            if (index != -1) {
                isErrorStatus=AddSerialByMaterialNo(dnDetailModels, barCodeModel, index);
                if(isErrorStatus!=-1) break;
            } else {
                NewSerialByMaterialNo(dnDetailModels, barCodeModel, dnDetailModel);
            }
            dnModel.setDN_QTY(dnModel.getDN_QTY() + 1);
        }
        if (ShowErrMag(isErrorStatus)) return true;
        dnModel.setDN_STATUS(1);
        if(ParamaterModel.DnTypeModel.getCustomModel().getType().equals("Z2")){
            dnModel.setLEVEL_2_AGENT_NO(ParamaterModel.DnTypeModel.getCustomModel().getPartnerID());
            dnModel.setLEVEL_2_AGENT_NAME(ParamaterModel.DnTypeModel.getCustomModel().getPartnerName());
        }else if(ParamaterModel.DnTypeModel.getCustomModel().getType().equals("Z3")){
            dnModel.setCUSTOM_NO(ParamaterModel.DnTypeModel.getCustomModel().getPartnerID());
            dnModel.setCUSTOM_NAME(ParamaterModel.DnTypeModel.getCustomModel().getPartnerName());
        }
        dnModel.setUPDATE_USER(ParamaterModel.Operater);
        dnModel.setUPDATE_DATE(new Date());
        dnModel.setDN_SOURCE(ParamaterModel.DnTypeModel.getDNType().toString());
        dnModel.getDetailModels().addAll(dnDetailModels);
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
     * 条码扫描（非自建方式）
     * @param barCodeModels
     * @return
     * @throws Exception
     */
    private boolean ScanBarccode(ArrayList<BarCodeModel> barCodeModels) throws Exception {
        dnModel.resetDetailModels();
        List<DNDetailModel> dnDetailModels = dnModel.getDetailModels();
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
     * 扫描数量和重复检查
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
            List<DNScanModel> dnScanModels = dnDetailModels.get(index).getDnScanModels();
            DNScanModel dnScanModel = new DNScanModel();
            dnScanModel.setITEM_SERIAL_NO(barCodeModel.getSerial_Number());
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
            dnScanModel.setUPDATE_DATE(new Date());
            dnScanModel.setUPDATE_USER(ParamaterModel.Operater);
            dnScanModel.setMAT_TYPE(0);
            dnDetailModels.get(index).getDnScanModels().add(dnScanModel);
        }
        return isErrorStatus;
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

    /**
     * 显示错误信息
     * @param isErrorStatus
     * @return
     */
    private boolean ShowErrMag(int isErrorStatus) {
        edtBarcode.setText("");
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
     * 自建新增序列号
     * @param dnDetailModels
     * @param barCodeModel
     * @param dnDetailModel
     */
    private void NewSerialByMaterialNo(List<DNDetailModel> dnDetailModels, BarCodeModel barCodeModel, DNDetailModel dnDetailModel) {
        //保存物料信息
        dnDetailModel.setLINE_NO(dnModel.getDetailModels().size()+1);//行号
        dnDetailModel.setDN_QTY(1);
        dnDetailModel.setSCAN_QTY(1);
        dnDetailModel.setDETAIL_STATUS("1");

        //保存序列号
        DNScanModel dnScanModel=new DNScanModel();
        dnScanModel.setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
        dnScanModel.setLINE_NO(dnDetailModel.getLINE_NO());
        dnScanModel.setITEM_SERIAL_NO(barCodeModel.getSerial_Number());
        dnScanModel.setPACKING_DATE(barCodeModel.getPacking_Date());
        dnScanModel.setREGION(barCodeModel.getPlace_Code());
        dnScanModel.setCOUNTRY(barCodeModel.getCountry_Code());
        dnScanModel.setGOLFA_CODE(barCodeModel.getGolfa_Code());
        dnScanModel.setITEM_STATUS("AC");
        dnDetailModel.__setDaoSession(dnInfo.getDaoSession());
        if(dnDetailModel.getDnScanModels()==null)
            dnDetailModel.setDnScanModels(new ArrayList<DNScanModel>());
        dnDetailModel.getDnScanModels().add(dnScanModel);
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
        List<DNScanModel> dnScanModels=dnDetailModels.get(index).getDnScanModels();
        DNScanModel dnScanModel=new DNScanModel();
        dnScanModel.setITEM_SERIAL_NO(barCodeModel.getSerial_Number());
        int barcodeIndex=dnScanModels.indexOf(dnScanModel);
        if(barcodeIndex!=-1) return 0;

        //保存序列号
        dnScanModel.setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
        dnScanModel.setLINE_NO( dnDetailModels.get(index).getLINE_NO());
        dnScanModel.setPACKING_DATE(barCodeModel.getPacking_Date());
        dnScanModel.setREGION(barCodeModel.getPlace_Code());
        dnScanModel.setCOUNTRY(barCodeModel.getCountry_Code());
        dnScanModel.setGOLFA_CODE(barCodeModel.getGolfa_Code());
        dnScanModel.setITEM_STATUS("AC");
        dnDetailModels.get(index).getDnScanModels().add(dnScanModel);

        //更新物料扫码数量
        int qty = dnDetailModels.get(index).getDN_QTY() + 1;
        dnDetailModels.get(index).setDN_QTY(qty);
        dnDetailModels.get(index).setSCAN_QTY(qty);
        return -1;
    }

}
