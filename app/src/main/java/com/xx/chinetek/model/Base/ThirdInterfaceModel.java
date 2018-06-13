package com.xx.chinetek.model.Base;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by GHOST on 2018/6/13.
 */

public class ThirdInterfaceModel implements Parcelable {

    private String InterfaceIP;

    private int Port=80;

    private String Part;

    public String getInterfaceIP() {
        return InterfaceIP;
    }

    public void setInterfaceIP(String interfaceIP) {
        InterfaceIP = interfaceIP;
    }

    public int getPort() {
        return Port;
    }

    public void setPort(int port) {
        Port = port;
    }

    public String getPart() {
        return Part;
    }

    public void setPart(String part) {
        Part = part;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.InterfaceIP);
        dest.writeString(this.Part);
        dest.writeInt(this.Port);
    }

    public ThirdInterfaceModel() {
    }

    protected ThirdInterfaceModel(Parcel in) {
        this.InterfaceIP = in.readString();
        this.Part = in.readString();
        this.Port = in.readInt();
    }

    public static final Creator<ThirdInterfaceModel> CREATOR = new Creator<ThirdInterfaceModel>() {
        @Override
        public ThirdInterfaceModel createFromParcel(Parcel source) {
            return new ThirdInterfaceModel(source);
        }

        @Override
        public ThirdInterfaceModel[] newArray(int size) {
            return new ThirdInterfaceModel[size];
        }
    };
}
