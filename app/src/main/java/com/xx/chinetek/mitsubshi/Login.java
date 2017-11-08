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
        SharePreferUtil.ReadShare(context);
        SharePreferUtil.ReadUserShare(context);
        edtOperater.setText(ParamaterModel.Operater);
    }

    @Override
    protected void onResume() {
        super.onResume();
        txtPartner.setText(ParamaterModel.PartenerID);
    }

    @Event(R.id.btn_Login)
    private void btnLoginClick(View view) {
        String model = android.os.Build.MODEL;
//        if (!(model.toUpperCase().equals("TC75") || model.toUpperCase().equals("IDATA1500"))) {
//            MessageBox.Show(context, "设备型号不支持！");
//            return;
//        }

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

//    private class MyTask extends AsyncTask<String, Integer, ArrayList<HashMap<String, String>>> {
//
//
//        @Override
//        protected ArrayList<HashMap<String, String>> doInBackground(String... params) {
//            ArrayList<HashMap<String, String>> list=new ArrayList<HashMap<String, String>>();
//            try {
//
//                File storefile = new File(File.separator + "mnt" + File.separator
//                        + "sdcard" + File.separator + "DDN980015089_QR.txt");
//                ArrayList<String> list123  =new ArrayList<>();
//                list123.add(storefile.getAbsolutePath());
//                MailModel mailModel=new MailModel();
//
//                mailModel.setAccount("ghost_511@sina.com");
//                mailModel.setPassword("x5613x5305");
//                mailModel.setMailServerHost("smtp.sina.com");
//                mailModel.setMailClientHost("imap.sina.com");
//                mailModel.setMailServerPort("25");
//                mailModel.setValidate(true);
//                mailModel.setFromAddress("ghost_511@sina.com");
//                mailModel.setToAddress("ghost_511@sina.com");
//                mailModel.setSubject("测试");
//                mailModel.setContent("测试DDN980015089_QR.txt");
//
//               //
//
////                SendOneMail sendOneMail=new SendOneMail("");
////                sendOneMail.sendAttachment(mailModel,list123);
//
//                MailUtil.GetMail(mailModel);
//            } catch (Exception ex){
//                String str=ex.getMessage();
//            }
//            return list;
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<HashMap<String, String>> list) {
//
//            try {
//                DaoSession daoSession = DbManager.getDaoSession(new GreenDaoContext());
//                MaterialModelDao materialModelDao = daoSession.getMaterialModelDao();
//                MaterialModel materialModel = new MaterialModel();
//                materialModel.setMATNR("12333");
//                materialModel.setNORMT("Y");
//                materialModelDao.insert(materialModel);
//            } catch (Exception ex) {
//                MessageBox.Show(context, ex.getMessage());
//            }
//        }
//
//    }

}
