package com.xx.chinetek.model.DN;

import android.os.Parcel;
import android.os.Parcelable;

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
public class DNDetailModel implements Parcelable {

    private String  AGENT_DN_NO;
    private Integer  LINE_NO;
    private String  ITEM_NO;
    private String  ITEM_NAME;
    private String  GOLFA_CODE;
    private Integer  DN_QTY;
    /**
     * AC正常，CO关闭
     */
    private String  DETAIL_STATUS;
    private Date OPER_DATE;
    private Integer  SCAN_QTY;
    /**
     * 状态 0正常 1序列号重复 2数量超出
     */
    private Integer STATUS;
    @ToMany(joinProperties = {
            @JoinProperty(name = "AGENT_DN_NO", referencedName = "AGENT_DN_NO"),
            @JoinProperty(name = "LINE_NO", referencedName = "LINE_NO")})
    List<DNScanModel> SERIALS;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 1853994774)
    private transient DNDetailModelDao myDao;




    @Generated(hash = 2137669822)
    public DNDetailModel(String AGENT_DN_NO, Integer LINE_NO, String ITEM_NO,
            String ITEM_NAME, String GOLFA_CODE, Integer DN_QTY,
            String DETAIL_STATUS, Date OPER_DATE, Integer SCAN_QTY,
            Integer STATUS) {
        this.AGENT_DN_NO = AGENT_DN_NO;
        this.LINE_NO = LINE_NO;
        this.ITEM_NO = ITEM_NO;
        this.ITEM_NAME = ITEM_NAME;
        this.GOLFA_CODE = GOLFA_CODE;
        this.DN_QTY = DN_QTY;
        this.DETAIL_STATUS = DETAIL_STATUS;
        this.OPER_DATE = OPER_DATE;
        this.SCAN_QTY = SCAN_QTY;
        this.STATUS = STATUS;
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

    public void setSERIALS(List<DNScanModel> SERIALS) {
        this.SERIALS = SERIALS;
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




    public Date getOPER_DATE() {
        return this.OPER_DATE;
    }




    public void setOPER_DATE(Date OPER_DATE) {
        this.OPER_DATE = OPER_DATE;
    }




    public Integer getSCAN_QTY() {
        return this.SCAN_QTY;
    }




    public void setSCAN_QTY(Integer SCAN_QTY) {
        this.SCAN_QTY = SCAN_QTY;
    }




    public Integer getSTATUS() {
        return this.STATUS;
    }




    public void setSTATUS(Integer STATUS) {
        this.STATUS = STATUS;
    }




    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1047623420)
    public List<DNScanModel> getSERIALS() {
        if (SERIALS == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DNScanModelDao targetDao = daoSession.getDNScanModelDao();
            List<DNScanModel> SERIALSNew = targetDao
                    ._queryDNDetailModel_SERIALS(AGENT_DN_NO, LINE_NO);
            synchronized (this) {
                if (SERIALS == null) {
                    SERIALS = SERIALSNew;
                }
            }
        }
        return SERIALS;
    }




    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1077848333)
    public synchronized void resetSERIALS() {
        SERIALS = null;
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.AGENT_DN_NO);
        dest.writeValue(this.LINE_NO);
        dest.writeString(this.ITEM_NO);
        dest.writeString(this.ITEM_NAME);
        dest.writeString(this.GOLFA_CODE);
        dest.writeValue(this.DN_QTY);
        dest.writeString(this.DETAIL_STATUS);
        dest.writeLong(this.OPER_DATE != null ? this.OPER_DATE.getTime() : -1);
        dest.writeValue(this.SCAN_QTY);
        dest.writeValue(this.STATUS);
        dest.writeTypedList(this.SERIALS);
    }

    protected DNDetailModel(Parcel in) {
        this.AGENT_DN_NO = in.readString();
        this.LINE_NO = (Integer) in.readValue(Integer.class.getClassLoader());
        this.ITEM_NO = in.readString();
        this.ITEM_NAME = in.readString();
        this.GOLFA_CODE = in.readString();
        this.DN_QTY = (Integer) in.readValue(Integer.class.getClassLoader());
        this.DETAIL_STATUS = in.readString();
        long tmpOPER_DATE = in.readLong();
        this.OPER_DATE = tmpOPER_DATE == -1 ? null : new Date(tmpOPER_DATE);
        this.SCAN_QTY = (Integer) in.readValue(Integer.class.getClassLoader());
        this.STATUS = (Integer) in.readValue(Integer.class.getClassLoader());
        this.SERIALS = in.createTypedArrayList(DNScanModel.CREATOR);
    }

    public static final Parcelable.Creator<DNDetailModel> CREATOR = new Parcelable.Creator<DNDetailModel>() {
        @Override
        public DNDetailModel createFromParcel(Parcel source) {
            return new DNDetailModel(source);
        }

        @Override
        public DNDetailModel[] newArray(int size) {
            return new DNDetailModel[size];
        }
    };
}
