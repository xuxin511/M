package com.xx.chinetek.mitsubshi.Bulkupload;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
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
import com.xx.chinetek.method.DB.DbLogInfo;
import com.xx.chinetek.method.FTP.FtpUtil;
import com.xx.chinetek.method.Upload.UploadDN;
import com.xx.chinetek.mitsubshi.OrderFilter;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.DNStatusEnum;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.LogModel;
import com.xx.chinetek.model.DN.MultipleDN;
import com.xx.chinetek.model.QueryModel;

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
    @ViewInject(R.id.fab)
    FloatingActionButton fab;
    @ViewInject(R.id.fabCancel)
    FloatingActionButton fabCancel;
    @ViewInject(R.id.CBCloseDN)
    CheckBox CBCloseDN;

    QueryModel queryModel;
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
          //  LogUtil.WriteLog(Bulkupload.class, TAG_ExceptionDNList, result);
            ReturnMsgModelList<MultipleDN> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<MultipleDN>>() {
            }.getType());
            DbLogInfo.getInstance().InsertLog(new LogModel("出库单批量提交结果",returnMsgModel.getHeaderStatus(),""));
            if (returnMsgModel.getHeaderStatus().equals("S")) {
                ArrayList<DNModel> SendDNs=new ArrayList<>();//存放发送邮件和FTP-DN单据
               ArrayList<MultipleDN> multipleDNS = returnMsgModel.getModelJson();
               String dnno="";
               int completeDnNum=0;
               List<String> ftpNames=new ArrayList<>();
                for (MultipleDN mulitdn:multipleDNS) {
                    if(mulitdn.getDN().getDN_SOURCE()==2){//ftp需要移动文件之BAK
                        String ftpName= DbDnInfo.getInstance().GetFtpFileName(mulitdn.getDN().getAGENT_DN_NO());
                        ftpNames.add(ftpName);
                    }
                    String tempdnno=mulitdn.getDN().getDN_SOURCE()==3 || mulitdn.getDN().getDN_SOURCE()==5?mulitdn.getDN().getCUS_DN_NO():mulitdn.getDN().getAGENT_DN_NO();
                    DNModel tempdnModel = DbDnInfo.getInstance().GetLoaclDN(tempdnno);
                    //保留原有数据
                    if(tempdnModel!=null) {
                        mulitdn.getDN().setOPER_DATE(tempdnModel.getOPER_DATE());
                        mulitdn.getDN().setCUS_DN_NO(tempdnModel.getCUS_DN_NO());
                        mulitdn.getDN().setREMARK(tempdnModel.getREMARK());
                    }
                    String Showdnno=mulitdn.getDN().getDN_SOURCE()==3 || mulitdn.getDN().getDN_SOURCE()==5?mulitdn.getDN().getCUS_DN_NO():mulitdn.getDN().getAGENT_DN_NO();
                    if(mulitdn.getDN().getDN_SOURCE()==3 && !(
                            mulitdn.getStatus().equals("Z") || mulitdn.getStatus().equals("E"))){ //自建单据,修改系统单号
                        DbDnInfo.getInstance().DeleteDN(tempdnModel.getAGENT_DN_NO(),false);
                    }

                    DbDnInfo.getInstance().ChangeDNStatusByDnNo(mulitdn.getDN().getAGENT_DN_NO(), DNStatusEnum.complete);
                    if(mulitdn.getStatus().equals("R")){ //单据重复
                        dnno+="后台重复出库单："+Showdnno+"\n";
                        if ( mulitdn.getDN().getDN_SOURCE() == 3){
                            ArrayList<DNModel> dnModels = new ArrayList<>();
                            mulitdn.getDN().setSTATUS(DNStatusEnum.complete);
                            dnModels.add(mulitdn.getDN());
                            //插入数据
                            DbDnInfo.getInstance().InsertDNDB(dnModels);
                        }
                        DbDnInfo.getInstance().ChangeDNStatusByDnNo(mulitdn.getDN().getAGENT_DN_NO(), DNStatusEnum.Repert);

                    }
                    if(mulitdn.getStatus().equals("N")) {
                        completeDnNum++;
                        if ( mulitdn.getDN().getDN_SOURCE() == 3){
                            ArrayList<DNModel> dnModels = new ArrayList<>();
                            mulitdn.getDN().setSTATUS(DNStatusEnum.complete);
                            dnModels.add(mulitdn.getDN());
                            //插入数据
                            DbDnInfo.getInstance().InsertDNDB(dnModels);
                        }
                    }
                    if(mulitdn.getStatus().equals("F")){
                        completeDnNum++;

                        DbDnInfo.getInstance().DeleteDN(mulitdn.getDN().getAGENT_DN_NO(),false);
                        ArrayList<DNModel> dnModels=new ArrayList<>();
                        dnModels.add(mulitdn.getDN());
                        SendDNs.add(mulitdn.getDN());
                        DbDnInfo.getInstance().InsertDNDB(dnModels);
                        DbDnInfo.getInstance().ChangeDNStatusByDnNo(mulitdn.getDN().getAGENT_DN_NO(), DNStatusEnum.Sumbit);
                    }
                    if(mulitdn.getStatus().equals("S")){
                        if(mulitdn.getDN()!=null) {
                            dnno+="后台异常出库单："+Showdnno+"\n";
                            ArrayList<DNModel> dnModels = new ArrayList<>();
                            dnModels.add(mulitdn.getDN());
                            //删除异常数据，以下载为准
                          DbDnInfo.getInstance().DeleteDN(mulitdn.getDN().getAGENT_DN_NO(),false);
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
                String content=getString(R.string.Msg_DNSuccess)+completeDnNum+"\n" + dnno;
                MessageBox.Show(context, getString(R.string.Msg_bolkDNUploadSuccess)+"\n提交说明！\n"+content);
                DbLogInfo.getInstance().InsertLog(new LogModel("出库单批量提交结果",content,""));
            if(ftpNames.size()!=0) {
                 final  List<String> moveFiles=ftpNames;
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            LogUtil.WriteLog(Bulkupload.class, TAG_ExceptionDNList, "ftp_moveFile:" + moveFiles.toArray());
                            FtpUtil.FtpMoveFile(ParamaterModel.baseparaModel.getFtpModel(),moveFiles.toArray(new String[moveFiles.size()]));
                        } catch (Exception ex) {
                            LogUtil.WriteLog(Bulkupload.class, TAG_ExceptionDNList, "ftp_moveFile:" + ex.getMessage());

                        }
                    }
                }.start();
            }


                final  ArrayList<DNModel> ExportdnModels=SendDNs;
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(15000);
                         UploadDN.ExportDN(ExportdnModels, 0);
                         UploadDN.ExportDN(ExportdnModels, 1);
                        }catch (Exception ex){
                            LogUtil.WriteLog(Bulkupload.class, TAG_ExceptionDNList, "ExportDN:"+ex.getMessage());
                        }
                    }
                }.start();

              //  if(!TextUtils.isEmpty(dnno)){

               // }

            } else {

                MessageBox.Show(context, returnMsgModel.getMessage());
            }

        }catch (Exception ex){
            ToastUtil.show(ex.getMessage());
            LogUtil.WriteLog(Bulkupload.class,"Bulkupload-ExceptionDNList", ex.toString());
        }
        GetbulkuploadList(queryModel);
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

      //  DbDnInfo.getInstance().ChangeDNStatusByDnNo1();
        queryModel=null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, OrderFilter.class),1001);
            }
        });
        fabCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryModel=null;
                fabCancel.setVisibility(View.GONE);
                GetbulkuploadList(queryModel);
            }
        });

        edtDNNoFuilter.addTextChangedListener(bululoadTextWatcher);
        mSwipeLayout.setOnRefreshListener(this); //下拉刷新
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetbulkuploadList(queryModel);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1001 && resultCode==1){
            queryModel=data.getParcelableExtra("queryModel");
            if(fabCancel!=null)
                fabCancel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onRefresh() {
        mSwipeLayout.setRefreshing(false);
        queryModel=null;
        GetbulkuploadList(queryModel);
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
        String dnno=dnModel.getDN_SOURCE()==3 || dnModel.getDN_SOURCE()==5?dnModel.getCUS_DN_NO():dnModel.getAGENT_DN_NO();
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
            ArrayList<DNModel> postmodels=new ArrayList<>();
            boolean isUpload=true;
            boolean isFlag=false;
            for (int i = 0; i < DNModels.size(); i++) {
                if (bulkuploadListItemAdapter.getStates(i)) {
                    if(DNModels.get(i).getFlag()!=null && DNModels.get(i).getFlag()==1){
                        isFlag=true;
                        break;
                    }
                   DNModel postmodel = DbDnInfo.getInstance().AllPostDate(DNModels.get(i));
                    if (postmodel == null) {
                        MessageBox.Show(context, "出库单上报错误！\n" + DNModels.get(i).getAGENT_DN_NO());
                        isUpload=false;
                        break;
                    }

                    postmodels.add(postmodel);
                }
            }
            if(isFlag){
                MessageBox.Show(context, getString(R.string.Msg_miltuMaterial));
                return false;
            }
            if(isUpload && postmodels.size()!=0){
                final ArrayList<DNModel> uploadModels= postmodels;
                new AlertDialog.Builder(context).setTitle("提示")// 设置对话框标题
                        .setIcon(android.R.drawable.ic_dialog_info)// 设置对话框图
                        .setMessage(context.getResources().getString(R.string.Msg_Upload_DNSelf))
//                        .setPositiveButton("提交并关闭", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                UploadDN.UploadDNListToMaps(uploadModels,"F", mHandler);
//                            }
//                        })
                        .setNegativeButton("提交", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                UploadDN.UploadDNListToMaps(uploadModels,CBCloseDN.isChecked()?"F":"N", mHandler);
                            }
                        })
                        .show();
            }
        }
        if(item.getItemId()==R.id.action_select) {
            for(int i=0;i<DNModels.size();i++){
                bulkuploadListItemAdapter.modifyStates(i);
            }
        }
        return super.onOptionsItemSelected(item);
    }


    void GetbulkuploadList(QueryModel queryModel){
        try{
            DNModels =ImportbulkuploadList(queryModel);
            bulkuploadListItemAdapter=new BulkuploadListItemAdapter(context, DNModels);
            LsvExceptionList.setAdapter(bulkuploadListItemAdapter);
        }catch(Exception ex){
            MessageBox.Show(context,ex.toString());
        }
    }

    ArrayList<DNModel> ImportbulkuploadList(QueryModel queryModel){
        ArrayList<DNModel> DNModels =new ArrayList<>();
        DNModels = DbDnInfo.getInstance().GetLoaclcompleteDN(queryModel);
        return DNModels;
    }
}
