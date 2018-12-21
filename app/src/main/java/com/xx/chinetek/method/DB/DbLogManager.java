package com.xx.chinetek.method.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xx.chinetek.greendao.DaoMaster;
import com.xx.chinetek.greendao.DaoSession;

/**
 * Created by GHOST on 2017/11/6.
 */

public class DbLogManager {

    public static String DB_NAME = "mitsubshiLog.db";
    private static DbLogManager mDbManager;
    private static DaoMaster.DevOpenHelper mDevOpenHelper;
    public static DaoMaster mDaoMaster;
    public static DaoSession mDaoSession;

    private Context mContext;

    private DbLogManager(Context context) {
        this.mContext = context;
        // 初始化数据库信息
        mDevOpenHelper = new DaoMaster.DevOpenHelper(context, DB_NAME);
//        getDaoMaster(context);
//        getDaoSession(context);
    }

    public static DbLogManager getInstance(Context context) {
        if (null == mDbManager) {
            synchronized (DbLogManager.class) {
                if (null == mDbManager) {
                    mDbManager = new DbLogManager(context);
                }
            }
        }
        return mDbManager;
    }

    /**
     * 获取可读数据库
     *
     * @param context
     * @return
     */
    public static SQLiteDatabase getReadableDatabase(Context context) {
        if (null == mDevOpenHelper) {
            getInstance(context);
        }
        return mDevOpenHelper.getReadableDatabase();
    }

    /**
     * 获取可写数据库
     *
     * @param context
     * @return
     */
    public static SQLiteDatabase getWritableDatabase(Context context) {
        if (null == mDevOpenHelper) {
            getInstance(context);
        }
        return mDevOpenHelper.getWritableDatabase();
    }

    /**
     * 获取DaoMaster
     *
     * 判断是否存在数据库，如果没有则创建数据库
     * @return
     */
    public static DaoMaster getDaoMaster(Context context) {
        if (null == mDaoMaster) {
            synchronized (DbLogManager.class) {
                if (null == mDaoMaster) {
                    DbOpenHelper helper = new DbOpenHelper(context,DB_NAME,null);
                        mDaoMaster =new DaoMaster(helper.getWritableDatabase());

                }
            }
        }
        return mDaoMaster;
    }

    /**
     * 获取DaoSession
     *
     * @param context
     * @return
     */
    public static DaoSession getDaoSession(Context context) {
        if (null == mDaoSession) {
            synchronized (DbLogManager.class) {
                mDaoSession = getDaoMaster(context).newSession();
            }
        }

        return mDaoSession;
    }
}