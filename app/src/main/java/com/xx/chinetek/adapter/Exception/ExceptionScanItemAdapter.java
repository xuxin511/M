package com.xx.chinetek.adapter.Exception;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNDetailModel;

import java.util.ArrayList;

/**
 * Created by GHOST on 2017/1/13.
 */

public class ExceptionScanItemAdapter extends BaseAdapter {
    private Context context; // 运行上下文
    private ArrayList<DNDetailModel> dnDetailModels; // 信息集合
    private LayoutInflater listContainer; // 视图容器
    private Integer dnsource=0;


    public final class ListItemView { // 自定义控件集合

        public TextView txtItemNo;
        public TextView txtRowNo;
        public TextView txtItemName;
        public TextView txtDNQty;
        public TextView txtScanQty ;
    }

    public ExceptionScanItemAdapter(Context context, ArrayList<DNDetailModel> dnDetailModels, Integer SOURCE) {
        dnsource=SOURCE;
        this.context = context;
        listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.dnDetailModels = dnDetailModels;

    }


    @Override
    public int getCount() {
        return dnDetailModels ==null?0: dnDetailModels.size();
    }

    @Override
    public Object getItem(int position) {
        return dnDetailModels.get(position);
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
            convertView = listContainer.inflate(R.layout.item_exceptiony_scan,null);
            listItemView.txtItemNo = (TextView) convertView.findViewById(R.id.item_barcode);
            listItemView.txtRowNo = (TextView) convertView.findViewById(R.id.item_RowNo);
            listItemView.txtItemName = (TextView) convertView.findViewById(R.id.item_ItemName);
            listItemView.txtDNQty = (TextView) convertView.findViewById(R.id.item_reason);
            listItemView.txtScanQty = (TextView) convertView.findViewById(R.id.item_ScanQty);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
        DNDetailModel dnDetailModel = dnDetailModels.get(selectID);
        if(dnDetailModel.getSCAN_QTY()==null)
            dnDetailModel.setSCAN_QTY(0);
        listItemView.txtItemNo.setText(dnDetailModel.getGOLFA_CODE()==null?dnDetailModel.getITEM_NO():dnDetailModel.getGOLFA_CODE());
        listItemView.txtRowNo.setText( (dnDetailModel.getSTATUS()==0?"正常":dnDetailModel.getSTATUS()==1?"重复序列号已处理":"数量超出"));
        listItemView.txtItemName.setText(dnDetailModel.getITEM_NAME());
        listItemView.txtDNQty.setText(dnsource==3?"":(convertView.getResources().getString(R.string.dnQty)+ dnDetailModel.getDN_QTY().toString()));
        listItemView.txtScanQty.setText(convertView.getResources().getString(R.string.scanQty)+dnDetailModel.getSCAN_QTY());
        if(dnDetailModel.getDN_QTY()==dnDetailModel.getSCAN_QTY()){
            convertView.setBackgroundResource(R.color.mediumaquamarine);
        }else if(dnDetailModel.getDN_QTY()>dnDetailModel.getSCAN_QTY() && dnDetailModel.getSCAN_QTY()!=0){
            convertView.setBackgroundResource(R.color.antiquewhite);
        }
        else if(dnDetailModel.getDN_QTY()<dnDetailModel.getSCAN_QTY()){
            convertView.setBackgroundResource(R.color.peachpuff);
        }
        else {
            convertView.setBackgroundResource(R.color.trans);
        }
        return convertView;
    }



}
