package com.xx.chinetek.model.DN;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;

/**
 * Created by GHOST on 2017/10/25.
 */

@Entity
public class DNModel implements Parcelable {

    /*
    出库单号
     */
    @Unique
    private String AGENT_DN_NO;
    /*
    出库日期
     */
    private Date DN_DATE;
    /*
      出库状态
    */
    private String DN_STATUS;
    /*
    一级代理商编码
    */
    private String LEVEL_1_AGENT_NO;
    /*
一级代理商名称
*/
    private String LEVEL_1_AGENT_NAME;
    /*
二级代理商编码
*/
    private String LEVEL_2_AGENT_NO;
    /*
二级代理商名称
*/
    private String LEVEL_2_AGENT_NAME;
    /*
    客户编码
     */
    private String CUSTOM_NO;
    /*
    客户名称
     */
    private String CUSTOM_NAME;
    /*
    出库数量
     */
    private Float DN_QTY;
    /*
    修改人
     */
    private String UPDATE_USER;
    /*
    修改日期
     */
    private Date UPDATE_DATE;
    /*
 单据来源
  */
    private String DN_SOURCE;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.AGENT_DN_NO);
        dest.writeLong(this.DN_DATE != null ? this.DN_DATE.getTime() : -1);
        dest.writeString(this.DN_STATUS);
        dest.writeString(this.LEVEL_1_AGENT_NO);
        dest.writeString(this.LEVEL_1_AGENT_NAME);
        dest.writeString(this.LEVEL_2_AGENT_NO);
        dest.writeString(this.LEVEL_2_AGENT_NAME);
        dest.writeString(this.CUSTOM_NO);
        dest.writeString(this.CUSTOM_NAME);
        dest.writeValue(this.DN_QTY);
        dest.writeString(this.UPDATE_USER);
        dest.writeLong(this.UPDATE_DATE != null ? this.UPDATE_DATE.getTime() : -1);
        dest.writeString(this.DN_SOURCE);
    }

    public String getAGENT_DN_NO() {
        return this.AGENT_DN_NO;
    }

    public void setAGENT_DN_NO(String AGENT_DN_NO) {
        this.AGENT_DN_NO = AGENT_DN_NO;
    }

    public Date getDN_DATE() {
        return this.DN_DATE;
    }

    public void setDN_DATE(Date DN_DATE) {
        this.DN_DATE = DN_DATE;
    }

    public String getDN_STATUS() {
        return this.DN_STATUS;
    }

    public void setDN_STATUS(String DN_STATUS) {
        this.DN_STATUS = DN_STATUS;
    }

    public String getLEVEL_1_AGENT_NO() {
        return this.LEVEL_1_AGENT_NO;
    }

    public void setLEVEL_1_AGENT_NO(String LEVEL_1_AGENT_NO) {
        this.LEVEL_1_AGENT_NO = LEVEL_1_AGENT_NO;
    }

    public String getLEVEL_1_AGENT_NAME() {
        return this.LEVEL_1_AGENT_NAME;
    }

    public void setLEVEL_1_AGENT_NAME(String LEVEL_1_AGENT_NAME) {
        this.LEVEL_1_AGENT_NAME = LEVEL_1_AGENT_NAME;
    }

    public String getLEVEL_2_AGENT_NO() {
        return this.LEVEL_2_AGENT_NO;
    }

    public void setLEVEL_2_AGENT_NO(String LEVEL_2_AGENT_NO) {
        this.LEVEL_2_AGENT_NO = LEVEL_2_AGENT_NO;
    }

    public String getLEVEL_2_AGENT_NAME() {
        return this.LEVEL_2_AGENT_NAME;
    }

    public void setLEVEL_2_AGENT_NAME(String LEVEL_2_AGENT_NAME) {
        this.LEVEL_2_AGENT_NAME = LEVEL_2_AGENT_NAME;
    }

    public String getCUSTOM_NO() {
        return this.CUSTOM_NO;
    }

    public void setCUSTOM_NO(String CUSTOM_NO) {
        this.CUSTOM_NO = CUSTOM_NO;
    }

    public String getCUSTOM_NAME() {
        return this.CUSTOM_NAME;
    }

    public void setCUSTOM_NAME(String CUSTOM_NAME) {
        this.CUSTOM_NAME = CUSTOM_NAME;
    }

    public Float getDN_QTY() {
        return this.DN_QTY;
    }

    public void setDN_QTY(Float DN_QTY) {
        this.DN_QTY = DN_QTY;
    }

    public String getUPDATE_USER() {
        return this.UPDATE_USER;
    }

    public void setUPDATE_USER(String UPDATE_USER) {
        this.UPDATE_USER = UPDATE_USER;
    }

    public Date getUPDATE_DATE() {
        return this.UPDATE_DATE;
    }

    public void setUPDATE_DATE(Date UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }

    public String getDN_SOURCE() {
        return this.DN_SOURCE;
    }

    public void setDN_SOURCE(String DN_SOURCE) {
        this.DN_SOURCE = DN_SOURCE;
    }

    public DNModel() {
    }

    protected DNModel(Parcel in) {
        this.AGENT_DN_NO = in.readString();
        long tmpDN_DATE = in.readLong();
        this.DN_DATE = tmpDN_DATE == -1 ? null : new Date(tmpDN_DATE);
        this.DN_STATUS = in.readString();
        this.LEVEL_1_AGENT_NO = in.readString();
        this.LEVEL_1_AGENT_NAME = in.readString();
        this.LEVEL_2_AGENT_NO = in.readString();
        this.LEVEL_2_AGENT_NAME = in.readString();
        this.CUSTOM_NO = in.readString();
        this.CUSTOM_NAME = in.readString();
        this.DN_QTY = (Float) in.readValue(Float.class.getClassLoader());
        this.UPDATE_USER = in.readString();
        long tmpUPDATE_DATE = in.readLong();
        this.UPDATE_DATE = tmpUPDATE_DATE == -1 ? null : new Date(tmpUPDATE_DATE);
        this.DN_SOURCE = in.readString();
    }

    @Generated(hash = 1397919254)
    public DNModel(String AGENT_DN_NO, Date DN_DATE, String DN_STATUS,
            String LEVEL_1_AGENT_NO, String LEVEL_1_AGENT_NAME,
            String LEVEL_2_AGENT_NO, String LEVEL_2_AGENT_NAME, String CUSTOM_NO,
            String CUSTOM_NAME, Float DN_QTY, String UPDATE_USER, Date UPDATE_DATE,
            String DN_SOURCE) {
        this.AGENT_DN_NO = AGENT_DN_NO;
        this.DN_DATE = DN_DATE;
        this.DN_STATUS = DN_STATUS;
        this.LEVEL_1_AGENT_NO = LEVEL_1_AGENT_NO;
        this.LEVEL_1_AGENT_NAME = LEVEL_1_AGENT_NAME;
        this.LEVEL_2_AGENT_NO = LEVEL_2_AGENT_NO;
        this.LEVEL_2_AGENT_NAME = LEVEL_2_AGENT_NAME;
        this.CUSTOM_NO = CUSTOM_NO;
        this.CUSTOM_NAME = CUSTOM_NAME;
        this.DN_QTY = DN_QTY;
        this.UPDATE_USER = UPDATE_USER;
        this.UPDATE_DATE = UPDATE_DATE;
        this.DN_SOURCE = DN_SOURCE;
    }

    public static final Creator<DNModel> CREATOR = new Creator<DNModel>() {
        @Override
        public DNModel createFromParcel(Parcel source) {
            return new DNModel(source);
        }

        @Override
        public DNModel[] newArray(int size) {
            return new DNModel[size];
        }
    };
}
