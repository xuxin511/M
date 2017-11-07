package com.xx.chinetek.model.Base;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by GHOST on 2017/11/2.
 */

@Entity
public class CustomModel implements Parcelable {
    /**
     * 客户编号
     */
    @Unique
    private String PartnerID;

    /**
     * 客户编号
     */
    private String PartnerName;

    /**
     * 客户类型  Z2:二级 Z3:客户
     */
    private String Type;

    public String getPartnerID() {
        return PartnerID;
    }

    public void setPartnerID(String partnerID) {
        PartnerID = partnerID;
    }

    public String getPartnerName() {
        return PartnerName;
    }

    public void setPartnerName(String partnerName) {
        PartnerName = partnerName;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.PartnerID);
        dest.writeString(this.PartnerName);
        dest.writeString(this.Type);
    }

    public CustomModel() {
    }

    protected CustomModel(Parcel in) {
        this.PartnerID = in.readString();
        this.PartnerName = in.readString();
        this.Type = in.readString();
    }

    @Generated(hash = 372893884)
    public CustomModel(String PartnerID, String PartnerName, String Type) {
        this.PartnerID = PartnerID;
        this.PartnerName = PartnerName;
        this.Type = Type;
    }

    public static final Parcelable.Creator<CustomModel> CREATOR = new Parcelable.Creator<CustomModel>() {
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
