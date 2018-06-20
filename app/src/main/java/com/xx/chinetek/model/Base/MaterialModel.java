package com.xx.chinetek.model.Base;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by GHOST on 2017/10/25.
 */

@Entity(
        indexes = {
                @Index(value = "BISMT" )//,STATUS
        }
)
public class MaterialModel implements Parcelable {

    /**
     * sap物料名(主键)
     */
    @Unique
    private String MATNR;

    /**
     * GOLFA CODE
     */
    private String BISMT;

    /**
     * 客户产品线
     */
    private String SPART;

    /**
     * 型号
     */
    private String MAKTX;
    /**
     * 型号
     */
    private String ZMAKTX;

    private String ACTION_CODE;


    public String getMATNR() {
        return this.MATNR;
    }

    public void setMATNR(String MATNR) {
        this.MATNR = MATNR;
    }

    public String getBISMT() {
        return this.BISMT;
    }

    public void setBISMT(String BISMT) {
        this.BISMT = BISMT;
    }

    public String getSPART() {
        return this.SPART;
    }

    public void setSPART(String SPART) {
        this.SPART = SPART;
    }

    public String getMAKTX() {
        return this.MAKTX;
    }

    public void setMAKTX(String MAKTX) {
        this.MAKTX = MAKTX;
    }

    public String getZMAKTX() {
        return this.ZMAKTX;
    }

    public void setZMAKTX(String ZMAKTX) {
        this.ZMAKTX = ZMAKTX;
    }

    public String getACTION_CODE() {
        return ACTION_CODE;
    }

    public void setACTION_CODE(String ACTION_CODE) {
        this.ACTION_CODE = ACTION_CODE;
    }

    public MaterialModel() {
    }

    @Generated(hash = 1216317872)
    public MaterialModel(String MATNR, String BISMT, String SPART, String MAKTX,
            String ZMAKTX, String ACTION_CODE) {
        this.MATNR = MATNR;
        this.BISMT = BISMT;
        this.SPART = SPART;
        this.MAKTX = MAKTX;
        this.ZMAKTX = ZMAKTX;
        this.ACTION_CODE = ACTION_CODE;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.MATNR);
        dest.writeString(this.BISMT);
        dest.writeString(this.SPART);
        dest.writeString(this.MAKTX);
        dest.writeString(this.ZMAKTX);
        dest.writeString(this.ACTION_CODE);
    }

    protected MaterialModel(Parcel in) {
        this.MATNR = in.readString();
        this.BISMT = in.readString();
        this.SPART = in.readString();
        this.MAKTX = in.readString();
        this.ZMAKTX = in.readString();
        this.ACTION_CODE = in.readString();
    }

    public static final Creator<MaterialModel> CREATOR = new Creator<MaterialModel>() {
        @Override
        public MaterialModel createFromParcel(Parcel source) {
            return new MaterialModel(source);
        }

        @Override
        public MaterialModel[] newArray(int size) {
            return new MaterialModel[size];
        }
    };
}
