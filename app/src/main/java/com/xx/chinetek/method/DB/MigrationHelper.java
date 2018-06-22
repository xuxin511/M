package com.xx.chinetek.method.DB;

import android.content.Context;

import com.xx.chinetek.model.Base.ParamaterModel;


import net.sqlcipher.database.SQLiteDatabase;


import java.io.File;
import java.io.IOException;


/**
 * Created by GHOST on 2018/6/19.
 */

public class MigrationHelper {

    public static void encrypt(Context context, String dbName,String passphrase) throws IOException {
        File baseFile =new File(ParamaterModel.DBDirectory);
        StringBuffer buffer = new StringBuffer();
        buffer.append(baseFile.getPath());
        buffer.append(File.separator);
        buffer.append(dbName);
        File originalFile =new File(buffer.toString());

        if (originalFile.exists()) {
            SQLiteDatabase.loadLibs(context, context.getFilesDir());
            File newFile =
                    File.createTempFile("sqlcipherutils", "tmp",
                            baseFile);
            SQLiteDatabase db =
                    SQLiteDatabase.openDatabase(originalFile.getAbsolutePath(),
                            "", null,
                            SQLiteDatabase.OPEN_READWRITE);

            db.rawExecSQL(String.format("ATTACH DATABASE '%s' AS encrypted KEY '%s';",
                    newFile.getAbsolutePath(), passphrase));
            db.rawExecSQL("SELECT sqlcipher_export('encrypted')");
            db.rawExecSQL("DETACH DATABASE encrypted;");

            int version = db.getVersion();

            db.close();

            db =
                    SQLiteDatabase.openDatabase(newFile.getAbsolutePath(),
                            passphrase, null,
                            SQLiteDatabase.OPEN_READWRITE);
            db.setVersion(version);
            db.close();

            originalFile.delete();
            newFile.renameTo(originalFile);
        }
    }

}
