package com.xx.chinetek.method.DB;

import com.xx.chinetek.greendao.CustomModelDao;
import com.xx.chinetek.greendao.DaoSession;
import com.xx.chinetek.greendao.MaterialModelDao;
import com.xx.chinetek.greendao.SyncParaModelDao;
import com.xx.chinetek.model.Base.CustomModel;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.Base.SyncParaModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GHOST on 2017/11/7.
 */

public class DbBaseInfo {

    private static DbBaseInfo mSyncDB;
    private DaoSession daoSession=null;
    private MaterialModelDao materialModelDao;
    private CustomModelDao customModelDao;
    private SyncParaModelDao syncParaModelDao;

    private DbBaseInfo() {
        if(daoSession==null) {
            daoSession = DbManager.getDaoSession(new GreenDaoContext());
            materialModelDao = daoSession.getMaterialModelDao();
            customModelDao = daoSession.getCustomModelDao();
            syncParaModelDao=daoSession.getSyncParaModelDao();
        }
    }

    public static DbBaseInfo getInstance() {
        if (null == mSyncDB) {
            synchronized (DbManager.class) {
                if (null == mSyncDB) {
                    mSyncDB = new DbBaseInfo();
                }
            }
        }
        return mSyncDB;
    }

    /**
     * 插入物料
     * @param materialModels
     * @throws Exception
     */
    public void InsertMaterialDB(ArrayList<MaterialModel> materialModels) throws Exception{
        if(materialModels!=null && materialModels.size()!=0) {
            materialModelDao.insertOrReplaceInTx(materialModels);
            materialModelDao.detachAll();
        }
    }



    /**
     * 插入客户代理商
     * @param customModels
     * @throws Exception
     */
    public void InsertCustomDB(ArrayList<CustomModel> customModels) throws Exception{
        if(customModels!=null && customModels.size()!=0) {
            customModelDao.insertOrReplaceInTx(customModels);
            customModelDao.detachAll();
        }
    }

    /**
     * 根据类型查询客户或代理商
     * @param DNCusType
     * @return
     * @throws Exception
     */
    public List<CustomModel> QueryCustomDBByType(int DNCusType) throws Exception{
        List<CustomModel> customModels=  customModelDao.queryBuilder().where(CustomModelDao.Properties.Type.eq(DNCusType==0?"Z2":"Z3")).distinct().list();
        return customModels;
    }

    /**
     * 插入客户代理商
     * @param paraModels
     * @throws Exception
     */
    public void InsertParamaterDB(ArrayList<SyncParaModel> paraModels) throws Exception{
        if(paraModels!=null && paraModels.size()!=0) {
            syncParaModelDao.insertOrReplaceInTx(paraModels);
            syncParaModelDao.detachAll();
        }
    }

    /**
     * 根据编号查询名称
     * @param CusCode
     * @return
     */
    public String GetCustomName(String CusCode){
      CustomModel customModel=customModelDao.queryBuilder().where(CustomModelDao.Properties.PartnerID.eq(CusCode)).unique();
      return customModel==null?null:customModel.getPartnerName();
    }

    /**
     * 根据SAP编号或GolfaCode查名称
     * @param condition
     * @return
     */
    public MaterialModel GetItemName(String condition){
        MaterialModel  materialModel = materialModelDao.queryBuilder().whereOr(MaterialModelDao.Properties.MATNR.eq(condition),
                MaterialModelDao.Properties.BISMT.eq(condition)).unique();
        return materialModel;
    }
}
