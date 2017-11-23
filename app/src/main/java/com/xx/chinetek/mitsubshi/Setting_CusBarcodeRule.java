package com.xx.chinetek.mitsubshi;

import android.content.Context;

import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

@ContentView(R.layout.activity_setting__cus_barcode_rule)
public class Setting_CusBarcodeRule extends BaseActivity {

    Context context=Setting_CusBarcodeRule.this;
    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.login_setting_BarcodeRule),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
    }
}
