package com.xx.chinetek.mitsubshi.Bulkupload;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.adapter.bulkupload.BulkuploadListItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.model.ReturnMsgModelList;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.Upload.UploadDN;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.DNStatusEnum;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.MultipleDN;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_ExceptionDNList;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_ExceptionDNList;

@ContentView(R.layout.activity_exception_list)
public class Bulkupload extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    Context context = Bulkupload.this;
    @ViewInject(R.id.edt_DNNoFuilter)
    EditText edtDNNoFuilter;
    @ViewInject(R.id.Lsv_ExceptionList)
    ListView LsvExceptionList;
    @ViewInject(R.id.mSwipeLayout)
    SwipeRefreshLayout mSwipeLayout;

    ArrayList<DNModel> DNModels;
    BulkuploadListItemAdapter bulkuploadListItemAdapter;


   // String UploadDNno="";

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_ExceptionDNList:
                AnalysisExceptionDNListJson((String) msg.obj);

                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____" + msg.obj);
                break;
        }

    }


    void  AnalysisExceptionDNListJson(String result){

        try {
            LogUtil.WriteLog(Bulkupload.class, TAG_ExceptionDNList, result);
            ReturnMsgModelList<MultipleDN> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<MultipleDN>>() {
            }.getType());
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                ArrayList<DNModel> SendDNs=new ArrayList<>();//存放发送邮件和FTP-DN单据
               ArrayList<MultipleDN> multipleDNS = returnMsgModel.getModelJson();
               String dnno="";
                for (MultipleDN mulitdn:multipleDNS) {
                    String tempdnno=mulitdn.getDN().getDN_SOURCE()==3?mulitdn.getDN().getCUS_DN_NO():mulitdn.getDN().getAGENT_DN_NO();
                    DNModel tempdnModel = DbDnInfo.getInstance().GetLoaclDN(tempdnno);
                    //保留原有数据
                    if(tempdnModel!=null) {
                        mulitdn.getDN().setOPER_DATE(tempdnModel.getOPER_DATE());
                        mulitdn.getDN().setCUS_DN_NO(tempdnModel.getCUS_DN_NO());
                        mulitdn.getDN().setREMARK(tempdnModel.getREMARK());
                    }
                    String Showdnno=mulitdn.getDN().getDN_SOURCE()==3?mulitdn.getDN().getCUS_DN_NO():mulitdn.getDN().getAGENT_DN_NO();
                    if(mulitdn.getDN().getDN_SOURCE()==3){ //自建单据,修改系统单号
                        DbDnInfo.getInstance().DeleteDN(tempdnModel.getAGENT_DN_NO());
//                        ArrayList<DNModel> dnModels = new ArrayList<>();
//                        dnModels.add(mulitdn.getDN());
//                        //插入数据
//                        DbDnInfo.getInstance().InsertDNDB(dnModels);
                    }

                    DbDnInfo.getInstance().ChangeDNStatusByDnNo(mulitdn.getDN().getAGENT_DN_NO(), DNStatusEnum.complete);
                    if(mulitdn.getStatus().equals("N") && mulitdn.getDN().getDN_SOURCE()==3){
                        ArrayList<DNModel> dnModels = new ArrayList<>();
                        mulitdn.getDN().setSTATUS(DNStatusEnum.complete);
                        dnModels.add(mulitdn.getDN());
                        //插入数据
                        DbDnInfo.getInstance().InsertDNDB(dnModels);
                    }
                    if(mulitdn.getStatus().equals("F")){
                        ArrayList<DNModel> dnModels=new ArrayList<>();
                        dnModels.add(mulitdn.getDN());
                        SendDNs.add(mulitdn.getDN());
                        DbDnInfo.getInstance().InsertDNDB(dnModels);
                        DbDnInfo.getInstance().ChangeDNStatusByDnNo(mulitdn.getDN().getAGENT_DN_NO(), DNStatusEnum.Sumbit);
                    }
                    if(mulitdn.getStatus().equals("S")){
                        if(mulitdn.getDN()!=null) {
                            ArrayList<DNModel> dnModels = new ArrayList<>();
                            dnModels.add(mulitdn.getDN());
                            //插入数据
                            DbDnInfo.getInstance().InsertDNDB(dnModels);
                            //更新出库单状态(异常)
                            DbDnInfo.getInstance().ChangeDNStatusByDnNo(mulitdn.getDN().getAGENT_DN_NO(), DNStatusEnum.exeption);
                        }
                    }
                    if(mulitdn.getStatus().equals("K") && mulitdn.getDN()!=null){ //后台单据已关闭
                        dnno+="关闭出库单："+Showdnno+"\n";
                        DbDnInfo.getInstance().DELscanbyagent(mulitdn.getDN().getAGENT_DN_NO());
                        int status=mulitdn.getDN().getSTATUS()==DNStatusEnum.download?DNStatusEnum.complete:DNStatusEnum.Sumbit;
                        mulitdn.getDN().setSTATUS(status);
                        ArrayList<DNModel> dnModels = new ArrayList<>();
                        dnModels.add(mulitdn.getDN());
                        //插入数据
                        DbDnInfo.getInstance().InsertDNDB(dnModels);
                    }
                    if(mulitdn.getStatus().equals("Z") ) { //后台单据有异常
                        dnno+="后台异常出库单："+Showdnno+"\n";
                    }
                    if(mulitdn.getStatus().equals("E")){
                        dnno+="失败出库单："+Showdnno+"\n";
                    }
                }

                final  ArrayList<DNModel> ExportdnModels=SendDNs;
                new Thread() {
                    @Override
                    public void run() {
                        try {
                         UploadDN.ExportDN(ExportdnModels, 0);
                         UploadDN.ExportDN(ExportdnModels, 1);
                        }catch (Exception ex){

                        }
                    }
                }.start();

                if(!TextUtils.isEmpty(dnno)){
                    MessageBox.Show(context, "提交说明！\n"+dnno);
                }else{
                    MessageBox.Show(context, getString(R.string.Msg_DNUploadSuccess));
                }

            } else {
                MessageBox.Show(context, returnMsgModel.getMessage());
            }

        }catch (Exception ex){
            MessageBox.Show(context, ex.getMessage());
        }
        GetbulkuploadList();
    }

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.Bulkupload),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        GetbulkuploadList();
        edtDNNoFuilter.addTextChangedListener(bululoadTextWatcher);
        mSwipeLayout.setOnRefreshListener(this); //下拉刷新
    }

    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(false);
    }

        @Event(value = R.id.Lsv_ExceptionList,type = AdapterView.OnItemLongClickListener.class)
    private boolean LsvItemlongClick(AdapterView<?> parent, View view, int position, long id) {
            bulkuploadListItemAdapter.modifyStates(position);
        return true;
    }


    @Event(value = R.id.Lsv_ExceptionList,type = AdapterView.OnItemClickListener.class)
    private void LsvExceptionListonItemClick(AdapterView<?> parent, View view, int position, long id) {
        try{
            DNModel  dnModel=(DNModel)bulkuploadListItemAdapter.getItem(position);
            StartScan(dnModel);

        }catch(Exception ex){
            MessageBox.Show(context,ex.toString());
        }

    }


    /**
     * 文本变化事件
     */
    TextWatcher bululoadTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String filterContent=edtDNNoFuilter.getText().toString();
            if(!filterContent.equals(""))
                bulkuploadListItemAdapter.getFilter().filter(filterContent);
            else{
                BindListView();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    void BindListView(){
        if(DNModels!=null) {
            bulkuploadListItemAdapter = new BulkuploadListItemAdapter(context, DNModels);
            LsvExceptionList.setAdapter(bulkuploadListItemAdapter);
        }
    }


    private void StartScan(DNModel dnModel) {
        Intent intent=new Intent(context,BulkuploadScan.class);
        Bundle bundle=new Bundle();
        bundle.putParcelable("DNModel",dnModel);
        intent.putExtras(bundle);
        String dnno=dnModel.getDN_SOURCE()==3?dnModel.getCUS_DN_NO():dnModel.getAGENT_DN_NO();
        intent.putExtra("DNNo",dnno);
        startActivityLeft(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_bulkuptitle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_Export) {
            List<DNModel> postmodels=new ArrayList<>();
            boolean isUpload=true;
            for (int i = 0; i < DNModels.size(); i++) {
                if (bulkuploadListItemAdapter.getStates(i)) {
                   DNModel postmodel = DbDnInfo.getInstance().AllPostDate(DNModels.get(i));
                    if (postmodel == null) {
                        MessageBox.Show(context, "出库单上报错误！\n" + DNModels.get(i).getAGENT_DN_NO());
                        isUpload=false;
                        break;
                    }
                    postmodels.add(postmodel);
                }
            }
            if(isUpload && postmodels.size()!=0){
                final List<DNModel> uploadModels= postmodels;
                new AlertDialog.Builder(context).setTitle("提示")// 设置对话框标题
                        .setIcon(android.R.drawable.ic_dialog_info)// 设置对话框图
                        .setMessage(context.getResources().getString(R.string.Msg_Upload_DNSelf))
                        .setPositiveButton("提交并关闭", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                UploadDN.UploadDNListToMaps(uploadModels,"F", mHandler);
                            }
                        })
                        .setNegativeButton("提交", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                UploadDN.UploadDNListToMaps(uploadModels,"N", mHandler);
                            }
                        })
                        .show();
            }
        }
        return super.onOptionsItemSelected(item);
    }


    void GetbulkuploadList(){
        try{
            DNModels =ImportbulkuploadList();
            bulkuploadListItemAdapter=new BulkuploadListItemAdapter(context, DNModels);
            LsvExceptionList.setAdapter(bulkuploadListItemAdapter);
        }catch(Exception ex){
            MessageBox.Show(context,ex.toString());
        }
    }

    ArrayList<DNModel> ImportbulkuploadList(){
        ArrayList<DNModel> DNModels =new ArrayList<>();
        DNModels = DbDnInfo.getInstance().GetLoaclcompleteDN();
        return DNModels;
    }
}
