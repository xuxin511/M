package com.xx.chinetek.mitsubshi.Bulkupload;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.xx.chinetek.adapter.bulkupload.BulkuploadListItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.Upload.UploadDN;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_UploadDN;

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

    int uploadIndex=0;
    DNModel postmodel;
   // String UploadDNno="";

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_UploadDN:
                UploadDN.AnalysisUploadDNToMapsJson(context, (String) msg.obj,postmodel);
                uploadIndex--;
                if(uploadIndex==0) {
                    MessageBox.Show(context,getString(R.string.Msg_DNUploadSuccess));
                    GetbulkuploadList();
                }
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____" + msg.obj);
                break;
        }

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
//        ImportDelivery();
        mSwipeLayout.setRefreshing(false);
    }


    @Event(value = R.id.edt_DNNoFuilter, type = View.OnKeyListener.class)
    private boolean edtDNNoFuilterOnkeyUp(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//            ExceptionListItemAdapter.getFilter().filter(edtDNNoFuilter.getText().toString());
        }
        return false;
    }


    @Event(value = R.id.Lsv_ExceptionList,type = AdapterView.OnItemLongClickListener.class)
    private boolean LsvItemlongClick(AdapterView<?> parent, View view, int position, long id) {
        try{
            DNModel  dnModel=(DNModel)bulkuploadListItemAdapter.getItem(position);
                if(dnModel.getFlag()==null||dnModel.getFlag().equals("0")){
                    dnModel.setFlag("1");
                }else{
                    dnModel.setFlag("0");
                }
            bulkuploadListItemAdapter.notifyDataSetInvalidated();
        }catch(Exception ex){
            MessageBox.Show(context,ex.toString());
        }
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
        intent.putExtra("DNNo",dnModel.getAGENT_DN_NO());
        startActivityLeft(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_querytitle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_Export){
            String DnNo="";
            for(int i=0;i<DNModels.size();i++){
                if(DNModels.get(i).getFlag()=="1"){
                    postmodel = DbDnInfo.getInstance().AllPostDate(DNModels.get(i));
                    if(postmodel==null){
                        DnNo+=DNModels.get(i).getAGENT_DN_NO()+"\n";
                    }else{
                        uploadIndex++;
                        UploadDN.UploadDNToMaps(postmodel,"N",mHandler);
                    }
                }
            }
            if(!TextUtils.isEmpty(DnNo))
                MessageBox.Show(context,"出库单号\n"+DnNo+"提交失败！");
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
