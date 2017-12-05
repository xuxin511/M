package com.xx.chinetek.method;

import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.util.log.LogUtil;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.BarCodeModel;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.Base.ParamaterModel;

import java.util.ArrayList;

import static com.xx.chinetek.model.Base.TAG_RESULT.TAG_ScanBarcode;

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
                    barCodeModel.setGolfa_Code(KeyCode.trim());
                    barCodeModel.setMAT_TYPE(0); //非三菱
                    barCodeModel.setLINE_NO(null);
                    SetNotMitSubshiCode(Barcode, barCodeModel);
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

    private static void SetNotMitSubshiCode(String Barcode, BarCodeModel barCodeModel) {
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
                break;
            barCodeModel.setSerial_Number(data[j].trim().trim());
            barCodeModels.add(barCodeModel);
        }
        return  barCodeModels;
    }
}
