
package com.tools.util;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class TestResultProvider extends ContentProvider
{
    public static final String TEST_RESULT_TABLE = "testresult";

    // Constants
    public static final String AUTHORITY = "com.tools.providers.factorytest";
    public static final String ID = "_id";
    public static final String TEST_ID = "test_id";
    public static final String TIME = "time";
    public static final String CASE_NAME = "name";
    public static final String STATUS = "status";
    public static final String TEST_TYPE = "test_type";
    public static final String BUILD_VERSION = "build_version";
    public static final String FAIL_NUM = "fail_num";
    public static final String PASS_NUM = "pass_num";

    public static final Uri TEST_RESULT_URI = Uri.parse("content://" + AUTHORITY + "/"
            + TEST_RESULT_TABLE);

    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int CASES = 1;
    private static final int CASE = 2;
    private DBOpenHelper helper;

    static
    {
        MATCHER.addURI(AUTHORITY, TEST_RESULT_TABLE, CASES);
        MATCHER.addURI(AUTHORITY, TEST_RESULT_TABLE + "/#", CASE);
    }

    @Override
    public boolean onCreate()
    {
        helper = new DBOpenHelper(this.getContext());

        return true;
    }

    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder)
    {
        SQLiteDatabase db = helper.getWritableDatabase();

        switch (MATCHER.match(uri))
        {
            case CASES:
                return db.query(TEST_RESULT_TABLE, projection, selection, selectionArgs, null,
                        null, sortOrder);

            case CASE:
                long id = ContentUris.parseId(uri);
                String where = ID + "=" + id;
                if (selection != null && !"".equals(selection.trim()))
                {
                    where = where + " and " + selection;
                }
                return db.query(TEST_RESULT_TABLE, projection, where, selectionArgs, null, null,
                        sortOrder);

            default:
                throw new IllegalArgumentException("Unknown Uri:" + uri);
        }
    }

    public String getType(Uri uri)
    {
        switch (MATCHER.match(uri))
        {
            case CASES:
                return "vnd.android.cursor.dir/com.tools.factorytest";

            case CASE:
                return "vnd.android.cursor.item/com.tools.factorytest";

            default:
                throw new IllegalArgumentException("Unknown Uri:" + uri);
        }
    }

    public Uri insert(Uri uri, ContentValues values)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        // Log.i("*********", MATCHER.match(uri)+"");
        switch (MATCHER.match(uri))
        {
            case CASES:
                long rowid = db.insert(TEST_RESULT_TABLE, ID, values);
                if (rowid > 0) {
                    Uri insertedUri = ContentUris.withAppendedId(uri, rowid);
                    getContext().getContentResolver().notifyChange(insertedUri, null);
                    helper.close();
                    return insertedUri;
                }
                break;
            default:
                ;
        }
        helper.close();
        return null;
    }

    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        int num = 0;

        switch (MATCHER.match(uri))
        {
            case CASES:
                num = db.delete(TEST_RESULT_TABLE, selection, selectionArgs);
                break;
            case CASE:
                long id = ContentUris.parseId(uri);
                String where = ID + "=" + id;
                if (selection != null && !"".equals(selection.trim()))
                {
                    where = where + " and " + selection;
                }
                num = db.delete(TEST_RESULT_TABLE, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        helper.close();
        return num;
    }

    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs)
    {
        SQLiteDatabase db = helper.getWritableDatabase();
        int num = 0;

        switch (MATCHER.match(uri))
        {
            case CASES:
                num = db.update(TEST_RESULT_TABLE, values, selection, selectionArgs);
                break;
            case CASE:
                long id = ContentUris.parseId(uri);
                String where = ID + "=" + id;
                if (selection != null && !"".equals(selection.trim()))
                {
                    where = where + " and " + selection;
                }
                num = db.update(TEST_RESULT_TABLE, values, where, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown Uri:" + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        helper.close();
        return num;
    }
}
