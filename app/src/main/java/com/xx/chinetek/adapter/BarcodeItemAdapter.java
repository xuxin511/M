package com.xx.chinetek.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNScanModel;

import java.util.ArrayList;

/**
 * Created by GHOST on 2017/1/13.
 */

public class BarcodeItemAdapter extends BaseAdapter {
    private Context context; // 运行上下文
    private ArrayList<DNScanModel> dnScanModels; // 信息集合
    private LayoutInflater listContainer; // 视图容器
    int index=0;

    public final class ListItemView { // 自定义控件集合

        public TextView txtBarcode;
        public TextView txtIndex;
    }

    public BarcodeItemAdapter(Context context, ArrayList<DNScanModel> dnScanModels) {
        this.context = context;
        listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.dnScanModels = dnScanModels;

    }


    @Override
    public int getCount() {
        return dnScanModels ==null?0: dnScanModels.size();
    }

    @Override
    public Object getItem(int position) {
        return dnScanModels.get(position);
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
            convertView = listContainer.inflate(R.layout.item_barcode_detail,null);
            listItemView.txtBarcode = (TextView) convertView.findViewById(R.id.item_Barcode);
            listItemView.txtIndex = (TextView) convertView.findViewById(R.id.item_index);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
        DNScanModel dnScanModel = dnScanModels.get(selectID);
        listItemView.txtBarcode.setText(dnScanModel.getSERIAL_NO());
        listItemView.txtIndex.setText(convertView.getResources().getString(R.string.RowNo)+(++index));
        return convertView;
    }



}
