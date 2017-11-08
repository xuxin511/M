package com.xx.chinetek.method.DB;

import com.xx.chinetek.greendao.DNDetailModelDao;
import com.xx.chinetek.greendao.DNModelDao;
import com.xx.chinetek.greendao.DaoSession;
import com.xx.chinetek.model.Base.DNStatusEnum;
import com.xx.chinetek.model.DN.DNModel;

import java.util.ArrayList;

/**
 * Created by GHOST on 2017/11/7.
 */

public class DbDnInfo {

    private static DbDnInfo mSyncDn;
    private DaoSession daoSession=null;
    private DNModelDao dnModelDao;
    private DNDetailModelDao dnDetailModelDao;


    private DbDnInfo() {
        if(daoSession==null) {
            daoSession = DbManager.getDaoSession(new GreenDaoContext());
            dnModelDao=daoSession.getDNModelDao();
            dnDetailModelDao=daoSession.getDNDetailModelDao();
        }
    }

    public static DbDnInfo getInstance() {
        if (null == mSyncDn) {
            synchronized (DbManager.class) {
                if (null == mSyncDn) {
                    mSyncDn = new DbDnInfo();
                }
            }
        }
        return mSyncDn;
    }

    /**
     * 插入DN
     * @param dnModels
     * @throws Exception
     */
    public void InsertDNDB(ArrayList<DNModel> dnModels) throws Exception{
        if(dnModels!=null && dnModels.size()!=0) {
            dnModelDao.insertOrReplaceInTx(dnModels);
            dnModelDao.detachAll();
            for(DNModel  dnModel :dnModels){
                dnDetailModelDao.insertOrReplaceInTx(dnModel.getDetailModels());
                dnDetailModelDao.detachAll();
            }
        }
    }

    /**
     * 获取本地数据所有未处理完成单据
     * @return
     */
    public  ArrayList<DNModel> GetLoaclDN(){
        ArrayList<DNModel> dnModels=new ArrayList<>();
        dnModels=(ArrayList<DNModel>) dnModelDao.queryBuilder().distinct()
                .whereOr(DNModelDao.Properties.DN_STATUS.eq(DNStatusEnum.ready),
                        DNModelDao.Properties.DN_STATUS.eq(DNStatusEnum.download)).list();
        return dnModels;
    }
}
