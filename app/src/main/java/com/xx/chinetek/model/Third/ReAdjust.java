package com.xx.chinetek.model.Third;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GHOST on 2019/3/12.
 */

public class ReAdjust implements Parcelable {
    private String Cpxh;
    private String Golfa_Code;
    private String Pd;
    private String Serial_No;

    public String getCpxh() {
        return Cpxh;
    }

    public void setCpxh(String cpxh) {
        Cpxh = cpxh;
    }

    public String getGolfa_Code() {
        return Golfa_Code;
    }

    public void setGolfa_Code(String golfa_Code) {
        Golfa_Code = golfa_Code;
    }

    public String getPd() {
        return Pd;
    }

    public void setPd(String pd) {
        Pd = pd;
    }

    public String getSerial_No() {
        return Serial_No;
    }

    public void setSerial_No(String serial_No) {
        Serial_No = serial_No;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Cpxh);
        dest.writeString(this.Golfa_Code);
        dest.writeString(this.Pd);
        dest.writeValue(this.Serial_No);
    }

    public ReAdjust() {
    }

    protected ReAdjust(Parcel in) {
        this.Cpxh = in.readString();
        this.Golfa_Code = in.readString();
        this.Pd = in.readString();
        this.Serial_No = in.readString();
    }

    public static final Parcelable.Creator<ReAdjust> CREATOR = new Parcelable.Creator<ReAdjust>() {
        @Override
        public ReAdjust createFromParcel(Parcel source) {
            return new ReAdjust(source);
        }

        @Override
        public ReAdjust[] newArray(int size) {
            return new ReAdjust[size];
        }
    };
}
