package com.xx.chinetek.mitsubshi.DN;


import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xx.chinetek.adapter.DN.SyncListItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
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

    ArrayList<DNModel> dnModels;
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
        GetFTPloadList();
    }

    // 双击事件记录最近一次点击的ID
    private String  lastClickId;

    // 双击事件记录最近一次点击的时间
    private long lastClickTime;
    @Event(value = R.id.Lsv_ExceptionList,type = AdapterView.OnItemClickListener.class)
    private void LsvItemClick(AdapterView<?> parent, View view, int position, long id) {
        try{
            DNModel dnModel=(DNModel) syncListItemAdapter.getItem(position);
            //判断单号是否在本地重复
           DNModel temp= DbDnInfo.getInstance().GetLoaclDN(dnModel.getAGENT_DN_NO());
           if(temp!=null){
               MessageBox.Show(context,getString(R.string.Msg_ExitDn)+dnModel.getAGENT_DN_NO());
               return;
           }

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
            MessageBox.Show(context,ex.toString());
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
                for(int i=0;i<dnModels.size();i++){
                    if (syncListItemAdapter.getStates(i)) {


                        Tempdnmodels.add(0, dnModels.get(i));
                    }
                }
                if (DownDN(Tempdnmodels)) return false;

            }catch(Exception ex){
                MessageBox.Show(context,ex.toString());
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
            dnModels = SyncDN.DNFromFiles();
            syncListItemAdapter = new SyncListItemAdapter(context, dnModels);
            LsvExceptionList.setAdapter(syncListItemAdapter);
        } catch (Exception ex) {
            MessageBox.Show(context, ex.toString());
        }

    }

}
