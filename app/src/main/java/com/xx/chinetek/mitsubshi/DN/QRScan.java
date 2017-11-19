package com.xx.chinetek.mitsubshi.DN;

import android.content.Context;
import android.os.Message;

import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.mitsubshi.BaseIntentActivity;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNTypeModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

@ContentView(R.layout.activity_qrscan)
public class QRScan extends BaseIntentActivity {

   Context context=QRScan.this;

   DNTypeModel dnTypeModel;

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case TAG_SCAN:
                    String Barcode=(String) msg.obj;
                break;
        }
    }

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.qrscan),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
    }
}
