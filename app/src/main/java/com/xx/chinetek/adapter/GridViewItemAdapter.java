package com.xx.chinetek.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xx.chinetek.mitsubshi.R;

import java.util.List;
import java.util.Map;


public class GridViewItemAdapter extends BaseAdapter {

    private Context context;
    private List<Map<String, Object>> listitem;

    public GridViewItemAdapter(Context context, List<Map<String, Object>> listitem) {
        this.context = context;
        this.listitem = listitem;
    }

    @Override
    public int getCount() {
        return listitem==null?0:listitem.size();
    }

    @Override
    public Object getItem(int position) {
        return listitem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_gridview, null);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.ItemImage);
        TextView textView = (TextView) convertView.findViewById(R.id.ItemText);

        Map<String, Object> map = listitem.get(position);
        imageView.setImageResource((Integer) map.get("image"));
        textView.setText(map.get("text") + "");
        switch (position) {
            case 0:
                convertView.setBackgroundResource(R.color.trans);
                break;
            case 1:
                convertView.setBackgroundResource(R.color.trans);
                break;
            case 2:
                convertView.setBackgroundResource(R.color.trans);
                break;
            case 3:
                convertView.setBackgroundResource(R.color.trans);
                break;
            case 4:
                convertView.setBackgroundResource(R.color.trans);
                break;
        }
        return convertView;
    }


}