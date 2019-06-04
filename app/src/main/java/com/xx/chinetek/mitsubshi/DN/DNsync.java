package com.xx.chinetek.mitsubshi.DN;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.xx.chinetek.adapter.DN.SyncListItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.mitsubshi.OrderFilter;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.DNStatusEnum;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.QueryModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

@ContentView(R.layout.activity_dnsync_list)
public class DNsync extends BaseActivity{

    Context context = DNsync.this;
    @ViewInject(R.id.Lsv_ExceptionList)
    ListView LsvExceptionList;
    @ViewInject(R.id.edt_DeleveryNoFuilter)
    EditText edtFuilter;

    ArrayList<DNModel> DNModels;
    SyncListItemAdapter syncListItemAdapter;


    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.FTPDNChoice),true);
        x.view().inject(this);

    }

    @Override
    protected void initData() {
        super.initData();
        DNModels=getIntent().getParcelableArrayListExtra("DNModels");
        String dnDate=getIntent().getStringExtra("DNDate");
        if(DNModels!=null && DNModels.size()>0){
            if(DNModels.get(0).getDN_SOURCE()==5) {
//                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
//                Calendar c = Calendar.getInstance();
//                String datetime = sf.format(c.getTime());
                String datestr=dnDate.substring(0,4)+"-"+dnDate.substring(4,6)+"-"+dnDate.substring(6,8);
                edtFuilter.setText("SS-" + datestr + "-");
            }
        }
        edtFuilter.addTextChangedListener(DeleveryNoTextWatcher);
        GetbulkuploadList();

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
            String filterContent=edtFuilter.getText().toString();
            if(!filterContent.equals(""))
                syncListItemAdapter.getFilter().filter(filterContent);
            else{
                GetbulkuploadList();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // 双击事件记录最近一次点击的ID
    private String  lastClickId;

    // 双击事件记录最近一次点击的时间
    private long lastClickTime;

    @Event(value = R.id.Lsv_ExceptionList,type = AdapterView.OnItemClickListener.class)
    private void LsvItemClick(AdapterView<?> parent, View view, int position, long id) {
        try{
            DNModel dnModel=(DNModel) syncListItemAdapter.getItem(position);
            if(dnModel.getAGENT_DN_NO().equals(lastClickId)
                    && (Math.abs(lastClickTime-System.currentTimeMillis()) < 1000)){
                lastClickId = null;
                lastClickTime = 0;
                ArrayList<DNModel> Tempdnmodels= new ArrayList<DNModel>();

                Tempdnmodels.add(dnModel);
                DownDN(Tempdnmodels);
            }else{
                lastClickId = dnModel.getAGENT_DN_NO();
                lastClickTime = System.currentTimeMillis();
                syncListItemAdapter.modifyStates(position);
            }
        }catch(Exception ex){
            ToastUtil.show(ex.getMessage());
            LogUtil.WriteLog(DNsync.class,"DNsync-DownDN", ex.toString());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_down, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_submit){
            try{
                ArrayList<DNModel> Tempdnmodels= new ArrayList<DNModel>();
                for(int i=0;i<syncListItemAdapter.getCount();i++){
                    if (syncListItemAdapter.getStates(i)) {
                        Tempdnmodels.add(0, (DNModel) syncListItemAdapter.getItem(i));
                    }
                }
                if (DownDN(Tempdnmodels)) return false;

            }catch(Exception ex){
                MessageBox.Show(context,ex.toString());
            }
        }
        if(item.getItemId()==R.id.action_SelectAll){
            for(int i=0;i<syncListItemAdapter.getCount();i++){
                syncListItemAdapter.modifyStates(i);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean DownDN(ArrayList<DNModel> tempdnmodels) throws Exception {
        if(tempdnmodels ==null|| tempdnmodels.size()==0){
            MessageBox.Show(context,"请先选择需要同步的单据！");
            return true;
        }

        int size= tempdnmodels.size();
        DbDnInfo dnInfo=DbDnInfo.getInstance();
        for(int i=0;i<size;i++) {
            tempdnmodels.get(i).__setDaoSession(dnInfo.getDaoSession());
            String dnNo= tempdnmodels.get(i).getDN_SOURCE()==3 || tempdnmodels.get(i).getDN_SOURCE()==5? tempdnmodels.get(i).getCUS_DN_NO(): tempdnmodels.get(i).getAGENT_DN_NO();
            DNModel dnModel = DbDnInfo.getInstance().GetLoaclDN(dnNo);
            if(dnModel!=null) {
                tempdnmodels.get(i).setSTATUS( tempdnmodels.get(i).getSTATUS()==-1? tempdnmodels.get(i).getSTATUS():dnModel.getSTATUS());
               /// tempdnmodels.get(i).setAGENT_DN_NO(dnModel.getAGENT_DN_NO()); //自建单据保留原始系统单号
                tempdnmodels.get(i).setOPER_DATE(dnModel.getOPER_DATE());
                tempdnmodels.get(i).setOPER_DATE(dnModel.getOPER_DATE());
                tempdnmodels.get(i).setCUS_DN_NO(dnModel.getCUS_DN_NO());
                tempdnmodels.get(i).setREMARK(dnModel.getREMARK());
                if(tempdnmodels.get(i).getDETAILS()!=null) {
                    for (int j = 0; j < tempdnmodels.get(i).getDETAILS().size(); j++) {
                        int scanQty = tempdnmodels.get(i).getDETAILS().get(j).getSERIALS().size();
                        tempdnmodels.get(i).getDETAILS().get(j).setSCAN_QTY(scanQty);

                    }
                }
                if(tempdnmodels.get(i).getDN_SOURCE()==3|| tempdnmodels.get(i).getSTATUS()== DNStatusEnum.exeption) {
                    DbDnInfo.getInstance().DeleteDN(dnModel.getAGENT_DN_NO(),false);
                }
            }
        }
        //插入数据
        DbDnInfo.getInstance().InsertDNDB(tempdnmodels);
        if(tempdnmodels.size()==1){
            Intent intent = new Intent();
            intent.putExtra("DNModel", tempdnmodels.get(0));
            setResult(2, intent);
        }
        closeActiviry();
        return false;
    }


    void GetbulkuploadList(){
        try{
            syncListItemAdapter=new SyncListItemAdapter(context, DNModels);
            LsvExceptionList.setAdapter(syncListItemAdapter);
        }catch(Exception ex){
            MessageBox.Show(context,ex.toString());
        }

    }

}
