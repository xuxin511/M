package com.xx.chinetek.adapter;

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

public class BulkuploadItemAdapter extends BaseAdapter {
    private Context context; // 运行上下文
    private ArrayList<DNModel> DNModels; // 信息集合
    private LayoutInflater listContainer; // 视图容器


    public final class ListItemView { // 自定义控件集合

        public TextView txtDeliveryNo;
        public TextView txtStatus;
        public TextView txtConsignee;
        public TextView txtSumbitTime;
        public TextView txtSource;
        public TextView txtSubmitUser;
    }

    public BulkuploadItemAdapter(Context context, ArrayList<DNModel> DNModels) {
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
            convertView = listContainer.inflate(R.layout.item_bulkupload_list,null);
            listItemView.txtDeliveryNo = (TextView) convertView.findViewById(R.id.item_DeliveryNo);
            listItemView.txtStatus = (TextView) convertView.findViewById(R.id.item_Status);
            listItemView.txtConsignee = (TextView) convertView.findViewById(R.id.item_Consignee);
            listItemView.txtSumbitTime = (TextView) convertView.findViewById(R.id.item_SubmitTime);
            listItemView.txtSource = (TextView) convertView.findViewById(R.id.item_Source);
            listItemView.txtSubmitUser = (TextView) convertView.findViewById(R.id.item_SubmitUser);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
        DNModel DNModel = DNModels.get(selectID);
        listItemView.txtDeliveryNo.setText(DNModel.getAGENT_DN_NO());
        listItemView.txtStatus.setText(String.valueOf(DNModel.getDN_STATUS()));
        listItemView.txtConsignee.setText(DNModel.getCUSTOM_NAME());
//        listItemView.txtSumbitTime.setText(convertView.getResources().getString(R.string.submittime)+ CommonUtil.DateToString(DNModel.getUPDATE_DATE(),null));
        listItemView.txtSource.setText(DNModel.getDN_SOURCE());
//        listItemView.txtSubmitUser.setText(convertView.getResources().getString(R.string.submituser)+DNModel.getUPDATE_USER());
        return convertView;
    }
//
//    @Override
//    public Filter getFilter() {
//        if (mFilter == null) {
//            mFilter = new BillAdapter.ArrayFilter();
//        }
//        return mFilter;
//    }
//
//    private class ArrayFilter extends Filter {
//
//        @Override
//        protected FilterResults performFiltering(CharSequence prefix) {
//            FilterResults results = new FilterResults();
//
//            if (mUnfilteredData == null) {
//                mUnfilteredData =  new ArrayList<WoModel>(WoModels);
//            }
//
//            if (prefix == null || prefix.length() == 0) {
//                ArrayList<WoModel> list = mUnfilteredData;
//                results.values = list;
//                results.count = list.size();
//            } else {
//                String prefixString = prefix.toString().toLowerCase();
//
//                ArrayList<WoModel> unfilteredValues = mUnfilteredData;
//                int count = unfilteredValues.size();
//
//                ArrayList<WoModel> newValues = new ArrayList<WoModel>(count);
//
//                for (int i = 0; i < count; i++) {
//                    WoModel pc = unfilteredValues.get(i);
//                    if (pc != null) {
//                        int len =pc.getErpVoucherNo().toUpperCase().length();
//                        int LenPre = prefixString.toUpperCase().length();
//                        if (LenPre<=8)
//                        {
//                            if(pc.getErpVoucherNo().toUpperCase().substring(len-8,len).startsWith(prefixString.toUpperCase())){
//                                newValues.add(pc);
//                            }
//                        }
//                        if (LenPre==18)
//                        {
//                            if(pc.getErpVoucherNo().toUpperCase().substring(0,len).startsWith(prefixString.toUpperCase())){
//                                newValues.add(pc);
//                            }
//                        }
//
//                    }
//                }
//
//                results.values = newValues;
//                results.count = newValues.size();
//            }
//
//            return results;
//        }
//
//        @Override
//        protected void publishResults(CharSequence constraint,
//                                      FilterResults results) {
//            //noinspection unchecked
//            WoModels = (ArrayList<WoModel>) results.values;
//            if (results.count > 0) {
//                notifyDataSetChanged();
//            } else {
//                notifyDataSetInvalidated();
//            }
//        }

}
