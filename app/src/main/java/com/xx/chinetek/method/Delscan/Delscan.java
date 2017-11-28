package com.xx.chinetek.method.Delscan;

import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;

import static com.xx.chinetek.chineteklib.base.BaseApplication.context;

/**
 * Created by GHOST on 2017/11/26.
 */

public class Delscan {

    public static void DelDNmodel(DNModel Model){
        try {
                //删除扫描记录，改变表头状态，改变明细数量
                if (DbDnInfo.getInstance().DELscanbyagent(Model.getAGENT_DN_NO(), "")) {
//                                                DbDnInfo.getInstance().UpdateDNmodelDetailNumberbyDN(Model.getAGENT_DN_NO(),"");
//                                                DbDnInfo.getInstance().UpdateDNmodelState(Model.getAGENT_DN_NO(),"2","");
                    //判断剩余的扫描数量
                    if (DbDnInfo.getInstance().UpdateDetailAllNum(Model.getAGENT_DN_NO(), 0, Model.getDN_SOURCE())) {
                        //需要改变主表状态
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

        } catch (Exception ex) {
            MessageBox.Show(context, ex.toString());
        }
    }


    public static void DelDNDetailmodel(DNDetailModel Model, DNModel dnModel){
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
                            MessageBox.Show(context,BaseApplication.context.getString(R.string.Error_del_dnmodel));
                            return;
                        }
                    }
                    MessageBox.Show(context,BaseApplication.context.getString(R.string.Msg_del_success));

                }else{
                    MessageBox.Show(context,BaseApplication.context.getString(R.string.Error_del_dnmodeldetail));
                    return;
                }

            }else{
                MessageBox.Show(context,BaseApplication.context.getString(R.string.Error_del_dnmodelbarcode));
            }
        }catch(Exception ex){
            MessageBox.Show(context,ex.toString());
        }
    }


    public static void DelScanmodel(DNScanModel Model,DNDetailModel dndetailmodel,DNModel dnModel){
        //删除扫描记录，改变明细数量
        if(DbDnInfo.getInstance().DELscanbyserial(Model.getAGENT_DN_NO(),Model.getGOLFA_CODE(),Model.getLINE_NO(),Model.getSERIAL_NO(),"")){
            //判断剩余的扫描数量
            Integer lastNum=DbDnInfo.getInstance().GetLoaclDNScanModelDNNum(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO());
            if(DbDnInfo.getInstance().UpdateDetailNum(dndetailmodel.getAGENT_DN_NO(),dndetailmodel.getGOLFA_CODE(),dndetailmodel.getLINE_NO(),lastNum,dnModel.getDN_SOURCE())){
                if(DbDnInfo.getInstance().GetLoaclDNScanModelDNNumbyDNNO(dndetailmodel.getAGENT_DN_NO())==0){
                    //需要改变主表状态
                    if(DbDnInfo.getInstance().UpdateDNmodelState(dndetailmodel.getAGENT_DN_NO(),"1","",dnModel.getDN_SOURCE())){

                    }else{
                        MessageBox.Show(context,BaseApplication.context.getString(R.string.Error_del_dnmodel));
                        return;
                    }
                }
                MessageBox.Show(context,BaseApplication.context.getString(R.string.Msg_del_success));

            }else{
                MessageBox.Show(context,BaseApplication.context.getString(R.string.Error_del_dnmodeldetail));
                return;
            }
        }else{
            MessageBox.Show(context,BaseApplication.context.getString(R.string.Error_del_dnmodelbarcode));
        }

    }


}
