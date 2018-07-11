package com.xx.chinetek.mitsubshi;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.xx.chinetek.adapter.DN.PartnerItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.method.GetPartner;
import com.xx.chinetek.model.Base.CustomModel;
import com.xx.chinetek.model.QueryModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

@ContentView(R.layout.activity_order_filter)
public class OrderFilter extends BaseActivity {

    Context context=OrderFilter.this;

    PartnerItemAdapter partnerItemAdapter;
    ArrayList<CustomModel> customModels;
    CustomModel customModel;//客户或代理商信息

    @ViewInject(R.id.edt_ContentText2)
    EditText edtContentText;
    @ViewInject(R.id.edt_StartTime)
    TextView edtStartTime;
    @ViewInject(R.id.edt_EndTime)
    TextView edtEndTime;
    @ViewInject(R.id.lsv_Partner)
    ListView lsvPartner;


    QueryModel queryModel;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.filter),true);
        x.view().inject(this);
        edtContentText.addTextChangedListener(CustomTextWatcher);
    }

    @Override
    protected void initData() {
        super.initData();
        customModel=null;
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        edtEndTime.setText(sf.format(c.getTime()));
        c.add(Calendar.DAY_OF_MONTH, -7);
        edtStartTime.setText(sf.format(c.getTime()));
        try {
            customModels = GetPartner.GetFilterCustom();
            InitListview();
        }catch (Exception ex){
            MessageBox.Show(context,ex.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_filter){
            queryModel = new QueryModel();
            queryModel.setStartTIme(edtStartTime.getText().toString().trim());
            queryModel.setEndTime(getDateStr(edtEndTime.getText().toString().trim(),1));
            queryModel.setCustomModel(customModel);

            Intent intent = new Intent();
            intent.putExtra("queryModel", queryModel);
            setResult(1, intent);
            closeActiviry();
        }
        return super.onOptionsItemSelected(item);
    }

    private void InitListview() {
        partnerItemAdapter=new PartnerItemAdapter(context, customModels);
        lsvPartner.setAdapter(partnerItemAdapter);
    }


    public static String getDateStr(String day,int Num) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        try {
            nowDate = df.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //如果需要向后计算日期 -改为+
        Date newDate2 = new Date(nowDate.getTime() + (long)Num * 24 * 60 * 60 * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateOk = simpleDateFormat.format(newDate2);
        return dateOk;
    }

    @Event(value = R.id.lsv_Partner,type =  AdapterView.OnItemClickListener.class)
    private void lsvPartneronItemClick(AdapterView<?> parent, View view, int position, long id) {
        customModel =(CustomModel) partnerItemAdapter.getItem(position);
        if(customModel !=null) {
            keyBoardCancle();
            edtContentText.setText(customModel.getCUSTOMER());
            CommonUtil.setEditFocus(edtContentText);
        }
    }

    @Event(R.id.edt_StartTime)
    private  void edtStartTimeClick(View view){
        Calendar calendar = Calendar.getInstance();
        AlertDialog dialog=new DatePickerDialog(context, 0, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String time=year+"-"+String.format("%02d", month+1)+"-"+String.format("%02d", day);
                edtStartTime.setText(time);
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    @Event(R.id.edt_EndTime)
    private  void edtEndTimeClick(View view){
        Calendar calendar = Calendar.getInstance();
        AlertDialog dialog=new DatePickerDialog(context, 0, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String time=year+"-"+String.format("%02d", month+1)+"-"+String.format("%02d", day);
                edtEndTime.setText(time);
            }
        },calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    /**
     * 文本变化事件
     */
    TextWatcher CustomTextWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if(!edtContentText.getText().toString().equals("")) {
                partnerItemAdapter.getFilter().filter(edtContentText.getText().toString());
            }
            else{
                InitListview();
                customModel=null;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Thread.sleep(500);
                        if(partnerItemAdapter.getCount()==1){
                            keyBoardCancle();
                        }
                    }catch (Exception ex){

                    }
                }
            }).start();

        }
    };



}
