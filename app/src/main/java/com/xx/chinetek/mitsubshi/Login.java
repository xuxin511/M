package com.xx.chinetek.mitsubshi;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.model.Paramater;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.chineteklib.util.function.FileUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.ModelInfo;
import com.xx.chinetek.method.SharePreferUtil;
import com.xx.chinetek.model.Base.ParamaterModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_login)
public class Login extends BaseActivity {


    Context context = Login.this;
    @ViewInject(R.id.txt_Partner)
    TextView txtPartner;
    @ViewInject(R.id.txt_SerialNo)
    TextView txtSerialNo;
    @ViewInject(R.id.edt_Operater)
    EditText  edtOperater;

    @Override
    protected void initViews() {
        super.initViews();
        Paramater.IsShowTitleBar = false;
        x.view().inject(this);
        CreateDirectory();
    }

    @Override
    protected void initData() {
        super.initData();
        ModelInfo.GetSysINfo();
        SharePreferUtil.ReadShare(context);
        SharePreferUtil.ReadUserShare(context);
        edtOperater.setText(ParamaterModel.Operater);
        if(ParamaterModel.SerialNo!=null) {
            txtSerialNo.setText(ParamaterModel.SerialNo);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtPartner.setText(ParamaterModel.PartenerID);
    }

    @Event(R.id.btn_Login)
    private void btnLoginClick(View view) {
        unregisterReceiver(ModelInfo.myReceiver); //取消MDM注册广播
        if (!(ParamaterModel.Model.toUpperCase().equals("TC75") || ParamaterModel.Model.toUpperCase().equals("A15_A5"))) {
            MessageBox.Show(context,getString(R.string.Msg_NotSupportModel));
            return;
        }

        if (TextUtils.isEmpty(ParamaterModel.PartenerID)) {
            MessageBox.Show(context, getString(R.string.Msg_No_Partner));
            return;
        }

        ParamaterModel.Operater = edtOperater.getText().toString().trim();
        if (!TextUtils.isEmpty(ParamaterModel.Operater)) {
            SharePreferUtil.SetUserShare(context);
        }


        CommonUtil.setEditFocus(edtOperater);
        LogUtil.WriteLog(Login.class,"btnLoginClick",ParamaterModel.Operater);
        Intent intent = new Intent(context, MainActivity.class);
        startActivityLeft(intent);

    }


    @Event(R.id.btnSetting)
    private void btnSettingClick(View view) {
        Intent intent=new Intent(context,Setting.class);
        startActivityLeft(intent);
    }


    void CreateDirectory(){
        List<String> Paths=new ArrayList<>();
        Paths.add(ParamaterModel.Directory);
        Paths.add(ParamaterModel.DBDirectory);
        Paths.add(ParamaterModel.DownDirectory);
        Paths.add(ParamaterModel.UpDirectory);
        FileUtil.CreateFile(Paths);

    }


}