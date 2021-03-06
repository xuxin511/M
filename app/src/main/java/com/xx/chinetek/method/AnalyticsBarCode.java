package com.xx.chinetek.method;

import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.BarCodeModel;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.Base.ParamaterModel;

import java.util.ArrayList;

/**
 * Created by GHOST on 2017/11/9.
 */

public class AnalyticsBarCode {


    public static ArrayList<BarCodeModel> CheckBarcode(String Barcode,int selectRule) throws Exception{

        ArrayList<BarCodeModel> barCodeModels=new ArrayList<>();
        //判断是否启用非三菱条码
        Boolean isMitsubshiCode=true;
        if(ParamaterModel.baseparaModel.getCusBarcodeRule()!=null && ParamaterModel.baseparaModel.getCusBarcodeRule().getUsed()){
            int len=Barcode.length();
            if(len== ParamaterModel.baseparaModel.getCusBarcodeRule().getBarcodeRules().get(selectRule).getBarcodeLength()){
                String KeyCode=Barcode.substring(ParamaterModel.baseparaModel.getCusBarcodeRule().getBarcodeRules().get(selectRule).getKeyStartIndex()-1,
                        ParamaterModel.baseparaModel.getCusBarcodeRule().getBarcodeRules().get(selectRule).getKeyEndIndex());
                MaterialModel materialModel= DbBaseInfo.getInstance().GetItemName(KeyCode);
                if(materialModel==null) {//物料没有记录，非三菱条码
                    isMitsubshiCode=false;
                    BarCodeModel barCodeModel=new BarCodeModel();
                    barCodeModel.setGolfa_Code(KeyCode.trim());
                    barCodeModel.setMAT_TYPE(0); //非三菱
                    barCodeModel.setLINE_NO(null);
                    SetNotMitSubshiCode(Barcode, barCodeModel,selectRule);
                    barCodeModels.add(barCodeModel);
                }
            }
        }
        if(isMitsubshiCode) {
            if(Barcode.length()<32) throw  new Exception(BaseApplication.context.getString(R.string.Msg_BarcodeNotmatch));
                barCodeModels = Barcode.length() < 400 ?
                        AnalyticsBarCode.AnalyticsSmall(Barcode)
                        : AnalyticsBarCode.AnalyticsLarge(Barcode);

        }
        return  barCodeModels;
    }

    private static void SetNotMitSubshiCode(String Barcode, BarCodeModel barCodeModel,int selectRule) {
        String serialNo=Barcode.substring(ParamaterModel.baseparaModel.getCusBarcodeRule().getBarcodeRules().get(selectRule).getSerialStartIndex()-1,
                ParamaterModel.baseparaModel.getCusBarcodeRule().getBarcodeRules().get(selectRule).getSerialEndIndex());
        barCodeModel.setSerial_Number(serialNo);
        barCodeModel.setOtherCode(new ArrayList<String>());
        int otherSize=ParamaterModel.baseparaModel.getCusBarcodeRule().getBarcodeRules().get(selectRule).getOtherColumn().size();
        for(int i=0;i<otherSize;i++){
           String[] indexs=ParamaterModel.baseparaModel.getCusBarcodeRule().getBarcodeRules().get(selectRule).getOtherColumn().get(i).split("-");
           int start=Integer.parseInt(indexs[0].toString())-1;
           int end=Integer.parseInt(indexs[1].toString());
           String column=Barcode.substring(start,end);
            barCodeModel.getOtherCode().add(column);
        }
    }

    /**
     * 解析小包装
     * @param Barcode
     * @return
     */
    private static ArrayList<BarCodeModel> AnalyticsSmall(String Barcode) throws Exception{
        ArrayList<BarCodeModel> barCodeModels=new ArrayList<>();
        BarCodeModel barCodeModel=new BarCodeModel();
        String headCode=Barcode.substring(0,1);
        barCodeModel.setHeading_Code(headCode);
        barCodeModel.setMAT_TYPE(1);//三菱
        switch (headCode){
            case "1":
                barCodeModel.setGolfa_Code(Barcode.substring(1,7).trim());
                barCodeModel.setSerial_Number(Barcode.substring(7,24).trim());
                barCodeModel.setPacking_Date(Barcode.substring(24,32).trim());
                break;
            case "2":
                barCodeModel.setGolfa_Code(Barcode.substring(1,14).trim());
                barCodeModel.setSerial_Number(Barcode.substring(14,31).trim());
                barCodeModel.setPacking_Date(Barcode.substring(31,39).trim());
                break;
            case "3":
            case "4":
                barCodeModel.setGolfa_Code(Barcode.substring(1,19).trim());
                barCodeModel.setSerial_Number(Barcode.substring(19,36).trim());
                barCodeModel.setPacking_Date(Barcode.substring(36,44).trim());
                barCodeModel.setPlace_Code(Barcode.substring(44,47).trim());
                barCodeModel.setCountry_Code(Barcode.substring(47,49).trim());
                break;
        }
        if(barCodeModel.getGolfa_Code()==null){
            throw  new Exception(BaseApplication.context.getString(R.string.Msg_BarcodeNotmatch));
        }
        barCodeModels.add(barCodeModel);
        return barCodeModels;
    }

    /**
     * 解析大包装
     * @param Barcode
     * @return
     */
    private static ArrayList<BarCodeModel> AnalyticsLarge(String Barcode){
        ArrayList<BarCodeModel> barCodeModels=new ArrayList<>();
        String temp_data = Barcode.substring(48);
        String[] data = temp_data.split(" ");
        String Golfa_Code=Barcode.substring(8,14).trim();
        String Packing_Date=Barcode.substring(14,22).trim();
        for (int j = 0; j < data.length; j++)
        {
            BarCodeModel barCodeModel=new BarCodeModel();
            barCodeModel.setGolfa_Code(Golfa_Code);
            barCodeModel.setMAT_TYPE(1);//三菱
            barCodeModel.setPacking_Date(Packing_Date);
            if(data[j].trim().length()!=7)
                continue;
            barCodeModel.setSerial_Number(data[j].trim().trim());
            barCodeModels.add(barCodeModel);
        }
        return  barCodeModels;
    }
}
