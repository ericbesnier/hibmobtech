package com.hibernatus.hibmobtech.database.sqlitebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hibernatus.hibmobtech.model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Eric on 23/01/2016.
 */
public class SQLiteBaseDaoTask {
    public static final String TAG = SQLiteBaseDaoTask.class.getSimpleName();
    private static final int NUM_COL_ID = 0;
    private static final int NUM_COL_DESCRIPTION = 1;
    private static final int NUM_COL_CATEGORY = 2;

    private SQLiteDatabase sqLiteDatabase;
    private SQLiteBaseDaoFactory sqLiteBaseDaoFactory;

    private String[] allColumns = {
            SQLiteBaseDaoFactory.COL_ID,
            SQLiteBaseDaoFactory.COL_DESCRIPTION,
            SQLiteBaseDaoFactory.COL_CATEGORY };

    public SQLiteBaseDaoTask(Context context){
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

    synchronized public SQLiteDatabase getSQLiteDatabase(){
        return sqLiteDatabase;
    }

    synchronized public int deleteAll(){
        Log.d(TAG, "deleteAll");
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_TASK, null, null);
        }
        else {
            open();
            Log.d(TAG, "deleteAllCauses: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_TASK, null, null);
        }
    }

    synchronized public List<Task> getAllTasks() {
        Log.d(TAG, "getAllTasks");
        List<Task> tasks = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.query(SQLiteBaseDaoFactory.TABLE_TASK,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }
        cursor.close();
        return tasks;
    }

    synchronized public List<Task> getTasksByDescription(String description) {
        Log.d(TAG, "getTasksByDescription: description=" + description);
        List<Task> tasks = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from table_task where description like ?",
                new String[]{"%" + description + "%"});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Task task = cursorToTask(cursor);
            tasks.add(task);
            cursor.moveToNext();
        }
        cursor.close();
        return tasks;
    }

    synchronized private Task cursorToTask(Cursor cursor){
        Log.d(TAG, "cursorToTask: cursor=" + cursor
                + " cursorPosition=" + cursor.getPosition());
        Task task = new Task();
        task.setId(Long.valueOf(cursor.getInt(NUM_COL_ID)));
        task.setDescription(cursor.getString(NUM_COL_DESCRIPTION));
        task.setCategory(cursor.getString(NUM_COL_CATEGORY));

        Log.d(TAG, "cursorToTask: cursorPosition=" + cursor.getPosition() + " task=" + task.getDescription());
        return task;
    }

    synchronized public long insertTask(Task task){
        Log.d(TAG, "insertTask: id=" + task.getId()
                + " description=" + task.getDescription()
                + " category=" + task.getCategory());

        ContentValues values = new ContentValues();
        values.put(SQLiteBaseDaoFactory.COL_ID, task.getId());
        values.put(SQLiteBaseDaoFactory.COL_DESCRIPTION, task.getDescription());
        values.put(SQLiteBaseDaoFactory.COL_CATEGORY, task.getCategory());
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.insert(SQLiteBaseDaoFactory.TABLE_TASK, null, values);
        }
        else {
            open();
            Log.d(TAG, "insertTask: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.insert(SQLiteBaseDaoFactory.TABLE_TASK, null, values);
        }
    }

    synchronized public int updateTask(int id, Task task){
        Log.d(TAG, "updateTask: description=" + task.getDescription());
        ContentValues values = new ContentValues();
        values.put(SQLiteBaseDaoFactory.COL_ID, task.getId());
        values.put(SQLiteBaseDaoFactory.COL_DESCRIPTION, task.getDescription());
        values.put(SQLiteBaseDaoFactory.COL_CATEGORY, task.getCategory());
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.update(SQLiteBaseDaoFactory.TABLE_TASK, values, SQLiteBaseDaoFactory.COL_ID + " = " + id, null);
        }
        else {
            open();
            Log.d(TAG, "updateTask: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.update(SQLiteBaseDaoFactory.TABLE_TASK, values, SQLiteBaseDaoFactory.COL_ID + " = " + id, null);
        }
    }

    synchronized public int removeTaskWithID(int id){
        return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_TASK, SQLiteBaseDaoFactory.COL_ID + " = " + id, null);
    }

    synchronized public Task getTaskWithDescription(String description){
        Log.d(TAG, "getTaskWithDescription: description=" + description);
        Cursor c = sqLiteDatabase.rawQuery("select * from table_task where description = ?", new String[]{description});
        return cursorToTask(c);
    }
}
