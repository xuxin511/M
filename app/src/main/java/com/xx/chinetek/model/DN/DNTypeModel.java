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

    private Integer SelectCusType=0;

    private Integer SelectRule=0;

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

    public Integer getSelectRule() {
        return SelectRule;
    }

    public void setSelectRule(Integer selectRule) {
        SelectRule = selectRule;
    }

    public Integer getSelectCusType() {
        return SelectCusType;
    }

    public void setSelectCusType(Integer selectCusType) {
        SelectCusType = selectCusType;
    }

    public DNTypeModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.DNType);
        dest.writeValue(this.DNCusType);
        dest.writeValue(this.SelectCusType);
        dest.writeValue(this.SelectRule);
        dest.writeParcelable(this.CustomModel, flags);
    }

    protected DNTypeModel(Parcel in) {
        this.DNType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.DNCusType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.SelectCusType = (Integer) in.readValue(Integer.class.getClassLoader());
        this.SelectRule = (Integer) in.readValue(Integer.class.getClassLoader());
        this.CustomModel = in.readParcelable(com.xx.chinetek.model.Base.CustomModel.class.getClassLoader());
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
