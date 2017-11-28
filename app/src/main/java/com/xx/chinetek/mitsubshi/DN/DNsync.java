package com.xx.chinetek.mitsubshi.DN;


import android.content.Context;
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
    SyncListItemAdapter syncListItemAdapter;



    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.DNsync),true);
        x.view().inject(this);
        edtDNNoFuilter.addTextChangedListener(syncdnNoTextWatcher);
        edtDNNoFuilter.setVisibility(View.GONE);
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


    /**
     * 文本变化事件
     */
    TextWatcher syncdnNoTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String filterContent=edtDNNoFuilter.getText().toString();
            if(!filterContent.equals(""))
                syncListItemAdapter.getFilter().filter(filterContent);

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };



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
                    if (syncListItemAdapter.getStates(i)) {
                        Tempdnmodels.add(0, DNModels.get(i));
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
            syncListItemAdapter=new SyncListItemAdapter(context, DNModels);
            LsvExceptionList.setAdapter(syncListItemAdapter);
        }catch(Exception ex){
            MessageBox.Show(context,ex.toString());
        }

    }

}
