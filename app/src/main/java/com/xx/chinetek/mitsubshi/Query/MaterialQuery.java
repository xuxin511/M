package com.xx.chinetek.mitsubshi.Query;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.xx.chinetek.adapter.MaterialQueryItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.LoadingDialog;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.FTP.FtpUtil;
import com.xx.chinetek.method.FileUtils;
import com.xx.chinetek.method.Mail.MailUtil;
import com.xx.chinetek.method.Upload.UploadDN;
import com.xx.chinetek.method.Upload.UploadFiles;
import com.xx.chinetek.mitsubshi.BaseIntentActivity;
import com.xx.chinetek.mitsubshi.Exception.ExceptionBarcodelist;
import com.xx.chinetek.mitsubshi.Exception.ExceptionScan;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DBReturnModel;
import com.xx.chinetek.model.DN.DNModel;

import org.greenrobot.greendao.query.QueryBuilder;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncFTP;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncMail;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncUSB;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_UploadDN;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_ScanBarcode;

@ContentView(R.layout.activity_material_query)
public class MaterialQuery extends BaseIntentActivity {

    Context context=MaterialQuery.this;

    @ViewInject(R.id.lsv_MaterialQuery)
    ListView lsvMaterialQuery;
    @ViewInject(R.id.edt_ItemName)
    EditText edtItemName;
    @ViewInject(R.id.edt_Code)
    EditText edtCode;
    @ViewInject(R.id.spin_ItemLine)
    Spinner spinItemLine;
    @ViewInject(R.id.spin_ShowCount )
    Spinner spinShowCount;
    @ViewInject(R.id.txt_ListCount)
    TextView txtListCount;


    LoadingDialog dialog;

    MaterialQueryItemAdapter materialQueryItemAdapter;
    List<MaterialModel> materialModels;

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case TAG_SCAN:
                try {
                    String itemLine="";
                    if(!spinItemLine.getSelectedItem().toString().equals("所有")){
                        itemLine=spinItemLine.getSelectedItem().toString();
                    }
                    BindListView();
                } catch (Exception ex) {
                    ToastUtil.show(ex.getMessage());
                    LogUtil.WriteLog(ExceptionScan.class,"ExceptionScan-CheckScanBarcode", ex.toString());
                }
                break;
            case RESULT_SyncMail:
            case RESULT_SyncFTP:
                dialog.dismiss();
                MessageBox.Show(context,(String)msg.obj+("\r\n文件格式：txt/csv"));
                break;
            case RESULT_SyncUSB:
                dialog.dismiss();
                MessageBox.Show(context,getString(R.string.Msg_UploadSuccess)+msg.obj+("\r\n文件格式：txt/csv")+"\n路径："+ParamaterModel.UpDirectory);
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                dialog.dismiss();
                ToastUtil.show("获取请求失败_____" + msg.obj);
                break;
        }
    }
    void BindSpinner(){
        ArrayList<String> ItemLines=DbBaseInfo.getInstance().QueryItemLines();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.item_spinner);
        adapter.addAll(ItemLines);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinItemLine.setAdapter(adapter);
        spinItemLine.setPrompt(getString(R.string.choiceItemLine));
        spinItemLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               //if( !spinItemLine.getSelectedItem().toString().equals("所有"))
                    BindListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

      String[] showCount  =getResources().getStringArray(R.array.Material_ShowCount);
        adapter = new ArrayAdapter<String>(this,
                R.layout.item_spinner);
        adapter.addAll(showCount);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinShowCount.setAdapter(adapter);
        spinShowCount.setPrompt(getString(R.string.choiceShowCount));
        spinShowCount.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    BindListView();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void initViews() {
        super.initViews();
//        QueryBuilder.LOG_SQL = true;
//        QueryBuilder.LOG_VALUES = true;
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.MaterialQuery),true);
        x.view().inject(this);
    }

    void BindListView(){
        try {
            String itemLine="";
            if(!spinItemLine.getSelectedItem().toString().equals("所有")){
                itemLine=spinItemLine.getSelectedItem().toString();
            }
            String showCount=spinShowCount.getSelectedItem().toString();
            String itemName=edtItemName.getText().toString().trim();
            String code=edtCode.getText().toString().trim();
//            BaseApplication.DialogShowText = getString(R.string.Dia_MaterialQuery);
//            dialog = new LoadingDialog(context);
//            dialog.show();
            materialModels = DbBaseInfo.getInstance().Querytems(code, itemName, code, itemLine,showCount);
            materialQueryItemAdapter = new MaterialQueryItemAdapter(context, materialModels);
            lsvMaterialQuery.setAdapter(materialQueryItemAdapter);
         //   dialog.dismiss();
            txtListCount.setText("合计数：" + materialModels.size() + "");
        }catch (Exception ex){
          //  dialog.dismiss();
        }
    }

    @Event(value = {R.id.edt_ItemName,R.id.edt_Code}, type = View.OnKeyListener.class)
    private boolean edtOnKey(View v, int keyCode, KeyEvent event) {
        try{
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
            {
                keyBoardCancle();
                BindListView();
                return true;
            }
        }catch(Exception ex){
            MessageBox.Show(context, ex.getMessage());
            CommonUtil.setEditFocus(v.getId()==R.id.edt_Code?edtCode:edtItemName);
            return true;
        }
        return false;
    }

    @Override
    protected void initData() {
        super.initData();
        BindSpinner();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_materialquerytitle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.action_Export) {
                final String[] items = getResources().getStringArray(R.array.ExportTypeList);
                new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.Msg_Export_Type))// 设置对话框标题
                        .setIcon(android.R.drawable.ic_dialog_info)// 设置对话框图
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    ExportDN(which);
                                } catch (Exception ex) {
                                    dialog.dismiss();
                                }
                            }
                        }).show();
            }
        return super.onOptionsItemSelected(item);
    }

    void ExportDN(final  int Index) throws Exception{
        try {
            FileUtils.DeleteFiles(Index);
            String itemLine="";
            if(!spinItemLine.getSelectedItem().toString().equals("所有")){
                itemLine=spinItemLine.getSelectedItem().toString();
            }
            final String line=itemLine;
            switch (Index){
                case 0: //邮件
                    if (ParamaterModel.baseparaModel.getMailModel() != null && (
                            ParamaterModel.baseparaModel.getMailModel().getToAddress() == null
                                    || ParamaterModel.baseparaModel.getMailModel().getToAddress().size() == 0)) {
                        MessageBox.Show(context, getString(R.string.Msg_ToMailNotSet));
                        break;
                    }
                    BaseApplication.DialogShowText = getString(R.string.Dia_UploadMailM);
                    dialog = new LoadingDialog(context);
                    dialog.show();
                    break;
                case 1: //FTP
                    BaseApplication.DialogShowText = getString(R.string.Dia_UploadFtpM);
                    dialog = new LoadingDialog(context);
                    dialog.show();
                    break;
            }
            new Thread(){
                @Override
                public void run() {
                    try {
                        List<MaterialModel> materialModels=DbBaseInfo.getInstance().Querytems("","","",line,"");
                        FileUtils.ExportMaterialFile(materialModels, Index); //导出文件至本地目录
                        File dirFile = new File(FileUtils.GetDirectory(Index));
                        if (dirFile.isDirectory()) {
                            File[] Files = dirFile.listFiles();
                            if (Files.length > 0) {
                                switch (Index) {
                                    case 0: //邮件
//                            if (ParamaterModel.baseparaModel.getMailModel() != null && (
//                                    ParamaterModel.baseparaModel.getMailModel().getToAddress() == null
//                                            || ParamaterModel.baseparaModel.getMailModel().getToAddress().size() == 0)) {
//                                MessageBox.Show(context, getString(R.string.Msg_ToMailNotSet));
//                                break;
//                            }
//                            BaseApplication.DialogShowText = getString(R.string.Dia_UploadMailM);
//                            dialog = new LoadingDialog(context);
//                            dialog.show();
                                        UploadFiles.UploadMail(Files, mHandler);
                                        break;
                                    case 1: //FTP
//                            BaseApplication.DialogShowText = getString(R.string.Dia_UploadFtpM);
//                            dialog = new LoadingDialog(context);
//                            dialog.show();
                                        UploadFiles.UploadFtp(Files, mHandler);
                                        break;
                                }
                            }
                        } else {
                            dialog.dismiss();
                            MessageBox.Show(context, getString(R.string.Msg_No_DirExportDn));
                        }
                    }catch (Exception ex){
                        dialog.dismiss();
                    }
                }
            }.start();

        }catch (Exception ex){
            dialog.dismiss();
            ToastUtil.show(ex.getMessage());
            LogUtil.WriteLog(QueryList.class,"QueryList-ExportDN", ex.toString());
        }
    }

}
