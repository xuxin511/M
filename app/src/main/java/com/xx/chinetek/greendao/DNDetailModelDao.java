package com.xx.chinetek.greendao;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import com.xx.chinetek.model.DN.DNDetailModel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "DNDETAIL_MODEL".
*/
public class DNDetailModelDao extends AbstractDao<DNDetailModel, Void> {

    public static final String TABLENAME = "DNDETAIL_MODEL";

    /**
     * Properties of entity DNDetailModel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property AGENT_DN_NO = new Property(0, String.class, "AGENT_DN_NO", false, "AGENT__DN__NO");
        public final static Property LINE_NO = new Property(1, Integer.class, "LINE_NO", false, "LINE__NO");
        public final static Property ITEM_NO = new Property(2, String.class, "ITEM_NO", false, "ITEM__NO");
        public final static Property ITEM_NAME = new Property(3, String.class, "ITEM_NAME", false, "ITEM__NAME");
        public final static Property GOLFA_CODE = new Property(4, String.class, "GOLFA_CODE", false, "GOLFA__CODE");
        public final static Property DN_QTY = new Property(5, Integer.class, "DN_QTY", false, "DN__QTY");
        public final static Property DETAIL_STATUS = new Property(6, String.class, "DETAIL_STATUS", false, "DETAIL__STATUS");
        public final static Property OPER_DATE = new Property(7, java.util.Date.class, "OPER_DATE", false, "OPER__DATE");
        public final static Property SCAN_QTY = new Property(8, Integer.class, "SCAN_QTY", false, "SCAN__QTY");
        public final static Property STATUS = new Property(9, Integer.class, "STATUS", false, "STATUS");
        public final static Property Flag = new Property(10, Integer.class, "Flag", false, "FLAG");
    }

    private DaoSession daoSession;

    private Query<DNDetailModel> dNModel_DETAILSQuery;

    public DNDetailModelDao(DaoConfig config) {
        super(config);
    }
    
    public DNDetailModelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
        this.daoSession = daoSession;
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"DNDETAIL_MODEL\" (" + //
                "\"AGENT__DN__NO\" TEXT," + // 0: AGENT_DN_NO
                "\"LINE__NO\" INTEGER," + // 1: LINE_NO
                "\"ITEM__NO\" TEXT," + // 2: ITEM_NO
                "\"ITEM__NAME\" TEXT," + // 3: ITEM_NAME
                "\"GOLFA__CODE\" TEXT," + // 4: GOLFA_CODE
                "\"DN__QTY\" INTEGER," + // 5: DN_QTY
                "\"DETAIL__STATUS\" TEXT," + // 6: DETAIL_STATUS
                "\"OPER__DATE\" INTEGER," + // 7: OPER_DATE
                "\"SCAN__QTY\" INTEGER," + // 8: SCAN_QTY
                "\"STATUS\" INTEGER," + // 9: STATUS
                "\"FLAG\" INTEGER);"); // 10: Flag
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_DNDETAIL_MODEL_AGENT__DN__NO_LINE__NO ON \"DNDETAIL_MODEL\"" +
                " (\"AGENT__DN__NO\" ASC,\"LINE__NO\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"DNDETAIL_MODEL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DNDetailModel entity) {
        stmt.clearBindings();
 
        String AGENT_DN_NO = entity.getAGENT_DN_NO();
        if (AGENT_DN_NO != null) {
            stmt.bindString(1, AGENT_DN_NO);
        }
 
        Integer LINE_NO = entity.getLINE_NO();
        if (LINE_NO != null) {
            stmt.bindLong(2, LINE_NO);
        }
 
        String ITEM_NO = entity.getITEM_NO();
        if (ITEM_NO != null) {
            stmt.bindString(3, ITEM_NO);
        }
 
        String ITEM_NAME = entity.getITEM_NAME();
        if (ITEM_NAME != null) {
            stmt.bindString(4, ITEM_NAME);
        }
 
        String GOLFA_CODE = entity.getGOLFA_CODE();
        if (GOLFA_CODE != null) {
            stmt.bindString(5, GOLFA_CODE);
        }
 
        Integer DN_QTY = entity.getDN_QTY();
        if (DN_QTY != null) {
            stmt.bindLong(6, DN_QTY);
        }
 
        String DETAIL_STATUS = entity.getDETAIL_STATUS();
        if (DETAIL_STATUS != null) {
            stmt.bindString(7, DETAIL_STATUS);
        }
 
        java.util.Date OPER_DATE = entity.getOPER_DATE();
        if (OPER_DATE != null) {
            stmt.bindLong(8, OPER_DATE.getTime());
        }
 
        Integer SCAN_QTY = entity.getSCAN_QTY();
        if (SCAN_QTY != null) {
            stmt.bindLong(9, SCAN_QTY);
        }
 
        Integer STATUS = entity.getSTATUS();
        if (STATUS != null) {
            stmt.bindLong(10, STATUS);
        }
 
        Integer Flag = entity.getFlag();
        if (Flag != null) {
            stmt.bindLong(11, Flag);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DNDetailModel entity) {
        stmt.clearBindings();
 
        String AGENT_DN_NO = entity.getAGENT_DN_NO();
        if (AGENT_DN_NO != null) {
            stmt.bindString(1, AGENT_DN_NO);
        }
 
        Integer LINE_NO = entity.getLINE_NO();
        if (LINE_NO != null) {
            stmt.bindLong(2, LINE_NO);
        }
 
        String ITEM_NO = entity.getITEM_NO();
        if (ITEM_NO != null) {
            stmt.bindString(3, ITEM_NO);
        }
 
        String ITEM_NAME = entity.getITEM_NAME();
        if (ITEM_NAME != null) {
            stmt.bindString(4, ITEM_NAME);
        }
 
        String GOLFA_CODE = entity.getGOLFA_CODE();
        if (GOLFA_CODE != null) {
            stmt.bindString(5, GOLFA_CODE);
        }
 
        Integer DN_QTY = entity.getDN_QTY();
        if (DN_QTY != null) {
            stmt.bindLong(6, DN_QTY);
        }
 
        String DETAIL_STATUS = entity.getDETAIL_STATUS();
        if (DETAIL_STATUS != null) {
            stmt.bindString(7, DETAIL_STATUS);
        }
 
        java.util.Date OPER_DATE = entity.getOPER_DATE();
        if (OPER_DATE != null) {
            stmt.bindLong(8, OPER_DATE.getTime());
        }
 
        Integer SCAN_QTY = entity.getSCAN_QTY();
        if (SCAN_QTY != null) {
            stmt.bindLong(9, SCAN_QTY);
        }
 
        Integer STATUS = entity.getSTATUS();
        if (STATUS != null) {
            stmt.bindLong(10, STATUS);
        }
 
        Integer Flag = entity.getFlag();
        if (Flag != null) {
            stmt.bindLong(11, Flag);
        }
    }

    @Override
    protected final void attachEntity(DNDetailModel entity) {
        super.attachEntity(entity);
        entity.__setDaoSession(daoSession);
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public DNDetailModel readEntity(Cursor cursor, int offset) {
        DNDetailModel entity = new DNDetailModel( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // AGENT_DN_NO
            cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1), // LINE_NO
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // ITEM_NO
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // ITEM_NAME
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // GOLFA_CODE
            cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5), // DN_QTY
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // DETAIL_STATUS
            cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)), // OPER_DATE
            cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8), // SCAN_QTY
            cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9), // STATUS
            cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10) // Flag
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DNDetailModel entity, int offset) {
        entity.setAGENT_DN_NO(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setLINE_NO(cursor.isNull(offset + 1) ? null : cursor.getInt(offset + 1));
        entity.setITEM_NO(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setITEM_NAME(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setGOLFA_CODE(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setDN_QTY(cursor.isNull(offset + 5) ? null : cursor.getInt(offset + 5));
        entity.setDETAIL_STATUS(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setOPER_DATE(cursor.isNull(offset + 7) ? null : new java.util.Date(cursor.getLong(offset + 7)));
        entity.setSCAN_QTY(cursor.isNull(offset + 8) ? null : cursor.getInt(offset + 8));
        entity.setSTATUS(cursor.isNull(offset + 9) ? null : cursor.getInt(offset + 9));
        entity.setFlag(cursor.isNull(offset + 10) ? null : cursor.getInt(offset + 10));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(DNDetailModel entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(DNDetailModel entity) {
        return null;
    }

    @Override
    public boolean hasKey(DNDetailModel entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "DETAILS" to-many relationship of DNModel. */
    public List<DNDetailModel> _queryDNModel_DETAILS(String AGENT_DN_NO) {
        synchronized (this) {
            if (dNModel_DETAILSQuery == null) {
                QueryBuilder<DNDetailModel> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.AGENT_DN_NO.eq(null));
                dNModel_DETAILSQuery = queryBuilder.build();
            }
        }
        Query<DNDetailModel> query = dNModel_DETAILSQuery.forCurrentThread();
        query.setParameter(0, AGENT_DN_NO);
        return query.list();
    }

}
