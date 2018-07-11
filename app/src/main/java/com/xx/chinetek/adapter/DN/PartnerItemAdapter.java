package com.xx.chinetek.adapter.DN;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.xx.chinetek.chineteklib.base.AppManager;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.CustomModel;

import java.util.ArrayList;

/**
 * Created by GHOST on 2017/1/13.
 */

public class PartnerItemAdapter extends BaseAdapter  implements Filterable {
    private Context context; // 运行上下文
    private ArrayFilter mFilter;
    private ArrayList<CustomModel> mUnfilteredData;
    private ArrayList<CustomModel> customModels; // 信息集合
    private LayoutInflater listContainer; // 视图容器

    public final class ListItemView { // 自定义控件集合

        public TextView txtPartnerName;
        public TextView txtType;
    }

    public PartnerItemAdapter(Context context, ArrayList<CustomModel> customModels) {
        this.context = context;
        listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.customModels = customModels;
    }


    @Override
    public int getCount() {
        return customModels ==null?0: customModels.size();
    }

    @Override
    public Object getItem(int position) {
        return customModels.get(position);
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
            convertView = listContainer.inflate(R.layout.item_partner_detail,null);
            listItemView.txtPartnerName = (TextView) convertView.findViewById(R.id.item_PartnerName);
            listItemView.txtType = (TextView) convertView.findViewById(R.id.item_Type);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
        CustomModel customModel = customModels.get(selectID);
        listItemView.txtPartnerName.setText(customModel.getNAME());
      listItemView.txtType.setText(customModel.getPARTNER_FUNCTION());
        return convertView;
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (mUnfilteredData == null) {
                mUnfilteredData = new ArrayList<CustomModel>(customModels);
            }
            if (prefix == null || prefix.length() == 0) {
                ArrayList<CustomModel> list = mUnfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<CustomModel> unfilteredValues = mUnfilteredData;
                int count = unfilteredValues.size();

                ArrayList<CustomModel> newValues = new ArrayList<CustomModel>(count);

                for (int i = 0; i < count; i++) {
                    CustomModel pc = unfilteredValues.get(i);
                    if (pc != null) {
                        if(pc.getCUSTOMER()!=null && pc.getCUSTOMER().toUpperCase().startsWith(prefixString.toUpperCase())
                                || pc.getNAME()!=null && pc.getNAME().toUpperCase().contains(prefixString.toUpperCase())){
                            newValues.add(pc);
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,FilterResults results) {
            customModels = (ArrayList<CustomModel>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }


}
