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
    private String Key;

    private String Value;

    @Generated(hash = 860805823)
    public SyncParaModel(String Key, String Value) {
        this.Key = Key;
        this.Value = Value;
    }

    @Generated(hash = 1808823217)
    public SyncParaModel() {
    }

    public String getKey() {
        return this.Key;
    }

    public void setKey(String Key) {
        this.Key = Key;
    }

    public String getValue() {
        return this.Value;
    }

    public void setValue(String Value) {
        this.Value = Value;
    }

    

}
