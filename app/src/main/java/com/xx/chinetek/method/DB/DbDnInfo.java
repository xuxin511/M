package com.xx.chinetek.method.DB;

import android.database.Cursor;

import com.xx.chinetek.greendao.DNDetailModelDao;
import com.xx.chinetek.greendao.DNModelDao;
import com.xx.chinetek.greendao.DNScanModelDao;
import com.xx.chinetek.greendao.DaoSession;
import com.xx.chinetek.model.Base.DNStatusEnum;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DBReturnModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
     * 删除超出时间DN单据
     */
    public void DeleteDnBySaveTime(){
        try {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 0- ParamaterModel.DNSaveTime);
            List<DNModel> dnModels = dnModelDao.queryBuilder().where(DNModelDao.Properties.OPER_DATE.le(cal.getTimeInMillis())).distinct().list();
            if (dnModels != null & dnModels.size() != 0) {
                for (DNModel dnModel : dnModels) {
                    String sql = "delete from DNMODEL where AGENT__DN__NO='" + dnModel.getAGENT_DN_NO() + "'";
                    dnModelDao.getDatabase().execSQL(sql);
                    sql = "delete from DNDETAIL_MODEL where AGENT__DN__NO='" + dnModel.getAGENT_DN_NO() + "'";
                    dnDetailModelDao.getDatabase().execSQL(sql);
                    sql = "delete from DNSCAN_MODEL where AGENT__DN__NO='" + dnModel.getAGENT_DN_NO() + "'";
                    dnScanModelDao.getDatabase().execSQL(sql);
                }
            }
        }catch (Exception ex){}
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
                .whereOr(DNModelDao.Properties.STATUS.eq(DNStatusEnum.ready),
                        DNModelDao.Properties.STATUS.eq(DNStatusEnum.download)).list();
        return dnModels;
    }

    /**
     * 获取本地数据所有异常单据
     * @return
     */
    public  ArrayList<DNModel> GetLoaclExceptDN(){
        ArrayList<DNModel> dnModels=new ArrayList<>();
        dnModels=(ArrayList<DNModel>) dnModelDao.queryBuilder().distinct()
                .where(DNModelDao.Properties.STATUS.eq(DNStatusEnum.exeption)).list();
        return dnModels;
    }

    /**
     * 获取本地数据所有完成单据
     * @return
     */
    public  ArrayList<DNModel> GetLoaclcompleteDN(){
        ArrayList<DNModel> dnModels=new ArrayList<>();
        dnModels=(ArrayList<DNModel>) dnModelDao.queryBuilder().distinct()
                .where(DNModelDao.Properties.STATUS.eq(DNStatusEnum.complete)).list();
        return dnModels;
    }

    /**
     * 查询除未下载之外所有单据
     * @return
     */
    public  ArrayList<DNModel> GetLoaclDNbyCondition(){
        ArrayList<DNModel> dnModels=new ArrayList<>();
        dnModels=(ArrayList<DNModel>) dnModelDao.queryBuilder()
                .where(DNModelDao.Properties.STATUS.notEq(DNStatusEnum.ready)).distinct().list();
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
        String sql="select  COUNT(SERIAL__NO) as  SCANQTY from DNSCAN_MODEL " +
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

    /**
     *更新单据状态
     * @param DnNo
     */
    public void ChangeDNStatusByDnNo(String DnNo,int Status){
        String sql="update DNMODEL set status="+Status+" where AGENT__DN__NO='"+DnNo+"'";
        dnModelDao.getDatabase().execSQL(sql);
        if(Status==DNStatusEnum.Sumbit){
            sql="update DNDETAIL_MODEL set status=0 where AGENT__DN__NO='"+DnNo+"'";
            dnDetailModelDao.getDatabase().execSQL(sql);
            sql="update DNSCAN_MODEL set status=0 where AGENT__DN__NO='"+DnNo+"'";
            dnScanModelDao.getDatabase().execSQL(sql);
        }
    }


    //ymh
    /**
     * 获取本地数据所有异常的单据
     * @return
     */
    public  ArrayList<DNModel> GetLoaclExceptionDN(){
        ArrayList<DNModel> dnModels=new ArrayList<>();
        dnModels=(ArrayList<DNModel>) dnModelDao.queryBuilder().distinct()
                .where(DNModelDao.Properties.DN_STATUS.eq(DNStatusEnum.exeption)).list();
        return dnModels;
    }

    /**
     * 获取本地数据所有异常的单据的明细
     * @return
     */
    public  ArrayList<DNDetailModel> GetLoaclExceptionDetailsDN(String DNNo){
        ArrayList<DNDetailModel> dnModeldetails=new ArrayList<>();
        dnModeldetails=(ArrayList<DNDetailModel>) dnDetailModelDao.queryBuilder().distinct()
                .where(DNDetailModelDao.Properties.AGENT_DN_NO.eq(DNNo)).list();
        return dnModeldetails;
    }

    /**
     * 获取本地数据所有异常的单据的明细扫描数据
     * @return
     */
    public  ArrayList<DNScanModel> GetLoaclDNScanModelDN(String DNNo, String Material,Integer lineno){
        ArrayList<DNScanModel> DNScanModels=new ArrayList<>();
        DNScanModels=(ArrayList<DNScanModel>) dnScanModelDao.queryBuilder().distinct()
                .where(DNScanModelDao.Properties.AGENT_DN_NO.eq(DNNo),DNScanModelDao.Properties.GOLFA_CODE.eq(Material),DNScanModelDao.Properties.LINE_NO.eq(lineno)).list();
        return DNScanModels;
    }

    /**
     * 获取本地数据所有异常的单据的明细扫描数据
     * @return
     */
    public  Integer GetLoaclDNScanModelDNNum(String DNNo,String Material,Integer lineno){
        ArrayList<DNScanModel> DNScanModels=new ArrayList<>();
        DNScanModels=(ArrayList<DNScanModel>) dnScanModelDao.queryBuilder().distinct()
                .where(DNScanModelDao.Properties.AGENT_DN_NO.eq(DNNo),DNScanModelDao.Properties.GOLFA_CODE.eq(Material),DNScanModelDao.Properties.LINE_NO.eq(lineno)).list();
        return DNScanModels.size();
    }

    /**
     * 获取本地数据单据下是否拥有扫描数据
     * @return
     */
    public  Integer GetLoaclDNScanModelDNNumbyDNNO(String DNNo){
        ArrayList<DNScanModel> DNScanModels=new ArrayList<>();
        DNScanModels=(ArrayList<DNScanModel>) dnScanModelDao.queryBuilder().distinct()
                .where(DNScanModelDao.Properties.AGENT_DN_NO.eq(DNNo)).list();
        return DNScanModels.size();
    }


    //非自建单据更新数据
    /**
     * 修改主表明细行的扫描数据
     * @param DNNo
     * @param Material
     */
    public boolean UpdateDetailNum(String DNNo,String Material,Integer lineno,Integer Number,Integer DNSOURCE){
        try{
            String sql="";
            if(DNSOURCE==3&&Number==0){
                sql="delete from DNDETAIL_MODEL where AGENT__DN__NO='"+DNNo+"' and GOLFA__CODE='"+Material+"' and LINE__NO='"+lineno+"'";
            }else{
                sql="update DNDETAIL_MODEL set SCAN__QTY= '"+Number+"' where AGENT__DN__NO='"+DNNo+"' and GOLFA__CODE='"+Material+"' and LINE__NO='"+lineno+"'";
            }

            daoSession.getDatabase().execSQL(sql);
            return true;
        }catch(Exception ex){
            return false;
        }
    }



    /**
     * 修改主表明细行的扫描数据
     * @param DNNo
     * @param Number
     */
    public boolean UpdateDetailAllNum(String DNNo,Integer Number,Integer DNSOURCE){
        try{
            String sql="";
            if(DNSOURCE==3&&Number==0){
                sql="delete from DNDETAIL_MODEL where AGENT__DN__NO='"+DNNo+"'";
            }else{
                sql="update DNDETAIL_MODEL set SCAN__QTY= '"+Number+"' where AGENT__DN__NO='"+DNNo+"'";
            }

            daoSession.getDatabase().execSQL(sql);
            return true;
        }catch(Exception ex){
            return false;
        }

    }


    /**
     * 删除扫描记录根据主表主键
     * @param DNNo
     * @param condition
     */
    public boolean DELscanbyagent(String DNNo,String condition){
        try{
            String deletesql="delete from DNSCAN_MODEL " +
                    "where AGENT__DN__NO='"+DNNo+"'";
            daoSession.getDatabase().execSQL(deletesql);
            return true;
        }catch(Exception ex){
            return false;
        }

    }

    /**
     * 删除扫描记录根据主表明细主键
     * @param model
     * @param condition
     */
    public boolean DELscanbyagentdetail(DNDetailModel model,String condition){
        try{
            String deletesql="delete from DNSCAN_MODEL " +
                    "where AGENT__DN__NO='"+model.getAGENT_DN_NO()+"' and GOLFA__CODE='"+ model.getGOLFA_CODE()+"' and LINE__NO='"+ model.getLINE_NO() +"'";
            daoSession.getDatabase().execSQL(deletesql);
            return true;
        }catch(Exception ex){
            return false;
        }

    }

    /**
     * 删除扫描记录根据序列号ymh
     * @param serialno
     * @param condition
     */
    public boolean DELscanbyserial(String DNNo,String Material,Integer lineno,String serialno,String condition){
        try{
            String deletesql="delete from DNSCAN_MODEL where AGENT__DN__NO='"+ DNNo +"' and LINE__NO='"+ lineno +"' and GOLFA__CODE='"+ Material +"'and SERIAL__NO='"+ serialno+"'";
            daoSession.getDatabase().execSQL(deletesql);
            return true;
        }catch(Exception ex){
            return false;
        }

    }

//非自建单据更新数据
    /**
     * 修改主表的状态
     * @param DNNo
     *  @param status
     * @param condition
     */
    public boolean UpdateDNmodelState(String DNNo,String status,String condition,Integer DNSOURCE){
        try{
            String sql="";
            if(DNSOURCE==3){
                sql="delete from DNMODEL where AGENT__DN__NO='"+ DNNo+"'";
                daoSession.getDatabase().execSQL(sql);
            }else{
                sql="update DNMODEL set status="+status+" where AGENT__DN__NO='"+DNNo+"'";
                dnModelDao.getDatabase().execSQL(sql);
            }
          return true;
        }catch(Exception ex){
            return false;
        }
    }


    /**
     * 修改主表的状态异常出库表
     * @param DNNo
     * @param condition
     */
    public boolean UpdateDNmodelDetailNumberbyDN(String DNNo,String condition){
        try{
            String sql="update DNDETAIL_MODEL set DN__QTY=0 " +
                    "where AGENT__DN__NO='"+ DNNo +"'";
            daoSession.getDatabase().execSQL(sql);
            return true;
        }catch(Exception ex){
            return false;
        }
    }

//    /**
//     * 修改主表的状态根据明细行
//     * @param model
//     * @param condition
//     */
//    public boolean UpdateDNmodelDetailNumberbyGOLFACODE(DNDetailModel model,String condition){
//        try{
//            String sql="update DNMODEL set DN__QTY=0 " +
//                    "where AGENT__DN__NO='"+model.getAGENT_DN_NO()+"' and GOLFA_CODE='"+ model.getGOLFA_CODE()+"'";
//            daoSession.getDatabase().execSQL(sql);
//            return true;
//        }catch(Exception ex){
//            return false;
//        }
//    }



}

