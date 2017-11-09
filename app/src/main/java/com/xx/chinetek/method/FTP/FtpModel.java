package com.xx.chinetek.method.FTP;

/**
 * Created by GHOST on 2017/11/8.
 */

public class FtpModel {

    /**
     * FTP服务器地址
     */
    private String FtpHost;

    /**
     * 用户名
     */
    private String FtpUserName;

    /**
     * 密码
     */
    private String FtpPassword;

    /**
     * 端口
     */
    private int FtpPort=21;

    /**
     * FTP下载目录
     */
    private String FtpDownLoad;

    /**
     * FTP上传目录
     */
    private String FtpUpLoad;

    public String getFtpHost() {
        return FtpHost;
    }

    public void setFtpHost(String ftpHost) {
        FtpHost = ftpHost;
    }

    public String getFtpUserName() {
        return FtpUserName;
    }

    public void setFtpUserName(String ftpUserName) {
        FtpUserName = ftpUserName;
    }

    public String getFtpPassword() {
        return FtpPassword;
    }

    public void setFtpPassword(String ftpPassword) {
        FtpPassword = ftpPassword;
    }

    public int getFtpPort() {
        return FtpPort;
    }

    public void setFtpPort(int ftpPort) {
        FtpPort = ftpPort;
    }

    public String getFtpDownLoad() {
        return FtpDownLoad;
    }

    public void setFtpDownLoad(String ftpDownLoad) {
        FtpDownLoad = ftpDownLoad;
    }

    public String getFtpUpLoad() {
        return FtpUpLoad;
    }

    public void setFtpUpLoad(String ftpUpLoad) {
        FtpUpLoad = ftpUpLoad;
    }
}
