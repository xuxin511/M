package com.xx.chinetek.model.DN;

import java.sql.Time;
import java.util.Date;

/**
 * Created by GHOST on 2017/10/25.
 */

public class DNDetailModel {
    private String  AGENT_DN_NO;
    private Float  LINE_NO;
    private String  ITEM_NO;
    private String  ITEM_NAME;
    private String  GOLFA_CODE;
    private Float  DN_QTY;
    private String  DETAIL_STATUS;
    private Date UPDATE_DATE;
    private Time UPDATE_TIME;
    private String  NORMT;
    private Float  SCAN_QTY;

    public Float getSCAN_QTY() {
        return SCAN_QTY;
    }

    public void setSCAN_QTY(Float SCAN_QTY) {
        this.SCAN_QTY = SCAN_QTY;
    }

    public String getAGENT_DN_NO() {
        return AGENT_DN_NO;
    }

    public void setAGENT_DN_NO(String AGENT_DN_NO) {
        this.AGENT_DN_NO = AGENT_DN_NO;
    }

    public Float getLINE_NO() {
        return LINE_NO;
    }

    public void setLINE_NO(Float LINE_NO) {
        this.LINE_NO = LINE_NO;
    }

    public String getITEM_NO() {
        return ITEM_NO;
    }

    public void setITEM_NO(String ITEM_NO) {
        this.ITEM_NO = ITEM_NO;
    }

    public String getITEM_NAME() {
        return ITEM_NAME;
    }

    public void setITEM_NAME(String ITEM_NAME) {
        this.ITEM_NAME = ITEM_NAME;
    }

    public String getGOLFA_CODE() {
        return GOLFA_CODE;
    }

    public void setGOLFA_CODE(String GOLFA_CODE) {
        this.GOLFA_CODE = GOLFA_CODE;
    }

    public Float getDN_QTY() {
        return DN_QTY;
    }

    public void setDN_QTY(Float DN_QTY) {
        this.DN_QTY = DN_QTY;
    }

    public String getDETAIL_STATUS() {
        return DETAIL_STATUS;
    }

    public void setDETAIL_STATUS(String DETAIL_STATUS) {
        this.DETAIL_STATUS = DETAIL_STATUS;
    }

    public Date getUPDATE_DATE() {
        return UPDATE_DATE;
    }

    public void setUPDATE_DATE(Date UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }

    public Time getUPDATE_TIME() {
        return UPDATE_TIME;
    }

    public void setUPDATE_TIME(Time UPDATE_TIME) {
        this.UPDATE_TIME = UPDATE_TIME;
    }

    public String getNORMT() {
        return NORMT;
    }

    public void setNORMT(String NORMT) {
        this.NORMT = NORMT;
    }
}
