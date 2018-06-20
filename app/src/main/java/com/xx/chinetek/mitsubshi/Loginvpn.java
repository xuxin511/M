//package com.xx.chinetek.mitsubshi;
//
//import android.app.AlertDialog;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.os.AsyncTask;
//import android.os.Message;
//import android.text.InputType;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.View;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.Request;
//import com.google.gson.reflect.TypeToken;
//import com.sangfor.ssl.IVpnDelegate;
//import com.sangfor.ssl.SFException;
//import com.sangfor.ssl.SangforAuth;
//import com.sangfor.ssl.common.VpnCommon;
//import com.xx.chinetek.chineteklib.base.BaseActivity;
//import com.xx.chinetek.chineteklib.base.BaseApplication;
//import com.xx.chinetek.chineteklib.model.Paramater;
//import com.xx.chinetek.chineteklib.model.ReturnMsgModel;
//import com.xx.chinetek.chineteklib.util.Network.NetworkError;
//import com.xx.chinetek.chineteklib.util.Network.RequestHandler;
//import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
//import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
//import com.xx.chinetek.chineteklib.util.function.CommonUtil;
//import com.xx.chinetek.chineteklib.util.function.FileUtil;
//import com.xx.chinetek.chineteklib.util.function.GsonUtil;
//import com.xx.chinetek.chineteklib.util.log.LogUtil;
//import com.xx.chinetek.method.DB.DbBaseInfo;
//import com.xx.chinetek.method.DB.DbDnInfo;
//import com.xx.chinetek.method.DB.DbManager;
//import com.xx.chinetek.method.SharePreferUtil;
//import com.xx.chinetek.method.Sync.SyncBase;
//import com.xx.chinetek.model.Base.ParamaterModel;
//import com.xx.chinetek.model.Base.URLModel;
//import com.xx.chinetek.model.Base.UserInfoModel;
//
//import org.json.JSONObject;
//import org.xutils.view.annotation.ContentView;
//import org.xutils.view.annotation.Event;
//import org.xutils.view.annotation.ViewInject;
//import org.xutils.x;
//
//import java.net.InetAddress;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_Login;
//import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_Login;
//
//@ContentView(R.layout.activity_login)
//public class Loginvpn extends BaseActivity implements IVpnDelegate{
//
//    Context context = Loginvpn.this;
//    @ViewInject(R.id.txt_Partner)
//    TextView txtPartner;
//    @ViewInject(R.id.txt_SerialNo)
//    TextView txtSerialNo;
//    @ViewInject(R.id.txtVer)
//    TextView txtVer;
//    @ViewInject(R.id.edt_Operater)
//    EditText  edtOperater;
//
//
//    public final static String REQUEST = "extra.mdm.request";
//    public final static String RESPONE = "extra.mdm.respone";
//    public static MyReceiver myReceiver;
//
//
//    @Override
//    public void onHandleMessage(Message msg) {
//        switch (msg.what) {
//            case RESULT_Login:
//                AnalysisLoginJson((String) msg.obj);
//                break;
//            case NetworkError.NET_ERROR_CUSTOM:
//                ToastUtil.show("获取请求失败_____"+ msg.obj);
//                break;
//        }
//    }
//
//    void  AnalysisLoginJson(String result){
//        LogUtil.WriteLog(Loginvpn.class,TAG_Login,result);
//        try {
//            ReturnMsgModel<String> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<String>>() {
//            }.getType());
//            if (returnMsgModel.getHeaderStatus().equals("S")) {
//                SharePreferUtil.SetSyncTimeShare("Register","1");
//                Intent intent = new Intent(context, MainActivity.class);
//                startActivityLeft(intent);
//            } else {
//                MessageBox.Show(context,returnMsgModel.getMessage());
//            }
//        }catch (Exception ex) {
//            MessageBox.Show(context,ex.getMessage());
//        }
//    }
//
//    @Override
//    protected void initViews() {
//        super.initViews();
//        Paramater.IsShowTitleBar = false;
//        x.view().inject(this);
//        CreateDirectory();
//    }
//
//    @Override
//    protected void initData() {
//        super.initData();
//        GetSysINfo();
//        SharePreferUtil.ReadShare(context);
//        SharePreferUtil.ReadUserShare(context);
//        edtOperater.setText(ParamaterModel.Operater);
//        txtVer.setText(getString(R.string.login_ver)+(updateVersionService.getVersionCode(context)));
//        initVpnModule();
//    }
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        txtPartner.setText( ParamaterModel.PartenerID);
//        SangforAuth sfAuth = SangforAuth.getInstance();
//        if (sfAuth != null) {
//            // 从其它界面回到这个界面时，需重新设置回调，让vpn把回调发送到当前Activity的vpnCallback里面来
//            sfAuth.setDelegate(this);
//            Log.i(TAG, "set IVpnDelegate");
//        }
//    }
//
//    @Event(R.id.btn_Login)
//    private void btnLoginClick(View view) {
//        if(ParamaterModel.SerialNo==null || TextUtils.isEmpty(ParamaterModel.SerialNo)){
//            return;
//        }
//        if(myReceiver!=null) {
//            unregisterReceiver(myReceiver); //取消MDM注册广播
//            myReceiver=null;
//        }
//            if (!(ParamaterModel.Model.toUpperCase().equals("TC75") || ParamaterModel.Model.toUpperCase().equals("A15_A5"))) {
//                MessageBox.Show(context, getString(R.string.Msg_NotSupportModel));
//                return;
//            }
//
//        if (TextUtils.isEmpty(ParamaterModel.PartenerID)) {
//            MessageBox.Show(context, getString(R.string.Msg_No_Partner));
//            return;
//        }
//
//        if (ParamaterModel.baseparaModel.getCusDnnoRule()==null) {
//            MessageBox.Show(context, getString(R.string.Msg_No_CusDnRule));
//            return;
//        }
//
//        ParamaterModel.Operater = edtOperater.getText().toString().trim();
//        if (!TextUtils.isEmpty(ParamaterModel.Operater)) {
//            SharePreferUtil.SetUserShare(context);
//        }
//
//        //设置数据库名称
//        DbDnInfo.mSyncDn=null;
//        DbManager.mDaoSession=null;
//        DbManager.mDaoMaster=null;
//        DbBaseInfo.mSyncDB=null;
//        DbManager.DB_NAME=ParamaterModel.PartenerID+".db";
//
//        CommonUtil.setEditFocus(edtOperater);
//
//        //删除超过保存日期的DN单据
//        DbDnInfo.getInstance().DeleteDnBySaveTime();
//        //获取参数
//        SharePreferUtil.ReadSyncTimeShare();
//
//        ParamaterModel.PartenerName = DbBaseInfo.getInstance().GetCustomNameById(ParamaterModel.PartenerID);
//
//        LogUtil.WriteLog(Loginvpn.class,"btnLoginClick",ParamaterModel.Operater);
//
//        UserInfoModel userInfoModel=new UserInfoModel();
//        userInfoModel.setAGENT_NO(ParamaterModel.PartenerID);
//        userInfoModel.setPDA_CODE(ParamaterModel.SerialNo);
//        userInfoModel.setUSER_CODE(ParamaterModel.Operater);
//        ParamaterModel.userInfoModel=userInfoModel;
//
////        Intent intent = new Intent(context, MainActivity.class);
////        startActivityLeft(intent);
//
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
//
//
//
//    }
//
//
//    @Event(R.id.btnSetting)
//    private void btnSettingClick(View view) {
//
////        Intent intent = new Intent(context, Setting.class);
////        startActivityLeft(intent);
//        try {
//            final EditText et = new EditText(this);
//            et.setInputType(InputType.TYPE_CLASS_TEXT
//                    | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//            et.setTextColor(getResources().getColor(R.color.black));
//            new AlertDialog.Builder(this).setTitle(getString(R.string.Msg_InputPassword))
//                    .setIcon(android.R.drawable.ic_dialog_info)
//                    .setView(et)
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            String input = et.getText().toString();
//                            if (ParamaterModel.SysPassword!=null && !ParamaterModel.SysPassword.equals(input)) {
//                                MessageBox.Show(context, getString(R.string.Msg_PasswordError));
//                                return;
//                            }
//                            Intent intent = new Intent(context, Setting.class);
//                            startActivityLeft(intent);
//                        }
//                    })
//                    .setNegativeButton("取消", null)
//                    .show();
//        } catch (Exception ex) {
//            MessageBox.Show(context, ex.getMessage());
//        }
//    }
//
//
//    void CreateDirectory(){
//        List<String> Paths=new ArrayList<>();
//        Paths.add(ParamaterModel.Directory);
//        Paths.add(ParamaterModel.DBDirectory);
//        Paths.add(ParamaterModel.DownDirectory);
//        Paths.add(ParamaterModel.UpDirectory);
//        Paths.add(ParamaterModel.MailDirectory);
//        Paths.add(ParamaterModel.FTPDirectory);
//        FileUtil.CreateFile(Paths);
//
//    }
//
//    private void GetSysINfo(){
//
//        String model = android.os.Build.MODEL;
//        String serialNo=android.os.Build.SERIAL;
//        if(!model.toUpperCase().equals("TC75")){
//            myReceiver = new MyReceiver();
//            IntentFilter intentFilter = new IntentFilter();
//            intentFilter.addAction(RESPONE);
//            BaseApplication.context.registerReceiver(myReceiver, intentFilter);
//            SendBroadcast(new Date().getTime(), 0x0003, "Option", 1);
//            SendBroadcast(new Date().getTime(), 0x0002, null, 1);
//
//        }else {
//            ParamaterModel.SerialNo = serialNo;
//            ParamaterModel.Model = model;
//            txtSerialNo.setText(ParamaterModel.SerialNo);
//        }
//    }
//
//    public void SendBroadcast(long timestamp, int cmd, String para, int value){
//        Intent _intent = new Intent(REQUEST);
//
//        _intent.putExtra("Timestamp", timestamp);
//        _intent.putExtra("Cmd", cmd);
//        if(para!=null)
//            _intent.putExtra(para, value);
//        context.sendBroadcast(_intent);
//    }
//
//    private  class MyReceiver extends BroadcastReceiver {
//
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equals(RESPONE)){
//                if ((intent.getLongExtra("Timestamp", -1) > 0) &&
//                        (intent.getIntExtra("Cmd", -1) > 0)){
//                    if(intent.getIntExtra("Cmd", -1)==0x0003) {
//                        ParamaterModel.SerialNo = intent.getStringExtra("Data");
//                        txtSerialNo.setText(ParamaterModel.SerialNo);
//                    }
//                    if(intent.getIntExtra("Cmd", -1)==0x0002)
//                        ParamaterModel.Model=intent.getStringExtra("Data");
//                }
//            }
//        }
//    }
//
////-----------VPN----------------------
//    private int AUTH_MODULE = SangforAuth.AUTH_MODULE_EASYAPP;
//    private static final String TAG = "SFSDK_" + Loginvpn.class.getSimpleName();
//    private static String VPN_IP = "116.247.107.213"; // VPN设备地址　（也可以使用域名访问）
//    private static int VPN_PORT = 443; // vpn设备端口号，一般为443
//    // 用户名密码认证；用户名和密码
//    private static String USER_NAME = "TESTING";
//    private static String USER_PASSWD = "meach.2019";
//
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        SangforAuth sfAuth = SangforAuth.getInstance();
//        if (sfAuth != null) {
//            // 取消其它地方的vpnCallback调用，让vpn把回调发送到当前Activity的vpnCallback里面来
//            sfAuth.setDelegate(this);
//            Log.d(TAG, "Set delegate :" + TAG);
//        }
//        Log.i("TAG", "Do vpnLogout");
//        // vpnLogout是异步接口，在vpnCallback里面等到回调后表示从服务端成功注销用户
//        if (SangforAuth.getInstance().vpnLogout() == false) {
//            Log.e("TAG", "call vpnLogout failed, please see Logcat log");
//        } else {
//            // 调用vpnLogout成功后会在vpnCallback方法中收到结果
//        }
//    }
//
//    @Override
//    public void vpnCallback(int vpnResult, int authType) {
//        SangforAuth sfAuth = SangforAuth.getInstance();
//
//        switch (vpnResult) {
//            case IVpnDelegate.RESULT_VPN_INIT_FAIL:
//                /**
//                 * 初始化vpn失败
//                 */
//                Log.i(TAG, "RESULT_VPN_INIT_FAIL, error is " + sfAuth.vpnGeterr());
//                break;
//
//            case IVpnDelegate.RESULT_VPN_INIT_SUCCESS:
//                /**
//                 * 初始化vpn成功，接下来就需要开始认证工作了
//                 */
//                Log.i(TAG, "RESULT_VPN_INIT_SUCCESS, current vpn status is " + sfAuth.vpnQueryStatus());
//                Log.i(TAG, "vpnResult============" + vpnResult + "\nauthType ============" + authType);
//                sfAuth.setLoginParam(IVpnDelegate.AUTH_DEVICE_LANGUAGE, "zh_CN");// zh_CN or en_US
//                // 设置后台不自动登陆,true为off,即取消自动登陆.默认为false,后台自动登陆.
//                // sfAuth.setLoginParam(AUTO_LOGIN_OFF_KEY, "true");
//                // 初始化成功，进行认证操作　（此处采用“用户名密码”认证）
//                doVpnLogin(IVpnDelegate.AUTH_TYPE_PASSWORD);//AUTH_TYPE_CERTIFICATE
//                break;
//            case IVpnDelegate.RESULT_VPN_AUTH_SUCCESS:
//                /**
//                 * 认证成功，认证成功有两种情况，一种是认证通过，可以使用sslvpn功能了，
//                 *
//                 * 另一种是 前一个认证（如：用户名密码认证）通过，但需要继续认证（如：需要继续证书认证）
//                 */
//                if (authType == IVpnDelegate.AUTH_TYPE_NONE) {
//
//				/*
//				 * // session共享登陆--主APP保存：认证成功 保存TWFID（SessionId），供子APP使用 String twfid = sfAuth.getTwfid(); Log.i(TAG, "twfid = "+twfid);
//				 */
//                    // 若为L3vpn流程，认证成功后会自动开启l3vpn服务，需等l3vpn服务开启完成后再访问资源
//                    if (SangforAuth.getInstance().getModuleUsed() == SangforAuth.AUTH_MODULE_EASYAPP) {
//                        // EasyApp流程，认证流程结束，可访问资源。
//                        doResourceRequest();
//                    }
//                }
//                break;
//            case IVpnDelegate.RESULT_VPN_AUTH_CANCEL:
//                Log.i(TAG, "RESULT_VPN_AUTH_CANCEL");
//                break;
//            case IVpnDelegate.RESULT_VPN_AUTH_LOGOUT:
//                /**
//                 * 主动注销（自己主动调用logout接口）
//                 */
//                Log.i(TAG, "RESULT_VPN_AUTH_LOGOUT");
//                break;
//            default:
//                /**
//                 * 其它情况
//                 */
//                Log.i(TAG, "default result, vpn result is " + vpnResult);
//                break;
//        }
//    }
//
//    @Override
//    public void reloginCallback(int status, int result) {
//        switch (status) {
//            case IVpnDelegate.VPN_START_RELOGIN:
//                Log.e(TAG, "relogin callback start relogin start ...");
//                break;
//            case IVpnDelegate.VPN_END_RELOGIN:
//                Log.e(TAG, "relogin callback end relogin ...");
//
//                if (result == IVpnDelegate.VPN_RELOGIN_SUCCESS) {
//                    Log.e(TAG, "relogin callback, relogin success!");
//                } else {
//                    Log.e(TAG, "relogin callback, relogin failed");
//                }
//                break;
//        }
//    }
//
//    @Override
//    public void vpnRndCodeCallback(byte[] bytes) {
//
//    }
//
//    private void initVpnModule() {
//        SangforAuth sfAuth = SangforAuth.getInstance();
//        try {
//            // SDK模式初始化，easyapp模式或者是l3vpn模式，两种模式区别请参考文档
//            Intent intent = getIntent();
//            AUTH_MODULE = intent.getIntExtra("vpn_mode", SangforAuth.AUTH_MODULE_EASYAPP);
//            //sfAuth.init，每次app运行只需要执行一次，注销vpn后重新登录不需要再执行
//            sfAuth.init(getApplication(), this, this, AUTH_MODULE);//SangforAuth.AUTH_MODULE_L3VPN、SangforAuth.AUTH_MODULE_EASYAPP
//            sfAuth.setLoginParam(AUTH_CONNECT_TIME_OUT, String.valueOf(8));
//        } catch (SFException e) {
//            e.printStackTrace();
//        }
//        initSslVpn();
//    }
//
//    /**
//     * 开始初始化VPN，该初始化为异步接口，后续动作通过回调函数vpncallback通知结果
//     *
//     * @return 成功返回true，失败返回false，一般情况下返回true
//     */
//    private boolean initSslVpn() {
//        InitSslVpnTask initSslVpnTask = new InitSslVpnTask();
//        initSslVpnTask.execute();
//        return true;
//    }
//
//    private void doResourceRequest() {
//        // 认证结束，可访问资源。
//        Log.i(TAG, "welcome to sangfor sslvpn!");
//    }
//
//    /**
//     * 处理认证，通过传入认证类型（需要的话可以改变该接口传入一个hashmap的参数用户传入认证参数）.
//     *
//     * 也可以一次性把认证参数设入，这样就如果认证参数全满足的话就可以一次性认证通过，可见下面屏蔽代码
//     *
//     * @param authType
//     *            认证类型
//     * @throws SFException
//     */
//    private void doVpnLogin(int authType) {
//        Log.d(TAG, "doVpnLogin authType " + authType);
//        boolean ret = false;
//        SangforAuth sfAuth = SangforAuth.getInstance();
//		/*
//		 * // session共享登陆：主APP封装时走原认证流程，子APP认证时使用TWFID（SessionId）认证方式 boolean isMainApp = true; //子APP,isMainApp = false; if(!isMainApp){ authType =
//		 * IVpnDelegate.AUTH_TYPE_TWFID; }
//		 */
//        switch (authType) {
//            case IVpnDelegate.AUTH_TYPE_PASSWORD://用户名密码认证
//                if (USER_NAME.isEmpty()) {
//                    Log.d(TAG,"请输入VPN用户名及密码");
//                    return;
//                }
//                sfAuth.setLoginParam(IVpnDelegate.PASSWORD_AUTH_USERNAME, USER_NAME);
//                sfAuth.setLoginParam(IVpnDelegate.PASSWORD_AUTH_PASSWORD, USER_PASSWD);
//                ret = sfAuth.vpnLogin(IVpnDelegate.AUTH_TYPE_PASSWORD);
//                break;
//            default:
//                Log.w(TAG, "do not support authType " + authType);
//                break;
//        }
//        Log.i(TAG, "call login method " + (ret ? "success" : "failed"));
//    }
//
//
//
//    class InitSslVpnTask extends AsyncTask<Void, Void, Boolean> {
//        InetAddress m_iAddr = null;
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            try {
//                VPN_IP = VPN_IP.replaceAll("(?i)https://", "").replaceAll("(?i)http://", "");//去掉协议头
//                m_iAddr = InetAddress.getByName(VPN_IP);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(Boolean result) {
//            SangforAuth sfAuth = SangforAuth.getInstance();
//            String strHost = "";
//            if (m_iAddr != null) {
//                strHost = m_iAddr.getHostAddress();
//            }
//            if (TextUtils.isEmpty(strHost)) {
//                Log.i(TAG, "解析VPN服务器域名失败");
//                Toast.makeText(Loginvpn.this, "解析VPN服务器域名失败", Toast.LENGTH_SHORT).show();
//                strHost = "0.0.0.0";
//            }
//            Log.i(TAG, "vpn server ip is: " + strHost);
//            long host = VpnCommon.ipToLong(strHost);
//            if (sfAuth.vpnInit(host, VPN_PORT) == false) {
//                Log.d(TAG, "vpn init fail, errno is " + sfAuth.vpnGeterr());
//                return;
//            }
//        }
//    }
//}
