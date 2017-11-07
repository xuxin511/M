package com.xx.chinetek.model.DN;

/**
 * Created by GHOST on 2017/11/1.
 */

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 发货参数
 */
public class DNTypeModel implements  Parcelable {

    /**
     * 发货方式
     * 0：MAPS
     * 1:邮件
     * 2：FTP
     * 3：自建
     * 4：USB
     * 5：二维码
     */
    private Integer DNType;

    /**
     * 发货方
     * 0:代理商
     * 1：客户
     */
    private Integer DNCusType;

    /**
     * 发货方信息
     */
    private com.xx.chinetek.model.Base.CustomModel CustomModel;

    public Integer getDNType() {
        return DNType;
    }

    public void setDNType(Integer DNType) {
        this.DNType = DNType;
    }

    public Integer getDNCusType() {
        return DNCusType;
    }

    public void setDNCusType(Integer DNCusType) {
        this.DNCusType = DNCusType;
    }

    public com.xx.chinetek.model.Base.CustomModel getCustomModel() {
        return CustomModel;
    }

    public void setCustomModel(com.xx.chinetek.model.Base.CustomModel customModel) {
        CustomModel = customModel;
    }

    public DNTypeModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.CustomModel, flags);
        dest.writeValue(this.DNCusType);
        dest.writeValue(this.DNType);
    }

    protected DNTypeModel(Parcel in) {
        this.CustomModel = in.readParcelable(com.xx.chinetek.model.Base.CustomModel.class.getClassLoader());
        this.DNCusType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.DNType = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<DNTypeModel> CREATOR = new Creator<DNTypeModel>() {
        @Override
        public DNTypeModel createFromParcel(Parcel source) {
            return new DNTypeModel(source);
        }

        @Override
        public DNTypeModel[] newArray(int size) {
            return new DNTypeModel[size];
        }
    };
}
