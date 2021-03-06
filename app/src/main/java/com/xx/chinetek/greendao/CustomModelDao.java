package com.xx.chinetek.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.xx.chinetek.model.Base.CustomModel;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CUSTOM_MODEL".
*/
public class CustomModelDao extends AbstractDao<CustomModel, Void> {

    public static final String TABLENAME = "CUSTOM_MODEL";

    /**
     * Properties of entity CustomModel.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property CUSTOMER = new Property(0, String.class, "CUSTOMER", false, "CUSTOMER");
        public final static Property NAME = new Property(1, String.class, "NAME", false, "NAME");
        public final static Property SALES_ORGANIZATION = new Property(2, String.class, "SALES_ORGANIZATION", false, "SALES__ORGANIZATION");
        public final static Property DISTRIBUTION_CHANNEL = new Property(3, String.class, "DISTRIBUTION_CHANNEL", false, "DISTRIBUTION__CHANNEL");
        public final static Property DIVISION = new Property(4, String.class, "DIVISION", false, "DIVISION");
        public final static Property PARTNER_FUNCTION = new Property(5, String.class, "PARTNER_FUNCTION", false, "PARTNER__FUNCTION");
        public final static Property PARTNER_COUNTER = new Property(6, String.class, "PARTNER_COUNTER", false, "PARTNER__COUNTER");
        public final static Property CUST_NO_OF_BUSINESS_PARTNER = new Property(7, String.class, "CUST_NO_OF_BUSINESS_PARTNER", false, "CUST__NO__OF__BUSINESS__PARTNER");
    }


    public CustomModelDao(DaoConfig config) {
        super(config);
    }
    
    public CustomModelDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CUSTOM_MODEL\" (" + //
                "\"CUSTOMER\" TEXT," + // 0: CUSTOMER
                "\"NAME\" TEXT," + // 1: NAME
                "\"SALES__ORGANIZATION\" TEXT," + // 2: SALES_ORGANIZATION
                "\"DISTRIBUTION__CHANNEL\" TEXT," + // 3: DISTRIBUTION_CHANNEL
                "\"DIVISION\" TEXT," + // 4: DIVISION
                "\"PARTNER__FUNCTION\" TEXT," + // 5: PARTNER_FUNCTION
                "\"PARTNER__COUNTER\" TEXT," + // 6: PARTNER_COUNTER
                "\"CUST__NO__OF__BUSINESS__PARTNER\" TEXT);"); // 7: CUST_NO_OF_BUSINESS_PARTNER
        // Add Indexes
        db.execSQL("CREATE UNIQUE INDEX " + constraint + "IDX_CUSTOM_MODEL_CUSTOMER_SALES__ORGANIZATION_DISTRIBUTION__CHANNEL_DIVISION_PARTNER__FUNCTION_PARTNER__COUNTER ON \"CUSTOM_MODEL\"" +
                " (\"CUSTOMER\" ASC,\"SALES__ORGANIZATION\" ASC,\"DISTRIBUTION__CHANNEL\" ASC,\"DIVISION\" ASC,\"PARTNER__FUNCTION\" ASC,\"PARTNER__COUNTER\" ASC);");
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CUSTOM_MODEL\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, CustomModel entity) {
        stmt.clearBindings();
 
        String CUSTOMER = entity.getCUSTOMER();
        if (CUSTOMER != null) {
            stmt.bindString(1, CUSTOMER);
        }
 
        String NAME = entity.getNAME();
        if (NAME != null) {
            stmt.bindString(2, NAME);
        }
 
        String SALES_ORGANIZATION = entity.getSALES_ORGANIZATION();
        if (SALES_ORGANIZATION != null) {
            stmt.bindString(3, SALES_ORGANIZATION);
        }
 
        String DISTRIBUTION_CHANNEL = entity.getDISTRIBUTION_CHANNEL();
        if (DISTRIBUTION_CHANNEL != null) {
            stmt.bindString(4, DISTRIBUTION_CHANNEL);
        }
 
        String DIVISION = entity.getDIVISION();
        if (DIVISION != null) {
            stmt.bindString(5, DIVISION);
        }
 
        String PARTNER_FUNCTION = entity.getPARTNER_FUNCTION();
        if (PARTNER_FUNCTION != null) {
            stmt.bindString(6, PARTNER_FUNCTION);
        }
 
        String PARTNER_COUNTER = entity.getPARTNER_COUNTER();
        if (PARTNER_COUNTER != null) {
            stmt.bindString(7, PARTNER_COUNTER);
        }
 
        String CUST_NO_OF_BUSINESS_PARTNER = entity.getCUST_NO_OF_BUSINESS_PARTNER();
        if (CUST_NO_OF_BUSINESS_PARTNER != null) {
            stmt.bindString(8, CUST_NO_OF_BUSINESS_PARTNER);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, CustomModel entity) {
        stmt.clearBindings();
 
        String CUSTOMER = entity.getCUSTOMER();
        if (CUSTOMER != null) {
            stmt.bindString(1, CUSTOMER);
        }
 
        String NAME = entity.getNAME();
        if (NAME != null) {
            stmt.bindString(2, NAME);
        }
 
        String SALES_ORGANIZATION = entity.getSALES_ORGANIZATION();
        if (SALES_ORGANIZATION != null) {
            stmt.bindString(3, SALES_ORGANIZATION);
        }
 
        String DISTRIBUTION_CHANNEL = entity.getDISTRIBUTION_CHANNEL();
        if (DISTRIBUTION_CHANNEL != null) {
            stmt.bindString(4, DISTRIBUTION_CHANNEL);
        }
 
        String DIVISION = entity.getDIVISION();
        if (DIVISION != null) {
            stmt.bindString(5, DIVISION);
        }
 
        String PARTNER_FUNCTION = entity.getPARTNER_FUNCTION();
        if (PARTNER_FUNCTION != null) {
            stmt.bindString(6, PARTNER_FUNCTION);
        }
 
        String PARTNER_COUNTER = entity.getPARTNER_COUNTER();
        if (PARTNER_COUNTER != null) {
            stmt.bindString(7, PARTNER_COUNTER);
        }
 
        String CUST_NO_OF_BUSINESS_PARTNER = entity.getCUST_NO_OF_BUSINESS_PARTNER();
        if (CUST_NO_OF_BUSINESS_PARTNER != null) {
            stmt.bindString(8, CUST_NO_OF_BUSINESS_PARTNER);
        }
    }

    @Override
    public Void readKey(Cursor cursor, int offset) {
        return null;
    }    

    @Override
    public CustomModel readEntity(Cursor cursor, int offset) {
        CustomModel entity = new CustomModel( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // CUSTOMER
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // NAME
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // SALES_ORGANIZATION
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // DISTRIBUTION_CHANNEL
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // DIVISION
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // PARTNER_FUNCTION
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // PARTNER_COUNTER
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // CUST_NO_OF_BUSINESS_PARTNER
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, CustomModel entity, int offset) {
        entity.setCUSTOMER(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setNAME(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setSALES_ORGANIZATION(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setDISTRIBUTION_CHANNEL(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setDIVISION(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setPARTNER_FUNCTION(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setPARTNER_COUNTER(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setCUST_NO_OF_BUSINESS_PARTNER(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    @Override
    protected final Void updateKeyAfterInsert(CustomModel entity, long rowId) {
        // Unsupported or missing PK type
        return null;
    }
    
    @Override
    public Void getKey(CustomModel entity) {
        return null;
    }

    @Override
    public boolean hasKey(CustomModel entity) {
        // TODO
        return false;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
