package com.xx.chinetek.adapter.DN;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GHOST on 2017/1/13.
 */

public class DeliveryListItemAdapter extends BaseAdapter implements Filterable {
    private Context context; // 运行上下文
    private DeliveryListItemAdapter.ArrayFilter mFilter;
    private ArrayList<DNModel> mUnfilteredData;
    private ArrayList<DNModel> DNModels; // 信息集合
    private LayoutInflater listContainer; // 视图容器
    private List<Boolean> listselected;//用布尔型的list记录每一行的选中状态

    public final class ListItemView { // 自定义控件集合

        public TextView txtDeliveryNo;
        public TextView txtStatus;
        public TextView txtConsignee;
        public TextView txtCreateTime;
        public TextView txtSource;
        public TextView txtDNQty;
        public TextView txtScanQty;
    }

    public DeliveryListItemAdapter(Context context, ArrayList<DNModel> DNModels) {
        this.context = context;
        listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.DNModels = DNModels;
        this.setListselected(new ArrayList<Boolean>(getCount()));
        for(int i=0;i<getCount();i++)
            getListselected().add(false);//初始为false，长度和listview一样
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
            listItemView.txtDNQty = (TextView) convertView.findViewById(R.id.item_DNQty);
            listItemView.txtScanQty = (TextView) convertView.findViewById(R.id.item_ScanQty);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
        DNModel DNModel = DNModels.get(selectID);
        int dnSoource=DNModel.getDN_SOURCE()==null?-1:DNModel.getDN_SOURCE();
        listItemView.txtDeliveryNo.setText(dnSoource==3?DNModel.getCUS_DN_NO():DNModel.getAGENT_DN_NO());
        listItemView.txtStatus.setText(convertView.getResources().getStringArray(R.array.DNStatus)[DNModel.getSTATUS()+1]);
        listItemView.txtConsignee.setText(DNModel.getCUSTOM_NAME()==null || TextUtils.isEmpty(DNModel.getCUSTOM_NAME())?DNModel.getLEVEL_2_AGENT_NAME():DNModel.getCUSTOM_NAME());
        listItemView.txtCreateTime.setText(convertView.getResources().getString(R.string.createtime)+ CommonUtil.DateToString(DNModel.getDN_DATE(),null));
        listItemView.txtDNQty.setText(DNModel.getDN_SOURCE()!=null && DNModel.getDN_SOURCE()==3?"":convertView.getResources().getString(R.string.dnQty)+ DbDnInfo.getInstance().GetDNQtyInDNDetail(DNModel.getAGENT_DN_NO()));
        listItemView.txtScanQty.setText(convertView.getResources().getString(R.string.scanQty)+ DbDnInfo.getInstance().GetScanQtyInDNScanModel(DNModel.getAGENT_DN_NO()));
        if(DNModel.getDN_SOURCE()!=null)
            listItemView.txtSource.setText(context.getResources().getStringArray(R.array.sendTypeList)[DNModel.getDN_SOURCE()]);
        if (getListselected().get(position)) {
            convertView.setBackgroundResource(R.color.lightgreen);
        }else if(DNModel.getFlag()!=null && DNModel.getFlag()==1){
            convertView.setBackgroundResource(R.color.palevioletred);
        } else {
            convertView.setBackgroundResource(R.color.trans);
        }
        return convertView;
    }



    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new DeliveryListItemAdapter.ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mUnfilteredData == null) {
                mUnfilteredData = new ArrayList<DNModel>(DNModels);
            }

            if (prefix == null || prefix.length() == 0) {
                ArrayList<DNModel> list = mUnfilteredData;
                results.values = list;
                results.count = list.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                ArrayList<DNModel> unfilteredValues = mUnfilteredData;
                int count = unfilteredValues.size();

                ArrayList<DNModel> newValues = new ArrayList<DNModel>(count);

                for (int i = 0; i < count; i++) {
                    DNModel pc = unfilteredValues.get(i);
                    if (pc != null) {
                        if((pc.getAGENT_DN_NO()!=null && pc.getAGENT_DN_NO().toUpperCase().startsWith(prefixString.toUpperCase()))
                                || (pc.getCUS_DN_NO()!=null && pc.getCUS_DN_NO().toUpperCase().startsWith(prefixString.toUpperCase()))){

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
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            //noinspection unchecked
            DNModels = (ArrayList<DNModel>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }


}
