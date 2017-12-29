package com.xx.chinetek.method.Delscan;

import android.content.Context;

import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;

/**
 * Created by GHOST on 2017/11/26.
 */

public class Delscan {

    public static void DelDNmodel(Context context,DNModel Model){
        try {
                //删除扫描记录，改变表头状态，改变明细数量
                if (DbDnInfo.getInstance().DELscanbyagent(Model.getAGENT_DN_NO())) {
//                                                DbDnInfo.getInstance().UpdateDNmodelDetailNumberbyDN(Model.getAGENT_DN_NO(),"");
//                                                DbDnInfo.getInstance().UpdateDNmodelState(Model.getAGENT_DN_NO(),"2","");
                    //判断剩余的扫描数量
//                    if (DbDnInfo.getInstance().UpdateDetailAllNum(Model.getAGENT_DN_NO(), 0, Model.getDN_SOURCE())) {
                    if (DbDnInfo.getInstance().DelDetailAllNum(Model.getAGENT_DN_NO())) {
                        //需要改变主表状态
//                        if (DbDnInfo.getInstance().UpdateDNmodelState(Model.getAGENT_DN_NO(), "1", "", Model.getDN_SOURCE())) {
                            if (DbDnInfo.getInstance().DelDNmodels(Model.getAGENT_DN_NO())) {
                            MessageBox.Show(context, context.getString(R.string.Msg_del_success));

                        } else {
                            MessageBox.Show(context, context.getString(R.string.Error_del_dnmodel));
                            return;
                        }

                    } else {
                        MessageBox.Show(context, context.getString(R.string.Error_del_dnmodeldetail));
                        return;
                    }
                } else {
                    MessageBox.Show(context, context.getString(R.string.Error_del_dnmodelbarcode));
                }

        } catch (Exception ex) {
            MessageBox.Show(context, ex.toString());
        }
    }


    public static void DelDNDetailmodel(Context context,DNDetailModel Model, DNModel dnModel){
        try{
            // TODO 自动生成的方法
            //删除扫描记录，改变明细数量
            if(DbDnInfo.getInstance().DELscanbyagentdetail(Model,"")){
//                                DbDnInfo.getInstance().UpdateDNmodelDetailNumberbyGOLFACODE(Model,"");
                //判断剩余的扫描数量
                Integer lastNum=DbDnInfo.getInstance().GetLoaclDNScanModelDNNum(Model.getAGENT_DN_NO(),Model.getGOLFA_CODE(),Model.getLINE_NO());
                if(DbDnInfo.getInstance().UpdateDetailNum(Model.getAGENT_DN_NO(),Model.getGOLFA_CODE(),Model.getLINE_NO(),lastNum,dnModel.getDN_SOURCE())){
                    if(DbDnInfo.getInstance().GetLoaclDNScanModelDNNumbyDNNO(Model.getAGENT_DN_NO())==0){
                        //需要改变主表状态
                        if(DbDnInfo.getInstance().UpdateDNmodelState(Model.getAGENT_DN_NO(),"1","",dnModel.getDN_SOURCE())){

                        }else{
                            MessageBox.Show(context,context.getString(R.string.Error_del_dnmodel));
                            return;
                        }
                    }
                    MessageBox.Show(context,context.getString(R.string.Msg_del_success));

                }else{
                    MessageBox.Show(context,context.getString(R.string.Error_del_dnmodeldetail));
                    return;
                }

            }else{
                MessageBox.Show(context,context.getString(R.string.Error_del_dnmodelbarcode));
            }
        }catch(Exception ex){
            MessageBox.Show(context,ex.toString());
        }
    }


    public static Integer DelScanmodel(Context context,DNScanModel Model, DNDetailModel dndetailmodel, DNModel dnModel){
        //删除扫描记录，改变明细数量
        if(DbDnInfo.getInstance().DELscanbyserial(Model.getAGENT_DN_NO(),Model.getGOLFA_CODE(),Model.getLINE_NO(),Model.getSERIAL_NO(),"",Model.getSTATUS())){
            //判断剩余的扫描数量
            Integer lastNum=DbDnInfo.getInstance().GetLoaclDNScanModelDNNum(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO());

            //删除扫描记录，修改扫描记录表
             DNDetailModel dndetail = DbDnInfo.getInstance().GetLoaclDNDetailStatus(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO());
            if(dndetail.getSTATUS()==2||dndetail.getSTATUS()==3){
                if(DbDnInfo.getInstance().GetDNScanOKModel(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO()) && dndetail.getDN_QTY()>=lastNum){
                    //改变所有序列号的状态
                    DbDnInfo.getInstance().UpdateDNScanState(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO());
                }
            }


            if(DbDnInfo.getInstance().UpdateDetailNum(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO(),lastNum,dnModel.getDN_SOURCE())){
                if(DbDnInfo.getInstance().GetLoaclDNScanModelDNNumbyDNNO(dndetailmodel.getAGENT_DN_NO())==0){
                    //需要改变主表状态
                    if(DbDnInfo.getInstance().UpdateDNmodelState(dndetailmodel.getAGENT_DN_NO(),"1","",dnModel.getDN_SOURCE())){

                    }else{
                        MessageBox.Show(context,context.getString(R.string.Error_del_dnmodel));
                        return -1;
                    }
                }
                MessageBox.Show(context,context.getString(R.string.Msg_del_success));
                return lastNum;

            }else{
                MessageBox.Show(context,context.getString(R.string.Error_del_dnmodeldetail));
                return-1 ;
            }
        }else{
            MessageBox.Show(context,context.getString(R.string.Error_del_dnmodelbarcode));
        }
        return-1 ;
    }



    public static void DelAllScanmodel(Context  context,DNDetailModel dndetailmodel,DNModel dnModel){
        //删除扫描记录，改变明细数量
        if(DbDnInfo.getInstance().DelExceptionScanmodel(dndetailmodel)){
            //判断剩余的扫描数量
            Integer lastNum=DbDnInfo.getInstance().GetLoaclDNScanModelDNNum(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO());

            //删除扫描记录，修改扫描记录表
            DNDetailModel dndetail = DbDnInfo.getInstance().GetLoaclDNDetailStatus(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO());
            if(dndetail.getSTATUS()==2||dndetail.getSTATUS()==3){
                if(DbDnInfo.getInstance().GetDNScanOKModel(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO()) && dndetail.getDN_QTY()>=lastNum){
                    //改变所有序列号的状态
                    DbDnInfo.getInstance().UpdateDNScanState(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO());
                }
            }


            if(DbDnInfo.getInstance().UpdateDetailNum(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO(),lastNum,dnModel.getDN_SOURCE())){
                if(DbDnInfo.getInstance().GetLoaclDNScanModelDNNumbyDNNO(dndetailmodel.getAGENT_DN_NO())==0){
                    //需要改变主表状态
                    if(DbDnInfo.getInstance().UpdateDNmodelState(dndetailmodel.getAGENT_DN_NO(),"1","",dnModel.getDN_SOURCE())){

                    }else{
                        MessageBox.Show(context,context.getString(R.string.Error_del_dnmodel));
                        return;
                    }
                }
                MessageBox.Show(context,context.getString(R.string.Msg_del_success));

            }else{
                MessageBox.Show(context,context.getString(R.string.Error_del_dnmodeldetail));
                return;
            }
        }else{
            MessageBox.Show(context,context.getString(R.string.Error_del_dnmodelbarcode));
        }

    }


}
