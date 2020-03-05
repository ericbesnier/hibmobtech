package com.hibernatus.hibmobtech.database.sqlitebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hibernatus.hibmobtech.model.TrackingLocation;

import java.util.ArrayList;
import java.util.List;

import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ACCURACY;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ALTITUDE;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_BEARING;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_LATITUDE;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_LONGITUDE;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_SPEED;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_TIME;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.TABLE_TRACKING_LOCATION;
import static com.hibernatus.hibmobtech.utils.ThreadUtils.getThreadSignature;

/**
 * Created by Eric on 23/01/2016.
 */
public class SQLiteBaseDaoTrackingLocation {
    public static final String TAG = SQLiteBaseDaoTrackingLocation.class.getSimpleName();

    private static final int NUM_COL_LATITUDE = 1;
    private static final int NUM_COL_LONGITUDE = 2;
    private static final int NUM_COL_TIME = 3;
    private static final int NUM_COL_ALTITUDE = 4;
    private static final int NUM_COL_ACCURACY = 5;
    private static final int NUM_COL_SPEED = 6;
    private static final int NUM_COL_BEARING = 7;

    private SQLiteDatabase sqLiteDatabase;
    private SQLiteBaseDaoFactory sqLiteBaseDaoFactory;

    private String[] allColumns = {
            COL_ID,
            COL_LATITUDE,
            COL_LONGITUDE,
            COL_TIME,
            COL_ALTITUDE,
            COL_ACCURACY,
            COL_SPEED,
            COL_BEARING,};

    public SQLiteBaseDaoTrackingLocation(Context context){
        Log.d(TAG, "Constructor sqLiteBaseDaoTrackingLocation");
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
            return sqLiteDatabase.delete(TABLE_TRACKING_LOCATION, null, null);
        }
        else {
            open();
            Log.d(TAG, "deleteAll: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.delete(TABLE_TRACKING_LOCATION, null, null);
        }
    }

    synchronized public int deleteTrackingLocation(TrackingLocation trackingLocation){
        double latitude = trackingLocation.getLatitude();
        double longitude = trackingLocation.getLongitude();
        Log.d(TAG, "deleteTrackingLocation: " + getThreadSignature()
                + " lat=" + latitude + " long=" + longitude);

        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.delete(TABLE_TRACKING_LOCATION,
                    COL_LATITUDE + " = " + latitude + " AND " + COL_LONGITUDE + " = " + longitude, null);
        }
        else {
            open();
            Log.d(TAG, "deleteAll: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.delete(TABLE_TRACKING_LOCATION,
                    COL_LATITUDE + " = " + latitude + " AND " + COL_LONGITUDE + " = " + longitude, null);
        }
    }

    synchronized public List<TrackingLocation> getAllTrackingLocations() {
        //Log.d(TAG, "getAllTrackingLocations");
        List<TrackingLocation> trackingLocations = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.query(TABLE_TRACKING_LOCATION,
                allColumns, null, null, null, null, null);

        int i = 1;
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            TrackingLocation trackingLocation = cursorToTrackingLocation(cursor);
            Log.d(TAG, "getAllTrackingLocations: " + getThreadSignature() + " [" + i + "] lat=" + trackingLocation.getLatitude()
            + " long=" + trackingLocation.getLongitude());

            trackingLocations.add(trackingLocation);
            cursor.moveToNext();
            i++;
        }
        cursor.close();
        return trackingLocations;
    }

    synchronized private TrackingLocation cursorToTrackingLocation(Cursor cursor){
/*        Log.d(TAG, "cursorToTrackingLocation: cursor=" + cursor
                + " cursorPosition=" + cursor.getPosition());*/
        TrackingLocation trackingLocation = new TrackingLocation();
        trackingLocation.setLatitude(Double.parseDouble(cursor.getString(NUM_COL_LATITUDE)));
        trackingLocation.setLongitude(Double.parseDouble(cursor.getString(NUM_COL_LONGITUDE)));
        trackingLocation.setTime(Long.parseLong(cursor.getString(NUM_COL_TIME)));
        trackingLocation.setAltitude(Double.parseDouble(cursor.getString(NUM_COL_ALTITUDE)));
        trackingLocation.setAccuracy(Float.parseFloat(cursor.getString(NUM_COL_ACCURACY)));
        trackingLocation.setSpeed(Float.parseFloat(cursor.getString(NUM_COL_SPEED)));
        trackingLocation.setBearing(Float.parseFloat(cursor.getString(NUM_COL_BEARING)));
/*        Log.d(TAG, "cursorToTrackingLocation: cursorPosition=" + cursor.getPosition()
                + " lat=" + trackingLocation.getLatitude()
                + " long=" + trackingLocation.getLongitude());*/
        return trackingLocation;
    }

    synchronized public long insertTrackingLocation(TrackingLocation trackingLocation){
        Log.d(TAG, "insertTrackingLocation: " + getThreadSignature() + " lat=" + trackingLocation.getLatitude()
                + " long=" + trackingLocation.getLongitude());

        ContentValues values = new ContentValues();
        values.put(COL_LATITUDE, trackingLocation.getLatitude());
        values.put(COL_LONGITUDE, trackingLocation.getLongitude());
        values.put(COL_TIME, trackingLocation.getTime());
        values.put(COL_ALTITUDE, trackingLocation.getAltitude());
        values.put(COL_ACCURACY, trackingLocation.getAccuracy());
        values.put(COL_SPEED, trackingLocation.getSpeed());
        values.put(COL_BEARING, trackingLocation.getBearing());

        if (sqLiteDatabase.isOpen()){return sqLiteDatabase.insert(TABLE_TRACKING_LOCATION, null, values);
        }
        else {
            open();
            Log.d(TAG, "insertTrackingLocation: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.insert(TABLE_TRACKING_LOCATION, null, values);
        }
    }
}
