package com.xx.chinetek.method;

import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.model.Base.CustomModel;

import java.util.ArrayList;

/**
 * Created by GHOST on 2017/11/2.
 */

public class GetPartner {

    /**
     * 获取代理商下属代理商和客户
     * @param DNCusType 客户类型 0:代理商 1：客户
     * @return
     */
    public static ArrayList<CustomModel> GetPartners(int DNCusType) throws Exception{
        ArrayList<CustomModel> customModels= (ArrayList<CustomModel>) DbBaseInfo.getInstance().QueryCustomDBByType(DNCusType);
        return customModels;
    }
}
