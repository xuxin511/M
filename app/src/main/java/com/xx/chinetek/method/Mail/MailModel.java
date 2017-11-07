package com.xx.chinetek.method.Mail;

import java.util.Properties;

/**
 * Created by GHOST on 2017/10/30.
 */

public class MailModel {
    /**
     * 登陆邮件发送服务器的用户名和密码
     */
    private String Account;
    private String Password;
    /**
     * 接收邮件服务器
     */
    private String mailClientHost;
    /**
     * 发送邮件的服务器的IP和端口
     */
    private String mailServerHost;
    private String mailServerPort;
    /**
     * 邮件发送者的地址
     */
    private String fromAddress;
    /**
     * 邮件接受者的地址
     */
    private String toAddress;
    /**
     * 是否需要身份验证
     */
    private boolean validate = true;
    /**
     * 邮件发送的主题
     */
    private String subject;
    /**
     * 邮件发送的内容
     */
    private String content;
    /**
     * 邮件附件的文件名
     */
    private String[] attachFileNames;

    /**
     * 获取邮件会话属性
     * @return
     */
    public Properties getProperties(){
        Properties p = new Properties();
        p.put("mail.smtp.host", this.mailServerHost);
        p.put("mail.smtp.port", this.mailServerPort);
        p.put("mail.transport.protocol", "smtp");
        p.put("mail.smtp.auth", validate ? "true" : "false");
        return p;
    }

    public String getMailClientHost() {
        return mailClientHost;
    }

    public void setMailClientHost(String mailClientHost) {
        this.mailClientHost = mailClientHost;
    }

    public String getMailServerHost() {
        return mailServerHost;
    }

    public void setMailServerHost(String mailServerHost) {
        this.mailServerHost = mailServerHost;
    }

    public String getMailServerPort() {
        return mailServerPort;
    }

    public void setMailServerPort(String mailServerPort) {
        this.mailServerPort = mailServerPort;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public boolean isValidate() {
        return validate;
    }

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String[] getAttachFileNames() {
        return attachFileNames;
    }

    public void setAttachFileNames(String[] attachFileNames) {
        this.attachFileNames = attachFileNames;
    }

    public String getAccount() {
        return Account;
    }

    public void setAccount(String account) {
        Account = account;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }
}
