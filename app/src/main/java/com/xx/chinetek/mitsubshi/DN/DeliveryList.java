package com.xx.chinetek.mitsubshi.DN;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.adapter.DN.DeliveryListItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.model.ReturnMsgModelList;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.LoadingDialog;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.greendao.DNDetailModelDao;
import com.xx.chinetek.method.CreateDnNo;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.DB.DbLogInfo;
import com.xx.chinetek.method.Delscan.DelAgentScan;
import com.xx.chinetek.method.Log.DBLogUtil;
import com.xx.chinetek.method.PlaySound;
import com.xx.chinetek.method.Sync.SyncDN;
import com.xx.chinetek.mitsubshi.BaseIntentActivity;
import com.xx.chinetek.mitsubshi.OrderFilter;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.BarCodeModel;
import com.xx.chinetek.model.Base.DNStatusEnum;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;
import com.xx.chinetek.model.DN.LogModel;
import com.xx.chinetek.model.QueryModel;
import com.xx.chinetek.model.Third.ReAdjust;
import com.xx.chinetek.model.Third.ThirdDNDetailModel;
import com.xx.chinetek.model.Third.ThirdDNModel;
import com.xx.chinetek.model.Third.ThirdReturnModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.xx.chinetek.method.Delscan.Delscan.DelDNmodel;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_DeleteQRScan;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_GetVoucherDetail;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_GetVoucherHead;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncDn;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncDnDetail;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncFTP;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncMail;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncUSB;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_GetVoucherDetail;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SGetVoucherHead;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncDn;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncDnDetail;

@ContentView(R.layout.activity_delivery_list)
public class DeliveryList extends BaseIntentActivity implements SwipeRefreshLayout.OnRefreshListener {

    Context context = DeliveryList.this;
    LoadingDialog loadingDialog;


    @ViewInject(R.id.edt_DeleveryNoFuilter)
    EditText edtDeleveryNoFuilter;
    @ViewInject(R.id.Lsv_DeliveryList)
    ListView LsvDeliveryList;
    @ViewInject(R.id.mSwipeLayout)
    SwipeRefreshLayout mSwipeLayout;
    @ViewInject(R.id.fab)
    FloatingActionButton fab;
    @ViewInject(R.id.fabCancel)
    FloatingActionButton fabCancel;

    QueryModel queryModel;
    DeliveryListItemAdapter deliveryListItemAdapter;
    ArrayList<DNModel> DNModels; //所有未完成出库单
    LoadingDialog dialog;
    String dnDate;

    @Override
    public void onHandleMessage(Message msg) {
        try {
            switch (msg.what) {
                case RESULT_SyncDn:
                    AnalysisSyncMAPSDNJson((String) msg.obj);
                    break;
                case RESULT_GetVoucherHead:
                    AnalysisSyncInterFaceJson((String) msg.obj);
                    break;
                case RESULT_SyncDnDetail:
                    AnalysisSyncMAPSDNDetailJson((String) msg.obj);
                    break;
                case RESULT_GetVoucherDetail:
                    AnalysisetVoucherDetailJson((String) msg.obj);
                    break;
                case RESULT_SyncUSB:
                case RESULT_SyncMail:
                case RESULT_SyncFTP:
                    if ((int) msg.obj > 0) {
                        Intent intent = new Intent(context, FTPsync.class);
                        startActivityLeft(intent);
                    }
                    break;
                case TAG_SCAN:
                    CheckDNByDnNo((String) msg.obj);
                    break;
                case RESULT_DeleteQRScan:
                    if(loadingDialog!=null)
                        loadingDialog.dismiss();
                    ThirdReturnModel<ReAdjust> returnMsgModel = GsonUtil.getGsonUtil().fromJson((String) msg.obj, new TypeToken<ThirdReturnModel<ReAdjust>>() {
                    }.getType());
                    if (returnMsgModel.getSuccess() != 1) {
                        DbLogInfo.getInstance().InsertLog(new LogModel("单据列表-代理商条码删除异常",returnMsgModel.getMessage(),dnModel.getCUS_DN_NO()));
                        MessageBox.Show(context, returnMsgModel.getMessage());
                    }else {
//                        int delqty=0;
//                        for (DNDetailModel dndetail : dnModel.getDETAILS()) {
//                            delqty+=dndetail.getSERIALS().size();
//                        }
//                        if(delqty==returnMsgModel.getReAdjust().size()) {
                            DelDNmodel(context, dnModel);
                        //}
                        DNModels = DbDnInfo.getInstance().GetLoaclDN(queryModel);
                        BindListView();
                    }
                    break;
                case NetworkError.NET_ERROR_CUSTOM:
                    if(loadingDialog!=null)
                        loadingDialog.dismiss();
                    MessageBox.Show(context,"获取请求失败_____" + msg.obj);
                    break;
            }
            if(dialog!=null)
                dialog.dismiss();
            DNModels= DbDnInfo.getInstance().GetLoaclDN(queryModel);
            BindListView();
        } catch (Exception ex) {
            MessageBox.Show(context,ex.getMessage());
            if(dialog!=null)
            dialog.dismiss();
        }
    }

    /**
     * MAPS同步出库单
     * @param result
     */
    public void AnalysisSyncMAPSDNJson(String result) throws Exception {
        LogUtil.WriteLog(DeliveryList.class, TAG_SyncDn, result);
        ReturnMsgModelList<DNModel> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<DNModel>>() {
        }.getType());
        if (returnMsgModel.getHeaderStatus().equals("S")) {
            ArrayList<DNModel> dnModels = returnMsgModel.getModelJson();
            ArrayList<DNModel> SelectdnModels=new ArrayList<>();
            int size=dnModels.size();
            for(int i=0;i<size;i++){
                if(DbDnInfo.getInstance().CheckDNInDB(dnModels.get(i).getAGENT_DN_NO()))
                    SelectdnModels.add(dnModels.get(i));
            }
            if(SelectdnModels.size()!=0) {
                Intent intent = new Intent(context, DNsync.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("DNModels", SelectdnModels);
                intent.putExtras(bundle);
                startActivityForResult(intent,999);
            }
        } else {
            MessageBox.Show(context, returnMsgModel.getMessage());
        }
    }


    public void AnalysisSyncInterFaceJson(String result) throws Exception {
        LogUtil.WriteLog(DeliveryList.class, TAG_SGetVoucherHead, result);
        if(result.equals("[]")){
            MessageBox.Show(context,getString(R.string.Msg_No_DNno));
            return;
        }
        List<ThirdDNModel> dnModels = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<List<ThirdDNModel>>() {
        }.getType());

            ArrayList<DNModel> SelectdnModels=new ArrayList<>();
            int size=dnModels.size();
            for(int i=0;i<size;i++) {
                if (DbDnInfo.getInstance().CheckAgentDNInDB(dnModels.get(i).getVoucherNo())) {
                    DNModel dnModel = new DNModel();
                    CreateDnNo.GetDnNo(context, dnModel);
                    dnModel.setCUS_DN_NO(dnModels.get(i).getVoucherNo());
                    dnModel.setCUSTOM_NO(dnModels.get(i).getCustomNo());
                    dnModel.setCUSTOM_NAME(dnModels.get(i).getCustomName());
                    dnModel.setDN_QTY(dnModels.get(i).getOrderNum() - dnModels.get(i).getScanNum());
                    dnModel.setDN_DATE(CommonUtil.dateStrConvertDate(dnModels.get(i).getCreateTime(), null));
                    dnModel.setDN_SOURCE(ParamaterModel.DnTypeModel.getDNType());
                    dnModel.setSTATUS(DNStatusEnum.ready);
                    dnModel.setDN_STATUS("AC");
                    dnModel.setLEVEL_2_AGENT_NO(ParamaterModel.PartenerID);
                    dnModel.setLEVEL_2_AGENT_NAME(ParamaterModel.PartenerName);
                    dnModel.setOPER_DATE(CommonUtil.dateStrConvertDate(dnModels.get(i).getModifyTime(), null));
                    SelectdnModels.add(dnModel);
                }
            }


            if(SelectdnModels.size()!=0) {
                if(SelectdnModels.size()==1){
                    DbDnInfo.getInstance().InsertDNDB(SelectdnModels);
                    dnModel=SelectdnModels.get(0);
                    SyncDN.SyncInterFaceDetail(SelectdnModels.get(0).getCUS_DN_NO(),mHandler);
                }else {
                    Intent intent = new Intent(context, DNsync.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("DNModels", SelectdnModels);
                    bundle.putString("DNDate", dnDate);
                    intent.putExtras(bundle);
                    startActivityForResult(intent,999);
                }
            }

    }


    private void AnalysisetVoucherDetailJson(String result) throws Exception {
        LogUtil.WriteLog(DeliveryList.class, TAG_GetVoucherDetail, result);
        if(result.equals("[]")){
            MessageBox.Show(context,getString(R.string.Msg_No_DNDetail));
            return;
        }
        List<ThirdDNDetailModel> dnDetailModels = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<List<ThirdDNDetailModel>>() {
        }.getType());
        int size=dnDetailModels.size();
        //插入数据
        ArrayList<DNDetailModel> insertDetailModels=new ArrayList<>();
        for(int i=0;i<size;i++) {
            DNDetailModel dnDetailModel=new DNDetailModel();
            dnDetailModel.setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
            dnDetailModel.setLINE_NO(dnDetailModels.get(i).getRowNum());
            List<MaterialModel> materialModels = DbBaseInfo.getInstance().GetItems("", dnDetailModels.get(i).getType(), "");
            if (materialModels!=null && materialModels.size() ==0)
                dnDetailModel.setITEM_NO(dnDetailModels.get(i).getTypeNo());
            else {
                dnDetailModel.setITEM_NO(materialModels.get(0).getMATNR());
                dnDetailModel.setGOLFA_CODE(materialModels.get(0).getBISMT());
            }
            dnDetailModel.setITEM_NAME(dnDetailModels.get(i).getType());
            dnDetailModel.setDN_QTY(dnDetailModels.get(i).getOrderNum()-dnDetailModels.get(i).getScanNum());
            dnDetailModel.setSCAN_QTY(dnDetailModels.get(i).getScanNum());
            dnDetailModel.setDETAIL_STATUS("AC");
            dnDetailModel.setSTATUS(0);
            dnDetailModel.setOPER_DATE(new Date());
            dnDetailModel.setFlag(materialModels!=null && materialModels.size() > 1?1:0);
            insertDetailModels.add(dnDetailModel);
        }

        DbDnInfo.getInstance().InsertDNDetailDB(insertDetailModels);
        DbDnInfo.getInstance().ChangeDNStatusByDnNo(dnModel.getAGENT_DN_NO(), DNStatusEnum.download);
        dnModel.setSTATUS(DNStatusEnum.download);
        StartScan(dnModel);

    }

    private void AnalysisSyncMAPSDNDetailJson(String result) throws Exception {
        LogUtil.WriteLog(DeliveryList.class, TAG_SyncDnDetail, result);
        ReturnMsgModelList<DNDetailModel> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<DNDetailModel>>() {
        }.getType());
        if (returnMsgModel.getHeaderStatus().equals("S")) {
            ArrayList<DNDetailModel> dnDetailModels = returnMsgModel.getModelJson();
            int size=dnDetailModels.size();
            if(size!=0) {
                for (int i = 0; i < size; i++) {
                    int scanQTY = DbDnInfo.getInstance().GetScanQtyInDNScanModel(dnDetailModels.get(i).getAGENT_DN_NO()
                            , dnDetailModels.get(i).getGOLFA_CODE(), dnDetailModels.get(i).getLINE_NO());
                    dnDetailModels.get(i).setSCAN_QTY(scanQTY);
                }
                //插入数据

                DbDnInfo.getInstance().InsertDNDetailDB(dnDetailModels);
                DbDnInfo.getInstance().ChangeDNStatusByDnNo(dnDetailModels.get(0).getAGENT_DN_NO(), DNStatusEnum.download);
                dnModel.setSTATUS(DNStatusEnum.download);
                StartScan(dnModel);
            }
            else{
                MessageBox.Show(context, getString(R.string.Msg_No_DNDetail));
            }
        } else {
            MessageBox.Show(context, returnMsgModel.getMessage());
        }
    }


    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle = new ToolBarTitle(getResources().getString(R.string.outputlist), true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        queryModel=null;
        dnDate=getIntent().getStringExtra("DNDate");
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, OrderFilter.class),1001);
            }
        });
        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryModel=null;
                fabCancel.setVisibility(View.GONE);
                DNModels= DbDnInfo.getInstance().GetLoaclDN(queryModel);
                BindListView();
            }
        });

        if(ParamaterModel.DnTypeModel.getDNType()==5){
            String datestr=dnDate.substring(0,4)+"-"+dnDate.substring(4,6)+"-"+dnDate.substring(6,8);
//            SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
//            Calendar c = Calendar.getInstance();
//            String datetime = sf.format(c.getTime());
            edtDeleveryNoFuilter.setText("SS-" + datestr + "-");
        }

        edtDeleveryNoFuilter.addTextChangedListener(DeleveryNoTextWatcher);
        mSwipeLayout.setOnRefreshListener(this); //下拉刷新



    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1001 && resultCode==1){
            queryModel=data.getParcelableExtra("queryModel");
            if(fabCancel!=null)
                fabCancel.setVisibility(View.VISIBLE);
        }
        if(requestCode==999 && resultCode==2){
            DNModel tempdnmodel=data.getParcelableExtra("DNModel");
                if(tempdnmodel.getDN_SOURCE()==0){
                    SyncDN.SyncMAPSDetail(tempdnmodel.getAGENT_DN_NO(),mHandler);
                }else if(tempdnmodel.getDN_SOURCE()==5){
                    if(tempdnmodel.getDETAILS()!=null && tempdnmodel.getDETAILS().size()!=0)
                        StartScan(tempdnmodel);
                    else {
                        dnModel=tempdnmodel;
                        SyncDN.SyncInterFaceDetail(tempdnmodel.getCUS_DN_NO(), mHandler);
                    }
                }else {
                    StartScan(tempdnmodel);
                }

        }
    }

    @Override
    public void onRefresh() {
        queryModel=null;
        ImportDelivery();
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DNModels= DbDnInfo.getInstance().GetLoaclDN(queryModel);
        BindListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_title, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId()==R.id.action_QR){
//            Intent intent=new Intent(context,QRScan.class);
//            ParamaterModel.DnTypeModel.setDNType(5);
//            startActivityLeft(intent);
//        }
        if(item.getItemId()==R.id.action_sync){
            ImportDelivery();
        }
        return super.onOptionsItemSelected(item);
    }

    DNModel  dnModel;
    @Event(value = R.id.Lsv_DeliveryList,type = AdapterView.OnItemClickListener.class)
    private void LsvDeliveryListonItemClick(AdapterView<?> parent, View view, int position, long id) {
         dnModel=(DNModel)deliveryListItemAdapter.getItem(position);
        ParamaterModel.DnTypeModel.setDNType(dnModel.getDN_SOURCE());
        if(dnModel.getDN_SOURCE()==0){
            SyncDN.SyncMAPSDetail(dnModel.getAGENT_DN_NO(),mHandler);
        }else if(dnModel.getDN_SOURCE()==5){
            if(dnModel.getDETAILS()!=null && dnModel.getDETAILS().size()!=0)
                StartScan(dnModel);
            else
                SyncDN.SyncInterFaceDetail(dnModel.getCUS_DN_NO(),mHandler);
        }else {
            StartScan(dnModel);
        }
    }


    @Event(value = R.id.Lsv_DeliveryList,type = AdapterView.OnItemLongClickListener.class)
    private boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i < 0) {
            MessageBox.Show(context, "请先选择操作的行！");
            return false;
        }
        final DNModel Model = (DNModel) deliveryListItemAdapter.getItem(i);
        new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("确认删除扫描记录？\n")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 自动生成的方法
                        if(( Model.getDN_SOURCE()==5 || (ParamaterModel.IsAgentSoft && Model.getDN_SOURCE()==3) )&& Model.getSTATUS()<2) {

                            List<DNDetailModel> dnDetailModellist = Model.getDETAILS();
                            List<DNScanModel> dnScanModels = new ArrayList<>();
                            for (DNDetailModel dndetail : dnDetailModellist) {
                                dnScanModels.addAll(dndetail.getSERIALS());
                            }
                            if(dnScanModels.size()==0){
                                DelDNmodel(context,Model);
                                DNModels= DbDnInfo.getInstance().GetLoaclDN(queryModel);
                                BindListView();
                            }else {
                                BaseApplication.DialogShowText = "删除条码";
                                loadingDialog = new LoadingDialog(context);
                                loadingDialog.show();
                                dnModel=Model;
                                DelAgentScan.DelScan(mHandler, Model.getCUS_DN_NO(), dnScanModels);
                            }
                        }else{
                            DelDNmodel(context,Model);
                            DNModels= DbDnInfo.getInstance().GetLoaclDN(queryModel);
                            BindListView();
                        }


                    }
                }).setNegativeButton("取消", null).show();
        return true;
    }


    private void StartScan(DNModel dnModel) {
        Intent intent=new Intent(context,DeliveryScan.class);
        Bundle bundle=new Bundle();
        bundle.putParcelable("DNModel",dnModel);
        intent.putExtras(bundle);
        startActivityLeft(intent);
    }

    void CheckDNByDnNo(String DNNo){
        if(DNModels!=null) {
            DNModel tdnModel=new DNModel();
            tdnModel.setAGENT_DN_NO(DNNo);
            tdnModel.setCUS_DN_NO(DNNo);
            int index=DNModels.indexOf(tdnModel);
            if(index!=-1) {
                tdnModel = DNModels.get(index);
                if(tdnModel.getDN_SOURCE()==5) {
                    if (tdnModel.getDETAILS() != null && tdnModel.getDETAILS().size() != 0) {
                        ParamaterModel.DnTypeModel.setDNType(tdnModel.getDN_SOURCE());
                        StartScan(tdnModel);
                    }
                    else {
                        dnModel= DNModels.get(index);
                        SyncDN.SyncInterFaceDetail(dnModel.getCUS_DN_NO(), mHandler);
                    }
                }else {
                    ParamaterModel.DnTypeModel.setDNType(dnModel.getDN_SOURCE());
                    StartScan(dnModel);
                }
            }else{
                if(ParamaterModel.DnTypeModel.getDNType()==5){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BaseApplication.DialogShowText = getString(R.string.Dia_SyncInterface);
                            dialog = new LoadingDialog(DeliveryList.this);
                            dialog.show();
                        }
                    });
                    SyncDN.SyncInterface(DNNo,mHandler);
                }else{
                    MessageBox.Show(context,getString(R.string.Msg_No_DNno));
                    edtDeleveryNoFuilter.setText(DNNo);
                    CommonUtil.setEditFocus(edtDeleveryNoFuilter);
                }
            }
        }
    }
    @Event(value = R.id.edt_DeleveryNoFuilter, type = View.OnKeyListener.class)
    private boolean edtDeleveryNoFuilterOnKey(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
        {
            String dnno=edtDeleveryNoFuilter.getText().toString();
            CheckDNByDnNo(dnno);
            return true;
        }
        return false;
    }

    /**
     * 文本变化事件
     */
    TextWatcher DeleveryNoTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String filterContent=edtDeleveryNoFuilter.getText().toString();
            if(!filterContent.equals(""))
                deliveryListItemAdapter.getFilter().filter(filterContent);
            else{
                BindListView();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    void BindListView(){
        if(DNModels!=null) {
            deliveryListItemAdapter = new DeliveryListItemAdapter(context, DNModels);
            LsvDeliveryList.setAdapter(deliveryListItemAdapter);
        }
    }

    /**
     * 同步并导入DN单据
     * @return
     */
   private void ImportDelivery(){
       try {
           File[] DNfiles=new File(ParamaterModel.DownDirectory).listFiles();
           for(int i=0;i<DNfiles.length;i++) {
               DNfiles[i].delete();
           }
           switch (ParamaterModel.DnTypeModel.getDNType()) {
               case 0://MAPS
                   BaseApplication.DialogShowText = getString(R.string.Dia_SyncDn);
                   dialog = new LoadingDialog(context);
                   dialog.show();
                   SyncDN.SyncMAPS(mHandler);
                   break;
               case 1://邮件
                   BaseApplication.DialogShowText = getString(R.string.Dia_SyncMail);
                   dialog = new LoadingDialog(context);
                   dialog.show();
                   SyncDN.SyncMail(mHandler);
                   break;
               case 2://FTP
                   BaseApplication.DialogShowText = getString(R.string.Dia_SyncFTP);
                   dialog = new LoadingDialog(context);
                   dialog.show();
                   SyncDN.SyncFtp(mHandler);
                   break;
//               case 4://USB
//                   BaseApplication.DialogShowText = getString(R.string.Dia_SyncUSB);
//                   dialog = new LoadingDialog(context);
//                   dialog.show();
//                   android.os.Message msg = mHandler.obtainMessage(RESULT_SyncUSB, 1);
//                   mHandler.sendMessage(msg);
//                   break;
               case 3:
                   DNModels = DbDnInfo.getInstance().GetLoaclDN(queryModel);
                   BindListView();
                   break;
               case 5:
                   BaseApplication.DialogShowText = getString(R.string.Dia_SyncInterface);
                   dialog = new LoadingDialog(context);
                   dialog.show();
                   SyncDN.SyncInterface(dnDate,mHandler);
                   break;
           }
       }catch (Exception ex){
           ToastUtil.show(ex.getMessage());
           LogUtil.WriteLog(DeliveryList.class,"DeliveryList", ex.toString());
       }

    }




}
