package com.xx.chinetek.model.Base;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by GHOST on 2017/11/7.
 */
@Entity
public class SyncParaModel {
    @Unique
    private String ParaID;

    private String ParaName;

    private String ParaContext;

    @Generated(hash = 1825285884)
    public SyncParaModel(String ParaID, String ParaName, String ParaContext) {
        this.ParaID = ParaID;
        this.ParaName = ParaName;
        this.ParaContext = ParaContext;
    }

    @Generated(hash = 1808823217)
    public SyncParaModel() {
    }

    public String getParaID() {
        return this.ParaID;
    }

    public void setParaID(String ParaID) {
        this.ParaID = ParaID;
    }

    public String getParaName() {
        return this.ParaName;
    }

    public void setParaName(String ParaName) {
        this.ParaName = ParaName;
    }

    public String getParaContext() {
        return this.ParaContext;
    }

    public void setParaContext(String ParaContext) {
        this.ParaContext = ParaContext;
    }
}
