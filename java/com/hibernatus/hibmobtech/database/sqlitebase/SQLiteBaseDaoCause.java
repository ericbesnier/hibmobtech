package com.hibernatus.hibmobtech.database.sqlitebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hibernatus.hibmobtech.model.Cause;
import com.hibernatus.hibmobtech.model.dao.CauseDao;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 23/01/2016.
 */
public class SQLiteBaseDaoCause implements CauseDao {
    public static final String TAG = SQLiteBaseDaoCause.class.getSimpleName();

    private static final int NUM_COL_ID = 0;
    private static final int NUM_COL_DESCRIPTION = 1;
    private static final int NUM_COL_CATEGORY = 2;

    private SQLiteDatabase sqLiteDatabase;
    private SQLiteBaseDaoFactory sqLiteBaseDaoFactory;

    private String[] allColumns = {
            SQLiteBaseDaoFactory.COL_ID,
            SQLiteBaseDaoFactory.COL_DESCRIPTION,
            SQLiteBaseDaoFactory.COL_CATEGORY };

    public SQLiteBaseDaoCause(Context context){
        Log.i(TAG, "Constructor");
        sqLiteBaseDaoFactory = SQLiteBaseDaoFactory.getInstance(context);
    }

    synchronized public void getWritableDatabase(){
        sqLiteDatabase = sqLiteBaseDaoFactory.getWritableDatabase();
    }

    synchronized public void close(){
        Log.d(TAG, "close");
        //sqLiteDatabase.close();
    }

    synchronized public SQLiteDatabase getSQLiteDatabase(){
        return sqLiteDatabase;
    }

    synchronized public int deleteAllCauses(){
        Log.d(TAG, "deleteAllCauses");

        getWritableDatabase();
        Log.d(TAG, "deleteAllCauses: sqLiteDatabase is closed, re-getWritableDatabase it !");
        return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_CAUSE, null, null);

    }

    synchronized public List<Cause> getAllCauses() {
        Log.d(TAG, "getAllCauses");
        List<Cause> causes = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.query(SQLiteBaseDaoFactory.TABLE_CAUSE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Cause cause = cursorToCause(cursor);
            causes.add(cause);
            cursor.moveToNext();
        }
        cursor.close();
        return causes;
    }

    synchronized public List<Cause> getCauses(String description) {
        Log.d(TAG, "getCauses: description=" + description);
        List<Cause> causes = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from table_cause where description like ?",
                new String[]{"%" + description + "%"});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Cause cause = cursorToCause(cursor);
            causes.add(cause);
            cursor.moveToNext();
        }
        cursor.close();
        return causes;
    }

    private synchronized Cause cursorToCause(Cursor cursor){
        Cause cause = new Cause();
        cause.setId(Long.valueOf(cursor.getInt(NUM_COL_ID)));
        cause.setDescription(cursor.getString(NUM_COL_DESCRIPTION));
        cause.setCategory(cursor.getString(NUM_COL_CATEGORY));

        Log.d(TAG, "cursorToCause: cursorPosition=" + cursor.getPosition() + " cause=" + cause.getDescription());
        return cause;
    }

    synchronized public String addCause(Cause cause){
        Log.d(TAG, "setCausesView: id=" + cause.getId()
                + " description=" + cause.getDescription()
                + " category=" + cause.getCategory());

        ContentValues values = new ContentValues();
        values.put(SQLiteBaseDaoFactory.COL_ID, cause.getId());
        values.put(SQLiteBaseDaoFactory.COL_DESCRIPTION, cause.getDescription());
        values.put(SQLiteBaseDaoFactory.COL_CATEGORY, cause.getCategory());
        if (sqLiteDatabase.isOpen()){
            Long id = sqLiteDatabase.insert(SQLiteBaseDaoFactory.TABLE_CAUSE, null, values);
            return id.toString();
        }
        else {
            getWritableDatabase();
            Log.d(TAG, "setCausesView: sqLiteDatabase is closed, re-getWritableDatabase it !");
            Long id = sqLiteDatabase.insert(SQLiteBaseDaoFactory.TABLE_CAUSE, null, values);
            return id.toString();
        }
    }

    synchronized public int updateCause(int id, Cause cause){
        Log.d(TAG, "updateCause: description=" + cause.getDescription());
        ContentValues values = new ContentValues();
        values.put(SQLiteBaseDaoFactory.COL_ID, cause.getId());
        values.put(SQLiteBaseDaoFactory.COL_DESCRIPTION, cause.getDescription());
        values.put(SQLiteBaseDaoFactory.COL_CATEGORY, cause.getCategory());
        return sqLiteDatabase.update(SQLiteBaseDaoFactory.TABLE_CAUSE, values, SQLiteBaseDaoFactory.COL_ID + " = " + id, null);
    }

    synchronized public int deleteCause(int id){
        return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_CAUSE, SQLiteBaseDaoFactory.COL_ID + " = " + id, null);
    }

    synchronized public Cause getCauseByDescription(String description){
        Log.d(TAG, "getCauseByDescription: description=" + description);
        Cursor c = sqLiteDatabase.rawQuery("select * from table_cause where description = ?", new String[]{description});
        return cursorToCause(c);
    }
}



/*    synchronized public int deleteAllCauses(){
        Log.d(TAG, "deleteAllCauses");
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_CAUSE, null, null);
        }
        else {
            getWritableDatabase();
            Log.e(TAG, "deleteAllCauses: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_CAUSE, null, null);
        }
    }*/