
package com.tools.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBOpenHelper extends SQLiteOpenHelper
{

    public DBOpenHelper(Context context, String name, int version) {
        super(context, name, null, version);
    }

    public DBOpenHelper(Context context)
    {
        this(context, "testresult.db", 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        FTLog.i(this, "onCreate()");
        createTable(db);
    }

    /**
     * used when database needs to be upgraded
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        FTLog.w(this, "Upgrade database from version " + oldVersion + " to version " + newVersion);
        db.execSQL("drop table " + TestResultProvider.TEST_RESULT_TABLE);
        createTable(db);
    }

    public void createTable(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + TestResultProvider.TEST_RESULT_TABLE + " (" +
                        TestResultProvider.ID + " integer primary key autoincrement, " +
                        TestResultProvider.CASE_NAME + " varchar(20)," +
                        TestResultProvider.STATUS + " integer, " +
                        TestResultProvider.TIME + " varchar(25), " +
                        TestResultProvider.TEST_TYPE + " varchar(20), " +
                        TestResultProvider.BUILD_VERSION + " varchar(80), " +
                        TestResultProvider.PASS_NUM + " integer, " +
                        TestResultProvider.FAIL_NUM + " integer" +
                        ")"
                );
    }
}
