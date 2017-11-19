package com.xx.chinetek.model.Base;

/**
 * Created by GHOST on 2017/11/19.
 */

public class CusBarcodeRule {

    /**
     * 是否启用非三菱条码
     */
    private Boolean isUsed=false;

    private String StartWords;

    private Integer BarcodeLength=0;


    public Boolean getUsed() {
        return isUsed;
    }

    public void setUsed(Boolean used) {
        isUsed = used;
    }

    public String getStartWords() {
        return StartWords;
    }

    public void setStartWords(String startWords) {
        StartWords = startWords;
    }

    public Integer getBarcodeLength() {
        return BarcodeLength;
    }

    public void setBarcodeLength(Integer barcodeLength) {
        BarcodeLength = barcodeLength;
    }
}
