package com.xx.chinetek.method.DB;

import android.text.TextUtils;

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
     * 获取系统参数
     * @return
     * @throws Exception
     */
    public List<SyncParaModel> GetSysPara(){
        return syncParaModelDao.queryBuilder().distinct().list();
    }

    /**
     * 更新系统参数
     * @param syncParaModel
     */
    public void UpdateSysPara(SyncParaModel syncParaModel){
        syncParaModelDao.updateInTx(syncParaModel);
    }

    /**
     * 根据类型查询客户或代理商
     * @param DNCusType
     * @return
     * @throws Exception
     */
    public List<CustomModel> QueryCustomDBByType(int DNCusType) throws Exception{
        List<CustomModel> customModels=  customModelDao.queryBuilder().where(CustomModelDao.Properties.PARTNER_FUNCTION.eq(DNCusType==0?"Z2":"Z3")).distinct().list();
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
     * 判断是否存在物料数据
     * @return
     */
    public Boolean HasMaterialInfo(){
        return materialModelDao.queryBuilder().buildCount().count()>0;
    }

    /**
     * 根据名称查询编号
     * @param CusName
     * @return
     */
    public String GetCustomName(String CusName){
      CustomModel customModel=customModelDao.queryBuilder().where(CustomModelDao.Properties.NAME.eq(CusName)).unique();
      return customModel==null?null:customModel.getCUSTOMER();
    }

    /**
     * 根据SAP编号或GolfaCode查名称
     * @param condition
     * @return
     */
    public MaterialModel GetItemName(String condition){
        if(TextUtils.isEmpty(condition)) return null;
        MaterialModel  materialModel = materialModelDao.queryBuilder().whereOr(MaterialModelDao.Properties.MATNR.eq(condition),
                MaterialModelDao.Properties.BISMT.eq(condition)).unique();
        return materialModel;
    }
}
