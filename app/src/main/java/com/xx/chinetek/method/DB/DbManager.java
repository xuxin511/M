package com.xx.chinetek.method.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xx.chinetek.greendao.DaoMaster;
import com.xx.chinetek.greendao.DaoSession;

/**
 * Created by GHOST on 2017/11/6.
 */

public class DbManager {
    // 是否加密
    public static boolean ENCRYPTED = true;

    public static String DB_NAME = "mitsubshi.db";
    private static DbManager mDbManager;
    private static DaoMaster.DevOpenHelper mDevOpenHelper;
    public static DaoMaster mDaoMaster;
    public static DaoSession mDaoSession;
    public static String dbpassword="MitshisubQR";

    private Context mContext;

    private DbManager(Context context) {
        this.mContext = context;
        // 初始化数据库信息
        mDevOpenHelper = new DaoMaster.DevOpenHelper(context, DB_NAME);
//        getDaoMaster(context);
//        getDaoSession(context);
    }

    public static DbManager getInstance(Context context) {
        if (null == mDbManager) {
            synchronized (DbManager.class) {
                if (null == mDbManager) {
                    mDbManager = new DbManager(context);
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
            synchronized (DbManager.class) {
                if (null == mDaoMaster) {
                    DbOpenHelper helper = new DbOpenHelper(context,DB_NAME,null);
                    if (ENCRYPTED) {//加密
                        mDaoMaster= new DaoMaster(helper.getEncryptedReadableDb(dbpassword));
                    } else {
                        mDaoMaster =new DaoMaster(helper.getWritableDatabase());
                    }
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
            synchronized (DbManager.class) {
                mDaoSession = getDaoMaster(context).newSession();
            }
        }

        return mDaoSession;
    }
}