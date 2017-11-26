package com.xx.chinetek.mitsubshi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;

import com.android.volley.Request;
import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.model.Paramater;
import com.xx.chinetek.chineteklib.model.ReturnMsgModel;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.Network.RequestHandler;
import com.xx.chinetek.chineteklib.util.dialog.LoadingDialog;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.FTP.FtpModel;
import com.xx.chinetek.method.Mail.MailModel;
import com.xx.chinetek.method.SharePreferUtil;
import com.xx.chinetek.model.Base.BaseparaModel;
import com.xx.chinetek.model.Base.CusBarcodeRule;
import com.xx.chinetek.model.Base.CusDnnoRule;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.Base.SyncParaModel;
import com.xx.chinetek.model.Base.URLModel;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_UploadPara;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncMaterial;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncPara;

@ContentView(R.layout.activity_setting)
public class Setting extends BaseActivity {

    Context context=Setting.this;

    @ViewInject(R.id.tabHost)
    TabHost tabHost;

    @ViewInject(R.id.edt_IPAdress)
    EditText edtIPAdress;
    @ViewInject(R.id.edt_Port)
    EditText edtPort;
    @ViewInject(R.id.edt_TimeOut)
    EditText edtTimeOut;
    @ViewInject(R.id.edt_MailAccount)
    EditText edtMailAccount;
    @ViewInject(R.id.edt_MailPassword)
    EditText edtMailPassword;
    @ViewInject(R.id.edt_MailSMTPort)
    EditText edtMailSMTPort;
    @ViewInject(R.id.edt_MailSMTP)
    EditText edtMailSMTP;
    @ViewInject(R.id.edt_MailIMAP)
    EditText edtMailIMAP;
    @ViewInject(R.id.edt_FtpHost)
    EditText edtFtpHost;
    @ViewInject(R.id.edt_FtpUserName)
    EditText edtFtpUserName;
    @ViewInject(R.id.edt_FtpPassword)
    EditText edtFtpPassword;
    @ViewInject(R.id.edt_FtpPort)
    EditText edtFtpPort;
    @ViewInject(R.id.edt_FtpDown)
    EditText edtFtpDown;
    @ViewInject(R.id.edt_FtpUp)
    EditText edtFtpUp;
    @ViewInject(R.id.txt_Partner)
    TextView txtPartner;
    @ViewInject(R.id.txt_DNSaveTime)
    TextView txtDNSaveTime;
    @ViewInject(R.id.ckIsuserRemark)
    CheckBox ckIsuserRemark;
    @ViewInject(R.id.ckSelfBarcode)
    CheckBox ckSelfBarcode;

    final  int LogUploadIndex=2;
    String Password;//临时存放密码

    String startwordsCusDN;
    Integer indexLength=0;
    CusBarcodeRule cusBarcodeRule;
    List<String> ToAdress;

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_UploadPara:
                AnalysisUploadParaJson((String) msg.obj);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____"+ msg.obj);
                break;
        }
    }

    void  AnalysisUploadParaJson(String result){
        LogUtil.WriteLog(Setting.class,TAG_SyncMaterial,result);
        try {
            ReturnMsgModel<String> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModel<String>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                MessageBox.Show(context,getString(R.string.Dia_UploadSuccess));
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
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.login_setting),true);
        x.view().inject(this);
        //初始化TabHost容器
        tabHost.setup();

        //在TabHost创建标签，然后设置：标题／图标／标签页布局
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("服务器" , null).setContent(R.id.tab1));
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("FTP" , null).setContent(R.id.tab2));
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("基本参数" , null).setContent(R.id.tab3));
    }

    @Override
    protected void initData() {
        super.initData();


        if(ParamaterModel.baseparaModel==null) ParamaterModel.baseparaModel=new BaseparaModel();
        edtIPAdress.setText(Paramater.IPAdress);
        edtPort.setText(Paramater.Port+"");
        edtTimeOut.setText(Paramater.SOCKET_TIMEOUT/1000+"");
        txtPartner.setText(ParamaterModel.PartenerID);
        txtDNSaveTime.setText(ParamaterModel.baseparaModel.getDNSaveTime()+"");
        ckIsuserRemark.setChecked(ParamaterModel.baseparaModel.getUseRemark());

        if(ParamaterModel.baseparaModel.getCusBarcodeRule()!=null){
            ckSelfBarcode.setChecked(ParamaterModel.baseparaModel.getCusBarcodeRule().getUsed());
            cusBarcodeRule=ParamaterModel.baseparaModel.getCusBarcodeRule();
        }
        if(ParamaterModel.baseparaModel.getMailModel()!=null){
            edtMailAccount.setText(ParamaterModel.baseparaModel.getMailModel().getAccount());
            edtMailPassword.setText(ParamaterModel.baseparaModel.getMailModel().getPassword());
            edtMailSMTPort.setText(ParamaterModel.baseparaModel.getMailModel().getMailServerPort());
            edtMailSMTP.setText(ParamaterModel.baseparaModel.getMailModel().getMailServerHost());
            edtMailIMAP.setText(ParamaterModel.baseparaModel.getMailModel().getMailClientHost());
            ToAdress=ParamaterModel.baseparaModel.getMailModel().getToAddress();
        }
        if(ParamaterModel.baseparaModel.getFtpModel()!=null){
            edtFtpHost.setText(ParamaterModel.baseparaModel.getFtpModel().getFtpHost());
            edtFtpUserName.setText(ParamaterModel.baseparaModel.getFtpModel().getFtpUserName());
            edtFtpPassword.setText(ParamaterModel.baseparaModel.getFtpModel().getFtpPassword());
            edtFtpPort.setText(ParamaterModel.baseparaModel.getFtpModel().getFtpPort()+"");
            edtFtpDown.setText(ParamaterModel.baseparaModel.getFtpModel().getFtpDownLoad());
            edtFtpUp.setText(ParamaterModel.baseparaModel.getFtpModel().getFtpUpLoad());

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1001  && resultCode==1){
            cusBarcodeRule=data.getParcelableExtra("cusBarcodeRule");
            ckSelfBarcode.setChecked(true);
        }
        if(requestCode==1002  && resultCode==1){
            ToAdress=data.getStringArrayListExtra("ToAdress");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Event(R.id.layoutCustom)
    private void layoutCustomClick(View view){
        final EditText et = new EditText(this);
        et.setTextColor(getResources().getColor(R.color.black));
        new AlertDialog.Builder(this).setTitle(getString(R.string.Msg_InputCustom))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String input = et.getText().toString();
                        txtPartner.setText(input);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Event(R.id.layoutSysPassword)
    private void layoutSysPasswordClick(View view){
        final EditText et = new EditText(this);
        et.setTextColor(getResources().getColor(R.color.black));
        new AlertDialog.Builder(this).setTitle(getString(R.string.Msg_InputPassword))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(et)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Password= et.getText().toString();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    @Event(R.id.layoutDNSaveTime)
    private void layoutDNSaveTimeClick(View view){
        final String[] item=getResources().getStringArray(R.array.DNSaveTime);
        new AlertDialog.Builder(this).setTitle(getString(R.string.Msg_ChoiceSaveTime))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setItems(item, new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialogInterface, int i) {
                    txtDNSaveTime.setText(item[i].toString());
                  }
              }).show();

    }

    @Event(R.id.layoutCusBarcode)
    private void layoutCusBarcodeClick(View view){
        if(ckSelfBarcode.isChecked()){
            ckSelfBarcode.setChecked(false);
            return;
        }
        Intent intent=new Intent(context,Setting_CusBarcodeRule.class);
        startActivityForResult(intent,1001);
    }

 @Event(R.id.layoutTOMail)
    private void layoutTOMailClick(View view){
        Intent intent=new Intent(context,Setting_ToMailList.class);
        startActivityForResult(intent,1002);
    }

    @Event(value = R.id.ckIsuserRemark,type = CompoundButton.OnCheckedChangeListener.class)
    private void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
        ckIsuserRemark.setChecked(isCheck);
    }

//    @Event(value = R.id.ckSelfBarcode,type = CompoundButton.OnCheckedChangeListener.class)
//    private void ckSelfBarcodeChanged(CompoundButton compoundButton, boolean isCheck) {
//        ckSelfBarcode.setChecked(!ckSelfBarcode.isChecked());
//    }



    @Event(R.id.layoutCusDnNo)
    private void layoutCusDnNoClick(View view){
        final View textEntryView = LayoutInflater.from(this).inflate(R.layout.activity_cusdnno_content, null);
        final EditText edtStartWords=(EditText) textEntryView.findViewById(R.id.edt_StartWords);
        final EditText edtIndexLength=(EditText)textEntryView.findViewById(R.id.edt_barcodeLength);
        if(ParamaterModel.baseparaModel.getCusDnnoRule()!=null){
            edtStartWords.setText(ParamaterModel.baseparaModel.getCusDnnoRule().getStartWords());
            edtIndexLength.setText(ParamaterModel.baseparaModel.getCusDnnoRule().getIndexLength().toString());
        }
        new AlertDialog.Builder(this).setTitle(getString(R.string.Msg_SetbarcodeRule))
                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(textEntryView)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String startword=edtStartWords.getText().toString().trim();
                        String length=edtIndexLength.getText().toString().trim();
                        if(!CommonUtil.isNumeric(length)){
                            MessageBox.Show(context,getString(R.string.Msg_inputNumic));
                            return;
                        }
                        if(TextUtils.isEmpty(startword) || TextUtils.isEmpty(length)){
                            MessageBox.Show(context,getString(R.string.Msg_notEmpty));
                            return;
                        }
                        int len=Integer.parseInt(length);
                        if(startword.length()+len>12){
                            MessageBox.Show(context,getString(R.string.Msg_lenMax));
                            return;
                        }
                        startwordsCusDN=startword;
                        indexLength=len;
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menusetting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.getItemId() == R.id.action_filter) {
                BaseApplication.DialogShowText = getString(R.string.Dia_UploadLogFile);
                UploadLog();
            }
            if (item.getItemId() == R.id.action_Save) {
                SaveSetting();
            }
            if (item.getItemId() == R.id.action_upPara) {
                savePara();
                final Map<String, String> params = new HashMap<String, String>();
                String basePara=GsonUtil.parseModelToJson(ParamaterModel.baseparaModel);
                SyncParaModel syncParaModel=new SyncParaModel();
                syncParaModel.setKey("cusPara");
                syncParaModel.setValue(basePara.replace(":","?")
                        .replace("{","#").replace("\"","!"));
                ArrayList<SyncParaModel> syncParaModels=new ArrayList<>();
                syncParaModels.add(syncParaModel);
                params.put("Dist_Code", ParamaterModel.PartenerID);
                params.put("CusSettingJS", GsonUtil.parseModelListToJsonArray(syncParaModels));
                String para = (new JSONObject(params)).toString();
                LogUtil.WriteLog(Setting.class, TAG_SyncPara, para);
                RequestHandler.addRequestWithDialog(Request.Method.POST, TAG_SyncPara,
                        context.getString(R.string.Dia_UploadPara), context, mHandler, RESULT_UploadPara, null,  URLModel.GetURL().UploadPara, params, null);
            }
        }catch (Exception ex){
            MessageBox.Show(context,ex.getMessage());
        }
        return super.onOptionsItemSelected(item);
    }

    void SaveSetting() {
        savePara();
        new AlertDialog.Builder(context).setTitle("提示").setCancelable(false).setMessage(getResources().getString(R.string.Msg_SaveSuccess)).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeActiviry();
            }
        }).show();

    }

    private void savePara() {
        Paramater.IPAdress = edtIPAdress.getText().toString().trim();
        Paramater.Port = Integer.parseInt(edtPort.getText().toString().trim());
        Paramater.SOCKET_TIMEOUT = Integer.parseInt(edtTimeOut.getText().toString().trim()) * 1000;
        ParamaterModel.PartenerID =txtPartner.getText().toString().trim();
        ParamaterModel.baseparaModel.setDNSaveTime(Integer.parseInt(txtDNSaveTime.getText().toString().trim()));
        ParamaterModel.baseparaModel.setUseRemark(ckIsuserRemark.isChecked());
        ParamaterModel.SysPassword=Password;
        ParamaterModel.baseparaModel.setCusBarcodeRule(new CusBarcodeRule());
        ParamaterModel.baseparaModel.getCusBarcodeRule().setUsed(ckSelfBarcode.isChecked());
        if(cusBarcodeRule==null)  cusBarcodeRule=new CusBarcodeRule();
        cusBarcodeRule.setUsed(ckSelfBarcode.isChecked());
        ParamaterModel.baseparaModel.setCusBarcodeRule(cusBarcodeRule);

        if(!TextUtils.isEmpty(startwordsCusDN) && indexLength!=0) {
            if (ParamaterModel.baseparaModel.getCusDnnoRule() == null) ParamaterModel.baseparaModel.setCusDnnoRule(new CusDnnoRule());
            ParamaterModel.baseparaModel.getCusDnnoRule().setStartWords(startwordsCusDN==null?"":startwordsCusDN);
            ParamaterModel.baseparaModel.getCusDnnoRule().setIndexLength(indexLength);
        }
        if(ParamaterModel.baseparaModel.getMailModel()==null) ParamaterModel.baseparaModel.setMailModel(new MailModel());
        ParamaterModel.baseparaModel.getMailModel().setAccount(edtMailAccount.getText().toString().trim());
        ParamaterModel.baseparaModel.getMailModel().setPassword(edtMailPassword.getText().toString().trim());
        ParamaterModel.baseparaModel.getMailModel().setMailServerPort(edtMailSMTPort.getText().toString().trim());
        ParamaterModel.baseparaModel.getMailModel().setMailServerHost(edtMailSMTP.getText().toString().trim());
        ParamaterModel.baseparaModel.getMailModel().setMailClientHost(edtMailIMAP.getText().toString().trim());
        ParamaterModel.baseparaModel.getMailModel().setToAddress(ToAdress);
        if(ParamaterModel.baseparaModel.getFtpModel()==null) ParamaterModel.baseparaModel.setFtpModel(new FtpModel());
        ParamaterModel.baseparaModel.getFtpModel().setFtpHost(edtFtpHost.getText().toString().trim());
        ParamaterModel.baseparaModel.getFtpModel().setFtpUserName(edtFtpUserName.getText().toString().trim());
        ParamaterModel.baseparaModel.getFtpModel().setFtpPassword(edtFtpPassword.getText().toString().trim());
        ParamaterModel.baseparaModel.getFtpModel().setFtpPort(Integer.parseInt(edtFtpPort.getText().toString().trim()));
        ParamaterModel.baseparaModel.getFtpModel().setFtpDownLoad(edtFtpDown.getText().toString().trim());
        ParamaterModel.baseparaModel.getFtpModel().setFtpUpLoad(edtFtpUp.getText().toString().trim());

        SharePreferUtil.SetShare(context);
    }

    void UploadLog(){
        final LoadingDialog dialog = new LoadingDialog(context);
        dialog.show();
        String url="http://"+ Paramater.IPAdress+":"+Paramater.Port+"/UpLoad.ashx";
        File[] files = new File(Environment.getExternalStorageDirectory()+"/log/").listFiles();
        final List<File> list= Arrays.asList(files);
        Collections.sort(list, new FileComparator());

        for(int i=0;i<LogUploadIndex;i++) {
            final  int index=i;
            RequestParams params = new RequestParams(url);
            params.setMultipart(true);
            params.addBodyParameter("file", new File(list.get(list.size()-i-1).getAbsolutePath()));
            x.http().post(params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    //加载成功回调，返回获取到的数据
                    if(index==LogUploadIndex-1) {
                        ToastUtil.show(result);
                    }
                }
                @Override
                public void onFinished() {
                    if(index==LogUploadIndex-1) {
                        dialog.dismiss();
                    }
                }
                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    ToastUtil.show(ex.toString());
                }
            });
        }
    }

    public class FileComparator implements Comparator<File> {
        public int compare(File file1, File file2) {
            if(file1.getName().compareTo(file2.getName())<1)
            {
                return -1;
            }else
            {
                return 1;
            }
        }
    }
}
