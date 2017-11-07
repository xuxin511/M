package com.xx.chinetek.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.xx.chinetek.model.DN.DNModel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DNMODEL".
*/
public class DNModelDao extends AbstractDao<DNModel, Void> {

    public static final String TABLENAME = "DNMODEL";

    /**
     * Properties of entity DNModel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property AGENT_DN_NO = new Property(0, String.class, "AGENT_DN_NO", false, "AGENT__DN__NO");
        public final static Property DN_DATE = new Property(1, java.util.Date.class, "DN_DATE", false, "DN__DATE");
        public final static Property DN_STATUS = new Property(2, String.class, "DN_STATUS", false, "DN__STATUS");
        public final static Property LEVEL_1_AGENT_NO = new Property(3, String.class, "LEVEL_1_AGENT_NO", false, "LEVEL_1__AGENT__NO");
        public final static Property LEVEL_1_AGENT_NAME = new Property(4, String.class, "LEVEL_1_AGENT_NAME", false, "LEVEL_1__AGENT__NAME");
        public final static Property LEVEL_2_AGENT_NO = new Property(5, String.class, "LEVEL_2_AGENT_NO", false, "LEVEL_2__AGENT__NO");
        public final static Property LEVEL_2_AGENT_NAME = new Property(6, String.class, "LEVEL_2_AGENT_NAME", false, "LEVEL_2__AGENT__NAME");
        public final static Property CUSTOM_NO = new Property(7, String.class, "CUSTOM_NO", false, "CUSTOM__NO");
        public final static Property CUSTOM_NAME = new Property(8, String.class, "CUSTOM_NAME", false, "CUSTOM__NAME");
        public final static Property DN_QTY = new Property(9, Float.class, "DN_QTY", false, "DN__QTY");
        public final static Property UPDATE_USER = new Property(10, String.class, "UPDATE_USER", false, "UPDATE__USER");
        public final static Property UPDATE_DATE = new Property(11, java.util.Date.class, "UPDATE_DATE", false, "UPDATE__DATE");
        public final static Property DN_SOURCE = new Property(12, String.class, "DN_SOURCE", false, "DN__SOURCE");
    }


    public DNModelDao(DaoConfig config) {
        super(config);
    }
    
    public DNModelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DNMODEL\" (" + //
                "\"AGENT__DN__NO\" TEXT UNIQUE ," + // 0: AGENT_DN_NO
                "\"DN__DATE\" INTEGER," + // 1: DN_DATE
                "\"DN__STATUS\" TEXT," + // 2: DN_STATUS
                "\"LEVEL_1__AGENT__NO\" TEXT," + // 3: LEVEL_1_AGENT_NO
                "\"LEVEL_1__AGENT__NAME\" TEXT," + // 4: LEVEL_1_AGENT_NAME
                "\"LEVEL_2__AGENT__NO\" TEXT," + // 5: LEVEL_2_AGENT_NO
                "\"LEVEL_2__AGENT__NAME\" TEXT," + // 6: LEVEL_2_AGENT_NAME
                "\"CUSTOM__NO\" TEXT," + // 7: CUSTOM_NO
                "\"CUSTOM__NAME\" TEXT," + // 8: CUSTOM_NAME
                "\"DN__QTY\" REAL," + // 9: DN_QTY
                "\"UPDATE__USER\" TEXT," + // 10: UPDATE_USER
                "\"UPDATE__DATE\" INTEGER," + // 11: UPDATE_DATE
                "\"DN__SOURCE\" TEXT);"); // 12: DN_SOURCE
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DNMODEL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DNModel entity) {
        stmt.clearBindings();
 
        String AGENT_DN_NO = entity.getAGENT_DN_NO();
        if (AGENT_DN_NO != null) {
            stmt.bindString(1, AGENT_DN_NO);
        }
 
        java.util.Date DN_DATE = entity.getDN_DATE();
        if (DN_DATE != null) {
            stmt.bindLong(2, DN_DATE.getTime());
        }
 
        String DN_STATUS = entity.getDN_STATUS();
        if (DN_STATUS != null) {
            stmt.bindString(3, DN_STATUS);
        }
 
        String LEVEL_1_AGENT_NO = entity.getLEVEL_1_AGENT_NO();
        if (LEVEL_1_AGENT_NO != null) {
            stmt.bindString(4, LEVEL_1_AGENT_NO);
        }
 
        String LEVEL_1_AGENT_NAME = entity.getLEVEL_1_AGENT_NAME();
        if (LEVEL_1_AGENT_NAME != null) {
            stmt.bindString(5, LEVEL_1_AGENT_NAME);
        }
 
        String LEVEL_2_AGENT_NO = entity.getLEVEL_2_AGENT_NO();
        if (LEVEL_2_AGENT_NO != null) {
            stmt.bindString(6, LEVEL_2_AGENT_NO);
        }
 
        String LEVEL_2_AGENT_NAME = entity.getLEVEL_2_AGENT_NAME();
        if (LEVEL_2_AGENT_NAME != null) {
            stmt.bindString(7, LEVEL_2_AGENT_NAME);
        }
 
        String CUSTOM_NO = entity.getCUSTOM_NO();
        if (CUSTOM_NO != null) {
            stmt.bindString(8, CUSTOM_NO);
        }
 
        String CUSTOM_NAME = entity.getCUSTOM_NAME();
        if (CUSTOM_NAME != null) {
            stmt.bindString(9, CUSTOM_NAME);
        }
 
        Float DN_QTY = entity.getDN_QTY();
        if (DN_QTY != null) {
            stmt.bindDouble(10, DN_QTY);
        }
 
        String UPDATE_USER = entity.getUPDATE_USER();
        if (UPDATE_USER != null) {
            stmt.bindString(11, UPDATE_USER);
        }
 
        java.util.Date UPDATE_DATE = entity.getUPDATE_DATE();
        if (UPDATE_DATE != null) {
            stmt.bindLong(12, UPDATE_DATE.getTime());
        }
 
        String DN_SOURCE = entity.getDN_SOURCE();
        if (DN_SOURCE != null) {
            stmt.bindString(13, DN_SOURCE);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DNModel entity) {
        stmt.clearBindings();
 
        String AGENT_DN_NO = entity.getAGENT_DN_NO();
        if (AGENT_DN_NO != null) {
            stmt.bindString(1, AGENT_DN_NO);
        }
 
        java.util.Date DN_DATE = entity.getDN_DATE();
        if (DN_DATE != null) {
            stmt.bindLong(2, DN_DATE.getTime());
        }
 
        String DN_STATUS = entity.getDN_STATUS();
        if (DN_STATUS != null) {
            stmt.bindString(3, DN_STATUS);
        }
 
        String LEVEL_1_AGENT_NO = entity.getLEVEL_1_AGENT_NO();
        if (LEVEL_1_AGENT_NO != null) {
            stmt.bindString(4, LEVEL_1_AGENT_NO);
        }
 
        String LEVEL_1_AGENT_NAME = entity.getLEVEL_1_AGENT_NAME();
        if (LEVEL_1_AGENT_NAME != null) {
            stmt.bindString(5, LEVEL_1_AGENT_NAME);
        }
 
        String LEVEL_2_AGENT_NO = entity.getLEVEL_2_AGENT_NO();
        if (LEVEL_2_AGENT_NO != null) {
            stmt.bindString(6, LEVEL_2_AGENT_NO);
        }
 
        String LEVEL_2_AGENT_NAME = entity.getLEVEL_2_AGENT_NAME();
        if (LEVEL_2_AGENT_NAME != null) {
            stmt.bindString(7, LEVEL_2_AGENT_NAME);
        }
 
        String CUSTOM_NO = entity.getCUSTOM_NO();
        if (CUSTOM_NO != null) {
            stmt.bindString(8, CUSTOM_NO);
        }
 
        String CUSTOM_NAME = entity.getCUSTOM_NAME();
        if (CUSTOM_NAME != null) {
            stmt.bindString(9, CUSTOM_NAME);
        }
 
        Float DN_QTY = entity.getDN_QTY();
        if (DN_QTY != null) {
            stmt.bindDouble(10, DN_QTY);
        }
 
        String UPDATE_USER = entity.getUPDATE_USER();
        if (UPDATE_USER != null) {
            stmt.bindString(11, UPDATE_USER);
        }
 
        java.util.Date UPDATE_DATE = entity.getUPDATE_DATE();
        if (UPDATE_DATE != null) {
            stmt.bindLong(12, UPDATE_DATE.getTime());
        }
 
        String DN_SOURCE = entity.getDN_SOURCE();
        if (DN_SOURCE != null) {
            stmt.bindString(13, DN_SOURCE);
        }
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public DNModel readEntity(Cursor cursor, int offset) {
        DNModel entity = new DNModel( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // AGENT_DN_NO
            cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)), // DN_DATE
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // DN_STATUS
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // LEVEL_1_AGENT_NO
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // LEVEL_1_AGENT_NAME
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // LEVEL_2_AGENT_NO
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // LEVEL_2_AGENT_NAME
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // CUSTOM_NO
            cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8), // CUSTOM_NAME
            cursor.isNull(offset + 9) ? null : cursor.getFloat(offset + 9), // DN_QTY
            cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10), // UPDATE_USER
            cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)), // UPDATE_DATE
            cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12) // DN_SOURCE
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DNModel entity, int offset) {
        entity.setAGENT_DN_NO(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setDN_DATE(cursor.isNull(offset + 1) ? null : new java.util.Date(cursor.getLong(offset + 1)));
        entity.setDN_STATUS(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setLEVEL_1_AGENT_NO(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setLEVEL_1_AGENT_NAME(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setLEVEL_2_AGENT_NO(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setLEVEL_2_AGENT_NAME(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setCUSTOM_NO(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setCUSTOM_NAME(cursor.isNull(offset + 8) ? null : cursor.getString(offset + 8));
        entity.setDN_QTY(cursor.isNull(offset + 9) ? null : cursor.getFloat(offset + 9));
        entity.setUPDATE_USER(cursor.isNull(offset + 10) ? null : cursor.getString(offset + 10));
        entity.setUPDATE_DATE(cursor.isNull(offset + 11) ? null : new java.util.Date(cursor.getLong(offset + 11)));
        entity.setDN_SOURCE(cursor.isNull(offset + 12) ? null : cursor.getString(offset + 12));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(DNModel entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(DNModel entity) {
        return null;
    }

    @Override
    public boolean hasKey(DNModel entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
