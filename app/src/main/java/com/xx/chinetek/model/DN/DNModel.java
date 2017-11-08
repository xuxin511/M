package com.xx.chinetek.model.DN;

import android.os.Parcel;
import android.os.Parcelable;

import com.xx.chinetek.greendao.DNDetailModelDao;
import com.xx.chinetek.greendao.DNModelDao;
import com.xx.chinetek.greendao.DaoSession;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.JoinProperty;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by GHOST on 2017/10/25.
 */

@Entity
public class DNModel implements Parcelable {

    /*
    出库单号
     */
    @Unique
    private String AGENT_DN_NO;
    /*
    出库日期
     */
    private Date DN_DATE;
    /*
      出库状态
    */
    private int DN_STATUS;
    /*
    一级代理商编码
    */
    private String LEVEL_1_AGENT_NO;
    /*
一级代理商名称
*/
    private String LEVEL_1_AGENT_NAME;
    /*
二级代理商编码
*/
    private String LEVEL_2_AGENT_NO;
    /*
二级代理商名称
*/
    private String LEVEL_2_AGENT_NAME;
    /*
    客户编码
     */
    private String CUSTOM_NO;
    /*
    客户名称
     */
    private String CUSTOM_NAME;
    /*
    出库数量
     */
    private Integer DN_QTY;
    /*
    修改人
     */
    private String UPDATE_USER;
    /*
    修改日期
     */
    private Date UPDATE_DATE;
    /*
 单据来源
  */
    private String DN_SOURCE;


    @ToMany(joinProperties = {
            @JoinProperty(name = "AGENT_DN_NO", referencedName = "AGENT_DN_NO")})
    List<DNDetailModel> detailModels;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.AGENT_DN_NO);
        dest.writeLong(this.DN_DATE != null ? this.DN_DATE.getTime() : -1);
        dest.writeInt(this.DN_STATUS);
        dest.writeString(this.LEVEL_1_AGENT_NO);
        dest.writeString(this.LEVEL_1_AGENT_NAME);
        dest.writeString(this.LEVEL_2_AGENT_NO);
        dest.writeString(this.LEVEL_2_AGENT_NAME);
        dest.writeString(this.CUSTOM_NO);
        dest.writeString(this.CUSTOM_NAME);
        dest.writeValue(this.DN_QTY);
        dest.writeString(this.UPDATE_USER);
        dest.writeLong(this.UPDATE_DATE != null ? this.UPDATE_DATE.getTime() : -1);
        dest.writeString(this.DN_SOURCE);
        dest.writeList(this.detailModels);
    }

    public void setDetailModels(List<DNDetailModel> detailModels) {
        this.detailModels = detailModels;
    }

    public String getAGENT_DN_NO() {
        return this.AGENT_DN_NO;
    }

    public void setAGENT_DN_NO(String AGENT_DN_NO) {
        this.AGENT_DN_NO = AGENT_DN_NO;
    }

    public Date getDN_DATE() {
        return this.DN_DATE;
    }

    public void setDN_DATE(Date DN_DATE) {
        this.DN_DATE = DN_DATE;
    }

    public int getDN_STATUS() {
        return this.DN_STATUS;
    }

    public void setDN_STATUS(int DN_STATUS) {
        this.DN_STATUS = DN_STATUS;
    }

    public String getLEVEL_1_AGENT_NO() {
        return this.LEVEL_1_AGENT_NO;
    }

    public void setLEVEL_1_AGENT_NO(String LEVEL_1_AGENT_NO) {
        this.LEVEL_1_AGENT_NO = LEVEL_1_AGENT_NO;
    }

    public String getLEVEL_1_AGENT_NAME() {
        return this.LEVEL_1_AGENT_NAME;
    }

    public void setLEVEL_1_AGENT_NAME(String LEVEL_1_AGENT_NAME) {
        this.LEVEL_1_AGENT_NAME = LEVEL_1_AGENT_NAME;
    }

    public String getLEVEL_2_AGENT_NO() {
        return this.LEVEL_2_AGENT_NO;
    }

    public void setLEVEL_2_AGENT_NO(String LEVEL_2_AGENT_NO) {
        this.LEVEL_2_AGENT_NO = LEVEL_2_AGENT_NO;
    }

    public String getLEVEL_2_AGENT_NAME() {
        return this.LEVEL_2_AGENT_NAME;
    }

    public void setLEVEL_2_AGENT_NAME(String LEVEL_2_AGENT_NAME) {
        this.LEVEL_2_AGENT_NAME = LEVEL_2_AGENT_NAME;
    }

    public String getCUSTOM_NO() {
        return this.CUSTOM_NO;
    }

    public void setCUSTOM_NO(String CUSTOM_NO) {
        this.CUSTOM_NO = CUSTOM_NO;
    }

    public String getCUSTOM_NAME() {
        return this.CUSTOM_NAME;
    }

    public void setCUSTOM_NAME(String CUSTOM_NAME) {
        this.CUSTOM_NAME = CUSTOM_NAME;
    }

    public Integer getDN_QTY() {
        return this.DN_QTY;
    }

    public void setDN_QTY(Integer DN_QTY) {
        this.DN_QTY = DN_QTY;
    }

    public String getUPDATE_USER() {
        return this.UPDATE_USER;
    }

    public void setUPDATE_USER(String UPDATE_USER) {
        this.UPDATE_USER = UPDATE_USER;
    }

    public Date getUPDATE_DATE() {
        return this.UPDATE_DATE;
    }

    public void setUPDATE_DATE(Date UPDATE_DATE) {
        this.UPDATE_DATE = UPDATE_DATE;
    }

    public String getDN_SOURCE() {
        return this.DN_SOURCE;
    }

    public void setDN_SOURCE(String DN_SOURCE) {
        this.DN_SOURCE = DN_SOURCE;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1155724824)
    public List<DNDetailModel> getDetailModels() {
        if (detailModels == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            DNDetailModelDao targetDao = daoSession.getDNDetailModelDao();
            List<DNDetailModel> detailModelsNew = targetDao
                    ._queryDNModel_DetailModels(AGENT_DN_NO);
            synchronized (this) {
                if (detailModels == null) {
                    detailModels = detailModelsNew;
                }
            }
        }
        return detailModels;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1750110442)
    public synchronized void resetDetailModels() {
        detailModels = null;
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
    @Generated(hash = 744563246)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getDNModelDao() : null;
    }

    public DNModel() {
    }

    protected DNModel(Parcel in) {
        this.AGENT_DN_NO = in.readString();
        long tmpDN_DATE = in.readLong();
        this.DN_DATE = tmpDN_DATE == -1 ? null : new Date(tmpDN_DATE);
        this.DN_STATUS = in.readInt();
        this.LEVEL_1_AGENT_NO = in.readString();
        this.LEVEL_1_AGENT_NAME = in.readString();
        this.LEVEL_2_AGENT_NO = in.readString();
        this.LEVEL_2_AGENT_NAME = in.readString();
        this.CUSTOM_NO = in.readString();
        this.CUSTOM_NAME = in.readString();
        this.DN_QTY = (Integer) in.readValue(Integer.class.getClassLoader());
        this.UPDATE_USER = in.readString();
        long tmpUPDATE_DATE = in.readLong();
        this.UPDATE_DATE = tmpUPDATE_DATE == -1 ? null : new Date(tmpUPDATE_DATE);
        this.DN_SOURCE = in.readString();
        this.detailModels = new ArrayList<DNDetailModel>();
        in.readList(this.detailModels, DNDetailModel.class.getClassLoader());
    }

    @Generated(hash = 1502020661)
    public DNModel(String AGENT_DN_NO, Date DN_DATE, int DN_STATUS,
            String LEVEL_1_AGENT_NO, String LEVEL_1_AGENT_NAME,
            String LEVEL_2_AGENT_NO, String LEVEL_2_AGENT_NAME, String CUSTOM_NO,
            String CUSTOM_NAME, Integer DN_QTY, String UPDATE_USER,
            Date UPDATE_DATE, String DN_SOURCE) {
        this.AGENT_DN_NO = AGENT_DN_NO;
        this.DN_DATE = DN_DATE;
        this.DN_STATUS = DN_STATUS;
        this.LEVEL_1_AGENT_NO = LEVEL_1_AGENT_NO;
        this.LEVEL_1_AGENT_NAME = LEVEL_1_AGENT_NAME;
        this.LEVEL_2_AGENT_NO = LEVEL_2_AGENT_NO;
        this.LEVEL_2_AGENT_NAME = LEVEL_2_AGENT_NAME;
        this.CUSTOM_NO = CUSTOM_NO;
        this.CUSTOM_NAME = CUSTOM_NAME;
        this.DN_QTY = DN_QTY;
        this.UPDATE_USER = UPDATE_USER;
        this.UPDATE_DATE = UPDATE_DATE;
        this.DN_SOURCE = DN_SOURCE;
    }

    public static final Creator<DNModel> CREATOR = new Creator<DNModel>() {
        @Override
        public DNModel createFromParcel(Parcel source) {
            return new DNModel(source);
        }

        @Override
        public DNModel[] newArray(int size) {
            return new DNModel[size];
        }
    };
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 718579950)
    private transient DNModelDao myDao;
}
