package com.xx.chinetek.mitsubshi;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.xx.chinetek.adapter.ExceptionListItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.mitsubshi.DN.DeliveryScan;
import com.xx.chinetek.model.DN.DNModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.activity_exception_list)
public class ExceptionList extends BaseActivity {

    Context context = ExceptionList.this;
    @ViewInject(R.id.edt_DNNoFuilter)
    EditText edtDNNoFuilter;
    @ViewInject(R.id.Lsv_ExceptionList)
    ListView LsvExceptionList;

    ArrayList<DNModel> DNModels;
    ExceptionListItemAdapter exceptionListItemAdapter;
    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.exceptionList),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        GetExceptionList();
    }


    @Event(value = R.id.edt_DNNoFuilter, type = View.OnKeyListener.class)
    private boolean edtDNNoFuilterOnkeyUp(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {

        }
        return false;
    }

    @Event(value = R.id.Lsv_ExceptionList,type = AdapterView.OnItemClickListener.class)
    private void LsvExceptionListonItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(context,DeliveryScan.class);
        startActivityLeft(intent);
    }

    void GetExceptionList(){
        DNModels =ImportExceptionList();
        exceptionListItemAdapter=new ExceptionListItemAdapter(context, DNModels);
        LsvExceptionList.setAdapter(exceptionListItemAdapter);
    }

    ArrayList<DNModel> ImportExceptionList(){
        ArrayList<DNModel> DNModels =new ArrayList<>();
        for(int i=0;i<10;i++) {
            DNModel DNModel = new DNModel();
            DNModel.setAGENT_DN_NO("ck12345678"+i);
            DNModel.setDN_STATUS("异常");
            DNModel.setCUSTOM_NAME("收货方xxxxxxx");
            DNModel.setUPDATE_DATE(CommonUtil.dateStrConvertDate("2017-10-30",null));
            DNModel.setDN_SOURCE("MAPS");
            DNModel.setUPDATE_USER("10001");
            DNModels.add(DNModel);
        }
        return DNModels;
    }
}
