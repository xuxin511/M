package com.xx.chinetek.model.Base;

import java.util.List;

/**
 * Created by GHOST on 2017/11/19.
 */

public class CusBarcodeRule {

    /**
     * 是否启用非三菱条码
     */
    private Boolean isUsed=false;
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



    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
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
}
