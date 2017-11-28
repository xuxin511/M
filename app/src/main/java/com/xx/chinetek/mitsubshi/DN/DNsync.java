package com.xx.chinetek.mitsubshi.DN;


import android.content.Context;
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
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.activity_dnsync_list)
public class DNsync extends BaseActivity{

    Context context = DNsync.this;
    @ViewInject(R.id.edt_DNNoFuilter)
    EditText edtDNNoFuilter;
    @ViewInject(R.id.Lsv_ExceptionList)
    ListView LsvExceptionList;

    ArrayList<DNModel> DNModels;
    BulkuploadListItemAdapter bulkuploadListItemAdapter;



    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.DNsync),true);
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
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan_title, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_submit){
            try{
                ArrayList<DNModel> Tempdnmodels= new ArrayList<DNModel>();
                for(int i=0;i<DNModels.size();i++){
                    if(DNModels.get(i).getFlag()=="1"){
                        Tempdnmodels.add(DNModels.get(i));
                    }
                }
                if(Tempdnmodels==null||Tempdnmodels.size()==0){
                    MessageBox.Show(context,"请先选择需要同步的单据！");
                    return false;
                }

                int size=Tempdnmodels.size();
                for(int i=0;i<size;i++) {
                    DNModel dnModel = DbDnInfo.getInstance().GetLoaclDN(Tempdnmodels.get(i).getAGENT_DN_NO());
                    if(dnModel!=null) {
                        Tempdnmodels.get(i).setSTATUS(dnModel.getSTATUS());
                        Tempdnmodels.get(i).setOPER_DATE(dnModel.getOPER_DATE());
                        Tempdnmodels.get(i).setOPER_DATE(dnModel.getOPER_DATE());
                        Tempdnmodels.get(i).setCUS_DN_NO(dnModel.getCUS_DN_NO());
                        Tempdnmodels.get(i).setREMARK(dnModel.getREMARK());
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
            bulkuploadListItemAdapter=new BulkuploadListItemAdapter(context, DNModels);
            LsvExceptionList.setAdapter(bulkuploadListItemAdapter);
        }catch(Exception ex){
            MessageBox.Show(context,ex.toString());
        }

    }

}
