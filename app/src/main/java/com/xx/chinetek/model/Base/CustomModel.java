package com.xx.chinetek.model.Base;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;

/**
 * Created by GHOST on 2017/11/2.
 */

@Entity(
        indexes={
                @Index(value = "CUSTOMER,SALES_ORGANIZATION,DISTRIBUTION_CHANNEL,DIVISION,PARTNER_FUNCTION,PARTNER_COUNTER", unique = true)
        }
)
public class CustomModel implements Parcelable {
    /**
     * 客户编号
     */
    private String CUSTOMER;

    /**
     * 客户编号
     */
    private String NAME;

    /**
     * 上一级代理商
     */
    private String SALES_ORGANIZATION;

    /**
     * unknown，可能是渠道号
     */
    private String DISTRIBUTION_CHANNEL;

    /**
     * //产品线
     */
    private String DIVISION;

    /**
     * //级别，Z2二级，Z3客户
     */
    private String PARTNER_FUNCTION;

    /**
     * //unknown
     */
    private String PARTNER_COUNTER;

    /**
     * //上一级代理商
     */
    private String CUST_NO_OF_BUSINESS_PARTNER;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.CUSTOMER);
        dest.writeString(this.NAME);
        dest.writeString(this.SALES_ORGANIZATION);
        dest.writeString(this.DISTRIBUTION_CHANNEL);
        dest.writeString(this.DIVISION);
        dest.writeString(this.PARTNER_FUNCTION);
        dest.writeString(this.PARTNER_COUNTER);
        dest.writeString(this.CUST_NO_OF_BUSINESS_PARTNER);
    }

    public String getCUSTOMER() {
        return this.CUSTOMER;
    }

    public void setCUSTOMER(String CUSTOMER) {
        this.CUSTOMER = CUSTOMER;
    }

    public String getNAME() {
        return this.NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getSALES_ORGANIZATION() {
        return this.SALES_ORGANIZATION;
    }

    public void setSALES_ORGANIZATION(String SALES_ORGANIZATION) {
        this.SALES_ORGANIZATION = SALES_ORGANIZATION;
    }

    public String getDISTRIBUTION_CHANNEL() {
        return this.DISTRIBUTION_CHANNEL;
    }

    public void setDISTRIBUTION_CHANNEL(String DISTRIBUTION_CHANNEL) {
        this.DISTRIBUTION_CHANNEL = DISTRIBUTION_CHANNEL;
    }

    public String getDIVISION() {
        return this.DIVISION;
    }

    public void setDIVISION(String DIVISION) {
        this.DIVISION = DIVISION;
    }

    public String getPARTNER_FUNCTION() {
        return this.PARTNER_FUNCTION;
    }

    public void setPARTNER_FUNCTION(String PARTNER_FUNCTION) {
        this.PARTNER_FUNCTION = PARTNER_FUNCTION;
    }

    public String getPARTNER_COUNTER() {
        return this.PARTNER_COUNTER;
    }

    public void setPARTNER_COUNTER(String PARTNER_COUNTER) {
        this.PARTNER_COUNTER = PARTNER_COUNTER;
    }

    public String getCUST_NO_OF_BUSINESS_PARTNER() {
        return this.CUST_NO_OF_BUSINESS_PARTNER;
    }

    public void setCUST_NO_OF_BUSINESS_PARTNER(String CUST_NO_OF_BUSINESS_PARTNER) {
        this.CUST_NO_OF_BUSINESS_PARTNER = CUST_NO_OF_BUSINESS_PARTNER;
    }

    public CustomModel() {
    }

    protected CustomModel(Parcel in) {
        this.CUSTOMER = in.readString();
        this.NAME = in.readString();
        this.SALES_ORGANIZATION = in.readString();
        this.DISTRIBUTION_CHANNEL = in.readString();
        this.DIVISION = in.readString();
        this.PARTNER_FUNCTION = in.readString();
        this.PARTNER_COUNTER = in.readString();
        this.CUST_NO_OF_BUSINESS_PARTNER = in.readString();
    }

    @Generated(hash = 1400213901)
    public CustomModel(String CUSTOMER, String NAME, String SALES_ORGANIZATION,
            String DISTRIBUTION_CHANNEL, String DIVISION, String PARTNER_FUNCTION,
            String PARTNER_COUNTER, String CUST_NO_OF_BUSINESS_PARTNER) {
        this.CUSTOMER = CUSTOMER;
        this.NAME = NAME;
        this.SALES_ORGANIZATION = SALES_ORGANIZATION;
        this.DISTRIBUTION_CHANNEL = DISTRIBUTION_CHANNEL;
        this.DIVISION = DIVISION;
        this.PARTNER_FUNCTION = PARTNER_FUNCTION;
        this.PARTNER_COUNTER = PARTNER_COUNTER;
        this.CUST_NO_OF_BUSINESS_PARTNER = CUST_NO_OF_BUSINESS_PARTNER;
    }

    public static final Creator<CustomModel> CREATOR = new Creator<CustomModel>() {
        @Override
        public CustomModel createFromParcel(Parcel source) {
            return new CustomModel(source);
        }

        @Override
        public CustomModel[] newArray(int size) {
            return new CustomModel[size];
        }
    };
}
