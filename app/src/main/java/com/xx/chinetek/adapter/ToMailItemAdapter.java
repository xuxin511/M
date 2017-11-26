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

public class ToMailItemAdapter extends BaseAdapter {
    private Context context; // 运行上下文
    private List<String> ToAdress; // 信息集合
    private LayoutInflater listContainer; // 视图容器

    public final class ListItemView { // 自定义控件集合

        public TextView txtadress;
    }

    public ToMailItemAdapter(Context context, List<String> ToAdress) {
        this.context = context;
        listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.ToAdress = ToAdress;

    }


    @Override
    public int getCount() {
        return ToAdress ==null?0: ToAdress.size();
    }

    @Override
    public Object getItem(int position) {
        return ToAdress.get(position);
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
            convertView = listContainer.inflate(R.layout.item_mail,null);
            listItemView.txtadress = (TextView) convertView.findViewById(R.id.item_address);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
        String adress = ToAdress.get(selectID);
        listItemView.txtadress.setText(adress);
        return convertView;
    }



}
