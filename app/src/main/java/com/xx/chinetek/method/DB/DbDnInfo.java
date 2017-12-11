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
import java.util.Date;
import java.util.List;

/**
 * Created by GHOST on 2017/11/7.
 */

public class DbDnInfo {

    public static DbDnInfo mSyncDn;
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
            cal.add(Calendar.DAY_OF_MONTH, 0- ParamaterModel.baseparaModel.getDNSaveTime());
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

    public void InsertDNDetailDB(ArrayList<DNDetailModel> dnDetailModels) throws Exception{
        if(dnDetailModels!=null && dnDetailModels.size()!=0) {
                dnDetailModelDao.insertOrReplaceInTx(dnDetailModels);
                dnDetailModelDao.detachAll();
        }
    }

    public void UpdateOperaterData(DNModel  dnModel) {
        dnModel.setOPER_DATE(new Date());
        dnModelDao.insertOrReplace(dnModel);
//        String sql="update DNMODEL set OPER__DATE='"+new Date()+"' where AGENT__DN__NO='"+dnModel.getAGENT_DN_NO()+"'";
//        dnModelDao.getDatabase().execSQL(sql);
    }

    /**
     * 获取本地数据所有未处理完成单据
     * @return
     */
    public  ArrayList<DNModel> GetLoaclDN(){
        ArrayList<DNModel> dnModels=new ArrayList<>();
        dnModels=(ArrayList<DNModel>) dnModelDao.queryBuilder().distinct()
                .whereOr(DNModelDao.Properties.STATUS.eq(DNStatusEnum.ready),
                        DNModelDao.Properties.STATUS.eq(DNStatusEnum.download))
                .orderDesc(DNModelDao.Properties.DN_DATE).list();
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
                .where(DNModelDao.Properties.STATUS.notEq(DNStatusEnum.ready))
                .orderAsc(DNModelDao.Properties.DN_SOURCE).distinct().list();
        return dnModels;
    }

    /**
     * 根据单号获取DN
     * @return
     */
    public  DNModel GetLoaclDN(String Dnno){
        return dnModelDao.queryBuilder().whereOr(DNModelDao.Properties.CUS_DN_NO.eq(Dnno),DNModelDao.Properties.AGENT_DN_NO.eq(Dnno)).unique();
    }

    /**
     * 查询DN单据是否存在，且状态不是未下载
     * @param Dnno
     * @return
     */
    public Boolean CheckDNInDB(String Dnno){
          long count=dnModelDao.queryBuilder().where(DNModelDao.Properties.AGENT_DN_NO.eq(Dnno),
                  DNModelDao.Properties.STATUS.notEq(DNStatusEnum.ready)).distinct().count();
          return  count==0;
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
        dnDetailModels =( ArrayList<DNDetailModel>)dnDetailModelDao.queryBuilder().where(DNDetailModelDao.Properties.AGENT_DN_NO.eq(DNNo))
               .list();
        for(int i=0;i<dnDetailModels.size();i++){
            dnDetailModels.get(i).getSERIALS();
            dnDetailModels.get(i).setSCAN_QTY(dnDetailModels.get(i).getSERIALS().size());
        }
        return dnDetailModels;
    }


    public void DeleteDN(DNModel dnModel){
        DELscanbyagent(dnModel.getAGENT_DN_NO());
        DelDetailAllNum(dnModel.getAGENT_DN_NO());
        DelDNmodels(dnModel.getAGENT_DN_NO());
    }

    /**
     * 查询DN单中扫描数量和出库数量
     * @param DNNo
     * @param condition
     */
    public DBReturnModel  GetDNQty(String DNNo,String condition,Integer Line_NO){
        String sql="select sum(DN__QTY) as DNQTY,sum(SCAN__QTY) as SCANQTY from DNDETAIL_MODEL " +
                "where AGENT__DN__NO='"+DNNo+"' and  (ITEM__NO='"+condition+"' or GOLFA__CODE='"+condition+"')";
        if(Line_NO!=null)
            sql+= " and LINE__NO="+Line_NO;
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

    /**
     * 自建单据，根据自定义单号查询系统单号
     * @param CusNo
     */
    public String GetAgentNoByCusDnNO(String CusNo){
        DNModel dnModel=dnModelDao.queryBuilder().where(DNModelDao.Properties.CUS_DN_NO.eq(CusNo)).unique();
        if(dnModel!=null){
            return  dnModel.getAGENT_DN_NO();
        }
        return "";
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
        for(int i=0;i<dnModeldetails.size();i++){
            dnModeldetails.get(i).getSERIALS();
        }
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
     * 获取本地数据表体
     * @return
     */
    public  DNDetailModel GetLoaclDNDetailStatus(String DNNo,String Material,Integer lineno){
        ArrayList<DNDetailModel> DNDetailModels=new ArrayList<>();
        DNDetailModels=(ArrayList<DNDetailModel>) dnDetailModelDao.queryBuilder().distinct()
                .where(DNDetailModelDao.Properties.AGENT_DN_NO.eq(DNNo),DNDetailModelDao.Properties.GOLFA_CODE.eq(Material),DNDetailModelDao.Properties.LINE_NO.eq(lineno)).list();
        return DNDetailModels.get(0);
    }

    /**
     * 查询扫描表是否重复
     * @param DNNo
     * @param Material
     * @param lineno
     * @return
     */
    public boolean GetDNScanOKModel(String DNNo,String Material,Integer lineno){
        String sql="select count(SERIAL__NO) from DNSCAN_MODEL where AGENT__DN__NO='"+DNNo+"' and GOLFA__CODE='"+Material+"' and LINE__NO='"+ lineno +"' group by SERIAL__NO HAVING count(SERIAL__NO)>1";
        Cursor cursor= dnDetailModelDao.getDatabase().rawQuery(sql,null);
        cursor.close();
        if(cursor!=null){
            return false;
        }
       return  true;
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

    /**
     * 判断是否存在非三菱条码
     * @param Dnno
     * @return
     */
    public long HasCusBarcode(String Dnno){
       return dnScanModelDao.queryBuilder().where(DNScanModelDao.Properties.AGENT_DN_NO.eq(Dnno),
                DNScanModelDao.Properties.MAT_TYPE.eq(0)).distinct().count();
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
     * 修改主表明细行的扫描数据
     * @param DNNo
     */
    public boolean DelDetailAllNum(String DNNo){
        try{
            String sql="";
            sql="delete from DNDETAIL_MODEL where AGENT__DN__NO='"+DNNo+"'";
            daoSession.getDatabase().execSQL(sql);
            return true;
        }catch(Exception ex){
            return false;
        }

    }


    /**
     * 删除扫描记录根据主表主键
     * @param DNNo
     */
    public boolean DELscanbyagent(String DNNo){
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
    public boolean DELscanbyserial(String DNNo,String Material,Integer lineno,String serialno,String condition,String status){
        try{
            String deletesql="delete from DNSCAN_MODEL where AGENT__DN__NO='"+ DNNo
                    +"' and LINE__NO='"+ lineno +"' and GOLFA__CODE='"+ Material
                    +"'and SERIAL__NO='"+ serialno+"' and status='"+status+"'";
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
     * 删除主表
     * @param DNNo
     */
    public boolean DelDNmodels(String DNNo){
        try{
            String sql="";
            sql="delete from DNMODEL where AGENT__DN__NO='"+ DNNo+"'";
            daoSession.getDatabase().execSQL(sql);
            return true;
        }catch(Exception ex){
            return false;
        }
    }

    /**
     * 修改扫描记录的状态
     * @param DNNo
     *  @param Material
     * @param lineno
     */
    public boolean UpdateDNScanState(String DNNo,String Material,Integer lineno){
        try{
            String sql = "update DNSCAN_MODEL set status='0' where AGENT__DN__NO='"+DNNo+"' and GOLFA__CODE='"+Material+"' and LINE__NO='"+lineno+"'";
            dnScanModelDao.getDatabase().execSQL(sql);
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


    /**
     * 整理三层的提交数据
     * @param model
     */
    public DNModel AllPostDate(DNModel model){
        try{
            ArrayList<DNDetailModel> dndetails = GetLoaclExceptionDetailsDN(model.getAGENT_DN_NO());
            for(int i=0;i<dndetails.size();i++){
                ArrayList<DNScanModel> dnscans =  GetLoaclDNScanModelDN(dndetails.get(i).getAGENT_DN_NO(),dndetails.get(i).getGOLFA_CODE(),dndetails.get(i).getLINE_NO());
                dndetails.get(i).setSERIALS(dnscans);
            }
            model.setDETAILS(dndetails);
            return model;
        }catch(Exception ex){
            return null;
        }
    }


    /**
     * 检查提交的数据
     * @param model
     */
    public boolean CheckPostDate(DNModel model){
        long count=dnScanModelDao.queryBuilder().where(DNScanModelDao.Properties.AGENT_DN_NO.eq(model.getAGENT_DN_NO()),
                DNScanModelDao.Properties.STATUS.notEq(0)).distinct().count();
        return  count==0;
    }


    /**
     * 插入DNscan
     * @param dnscanmodels
     * @throws Exception
     */
    public Boolean InsertDNScan(ArrayList<DNScanModel> dnscanmodels){
        try{
            if(dnscanmodels!=null && dnscanmodels.size()!=0) {
                for(DNScanModel  Model:dnscanmodels){
                    dnScanModelDao.insertOrReplaceInTx(Model);
                    dnScanModelDao.detachAll();
                }
            }
            return true;
        }catch(Exception ex){
            return false;
        }

    }


//    /**
//     * 插入DN
//     * @param dnModels
//     * @throws Exception
//     */
//    public void InsertDNDBymh(ArrayList<DNModel> dnModels) throws Exception{
//        if(dnModels!=null && dnModels.size()!=0) {
//            dnModelDao.insertOrReplaceInTx(dnModels);
//            dnModelDao.detachAll();
//            for(DNModel  dnModel :dnModels){
//                dnDetailModelDao.insertOrReplaceInTx(dnModel.getDETAILS());
//                dnDetailModelDao.detachAll();
//                for(DNDetailModel  dnDetailModel:dnModel.getDETAILS()){
//                    dnScanModelDao.insertOrReplaceInTx(dnDetailModel.getSERIALS());
//                    dnScanModelDao.detachAll();
//                }
//            }
//        }
//    }

}

