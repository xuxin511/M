package com.xx.chinetek.model.DN;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;

/**
 * Created by GHOST on 2017/10/25.
 */
@Entity(
        indexes = {
        @Index(value = "AGENT_DN_NO,LINE_NO,SERIAL_NO" ,unique = true)//,STATUS
}
)
public class DNScanModel implements Parcelable{
    private String AGENT_DN_NO;
    private Integer LINE_NO;
    private String SERIAL_NO;
    private String PACKING_DATE;
    private String REGION;
    private String COUNTRY;
    private String ITEM_NO;
    private String GOLFA_CODE;
    private String STATUS;
    private String ITEM_STATUS;
    /**
     * 扫描时间
     */
    private String DEAL_SALE_DATE;

    private String ITEM_NAME;
    /**
     * 1三菱 0非三菱 2代理商
     */
    private Integer MAT_TYPE;

    private String EXTEND_FIELD1;
    private String EXTEND_FIELD2;
    private String EXTEND_FIELD3;
    private String EXTEND_FIELD4;
    private String EXTEND_FIELD5;
    private String EXTEND_FIELD6;

    @Keep
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DNScanModel that = (DNScanModel) o;
        if(that.GOLFA_CODE==null)
            return SERIAL_NO.trim().equals(that.SERIAL_NO.trim());
        else
            return SERIAL_NO.trim().equals(that.SERIAL_NO.trim()) && GOLFA_CODE.trim().equals(that.GOLFA_CODE.trim());

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.AGENT_DN_NO);
        dest.writeValue(this.LINE_NO);
        dest.writeString(this.SERIAL_NO);
        dest.writeString(this.PACKING_DATE);
        dest.writeString(this.REGION);
        dest.writeString(this.COUNTRY);
        dest.writeString(this.ITEM_NO);
        dest.writeString(this.GOLFA_CODE);
        dest.writeString(this.STATUS);
        dest.writeString(this.ITEM_STATUS);
        dest.writeString(this.DEAL_SALE_DATE);
        dest.writeString(this.ITEM_NAME);
        dest.writeValue(this.MAT_TYPE);
        dest.writeString(this.EXTEND_FIELD1);
        dest.writeString(this.EXTEND_FIELD2);
        dest.writeString(this.EXTEND_FIELD3);
        dest.writeString(this.EXTEND_FIELD4);
        dest.writeString(this.EXTEND_FIELD5);
        dest.writeString(this.EXTEND_FIELD6);
    }


    public String getAGENT_DN_NO() {
        return this.AGENT_DN_NO;
    }


    public void setAGENT_DN_NO(String AGENT_DN_NO) {
        this.AGENT_DN_NO = AGENT_DN_NO;
    }


    public Integer getLINE_NO() {
        return this.LINE_NO;
    }


    public void setLINE_NO(Integer LINE_NO) {
        this.LINE_NO = LINE_NO;
    }


    public String getSERIAL_NO() {
        return this.SERIAL_NO;
    }


    public void setSERIAL_NO(String SERIAL_NO) {
        this.SERIAL_NO = SERIAL_NO;
    }


    public String getPACKING_DATE() {
        return this.PACKING_DATE;
    }


    public void setPACKING_DATE(String PACKING_DATE) {
        this.PACKING_DATE = PACKING_DATE;
    }


    public String getREGION() {
        return this.REGION;
    }


    public void setREGION(String REGION) {
        this.REGION = REGION;
    }


    public String getCOUNTRY() {
        return this.COUNTRY;
    }


    public void setCOUNTRY(String COUNTRY) {
        this.COUNTRY = COUNTRY;
    }


    public String getITEM_NO() {
        return this.ITEM_NO;
    }


    public void setITEM_NO(String ITEM_NO) {
        this.ITEM_NO = ITEM_NO;
    }


    public String getGOLFA_CODE() {
        return this.GOLFA_CODE;
    }


    public void setGOLFA_CODE(String GOLFA_CODE) {
        this.GOLFA_CODE = GOLFA_CODE;
    }


    public String getSTATUS() {
        return this.STATUS;
    }


    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }


    public String getITEM_STATUS() {
        return this.ITEM_STATUS;
    }


    public void setITEM_STATUS(String ITEM_STATUS) {
        this.ITEM_STATUS = ITEM_STATUS;
    }


    public String getDEAL_SALE_DATE() {
        return this.DEAL_SALE_DATE;
    }


    public void setDEAL_SALE_DATE(String DEAL_SALE_DATE) {
        this.DEAL_SALE_DATE = DEAL_SALE_DATE;
    }


    public String getITEM_NAME() {
        return this.ITEM_NAME;
    }


    public void setITEM_NAME(String ITEM_NAME) {
        this.ITEM_NAME = ITEM_NAME;
    }


    public Integer getMAT_TYPE() {
        return this.MAT_TYPE;
    }


    public void setMAT_TYPE(Integer MAT_TYPE) {
        this.MAT_TYPE = MAT_TYPE;
    }


    public String getEXTEND_FIELD1() {
        return this.EXTEND_FIELD1;
    }


    public void setEXTEND_FIELD1(String EXTEND_FIELD1) {
        this.EXTEND_FIELD1 = EXTEND_FIELD1;
    }


    public String getEXTEND_FIELD2() {
        return this.EXTEND_FIELD2;
    }


    public void setEXTEND_FIELD2(String EXTEND_FIELD2) {
        this.EXTEND_FIELD2 = EXTEND_FIELD2;
    }


    public String getEXTEND_FIELD3() {
        return this.EXTEND_FIELD3;
    }


    public void setEXTEND_FIELD3(String EXTEND_FIELD3) {
        this.EXTEND_FIELD3 = EXTEND_FIELD3;
    }


    public String getEXTEND_FIELD4() {
        return this.EXTEND_FIELD4;
    }


    public void setEXTEND_FIELD4(String EXTEND_FIELD4) {
        this.EXTEND_FIELD4 = EXTEND_FIELD4;
    }


    public String getEXTEND_FIELD5() {
        return this.EXTEND_FIELD5;
    }


    public void setEXTEND_FIELD5(String EXTEND_FIELD5) {
        this.EXTEND_FIELD5 = EXTEND_FIELD5;
    }


    public String getEXTEND_FIELD6() {
        return this.EXTEND_FIELD6;
    }


    public void setEXTEND_FIELD6(String EXTEND_FIELD6) {
        this.EXTEND_FIELD6 = EXTEND_FIELD6;
    }

    public DNScanModel() {
    }

    protected DNScanModel(Parcel in) {
        this.AGENT_DN_NO = in.readString();
        this.LINE_NO = (Integer) in.readValue(Integer.class.getClassLoader());
        this.SERIAL_NO = in.readString();
        this.PACKING_DATE = in.readString();
        this.REGION = in.readString();
        this.COUNTRY = in.readString();
        this.ITEM_NO = in.readString();
        this.GOLFA_CODE = in.readString();
        this.STATUS = in.readString();
        this.ITEM_STATUS = in.readString();
        this.DEAL_SALE_DATE = in.readString();
        this.ITEM_NAME = in.readString();
        this.MAT_TYPE = (Integer) in.readValue(Integer.class.getClassLoader());
        this.EXTEND_FIELD1 = in.readString();
        this.EXTEND_FIELD2 = in.readString();
        this.EXTEND_FIELD3 = in.readString();
        this.EXTEND_FIELD4 = in.readString();
        this.EXTEND_FIELD5 = in.readString();
        this.EXTEND_FIELD6 = in.readString();
    }


    @Generated(hash = 192327553)
    public DNScanModel(String AGENT_DN_NO, Integer LINE_NO, String SERIAL_NO,
            String PACKING_DATE, String REGION, String COUNTRY, String ITEM_NO,
            String GOLFA_CODE, String STATUS, String ITEM_STATUS,
            String DEAL_SALE_DATE, String ITEM_NAME, Integer MAT_TYPE,
            String EXTEND_FIELD1, String EXTEND_FIELD2, String EXTEND_FIELD3,
            String EXTEND_FIELD4, String EXTEND_FIELD5, String EXTEND_FIELD6) {
        this.AGENT_DN_NO = AGENT_DN_NO;
        this.LINE_NO = LINE_NO;
        this.SERIAL_NO = SERIAL_NO;
        this.PACKING_DATE = PACKING_DATE;
        this.REGION = REGION;
        this.COUNTRY = COUNTRY;
        this.ITEM_NO = ITEM_NO;
        this.GOLFA_CODE = GOLFA_CODE;
        this.STATUS = STATUS;
        this.ITEM_STATUS = ITEM_STATUS;
        this.DEAL_SALE_DATE = DEAL_SALE_DATE;
        this.ITEM_NAME = ITEM_NAME;
        this.MAT_TYPE = MAT_TYPE;
        this.EXTEND_FIELD1 = EXTEND_FIELD1;
        this.EXTEND_FIELD2 = EXTEND_FIELD2;
        this.EXTEND_FIELD3 = EXTEND_FIELD3;
        this.EXTEND_FIELD4 = EXTEND_FIELD4;
        this.EXTEND_FIELD5 = EXTEND_FIELD5;
        this.EXTEND_FIELD6 = EXTEND_FIELD6;
    }

    public static final Creator<DNScanModel> CREATOR = new Creator<DNScanModel>() {
        @Override
        public DNScanModel createFromParcel(Parcel source) {
            return new DNScanModel(source);
        }

        @Override
        public DNScanModel[] newArray(int size) {
            return new DNScanModel[size];
        }
    };
}
