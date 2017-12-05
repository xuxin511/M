package com.xx.chinetek.model;

/**
 * Created by GHOST on 2017/11/10.
 */

public class DBReturnModel {
    private   Integer DNQTY;
    private   Integer SCANQTY;

    private Integer returnCode;
    private String returnMsg;

    public Integer getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(String returnMsg) {
        this.returnMsg = returnMsg;
    }

    public Integer getDNQTY() {
        return DNQTY;
    }

    public void setDNQTY(Integer DNQTY) {
        this.DNQTY = DNQTY;
    }

    public Integer getSCANQTY() {
        return SCANQTY;
    }

    public void setSCANQTY(Integer SCANQTY) {
        this.SCANQTY = SCANQTY;
    }
}
