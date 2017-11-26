package com.xx.chinetek.method;

import com.xx.chinetek.method.DB.DbDnInfo;
import com.xx.chinetek.model.BarCodeModel;
import com.xx.chinetek.model.DBReturnModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by GHOST on 2017/11/16.
 */

public class Scan {
    /**
     * 条码扫描（非自建方式）
     * @param barCodeModels
     * @return
     * @throws Exception
     */
    public static int ScanBarccode(DbDnInfo dnInfo,DNModel dnModel, ArrayList<BarCodeModel> barCodeModels) throws Exception {
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
            DBReturnModel dbReturnModel = dnInfo.GetDNQty(dnModel.getAGENT_DN_NO(), condition);
            if (dbReturnModel.getDNQTY() < dbReturnModel.getSCANQTY() + barCodeModels.size()) {
                isErrorStatus = 1;
            }else {
                isErrorStatus = Checkbarcode(dnInfo,dnModel,barCodeModels, dnDetailModels, index);
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
    public static int getIndex(DNModel dnModel, List<DNDetailModel> dnDetailModels, BarCodeModel barCodeModel, DNDetailModel dnDetailModel) {
        dnDetailModel.setAGENT_DN_NO(dnModel.getAGENT_DN_NO());
        dnDetailModel.setGOLFA_CODE(barCodeModel.getGolfa_Code());
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
    private static int Checkbarcode(DbDnInfo dnInfo,DNModel dnModel,ArrayList<BarCodeModel> barCodeModels, List<DNDetailModel> dnDetailModels, int index) {
        int isErrorStatus=-1;
        for (BarCodeModel barCodeModel : barCodeModels) {
            //判断条码是否存在
            dnDetailModels.get(index).__setDaoSession(dnInfo.getDaoSession());
            List<DNScanModel> dnScanModels = dnDetailModels.get(index).getSERIALS();
            DNScanModel dnScanModel = new DNScanModel();
            dnScanModel.setSERIAL_NO(barCodeModel.getSerial_Number());
            int barcodeIndex = dnScanModels.indexOf(dnScanModel);
            if (barcodeIndex != -1) {
                isErrorStatus = 0;
                break;
            }

            index=findIndexByGolfaCode(dnDetailModels,barCodeModel.getGolfa_Code());
            if(index==-1){
                isErrorStatus = 1;
                break;
            }
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
            dnScanModel.setDEAL_SALE_DATE(new Date());
            dnScanModel.setMAT_TYPE(barCodeModel.getMAT_TYPE());
            setOtherColumn(barCodeModel, dnScanModel);

            dnDetailModels.get(index).setOPER_DATE(new Date());
            dnDetailModels.get(index).getSERIALS().add(dnScanModel);
            dnDetailModels.get(index).setDETAIL_STATUS("AC");
            dnDetailModels.get(index).setSTATUS(0);
        }
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
            if(dnDetailModel.getGOLFA_CODE().equals(GolfaCode)){
                if(dnDetailModel.getDN_QTY()>dnDetailModel.getSCAN_QTY()) {
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
        if(barCodeModel.getMAT_TYPE()==0){
            int size=barCodeModel.getOtherCode().size();
            int len=0;
            while(len<size){
                switch (len){
                    case 0:
                        dnScanModel.setType1(barCodeModel.getOtherCode().get(len));
                        break;
                    case 1:
                        dnScanModel.setType2(barCodeModel.getOtherCode().get(len));
                        break;
                    case 2:
                        dnScanModel.setType3(barCodeModel.getOtherCode().get(len));
                        break;
                    case 3:
                        dnScanModel.setType4(barCodeModel.getOtherCode().get(len));
                        break;
                    case 4:
                        dnScanModel.setType5(barCodeModel.getOtherCode().get(len));
                        break;
                    case 5:
                        dnScanModel.setType6(barCodeModel.getOtherCode().get(len));
                        break;
                }
                len++;
            }
        }
    }

}
