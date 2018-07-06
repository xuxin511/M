package com.xx.chinetek.mitsubshi.DN;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;

import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.Sync.SyncDN;
import com.xx.chinetek.mitsubshi.BaseIntentActivity;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DN.DNModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

@ContentView(R.layout.activity_qrscan)
public class QRScan extends BaseIntentActivity {

   Context context=QRScan.this;
    ArrayList<DNModel>  dnModels;

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
            if(lines.length>1) {
                String[] cloumns=lines[1].toString().split(",");
                if(cloumns.length==8) {
                    DNno = cloumns[0].toString();
                    //判断单号是否在本地重复
                    DNModel temp= DbDnInfo.getInstance().GetLoaclDN(DNno);
                    if(temp!=null){
                        MessageBox.Show(context,getString(R.string.Msg_ExitDn)+DNno);
                        return;
                    }
                    File file = new File(ParamaterModel.DownDirectory + File.separator + fileName);
                    OutputStreamWriter writerStream = new OutputStreamWriter(new FileOutputStream(file), "GBK");
                    BufferedWriter bw = new BufferedWriter(writerStream);
                    bw.write(Barcode);
                    bw.close();
                    dnModels= SyncDN.DNFromFiles();
                   if(dnModels.size()==0){
                       return;
                   }
                    isFormartCongif=true;

                }
            }
            if(!isFormartCongif){
                MessageBox.Show(context,getString(R.string.Msg_QRScanFormatr));
                return;
            }
            if(dnModels.size()==1){
                if(dnModels.get(0).getFlag()==1){
                    Intent intent=new Intent(context,MultiMaterialSelect.class);
                    Bundle bundle = new Bundle();
                    bundle.putParcelable("DNModel", dnModels.get(0));
                    intent.putExtras(bundle);
                    startActivityForResult(intent,1001);
                }else{
                    startActivity(DNno);
                  //  closeActiviry();
                }
            }
        }catch (Exception  ex){
            ToastUtil.show(ex.getMessage());
            LogUtil.WriteLog(QRScan.class,"QRScan-ImportQRScanInfo", ex.toString());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1001 && resultCode==1){
            try {
                DNModel dnModel = data.getParcelableExtra("DNModel");
                if (dnModel.getFlag() == 1) {
                    MessageBox.Show(context,getString(R.string.Msg_QRMulitMaterial));
                } else {
                    dnModels.set(0, dnModel);
                    startActivity(dnModel.getAGENT_DN_NO());
                }
            }catch (Exception  ex){
                MessageBox.Show(context,ex.getMessage());
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void startActivity(String DNno) throws Exception{
        DbDnInfo.getInstance().InsertDNDB(dnModels);
        DNModel dnModel= DbDnInfo.getInstance().GetLoaclDN(DNno);
        if(dnModel!=null){
            Intent intent=new Intent(context,DeliveryScan.class);
            Bundle bundle=new Bundle();
            bundle.putParcelable("DNModel",dnModel);
            intent.putExtras(bundle);
            startActivityLeft(intent);
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
