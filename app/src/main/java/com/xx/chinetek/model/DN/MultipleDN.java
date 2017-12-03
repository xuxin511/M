package com.xx.chinetek.model.DN;

/**
 * Created by GHOST on 2017/12/3.
 */

public class MultipleDN {

    private String Status;

    private DNModel DN;

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public DNModel getDN() {
        return DN;
    }

    public void setDN(DNModel DN) {
        this.DN = DN;
    }
}
