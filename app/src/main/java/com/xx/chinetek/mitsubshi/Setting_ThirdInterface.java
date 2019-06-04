package com.xx.chinetek.mitsubshi;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SimpleAdapter;

import com.xx.chinetek.adapter.ToMailItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.model.Paramater;
import com.xx.chinetek.chineteklib.util.CompressUtil;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.model.Base.BarcodeRule;
import com.xx.chinetek.model.Base.CusBarcodeRule;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.ThirdInterfaceModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_setting__third_interface)
public class Setting_ThirdInterface  extends BaseActivity {

    ThirdInterfaceModel thirdInterfaceModel;
    @ViewInject(R.id.edt_InertfaceIPAdress)
    EditText edtInertfaceIPAdress;
    @ViewInject(R.id.edt_InertfacePort)
    EditText edtInertfacePort;
    @ViewInject(R.id.edt_InertfacePart)
    EditText edtInertfacePart;
    @ViewInject(R.id.edt_ThirdTimeOut)
    EditText edtThirdTimeOut;
    @ViewInject(R.id.edt_ThirdUsername)
    EditText edtThirdUsername;
    @ViewInject(R.id.edt_ThirdPassword)
    EditText edtThirdPassword;
    @ViewInject(R.id.ckIsAgentSoft)
    CheckBox ckIsAgentSoft;

    Context context=Setting_ThirdInterface.this;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.Setting_thirdInterface),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        ckIsAgentSoft.setChecked(ParamaterModel.IsAgentSoft);
        if(ParamaterModel.baseparaModel.getThirdInterfaceModel()==null){
            ParamaterModel.baseparaModel.setThirdInterfaceModel(new ThirdInterfaceModel());
        }
        thirdInterfaceModel= ParamaterModel.baseparaModel.getThirdInterfaceModel();
        initView();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseActivity();
            }
        });
    }

    private void initView() {
        edtInertfaceIPAdress.setText(thirdInterfaceModel.getInterfaceIP());
        edtInertfacePort.setText(thirdInterfaceModel.getPort()+"");
        edtInertfacePart.setText(thirdInterfaceModel.getPart());
        edtThirdTimeOut.setText(Paramater.SOCKET_THIRDTIMEOUT/1000+"");
        edtThirdUsername.setText(Paramater.Username);
        edtThirdPassword.setText(Paramater.Password);
    }

    @Event(R.id.layoutCusBarcode)
    private void layoutCusBarcodeClick(View view){
        ckIsAgentSoft.setChecked(!ckIsAgentSoft.isChecked());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting_mail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_Save) {
            ParamaterModel.IsAgentSoft=ckIsAgentSoft.isChecked();
            if( ParamaterModel.IsAgentSoft) {
                String port = edtInertfacePort.getText().toString().trim();
                if (!port.equals("") && !CommonUtil.isNumeric(port)) {
                    MessageBox.Show(context, getString(R.string.Msg_inputNumic));
                    return true;
                }
                thirdInterfaceModel.setInterfaceIP(edtInertfaceIPAdress.getText().toString().trim());
                thirdInterfaceModel.setPort(Integer.parseInt(port.equals("") ? "0" : port));
                thirdInterfaceModel.setPart(edtInertfacePart.getText().toString().trim());
                Paramater.Username=edtThirdUsername.getText().toString().trim();
                Paramater.Password=edtThirdPassword.getText().toString().trim();
                Paramater.SOCKET_THIRDTIMEOUT = Integer.parseInt(edtThirdTimeOut.getText().toString().trim()) * 1000;
            }
            CloseActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    private void CloseActivity() {
        Intent mIntent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putParcelable("thirdInterfaceModel",thirdInterfaceModel);
        mIntent.putExtras(bundle);
        setResult(1, mIntent);
        closeActiviry();
    }
}
