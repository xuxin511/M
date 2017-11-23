package com.xx.chinetek.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xx.chinetek.mitsubshi.R;

import java.util.List;

/**
 * Created by GHOST on 2017/1/13.
 */

public class BarcodeRuleItemAdapter extends BaseAdapter {
    private Context context; // 运行上下文
    private List<String> cusBarcodeRules; // 信息集合
    private LayoutInflater listContainer; // 视图容器

    public final class ListItemView { // 自定义控件集合

        public TextView txtStart;
        public TextView txtEnd;
        public TextView txtIndex;
    }

    public BarcodeRuleItemAdapter(Context context, List<String> cusBarcodeRules) {
        this.context = context;
        listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.cusBarcodeRules = cusBarcodeRules;

    }


    @Override
    public int getCount() {
        return cusBarcodeRules ==null?0: cusBarcodeRules.size();
    }

    @Override
    public Object getItem(int position) {
        return cusBarcodeRules.get(position);
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
            convertView = listContainer.inflate(R.layout.item_barcode_rule_detail,null);
            listItemView.txtStart = (TextView) convertView.findViewById(R.id.item_Start);
            listItemView.txtEnd = (TextView) convertView.findViewById(R.id.item_End);
            listItemView.txtIndex = (TextView) convertView.findViewById(R.id.item_index);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
        String[] cusBarcodeRule = cusBarcodeRules.get(selectID).split("-");
        listItemView.txtStart.setText("起始位："+cusBarcodeRule[0]);
        listItemView.txtEnd.setText("结束位："+cusBarcodeRule[1]);
        listItemView.txtIndex.setText("参数"+(selectID+1));
        return convertView;
    }



}
