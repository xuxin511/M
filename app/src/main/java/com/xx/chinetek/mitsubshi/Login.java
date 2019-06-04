package com.xx.chinetek.mitsubshi;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.model.Paramater;
import com.xx.chinetek.chineteklib.model.ReturnMsgModel;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.Network.RequestHandler;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.chineteklib.util.function.DESUtil;
import com.xx.chinetek.chineteklib.util.function.FileUtil;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.DB.DbLogInfo;
import com.xx.chinetek.method.DB.DbManager;
import com.xx.chinetek.method.DB.MigrationHelper;
import com.xx.chinetek.method.PlaySound;
import com.xx.chinetek.method.SharePreferUtil;
import com.xx.chinetek.method.Sync.SyncBase;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.URLModel;
import com.xx.chinetek.model.Base.UserInfoModel;
import com.xx.chinetek.model.DN.LogModel;
import com.xx.chinetek.service.LocationService;

import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_Login;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_Login;

@ContentView(R.layout.activity_login)
public class Login extends BaseActivity {

    Context context = Login.this;
    @ViewInject(R.id.txt_Partner)
    TextView txtPartner;
    @ViewInject(R.id.txt_PartnerName)
    TextView txtPartnerName;
    @ViewInject(R.id.txt_SerialNo)
    TextView txtSerialNo;
    @ViewInject(R.id.txtVer)
    TextView txtVer;
    @ViewInject(R.id.edt_Operater)
    EditText  edtOperater;


    public final static String REQUEST = "extra.mdm.request";
    public final static String RESPONE = "extra.mdm.respone";
    public static MyReceiver myReceiver;


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
        try {
            ReturnMsgModel<String> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<String>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                DESUtil.pvkey=returnMsgModel.getMaterialDoc();
                Intent intent = new Intent(context, MainActivity.class);
                startActivityLeft(intent);
            } else {
                MessageBox.Show(context,returnMsgModel.getMessage());
            }
        }catch (Exception ex) {
            ToastUtil.show(ex.getMessage());
            LogUtil.WriteLog(Login.class,"Login", ex.toString());
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
        MitApplication.locationModel=null;
        GetSysINfo();
        SharePreferUtil.ReadShare(context);
        SharePreferUtil.ReadUserShare(context);
        edtOperater.setText(ParamaterModel.Operater);
        Double version=updateVersionService.getVersionCode(context);
        txtVer.setText(getString(R.string.login_ver)+version);
        getPersimmions();
        PlaySound.getInstance();
//        if(Paramater.Port==13555){
//            Paramater.Port=8081;
//            SharePreferUtil.SetShare(context);
//        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        txtPartner.setText( ParamaterModel.PartenerID);
        txtPartnerName.setText( ParamaterModel.PartenerName);
    }

    @Event(R.id.btn_Login)
    private void btnLoginClick(View view) {
        DESUtil.pvkey = "SCGWMS00"; //初始密钥
     //   ParamaterModel.SerialNo = "18235522511436";//"1177326";
//        ParamaterModel.SerialNo = "1177326";
//       ParamaterModel.Model = "A15_A5";
        if (ParamaterModel.SerialNo == null || TextUtils.isEmpty(ParamaterModel.SerialNo)) {
            return;
        }
        if (myReceiver != null) {
            unregisterReceiver(myReceiver); //取消MDM注册广播
            myReceiver = null;
        }
        if (ParamaterModel.Model == null || !(Arrays.asList(getResources().getStringArray(R.array.Model)).contains(ParamaterModel.Model.toUpperCase()))) {
            MessageBox.Show(context, getString(R.string.Msg_NotSupportModel));
            return;
        }

        if (TextUtils.isEmpty(ParamaterModel.PartenerID)) {
            MessageBox.Show(context, getString(R.string.Msg_No_Partner));
            return;
        }

        if (ParamaterModel.baseparaModel.getCusDnnoRule() == null) {
            MessageBox.Show(context, getString(R.string.Msg_No_CusDnRule));
            return;
        }

        ParamaterModel.Operater = edtOperater.getText().toString().trim();
        //  if (!TextUtils.isEmpty(ParamaterModel.Operater)) {
        SharePreferUtil.SetUserShare(context);
        // }

        //设置数据库名称
        DbDnInfo.mSyncDn = null;
        DbManager.mDaoSession = null;
        DbManager.mDaoMaster = null;
        DbBaseInfo.mSyncDB = null;
        DbManager.DB_NAME = ParamaterModel.PartenerID + ".db";

        CommonUtil.setEditFocus(edtOperater);
        try {
            //删除超过保存日期的DN单据
            DbDnInfo.getInstance().DeleteDnBySaveTime();
        } catch (Exception ex) {
            try {
                //加密数据库升级
                MigrationHelper.encrypt(context, ParamaterModel.PartenerID + ".db", DbManager.dbpassword);
                DbBaseInfo.getInstance().DeleteAllBase();
            } catch (Exception exx) {

            }
        }
        //获取参数
        SharePreferUtil.ReadSyncTimeShare();
        if (ParamaterModel.PartenerName == null || ParamaterModel.PartenerName.equals("")) {
            ParamaterModel.PartenerName = DbBaseInfo.getInstance().GetCustomNameById(ParamaterModel.PartenerID);
            SharePreferUtil.SetShare(context);
        }
        LogUtil.WriteLog(Login.class, "btnLoginClick", ParamaterModel.Operater);

        UserInfoModel userInfoModel = new UserInfoModel();
        userInfoModel.setAGENT_NO(ParamaterModel.PartenerID);
        userInfoModel.setPDA_CODE(ParamaterModel.SerialNo);
        userInfoModel.setUSER_CODE(ParamaterModel.Operater);
        ParamaterModel.userInfoModel = userInfoModel;


        //验证登陆
        final Map<String, String> params = new HashMap<String, String>();
        String user = GsonUtil.parseModelToJson(ParamaterModel.userInfoModel);
        params.put("UserInfoJS", user);
        String para = (new JSONObject(params)).toString();

        LogUtil.WriteLog(SyncBase.class, TAG_Login, para);
        DbLogInfo.getInstance().InsertLog(new LogModel("登陆",para,""));

        RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_Login, getResources().getString(R.string.Msg_Login),
                context, mHandler, RESULT_Login, null, URLModel.GetURL().ValidateEquip, params, null);
    }


    @Event(R.id.btnSetting)
    private void btnSettingClick(View view) {
        try {
            final EditText et = new EditText(this);
            et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
        Paths.add(ParamaterModel.MailDirectory);
        Paths.add(ParamaterModel.FTPDirectory);
        FileUtil.CreateFile(Paths);
    }

    private void GetSysINfo(){

        String model = android.os.Build.MODEL;
        String serialNo=android.os.Build.SERIAL;
        if(!model.toUpperCase().equals("TC75")){
            myReceiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(RESPONE);
            BaseApplication.context.registerReceiver(myReceiver, intentFilter);
            SendBroadcast(new Date().getTime(), 0x0003, "Option", 1);
            SendBroadcast(new Date().getTime(), 0x0002, null, 1);
            Intent intent = new Intent("android.intent.action.BARCODEOUTPUT");
            intent.putExtra("android.intent.action.BARCODEOUTPUT", 1);
            sendBroadcast(intent);

        }else {
            ParamaterModel.SerialNo = serialNo;
            ParamaterModel.Model = model;
            txtSerialNo.setText(ParamaterModel.SerialNo);
        }
    }

    public void SendBroadcast(long timestamp, int cmd, String para, int value){
        Intent _intent = new Intent(REQUEST);

        _intent.putExtra("Timestamp", timestamp);
        _intent.putExtra("Cmd", cmd);
        if(para!=null)
            _intent.putExtra(para, value);
        context.sendBroadcast(_intent);
    }

    private  class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RESPONE)){
                if ((intent.getLongExtra("Timestamp", -1) > 0) &&
                        (intent.getIntExtra("Cmd", -1) > 0)){
                    if(intent.getIntExtra("Cmd", -1)==0x0003) {
                        ParamaterModel.SerialNo = intent.getStringExtra("Data");
                        txtSerialNo.setText(ParamaterModel.SerialNo);
                    }
                    if(intent.getIntExtra("Cmd", -1)==0x0002)
                        ParamaterModel.Model=intent.getStringExtra("Data");
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SDK_PERMISSION_REQUEST) {
            //询问用户权限
            if (permissions[2].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[2]
                    == PackageManager.PERMISSION_GRANTED) {
                CreateDirectory();
            }
        }
    }


    private final int SDK_PERMISSION_REQUEST = 127;
    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }


    private LocationService locationService;
    /***
     * Stop location service
     */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        // -----------location config ------------
        locationService = ((MitApplication) getApplication()).locationService;
        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        locationService.start();// 定位SDK

    }

    /*****
     *
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                StringBuffer sb = new StringBuffer();
                sb.append(location.getAddrStr());
                MitApplication.locationModel=sb.toString();
                MitApplication.gpsModel=location.getLongitude()+","+location.getLatitude();
            }
        }

    };
}
