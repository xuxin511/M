package com.xx.chinetek.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.Base.MaterialModel;

import java.util.List;

/**
 * Created by GHOST on 2017/1/13.
 */

public class MaterialQueryItemAdapter extends BaseAdapter {
    private Context context; // 运行上下文
    private List<MaterialModel> materialModels; // 信息集合
    private LayoutInflater listContainer; // 视图容器

    public final class ListItemView { // 自定义控件集合

        public TextView txtItemNo;
        public TextView txtRowNo;
        public TextView txtItemName;
        public TextView txtItemSap;
        public TextView txtItemLine;
        public TextView txtQR;
        public TextView txtCusGF;
    }

    public MaterialQueryItemAdapter(Context context, List<MaterialModel> materialModels) {
        this.context = context;
        listContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
        this.materialModels = materialModels;

    }

    public void modify(){
            notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return materialModels ==null?0: materialModels.size();
    }

    @Override
    public Object getItem(int position) {
        return materialModels.get(position);
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
            convertView = listContainer.inflate(R.layout.item_meatrial_query,null);
            listItemView.txtItemNo = (TextView) convertView.findViewById(R.id.txt_ItemNo);
            listItemView.txtRowNo = (TextView) convertView.findViewById(R.id.item_RowNo);
            listItemView.txtItemName = (TextView) convertView.findViewById(R.id.item_ItemName);
            listItemView.txtItemSap = (TextView) convertView.findViewById(R.id.txt_ItemSap);
            listItemView.txtItemLine = (TextView) convertView.findViewById(R.id.txt_ItemLine);
            listItemView.txtQR = (TextView) convertView.findViewById(R.id.item_QR);
            listItemView.txtCusGF = (TextView) convertView.findViewById(R.id.txt_cusgf);
            convertView.setTag(listItemView);
        } else {
            listItemView = (ListItemView) convertView.getTag();
        }
        MaterialModel materialModel = materialModels.get(selectID);
        listItemView.txtItemNo.setText(materialModel.getBISMT()==null?"":materialModel.getBISMT());
        listItemView.txtItemName.setText(materialModel.getMAKTX());
        listItemView.txtItemSap.setText("SAP号："+materialModel.getMATNR());
        listItemView.txtRowNo.setText("正常");
        listItemView.txtItemLine.setText(materialModel.getSPARTNAME());
        listItemView.txtRowNo.setTextColor(convertView.getResources().getColor(R.color.green));
        listItemView.txtQR .setText(materialModel.getNORMT()==null || TextUtils.isEmpty(materialModel.getNORMT())?"N":materialModel.getNORMT());
        if(materialModel.getCUSBISMT()!=null && !TextUtils.isEmpty(materialModel.getCUSBISMT())){
            listItemView.txtCusGF.setText("自定义GolfaCode："+materialModel.getCUSBISMT());
            listItemView.txtCusGF.setVisibility(View.VISIBLE);
        }else{
            listItemView.txtCusGF.setVisibility(View.GONE);
        }
        if(materialModel.getACTION_CODE()!=null && materialModel.getACTION_CODE().equals("D")){
            listItemView.txtRowNo.setText("已删除");
            listItemView.txtRowNo.setTextColor(convertView.getResources().getColor(R.color.white));
            convertView.setBackgroundColor(convertView.getResources().getColor(R.color.lightcoral));
        }else{
            convertView.setBackgroundColor(convertView.getResources().getColor(R.color.trans));
        }

        return convertView;
    }



}
