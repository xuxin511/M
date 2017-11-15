package com.xx.chinetek.mitsubshi.Query;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.xx.chinetek.adapter.DN.DeliveryListItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.mitsubshi.BaseIntentActivity;
import com.xx.chinetek.mitsubshi.DN.DeliveryScan;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNTypeModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

@ContentView(R.layout.activity_query_list)
public class QueryList extends BaseIntentActivity implements SwipeRefreshLayout.OnRefreshListener{

  Context context=QueryList.this;
    @ViewInject(R.id.edt_DeleveryNoFuilter)
    EditText edtDeleveryNoFuilter;
    @ViewInject(R.id.Lsv_DeliveryList)
    ListView LsvDeliveryList;
    @ViewInject(R.id.mSwipeLayout)
    SwipeRefreshLayout mSwipeLayout;
    ArrayList<DNModel> DNModels; //所有出库单

    DeliveryListItemAdapter deliveryListItemAdapter;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.Query),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        edtDeleveryNoFuilter.addTextChangedListener(DeleveryNoTextWatcher);
        mSwipeLayout.setOnRefreshListener(this); //下拉刷新
        BindListView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_querytitle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final String[] items=getResources().getStringArray(R.array.ExportTypeList);
        if(item.getItemId()==R.id.action_Export){
            new AlertDialog.Builder(context).setTitle(getResources().getString(R.string.Msg_Export_Type))// 设置对话框标题
                    .setIcon(android.R.drawable.ic_dialog_info)// 设置对话框图
                    .setCancelable(false)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 自动生成的方法存根
                            String select_item = items[which].toString();
                            dialog.dismiss();
                        }
                    }).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        BindListView();
        mSwipeLayout.setRefreshing(false);
    }

    @Event(value = R.id.Lsv_DeliveryList,type = AdapterView.OnItemClickListener.class)
    private void LsvDeliveryListonItemClick(AdapterView<?> parent, View view, int position, long id) {
        DNModel  dnModel=(DNModel)deliveryListItemAdapter.getItem(position);
        ParamaterModel.DnTypeModel=new DNTypeModel();
        ParamaterModel.DnTypeModel.setDNType(dnModel.getDN_SOURCE());
        switch (dnModel.getSTATUS()){
            case -1:
                break;
            case 1:
            case 2:
            case 3:
                Intent intent=new Intent(context,DeliveryScan.class);
                Bundle bundle=new Bundle();
                bundle.putParcelable("DNModel",dnModel);
                intent.putExtras(bundle);
                intent.putExtra("DNNo",dnModel.getAGENT_DN_NO());
                startActivityLeft(intent);
                break;
        }
    }

    void BindListView(){
        DNModels= DbDnInfo.getInstance().GetLoaclDNbyCondition();
        if(DNModels!=null) {
            deliveryListItemAdapter = new DeliveryListItemAdapter(context, DNModels);
            LsvDeliveryList.setAdapter(deliveryListItemAdapter);
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
            String filterContent=edtDeleveryNoFuilter.getText().toString();
            if(!filterContent.equals(""))
                deliveryListItemAdapter.getFilter().filter(filterContent);
            else{
                BindListView();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}
