package com.xx.chinetek.method.DB;

import android.database.Cursor;
import android.text.TextUtils;

import com.xx.chinetek.greendao.CustomModelDao;
import com.xx.chinetek.greendao.DaoSession;
import com.xx.chinetek.greendao.MaterialModelDao;
import com.xx.chinetek.greendao.SyncParaModelDao;
import com.xx.chinetek.model.Base.CustomModel;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.Base.SyncParaModel;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GHOST on 2017/11/7.
 */

public class DbBaseInfo {

    public static DbBaseInfo mSyncDB;
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


    public void DeleteCustomDB(){
        customModelDao.deleteAll();
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
     * 修改代理商
     * @param customModel
     */
    public void ModifyPartnersByID(CustomModel customModel){
        if(customModel!=null) {
           String sql="update Custom_Model set name='"+customModel.getNAME()+"' where CUSTOMER='"+customModel.getCUSTOMER()+"'";
            customModelDao.getDatabase().execSQL(sql);
        }
    }

    /**
     * 删除代理商
     * @param customModel
     */
    public void DeletePartnersByID(CustomModel customModel){
        if(customModel!=null) {
            String sql="delete from  Custom_Model  where CUSTOMER='"+customModel.getCUSTOMER()+"'";
            customModelDao.getDatabase().execSQL(sql);
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
        syncParaModelDao.insertOrReplace(syncParaModel);
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
        List<CustomModel> customModels=customModelDao.queryBuilder().where(CustomModelDao.Properties.NAME.eq(CusName)).distinct().list();
        CustomModel customModel=null;
        if(customModels!=null && customModels.size()>0)
            customModel=customModels.get(0);
      return customModel==null?null:customModel.getCUSTOMER();
    }

    public String GetCustomNameById(String CusID){
        List<CustomModel> customModels=customModelDao.queryBuilder().where(CustomModelDao.Properties.CUSTOMER.eq(CusID)).distinct().list();
        CustomModel customModel=null;
        if(customModels!=null && customModels.size()>0)
            customModel=customModels.get(0);
        return customModel==null?null:customModel.getNAME();
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

    public List<MaterialModel> GetItemNames(String GolafCode){
        if(TextUtils.isEmpty(GolafCode)) return null;
        List<MaterialModel>  materialModels = materialModelDao.queryBuilder().where(MaterialModelDao.Properties.BISMT.eq(GolafCode)).list();
        return materialModels;
    }

    public List<MaterialModel> GetItems(String SapNo,String ItemName,String GolafCode){
        List<MaterialModel> materialModels=new ArrayList<>();
        if(!TextUtils.isEmpty(SapNo)){
            materialModels=materialModelDao.queryBuilder().where(MaterialModelDao.Properties.MATNR.eq(SapNo)).list();
        }else{
            if(!TextUtils.isEmpty(ItemName) && !TextUtils.isEmpty(GolafCode)){
                materialModels=materialModelDao.queryBuilder().where(MaterialModelDao.Properties.BISMT.eq(GolafCode),
                        MaterialModelDao.Properties.MAKTX.eq(ItemName)).list();
            }else{
                if(!TextUtils.isEmpty(ItemName) && TextUtils.isEmpty(GolafCode))
                    materialModels=materialModelDao.queryBuilder().where(MaterialModelDao.Properties.MAKTX.eq(ItemName)).list();
                else
                    materialModels=materialModelDao.queryBuilder().where(MaterialModelDao.Properties.BISMT.eq(GolafCode)).list();
            }
        }
        return materialModels;
    }

    public List<MaterialModel> Querytems(String SapNo,String ItemName,String GolafCode,String ItemLine){
        List<MaterialModel> materialModels=new ArrayList<>();
        QueryBuilder queryBuilder=materialModelDao.queryBuilder();
        if(!TextUtils.isEmpty(SapNo))
            queryBuilder=queryBuilder.whereOr(MaterialModelDao.Properties.MATNR.like("%" + SapNo + "%"),
                    MaterialModelDao.Properties.BISMT.like("%" + GolafCode + "%"));
        if(!TextUtils.isEmpty(ItemName))
            queryBuilder=queryBuilder.where(MaterialModelDao.Properties.MAKTX.like("%" + ItemName + "%"));
        if(!TextUtils.isEmpty(ItemLine))
            queryBuilder=queryBuilder.where(MaterialModelDao.Properties.SPART.eq(ItemLine));
        materialModels=queryBuilder.list();

//        if(!TextUtils.isEmpty(ItemName) && !TextUtils.isEmpty(SapNo)) {
//            materialModels= materialModelDao.queryBuilder().where(MaterialModelDao.Properties.MAKTX.like("%" + ItemName + "%"))//,MaterialModelDao.Properties.BISMT.isNotNull()
//                    .whereOr(MaterialModelDao.Properties.MATNR.like("%" + SapNo + "%"),
//                    MaterialModelDao.Properties.BISMT.like("%" + GolafCode + "%")).list();
//        }else if(TextUtils.isEmpty(ItemName) && !TextUtils.isEmpty(SapNo)){
//            materialModels = materialModelDao.queryBuilder().whereOr(MaterialModelDao.Properties.MATNR.like("%" + SapNo + "%"),
//                    MaterialModelDao.Properties.BISMT.like("%" + GolafCode + "%")).list();//.where(MaterialModelDao.Properties.BISMT.isNotNull())
//        }else if(!TextUtils.isEmpty(ItemName) && TextUtils.isEmpty(SapNo)){
//            materialModels = materialModelDao.queryBuilder().where(MaterialModelDao.Properties.MAKTX.like("%" + ItemName + "%")).list();//,MaterialModelDao.Properties.BISMT.isNotNull()
//        }
       return materialModels;
    }

    public ArrayList<String> QueryItemLines(){
        ArrayList<String> ItemLines=new ArrayList<>();
        ItemLines.add("所有");
        String sql="SELECT DISTINCT SPART FROM MATERIAL_MODEL;";
        Cursor c = materialModelDao.getDatabase().rawQuery(sql,null);
        try{
            if (c.moveToFirst()) {
                do {
                    ItemLines.add(c.getString(0));
                } while (c.moveToNext());
            }
        } finally {
            c.close();
        }
        return ItemLines;
    }
}
