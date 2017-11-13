package com.xx.chinetek.method.DB;

import android.database.Cursor;

import com.xx.chinetek.greendao.DNDetailModelDao;
import com.xx.chinetek.greendao.DNModelDao;
import com.xx.chinetek.greendao.DNScanModelDao;
import com.xx.chinetek.greendao.DaoSession;
import com.xx.chinetek.model.Base.DNStatusEnum;
import com.xx.chinetek.model.DBReturnModel;
import com.xx.chinetek.model.DN.DNDetailModel;
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
    private DNScanModelDao dnScanModelDao;


    private DbDnInfo() {
        if(daoSession==null) {
            daoSession = DbManager.getDaoSession(new GreenDaoContext());
            dnModelDao=daoSession.getDNModelDao();
            dnDetailModelDao=daoSession.getDNDetailModelDao();
            dnScanModelDao=daoSession.getDNScanModelDao();
        }
    }

    public DaoSession getDaoSession(){
        return daoSession;
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
                dnDetailModelDao.insertOrReplaceInTx(dnModel.getDETAILS());
                dnDetailModelDao.detachAll();
                for(DNDetailModel  dnDetailModel:dnModel.getDETAILS()){
                    dnScanModelDao.insertOrReplaceInTx(dnDetailModel.getSERIALS());
                    dnScanModelDao.detachAll();
                }
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

    /**
     * 根据单号获取DN
     * @return
     */
    public  DNModel GetLoaclDN(String Dnno){
        return dnModelDao.queryBuilder().where(DNModelDao.Properties.AGENT_DN_NO.eq(Dnno)).unique();
    }

    /**
     * 根据单号、行号获取DN明细
     * @return
     */
    public  DNDetailModel GetLoaclDNDetail(String Dnno,Integer lineNo){
        return dnDetailModelDao.queryBuilder().where(DNDetailModelDao.Properties.AGENT_DN_NO.eq(Dnno), DNDetailModelDao.Properties.LINE_NO.eq(lineNo)).unique();
    }


    /**
     * 根据DN单号获取DN明细
     * @param DNNo
     * @return
     */
    public ArrayList<DNDetailModel> GetDNDetailByDNNo(String DNNo){
        ArrayList<DNDetailModel> dnDetailModels=new ArrayList<>();
        dnDetailModels =( ArrayList<DNDetailModel>)dnDetailModelDao.queryBuilder().where(DNDetailModelDao.Properties.AGENT_DN_NO.eq(DNNo)).list();
        return dnDetailModels;
    }

    /**
     * 查询DN单中扫描数量和出库数量
     * @param DNNo
     * @param condition
     */
    public DBReturnModel  GetDNQty(String DNNo,String condition){
        String sql="select sum(DN__QTY) as DNQTY,sum(SCAN__QTY) as SCANQTY from DNDETAIL_MODEL " +
                "where AGENT__DN__NO='"+DNNo+"' and  (ITEM__NO='"+condition+"' or GOLFA__CODE='"+condition+"')";
        DBReturnModel dbReturnModel=new DBReturnModel();
        Cursor cursor= dnDetailModelDao.getDatabase().rawQuery(sql,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                int index=cursor.getColumnIndex("DNQTY");
                dbReturnModel.setDNQTY(cursor.getInt(index));
                index=cursor.getColumnIndex("SCANQTY");
                dbReturnModel.setSCANQTY(cursor.getInt(index));
            }
        }
        cursor.close();
        return dbReturnModel;
    }

    /**
     * 查询扫描明细表
     * @param DNNo
     * @param condition
     * @return
     */
    public Integer GetScanQtyInDNScanModel(String DNNo,String condition,int lineNo){
        Integer qty=0;
        String sql="select  COUNT(ITEM__SERIAL__NO) as  SCANQTY from DNSCAN_MODEL " +
                "where AGENT__DN__NO='"+DNNo+"' and   GOLFA__CODE='"+condition+"' and LINE__NO="+lineNo;
        Cursor cursor= dnDetailModelDao.getDatabase().rawQuery(sql,null);
        if(cursor!=null){
            if(cursor.moveToFirst()){
                int index=cursor.getColumnIndex("SCANQTY");
                qty=cursor.getInt(index);
            }
        }
        cursor.close();
        return qty;
    }



}
