package com.xx.chinetek.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.xx.chinetek.model.Base.CustomModel;
import com.xx.chinetek.model.Base.MaterialModel;
import com.xx.chinetek.model.Base.SyncParaModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;

import com.xx.chinetek.greendao.CustomModelDao;
import com.xx.chinetek.greendao.MaterialModelDao;
import com.xx.chinetek.greendao.SyncParaModelDao;
import com.xx.chinetek.greendao.DNDetailModelDao;
import com.xx.chinetek.greendao.DNModelDao;
import com.xx.chinetek.greendao.DNScanModelDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig customModelDaoConfig;
    private final DaoConfig materialModelDaoConfig;
    private final DaoConfig syncParaModelDaoConfig;
    private final DaoConfig dNDetailModelDaoConfig;
    private final DaoConfig dNModelDaoConfig;
    private final DaoConfig dNScanModelDaoConfig;

    private final CustomModelDao customModelDao;
    private final MaterialModelDao materialModelDao;
    private final SyncParaModelDao syncParaModelDao;
    private final DNDetailModelDao dNDetailModelDao;
    private final DNModelDao dNModelDao;
    private final DNScanModelDao dNScanModelDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        customModelDaoConfig = daoConfigMap.get(CustomModelDao.class).clone();
        customModelDaoConfig.initIdentityScope(type);

        materialModelDaoConfig = daoConfigMap.get(MaterialModelDao.class).clone();
        materialModelDaoConfig.initIdentityScope(type);

        syncParaModelDaoConfig = daoConfigMap.get(SyncParaModelDao.class).clone();
        syncParaModelDaoConfig.initIdentityScope(type);

        dNDetailModelDaoConfig = daoConfigMap.get(DNDetailModelDao.class).clone();
        dNDetailModelDaoConfig.initIdentityScope(type);

        dNModelDaoConfig = daoConfigMap.get(DNModelDao.class).clone();
        dNModelDaoConfig.initIdentityScope(type);

        dNScanModelDaoConfig = daoConfigMap.get(DNScanModelDao.class).clone();
        dNScanModelDaoConfig.initIdentityScope(type);

        customModelDao = new CustomModelDao(customModelDaoConfig, this);
        materialModelDao = new MaterialModelDao(materialModelDaoConfig, this);
        syncParaModelDao = new SyncParaModelDao(syncParaModelDaoConfig, this);
        dNDetailModelDao = new DNDetailModelDao(dNDetailModelDaoConfig, this);
        dNModelDao = new DNModelDao(dNModelDaoConfig, this);
        dNScanModelDao = new DNScanModelDao(dNScanModelDaoConfig, this);

        registerDao(CustomModel.class, customModelDao);
        registerDao(MaterialModel.class, materialModelDao);
        registerDao(SyncParaModel.class, syncParaModelDao);
        registerDao(DNDetailModel.class, dNDetailModelDao);
        registerDao(DNModel.class, dNModelDao);
        registerDao(DNScanModel.class, dNScanModelDao);
    }
    
    public void clear() {
        customModelDaoConfig.clearIdentityScope();
        materialModelDaoConfig.clearIdentityScope();
        syncParaModelDaoConfig.clearIdentityScope();
        dNDetailModelDaoConfig.clearIdentityScope();
        dNModelDaoConfig.clearIdentityScope();
        dNScanModelDaoConfig.clearIdentityScope();
    }

    public CustomModelDao getCustomModelDao() {
        return customModelDao;
    }

    public MaterialModelDao getMaterialModelDao() {
        return materialModelDao;
    }

    public SyncParaModelDao getSyncParaModelDao() {
        return syncParaModelDao;
    }

    public DNDetailModelDao getDNDetailModelDao() {
        return dNDetailModelDao;
    }

    public DNModelDao getDNModelDao() {
        return dNModelDao;
    }

    public DNScanModelDao getDNScanModelDao() {
        return dNScanModelDao;
    }

}