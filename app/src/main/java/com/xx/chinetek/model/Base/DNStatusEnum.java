package com.xx.chinetek.model.Base;

/**
 * Created by GHOST on 2017/11/8.
 * DN单据状态
 */

public class DNStatusEnum {

    /**
     * 异常
     */
    public static final int exeption =-1;

    /**
     * 重复
     */
    public static final int Repert =4;

    /**
     * 未下载
     */
    public static final int ready =0;

    /**
     * 已下载
     */
    public static final int download =1;

    /**
     * 完成
     */
    public static final int complete =2;

    /**
     * 已提交
     */
    public static final int Sumbit =3;
}
