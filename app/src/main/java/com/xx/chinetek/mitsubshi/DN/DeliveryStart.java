package com.xx.chinetek.mitsubshi.DN;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.adapter.DN.PartnerItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.model.ReturnMsgModel;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.CreateDnNo;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.GetPartner;
import com.xx.chinetek.method.SharePreferUtil;
import com.xx.chinetek.method.Upload.UploadNewCus;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.CustomModel;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNTypeModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.Date;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_UploadCus;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_UploadCus;

@ContentView(R.layout.activity_delivery_start)
public class DeliveryStart extends BaseActivity {

    Context context=DeliveryStart.this;

    @ViewInject(R.id.spin_sendType)
    Spinner spinsendType;
    @ViewInject(R.id.spin_Custom)
    Spinner spinCustom;
    @ViewInject(R.id.txt_contentName)
    TextView txtContentName;
    @ViewInject(R.id.edt_ContentText)
    EditText edtContentText;
    @ViewInject(R.id.lsv_Partner)
    ListView lsvPartner;

    PartnerItemAdapter partnerItemAdapter;

    DNTypeModel dnTypeModel;
    CustomModel customModel;//客户或代理商信息
    ArrayList<CustomModel> customModels;

    Boolean isFirstRun=true;

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_UploadCus:
                AnalysisUploadCusJson((String) msg.obj);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____"+ msg.obj);
                break;
        }
    }


    @Override
    protected void initViews() {
        super.initViews();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.outputscan),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        dnTypeModel= SharePreferUtil.ReadDNTypeShare(context);
        BindSpinner();
        BindData();
        InitForm();
        if(dnTypeModel!=null && dnTypeModel.getDNType()!=null && dnTypeModel.getDNCusType()!=null){
            spinsendType.setSelection(dnTypeModel.getDNType());
            spinCustom.setSelection(dnTypeModel.getDNCusType());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(dnTypeModel!=null && dnTypeModel.getDNType()!=null && dnTypeModel.getDNCusType()!=null&&dnTypeModel.getDNType()==5){
            spinsendType.setSelection(dnTypeModel.getDNType());
            spinCustom.setSelection(dnTypeModel.getDNCusType());

        }
    }

    @Event(R.id.btn_StartOutPut)
    private void btnStartOutPutClick(View view){
        start();

    }

    private void start() {
        try {
            if (dnTypeModel.getDNType() == 3) { //自建方式需要确认发货客户
                final String code = edtContentText.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    MessageBox.Show(context, getString(R.string.Msg_No_CusCode));
                    CommonUtil.setEditFocus(edtContentText);
                    return;
                }
                //新增客户
                if (partnerItemAdapter.getCount() == 0) {
                    UploadNewCus.AddNewCusToMaps(code, "Z3", mHandler);
//                    new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.Msg_New_Custom))// 设置对话框标题
//                            .setIcon(android.R.drawable.ic_dialog_info)// 设置对话框图
//                            .setMessage(code)
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//
//                                }
//                            })
//                            .setNegativeButton("取消", null)
//                            .show();
                    CommonUtil.setEditFocus(edtContentText);
                    return;
                }

                if (customModel == null && partnerItemAdapter.getCount() == 1) {
                    customModel = (CustomModel) partnerItemAdapter.getItem(0);
                }
//            if (customModel == null) {
//                MessageBox.Show(context, getString(R.string.Msg_NoSelect_CusCode));
//                CommonUtil.setEditFocus(edtContentText);
//                return;
//            }
            }
            dnTypeModel.setCustomModel(customModel);
        }catch (Exception ex){
            MessageBox.Show(context,ex.getMessage());
        }
        if(customModel!=null)
            dnTypeModel.setDNCusType(customModel.getPARTNER_FUNCTION().equals("Z3")?1:0);
        CommonUtil.setEditFocus(edtContentText);
        SharePreferUtil.SetDNTypeShare(context,dnTypeModel);
        ParamaterModel.DnTypeModel=dnTypeModel;
        Intent intent=new Intent();
        Class jumpClass=null;
        DNModel dnModel=new DNModel();
        switch (dnTypeModel.getDNType()){
            case 3:
                CreateDnNo.GetDnNo(context,dnModel);
                dnModel.setDN_QTY(0);
                dnModel.setOPER_DATE(new Date());
               // dnModel.setDN_SOURCE(3);
                dnModel.setCUSTOM_NO(customModel.getCUSTOMER());
                dnModel.setCUSTOM_NAME(customModel.getNAME());
                jumpClass=DeliveryScan.class;
                break;
            case 5:
                jumpClass=QRScan.class;
                break;
            default:
                jumpClass=DeliveryList.class;
                break;
        }
        intent.setClass(context,jumpClass);
        Bundle bundle=new Bundle();
        bundle.putParcelable("DNModel",dnModel);
        intent.putExtras(bundle);
        startActivityLeft(intent);
    }

    @Event(value = R.id.spin_sendType,type = AdapterView.OnItemSelectedListener.class)
    private void sendTypeonItemSelected(AdapterView<?> adapterView, View view, int position,long id){
            dnTypeModel.setDNType(position);
        CommonUtil.setEditFocus(edtContentText);
    }

    private int chooseposition=-1;
    @Event(value = R.id.spin_Custom,type = AdapterView.OnItemSelectedListener.class)
    private void spinCustomonItemSelected(AdapterView<?> adapterView, View view, int position,long id) {
        chooseposition=position;
        txtContentName.setText(R.string.custom);
//         dnTypeModel.setDNCusType(position);
        if(!isFirstRun) {
            edtContentText.setText("");
            if(dnTypeModel==null) dnTypeModel=new DNTypeModel();
            customModel =null;
            BindData();
        }else isFirstRun=false;
        CommonUtil.setEditFocus(edtContentText);
    }


    @Event(value = R.id.lsv_Partner,type =  AdapterView.OnItemClickListener.class)
    private void lsvPartneronItemClick(AdapterView<?> parent, View view, int position, long id) {
        customModel =(CustomModel) partnerItemAdapter.getItem(position);
        if(customModel !=null) {
            edtContentText.setText(customModel.getCUSTOMER());
            CommonUtil.setEditFocus(edtContentText);
        }
    }

    /**
     * 文本变化事件
     */
    TextWatcher CustomTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!edtContentText.getText().toString().equals(""))
                partnerItemAdapter.getFilter().filter(edtContentText.getText().toString());
            else{
                InitListview();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };


    void BindSpinner(){
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.sendTypeList,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinsendType.setAdapter(adapter);
        spinsendType.setPrompt(getString(R.string.choiceSendType));
        adapter = ArrayAdapter.createFromResource(
                this, R.array.sendCustomListy,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCustom.setAdapter(adapter);
        spinCustom.setPrompt(getString(R.string.choiceSendCusttom));

    }

    void BindData(){
        try {
//            customModels = GetPartner.GetPartners(dnTypeModel == null ? 0 : dnTypeModel.getDNCusType());
            customModels = GetPartner.GetPartnersbyposition(chooseposition == -1 ? 0 : chooseposition);
            InitListview();
        }catch (Exception ex){
            MessageBox.Show(context,ex.getMessage());
        }
    }

    private void InitListview() {
        partnerItemAdapter=new PartnerItemAdapter(context, customModels);
        lsvPartner.setAdapter(partnerItemAdapter);
    }

    void InitForm(){
        edtContentText.addTextChangedListener(CustomTextWatcher);
        if(dnTypeModel==null) dnTypeModel=new DNTypeModel();
        customModel =dnTypeModel.getCustomModel();
        if(customModel !=null)
            edtContentText.setText(customModel.getCUSTOMER());
        CommonUtil.setEditFocus(edtContentText);
    }


    /**
     * 获取新增客户
     * @param result
     */
    void AnalysisUploadCusJson(String result){
        LogUtil.WriteLog(DeliveryStart.class,TAG_UploadCus,result);
        try {
            ReturnMsgModel<CustomModel> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<CustomModel>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                customModel = returnMsgModel.getModelJson();
                //插入数据
                ArrayList<CustomModel> customModels=new ArrayList<>();
                customModels.add(customModel);
                DbBaseInfo.getInstance().InsertCustomDB(customModels);
                BindData();
                edtContentText.setText(customModel.getCUSTOMER());
                start();
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
        }catch (Exception ex) {
            MessageBox.Show(context,ex.getMessage());
        }
    }

}
