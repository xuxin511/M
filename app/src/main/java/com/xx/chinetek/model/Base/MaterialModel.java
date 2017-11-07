package com.xx.chinetek.model.Base;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by GHOST on 2017/10/25.
 */

@Entity
public class MaterialModel implements Parcelable {

    /**
     * sap物料名(主键)
     */
    @Unique
    private String MATNR;
    private String MTART;
    private String MATKL;

    /**
     * GOLFA CODE
     */
    private String BISMT;

    /**
     * 物料是否存在QR码 Y:存在
     */
    private String NORMT;

    /**
     * 客户产品线
     */
    private String SPART;

    /**
     * 型号
     */
    private String MAKTX;
    private String ZMAKTX;
    private String ZEINR;
    @Generated(hash = 967889585)
    public MaterialModel(String MATNR, String MTART, String MATKL, String BISMT,
            String NORMT, String SPART, String MAKTX, String ZMAKTX, String ZEINR) {
        this.MATNR = MATNR;
        this.MTART = MTART;
        this.MATKL = MATKL;
        this.BISMT = BISMT;
        this.NORMT = NORMT;
        this.SPART = SPART;
        this.MAKTX = MAKTX;
        this.ZMAKTX = ZMAKTX;
        this.ZEINR = ZEINR;
    }
    @Generated(hash = 504750984)
    public MaterialModel() {
    }
    public String getMATNR() {
        return this.MATNR;
    }
    public void setMATNR(String MATNR) {
        this.MATNR = MATNR;
    }
    public String getMTART() {
        return this.MTART;
    }
    public void setMTART(String MTART) {
        this.MTART = MTART;
    }
    public String getMATKL() {
        return this.MATKL;
    }
    public void setMATKL(String MATKL) {
        this.MATKL = MATKL;
    }
    public String getBISMT() {
        return this.BISMT;
    }
    public void setBISMT(String BISMT) {
        this.BISMT = BISMT;
    }
    public String getNORMT() {
        return this.NORMT;
    }
    public void setNORMT(String NORMT) {
        this.NORMT = NORMT;
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
    public String getZEINR() {
        return this.ZEINR;
    }
    public void setZEINR(String ZEINR) {
        this.ZEINR = ZEINR;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.MATNR);
        dest.writeString(this.MTART);
        dest.writeString(this.MATKL);
        dest.writeString(this.BISMT);
        dest.writeString(this.NORMT);
        dest.writeString(this.SPART);
        dest.writeString(this.MAKTX);
        dest.writeString(this.ZMAKTX);
        dest.writeString(this.ZEINR);
    }

    protected MaterialModel(Parcel in) {
        this.MATNR = in.readString();
        this.MTART = in.readString();
        this.MATKL = in.readString();
        this.BISMT = in.readString();
        this.NORMT = in.readString();
        this.SPART = in.readString();
        this.MAKTX = in.readString();
        this.ZMAKTX = in.readString();
        this.ZEINR = in.readString();
    }

    public static final Parcelable.Creator<MaterialModel> CREATOR = new Parcelable.Creator<MaterialModel>() {
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
