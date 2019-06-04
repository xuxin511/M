package com.xx.chinetek.method;

import com.xx.chinetek.chineteklib.base.BaseActivity;
import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.chineteklib.util.hander.MyHandler;
import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.method.Upload.UploadAgentDN;
import com.xx.chinetek.model.BarCodeModel;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DBReturnModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by GHOST on 2017/11/16.
 */

public class Scan {

    public static ArrayList<BarCodeModel>  barcodes= new ArrayList<BarCodeModel>();

    /**
     * 条码扫描（非自建方式）
     * @param barCodeModels
     * @return
     * @throws Exception
     */
    public static int ScanBarccode(MyHandler<BaseActivity> mHandler,DbDnInfo dnInfo,DNModel dnModel, ArrayList<BarCodeModel> barCodeModels) throws Exception {
        dnModel.resetDETAILS();
        List<DNDetailModel> dnDetailModels = dnModel.getDETAILS();
        int isErrorStatus=-1;//0:物料已扫描  1：数量已超出 2：物料不存在
        DNDetailModel dnDetailModel = new DNDetailModel();

        //判断物料是否存在
        int index = getIndex(dnModel,dnDetailModels, barCodeModels.get(0), dnDetailModel);
        if (index == -1) {
            isErrorStatus=2;
        }else {
            //判断扫描数量是否超过出库数量
            String condition = dnDetailModel.getGOLFA_CODE() == null ? dnDetailModel.getITEM_NO() : dnDetailModel.getGOLFA_CODE();
            DBReturnModel dbReturnModel = dnInfo.GetDNQty(dnModel.getAGENT_DN_NO(), condition,dnDetailModel.getLINE_NO());
            if (dbReturnModel.getDNQTY() < dbReturnModel.getSCANQTY() + barCodeModels.size() && dnModel.getDN_SOURCE()!=3) {
                isErrorStatus = 1;
            }else {
                isErrorStatus = Checkbarcode(mHandler,dnInfo,dnModel,barCodeModels, dnDetailModels, index);
            }
        }
        return isErrorStatus;

    }

    /**
     * 代理商条码扫描
     * @param dnInfo
     * @param dnModel
     * @param barCodeModels
     * @return
     * @throws Exception
     */
    public static int ScanAgentBarccode(MyHandler<BaseActivity> mHandler,DbDnInfo dnInfo, DNModel dnModel, ArrayList<BarCodeModel> barCodeModels,String detailRemark) throws Exception {
        dnModel.resetDETAILS();
        List<DNDetailModel> dnDetailModels = dnModel.getDETAILS();
        int isErrorStatus=-1;//1：数量已超出 2：物料不存在
        DNDetailModel dnDetailModel = new DNDetailModel();
        //判断物料是否存在
        int index =getAgentIndex(dnModel,dnDetailModels, barCodeModels.get(0), dnDetailModel);
        if (index == -1) {
            isErrorStatus=2;
        }else {
            //判断扫描数量是否超过出库数量
            dnDetailModel.setITEM_NO(dnDetailModels.get(index).getITEM_NO());
            dnDetailModel.setITEM_NAME(dnDetailModels.get(index).getITEM_NAME());
            String condition = dnDetailModel.getGOLFA_CODE() == null ? dnDetailModel.getITEM_NO() : dnDetailModel.getGOLFA_CODE();
            DBReturnModel dbReturnModel = dnInfo.GetDNQty(dnModel.getAGENT_DN_NO(), condition,dnDetailModel.getLINE_NO());
            if (dbReturnModel.getDNQTY() < dbReturnModel.getSCANQTY() + barCodeModels.size() && dnModel.getDN_SOURCE()!=3) {
                isErrorStatus = 1;
            }else {
                List<DNScanModel> dnScanModels=new ArrayList<>();
                int count=0;
                for (BarCodeModel barCodeModel : barCodeModels) {
                    count++;
                    DNScanModel dnScanModel = new DNScanModel();

                    if(dnDetailModels.get(index).getSCAN_QTY()==null) dnDetailModels.get(index).setSCAN_QTY(0);
                    //更新物料扫描数量
                    dnDetailModels.get(index).setSCAN_QTY(dnDetailModels.get(index).getSCAN_QTY() + 1);
                    //保存序列号
                    Date curDate = new Date();
                    String dateStr = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.CHINA).format(curDate);
                    dnScanModel.setSERIAL_NO(dateStr+count);
                    dnScanModel.setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
                    dnScanModel.setLINE_NO(dnDetailModels.get(index).getLINE_NO());
                    dnScanModel.setPACKING_DATE(barCodeModel.getPacking_Date());
                    dnScanModel.setREGION(barCodeModel.getPlace_Code());
                    dnScanModel.setCOUNTRY(barCodeModel.getCountry_Code());
                    dnScanModel.setGOLFA_CODE("");
                    dnScanModel.setITEM_STATUS("AC");
                    dnScanModel.setITEM_NO(dnDetailModels.get(index).getITEM_NO());
                    dnScanModel.setITEM_NAME(dnDetailModels.get(index).getITEM_NAME());
                    dnScanModel.setDEAL_SALE_DATE(CommonUtil.DateToString(new Date(),"yyyy/MM/dd"));
                    dnScanModel.setMAT_TYPE(barCodeModel.getMAT_TYPE());
                    dnScanModel.setSTATUS("0");
                    dnScanModel.setFLAG(1);
                    setOtherColumn(barCodeModel, dnScanModel);
                    dnDetailModels.get(index).getSERIALS().add(dnScanModel);
                    dnScanModels.add(dnScanModel);

                }
                DbDnInfo.getInstance().InsertDNScanModel(dnScanModels);
                dnDetailModels.get(index).setOPER_DATE(new Date());
                dnDetailModels.get(index).setDETAIL_STATUS("AC");
                dnDetailModels.get(index).setSTATUS(0);
                dnDetailModels.get(index).setFlag(2);
                DbDnInfo.getInstance().InsertDNDetailDB(dnDetailModels.get(index));
                UploadAgentDN.UpAgentDN(mHandler, dnModel,detailRemark);
            }
        }
        return isErrorStatus;

    }

    /**
     * 判断物料是否存在出库单中
     * @param dnDetailModels
     * @param barCodeModel
     * @param dnDetailModel
     * @return
     */
    public static int getIndex(DNModel dnModel, List<DNDetailModel> dnDetailModels, BarCodeModel barCodeModels, DNDetailModel dnDetailModel) {
        dnDetailModel.setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
        dnDetailModel.setGOLFA_CODE(barCodeModels.getGolfa_Code());
        dnDetailModel.setLINE_NO(barCodeModels.getLINE_NO());
        //判断是否存在物料
        return dnDetailModels.indexOf(dnDetailModel);
    }

    public static int getAgentIndex(DNModel dnModel, List<DNDetailModel> dnDetailModels, BarCodeModel barCodeModel, DNDetailModel dnDetailModel) {
        dnDetailModel.setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
        dnDetailModel.setITEM_NO(barCodeModel.getItemNo());
        dnDetailModel.setITEM_NAME(barCodeModel.getItemName());
        //判断是否存在物料
        return dnDetailModels.indexOf(dnDetailModel);
    }

    /**
     * 非自建单据扫描数量和重复检查
     * @param barCodeModels
     * @param dnDetailModels
     * @param index
     * @return
     */
    private static int Checkbarcode(MyHandler<BaseActivity> mHandler,DbDnInfo dnInfo,DNModel dnModel,ArrayList<BarCodeModel> barCodeModels, List<DNDetailModel> dnDetailModels, int index) throws  Exception{
        int isErrorStatus=-1;
        List<DNScanModel> dnScanModels=new ArrayList<>();
        for(DNDetailModel dnDetailModel:dnDetailModels){
            dnDetailModel.__setDaoSession(dnInfo.getDaoSession());
            dnScanModels.addAll(dnDetailModel.getSERIALS());
        }
        List<DNScanModel> dnScanModels1s=new ArrayList<>();
        for (BarCodeModel barCodeModel : barCodeModels) {
            //判断条码是否存在
           // dnDetailModels.get(index).__setDaoSession(dnInfo.getDaoSession());
           // List<DNScanModel> dnScanModels = dnDetailModels.get(index).getSERIALS();
            DNScanModel dnScanModel = new DNScanModel();
            dnScanModel.setSERIAL_NO(barCodeModel.getSerial_Number());
            dnScanModel.setGOLFA_CODE(barCodeModel.getGolfa_Code());
            int barcodeIndex = dnScanModels.indexOf(dnScanModel);
            if (barcodeIndex != -1) {
                isErrorStatus = 0;
                break;
            }

            if(barCodeModel.getLINE_NO()==null) {
                index = findIndexByGolfaCode(dnDetailModels, barCodeModel.getGolfa_Code());
                if (index == -1) {
                    isErrorStatus = 1;
                    break;
                }
            }
            if(dnDetailModels.get(index).getSCAN_QTY()==null) dnDetailModels.get(index).setSCAN_QTY(0);
            //更新物料扫描数量
            dnDetailModels.get(index).setSCAN_QTY(dnDetailModels.get(index).getSCAN_QTY() + 1);
            //保存序列号
            dnScanModel.setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
            dnScanModel.setLINE_NO(dnDetailModels.get(index).getLINE_NO());
            dnScanModel.setPACKING_DATE(barCodeModel.getPacking_Date());
            dnScanModel.setREGION(barCodeModel.getPlace_Code());
            dnScanModel.setCOUNTRY(barCodeModel.getCountry_Code());
            dnScanModel.setGOLFA_CODE(barCodeModel.getGolfa_Code());
            dnScanModel.setITEM_STATUS("AC");
            dnScanModel.setITEM_NO(dnDetailModels.get(index).getITEM_NO());
            dnScanModel.setITEM_NAME(dnDetailModels.get(index).getITEM_NAME());
            dnScanModel.setDEAL_SALE_DATE(CommonUtil.DateToString(new Date(),"yyyy/MM/dd"));
            dnScanModel.setMAT_TYPE(barCodeModel.getMAT_TYPE());
            dnScanModel.setSTATUS("0");
            if(dnModel.getDN_SOURCE()==5 || (ParamaterModel.IsAgentSoft && dnModel.getDN_SOURCE()==3)) dnScanModel.setFLAG(1);
            setOtherColumn(barCodeModel, dnScanModel);
            dnScanModels1s.add(dnScanModel);
            dnDetailModels.get(index).getSERIALS().add(dnScanModel);

        }
        DbDnInfo.getInstance().InsertDNScanModel(dnScanModels1s);
        dnDetailModels.get(index).setOPER_DATE(new Date());
        dnDetailModels.get(index).setDETAIL_STATUS("AC");
        dnDetailModels.get(index).setSTATUS(0);
        if(dnModel.getDN_SOURCE()==5 || (ParamaterModel.IsAgentSoft && dnModel.getDN_SOURCE()==3)) dnDetailModels.get(index).setFlag(2);
        DbDnInfo.getInstance().InsertDNDetailDB(dnDetailModels.get(index));
        if(dnModel.getDN_SOURCE()==5 || (ParamaterModel.IsAgentSoft && dnModel.getDN_SOURCE()==3))  UploadAgentDN.UpAgentDN(mHandler, dnModel,"");
        return isErrorStatus;
    }

    /**
     * 查找可以出库行（GolfaCode相同，出库数量未完成）
     * @param GolfaCode
     * @return
     */
    private static int findIndexByGolfaCode(List<DNDetailModel> dnDetailModels,String GolfaCode){
        int index=-1;
        int size=dnDetailModels.size();
        for(int i=0;i<size;i++){
            DNDetailModel dnDetailModel=dnDetailModels.get(i);
            if(dnDetailModel.getGOLFA_CODE()!=null && dnDetailModel.getGOLFA_CODE().equals(GolfaCode)){
                if(dnDetailModel.getSERIALS()==null) dnDetailModel.setSERIALS(new ArrayList<DNScanModel>());
                if(dnDetailModel.getDN_QTY()>dnDetailModel.getSERIALS().size()) {
                    index = i;
                    break;
                }
            }
        }
        return index;
    }

    /**
     * 设置非三菱条码解析项
     * @param barCodeModel
     * @param dnScanModel
     */
    public static void setOtherColumn(BarCodeModel barCodeModel, DNScanModel dnScanModel) {
        if((barCodeModel.getMAT_TYPE()==0 || barCodeModel.getMAT_TYPE()==99) && barCodeModel.getOtherCode()!=null){
            int size=barCodeModel.getOtherCode().size();
            int len=0;
            while(len<size){
                switch (len){
                    case 0:
                        dnScanModel.setEXTEND_FIELD1(barCodeModel.getOtherCode().get(len));
                        break;
                    case 1:
                        dnScanModel.setEXTEND_FIELD2(barCodeModel.getOtherCode().get(len));
                        break;
                    case 2:
                        dnScanModel.setEXTEND_FIELD3(barCodeModel.getOtherCode().get(len));
                        break;
                    case 3:
                        dnScanModel.setEXTEND_FIELD4(barCodeModel.getOtherCode().get(len));
                        break;
                    case 4:
                        dnScanModel.setEXTEND_FIELD5(barCodeModel.getOtherCode().get(len));
                        break;
                    case 5:
                        dnScanModel.setEXTEND_FIELD6(barCodeModel.getOtherCode().get(len));
                        break;
                }
                len++;
            }
        }
    }

}
