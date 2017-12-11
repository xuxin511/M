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
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_dnsync_list)
public class DNsync extends BaseActivity{

    Context context = DNsync.this;
    @ViewInject(R.id.Lsv_ExceptionList)
    ListView LsvExceptionList;

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
        GetbulkuploadList();
    }

    @Event(value = R.id.Lsv_ExceptionList,type = AdapterView.OnItemClickListener.class)
    private void LsvItemClick(AdapterView<?> parent, View view, int position, long id) {
        try{
            syncListItemAdapter.modifyStates(position);
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
                for(int i=0;i<DNModels.size();i++){
                    if (syncListItemAdapter.getStates(i)) {
                        Tempdnmodels.add(0, DNModels.get(i));
                    }
                }
                if(Tempdnmodels==null||Tempdnmodels.size()==0){
                    MessageBox.Show(context,"请先选择需要同步的单据！");
                    return false;
                }

                int size=Tempdnmodels.size();
                DbDnInfo dnInfo=DbDnInfo.getInstance();
                for(int i=0;i<size;i++) {
                    Tempdnmodels.get(i).__setDaoSession(dnInfo.getDaoSession());
                    String dnNo=Tempdnmodels.get(i).getDN_SOURCE()==3?Tempdnmodels.get(i).getCUS_DN_NO():Tempdnmodels.get(i).getAGENT_DN_NO();
                    DNModel dnModel = DbDnInfo.getInstance().GetLoaclDN(dnNo);
                    if(dnModel!=null) {
                        Tempdnmodels.get(i).setSTATUS( Tempdnmodels.get(i).getSTATUS()==-1?Tempdnmodels.get(i).getSTATUS():dnModel.getSTATUS());
                        Tempdnmodels.get(i).setAGENT_DN_NO(dnModel.getAGENT_DN_NO()); //自建单据保留原始系统单号
                        Tempdnmodels.get(i).setOPER_DATE(dnModel.getOPER_DATE());
                        Tempdnmodels.get(i).setOPER_DATE(dnModel.getOPER_DATE());
                        Tempdnmodels.get(i).setCUS_DN_NO(dnModel.getCUS_DN_NO());
                        Tempdnmodels.get(i).setREMARK(dnModel.getREMARK());
                        if(Tempdnmodels.get(i).getDETAILS()!=null) {
                            for (int j = 0; j < Tempdnmodels.get(i).getDETAILS().size(); j++) {
                                int scanQty = Tempdnmodels.get(i).getDETAILS().get(j).getSERIALS().size();
                                Tempdnmodels.get(i).getDETAILS().get(j).setSCAN_QTY(scanQty);
                                if(Tempdnmodels.get(i).getDN_SOURCE()==3) {
                                    Tempdnmodels.get(i).getDETAILS().get(j).setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
                                    List<DNScanModel> dnScanModels = Tempdnmodels.get(i).getDETAILS().get(j).getSERIALS();
                                    for (DNScanModel dnscanmodel : dnScanModels) {
                                        dnscanmodel.setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
                                    }
                                }
                            }
                        }
                    }
                }
                //插入数据
                DbDnInfo.getInstance().InsertDNDB(Tempdnmodels) ;
                closeActiviry();

            }catch(Exception ex){
                MessageBox.Show(context,ex.toString());
            }
        }
        return super.onOptionsItemSelected(item);
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
