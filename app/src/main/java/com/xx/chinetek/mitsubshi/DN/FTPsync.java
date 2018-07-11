package com.xx.chinetek.mitsubshi.DN;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.xx.chinetek.method.Sync.SyncDN;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.activity_dnsync_list)
public class FTPsync extends BaseActivity{

    Context context = FTPsync.this;
    @ViewInject(R.id.Lsv_ExceptionList)
    ListView LsvExceptionList;
    @ViewInject(R.id.edt_DeleveryNoFuilter)
    EditText edtFuilter;

    ArrayList<DNModel> dnModels;
    SyncListItemAdapter syncListItemAdapter;



    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.FTPDNChoice),true);
        x.view().inject(this);
        edtFuilter.addTextChangedListener(DeleveryNoTextWatcher);
    }

    @Override
    protected void initData() {
        super.initData();
        try {
            dnModels = SyncDN.DNFromFiles();
            GetFTPloadList();
        }catch (Exception ex){
            MessageBox.Show(context, ex.toString());
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
            String filterContent=edtFuilter.getText().toString();
            if(!filterContent.equals(""))
                syncListItemAdapter.getFilter().filter(filterContent);
            else{
                GetFTPloadList();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    // 双击事件记录最近一次点击的ID
    private String  lastClickId;

    @Event(value = R.id.Lsv_ExceptionList,type = AdapterView.OnItemLongClickListener.class)
    private boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        if (position < 0) {
            MessageBox.Show(context, "请先选择操作的行！");
            return false;
        }
        DNModel dnModel = (DNModel) syncListItemAdapter.getItem(position);
        //判断单号是否在本地重复
        DNModel temp = DbDnInfo.getInstance().GetLoaclDN(dnModel.getAGENT_DN_NO());
        if (temp != null) {
            MessageBox.Show(context, getString(R.string.Msg_ExitDn) + dnModel.getAGENT_DN_NO());
            return false;
        }
        if (dnModel.getFlag() == 1) { //存在多条物料主数据
            Intent intent=new Intent(context,MultiMaterialSelect.class);
            Bundle bundle = new Bundle();
            bundle.putParcelable("DNModel", dnModel);
            intent.putExtras(bundle);
            startActivityForResult(intent,1001);
        }
        return true;
    }

    // 双击事件记录最近一次点击的时间
    private long lastClickTime;
    @Event(value = R.id.Lsv_ExceptionList,type = AdapterView.OnItemClickListener.class)
    private void LsvItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            DNModel dnModel = (DNModel) syncListItemAdapter.getItem(position);
            //判断单号是否在本地重复
            DNModel temp = DbDnInfo.getInstance().GetLoaclDN(dnModel.getAGENT_DN_NO());
            if (temp != null) {
                MessageBox.Show(context, getString(R.string.Msg_ExitDn) + dnModel.getAGENT_DN_NO());
                return;
            }
                if (dnModel.getAGENT_DN_NO().equals(lastClickId)
                        && (Math.abs(lastClickTime - System.currentTimeMillis()) < 1000)) {
                    lastClickId = null;
                    lastClickTime = 0;
                    ArrayList<DNModel> Tempdnmodels = new ArrayList<DNModel>();

                    Tempdnmodels.add(dnModel);
                    DownDN(Tempdnmodels);
                } else {
                    lastClickId = dnModel.getAGENT_DN_NO();
                    lastClickTime = System.currentTimeMillis();
                    syncListItemAdapter.modifyStates(position);
                }
        } catch (Exception ex) {
            ToastUtil.show(ex.getMessage());
            LogUtil.WriteLog(FTPsync.class,"FTPsync-LsvItemClick", ex.toString());
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1001 && resultCode==1){
            DNModel dnModel=data.getParcelableExtra("DNModel");
            int index=dnModels.indexOf(dnModel);
            if(index!=-1){
                dnModels.set(index,dnModel);
            }
            syncListItemAdapter = new SyncListItemAdapter(context, dnModels);
            LsvExceptionList.setAdapter(syncListItemAdapter);
        }
        super.onActivityResult(requestCode, resultCode, data);
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
            Boolean hasDnno=false;
            String contnt=getString(R.string.Msg_ExitDn) +"\n";
            for(int i=0;i<syncListItemAdapter.getCount();i++){
                //判断单号是否在本地重复
                DNModel temp = DbDnInfo.getInstance().GetLoaclDN(((DNModel) syncListItemAdapter.getItem(i)).getAGENT_DN_NO());
                if (temp != null) {
                    hasDnno=true;
                    contnt+= ((DNModel) syncListItemAdapter.getItem(i)).getAGENT_DN_NO() +"\n";
                }else {
                    syncListItemAdapter.modifyStates(i);
                }
            }
            if(hasDnno){
                MessageBox.Show(context,contnt);
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
        for(int i=0;i<size;i++) {
            DNModel dnModel = DbDnInfo.getInstance().GetLoaclDN(tempdnmodels.get(i).getAGENT_DN_NO());
            if(dnModel!=null) {
                tempdnmodels.get(i).setSTATUS(dnModel.getSTATUS());
                tempdnmodels.get(i).setOPER_DATE(dnModel.getOPER_DATE());
                tempdnmodels.get(i).setOPER_DATE(dnModel.getOPER_DATE());
                tempdnmodels.get(i).setCUS_DN_NO(dnModel.getCUS_DN_NO());
                tempdnmodels.get(i).setREMARK(dnModel.getREMARK());
            }
        }
        //插入数据
        DbDnInfo.getInstance().InsertDNDB(tempdnmodels) ;
        closeActiviry();
        return false;
    }


    void GetFTPloadList() {
        try {
            syncListItemAdapter = new SyncListItemAdapter(context, dnModels);
            LsvExceptionList.setAdapter(syncListItemAdapter);
        } catch (Exception ex) {
            MessageBox.Show(context, ex.toString());
        }

    }

}
