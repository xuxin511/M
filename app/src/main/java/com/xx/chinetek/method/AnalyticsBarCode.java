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


    public static ArrayList<BarCodeModel> CheckBarcode(String Barcode) throws Exception{
        ArrayList<BarCodeModel> barCodeModels=new ArrayList<>();
        //判断是否启用非三菱条码
        Boolean isMitsubshiCode=true;
        if(ParamaterModel.baseparaModel.getCusBarcodeRule()!=null && ParamaterModel.baseparaModel.getCusBarcodeRule().getUsed()){
            int len=Barcode.length();
            if(len== ParamaterModel.baseparaModel.getCusBarcodeRule().getBarcodeLength()){
                String KeyCode=Barcode.substring(ParamaterModel.baseparaModel.getCusBarcodeRule().getKeyStartIndex()-1,
                        ParamaterModel.baseparaModel.getCusBarcodeRule().getKeyEndIndex());
                MaterialModel materialModel= DbBaseInfo.getInstance().GetItemName(KeyCode);
                if(materialModel==null) {//物料没有记录，非三菱条码
                    isMitsubshiCode=false;
                    BarCodeModel barCodeModel=new BarCodeModel();
                    barCodeModel.setGolfa_Code(KeyCode);
                    barCodeModel.setMAT_TYPE(0); //非三菱
                    String serialNo=Barcode.substring(ParamaterModel.baseparaModel.getCusBarcodeRule().getSerialStartIndex()-1,
                            ParamaterModel.baseparaModel.getCusBarcodeRule().getSerialEndIndex());
                    barCodeModel.setSerial_Number(serialNo);
                    barCodeModel.setOtherCode(new ArrayList<String>());
                    int otherSize=ParamaterModel.baseparaModel.getCusBarcodeRule().getOtherColumn().size();
                    for(int i=0;i<otherSize;i++){
                       String[] indexs=ParamaterModel.baseparaModel.getCusBarcodeRule().getOtherColumn().get(i).split("-");
                       int start=Integer.parseInt(indexs[0].toString())-1;
                       int end=Integer.parseInt(indexs[1].toString());
                       String column=Barcode.substring(start,end);
                        barCodeModel.getOtherCode().add(column);
                    }
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
                barCodeModel.setGolfa_Code(Barcode.substring(1,7));
                barCodeModel.setSerial_Number(Barcode.substring(7,24));
                barCodeModel.setPacking_Date(Barcode.substring(24,32));
                break;
            case "2":
                barCodeModel.setGolfa_Code(Barcode.substring(1,14));
                barCodeModel.setSerial_Number(Barcode.substring(14,31));
                barCodeModel.setPacking_Date(Barcode.substring(31,39));
                break;
            case "3":
            case "4":
                barCodeModel.setGolfa_Code(Barcode.substring(1,19));
                barCodeModel.setSerial_Number(Barcode.substring(19,36));
                barCodeModel.setPacking_Date(Barcode.substring(36,44));
                barCodeModel.setPlace_Code(Barcode.substring(44,47));
                barCodeModel.setCountry_Code(Barcode.substring(47,49));
                break;
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
        String Golfa_Code=Barcode.substring(8,14);
        String Packing_Date=Barcode.substring(14,22);
        for (int j = 0; j < data.length; j++)
        {
            BarCodeModel barCodeModel=new BarCodeModel();
            barCodeModel.setGolfa_Code(Golfa_Code);
            barCodeModel.setMAT_TYPE(1);//三菱
            barCodeModel.setPacking_Date(Packing_Date);
            if(data[j].trim().length()!=7)
                break;
            barCodeModel.setSerial_Number(data[j].trim());
            barCodeModels.add(barCodeModel);
        }
        return  barCodeModels;
    }
}
