package com.xx.chinetek.mitsubshi.Exception;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xx.chinetek.adapter.Exception.ExceptionScanItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.Upload.UploadDN;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

import static com.xx.chinetek.method.Delscan.Delscan.DelDNDetailmodel;
import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_UploadDN;

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
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case RESULT_UploadDN:
                if(UploadDN.AnalysisUploadDNToMapsJson(context, (String) msg.obj,dnModel.getAGENT_DN_NO())){
                    closeActiviry();
                }
                break;
            case NetworkError.NET_ERROR_CUSTOM:
                ToastUtil.show("获取请求失败_____" + msg.obj);
                break;
        }
    }

    @Override
    protected void initViews() {
       super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.ScanDetails),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        dnInfo=DbDnInfo.getInstance();
        dnModel=getIntent().getParcelableExtra("DNModel");
        dnModel.__setDaoSession(dnInfo.getDaoSession());
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetDeliveryOrderScanList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_scan_title, menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_submit){
           DNModel postmodel = DbDnInfo.getInstance().AllPostDate(dnModel);
            if(postmodel==null){
                MessageBox.Show(context,"提交失败！");
            }else{
                UploadDN.SumbitDN(context,postmodel,mHandler);
            }

        }
        return super.onOptionsItemSelected(item);
    }


    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemClickListener.class)
    private void lsvDeliveryScanonItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(context,ExceptionBarcodelist.class);
        Bundle bundle=new Bundle();
        DNDetailModel DNdetailModel= (DNDetailModel)exceptionScanItemAdapter.getItem(position);
        bundle.putParcelable("DNdetailModel",DNdetailModel);
        bundle.putParcelable("DNModel",dnModel);
        intent.putExtras(bundle);
        startActivityLeft(intent);

    }




//    private int clickpositionlong=-1;
    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemLongClickListener.class)
    private boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i < 0) {
            MessageBox.Show(context, "请先选择操作的行！");
            return false;
        }
        final DNDetailModel detailModel= (DNDetailModel)exceptionScanItemAdapter.getItem(i);
        new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("确认删除扫描记录？\n")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 自动生成的方法
                        DelDNDetailmodel(detailModel,dnModel);
                        GetDeliveryOrderScanList();

                    }
                }).setNegativeButton("取消", null).show();
        return true;
    }


    void GetDeliveryOrderScanList(){
        dnDetailModels= DbDnInfo.getInstance().GetLoaclExceptionDetailsDN(dnModel.getAGENT_DN_NO().toString());
        int size=dnDetailModels.size();
        for(int i=0;i<size;i++){
            int scanQTY=DbDnInfo.getInstance().GetScanQtyInDNScanModel(dnDetailModels.get(i).getAGENT_DN_NO()
                    ,dnDetailModels.get(i).getGOLFA_CODE(),dnDetailModels.get(i).getLINE_NO());
            dnDetailModels.get(i).setSCAN_QTY(scanQTY);
        }
        dnModel.setDETAILS(dnDetailModels);
        exceptionScanItemAdapter=new ExceptionScanItemAdapter(context, dnDetailModels,dnModel.getDN_SOURCE());
        lsvDeliveryScan.setAdapter(exceptionScanItemAdapter);
    }

}
