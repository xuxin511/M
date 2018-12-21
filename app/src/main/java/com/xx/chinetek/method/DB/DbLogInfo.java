package com.xx.chinetek.method.DB;

import android.database.Cursor;
import android.text.TextUtils;

import com.xx.chinetek.chineteklib.util.function.CommonUtil;
import com.xx.chinetek.greendao.DNDetailModelDao;
import com.xx.chinetek.greendao.DNModelDao;
import com.xx.chinetek.greendao.DNScanModelDao;
import com.xx.chinetek.greendao.DaoSession;
import com.xx.chinetek.greendao.LogModelDao;
import com.xx.chinetek.model.Base.DNStatusEnum;
import com.xx.chinetek.model.Base.ParamaterModel;
import com.xx.chinetek.model.DBReturnModel;
import com.xx.chinetek.model.DN.DNDetailModel;
import com.xx.chinetek.model.DN.DNModel;
import com.xx.chinetek.model.DN.DNScanModel;
import com.xx.chinetek.model.DN.LogModel;
import com.xx.chinetek.model.QueryModel;

import org.greenrobot.greendao.async.AsyncSession;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by GHOST on 2017/11/7.
 */

public class DbLogInfo {

    public static DbLogInfo mSyncLog;
    private DaoSession daoSession=null;
    private LogModelDao logModelDao;
    private AsyncSession asyncSession;


    private DbLogInfo() {
        if(daoSession==null) {
            daoSession = DbLogManager.getDaoSession(new GreenDaoContext());
            logModelDao=daoSession.getLogModelDao();
            asyncSession= daoSession.startAsyncSession();
        }
    }

    public DaoSession getDaoSession(){
        return daoSession;
    }

    public static DbLogInfo getInstance() {
        if (null == mSyncLog) {
            synchronized (DbLogInfo.class) {
                if (null == mSyncLog) {
                    mSyncLog = new DbLogInfo();
                }
            }
        }
        return mSyncLog;
    }

   public void InsertLog(final ArrayList<LogModel> logModels) throws Exception{
           if(logModels!=null && logModels.size()!=0) {
               asyncSession.runInTx(new Runnable() {
                   @Override
                   public void run() {
                       logModelDao.insertOrReplaceInTx(logModels);
                       logModelDao.detachAll();
                   }
               });

           }
   }

    public void InsertLog(final LogModel logModel){
        try {
            if (logModel != null) {
                asyncSession.runInTx(new Runnable() {
                    @Override
                    public void run() {
                        logModelDao.insertOrReplaceInTx(logModel);
                        logModelDao.detachAll();
                    }
                });

            }
        }catch (Exception ex){

        }
    }
}

