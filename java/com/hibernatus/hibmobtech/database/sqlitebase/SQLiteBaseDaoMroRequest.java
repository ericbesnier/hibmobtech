package com.hibernatus.hibmobtech.database.sqlitebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hibernatus.hibmobtech.model.MroRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 23/01/2016.
 */
public class SQLiteBaseDaoMroRequest {
    public static final String TAG = SQLiteBaseDaoMroRequest.class.getSimpleName();
    private static final int NUM_COL_ID = 0;
    private SQLiteDatabase sqLiteDatabase;
    private SQLiteBaseDaoFactory sqLiteBaseDaoFactory;
    private String[] allColumns = { SQLiteBaseDaoFactory.COL_ID };

    public SQLiteBaseDaoMroRequest(Context context){
        Log.i(TAG, "Constructor");
        sqLiteBaseDaoFactory = SQLiteBaseDaoFactory.getInstance(context);
    }

    synchronized public void open(){
        sqLiteDatabase = sqLiteBaseDaoFactory.getWritableDatabase();
    }

    synchronized public void close(){
        Log.d(TAG, "close");
        //sqLiteDatabase.close();
    }

    synchronized public SQLiteDatabase getBDD(){
        return sqLiteDatabase;
    }

    synchronized public int deleteAll(){
        Log.i(TAG, "deleteAll");
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_MRO_REQUEST, null, null);
        }
        else {
            open();
            Log.d(TAG, "deleteAll: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_MRO_REQUEST, null, null);
        }
    }

    synchronized public List<MroRequest> getAllMroRequest() {
        Log.i(TAG, "getAllMroRequest");
        List<MroRequest> mroRequestList = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.query(SQLiteBaseDaoFactory.TABLE_MRO_REQUEST,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            MroRequest mroRequest = cursorToMroRequest(cursor);
            mroRequestList.add(mroRequest);
            cursor.moveToNext();
        }
        cursor.close();
        return mroRequestList;
    }

    synchronized private MroRequest cursorToMroRequest(Cursor cursor){
        MroRequest mroRequest = new MroRequest();
        mroRequest.setId(Long.valueOf(cursor.getInt(NUM_COL_ID)));
        return mroRequest;
    }

    synchronized public long insertMroRequest(MroRequest mroRequest){
        ContentValues values = new ContentValues();
        values.put(SQLiteBaseDaoFactory.COL_ID, mroRequest.getId());
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.insert(SQLiteBaseDaoFactory.TABLE_MRO_REQUEST, null, values);
        }
        else {
                open();
                Log.d(TAG, "insertMroRequest: sqLiteDatabase is closed, re-getWritableDatabase it !");
                return sqLiteDatabase.insert(SQLiteBaseDaoFactory.TABLE_MRO_REQUEST, null, values);
        }
    }

    synchronized public int updateMroRequest(int id, MroRequest mroRequest){
        Log.i(TAG, "updateMroRequest: mroRequest.getId()=" + mroRequest.getId());
        ContentValues values = new ContentValues();
        values.put(SQLiteBaseDaoFactory.COL_ID, mroRequest.getId());
        return sqLiteDatabase.update(SQLiteBaseDaoFactory.TABLE_MRO_REQUEST, values, SQLiteBaseDaoFactory.COL_ID + " = " + id, null);
    }

    synchronized public int removeMroRequestWithID(int id){
        return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_MRO_REQUEST, SQLiteBaseDaoFactory.COL_ID + " = " + id, null);
    }
}
