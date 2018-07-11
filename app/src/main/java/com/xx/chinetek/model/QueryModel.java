package com.xx.chinetek.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.xx.chinetek.model.Base.CustomModel;

/**
 * Created by GHOST on 2018/7/7.
 */

public class QueryModel implements Parcelable {
    private String startTIme;

    private String endTime;

    private CustomModel customModel;

    public String getStartTIme() {
        return startTIme;
    }

    public void setStartTIme(String startTIme) {
        this.startTIme = startTIme;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public CustomModel getCustomModel() {
        return customModel;
    }

    public void setCustomModel(CustomModel customModel) {
        this.customModel = customModel;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.startTIme);
        dest.writeString(this.endTime);
        dest.writeParcelable(this.customModel, flags);
    }

    public QueryModel() {
    }

    protected QueryModel(Parcel in) {
        this.startTIme = in.readString();
        this.endTime = in.readString();
        this.customModel = in.readParcelable(CustomModel.class.getClassLoader());
    }

    public static final Parcelable.Creator<QueryModel> CREATOR = new Parcelable.Creator<QueryModel>() {
        @Override
        public QueryModel createFromParcel(Parcel source) {
            return new QueryModel(source);
        }

        @Override
        public QueryModel[] newArray(int size) {
            return new QueryModel[size];
        }
    };
}
