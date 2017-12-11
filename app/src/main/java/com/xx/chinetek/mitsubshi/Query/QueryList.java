package com.xx.chinetek.mitsubshi.Query;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.xx.chinetek.adapter.DN.DeliveryListItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.LoadingDialog;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.FTP.FtpUtil;
import com.xx.chinetek.method.FileUtils;
import com.xx.chinetek.method.Upload.UploadFiles;
import com.xx.chinetek.mitsubshi.BaseIntentActivity;
import com.xx.chinetek.mitsubshi.DN.DeliveryScan;
import com.xx.chinetek.mitsubshi.Exception.ExceptionScan;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNTypeModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncFTP;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncMail;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncUSB;

@ContentView(R.layout.activity_query_list)
public class QueryList extends BaseIntentActivity implements SwipeRefreshLayout.OnRefreshListener{

  Context context=QueryList.this;
    @ViewInject(R.id.edt_DeleveryNoFuilter)
    EditText edtDeleveryNoFuilter;
    @ViewInject(R.id.Lsv_DeliveryList)
    ListView LsvDeliveryList;
    @ViewInject(R.id.mSwipeLayout)
    SwipeRefreshLayout mSwipeLayout;
    ArrayList<DNModel> DNModels; //所有出库单

    DeliveryListItemAdapter deliveryListItemAdapter;
    ArrayList<DNModel> SelectDnModels;//选择导出DN
    LoadingDialog dialog;

    int position;
    @Override
    public void onHandleMessage(Message msg) {
            switch (msg.what) {
                case RESULT_SyncMail:
                case RESULT_SyncFTP:
                    new Thread(){
                        @Override
                        public void run() {
                            String [] MoveFiles=new String[SelectDnModels.size()];
                            for(int i=0;i<SelectDnModels.size();i++){
                                MoveFiles[i]=SelectDnModels.get(i).getFtpFileName();
                            }
                            FtpUtil.FtpMoveFile(ParamaterModel.baseparaModel.getFtpModel(),MoveFiles);
                        }
                    }.start();
                    MessageBox.Show(context,(String)msg.obj);
                    break;
                case RESULT_SyncUSB:
                    MessageBox.Show(context,getString(R.string.Msg_UploadSuccess)+msg.obj+"\n路径："+ParamaterModel.UpDirectory);
                    break;
                case NetworkError.NET_ERROR_CUSTOM:
                    ToastUtil.show("获取请求失败_____" + msg.obj);
                    break;
            }

        dialog.dismiss();
        BindListView();
    }


    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.Query),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        edtDeleveryNoFuilter.addTextChangedListener(DeleveryNoTextWatcher);
        mSwipeLayout.setOnRefreshListener(this); //下拉刷新
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_querytitle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String[] items=getResources().getStringArray(R.array.ExportTypeList);
        if(item.getItemId()==R.id.action_Export){
           SelectDnModels=new ArrayList<>();
            for (int i = 0; i < DNModels.size(); i++) {
                if (deliveryListItemAdapter.getStates(i)) {
                    SelectDnModels.add(0, DNModels.get(i));
                }
            }
            if(SelectDnModels.size()!=0) {
//                ContextThemeWrapper contextThemeWrapper =
//                        new ContextThemeWrapper(context, R.style.dialog);
                new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.Msg_Export_Type))// 设置对话框标题
                        .setIcon(android.R.drawable.ic_dialog_info)// 设置对话框图
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    ExportDN(SelectDnModels, which);
                                }catch (Exception  ex){
                                    dialog.dismiss();
                                }
                            }
                        }).show();
            }else{
                MessageBox.Show(context,getString(R.string.Msg_No_ExportDn));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        BindListView();
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BindListView();
    }

    @Event(value = R.id.Lsv_DeliveryList,type = AdapterView.OnItemLongClickListener.class)
    private boolean LsvDeliveryListonItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        deliveryListItemAdapter.modifyStates(position);
        return true;
    }

    @Event(value = R.id.Lsv_DeliveryList,type = AdapterView.OnItemClickListener.class)
    private void LsvDeliveryListonItemClick(AdapterView<?> parent, View view, int position, long id) {
        DNModel  dnModel=(DNModel)deliveryListItemAdapter.getItem(position);
        ParamaterModel.DnTypeModel=new DNTypeModel();
        ParamaterModel.DnTypeModel.setDNType(dnModel.getDN_SOURCE());
        Intent intent = new Intent();
        Bundle bundle=new Bundle();
        switch (dnModel.getSTATUS()){
            case -1:
                intent=new Intent(context, ExceptionScan.class);
                bundle.putParcelable("DNModel",dnModel);
                intent.putExtras(bundle);
                startActivityLeft(intent);
                break;
            default:
                intent=new Intent(context,DeliveryScan.class);
                bundle.putParcelable("DNModel",dnModel);
                intent.putExtras(bundle);
                intent.putExtra("DNNo",dnModel.getAGENT_DN_NO());
                startActivityLeft(intent);
                break;
        }
    }

    /**
     *  导出DN
     * @param selectDnModels
     * @param Index
     * @throws Exception
     */
    void ExportDN(ArrayList<DNModel> selectDnModels, int Index) throws Exception{
        FileUtils.DeleteFiles(Index);
        FileUtils.ExportDNFile(selectDnModels,Index); //导出文件至本地目录
        File dirFile=new File(FileUtils.GetDirectory(Index));
        if(dirFile.isDirectory()) {
            File[] Files = dirFile.listFiles();
            if (Files.length > 0) {
                switch (Index) {
                    case 0: //邮件
                        if(ParamaterModel.baseparaModel.getMailModel()!=null &&(
                                ParamaterModel.baseparaModel.getMailModel().getToAddress()==null
                                || ParamaterModel.baseparaModel.getMailModel().getToAddress().size()==0)) {
                            MessageBox.Show(context, getString(R.string.Msg_ToMailNotSet));
                            break;
                        }
                        BaseApplication.DialogShowText = getString(R.string.Dia_UploadMail);
                        dialog = new LoadingDialog(context);
                        dialog.show();
                        UploadFiles.UploadMail(Files, mHandler);
                        break;
                    case 1: //FTP
                        BaseApplication.DialogShowText = getString(R.string.Dia_UploadFtp);
                        dialog = new LoadingDialog(context);
                        dialog.show();
                        UploadFiles.UploadFtp(Files, mHandler);
                        break;
                    case 2://USB
                        BaseApplication.DialogShowText = getString(R.string.Dia_UploadUSB);
                        dialog = new LoadingDialog(context);
                        dialog.show();
                        android.os.Message msg = mHandler.obtainMessage(RESULT_SyncUSB, Files.length);
                        mHandler.sendMessage(msg);
                        break;
                }
            }
        }else{
            MessageBox.Show(context,getString(R.string.Msg_No_DirExportDn));
        }
    }

    void BindListView(){
        DNModels= DbDnInfo.getInstance().GetLoaclDNbyCondition();
        if(DNModels!=null) {
            deliveryListItemAdapter = new DeliveryListItemAdapter(context, DNModels);
            LsvDeliveryList.setAdapter(deliveryListItemAdapter);
        }
    }




    /**
     * 文本变化事件
     */
    TextWatcher DeleveryNoTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String filterContent=edtDeleveryNoFuilter.getText().toString();
            if(!filterContent.equals(""))
                deliveryListItemAdapter.getFilter().filter(filterContent);
            else{
                BindListView();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
