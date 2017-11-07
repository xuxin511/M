package com.xx.chinetek.chineteklib.base;

/**
 * Created by GHOST on 2017/3/20.
 */

public class ToolBarTitle {

    public String Title = "";
   // public String subTitle = "";
    public boolean isShowBack = false;

    public ToolBarTitle(String TitleValue, boolean isShowBackValue) {// String subTitleValue,
        this.Title = TitleValue;
      //  this.subTitle = subTitleValue;
        this.isShowBack = isShowBackValue;
    }
}
