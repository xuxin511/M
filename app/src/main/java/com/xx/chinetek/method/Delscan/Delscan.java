package com.xx.chinetek.method.Delscan;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.xx.chinetek.adapter.DN.DeliveryListItemAdapter;
import com.xx.chinetek.adapter.DN.DeliveryScanItemAdapter;
import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;

import static com.xx.chinetek.chineteklib.base.BaseApplication.context;

/**
 * Created by GHOST on 2017/11/26.
 */

public class Delscan {

    public static void DelDNmodel(DNModel model){
        try {
//            final int clickpositionlong = i;
//            final DeliveryListItemAdapter ItemAdapter = deliveryListItemAdapter;
//            if (clickpositionlong == -1) {
//                MessageBox.Show(context, "请先选择操作的行！");
//                return;
//            }
            final DNModel Model= model;
            new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("确认删除扫描记录？\n")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO 自动生成的方法
                            //删除扫描记录，改变表头状态，改变明细数量
//
//                            DNModel Model = (DNModel) ItemAdapter.getItem(clickpositionlong);
                            if (DbDnInfo.getInstance().DELscanbyagent(Model.getAGENT_DN_NO(), "")) {
//                                                DbDnInfo.getInstance().UpdateDNmodelDetailNumberbyDN(Model.getAGENT_DN_NO(),"");
//                                                DbDnInfo.getInstance().UpdateDNmodelState(Model.getAGENT_DN_NO(),"2","");
                                //判断剩余的扫描数量
                                if (DbDnInfo.getInstance().UpdateDetailAllNum(Model.getAGENT_DN_NO(), 0, Model.getDN_SOURCE())) {
                                    //需要改变主表状态
//                                    DNModel modeldn=Model;
//                                    modeldn.setSTATUS(1);
                                    if (DbDnInfo.getInstance().UpdateDNmodelState(Model.getAGENT_DN_NO(), "1", "", Model.getDN_SOURCE())) {
                                        MessageBox.Show(context, BaseApplication.context.getString(R.string.Msg_del_success));

                                    } else {
                                        MessageBox.Show(context, BaseApplication.context.getString(R.string.Error_del_dnmodel));
                                        return;
                                    }

                                } else {
                                    MessageBox.Show(context, BaseApplication.context.getString(R.string.Error_del_dnmodeldetail));
                                    return;
                                }
                            } else {
                                MessageBox.Show(context, BaseApplication.context.getString(R.string.Error_del_dnmodelbarcode));
                            }

                        }
                    }).setNegativeButton("取消", null).show();

        } catch (Exception ex) {
            MessageBox.Show(context, ex.toString());
        }
    }


    public static void DelDNDetailmodel(DNDetailModel detailModel, DNModel dnmodel){
        try{
//            final int clickpositionlong=i;
//            final DeliveryScanItemAdapter ItemAdapter = deliveryScanItemAdapter;
            final DNDetailModel Model=detailModel;
            final DNModel dnModel = dnmodel;
            // TODO 自动生成的方法
            //删除扫描记录，改变明细数量
//            if(clickpositionlong==-1){
//                MessageBox.Show(context,"请先选择操作的行！");
//                return;
//            }
            new AlertDialog.Builder(context).setCancelable(false).setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("是否删除扫描记录？\n")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            DNDetailModel Model= (DNDetailModel)ItemAdapter.getItem(clickpositionlong);
                            if(DbDnInfo.getInstance().DELscanbyagentdetail(Model,"")){
//                                DbDnInfo.getInstance().UpdateDNmodelDetailNumberbyGOLFACODE(Model,"");
                                //判断剩余的扫描数量
                                Integer lastNum=DbDnInfo.getInstance().GetLoaclDNScanModelDNNum(Model.getAGENT_DN_NO(),Model.getGOLFA_CODE(),Model.getLINE_NO());
                                if(DbDnInfo.getInstance().UpdateDetailNum(Model.getAGENT_DN_NO(),Model.getGOLFA_CODE(),Model.getLINE_NO(),lastNum,dnModel.getDN_SOURCE())){
                                    if(DbDnInfo.getInstance().GetLoaclDNScanModelDNNumbyDNNO(Model.getAGENT_DN_NO())==0){
                                        //需要改变主表状态
//                                        DNModel modeldn=dnModel;
//                                        modeldn.setSTATUS(1);
                                        if(DbDnInfo.getInstance().UpdateDNmodelState(Model.getAGENT_DN_NO(),"1","",dnModel.getDN_SOURCE())){

                                        }else{
                                            MessageBox.Show(context,BaseApplication.context.getString(R.string.Error_del_dnmodel));
                                            return;
                                        }
                                    }
//                                    GetDeliveryOrderScanList();
                                    MessageBox.Show(context,BaseApplication.context.getString(R.string.Msg_del_success));

                                }else{
                                    MessageBox.Show(context,BaseApplication.context.getString(R.string.Error_del_dnmodeldetail));
                                    return;
                                }

                            }else{
                                MessageBox.Show(context,BaseApplication.context.getString(R.string.Error_del_dnmodelbarcode));
                            }
                        }
                    }).setNegativeButton("取消", null).show();

        }catch(Exception ex){
            MessageBox.Show(context,ex.toString());
        }
    }


}
