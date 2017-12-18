package com.xx.chinetek.adapter.Exception;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNScanModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GHOST on 2017/1/13.
 */

public class ExceptionScanbarcodeAdapter extends BaseAdapter {
    private Context context; // 运行上下文
    private ArrayList<DNScanModel> DNScanModels; // 信息集合
    private LayoutInflater listContainer; // 视图容器
    private List<Boolean> listselected;//用布尔型的list记录每一行的选中状态

    public final class ListItemView { // 自定义控件集合

        public TextView txtbarcode;
        public TextView txtnumber;
        public TextView txtreason;
    }

    public ExceptionScanbarcodeAdapter(Context context, ArrayList<DNScanModel> DNScanModels) {
        this.context = context;
        listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.DNScanModels = DNScanModels;
        this.setListselected(new ArrayList<Boolean>(getCount()));
        int size=getCount();
        for(int i=0;i<size;i++) {
            getListselected().add(!( DNScanModels.get(i).getSTATUS()!=null && DNScanModels.get(i).getSTATUS().equals("0")));//初始为false，长度和listview一样
        }
    }

    public List<Boolean> getListselected() {
        return listselected;
    }
    public void setListselected(List<Boolean> listselected) {
        this.listselected = listselected;
    }
    public Boolean getStates(int position){
        return getListselected().get(position);
    }

    public void modifyStates(int position){
        if(!getListselected().get(position)){
            getListselected().set(position, true);//如果相应position的记录是未被选中则设置为选中（true）
            notifyDataSetChanged();
        }else{
            getListselected().set(position, false);//否则相应position的记录是被选中则设置为未选中（false）
            notifyDataSetChanged();}
    }

    @Override
    public int getCount() {
        return DNScanModels ==null?0: DNScanModels.size();
    }

    @Override
    public Object getItem(int position) {
        return DNScanModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int selectID = position;
        // 自定义视图
        ListItemView listItemView = null;
        if (convertView == null) {
            listItemView = new ListItemView();

            // 获取list_item布局文件的视图
            convertView = listContainer.inflate(R.layout.item_exception_scan,null);
            listItemView.txtbarcode = (TextView) convertView.findViewById(R.id.item_barcode);
            listItemView.txtnumber = (TextView) convertView.findViewById(R.id.item_number);
            listItemView.txtreason = (TextView) convertView.findViewById(R.id.item_reason);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
        DNScanModel dnScanModel = DNScanModels.get(selectID);
        listItemView.txtbarcode.setText(dnScanModel.getSERIAL_NO());
        listItemView.txtnumber.setText("数量：1");
        listItemView.txtreason.setText((dnScanModel.getSTATUS().equals("0")?"正常":dnScanModel.getSTATUS().equals("1")?"序列号重复":"数量超出"));
        if (getListselected().get(position)) {
            convertView.setBackgroundResource(R.color.lightgreen);
        } else {
            convertView.setBackgroundResource(R.color.trans);
        }
        return convertView;
    }



}
