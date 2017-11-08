package com.xx.chinetek.model.DN;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Unique;

import java.util.Date;

/**
 * Created by GHOST on 2017/10/25.
 */

@Entity
public class DNDetailModel {

    @Unique
    private String  AGENT_DN_NO;
    @Unique
    private Integer  LINE_NO;
    private String  ITEM_NO;
    private String  ITEM_NAME;
    private String  ITEM_ZMAKTX;
    private String  GOLFA_CODE;
    private Integer  DN_QTY;
    private String  DETAIL_STATUS;
    private Date UPDATE_DATE;
    private String  NORMT;
    private Integer  SCAN_QTY;
    @Generated(hash = 502627958)
    public DNDetailModel(String AGENT_DN_NO, Integer LINE_NO, String ITEM_NO,
            String ITEM_NAME, String ITEM_ZMAKTX, String GOLFA_CODE, Integer DN_QTY,
            String DETAIL_STATUS, Date UPDATE_DATE, String NORMT,
            Integer SCAN_QTY) {
        this.AGENT_DN_NO = AGENT_DN_NO;
        this.LINE_NO = LINE_NO;
        this.ITEM_NO = ITEM_NO;
        this.ITEM_NAME = ITEM_NAME;
        this.ITEM_ZMAKTX = ITEM_ZMAKTX;
        this.GOLFA_CODE = GOLFA_CODE;
        this.DN_QTY = DN_QTY;
        this.DETAIL_STATUS = DETAIL_STATUS;
        this.UPDATE_DATE = UPDATE_DATE;
        this.NORMT = NORMT;
        this.SCAN_QTY = SCAN_QTY;
    }
    @Generated(hash = 395629670)
    public DNDetailModel() {
    }
    public String getAGENT_DN_NO() {
        return this.AGENT_DN_NO;
    }
    public void setAGENT_DN_NO(String AGENT_DN_NO) {
        this.AGENT_DN_NO = AGENT_DN_NO;
    }
    public Integer getLINE_NO() {
        return this.LINE_NO;
    }
    public void setLINE_NO(Integer LINE_NO) {
        this.LINE_NO = LINE_NO;
    }
    public String getITEM_NO() {
        return this.ITEM_NO;
    }
    public void setITEM_NO(String ITEM_NO) {
        this.ITEM_NO = ITEM_NO;
    }
    public String getITEM_NAME() {
        return this.ITEM_NAME;
    }
    public void setITEM_NAME(String ITEM_NAME) {
        this.ITEM_NAME = ITEM_NAME;
    }
    public String getITEM_ZMAKTX() {
        return this.ITEM_ZMAKTX;
    }
    public void setITEM_ZMAKTX(String ITEM_ZMAKTX) {
        this.ITEM_ZMAKTX = ITEM_ZMAKTX;
    }
    public String getGOLFA_CODE() {
        return this.GOLFA_CODE;
    }
    public void setGOLFA_CODE(String GOLFA_CODE) {
        this.GOLFA_CODE = GOLFA_CODE;
    }
    public Integer getDN_QTY() {
        return this.DN_QTY;
    }
    public void setDN_QTY(Integer DN_QTY) {
        this.DN_QTY = DN_QTY;
    }
    public String getDETAIL_STATUS() {
        return this.DETAIL_STATUS;
    }
    public void setDETAIL_STATUS(String DETAIL_STATUS) {
        this.DETAIL_STATUS = DETAIL_STATUS;
    }
    public Date getUPDATE_DATE() {
        return this.UPDATE_DATE;
    }
    public void setUPDATE_DATE(Date UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }
    public String getNORMT() {
        return this.NORMT;
    }
    public void setNORMT(String NORMT) {
        this.NORMT = NORMT;
    }
    public Integer getSCAN_QTY() {
        return this.SCAN_QTY;
    }
    public void setSCAN_QTY(Integer SCAN_QTY) {
        this.SCAN_QTY = SCAN_QTY;
    }

   
}
