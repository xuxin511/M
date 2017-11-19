package com.xx.chinetek.model.Base;

/**
 * Created by GHOST on 2017/11/19.
 */

public class CusDnnoRule {

    private String StartWords;

    private Integer IndexLength=0;

    public String getStartWords() {
        return StartWords;
    }

    public void setStartWords(String startWords) {
        StartWords = startWords;
    }

    public Integer getIndexLength() {
        return IndexLength;
    }

    public void setIndexLength(Integer indexLength) {
        IndexLength = indexLength;
    }
}
