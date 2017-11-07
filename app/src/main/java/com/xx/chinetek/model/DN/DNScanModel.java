package com.xx.chinetek.model.DN;

import java.sql.Time;
import java.util.Date;

/**
 * Created by GHOST on 2017/10/25.
 */

public class DNScanModel {
    private String ITEM_SERIAL_NO;
    private String PACKING_DATE;
    private String REGION;
    private String COUNTRY;
    private String DEAL_SALE_DATE;
    private Date IMPORT_DATE;
    private Time IMPORT_TIME;
    private String AGENT_DN_NO;
    private Float LINE_NO;
    private String ITEM_NO;
    private String GOLFA_CODE;
    private String ITEM_STATUS;
    private Date UPDATE_DATE;
    private Time UPDATE_TIME;
    private String ITEM_NAME;
    private Integer ID;
    private Integer FLAG;

    public String getITEM_SERIAL_NO() {
        return ITEM_SERIAL_NO;
    }

    public void setITEM_SERIAL_NO(String ITEM_SERIAL_NO) {
        this.ITEM_SERIAL_NO = ITEM_SERIAL_NO;
    }

    public String getPACKING_DATE() {
        return PACKING_DATE;
    }

    public void setPACKING_DATE(String PACKING_DATE) {
        this.PACKING_DATE = PACKING_DATE;
    }

    public String getREGION() {
        return REGION;
    }

    public void setREGION(String REGION) {
        this.REGION = REGION;
    }

    public String getCOUNTRY() {
        return COUNTRY;
    }

    public void setCOUNTRY(String COUNTRY) {
        this.COUNTRY = COUNTRY;
    }

    public String getDEAL_SALE_DATE() {
        return DEAL_SALE_DATE;
    }

    public void setDEAL_SALE_DATE(String DEAL_SALE_DATE) {
        this.DEAL_SALE_DATE = DEAL_SALE_DATE;
    }

    public Date getIMPORT_DATE() {
        return IMPORT_DATE;
    }

    public void setIMPORT_DATE(Date IMPORT_DATE) {
        this.IMPORT_DATE = IMPORT_DATE;
    }

    public Time getIMPORT_TIME() {
        return IMPORT_TIME;
    }

    public void setIMPORT_TIME(Time IMPORT_TIME) {
        this.IMPORT_TIME = IMPORT_TIME;
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

    public String getGOLFA_CODE() {
        return GOLFA_CODE;
    }

    public void setGOLFA_CODE(String GOLFA_CODE) {
        this.GOLFA_CODE = GOLFA_CODE;
    }

    public String getITEM_STATUS() {
        return ITEM_STATUS;
    }

    public void setITEM_STATUS(String ITEM_STATUS) {
        this.ITEM_STATUS = ITEM_STATUS;
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

    public String getITEM_NAME() {
        return ITEM_NAME;
    }

    public void setITEM_NAME(String ITEM_NAME) {
        this.ITEM_NAME = ITEM_NAME;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public Integer getFLAG() {
        return FLAG;
    }

    public void setFLAG(Integer FLAG) {
        this.FLAG = FLAG;
    }
}
