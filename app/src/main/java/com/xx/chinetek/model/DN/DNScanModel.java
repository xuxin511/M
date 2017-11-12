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
        @Index(value = "AGENT_DN_NO,LINE_NO,ITEM_SERIAL_NO", unique = true)
}
)
public class DNScanModel implements Parcelable{
    private String AGENT_DN_NO;
    private Integer LINE_NO;
    private String ITEM_SERIAL_NO;
    private String PACKING_DATE;
    private String REGION;
    private String COUNTRY;
    private String DEAL_SALE_DATE;

    private String ITEM_NO;
    private String GOLFA_CODE;
    private String ITEM_STATUS;
    private Date UPDATE_DATE;
    private String ITEM_NAME;
    private Integer MAT_TYPE;


    public String getAGENT_DN_NO() {
        return AGENT_DN_NO;
    }

    public void setAGENT_DN_NO(String AGENT_DN_NO) {
        this.AGENT_DN_NO = AGENT_DN_NO;
    }

    public Integer getLINE_NO() {
        return LINE_NO;
    }

    public void setLINE_NO(Integer LINE_NO) {
        this.LINE_NO = LINE_NO;
    }

    public String getITEM_SERIAL_NO() {
        return ITEM_SERIAL_NO;
    }

    public void setITEM_SERIAL_NO(String ITEM_SERIAL_NO) {
        this.ITEM_SERIAL_NO = ITEM_SERIAL_NO;
    }

    public String getPACKING_DATE() {
        return PACKING_DATE;
    }

    public void setPACKING_DATE(String PACKING_DATE) {
        this.PACKING_DATE = PACKING_DATE;
    }

    public String getREGION() {
        return REGION;
    }

    public void setREGION(String REGION) {
        this.REGION = REGION;
    }

    public String getCOUNTRY() {
        return COUNTRY;
    }

    public void setCOUNTRY(String COUNTRY) {
        this.COUNTRY = COUNTRY;
    }

    public String getDEAL_SALE_DATE() {
        return DEAL_SALE_DATE;
    }

    public void setDEAL_SALE_DATE(String DEAL_SALE_DATE) {
        this.DEAL_SALE_DATE = DEAL_SALE_DATE;
    }

    public String getITEM_NO() {
        return ITEM_NO;
    }

    public void setITEM_NO(String ITEM_NO) {
        this.ITEM_NO = ITEM_NO;
    }

    public String getGOLFA_CODE() {
        return GOLFA_CODE;
    }

    public void setGOLFA_CODE(String GOLFA_CODE) {
        this.GOLFA_CODE = GOLFA_CODE;
    }

    public String getITEM_STATUS() {
        return ITEM_STATUS;
    }

    public void setITEM_STATUS(String ITEM_STATUS) {
        this.ITEM_STATUS = ITEM_STATUS;
    }

    public Date getUPDATE_DATE() {
        return UPDATE_DATE;
    }

    public void setUPDATE_DATE(Date UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }

    public String getITEM_NAME() {
        return ITEM_NAME;
    }

    public void setITEM_NAME(String ITEM_NAME) {
        this.ITEM_NAME = ITEM_NAME;
    }

    public Integer getMAT_TYPE() {
        return MAT_TYPE;
    }

    public void setMAT_TYPE(Integer MAT_TYPE) {
        this.MAT_TYPE = MAT_TYPE;
    }

    @Keep
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DNScanModel that = (DNScanModel) o;

        return ITEM_SERIAL_NO.trim().equals(that.ITEM_SERIAL_NO.trim());

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.AGENT_DN_NO);
        dest.writeValue(this.LINE_NO);
        dest.writeString(this.ITEM_SERIAL_NO);
        dest.writeString(this.PACKING_DATE);
        dest.writeString(this.REGION);
        dest.writeString(this.COUNTRY);
        dest.writeString(this.DEAL_SALE_DATE);
        dest.writeString(this.ITEM_NO);
        dest.writeString(this.GOLFA_CODE);
        dest.writeString(this.ITEM_STATUS);
        dest.writeLong(this.UPDATE_DATE != null ? this.UPDATE_DATE.getTime() : -1);
        dest.writeString(this.ITEM_NAME);
        dest.writeValue(this.MAT_TYPE);
    }

    public DNScanModel() {
    }

    protected DNScanModel(Parcel in) {
        this.AGENT_DN_NO = in.readString();
        this.LINE_NO = (Integer) in.readValue(Integer.class.getClassLoader());
        this.ITEM_SERIAL_NO = in.readString();
        this.PACKING_DATE = in.readString();
        this.REGION = in.readString();
        this.COUNTRY = in.readString();
        this.DEAL_SALE_DATE = in.readString();
        this.ITEM_NO = in.readString();
        this.GOLFA_CODE = in.readString();
        this.ITEM_STATUS = in.readString();
        long tmpUPDATE_DATE = in.readLong();
        this.UPDATE_DATE = tmpUPDATE_DATE == -1 ? null : new Date(tmpUPDATE_DATE);
        this.ITEM_NAME = in.readString();
        this.MAT_TYPE = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    @Generated(hash = 133136757)
    public DNScanModel(String AGENT_DN_NO, Integer LINE_NO, String ITEM_SERIAL_NO,
            String PACKING_DATE, String REGION, String COUNTRY,
            String DEAL_SALE_DATE, String ITEM_NO, String GOLFA_CODE,
            String ITEM_STATUS, Date UPDATE_DATE, String ITEM_NAME,
            Integer MAT_TYPE) {
        this.AGENT_DN_NO = AGENT_DN_NO;
        this.LINE_NO = LINE_NO;
        this.ITEM_SERIAL_NO = ITEM_SERIAL_NO;
        this.PACKING_DATE = PACKING_DATE;
        this.REGION = REGION;
        this.COUNTRY = COUNTRY;
        this.DEAL_SALE_DATE = DEAL_SALE_DATE;
        this.ITEM_NO = ITEM_NO;
        this.GOLFA_CODE = GOLFA_CODE;
        this.ITEM_STATUS = ITEM_STATUS;
        this.UPDATE_DATE = UPDATE_DATE;
        this.ITEM_NAME = ITEM_NAME;
        this.MAT_TYPE = MAT_TYPE;
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
