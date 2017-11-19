package com.xx.chinetek.mitsubshi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
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

import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.model.Paramater;
import com.xx.chinetek.chineteklib.util.dialog.LoadingDialog;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.method.FTP.FtpModel;
import com.xx.chinetek.method.Mail.MailModel;
import com.xx.chinetek.method.SharePreferUtil;
import com.xx.chinetek.model.Base.CusBarcodeRule;
import com.xx.chinetek.model.Base.ParamaterModel;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    final  int LogUploadIndex=1;
    String Password;//临时存放密码
    String startwords;
    Integer barcodeLength=0;

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
        BaseApplication.DialogShowText = getString(R.string.Dia_UploadLogFile);

        edtIPAdress.setText(Paramater.IPAdress);
        edtPort.setText(Paramater.Port+"");
        edtTimeOut.setText(Paramater.SOCKET_TIMEOUT/1000+"");
        txtPartner.setText(ParamaterModel.PartenerID);
        txtDNSaveTime.setText(ParamaterModel.DNSaveTime+"");
        ckIsuserRemark.setChecked(ParamaterModel.IsUseRemark);
        if(ParamaterModel.cusBarcodeRule!=null){
            ckSelfBarcode.setChecked(ParamaterModel.cusBarcodeRule.getUsed());
        }
        if(ParamaterModel.mailModel!=null){
            edtMailAccount.setText(ParamaterModel.mailModel.getAccount());
            edtMailPassword.setText(ParamaterModel.mailModel.getPassword());
            edtMailSMTPort.setText(ParamaterModel.mailModel.getMailServerPort());
            edtMailSMTP.setText(ParamaterModel.mailModel.getMailServerHost());
            edtMailIMAP.setText(ParamaterModel.mailModel.getMailClientHost());
        }
        if(ParamaterModel.ftpModel!=null){
            edtFtpHost.setText(ParamaterModel.ftpModel.getFtpHost());
            edtFtpUserName.setText(ParamaterModel.ftpModel.getFtpUserName());
            edtFtpPassword.setText(ParamaterModel.ftpModel.getFtpPassword());
            edtFtpPort.setText(ParamaterModel.ftpModel.getFtpPort()+"");
            edtFtpDown.setText(ParamaterModel.ftpModel.getFtpDownLoad());
            edtFtpUp.setText(ParamaterModel.ftpModel.getFtpUpLoad());

        }

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

    @Event(value = R.id.ckIsuserRemark,type = CompoundButton.OnCheckedChangeListener.class)
    private void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
        ckIsuserRemark.setChecked(isCheck);
    }

    @Event(value = R.id.ckSelfBarcode)
    private void ckSelfBarcodeClick(View view){
        if(!ckSelfBarcode.isChecked()){
            ckSelfBarcode.setChecked(false);
            return;
        }
            final View textEntryView = LayoutInflater.from(this).inflate(R.layout.activity_selfbarcode_content, null);
            final EditText edtStartWords=(EditText) textEntryView.findViewById(R.id.edt_StartWords);
            final EditText edtbarcodeLength=(EditText)textEntryView.findViewById(R.id.edt_barcodeLength);
            if(ParamaterModel.cusBarcodeRule!=null){
                edtStartWords.setText(ParamaterModel.cusBarcodeRule.getStartWords());
                edtbarcodeLength.setText(ParamaterModel.cusBarcodeRule.getBarcodeLength().toString());
            }
            new AlertDialog.Builder(this).setTitle(getString(R.string.Msg_SetbarcodeRule))
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setView(textEntryView)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String startword=edtStartWords.getText().toString().trim();
                            String length=edtbarcodeLength.getText().toString().trim();
                            if(!CommonUtil.isNumeric(length)){
                                MessageBox.Show(context,getString(R.string.Msg_inputNumic));
                                ckSelfBarcode.setChecked(false);
                                return;
                            }
                            if(TextUtils.isEmpty(startword) || TextUtils.isEmpty(length)){
                                MessageBox.Show(context,getString(R.string.Msg_notEmpty));
                                ckSelfBarcode.setChecked(false);
                                return;
                            }
                            startwords=startword;
                            barcodeLength=Integer.parseInt(length);
                            ckSelfBarcode.setChecked(true);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String startword=edtStartWords.getText().toString().trim();
                            String length=edtbarcodeLength.getText().toString().trim();
                            if(TextUtils.isEmpty(startword) || TextUtils.isEmpty(length)){
                                ckSelfBarcode.setChecked(false);
                            }else{
                                ckSelfBarcode.setChecked(true);
                            }
                        }
                    })
                    .show();


    }
//
//    @Event(value = R.id.ckSelfBarcode,type = CompoundButton.OnCheckedChangeListener.class)
//    private void ckSelfBarcodeonCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
//
//    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menusetting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        try {
            if (item.getItemId() == R.id.action_filter) {
                UploadLog();
            }
            if (item.getItemId() == R.id.action_Save) {
                SaveSetting();
            }
        }catch (Exception ex){
            MessageBox.Show(context,ex.getMessage());
        }
        return super.onOptionsItemSelected(item);
    }

    void SaveSetting() {
        Paramater.IPAdress = edtIPAdress.getText().toString().trim();
        Paramater.Port = Integer.parseInt(edtPort.getText().toString().trim());
        Paramater.SOCKET_TIMEOUT = Integer.parseInt(edtTimeOut.getText().toString().trim()) * 1000;
        ParamaterModel.PartenerID =txtPartner.getText().toString().trim();
        ParamaterModel.DNSaveTime =Integer.parseInt(txtDNSaveTime.getText().toString().trim());
        ParamaterModel.IsUseRemark =ckIsuserRemark.isChecked();
        ParamaterModel.SysPassword=Password;
        ParamaterModel.cusBarcodeRule=new CusBarcodeRule();
        ParamaterModel.cusBarcodeRule.setUsed(ckSelfBarcode.isChecked());
        if(ckSelfBarcode.isChecked()){
            ParamaterModel.cusBarcodeRule.setStartWords(startwords);
            ParamaterModel.cusBarcodeRule.setBarcodeLength(barcodeLength);
        }
        if(ParamaterModel.mailModel==null) ParamaterModel.mailModel=new MailModel();
        ParamaterModel.mailModel.setAccount(edtMailAccount.getText().toString().trim());
        ParamaterModel.mailModel.setPassword(edtMailPassword.getText().toString().trim());
        ParamaterModel.mailModel.setMailServerPort(edtMailSMTPort.getText().toString().trim());
        ParamaterModel.mailModel.setMailServerHost(edtMailSMTP.getText().toString().trim());
        ParamaterModel.mailModel.setMailClientHost(edtMailIMAP.getText().toString().trim());
        if(ParamaterModel.ftpModel==null) ParamaterModel.ftpModel=new FtpModel();
        ParamaterModel.ftpModel.setFtpHost(edtFtpHost.getText().toString().trim());
        ParamaterModel.ftpModel.setFtpUserName(edtFtpUserName.getText().toString().trim());
        ParamaterModel.ftpModel.setFtpPassword(edtFtpPassword.getText().toString().trim());
        ParamaterModel.ftpModel.setFtpPort(Integer.parseInt(edtFtpPort.getText().toString().trim()));
        ParamaterModel.ftpModel.setFtpDownLoad(edtFtpDown.getText().toString().trim());
        ParamaterModel.ftpModel.setFtpUpLoad(edtFtpUp.getText().toString().trim());

        SharePreferUtil.SetShare(context);
        new AlertDialog.Builder(context).setTitle("提示").setCancelable(false).setMessage(getResources().getString(R.string.Msg_SaveSuccess)).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                closeActiviry();
            }
        }).show();

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
