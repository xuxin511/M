package com.xx.chinetek.mitsubshi.Bulkupload;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.xx.chinetek.adapter.bulkupload.BulkuploadScanItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.mitsubshi.BaseIntentActivity;
import com.xx.chinetek.mitsubshi.Exception.ExceptionBarcodelist;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_bulkupload_scan)
public class BulkuploadScan extends BaseIntentActivity {

    Context context=BulkuploadScan.this;
    @ViewInject(R.id.txt_DnNo)
    TextView txtDnNo;
    @ViewInject(R.id.txt_Custom)
    TextView txtCustom;
    @ViewInject(R.id.CBCloseDN)
    CheckBox CBCloseDN;
    @ViewInject(R.id.lsv_DeliveryScan)
    ListView lsvDeliveryScan;

    BulkuploadScanItemAdapter bulkuploadScanItemAdapter;
    ArrayList<DNDetailModel> dnDetailModels;
    DNModel dnModel;
    DbDnInfo dnInfo;




    @Override
    protected void initViews() {
       super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.Bulkupload),true);
        x.view().inject(this);
        CBCloseDN.setVisibility(View.GONE);
    }

    @Override
    protected void initData() {
        super.initData();
        dnInfo=DbDnInfo.getInstance();
        dnModel=getIntent().getParcelableExtra("DNModel");
        txtDnNo.setText(getIntent().getStringExtra("DNNo"));
        txtCustom.setText(dnModel.getCUSTOM_NAME()==null || TextUtils.isEmpty(dnModel.getCUSTOM_NAME())?dnModel.getLEVEL_2_AGENT_NAME():dnModel.getCUSTOM_NAME());
        dnModel.__setDaoSession(dnInfo.getDaoSession());
    }

    @Override
    protected void onResume() {
        super.onResume();
        GetDeliveryOrderScanList();
      //  if (dnModel.getDETAILS() == null)
            dnModel.setDETAILS(new ArrayList<DNDetailModel>());
        dnModel.getDETAILS().addAll(dnDetailModels);
    }

    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemClickListener.class)
    private void lsvDeliveryScanonItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent=new Intent(context,ExceptionBarcodelist.class);
        intent.putExtra("position",position);
        intent.putExtra("DNno",dnModel.getAGENT_DN_NO());
        intent.putExtra("WinModel",0);
        startActivityLeft(intent);
    }
    int selectIndex=0;
    @Event(value = R.id.lsv_DeliveryScan,type = AdapterView.OnItemLongClickListener.class)
    private boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (i < 0) {
            MessageBox.Show(context, "请先选择操作的行！");
            return false;
        }
        final DNDetailModel detailModel= (DNDetailModel)bulkuploadScanItemAdapter.getItem(i);
        final  int position=i;
        if(detailModel.getFlag()!=null && detailModel.getFlag()==1) { //多条物料主数据
            final List<MaterialModel> materialModels = DbBaseInfo.getInstance().GetItemNames(detailModel.getGOLFA_CODE());
            if (materialModels.size() > 1) {
                String[] items = new String[materialModels.size()];
                for (int j = 0; j < materialModels.size(); j++) {
                    String item = "SAP号:" + materialModels.get(j).getMATNR() + "\n" + materialModels.get(j).getBISMT() + "\n"
                            + materialModels.get(j).getMAKTX();
                    items[j] = item;
                }
                selectIndex = 0;
                new AlertDialog.Builder(this)
                        .setTitle("选择物料")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        //设置普通文本格式的对话框，设置的是普通的Item；
                        .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                selectIndex = i;
                            }
                        })
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dnModel.getDETAILS().get(position).setITEM_NO(materialModels.get(selectIndex).getMATNR());
                                dnModel.getDETAILS().get(position).setITEM_NAME(materialModels.get(selectIndex).getMAKTX());
                                dnModel.getDETAILS().get(position).setGOLFA_CODE(materialModels.get(selectIndex).getBISMT());
                                dnModel.getDETAILS().get(position).setFlag(0);
                                try {
                                    Boolean isExcecption=false;
                                    for(int k=0;k<dnModel.getDETAILS().size();k++) {
                                        if (dnModel.getDETAILS().get(k).getFlag()!=null && dnModel.getDETAILS().get(k).getFlag() == 1) {
                                            isExcecption=true;
                                            break;
                                        }
                                    }
                                    if(!isExcecption) {
                                        dnModel.setFlag(0);
                                    }
                                    DbDnInfo.getInstance().InsertDNDetailDB(dnModel.getDETAILS().get(position));
                                    DbDnInfo.getInstance().InsertDNModel(dnModel);
                                    dnModel=DbDnInfo.getInstance().GetLoaclDN(dnModel.getAGENT_DN_NO());
                                    GetDeliveryOrderScanList();
                                }catch (Exception ex){
                                    MessageBox.Show(context,ex.getMessage());
                                }
                            }
                        })
                        .setNegativeButton("取消", null).show();
            }
        }
        return true;
    }

    /**
     * 绑定列表
     */
    void GetDeliveryOrderScanList(){
        dnDetailModels= DbDnInfo.getInstance().GetDNDetailByDNNo(dnModel.getAGENT_DN_NO());
        bulkuploadScanItemAdapter=new BulkuploadScanItemAdapter(context, dnDetailModels,dnModel.getDN_SOURCE());
        lsvDeliveryScan.setAdapter(bulkuploadScanItemAdapter);
    }

}
