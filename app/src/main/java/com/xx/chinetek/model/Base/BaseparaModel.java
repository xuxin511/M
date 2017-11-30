package com.xx.chinetek.model.Base;

import com.xx.chinetek.method.FTP.FtpModel;
import com.xx.chinetek.method.Mail.MailModel;

/**
 * Created by GHOST on 2017/11/22.
 */

public class BaseparaModel {
    /**
     * DN单据本地保存时间
     */
    private int DNSaveTime=30;

    /**
     * 序列号最大长度
     */
    private int SerialMaxLength=50;

    /**
     * 是否启用备注栏
     */
    private Boolean IsUseRemark=false;

    /**
     * 是否启用三菱条码
     */
    private CusBarcodeRule cusBarcodeRule=null;

    /**
     * 自定义出库单规则
     */
    private CusDnnoRule cusDnnoRule=null;


    /**
     * 邮件服务器
     */
    private MailModel mailModel=new MailModel();

    /**
     * FTP服务器
     */
    private FtpModel ftpModel;

    public int getSerialMaxLength() {
        return SerialMaxLength;
    }

    public void setSerialMaxLength(int serialMaxLength) {
        SerialMaxLength = serialMaxLength;
    }

    public int getDNSaveTime() {
        return DNSaveTime;
    }

    public void setDNSaveTime(int DNSaveTime) {
        this.DNSaveTime = DNSaveTime;
    }

    public Boolean getUseRemark() {
        return IsUseRemark;
    }

    public void setUseRemark(Boolean useRemark) {
        IsUseRemark = useRemark;
    }

    public CusBarcodeRule getCusBarcodeRule() {
        return cusBarcodeRule;
    }

    public void setCusBarcodeRule(CusBarcodeRule cusBarcodeRule) {
        this.cusBarcodeRule = cusBarcodeRule;
    }

    public CusDnnoRule getCusDnnoRule() {
        return cusDnnoRule;
    }

    public void setCusDnnoRule(CusDnnoRule cusDnnoRule) {
        this.cusDnnoRule = cusDnnoRule;
    }

    public MailModel getMailModel() {
        return mailModel;
    }

    public void setMailModel(MailModel mailModel) {
        this.mailModel = mailModel;
    }

    public FtpModel getFtpModel() {
        return ftpModel;
    }

    public void setFtpModel(FtpModel ftpModel) {
        this.ftpModel = ftpModel;
    }
}
