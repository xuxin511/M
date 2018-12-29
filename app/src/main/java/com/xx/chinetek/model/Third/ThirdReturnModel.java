package com.xx.chinetek.model.Third;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GHOST on 2018/12/25.
 */

public class ThirdReturnModel implements Parcelable {
    private  String Message;

    private  int Success;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public int getSuccess() {
        return Success;
    }

    public void setSuccess(int success) {
        Success = success;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Message);
        dest.writeInt(this.Success);
    }

    public ThirdReturnModel() {
    }

    protected ThirdReturnModel(Parcel in) {
        this.Message = in.readString();
        this.Success = in.readInt();
    }

    public static final Parcelable.Creator<ThirdReturnModel> CREATOR = new Parcelable.Creator<ThirdReturnModel>() {
        @Override
        public ThirdReturnModel createFromParcel(Parcel source) {
            return new ThirdReturnModel(source);
        }

        @Override
        public ThirdReturnModel[] newArray(int size) {
            return new ThirdReturnModel[size];
        }
    };
}
