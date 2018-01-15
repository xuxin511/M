package com.xx.chinetek.mitsubshi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.xx.chinetek.adapter.ExceptionListItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.model.ReturnMsgModelList;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.GsonUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.Sync.SyncDN;
import com.xx.chinetek.mitsubshi.DN.DNsync;
import com.xx.chinetek.mitsubshi.Exception.ExceptionScan;
import com.xx.chinetek.model.DN.DNModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import static com.xx.chinetek.method.Delscan.Delscan.DelDNmodel;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_SyncException;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_SyncException;

@ContentView(R.layout.activity_exception_list)
public class ExceptionList extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    Context context = ExceptionList.this;
    @ViewInject(R.id.edt_DNNoFuilter)
    EditText edtDNNoFuilter;
    @ViewInject(R.id.Lsv_ExceptionList)
    ListView LsvExceptionList;
    @ViewInject(R.id.mSwipeLayout)
    SwipeRefreshLayout mSwipeLayout;
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
//        GetExceptionList();
        edtDNNoFuilter.addTextChangedListener(ExceptionTextWatcher);
        mSwipeLayout.setOnRefreshListener(this); //下拉刷新
    }

    @Override
    public void onRefresh() {
//        ImportDelivery();
        SyncDN.SyncException(mHandler);
        mSwipeLayout.setRefreshing(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetExceptionList();
    }


    @Override
    public void onHandleMessage(Message msg) {
        try {
            switch (msg.what) {
                case RESULT_SyncException:
                    AnalysisSyncExceptionDNJson((String) msg.obj);
                    break;
                case NetworkError.NET_ERROR_CUSTOM:
                    ToastUtil.show("获取请求失败_____" + msg.obj);
                    break;
            }
        } catch (Exception ex) {
            ToastUtil.show(ex.getMessage());
            LogUtil.WriteLog(ExceptionList.class,"QueryList-SyncExceptionDN", ex.toString());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_exceptiontitle, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == R.id.action_Delete) {
                new AlertDialog.Builder(this).setTitle("提示")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setMessage(R.string.Dia_DeleteExDn)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                for (DNModel dnModel : DNModels) {
                                    //  if(dnModel.getSTATUS()!= DNStatusEnum.Sumbit)
                                    DbDnInfo.getInstance().DeleteDN(dnModel.getAGENT_DN_NO());
                                }
                                GetExceptionList();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }


        return super.onOptionsItemSelected(item);
    }



    /**
     * 异常同步出库单
     * @param result
     */
    public void AnalysisSyncExceptionDNJson(String result) throws Exception {
        LogUtil.WriteLog(SyncDN.class, TAG_SyncException, result);
        ReturnMsgModelList<DNModel> returnMsgModel = GsonUtil.getGsonUtil().fromJson(result, new TypeToken<ReturnMsgModelList<DNModel>>() {
        }.getType());
        if (returnMsgModel.getHeaderStatus().equals("S")) {
            ArrayList<DNModel> dnModels = returnMsgModel.getModelJson();
//            ArrayList<DNModel> SelectdnModels=new ArrayList<>();
//            int size=dnModels.size();
//            for(int i=0;i<size;i++){
//                if(DbDnInfo.getInstance().CheckDNInDB(dnModels.get(i).getAGENT_DN_NO()))
//                    SelectdnModels.add(dnModels.get(i));
//            }
            if(dnModels.size()!=0) {
                Intent intent = new Intent(context, DNsync.class);
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("DNModels", dnModels);
                intent.putExtras(bundle);
                startActivityLeft(intent);
            }
        } else {
            MessageBox.Show(context, returnMsgModel.getMessage());
        }
    }


    @Event(value = R.id.edt_DNNoFuilter, type = View.OnKeyListener.class)
    private boolean edtDNNoFuilterOnkeyUp(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
//            ExceptionListItemAdapter.getFilter().filter(edtDNNoFuilter.getText().toString());
        }
        return false;
    }


//    private int clickposition=-1;
    @Event(value = R.id.Lsv_ExceptionList,type = AdapterView.OnItemClickListener.class)
    private void LsvExceptionListonItemClick(AdapterView<?> parent, View view, int position, long id) {
        try{
            Intent intent=new Intent(context, ExceptionScan.class);
            Bundle bundle=new Bundle();
            DNModel Model= (DNModel)exceptionListItemAdapter.getItem(position);
            bundle.putParcelable("DNModel",Model);
            intent.putExtras(bundle);
            startActivityLeft(intent);

//            clickposition=position;
//            new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("请选择操作" )
//                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("确认删除扫描记录？\n")
//                                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                        @Override
//                                        public void onClick(DialogInterface dialog, int which) {
//                                            // TODO 自动生成的方法
//                                            //删除扫描记录，改变表头状态，改变明细数量
//                                            if(clickposition==-1){
//                                                MessageBox.Show(context,"请先选择操作的行！");
//                                                return;
//                                            }
//                                            DNModel Model= (DNModel)exceptionListItemAdapter.getItem(clickposition);
//                                            if(DbDnInfo.getInstance().DELscanbyagent(Model.getAGENT_DN_NO(),"")){
////                                                DbDnInfo.getInstance().UpdateDNmodelDetailNumberbyDN(Model.getAGENT_DN_NO(),"");
////                                                DbDnInfo.getInstance().UpdateDNmodelState(Model.getAGENT_DN_NO(),"2","");
//                                                //判断剩余的扫描数量
//                                                DbDnInfo.getInstance().UpdateDetailAllNum(Model.getAGENT_DN_NO(),0);
//                                                //需要改变主表状态
//                                                DbDnInfo.getInstance().UpdateDNmodelState(Model.getAGENT_DN_NO(),"3","");
//                                                MessageBox.Show(context,"删除成功！");
//                                                GetExceptionList();
//                                            }else{
//                                                MessageBox.Show(context,"删除失败！");
//                                            }
//                                        }
//                                    }).setNegativeButton("取消", null).show();
//                        }
//                    }).setNegativeButton("查看", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialogInterface, int i) {
//                    if(clickposition==-1){
//                        MessageBox.Show(context,"请先选择操作的行！");
//                        return;
//                    }
//                    Intent intent=new Intent(context, ExceptionScan.class);
//                    Bundle bundle=new Bundle();
//                    DNModel Model= (DNModel)exceptionListItemAdapter.getItem(clickposition);
//                    bundle.putParcelable("DNModel",Model);
//                    intent.putExtras(bundle);
//                    startActivityLeft(intent);
//
//                }
//            }).show();
        }catch(Exception ex){
            ToastUtil.show(ex.getMessage());
            LogUtil.WriteLog(ExceptionList.class,"QueryList-LsvExceptionListonItemClick", ex.toString());
        }


    }

    /**
     * 文本变化事件
     */
    TextWatcher ExceptionTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String filterContent=edtDNNoFuilter.getText().toString();
            if(!filterContent.equals(""))
                exceptionListItemAdapter.getFilter().filter(filterContent);
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
            exceptionListItemAdapter = new ExceptionListItemAdapter(context, DNModels);
            LsvExceptionList.setAdapter(exceptionListItemAdapter);
        }
    }

    private int clickpositionlong=-1;
    @Event(value = R.id.Lsv_ExceptionList,type = AdapterView.OnItemLongClickListener.class)
    private boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i < 0) {
            MessageBox.Show(context, "请先选择操作的行！");
            return false;
        }
        final DNModel Model = (DNModel) exceptionListItemAdapter.getItem(i);
        new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("确认删除扫描记录？\n")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 自动生成的方法
                        DelDNmodel(context,Model);
                        GetExceptionList();

                    }
                }).setNegativeButton("取消", null).show();
        return true;
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
        DNModels = DbDnInfo.getInstance().GetLoaclExceptDN();
        return DNModels;
    }
}
