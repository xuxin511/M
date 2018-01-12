package com.xx.chinetek.mitsubshi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.model.Base.BarcodeRule;
import com.xx.chinetek.model.Base.CusBarcodeRule;
import com.xx.chinetek.model.Base.ParamaterModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_setting__barcode_rules)
public class Setting_BarcodeRules extends BaseActivity {

    Context context=Setting_BarcodeRules.this;
    @ViewInject(R.id.lsv_Rules)
    ListView lsvRules;

    CusBarcodeRule cusBarcodeRule;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.login_setting_BarcodeRule),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        if(ParamaterModel.baseparaModel.getCusBarcodeRule()==null){
            ParamaterModel.baseparaModel.setCusBarcodeRule(new CusBarcodeRule());
        }
        cusBarcodeRule= ParamaterModel.baseparaModel.getCusBarcodeRule();
        initView();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseActivity();
            }
        });
    }

    private void initView() {
        ArrayList<BarcodeRule> barcodeRules=new ArrayList<>();
        if(cusBarcodeRule.getBarcodeRules()!=null){
            barcodeRules=cusBarcodeRule.getBarcodeRules();
        }
        List<Map<String, Object>> RuleNames = new ArrayList<Map<String, Object>>();
        for (BarcodeRule rule:barcodeRules) {
            Map<String, Object> listem = new HashMap<String, Object>();
            listem.put("Name", rule.getRuleName());
            RuleNames.add(listem);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, RuleNames,
                R.layout.item_mail,new String[]{"Name"},new int[] {R.id.item_address});
        lsvRules.setAdapter(simpleAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            if( cusBarcodeRule.getBarcodeRules()==null) {
                cusBarcodeRule.setBarcodeRules(new ArrayList<BarcodeRule>());
            }
            Intent intent=new Intent(context,Setting_CusBarcodeRule.class);
            Bundle bundle=new Bundle();
            bundle.putParcelable("barcodeRule",new BarcodeRule());
            bundle.putInt("position",cusBarcodeRule.getBarcodeRules().size());
            intent.putExtras(bundle);
            startActivityForResult(intent,10011);
        }
        return super.onOptionsItemSelected(item);
    }

    @Event(value = R.id.lsv_Rules,type = AdapterView.OnItemClickListener.class)
    private void lsv_RulesItemClick(AdapterView<?> parent, View view, int position, long id) {
        BarcodeRule barcodeRule=cusBarcodeRule.getBarcodeRules().get(position);
        Intent intent=new Intent(context,Setting_CusBarcodeRule.class);
        Bundle bundle=new Bundle();
        bundle.putParcelable("barcodeRule",barcodeRule);
        bundle.putInt("position",position);
        intent.putExtras(bundle);
        startActivityForResult(intent,10011);
    }

    @Event(value = R.id.lsv_Rules,type = AdapterView.OnItemLongClickListener.class)
    private boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        final  int position=i;
        new AlertDialog.Builder(context).setCancelable(false).setTitle("提示")
                .setIcon(android.R.drawable.ic_dialog_info).setMessage("确认删除扫描规则？\n")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO 自动生成的方法
                        cusBarcodeRule.getBarcodeRules().remove(position);
                        initView();
                    }
                }).setNegativeButton("取消", null).show();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==10011  && resultCode==1){
            BarcodeRule barcodeRule=data.getParcelableExtra("barcodeRule");
            int position=data.getIntExtra("position",0);
            if( cusBarcodeRule.getBarcodeRules().size()==position) {
                cusBarcodeRule.getBarcodeRules().add(barcodeRule);
            }else{
                cusBarcodeRule.getBarcodeRules().set(position,barcodeRule);
            }

            initView();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode== KeyEvent.KEYCODE_BACK && event.getAction()==KeyEvent.ACTION_UP) {
            CloseActivity();
        }
        return true;
    }

    private void CloseActivity() {
        Intent mIntent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putParcelable("cusBarcodeRule",cusBarcodeRule);
        mIntent.putExtras(bundle);
        setResult(1, mIntent);
        closeActiviry();
    }


}
