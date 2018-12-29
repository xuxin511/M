package com.xx.chinetek.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.security.PrivilegedAction;
import java.util.List;

/**
 * Created by GHOST on 2017/11/9.
 */

public class BarCodeModel implements Parcelable {

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
     * 1三菱 0非三菱 2:代理商
     */
    private Integer MAT_TYPE;

    /**
     * 代理商字段
     */
    private String ItemNo;

    /**
     * 代理商字段
     */
    private String ItemName;
    /**
     * 非三菱自定义条码解析内容
     */
    private List<String> OtherCode;

    private Integer  LINE_NO;


    public String getItemNo() {
        return ItemNo;
    }

    public void setItemNo(String itemNo) {
        ItemNo = itemNo;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public Integer getLINE_NO() {
        return LINE_NO;
    }

    public void setLINE_NO(Integer LINE_NO) {
        this.LINE_NO = LINE_NO;
    }

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

    public BarCodeModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Heading_Code);
        dest.writeString(this.Golfa_Code);
        dest.writeString(this.Serial_Number);
        dest.writeString(this.Packing_Date);
        dest.writeString(this.Place_Code);
        dest.writeString(this.Country_Code);
        dest.writeValue(this.MAT_TYPE);
        dest.writeString(this.ItemNo);
        dest.writeString(this.ItemName);
        dest.writeStringList(this.OtherCode);
        dest.writeValue(this.LINE_NO);
    }

    protected BarCodeModel(Parcel in) {
        this.Heading_Code = in.readString();
        this.Golfa_Code = in.readString();
        this.Serial_Number = in.readString();
        this.Packing_Date = in.readString();
        this.Place_Code = in.readString();
        this.Country_Code = in.readString();
        this.MAT_TYPE = (Integer) in.readValue(Integer.class.getClassLoader());
        this.ItemNo = in.readString();
        this.ItemName = in.readString();
        this.OtherCode = in.createStringArrayList();
        this.LINE_NO = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<BarCodeModel> CREATOR = new Creator<BarCodeModel>() {
        @Override
        public BarCodeModel createFromParcel(Parcel source) {
            return new BarCodeModel(source);
        }

        @Override
        public BarCodeModel[] newArray(int size) {
            return new BarCodeModel[size];
        }
    };
}
