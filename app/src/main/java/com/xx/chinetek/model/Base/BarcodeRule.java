package com.xx.chinetek.model.Base;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by GHOST on 2018/1/9.
 */

public class BarcodeRule implements Parcelable {

    /**
     * 自定义规则名称
     */
    private String RuleName;

    /**
     * 条码长度
     */
    private Integer BarcodeLength=0;

    /**
     * 比对字段开始、结束位
     */
    private Integer KeyStartIndex=0;
    private Integer KeyEndIndex=0;

    /**
     * 序列号开始、结束位
     */
    private Integer SerialStartIndex=0;
    private Integer SerialEndIndex=0;

    /**
     * 格式：起始位-结束位
     */
    private List<String> OtherColumn;


    public String getRuleName() {
        return RuleName;
    }

    public void setRuleName(String ruleName) {
        RuleName = ruleName;
    }


    public Integer getBarcodeLength() {
        return BarcodeLength;
    }

    public void setBarcodeLength(Integer barcodeLength) {
        BarcodeLength = barcodeLength;
    }

    public Integer getKeyStartIndex() {
        return KeyStartIndex;
    }

    public void setKeyStartIndex(Integer keyStartIndex) {
        KeyStartIndex = keyStartIndex;
    }

    public Integer getKeyEndIndex() {
        return KeyEndIndex;
    }

    public void setKeyEndIndex(Integer keyEndIndex) {
        KeyEndIndex = keyEndIndex;
    }

    public Integer getSerialStartIndex() {
        return SerialStartIndex;
    }

    public void setSerialStartIndex(Integer serialStartIndex) {
        SerialStartIndex = serialStartIndex;
    }

    public Integer getSerialEndIndex() {
        return SerialEndIndex;
    }

    public void setSerialEndIndex(Integer serialEndIndex) {
        SerialEndIndex = serialEndIndex;
    }

    public List<String> getOtherColumn() {
        return OtherColumn;
    }

    public void setOtherColumn(List<String> otherColumn) {
        OtherColumn = otherColumn;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.RuleName);
        dest.writeValue(this.BarcodeLength);
        dest.writeValue(this.KeyStartIndex);
        dest.writeValue(this.KeyEndIndex);
        dest.writeValue(this.SerialStartIndex);
        dest.writeValue(this.SerialEndIndex);
        dest.writeStringList(this.OtherColumn);
    }

    public BarcodeRule() {
    }

    protected BarcodeRule(Parcel in) {
        this.RuleName = in.readString();
        this.BarcodeLength = (Integer) in.readValue(Integer.class.getClassLoader());
        this.KeyStartIndex = (Integer) in.readValue(Integer.class.getClassLoader());
        this.KeyEndIndex = (Integer) in.readValue(Integer.class.getClassLoader());
        this.SerialStartIndex = (Integer) in.readValue(Integer.class.getClassLoader());
        this.SerialEndIndex = (Integer) in.readValue(Integer.class.getClassLoader());
        this.OtherColumn = in.createStringArrayList();
    }

    public static final Creator<BarcodeRule> CREATOR = new Creator<BarcodeRule>() {
        @Override
        public BarcodeRule createFromParcel(Parcel source) {
            return new BarcodeRule(source);
        }

        @Override
        public BarcodeRule[] newArray(int size) {
            return new BarcodeRule[size];
        }
    };
}
