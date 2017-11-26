package com.xx.chinetek.mitsubshi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.xx.chinetek.adapter.ToMailItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.base.ToolBarTitle;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.model.Base.ParamaterModel;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_setting__to_mail_list)
public class Setting_ToMailList extends BaseActivity {

    Context context=Setting_ToMailList.this;
    List<String> ToAdress;

    @ViewInject(R.id.edt_ToMail)
    EditText edtToMail;
    @ViewInject(R.id.lsv_TomailList)
    ListView lsvTomailList;

    ToMailItemAdapter toMailItemAdapter;

    @Override
    protected void initViews() {
        super.initViews();
        BaseApplication.toolBarTitle=new ToolBarTitle(getString(R.string.login_setting_ToMail),true);
        x.view().inject(this);
    }

    @Override
    protected void initData() {
        super.initData();
       if(ParamaterModel.baseparaModel.getMailModel()!=null)
            ToAdress= ParamaterModel.baseparaModel.getMailModel().getToAddress();
        if(ToAdress==null)  ToAdress=new ArrayList<>();
        toMailItemAdapter=new ToMailItemAdapter(context,ToAdress);
        lsvTomailList.setAdapter(toMailItemAdapter);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting_mail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.action_Save){
            Intent mIntent = new Intent();
            mIntent.putStringArrayListExtra("ToAdress",(ArrayList<String>) ToAdress);
            setResult(1, mIntent);
            closeActiviry();
        }

        return super.onOptionsItemSelected(item);
    }


    @Event(value = R.id.edt_ToMail, type = View.OnKeyListener.class)
    private boolean edtToMailOnkeyUp(View v, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                String mail = edtToMail.getText().toString();
                if(ToAdress.contains(mail))
                {
                    MessageBox.Show(context,getString(R.string.Msg_ToMailExit));
                    CommonUtil.setEditFocus(edtToMail);
                    return true;
                }
                ToAdress.add(mail);
            toMailItemAdapter=new ToMailItemAdapter(context,ToAdress);
            lsvTomailList.setAdapter(toMailItemAdapter);
            edtToMail.setText("");
            CommonUtil.setEditFocus(edtToMail);
                return  true;
        }
        return false;
    }

    @Event(value = R.id.lsv_TomailList,type = AdapterView.OnItemClickListener.class)
    private void lsvTomailListonItemClick(AdapterView<?> parent, View view, final int position, long id) {
        new AlertDialog.Builder(context)
                .setTitle("提示")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setMessage("确认该收件人地址？\n"+ToAdress.get(position))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ToAdress.remove(position);
                        toMailItemAdapter=new ToMailItemAdapter(context,ToAdress);
                        lsvTomailList.setAdapter(toMailItemAdapter);
                        edtToMail.setText("");
                        CommonUtil.setEditFocus(edtToMail);
                    }
                }).setNegativeButton("取消",null).show();
    }
}
