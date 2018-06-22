package com.xx.chinetek.method.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.xx.chinetek.greendao.CustomModelDao;
import com.xx.chinetek.greendao.DNDetailModelDao;
import com.xx.chinetek.greendao.DNModelDao;
import com.xx.chinetek.greendao.DNScanModelDao;
import com.xx.chinetek.greendao.DaoMaster;
import com.xx.chinetek.greendao.MaterialModelDao;
import com.xx.chinetek.greendao.SyncParaModelDao;
import com.xx.chinetek.model.DN.DNModel;

import org.greenrobot.greendao.database.Database;

/**
 * Created by GHOST on 2017/11/6.
 */

public class DbOpenHelper extends DaoMaster.OpenHelper {
    public DbOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    /**
     * 数据库升级
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
            if(newVersion>oldVersion){
                switch (newVersion){
                    case 2:
                        db.execSQL("ALTER TABLE MATERIAL_MODEL ADD COLUMN SPARTNAME Text; ");
                        db.execSQL("ALTER TABLE MATERIAL_MODEL ADD COLUMN ACTION__CODE Text; ");
                        break;
                }
            }
    }

}
