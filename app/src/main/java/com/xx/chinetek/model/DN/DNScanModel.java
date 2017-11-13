package com.xx.chinetek.model.DN;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Keep;

import java.util.Date;

/**
 * Created by GHOST on 2017/10/25.
 */
@Entity(
        indexes = {
        @Index(value = "AGENT_DN_NO,LINE_NO,SERIAL_NO", unique = true)
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
    private Date DEAL_SALE_DATE;

    private String ITEM_NAME;
    /**
     * 1三菱 0非三菱
     */
    private Integer MAT_TYPE;

    @Keep
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DNScanModel that = (DNScanModel) o;

        return SERIAL_NO.trim().equals(that.SERIAL_NO.trim());

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


    public Date getDEAL_SALE_DATE() {
        return this.DEAL_SALE_DATE;
    }


    public void setDEAL_SALE_DATE(Date DEAL_SALE_DATE) {
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


    public String getITEM_STATUS() {
        return this.ITEM_STATUS;
    }


    public void setITEM_STATUS(String ITEM_STATUS) {
        this.ITEM_STATUS = ITEM_STATUS;
    }

    public DNScanModel() {
    }


    @Generated(hash = 1876084327)
    public DNScanModel(String AGENT_DN_NO, Integer LINE_NO, String SERIAL_NO,
            String PACKING_DATE, String REGION, String COUNTRY, String ITEM_NO,
            String GOLFA_CODE, String STATUS, String ITEM_STATUS, Date DEAL_SALE_DATE,
            String ITEM_NAME, Integer MAT_TYPE) {
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
        dest.writeLong(this.DEAL_SALE_DATE != null ? this.DEAL_SALE_DATE.getTime() : -1);
        dest.writeString(this.ITEM_NAME);
        dest.writeValue(this.MAT_TYPE);
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
        long tmpDEAL_SALE_DATE = in.readLong();
        this.DEAL_SALE_DATE = tmpDEAL_SALE_DATE == -1 ? null : new Date(tmpDEAL_SALE_DATE);
        this.ITEM_NAME = in.readString();
        this.MAT_TYPE = (Integer) in.readValue(Integer.class.getClassLoader());
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
