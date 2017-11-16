package com.xx.chinetek.mitsubshi.Exception;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.xx.chinetek.adapter.Exception.ExceptionScanItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_exception_scan)
public class ExceptionScan extends BaseActivity {

    Context context=ExceptionScan.this;
    @ViewInject(R.id.lsv_DeliveryScan)
    ListView lsvDeliveryScan;

    ExceptionScanItemAdapter exceptionScanItemAdapter;
    ArrayList<DNDetailModel> dnDetailModels;
    DNModel dnModel;
    DbDnInfo dnInfo;

    @Override
    protected void initViews() {
       super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.ExceptionScan),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        dnInfo=DbDnInfo.getInstance();
        dnModel=getIntent().getParcelableExtra("DNModel");
        GetDeliveryOrderScanList();
        if (dnModel.getDETAILS() == null)
            dnModel.setDETAILS(new ArrayList<DNDetailModel>());
        dnModel.getDETAILS().addAll(dnDetailModels);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dnInfo=DbDnInfo.getInstance();
        dnModel=getIntent().getParcelableExtra("DNModel");
        GetDeliveryOrderScanList();
        if (dnModel.getDETAILS() == null)
            dnModel.setDETAILS(new ArrayList<DNDetailModel>());
        dnModel.getDETAILS().addAll(dnDetailModels);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan_title, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_submit){

        }
        return super.onOptionsItemSelected(item);
    }

//    private int clickposition=-1;
    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemClickListener.class)
    private void lsvDeliveryScanonItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(context,ExceptionBarcodelist.class);
        Bundle bundle=new Bundle();
        DNDetailModel DNdetailModel= (DNDetailModel)exceptionScanItemAdapter.getItem(position);
        bundle.putParcelable("DNdetailModel",DNdetailModel);
        bundle.putParcelable("DNModel",dnModel);
        intent.putExtras(bundle);
        startActivityLeft(intent);

//        clickposition=position;
//        new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("请选择操作" )
//                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                    new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("是否删除扫描记录？\n")
//                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO 自动生成的方法
//                            //删除扫描记录，改变明细数量
//                            if(clickposition==-1){
//                                MessageBox.Show(context,"请先选择操作的行！");
//                                return;
//                            }
//                            DNDetailModel Model= (DNDetailModel)exceptionScanItemAdapter.getItem(clickposition);
//                            if(DbDnInfo.getInstance().DELscanbyagentdetail(Model,"")){
////                                DbDnInfo.getInstance().UpdateDNmodelDetailNumberbyGOLFACODE(Model,"");
//                                //判断剩余的扫描数量
//                                Integer lastNum=DbDnInfo.getInstance().GetLoaclDNScanModelDNNum(Model.getAGENT_DN_NO(),Model.getGOLFA_CODE());
//                                DbDnInfo.getInstance().UpdateDetailNum(Model.getAGENT_DN_NO(),Model.getGOLFA_CODE(),lastNum);
//                                if(lastNum==0){
//                                    //需要改变主表状态
//                                    DbDnInfo.getInstance().UpdateDNmodelState(Model.getAGENT_DN_NO(),"3","");
//                                }
//                                MessageBox.Show(context,"删除成功！");
//                                GetDeliveryOrderScanList();
//                            }else{
//                                MessageBox.Show(context,"删除失败！");
//                            }
//                        }
//                    }).setNegativeButton("取消", null).show();
//
//                    }
//                }).setNegativeButton("查看", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                if(clickposition==-1){
//                    MessageBox.Show(context,"请先选择操作的行！");
//                    return;
//                }
//                Intent intent=new Intent(context,ExceptionBarcodelist.class);
//                Bundle bundle=new Bundle();
//                DNDetailModel DNdetailModel= (DNDetailModel)exceptionScanItemAdapter.getItem(clickposition);
//                bundle.putParcelable("DNdetailModel",DNdetailModel);
//                bundle.putParcelable("DNModel",dnModel);
//                intent.putExtras(bundle);
//                startActivityLeft(intent);
//            }
//        }).show();

    }




    private int clickpositionlong=-1;
    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemLongClickListener.class)
    private boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        try{
            clickpositionlong=i;
            new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("是否删除扫描记录？\n")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 自动生成的方法
                            //删除扫描记录，改变明细数量
                            if(clickpositionlong==-1){
                                MessageBox.Show(context,"请先选择操作的行！");
                                return;
                            }
                            DNDetailModel Model= (DNDetailModel)exceptionScanItemAdapter.getItem(clickpositionlong);
                            if(DbDnInfo.getInstance().DELscanbyagentdetail(Model,"")){
//                                DbDnInfo.getInstance().UpdateDNmodelDetailNumberbyGOLFACODE(Model,"");
                                //判断剩余的扫描数量
                                Integer lastNum=DbDnInfo.getInstance().GetLoaclDNScanModelDNNum(Model.getAGENT_DN_NO(),Model.getGOLFA_CODE(),Model.getLINE_NO());
                                DbDnInfo.getInstance().UpdateDetailNum(Model.getAGENT_DN_NO(),Model.getGOLFA_CODE(),Model.getLINE_NO(),lastNum,dnModel.getDN_SOURCE());
                                if(DbDnInfo.getInstance().GetLoaclDNScanModelDNNumbyDNNO(Model.getAGENT_DN_NO())==0){
                                    //需要改变主表状态
                                    DbDnInfo.getInstance().UpdateDNmodelState(Model.getAGENT_DN_NO(),"1","",dnModel.getDN_SOURCE());
                                }
                                MessageBox.Show(context,"删除成功！");
                                GetDeliveryOrderScanList();
                            }else{
                                MessageBox.Show(context,"删除失败！");
                            }
                        }
                    }).setNegativeButton("取消", null).show();

        }catch(Exception ex){
            MessageBox.Show(context,ex.toString());
        }
        return true;

    }


    void GetDeliveryOrderScanList(){
        dnDetailModels= DbDnInfo.getInstance().GetLoaclExceptionDetailsDN(dnModel.getAGENT_DN_NO().toString());
        exceptionScanItemAdapter=new ExceptionScanItemAdapter(context, dnDetailModels);
        lsvDeliveryScan.setAdapter(exceptionScanItemAdapter);
    }

}
