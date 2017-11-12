package com.xx.chinetek.model.DN;

import com.xx.chinetek.greendao.DNDetailModelDao;
import com.xx.chinetek.greendao.DNScanModelDao;
import com.xx.chinetek.greendao.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.Keep;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.Date;
import java.util.List;

/**
 * Created by GHOST on 2017/10/25.
 */

@Entity(
        indexes={
            @Index(value = "AGENT_DN_NO,LINE_NO", unique = true)
        }
)
public class DNDetailModel {

    private String  AGENT_DN_NO;
    private Integer  LINE_NO;
    private String  ITEM_NO;
    private String  ITEM_NAME;
    private String  GOLFA_CODE;
    private Integer  DN_QTY;
    private String  DETAIL_STATUS;
    private Date UPDATE_DATE;
    private String UPDATE_USER;
    private Integer  SCAN_QTY;
    @ToMany(joinProperties = {
            @JoinProperty(name = "AGENT_DN_NO", referencedName = "AGENT_DN_NO"),
            @JoinProperty(name = "LINE_NO", referencedName = "LINE_NO")})
    List<DNScanModel> dnScanModels;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1853994774)
    private transient DNDetailModelDao myDao;



    @Generated(hash = 823850877)
    public DNDetailModel(String AGENT_DN_NO, Integer LINE_NO, String ITEM_NO, String ITEM_NAME,
            String GOLFA_CODE, Integer DN_QTY, String DETAIL_STATUS, Date UPDATE_DATE,
            String UPDATE_USER, Integer SCAN_QTY) {
        this.AGENT_DN_NO = AGENT_DN_NO;
        this.LINE_NO = LINE_NO;
        this.ITEM_NO = ITEM_NO;
        this.ITEM_NAME = ITEM_NAME;
        this.GOLFA_CODE = GOLFA_CODE;
        this.DN_QTY = DN_QTY;
        this.DETAIL_STATUS = DETAIL_STATUS;
        this.UPDATE_DATE = UPDATE_DATE;
        this.UPDATE_USER = UPDATE_USER;
        this.SCAN_QTY = SCAN_QTY;
    }



    @Generated(hash = 395629670)
    public DNDetailModel() {
    }


    @Keep
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DNDetailModel that = (DNDetailModel) o;
        boolean eq=false;
        if(ITEM_NO!=null){
            eq=ITEM_NO.trim().equals(that.ITEM_NO.trim());
        }

        return eq || GOLFA_CODE.trim().equals(that.GOLFA_CODE.trim());

    }



    public String getAGENT_DN_NO() {
        return this.AGENT_DN_NO;
    }


    public void setDnScanModels(List<DNScanModel> dnScanModels) {
        this.dnScanModels = dnScanModels;
    }

    public void setAGENT_DN_NO(String AGENT_DN_NO) {
        this.AGENT_DN_NO = AGENT_DN_NO;
    }



    public Integer getLINE_NO() {
        return this.LINE_NO;
    }



    public void setLINE_NO(Integer LINE_NO) {
        this.LINE_NO = LINE_NO;
    }



    public String getITEM_NO() {
        return this.ITEM_NO;
    }



    public void setITEM_NO(String ITEM_NO) {
        this.ITEM_NO = ITEM_NO;
    }



    public String getITEM_NAME() {
        return this.ITEM_NAME;
    }



    public void setITEM_NAME(String ITEM_NAME) {
        this.ITEM_NAME = ITEM_NAME;
    }




    public String getGOLFA_CODE() {
        return this.GOLFA_CODE;
    }



    public void setGOLFA_CODE(String GOLFA_CODE) {
        this.GOLFA_CODE = GOLFA_CODE;
    }



    public Integer getDN_QTY() {
        return this.DN_QTY;
    }



    public void setDN_QTY(Integer DN_QTY) {
        this.DN_QTY = DN_QTY;
    }



    public String getDETAIL_STATUS() {
        return this.DETAIL_STATUS;
    }



    public void setDETAIL_STATUS(String DETAIL_STATUS) {
        this.DETAIL_STATUS = DETAIL_STATUS;
    }



    public Date getUPDATE_DATE() {
        return this.UPDATE_DATE;
    }



    public void setUPDATE_DATE(Date UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }





    public Integer getSCAN_QTY() {
        return this.SCAN_QTY;
    }



    public void setSCAN_QTY(Integer SCAN_QTY) {
        this.SCAN_QTY = SCAN_QTY;
    }



    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1346071709)
    public List<DNScanModel> getDnScanModels() {
        if (dnScanModels == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DNScanModelDao targetDao = daoSession.getDNScanModelDao();
            List<DNScanModel> dnScanModelsNew = targetDao
                    ._queryDNDetailModel_DnScanModels(AGENT_DN_NO, LINE_NO);
            synchronized (this) {
                if (dnScanModels == null) {
                    dnScanModels = dnScanModelsNew;
                }
            }
        }
        return dnScanModels;
    }



    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 630488495)
    public synchronized void resetDnScanModels() {
        dnScanModels = null;
    }



    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }



    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }



    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }



    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2036050164)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDNDetailModelDao() : null;
    }



    public String getUPDATE_USER() {
        return this.UPDATE_USER;
    }



    public void setUPDATE_USER(String UPDATE_USER) {
        this.UPDATE_USER = UPDATE_USER;
    }
}
