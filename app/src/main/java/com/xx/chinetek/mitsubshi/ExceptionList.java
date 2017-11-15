package com.xx.chinetek.mitsubshi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;


import com.xx.chinetek.adapter.ExceptionListItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.mitsubshi.Exception.ExceptionScan;
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

    @Override
    protected void onResume() {
        super.onResume();
        GetExceptionList();
    }

    @Event(value = R.id.edt_DNNoFuilter, type = View.OnKeyListener.class)
    private boolean edtDNNoFuilterOnkeyUp(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//            ExceptionListItemAdapter.getFilter().filter(edtDNNoFuilter.getText().toString());
        }
        return false;
    }


    private int clickposition=-1;
    @Event(value = R.id.Lsv_ExceptionList,type = AdapterView.OnItemClickListener.class)
    private void LsvExceptionListonItemClick(AdapterView<?> parent, View view, int position, long id) {
        try{
            clickposition=position;
            new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("请选择操作" )
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("确认删除扫描记录？\n")
                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO 自动生成的方法
                                            //删除扫描记录，改变表头状态，改变明细数量
                                            if(clickposition==-1){
                                                MessageBox.Show(context,"请先选择操作的行！");
                                                return;
                                            }
                                            DNModel Model= (DNModel)exceptionListItemAdapter.getItem(clickposition);
                                            if(DbDnInfo.getInstance().DELscanbyagent(Model.getAGENT_DN_NO(),"")){
//                                                DbDnInfo.getInstance().UpdateDNmodelDetailNumberbyDN(Model.getAGENT_DN_NO(),"");
//                                                DbDnInfo.getInstance().UpdateDNmodelState(Model.getAGENT_DN_NO(),"2","");
                                                //判断剩余的扫描数量
                                                DbDnInfo.getInstance().UpdateDetailAllNum(Model.getAGENT_DN_NO(),0);
                                                //需要改变主表状态
                                                DbDnInfo.getInstance().UpdateDNmodelState(Model.getAGENT_DN_NO(),"3","");
                                                MessageBox.Show(context,"删除成功！");
                                                GetExceptionList();
                                            }else{
                                                MessageBox.Show(context,"删除失败！");
                                            }

                                        }
                                    }).setNegativeButton("取消", null).show();
                        }
                    }).setNegativeButton("查看", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if(clickposition==-1){
                        MessageBox.Show(context,"请先选择操作的行！");
                        return;
                    }
                    Intent intent=new Intent(context, ExceptionScan.class);
                    Bundle bundle=new Bundle();
                    DNModel Model= (DNModel)exceptionListItemAdapter.getItem(clickposition);
                    bundle.putParcelable("DNModel",Model);
                    intent.putExtras(bundle);
                    startActivityLeft(intent);

                }
            }).show();

        }catch(Exception ex){
            MessageBox.Show(context,ex.toString());
        }

    }



    @Event(value = R.id.Lsv_ExceptionList,type = AdapterView.OnItemLongClickListener.class)
    private void LsvExceptionListonItemlongClick(AdapterView<?> parent, View view, int position, long id) {
        try{

            MessageBox.Show(context,"请先选择操作的行！");

//            clickposition=position;
//            new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("是否删除扫描记录？\n")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO 自动生成的方法
//                           //删除扫描记录，改变表头状态，改变明细数量
//                            if(clickposition==-1){
//                                MessageBox.Show(context,"请先选择操作的行！");
//                                return;
//                            }
//                            DNModel Model= (DNModel)exceptionListItemAdapter.getItem(clickposition);
//                            if(DbDnInfo.getInstance().DELscanbyagent(Model.getAGENT_DN_NO(),"")){
//                                DbDnInfo.getInstance().UpdateDNmodelDetailNumberbyDN(Model.getAGENT_DN_NO(),"");
//                                DbDnInfo.getInstance().UpdateDNmodelState(Model.getAGENT_DN_NO(),"2","");
//                            }
//
//                        }
//                    }).setNegativeButton("取消", null).show();


        }catch(Exception ex){
            MessageBox.Show(context,ex.toString());
        }


    }


    void GetExceptionList(){
        try{
            DNModels =ImportExceptionList();
            exceptionListItemAdapter=new ExceptionListItemAdapter(context, DNModels);
            LsvExceptionList.setAdapter(exceptionListItemAdapter);
        }catch(Exception ex){
            MessageBox.Show(context,ex.toString());
        }

    }

    ArrayList<DNModel> ImportExceptionList(){
        ArrayList<DNModel> DNModels =new ArrayList<>();
        DNModels = DbDnInfo.getInstance().GetLoaclDN();
        return DNModels;
    }
}