package com.xx.chinetek.mitsubshi.DN;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.xx.chinetek.adapter.DN.PartnerItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.GetPartner;
import com.xx.chinetek.method.SharePreferUtil;
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
    }

    @Event(R.id.btn_StartOutPut)
    private void btnStartOutPutClick(View view){
        String code=edtContentText.getText().toString().trim();
        if(TextUtils.isEmpty(code)){
            MessageBox.Show(context,getString(R.string.Msg_No_CusCode));
            return;
        }
        if(customModel ==null && partnerItemAdapter.getCount()==1){
            customModel =(CustomModel) partnerItemAdapter.getItem(0);
        }
        if(customModel ==null){
            MessageBox.Show(context,getString(R.string.Msg_NoSelect_CusCode));
            return;
        }

        dnTypeModel.setCustomModel(customModel);
        CommonUtil.setEditFocus(edtContentText);
        SharePreferUtil.SetDNTypeShare(context,dnTypeModel);
        ParamaterModel.DnTypeModel=dnTypeModel;
        Intent intent=new Intent();
        Class jumpClass=null;
        DNModel dnModel=new DNModel();
        switch (dnTypeModel.getDNType()){
            case 3:
                String Dnno="CS1233335";
                dnModel.setAGENT_DN_NO(Dnno);
                dnModel.setDN_QTY(0);
                intent.putExtra("DNNo",Dnno);
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

    @Event(value = R.id.spin_Custom,type = AdapterView.OnItemSelectedListener.class)
    private void spinCustomonItemSelected(AdapterView<?> adapterView, View view, int position,long id) {
        txtContentName.setText(getString(position == 0 ? R.string.supplier : R.string.custom));
         dnTypeModel.setDNCusType(position);
        if(!isFirstRun) {
            edtContentText.setText("");
            BindData();
        }else isFirstRun=false;
        CommonUtil.setEditFocus(edtContentText);
    }


    @Event(value = R.id.lsv_Partner,type =  AdapterView.OnItemClickListener.class)
    private void lsvPartneronItemClick(AdapterView<?> parent, View view, int position, long id) {
        customModel =(CustomModel) partnerItemAdapter.getItem(position);
        if(customModel !=null) {
            edtContentText.setText(customModel.getPartnerID());
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
                this, R.array.sendCustomList,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinCustom.setAdapter(adapter);
        spinCustom.setPrompt(getString(R.string.choiceSendCusttom));
        if(dnTypeModel!=null){
            spinsendType.setSelection(dnTypeModel.getDNType());
            spinCustom.setSelection(dnTypeModel.getDNCusType());

        }
    }

    void BindData(){
        try {
            customModels = GetPartner.GetPartners(dnTypeModel == null ? 0 : dnTypeModel.getDNCusType());
            InitListview();
        }catch (Exception ex){
            MessageBox.Show(context,ex.getMessage());
            LogUtil.WriteLog(DeliveryStart.class,"GetPartners",ex.getMessage());
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
            edtContentText.setText(customModel.getPartnerID());
        CommonUtil.setEditFocus(edtContentText);
    }



}
