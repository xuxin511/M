package com.xx.chinetek.method;

import com.xx.chinetek.chineteklib.model.Paramater;
import com.xx.chinetek.greendao.CustomModelDao;
import com.xx.chinetek.method.DB.DbBaseInfo;
import com.xx.chinetek.method.DB.DbManager;
import com.xx.chinetek.method.DB.GreenDaoContext;
import com.xx.chinetek.model.Base.CustomModel;
import com.xx.chinetek.model.Base.ParamaterModel;

import org.greenrobot.greendao.query.WhereCondition;

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

    /**
     * 获取代理商下属代理商和客户
     * @param position 客户类型 0:下属客户 1：所有
     * @return
     */
    public static ArrayList<CustomModel> GetPartnersbyposition(int position) throws Exception{
        ArrayList<CustomModel> customModels= new ArrayList<CustomModel>();
        if(position==1)
            customModels = (ArrayList<CustomModel>) DbManager.getDaoSession(new GreenDaoContext()).getCustomModelDao().queryBuilder()
                    .where(new WhereCondition.StringCondition("1=1 GROUP BY CUSTOMER"))
                    .distinct()
                    .orderAsc(CustomModelDao.Properties.PARTNER_FUNCTION).list();
        else{
            customModels=(ArrayList<CustomModel>) DbManager.getDaoSession(new GreenDaoContext()).getCustomModelDao().queryBuilder().distinct().
                    where(CustomModelDao.Properties.PARTNER_FUNCTION.gt(ParamaterModel.PartenerFUNCTION),CustomModelDao.Properties.CUST_NO_OF_BUSINESS_PARTNER.eq(ParamaterModel.PartenerID))
                    .orderAsc(CustomModelDao.Properties.PARTNER_FUNCTION).list();
        }
        return customModels;
    }

    public static ArrayList<CustomModel> GetFilterCustom() throws Exception{
        ArrayList<CustomModel> customModels= new ArrayList<CustomModel>();
        customModels=(ArrayList<CustomModel>)DbManager.getDaoSession(new GreenDaoContext()).getCustomModelDao().queryBuilder().whereOr(
                new WhereCondition.StringCondition("CUSTOMER in (SELECT DISTINCT LEVEL_2__AGENT__NO from DNMODEL) "),
                new WhereCondition.StringCondition("CUSTOMER in (SELECT DISTINCT CUSTOM__NO from DNMODEL) "))
                .where(CustomModelDao.Properties.CUSTOMER.notEq(ParamaterModel.PartenerID),
                        new WhereCondition.StringCondition("1=1 GROUP BY CUSTOMER")).distinct()
                .orderAsc(CustomModelDao.Properties.PARTNER_FUNCTION).list();
        return customModels;

    }


}
