package com.hibernatus.hibmobtech.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoPicture;

/**
 * Created by Eric on 12/01/2017.
 */

public class ContentProviderPicture extends ContentProvider {
    public static final String TAG = ContentProviderPicture.class.getSimpleName();

    public static final String PROVIDER_NAME = "com.hibernatus.hibmobtech.contentprovider.ContentProviderPicture";

    /** A uri to do operations on cust_master table. A content provider is identified by its uri */
    public static final Uri CONTENT_URI = Uri.parse("content://" + PROVIDER_NAME + "/pictures" );

    /** Constants to identify the requested operation */
    private static final int PICTURES = 1;

    private static final UriMatcher uriMatcher ;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "pictures", PICTURES);
    }

    /** This content provider does the database operations by this object */
    SQLiteBaseDaoPicture mSQLiteBaseDaoPicture;

    /** A callback method which is invoked when the content provider is starting up */
    @Override
    public boolean onCreate() {
        Log.e(TAG, "onCreate");
        mSQLiteBaseDaoPicture = new SQLiteBaseDaoPicture(getContext());
        return true;
    }

    @Override
    public String getType(Uri uri) {
        Log.e(TAG, "getType");

        return null;
    }

    /** A callback method which is by the default content uri */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.e(TAG, "query");

        if(uriMatcher.match(uri)== PICTURES){
            return mSQLiteBaseDaoPicture.getCursorAllPictures();
        }else{
            return null;
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        Log.e(TAG, "delete");

        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Log.e(TAG, "insert");

        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        Log.e(TAG, "onUpdatedContent");

        // TODO Auto-generated method stub
        return 0;
    }
}
