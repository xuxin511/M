package com.xx.chinetek.model.Base;

/**
 * Created by GHOST on 2017/11/21.
 */

public class UserInfoModel {

    private String AGENT_NO;//代理商

    private String USER_CODE; //员工号

    private String PDA_CODE; //枪号

    public String getAGENT_NO() {
        return AGENT_NO;
    }

    public void setAGENT_NO(String AGENT_NO) {
        this.AGENT_NO = AGENT_NO;
    }

    public String getUSER_CODE() {
        return USER_CODE;
    }

    public void setUSER_CODE(String USER_CODE) {
        this.USER_CODE = USER_CODE;
    }

    public String getPDA_CODE() {
        return PDA_CODE;
    }

    public void setPDA_CODE(String PDA_CODE) {
        this.PDA_CODE = PDA_CODE;
    }
}
