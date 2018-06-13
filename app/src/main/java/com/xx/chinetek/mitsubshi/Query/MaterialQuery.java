package com.xx.chinetek.mitsubshi.Query;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.xx.chinetek.adapter.MaterialQueryItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.Network.NetworkError;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.dialog.ToastUtil;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.Upload.UploadDN;
import com.xx.chinetek.mitsubshi.BaseIntentActivity;
import com.xx.chinetek.mitsubshi.Exception.ExceptionBarcodelist;
import com.xx.chinetek.mitsubshi.Exception.ExceptionScan;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.DBReturnModel;

import org.greenrobot.greendao.query.QueryBuilder;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import static com.xx.chinetek.model.Base.TAG_RESULT.RESULT_UploadDN;
import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_ScanBarcode;

@ContentView(R.layout.activity_material_query)
public class MaterialQuery extends BaseIntentActivity {

    Context context=MaterialQuery.this;

    @ViewInject(R.id.lsv_MaterialQuery)
    ListView lsvMaterialQuery;
    @ViewInject(R.id.edt_ItemName)
    EditText edtItemName;
    @ViewInject(R.id.edt_Code)
    EditText edtCode;

    MaterialQueryItemAdapter materialQueryItemAdapter;
    List<MaterialModel> materialModels;

    @Override
    public void onHandleMessage(Message msg) {
        switch (msg.what) {
            case TAG_SCAN:
                try {
                    String bracode=(String) msg.obj;
                    materialModels = DbBaseInfo.getInstance().Querytems(bracode,bracode,bracode);
                    materialQueryItemAdapter=new MaterialQueryItemAdapter(context,materialModels);
                    lsvMaterialQuery.setAdapter(materialQueryItemAdapter);
                } catch (Exception ex) {
                    ToastUtil.show(ex.getMessage());
                    LogUtil.WriteLog(ExceptionScan.class,"ExceptionScan-CheckScanBarcode", ex.toString());
                }
                break;
        }
    }


    @Override
    protected void initViews() {
        super.initViews();
//        QueryBuilder.LOG_SQL = true;
//        QueryBuilder.LOG_VALUES = true;
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.MaterialQuery),true);
        x.view().inject(this);
    }

    @Event(value = {R.id.edt_ItemName,R.id.edt_Code}, type = View.OnKeyListener.class)
    private boolean edtOnKey(View v, int keyCode, KeyEvent event) {
        try{
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP)// 如果为Enter键
            {
                String itme=edtItemName.getText().toString().trim();
                String code=edtCode.getText().toString().trim();
                materialModels = DbBaseInfo.getInstance().Querytems(code,itme,code);
                materialQueryItemAdapter=new MaterialQueryItemAdapter(context,materialModels);
                lsvMaterialQuery.setAdapter(materialQueryItemAdapter);
                return true;
            }
        }catch(Exception ex){
            MessageBox.Show(context, ex.getMessage());
            CommonUtil.setEditFocus(v.getId()==R.id.edt_Code?edtCode:edtItemName);
            return true;
        }
        return false;
    }

    @Override
    protected void initData() {
        super.initData();
    }
}
