package com.xx.chinetek.adapter.DN;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNModel;

import java.util.ArrayList;

/**
 * Created by GHOST on 2017/1/13.
 */

public class DeliveryListItemAdapter extends BaseAdapter {
    private Context context; // 运行上下文
    private ArrayList<DNModel> DNModels; // 信息集合
    private LayoutInflater listContainer; // 视图容器


    public final class ListItemView { // 自定义控件集合

        public TextView txtDeliveryNo;
        public TextView txtStatus;
        public TextView txtConsignee;
        public TextView txtCreateTime;
        public TextView txtSource;
    }

    public DeliveryListItemAdapter(Context context, ArrayList<DNModel> DNModels) {
        this.context = context;
        listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.DNModels = DNModels;

    }


    @Override
    public int getCount() {
        return DNModels ==null?0: DNModels.size();
    }

    @Override
    public Object getItem(int position) {
        return DNModels.get(position);
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
            convertView = listContainer.inflate(R.layout.item_delivery_list,null);
            listItemView.txtDeliveryNo = (TextView) convertView.findViewById(R.id.item_DeliveryNo);
            listItemView.txtStatus = (TextView) convertView.findViewById(R.id.item_Status);
            listItemView.txtConsignee = (TextView) convertView.findViewById(R.id.item_Consignee);
            listItemView.txtCreateTime = (TextView) convertView.findViewById(R.id.item_CreateTime);
            listItemView.txtSource = (TextView) convertView.findViewById(R.id.item_Source);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
        DNModel DNModel = DNModels.get(selectID);
        listItemView.txtDeliveryNo.setText(DNModel.getAGENT_DN_NO());
        listItemView.txtStatus.setText(DNModel.getDN_STATUS()==1?"已下载":"未下载");
        listItemView.txtConsignee.setText(DNModel.getCUSTOM_NAME());
        listItemView.txtCreateTime.setText(convertView.getResources().getString(R.string.createtime)+ CommonUtil.DateToString(DNModel.getUPDATE_DATE(),null));
        listItemView.txtSource.setText(DNModel.getDN_SOURCE());
        return convertView;
    }



}
