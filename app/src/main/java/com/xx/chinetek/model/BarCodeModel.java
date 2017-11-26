package com.xx.chinetek.model;

import java.util.List;

/**
 * Created by GHOST on 2017/11/9.
 */

public class BarCodeModel {

    /**
     * 识别码（小包装）
     */
    private String Heading_Code;

    /**
     * Golfa_Code
     */
    private String Golfa_Code;

    /**
     * 序列号
     */
    private String Serial_Number;

    /**
     * 装箱时间
     */
    private String  Packing_Date;

    /**
     * 场所
     */
    private String  Place_Code;

    /**
     * 原产国
     */
    private String  Country_Code;
    /**
     * 1三菱 0非三菱
     */
    private Integer MAT_TYPE;
    /**
     * 非三菱自定义条码解析内容
     */
    private List<String> OtherCode;


    public Integer getMAT_TYPE() {
        return MAT_TYPE;
    }

    public void setMAT_TYPE(Integer MAT_TYPE) {
        this.MAT_TYPE = MAT_TYPE;
    }

    public List<String> getOtherCode() {
        return OtherCode;
    }

    public void setOtherCode(List<String> otherCode) {
        OtherCode = otherCode;
    }

    public String getHeading_Code() {
        return Heading_Code;
    }

    public void setHeading_Code(String heading_Code) {
        Heading_Code = heading_Code;
    }

    public String getGolfa_Code() {
        return Golfa_Code;
    }

    public void setGolfa_Code(String golfa_Code) {
        Golfa_Code = golfa_Code;
    }

    public String getSerial_Number() {
        return Serial_Number;
    }

    public void setSerial_Number(String serial_Number) {
        Serial_Number = serial_Number;
    }

    public String getPacking_Date() {
        return Packing_Date;
    }

    public void setPacking_Date(String packing_Date) {
        Packing_Date = packing_Date;
    }

    public String getPlace_Code() {
        return Place_Code;
    }

    public void setPlace_Code(String place_Code) {
        Place_Code = place_Code;
    }

    public String getCountry_Code() {
        return Country_Code;
    }

    public void setCountry_Code(String country_Code) {
        Country_Code = country_Code;
    }
}
