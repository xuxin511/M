package com.xx.chinetek.method;

import android.text.TextUtils;

import com.xx.chinetek.chineteklib.base.BaseApplication;
import com.xx.chinetek.chineteklib.util.dialog.MessageBox;
import com.xx.chinetek.mitsubshi.R;
import com.xx.chinetek.model.BarCodeModel;
import com.xx.chinetek.model.Base.ParamaterModel;

import java.util.ArrayList;

/**
 * Created by GHOST on 2017/11/9.
 */

public class AnalyticsBarCode {


    public static ArrayList<BarCodeModel> CheckBarcode(String Barcode){
        ArrayList<BarCodeModel> barCodeModels=new ArrayList<>();
        //判断是否启用非三菱条码
        if(ParamaterModel.cusBarcodeRule!=null && ParamaterModel.cusBarcodeRule.getUsed()){
            Boolean isCheck=true;
            if(TextUtils.isEmpty(ParamaterModel.cusBarcodeRule.getStartWords())){
                if(!Barcode.startsWith(ParamaterModel.cusBarcodeRule.getStartWords())){
                    isCheck=false;
                }
            }
            if(ParamaterModel.cusBarcodeRule.getBarcodeLength()!=0){
               if(Barcode.length()!=ParamaterModel.cusBarcodeRule.getBarcodeLength()){
                    isCheck=false;
                }
            }
            if(!isCheck){
                MessageBox.Show(BaseApplication.context,BaseApplication.context.getString(R.string.Msg_BarcodeNotmatch));
                return new ArrayList<>();
            }
            BarCodeModel barCodeModel=new BarCodeModel();
            barCodeModel.setSerial_Number(Barcode);
            barCodeModel.setGolfa_Code(Barcode);
            barCodeModels.add(barCodeModel);
        }else{
            barCodeModels=  Barcode.length() < 400 ?
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
    private static ArrayList<BarCodeModel> AnalyticsSmall(String Barcode){
        ArrayList<BarCodeModel> barCodeModels=new ArrayList<>();
        BarCodeModel barCodeModel=new BarCodeModel();
        String headCode=Barcode.substring(0,1);
        barCodeModel.setHeading_Code(headCode);
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
            barCodeModel.setPacking_Date(Packing_Date);
            if(data[j].trim().length()!=7)
                break;
            barCodeModel.setSerial_Number(data[j].trim());
            barCodeModels.add(barCodeModel);
        }
        return  barCodeModels;
    }
}
