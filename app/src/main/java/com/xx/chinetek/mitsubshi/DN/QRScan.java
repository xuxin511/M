package com.xx.chinetek.mitsubshi.DN;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.widget.TextView;

import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.Sync.SyncDN;
import com.xx.chinetek.mitsubshi.BaseIntentActivity;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DN.DNModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

@ContentView(R.layout.activity_qrscan)
public class QRScan extends BaseIntentActivity {

   Context context=QRScan.this;
    DNModel dnModel;


    @ViewInject(R.id.txt_DnNo)
    TextView txtDnNo;

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case TAG_SCAN:
                ImportQRScanInfo( (String) msg.obj);
                break;
        }
    }

    private void ImportQRScanInfo(String Barcode) {
        try {
            String fileName="DDN_Scan_QR.csv";
            String[] lines=Barcode.split("\n");
            Boolean isFormartCongif=false;
            String DNno ="";
            if(lines.length>2) {
                String[] cloumns=lines[1].toString().split(",");
                if(cloumns.length==8) {
                    File file = new File(ParamaterModel.DownDirectory + File.separator + fileName);
                    OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(file), "GBK");
                    BufferedWriter bw = new BufferedWriter(writerStream);
                    bw.write(Barcode);
                    bw.close();
                    SyncDN.DNFromFiles();
                    DNno = cloumns[0].toString();
                    isFormartCongif=true;
                }
            }
            if(!isFormartCongif){
                MessageBox.Show(context,getString(R.string.Msg_QRScanFormatr));
                return;
            }
            DNModel dnModel= DbDnInfo.getInstance().GetLoaclDN(DNno);
            if(dnModel!=null){
                Intent intent=new Intent(context,DeliveryScan.class);
                Bundle bundle=new Bundle();
                bundle.putParcelable("DNModel",dnModel);
                intent.putExtras(bundle);
                startActivityLeft(intent);
            }

        }catch (Exception  ex){
            MessageBox.Show(context,ex.getMessage());
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
