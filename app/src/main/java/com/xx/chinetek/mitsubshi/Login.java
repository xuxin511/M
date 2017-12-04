package com.xx.chinetek.mitsubshi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.model.Paramater;
import com.xx.chinetek.chineteklib.model.ReturnMsgModel;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.chineteklib.util.function.FileUtil;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.DB.DbManager;
import com.xx.chinetek.method.ModelInfo;
import com.xx.chinetek.method.SharePreferUtil;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.UserInfoModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_Login;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_Login;

@ContentView(R.layout.activity_login)
public class Login extends BaseActivity {

    Context context = Login.this;
    @ViewInject(R.id.txt_Partner)
    TextView txtPartner;
    @ViewInject(R.id.txt_SerialNo)
    TextView txtSerialNo;
    @ViewInject(R.id.txtVer)
    TextView txtVer;
    @ViewInject(R.id.edt_Operater)
    EditText  edtOperater;


    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_Login:
                AnalysisLoginJson((String) msg.obj);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____"+ msg.obj);
                break;
        }
    }

    void  AnalysisLoginJson(String result){
        LogUtil.WriteLog(Login.class,TAG_Login,result);
        try {
            ReturnMsgModel<String> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<String>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                SharePreferUtil.SetSyncTimeShare("Register","1");
                Intent intent = new Intent(context, MainActivity.class);
                startActivityLeft(intent);
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
        }catch (Exception ex) {
            MessageBox.Show(context,ex.getMessage());
        }
    }

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
        txtVer.setText(getString(R.string.login_ver)+(updateVersionService.getVersionCode(context)));
        if(ParamaterModel.SerialNo!=null) {
            txtSerialNo.setText(ParamaterModel.SerialNo);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
            txtPartner.setText( ParamaterModel.PartenerID);
    }

    @Event(R.id.btn_Login)
    private void btnLoginClick(View view) {
        if(ModelInfo.myReceiver!=null) {
            try {
                unregisterReceiver(ModelInfo.myReceiver); //取消MDM注册广播
                if (!(ParamaterModel.Model.toUpperCase().equals("TC75") || ParamaterModel.Model.toUpperCase().equals("A15_A5"))) {
                    MessageBox.Show(context, getString(R.string.Msg_NotSupportModel));
                    return;
                }
            } catch (Exception ex) {
                String str = ex.getMessage();
            }
        }
        if (TextUtils.isEmpty(ParamaterModel.PartenerID)) {
            MessageBox.Show(context, getString(R.string.Msg_No_Partner));
            return;
        }

        if (ParamaterModel.baseparaModel.getCusDnnoRule()==null) {
            MessageBox.Show(context, getString(R.string.Msg_No_CusDnRule));
            return;
        }

        ParamaterModel.Operater = edtOperater.getText().toString().trim();
        if (!TextUtils.isEmpty(ParamaterModel.Operater)) {
            SharePreferUtil.SetUserShare(context);
        }

        //设置数据库名称
        DbDnInfo.mSyncDn=null;
        DbManager.mDaoSession=null;
        DbManager.mDaoMaster=null;
        DbBaseInfo.mSyncDB=null;
        DbManager.DB_NAME=ParamaterModel.PartenerID+".db";


        UserInfoModel userInfoModel=new UserInfoModel();
        userInfoModel.setAGENT_NO(ParamaterModel.PartenerID);
        userInfoModel.setPDA_CODE(ParamaterModel.SerialNo);
        userInfoModel.setUSER_CODE(ParamaterModel.Operater);
        ParamaterModel.userInfoModel=userInfoModel;
        CommonUtil.setEditFocus(edtOperater);

        //删除超过保存日期的DN单据
        DbDnInfo.getInstance().DeleteDnBySaveTime();
        //获取参数
        SharePreferUtil.ReadSyncTimeShare();

        ParamaterModel.PartenerName = DbBaseInfo.getInstance().GetCustomNameById(ParamaterModel.PartenerID);

        LogUtil.WriteLog(Login.class,"btnLoginClick",ParamaterModel.Operater);

        Intent intent = new Intent(context, MainActivity.class);
        startActivityLeft(intent);

//        if(ParamaterModel.Register!=null &&  ParamaterModel.Register.equals("1")) {
//                Intent intent = new Intent(context, MainActivity.class);
//                startActivityLeft(intent);
//        }else {
//            final Map<String, String> params = new HashMap<String, String>();
//            String user = GsonUtil.parseModelToJson(ParamaterModel.userInfoModel);
//            params.put("UserInfoJS", user);
//            String para = (new JSONObject(params)).toString();
//            LogUtil.WriteLog(SyncBase.class, TAG_Login, para);
//            RequestHandler.addRequest(Request.Method.POST, TAG_Login, mHandler, RESULT_Login, null, URLModel.GetURL().ValidateEquip, params, null);
//        }



    }


    @Event(R.id.btnSetting)
    private void btnSettingClick(View view) {

//        Intent intent = new Intent(context, Setting.class);
//        startActivityLeft(intent);
        try {
            final EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_CLASS_TEXT
                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            et.setTextColor(getResources().getColor(R.color.black));
            new AlertDialog.Builder(this).setTitle(getString(R.string.Msg_InputPassword))
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(et)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String input = et.getText().toString();
                            if (ParamaterModel.SysPassword!=null && !ParamaterModel.SysPassword.equals(input)) {
                                MessageBox.Show(context, getString(R.string.Msg_PasswordError));
                                return;
                            }
                            Intent intent = new Intent(context, Setting.class);
                            startActivityLeft(intent);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        } catch (Exception ex) {
            MessageBox.Show(context, ex.getMessage());
        }
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
