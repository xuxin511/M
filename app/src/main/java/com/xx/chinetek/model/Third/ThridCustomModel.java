package com.xx.chinetek.model.Third;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GHOST on 2018/12/25.
 */

public class ThridCustomModel implements Parcelable {

    public ThridCustomModel(String CustomNo,String CustomName){
        this.CustomNo=CustomNo;
        this.CustomName=CustomName;
    }

    private String  CustomNo;
    private String  CustomName;

    public String getCustomNo() {
        return CustomNo;
    }

    public void setCustomNo(String customNo) {
        CustomNo = customNo;
    }

    public String getCustomName() {
        return CustomName;
    }

    public void setCustomName(String customName) {
        CustomName = customName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.CustomNo);
        dest.writeString(this.CustomName);
    }

    public ThridCustomModel() {
    }

    protected ThridCustomModel(Parcel in) {
        this.CustomNo = in.readString();
        this.CustomName = in.readString();
    }

    public static final Parcelable.Creator<ThridCustomModel> CREATOR = new Parcelable.Creator<ThridCustomModel>() {
        @Override
        public ThridCustomModel createFromParcel(Parcel source) {
            return new ThridCustomModel(source);
        }

        @Override
        public ThridCustomModel[] newArray(int size) {
            return new ThridCustomModel[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ThridCustomModel)) return false;

        ThridCustomModel that = (ThridCustomModel) o;
        return CustomNo.equals(that.CustomNo);
    }

    @Override
    public int hashCode() {
        int result = CustomNo.hashCode();
        result = 31 * result + (CustomName != null ? CustomName.hashCode() : 0);
        return result;
    }
}
