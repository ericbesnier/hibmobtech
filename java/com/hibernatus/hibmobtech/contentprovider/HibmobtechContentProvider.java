package com.hibernatus.hibmobtech.contentprovider;

/**
 * Created by Eric on 13/01/2017.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.hibernatus.hibmobtech.BuildConfig;
import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory;

import static com.hibernatus.hibmobtech.HibmobtechApplication.AUTHORITY;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_TIME;


public class HibmobtechContentProvider extends ContentProvider {
    public static final String TAG = HibmobtechContentProvider.class.getSimpleName();
    private SQLiteBaseDaoFactory sqLiteBaseDaoFactory;
    private static final UriMatcher URI_MATCHER;

    // TABLE
    private static final int PICTURE_TABLE = 0;
    private static final int PICTURE_CACHE_TABLE = 1;
    private static final int SIGNATURE_TABLE = 2;
    private static final int SIGNATURE_CACHE_TABLE = 3;

    // row ID
    private static final int PICTURE_ID = 5;
    private static final int PICTURE_CACHE_ID = 6;
    private static final int SIGNATURE_ID = 7;
    private static final int SIGNATURE_CACHE_ID = 8;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, SQLiteBaseDaoFactory.TABLE_PICTURE, PICTURE_TABLE);
        URI_MATCHER.addURI(AUTHORITY, SQLiteBaseDaoFactory.TABLE_PICTURE + "/#", PICTURE_ID);
        URI_MATCHER.addURI(AUTHORITY, SQLiteBaseDaoFactory.TABLE_PICTURE_CACHE, PICTURE_CACHE_TABLE);
        URI_MATCHER.addURI(AUTHORITY, SQLiteBaseDaoFactory.TABLE_PICTURE_CACHE + "/#", PICTURE_CACHE_ID);
        URI_MATCHER.addURI(AUTHORITY, SQLiteBaseDaoFactory.TABLE_SIGNATURE, SIGNATURE_TABLE);
        URI_MATCHER.addURI(AUTHORITY, SQLiteBaseDaoFactory.TABLE_SIGNATURE + "/#", SIGNATURE_ID);
        URI_MATCHER.addURI(AUTHORITY, SQLiteBaseDaoFactory.TABLE_SIGNATURE_CACHE, SIGNATURE_CACHE_TABLE);
        URI_MATCHER.addURI(AUTHORITY, SQLiteBaseDaoFactory.TABLE_SIGNATURE_CACHE + "/#", SIGNATURE_CACHE_ID);
    }

    public static Uri urlForPicture() {
        return Uri.parse("content://" + AUTHORITY + "/" + SQLiteBaseDaoFactory.TABLE_PICTURE);
    }

    public static Uri urlForPicture(long id) {
        return Uri.parse("content://" + AUTHORITY + "/" + SQLiteBaseDaoFactory.TABLE_PICTURE + "/" + id);
    }
    public static Uri urlForPictureCache() {
        Uri uri = Uri.parse("content://" + AUTHORITY + "/" + SQLiteBaseDaoFactory.TABLE_PICTURE_CACHE);
        return uri;
    }
    public static Uri urlForPictureCache(long id) {
        Uri uri = Uri.parse("content://" + AUTHORITY + "/" + SQLiteBaseDaoFactory.TABLE_PICTURE_CACHE + "/" + id);
        return uri;
    }
    public static Uri urlForSignature() {
        return Uri.parse("content://" + AUTHORITY + "/" + SQLiteBaseDaoFactory.TABLE_SIGNATURE);
    }
    public static Uri urlForSignature(long id) {
        return Uri.parse("content://" + AUTHORITY + "/" + SQLiteBaseDaoFactory.TABLE_SIGNATURE + "/" + id);
    }
    public static Uri urlForSignatureCache() {
        Uri uri = Uri.parse("content://" + AUTHORITY + "/" + SQLiteBaseDaoFactory.TABLE_SIGNATURE_CACHE);
        return uri;
    }
    public static Uri urlForSignatureCache(long id) {
        Uri uri = Uri.parse("content://" + AUTHORITY + "/" + SQLiteBaseDaoFactory.TABLE_SIGNATURE_CACHE + "/" + id);
        return uri;
    }

    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate");
        sqLiteBaseDaoFactory = new SQLiteBaseDaoFactory(getContext());
        return true;
    }

    @Override
    synchronized public Cursor query(@NonNull Uri uri, String[] projection,
                                     String selection, String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query:uri=" + uri + " projection=" + projection + " selection=" + selection
        + " sortOrder=" + sortOrder);

        SQLiteDatabase db = sqLiteBaseDaoFactory.getReadableDatabase();
        SQLiteQueryBuilder sqLiteQueryBuilder = new SQLiteQueryBuilder();
        Cursor cursor;

        switch (URI_MATCHER.match(uri)) {
            case PICTURE_TABLE: {
                Log.d(TAG, "query: PICTURE_TABLE: uri=" + uri);
                sqLiteQueryBuilder.setTables(SQLiteBaseDaoFactory.TABLE_PICTURE);
                sortOrder = COL_TIME + " DESC";
                break;
            }
            case PICTURE_CACHE_TABLE: {
                Log.d(TAG, "query: PICTURE_TABLE: uri=" + uri);
                sqLiteQueryBuilder.setTables(SQLiteBaseDaoFactory.TABLE_PICTURE_CACHE);
                break;
            }
            case SIGNATURE_TABLE: {
                Log.d(TAG, "query: PICTURE_TABLE: uri=" + uri);
                sqLiteQueryBuilder.setTables(SQLiteBaseDaoFactory.TABLE_SIGNATURE);
                break;
            }
            case SIGNATURE_CACHE_TABLE: {
                Log.d(TAG, "query: PICTURE_TABLE: uri=" + uri);
                sqLiteQueryBuilder.setTables(SQLiteBaseDaoFactory.TABLE_SIGNATURE_CACHE);
                break;
            }
            default:
                throw new IllegalArgumentException("uri not recognized!");
        }

        cursor = sqLiteQueryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder, null);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        Log.d(TAG, "query:cursor=" + cursor + " cursor.getCount()=" + cursor.getCount());

        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return BuildConfig.APPLICATION_ID + ".item";
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Log.d(TAG, "insert: uri=" + uri + " values=" + values);

        String table = "";
        switch (URI_MATCHER.match(uri)) {
            case PICTURE_TABLE: {
                Log.d(TAG, "insert: PICTURE_TABLE: uri=" + uri);
                table = SQLiteBaseDaoFactory.TABLE_PICTURE;
                break;
            }
            case PICTURE_CACHE_TABLE: {
                Log.d(TAG, "insert: PICTURE_CACHE_TABLE: uri=" + uri);
                table = SQLiteBaseDaoFactory.TABLE_PICTURE_CACHE;
                break;
            }
            case SIGNATURE_TABLE: {
                Log.d(TAG, "insert: SIGNATURE_TABLE: uri=" + uri);
                table = SQLiteBaseDaoFactory.TABLE_SIGNATURE;
                break;
            }
            case SIGNATURE_CACHE_TABLE: {
                Log.d(TAG, "insert: SIGNATURE_CACHE_TABLE: uri=" + uri);
                table = SQLiteBaseDaoFactory.TABLE_SIGNATURE_CACHE;
                break;
            }
            default:
                Log.d(TAG, "insert: ERROR table unknown !! table=" + table );
        }

        long result = sqLiteBaseDaoFactory.getWritableDatabase().insertWithOnConflict(table, null, values, SQLiteDatabase.CONFLICT_REPLACE);

        if (result == -1) {
            Log.d(TAG, "ERROR SQL :insert with conflict: Removes conflicting rows currently in the table, and the new row is inserted");
        }

        Uri retUri = ContentUris.withAppendedId(uri, result);
        Log.d(TAG, "insert: retUri=" + retUri);

        return retUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Log.d(TAG, "delete");
        return -1;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Log.d(TAG, "update");
        String table = "";
        switch (URI_MATCHER.match(uri)) {
            case PICTURE_ID: {
                Log.d(TAG, "update: PICTURE_ID: uri=" + uri);
                table = SQLiteBaseDaoFactory.TABLE_PICTURE;
                break;
            }
            case PICTURE_CACHE_ID: {
                Log.d(TAG, "update: PICTURE_CACHE_ID: uri=" + uri);
                table = SQLiteBaseDaoFactory.TABLE_PICTURE_CACHE;
                break;
            }
            case SIGNATURE_ID: {
                Log.d(TAG, "update: SIGNATURE_ID: uri=" + uri);
                table = SQLiteBaseDaoFactory.TABLE_SIGNATURE;
                break;
            }
            case SIGNATURE_CACHE_ID: {
                Log.d(TAG, "update: SIGNATURE_CACHE_ID: uri=" + uri);
                table = SQLiteBaseDaoFactory.TABLE_SIGNATURE_CACHE;
                break;
            }
            default:
                Log.d(TAG, "update: ERROR table unknown !! table=" + table );
        }

        String id = uri.getLastPathSegment();
        Log.d(TAG, "update: id=" + id );

        long result = sqLiteBaseDaoFactory.getWritableDatabase().updateWithOnConflict(table,
                values, COL_ID + "=" + id, null, SQLiteDatabase.CONFLICT_IGNORE);

/*        SQLiteBaseDaoPicture sqLiteBaseDaoPicture = new SQLiteBaseDaoPicture(getContext());
        sqLiteBaseDaoPicture.updatePicture()*/

        if (result == -1) {
            throw new SQLException(TAG + "update with conflict !!!");
        }
        Log.d(TAG, "update: result: number of rows affected=" + result );

        return safeLongToInt(result);
    }

    public static int safeLongToInt(long l) {
        int i = (int)l;
        if ((long)i != l) {
            throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
        }
        return i;
    }
}
