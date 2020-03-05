package com.hibernatus.hibmobtech.database.sqlitebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hibernatus.hibmobtech.model.Picture;
import com.hibernatus.hibmobtech.model.PictureInfos;

import java.util.ArrayList;
import java.util.List;

import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID_MRO_REQUEST;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_IMAGE;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_RESEND_NUMBER;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_THUMBNAIL;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_TIME;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_TITLE;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.TABLE_PICTURE;


/**
 * Created by Eric on 07/10/2016.
 */
public class SQLiteBaseDaoPicture {
    public static final String TAG = SQLiteBaseDaoPicture.class.getSimpleName();
    public static final int NUM_COL_ID = 0;
    public static final int NUM_COL_ID_MRO_REQUEST = 1;
    public static final int NUM_COL_IMAGE = 2;
    public static final int NUM_COL_THUMBNAIL = 3;
    //public static final int NUM_COL_TITLE = 4;
    public static final int NUM_COL_TITLE = 4;
    public static final int NUM_COL_TIME = 5;
    public static final int NUM_COL_RESEND_NUMBER = 6;


    private SQLiteDatabase sqLiteDatabase;
    private SQLiteBaseDaoFactory sqLiteBaseDaoFactory;

    public static String[] allColumns = {
            COL_ID,
            COL_ID_MRO_REQUEST,
            COL_IMAGE,
            COL_THUMBNAIL,
            COL_TITLE,
            COL_TIME,
            COL_RESEND_NUMBER,
    };

    public SQLiteBaseDaoPicture(Context context){
        Log.i(TAG, "Constructor");
        sqLiteBaseDaoFactory = SQLiteBaseDaoFactory.getInstance(context);
    }

    synchronized public void open(){
        Log.d(TAG, "open");
        sqLiteDatabase = sqLiteBaseDaoFactory.getWritableDatabase();
    }

    synchronized public void close(){
        Log.d(TAG, "close");
        //sqLiteDatabase.close();
    }

    synchronized public SQLiteDatabase getSQLiteDatabase(){
        return sqLiteDatabase;
    }


    /*
     * --------------------------------------------------
     * All CRUD(Create, Read, Update, Delete) Operations
     * --------------------------------------------------
     */

    synchronized public void addMroRequestPicture(Picture picture, Long mroRequestId) {
        Log.d(TAG, "addMroRequestPicture: description=" + picture.getInfos().getTitle()
        + "mroRequestId=" + mroRequestId);
        ContentValues values = new ContentValues();
        values.put(COL_ID_MRO_REQUEST, mroRequestId);
        values.put(COL_IMAGE, picture.getImage());
        values.put(COL_THUMBNAIL, picture.getThumbnail());
        //values.put(COL_TITLE, picture.getInfos().getTitle());
        values.put(COL_TITLE, picture.getInfos().getTitle());
        values.put(COL_TIME, picture.getInfos().getTime());
        values.put(COL_RESEND_NUMBER, picture.getResendNumber());
        sqLiteDatabase.insert(TABLE_PICTURE, null, values);
        //sqLiteDatabase.close(); // Closing database connection
    }

    synchronized public void addPicture(Picture picture) {
        Log.d(TAG, "addPictureToMroRequestCurrent");
        ContentValues values = new ContentValues();
        values.put(COL_IMAGE, picture.getImage());
        values.put(COL_THUMBNAIL, picture.getThumbnail());
        //values.put(COL_TITLE, picture.getInfos().getTitle());
        values.put(COL_TITLE, picture.getInfos().getTitle());
        values.put(COL_TIME, picture.getInfos().getTime());
        values.put(COL_RESEND_NUMBER, picture.getResendNumber());

        sqLiteDatabase.insert(TABLE_PICTURE, null, values);
        //sqLiteDatabase.close(); // Closing database connection
    }

    synchronized public Picture getPicture(int id) {
        Log.d(TAG, "getPicture");
        Cursor cursor = sqLiteDatabase.query(TABLE_PICTURE, allColumns , COL_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursorToTPicture(cursor);
    }

    synchronized private Picture cursorToTPicture(Cursor cursor){
/*        Log.d(TAG, "cursorToTPicture: cursor=" + cursor
                + " cursorPosition=" + cursor.getPosition());*/
        Picture picture = new Picture();
        picture.setId((long) cursor.getInt(NUM_COL_ID));
        picture.setIdMroRequest((long) cursor.getInt(NUM_COL_ID_MRO_REQUEST));
        picture.setImage(cursor.getBlob(NUM_COL_IMAGE));
        picture.setThumbnail(cursor.getBlob(NUM_COL_THUMBNAIL));
        picture.setResendNumber(cursor.getInt(NUM_COL_RESEND_NUMBER));
        PictureInfos pictureInfos = new PictureInfos();
        //pictureInfos.setTitle(cursor.getString(NUM_COL_TITLE));
        pictureInfos.setTitle(cursor.getString(NUM_COL_TITLE));
        pictureInfos.setTime(cursor.getLong(NUM_COL_TIME));
        picture.setInfos(pictureInfos);
        Log.d(TAG, "cursorToTPicture: picture=" + picture + " cursor.getPosition()=" + cursor.getPosition()
                + " picture.getInfos().getTitle()=" + picture.getInfos().getTitle());
        return picture;
    }

    // Getting All Pictures
    synchronized public List<Picture> getAllTablePictures() {
        Log.d(TAG, "getAllTablePictures");

        if (!sqLiteDatabase.isOpen()){
            open();
        }

        List<Picture> pictureList = new ArrayList<>();
        Picture picture;

        // Select All Query
        String selectQuery = "SELECT * FROM " +  TABLE_PICTURE + " ORDER BY " + COL_ID;

        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                picture = cursorToTPicture(cursor);

                // Adding picture to list
                pictureList.add(picture);
                Log.d(TAG, "getAllTablePictures: picture=" + picture + " picture.getInfos().getTitle()="
                        + picture.getInfos().getTitle());

            } while (cursor.moveToNext());
        }
        Log.d(TAG, "getAllTablePictures: pictureList=" + pictureList);
        return pictureList;

    }

    /** Returns all the Pictures in the table */
    public Cursor getCursorAllPictures(){
        return sqLiteDatabase.query(TABLE_PICTURE, allColumns ,
                null, null, null, null,
                COL_TIME + " asc ");
    }

    // Updating single picture
    synchronized public int updatePicture(Picture picture) {
        Log.d(TAG, "updatePicture");
        ContentValues values = new ContentValues();
        values.put(COL_ID, picture.getId());
        values.put(COL_IMAGE, picture.getImage());
        values.put(COL_THUMBNAIL, picture.getThumbnail());
        //values.put(COL_TITLE, picture.getInfos().getTitle());
        values.put(COL_TITLE, picture.getInfos().getTitle());
        values.put(COL_TIME, picture.getInfos().getTime());
        values.put(COL_RESEND_NUMBER, picture.getResendNumber());
        return sqLiteDatabase.update(TABLE_PICTURE, values, COL_ID + " = ?",
                new String[] { String.valueOf(picture.getId()) });

    }

    // Deleting single picture
    synchronized public int deletePicture(Picture picture) {
        Log.d(TAG, "deletePicture");
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.delete(TABLE_PICTURE, COL_ID + " = ?",
                    new String[] { String.valueOf(picture.getId()) });
        }
        else {
            open();
            Log.d(TAG, "deletePicture: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return  sqLiteDatabase.delete(TABLE_PICTURE, COL_ID + " = ?",
                    new String[] { String.valueOf(picture.getId()) });
        }
    }

    synchronized public int deleteAllTablePictures(){
        Log.i(TAG, "deleteAllTablePictures");
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_PICTURE, null, null);
        }
        else {
            open();
            Log.d(TAG, "deleteAllTablePictures: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_PICTURE, null, null);
        }
    }

    // Getting pictures Count
    synchronized public int getPicturesCount() {
        Log.d(TAG, "getPicturesCount");
        String countQuery = "SELECT * FROM " + TABLE_PICTURE;
        Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }
}
