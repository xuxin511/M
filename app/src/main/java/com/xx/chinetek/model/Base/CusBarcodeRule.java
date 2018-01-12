package com.xx.chinetek.model.Base;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by GHOST on 2017/11/19.
 */

public class CusBarcodeRule implements Parcelable {

    /**
     * 是否启用非三菱条码
     */
    private Boolean isUsed=false;

    private ArrayList<BarcodeRule> barcodeRules;


    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public ArrayList<BarcodeRule> getBarcodeRules() {
        return barcodeRules;
    }

    public void setBarcodeRules(ArrayList<BarcodeRule> barcodeRules) {
        this.barcodeRules = barcodeRules;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.isUsed);
        dest.writeTypedList(this.barcodeRules);
    }

    public CusBarcodeRule() {
    }

    protected CusBarcodeRule(Parcel in) {
        this.isUsed = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.barcodeRules = in.createTypedArrayList(BarcodeRule.CREATOR);
    }

    public static final Creator<CusBarcodeRule> CREATOR = new Creator<CusBarcodeRule>() {
        @Override
        public CusBarcodeRule createFromParcel(Parcel source) {
            return new CusBarcodeRule(source);
        }

        @Override
        public CusBarcodeRule[] newArray(int size) {
            return new CusBarcodeRule[size];
        }
    };
}
