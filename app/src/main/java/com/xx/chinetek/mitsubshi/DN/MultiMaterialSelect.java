package com.xx.chinetek.mitsubshi.DN;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.xx.chinetek.adapter.DN.DeliveryScanItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.method.DB.DbBaseInfo;
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

@ContentView(R.layout.activity_multi_material_select)
public class MultiMaterialSelect extends BaseActivity {

    Context context=MultiMaterialSelect.this;

    @ViewInject(R.id.lsv_Material)
    ListView lsvMaterial;
    @ViewInject(R.id.btn_Config)
    ListView btnConfig;

    DNModel dnModel;
    DeliveryScanItemAdapter deliveryScanItemAdapter;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.MaterialSelect),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        dnModel=getIntent().getParcelableExtra("DNModel");
        BindListview();
    }

    private void BindListview() {
        if(dnModel!=null) {
            ArrayList<DNDetailModel> dnDetailModels = (ArrayList<DNDetailModel>) dnModel.getDETAILS();
            deliveryScanItemAdapter = new DeliveryScanItemAdapter(context, dnDetailModels, dnModel.getDN_SOURCE());
            lsvMaterial.setAdapter(deliveryScanItemAdapter);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_UP) {
            checkDetails();
        }
        return true;
    }

    @Event(R.id.btn_Config)
    private void btnConfigClick(View view){
        checkDetails();
    }

    private void checkDetails() {
        ArrayList<DNDetailModel> dnDetailModels = (ArrayList<DNDetailModel>) dnModel.getDETAILS();
        Boolean isExcecption=false;
        for(int i=0;i<dnDetailModels.size();i++) {
            if (dnDetailModels.get(i).getFlag()!=null && dnDetailModels.get(i).getFlag() == 1) {
                isExcecption=true;
                break;
            }
        }
        if(!isExcecption) {
            dnModel.setFlag(0);
            ReturnActivity();
        }
        else{
            new AlertDialog.Builder(context).setCancelable(false).setTitle("提示")
                    .setIcon(android.R.drawable.ic_dialog_info).setMessage("存在未修改物料信息，是否退出？\n")
                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 自动生成的方法
                            ReturnActivity();
                        }
                    }).setNegativeButton("否", null).show();
        }
    }

    void ReturnActivity(){
        Intent mIntent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putParcelable("DNModel", dnModel);
        mIntent.putExtras(bundle);
        setResult(1, mIntent);
        closeActiviry();
    }

    int selectIndex=0;
    @Event(value = R.id.lsv_Material,type = AdapterView.OnItemClickListener.class)
    private void lsv_MaterialonItemClick(AdapterView<?> parent, View view, final int position, long id) {
        DNDetailModel DNdetailModel = (DNDetailModel) deliveryScanItemAdapter.getItem(position);
           final List<MaterialModel> materialModels = DbBaseInfo.getInstance().GetItems(DNdetailModel.getITEM_NO(), DNdetailModel.getITEM_NAME(), DNdetailModel.getGOLFA_CODE());
             if(materialModels.size()>1) {
                 String[] items = new String[materialModels.size()];
                 for (int i = 0; i < materialModels.size(); i++) {
                     String item = "SAP号:"+materialModels.get(i).getMATNR() + "\n" + materialModels.get(i).getBISMT() + "\n"
                             +materialModels.get(i).getMAKTX();
                     items[i] = item;
                 }
             selectIndex=0;
             new AlertDialog.Builder(this)
                     .setTitle("选择物料")
                     .setIcon(android.R.drawable.ic_dialog_info)
                     //设置普通文本格式的对话框，设置的是普通的Item；
                     .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                             selectIndex=i;
                         }
                     })
                     .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                         @Override
                         public void onClick(DialogInterface dialogInterface, int i) {
                             dnModel.getDETAILS().get(position).setITEM_NO(materialModels.get(selectIndex).getMATNR());
                             dnModel.getDETAILS().get(position).setITEM_NAME(materialModels.get(selectIndex).getMAKTX());
                             dnModel.getDETAILS().get(position).setGOLFA_CODE(materialModels.get(selectIndex).getBISMT());
                             dnModel.getDETAILS().get(position).setFlag(0);
                             BindListview();
                         }
                     })
                     .setNegativeButton("取消" ,  null ).show();
         }

    }
}
