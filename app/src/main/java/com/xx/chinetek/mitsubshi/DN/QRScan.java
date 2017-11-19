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
                try {
                    String msgstr=(String) msg.obj;
                   // String obj =new String(msgstr.getBytes("GBK"), "UTF-8");
                    String obj =decode(msgstr);
                            //URLEncoder.encode(, "GBK");

                }catch (Exception  ex){

                }
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
        dnTypeModel=getIntent().getParcelableExtra("DNType");
    }

    public static String decode(String unicodeStr) {
        if (unicodeStr == null) {
            return null;
        }
        StringBuffer retBuf = new StringBuffer();
        int maxLoop = unicodeStr.length();
        for (int i = 0; i < maxLoop; i++) {
            if (unicodeStr.charAt(i) == '\\') {
                if ((i < maxLoop - 5) && ((unicodeStr.charAt(i + 1) == 'u') || (unicodeStr.charAt(i + 1) == 'U')))
                    try {
                        retBuf.append((char) Integer.parseInt(unicodeStr.substring(i + 2, i + 6), 16));
                        i += 5;
                    } catch (NumberFormatException localNumberFormatException) {
                        retBuf.append(unicodeStr.charAt(i));
                    }
                else
                    retBuf.append(unicodeStr.charAt(i));
            } else {
                retBuf.append(unicodeStr.charAt(i));
            }
        }
        return retBuf.toString();
    }
}
