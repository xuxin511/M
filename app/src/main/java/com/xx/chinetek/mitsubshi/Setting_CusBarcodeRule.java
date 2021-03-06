package com.xx.chinetek.mitsubshi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.xx.chinetek.adapter.BarcodeRuleItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.model.Base.BarcodeRule;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_setting__cus_barcode_rule)
public class Setting_CusBarcodeRule extends BaseActivity {

    Context context=Setting_CusBarcodeRule.this;
    @ViewInject(R.id.btn_SaveRule)
    Button btnSaveRule;
    @ViewInject(R.id.edt_KeyStart)
    EditText edtKeyStart;
    @ViewInject(R.id.edt_KeyEnd)
    EditText edtKeyEnd;
    @ViewInject(R.id.edt_SerialStart)
    EditText edtSerialStart;
    @ViewInject(R.id.edt_SerialEnd)
    EditText edtSerialEnd;
    @ViewInject(R.id.edt_otherStart)
    EditText edtotherStart;
    @ViewInject(R.id.edt_otherEnd)
    EditText edtotherEnd;
    @ViewInject(R.id.edt_BarcodeLength)
    EditText edtBarcodeLength;
    @ViewInject(R.id.lsv_Cusbarcode)
    ListView lsvCusbarcode;

    BarcodeRuleItemAdapter barcodeRuleItemAdapter;

    BarcodeRule barcodeRule;
    int position=0;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.login_setting_BarcodeRule),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
        barcodeRule=getIntent().getParcelableExtra("barcodeRule");
        position=getIntent().getIntExtra("position",0);
        if(barcodeRule!=null) {
            edtBarcodeLength.setText(barcodeRule.getBarcodeLength().toString());
            edtKeyStart.setText(barcodeRule.getKeyStartIndex().toString());
            edtKeyEnd.setText(barcodeRule.getKeyEndIndex().toString());
            edtSerialStart.setText(barcodeRule.getSerialStartIndex().toString());
            edtSerialEnd.setText(barcodeRule.getSerialEndIndex().toString());
        }
        BindView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting_cus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_Save){
            List<EditText> editTexts=new ArrayList<EditText>();
            editTexts.add(edtBarcodeLength);
            editTexts.add(edtKeyStart);
            editTexts.add(edtKeyEnd);
            editTexts.add(edtSerialStart);
            editTexts.add(edtSerialEnd);
            if(CheckEmpty(editTexts)) return true;
            if(CheckNumeric(editTexts)) return true;
            final  Integer barcodeLength=Integer.parseInt(edtBarcodeLength.getText().toString().trim());
            if(CheckLength(edtKeyStart,edtKeyEnd,barcodeLength)) return true;
            if(CheckLength(edtSerialStart,edtSerialEnd,barcodeLength)) return true;

            if(barcodeRule.getRuleName()!=null && !TextUtils.isEmpty(barcodeRule.getRuleName())){
                save(barcodeLength);
            }else {
                final EditText et = new EditText(this);
                et.setTextColor(getResources().getColor(R.color.black));
                new AlertDialog.Builder(this).setTitle(getString(R.string.Msg_Inputname))
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setView(et)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String name = et.getText().toString();
                                if (TextUtils.isEmpty(name))
                                    return;
                                barcodeRule.setRuleName(name);
                                save(barcodeLength);
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

            }
        }
        if(item.getItemId()==R.id.action_Clear){
            edtBarcodeLength.setText("0");
            edtKeyStart.setText("0");
            edtKeyEnd.setText("0");
            edtSerialStart.setText("0");
            edtSerialEnd.setText("0");
            barcodeRule=new BarcodeRule();
            BindView();
        }
        return super.onOptionsItemSelected(item);
    }

    void save(int barcodeLength){
        Integer KeyStartLength=Integer.parseInt(edtKeyStart.getText().toString().trim());
        Integer KeyEndLength=Integer.parseInt(edtKeyEnd.getText().toString().trim());
        Integer SerialStartLength=Integer.parseInt(edtSerialStart.getText().toString().trim());
        Integer SerialEndLength=Integer.parseInt(edtSerialEnd.getText().toString().trim());

        barcodeRule.setBarcodeLength(barcodeLength);
        barcodeRule.setKeyStartIndex(KeyStartLength);
        barcodeRule.setKeyEndIndex(KeyEndLength);
        barcodeRule.setSerialStartIndex(SerialStartLength);
        barcodeRule.setSerialEndIndex(SerialEndLength);
        Intent mIntent = new Intent();
        Bundle bundle=new Bundle();
        bundle.putParcelable("barcodeRule",barcodeRule);
        bundle.putInt("position",position);
        mIntent.putExtras(bundle);
        setResult(1, mIntent);
        closeActiviry();
    }

    @Event(R.id.btn_SaveRule)
    private void btnSaveRuleClick(View view){
        List<EditText> editTexts=new ArrayList<EditText>();
        editTexts.add(edtBarcodeLength);
        editTexts.add(edtotherStart);
        editTexts.add(edtotherEnd);
        if(CheckEmpty(editTexts)) return;
        if(CheckNumeric(editTexts)) return;

        Integer barcodeLength=Integer.parseInt(edtBarcodeLength.getText().toString().trim());
        if(CheckLength(edtotherStart,edtotherEnd,barcodeLength)) return;

        if(barcodeRule.getOtherColumn()==null)
            barcodeRule.setOtherColumn(new ArrayList<String>());
        if(barcodeRule.getOtherColumn().size()<=6) {
            barcodeRule.getOtherColumn().add(edtotherStart.getText() + "-" + edtotherEnd.getText());
            BindView();
        }else{
            MessageBox.Show(context,getString(R.string.Error_maxLength));
        }
        edtotherStart.setText("");
        edtotherEnd.setText("");
        CommonUtil.setEditFocus(edtotherStart);
    }

    void  BindView(){
        if(barcodeRule!=null) {
            barcodeRuleItemAdapter = new BarcodeRuleItemAdapter(context, barcodeRule.getOtherColumn());
            lsvCusbarcode.setAdapter(barcodeRuleItemAdapter);
        }

    }

    Boolean CheckLength(EditText edtstart,EditText edtend,Integer length){
        Integer Start=Integer.parseInt(edtstart.getText().toString().trim());
        Integer End=Integer.parseInt(edtend.getText().toString().trim());
        if(Start>End){
            MessageBox.Show(context,getString(R.string.Error_start_end));
            CommonUtil.setEditFocus(edtstart);
            return true;
        }
        if(End>length){
            MessageBox.Show(context,getString(R.string.Error_over_length));
            CommonUtil.setEditFocus(edtend);
            return true;
        }
        return false;
    }

    Boolean CheckEmpty(List<EditText> editTexts){
        Boolean isEmpty=false;
        for (EditText edt : editTexts) {
            if (TextUtils.isEmpty(edt.getText().toString().trim())) {
                MessageBox.Show(context, getString(R.string.Msg_notEmpty));
                CommonUtil.setEditFocus(edt);
                isEmpty=true;
                break;
            }
        }
        return isEmpty;
    }

    Boolean CheckNumeric(List<EditText> editTexts){
        Boolean isEmpty=false;
        for (EditText edt : editTexts) {
            if (!CommonUtil.isNumeric(edt.getText().toString().trim())) {
                MessageBox.Show(context, getString(R.string.Msg_inputNumic));
                CommonUtil.setEditFocus(edt);
                isEmpty=true;
                break;
            }
        }
        return isEmpty;
    }
}
