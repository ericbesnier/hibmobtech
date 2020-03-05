package com.hibernatus.hibmobtech.database.sqlitebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hibernatus.hibmobtech.model.ClientSignature;
import com.hibernatus.hibmobtech.model.SignatureInfos;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID_MRO_REQUEST;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_RESEND_NUMBER;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_SIGNATURE_FILE;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_SIGNER_NAME;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_SIGNER_ROLE;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_TIMESTAMP;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.TABLE_SIGNATURE;

// TODO supprimer import java.sql.Date;

/**
 * Created by Eric on 07/10/2016.
 */
public class SQLiteBaseDaoSignature {
    public static final String TAG = SQLiteBaseDaoSignature.class.getSimpleName();
    private static final int NUM_COL_ID = 0;
    private static final int NUM_COL_ID_MRO_REQUEST = 1;
    private static final int NUM_COL_SIGNATURE_FILE = 2;
    private static final int NUM_COL_SIGNER_NAME = 3;
    private static final int NUM_COL_SIGNER_ROLE = 4;
    private static final int NUM_COL_TIMESTAMP = 5;
    private static final int NUM_COL_RESEND_NUMBER = 6;


    private SQLiteDatabase sqLiteDatabase;
    private SQLiteBaseDaoFactory sqLiteBaseDaoFactory;

    private String[] allColumns = {
            COL_ID,
            COL_ID_MRO_REQUEST,
            COL_SIGNATURE_FILE,
            COL_SIGNER_NAME,
            COL_SIGNER_ROLE,
            COL_TIMESTAMP,
            COL_RESEND_NUMBER,
    };

    public SQLiteBaseDaoSignature(Context context){
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
     * All CRUD (Create, Read, Update, Delete) Operations
     * --------------------------------------------------
     */

    synchronized public long addMroRequestSignature(ClientSignature clientSignature, Long mroRequestId) {
        Log.d(TAG, "addMroRequestSignature");
        ContentValues values = new ContentValues();
        values.put(COL_ID_MRO_REQUEST, mroRequestId);
        values.put(COL_SIGNATURE_FILE, clientSignature.getSignatureFile());
        values.put(COL_SIGNER_NAME, clientSignature.getInfos().getSignerName());
        values.put(COL_SIGNER_ROLE, clientSignature.getInfos().getSignerName());
        values.put(COL_TIMESTAMP, clientSignature.getInfos().getTimestamp().toString());
        values.put(COL_RESEND_NUMBER, clientSignature.getResendNumber());
        return sqLiteDatabase.insert(TABLE_SIGNATURE, null, values);
    }

    synchronized public void addSignature(ClientSignature clientSignature) {
        Log.d(TAG, "addSignature");
        ContentValues values = new ContentValues();
        values.put(COL_ID_MRO_REQUEST, clientSignature.getIdMroRequest());
        values.put(COL_SIGNATURE_FILE, clientSignature.getSignatureFile());
        values.put(COL_SIGNER_NAME, clientSignature.getInfos().getSignerName());
        values.put(COL_SIGNER_ROLE, clientSignature.getInfos().getSignerRole());
        values.put(COL_TIMESTAMP, clientSignature.getInfos().getTimestamp().toString());
        values.put(COL_RESEND_NUMBER, clientSignature.getResendNumber());
        sqLiteDatabase.insert(TABLE_SIGNATURE, null, values);    }

    synchronized public ClientSignature getSignature(long id) {
        Log.d(TAG, "getSignature");
        Cursor cursor = sqLiteDatabase.query(TABLE_SIGNATURE, allColumns , COL_ID + "=?",
                new String[] { String.valueOf(id) }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        return cursorToTSignature(cursor);
    }

    synchronized private ClientSignature cursorToTSignature(Cursor cursor){
        Log.d(TAG, "cursorToTSignature: cursor=" + cursor
                + " cursorPosition=" + cursor.getPosition());
        SignatureInfos signatureInfos = new SignatureInfos();
        signatureInfos.setSignerName(cursor.getString(NUM_COL_SIGNER_NAME));
        signatureInfos.setSignerRole(cursor.getString(NUM_COL_SIGNER_ROLE));

        Log.d(TAG, "cursorToTSignature: cursor.getString(NUM_COL_TIMESTAMP)=" + cursor.getString(NUM_COL_TIMESTAMP));

        // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.US);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date = null;
        try {
            date = simpleDateFormat.parse(cursor.getString(NUM_COL_TIMESTAMP));
        } catch (ParseException e) {
            e.printStackTrace();
        };
        signatureInfos.setTimestamp(date);

        ClientSignature clientSignature = new ClientSignature();
        clientSignature.setId((long) cursor.getInt(NUM_COL_ID));
        clientSignature.setIdMroRequest((long) cursor.getInt(NUM_COL_ID_MRO_REQUEST));
        clientSignature.setSignatureFile(cursor.getBlob(NUM_COL_SIGNATURE_FILE));
        clientSignature.setResendNumber(cursor.getInt(NUM_COL_RESEND_NUMBER));
        clientSignature.setInfos(signatureInfos);

        Log.d(TAG, "cursorToTSignature: signature=" + clientSignature + " cursor.getPosition()=" + cursor.getPosition()
                + " signature.getIdMroRequest()=" + clientSignature.getIdMroRequest());
        return clientSignature;
    }

    // Getting All Signatures of MroRequest
    synchronized public List<ClientSignature> getAllMroRequestSignatures(Long MroRequestId) {
        Log.d(TAG, "getAllMroRequestSignatures");
        List<ClientSignature> clientSignatureList = new ArrayList<>();
        ClientSignature clientSignature;

        // Select All Query
        String selectQuery = "SELECT * FROM " +  TABLE_SIGNATURE
                + " WHERE " + COL_ID_MRO_REQUEST + "=" + String.valueOf(MroRequestId)
                + " ORDER BY " + COL_ID;

        Cursor cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                clientSignature = cursorToTSignature(cursor);

                // Adding signature to list
                clientSignatureList.add(clientSignature);
                Log.d(TAG, "getAllMroRequestSignatures: signature=" + clientSignature + " cursor.getPosition()=" + cursor.getPosition()
                        + " signature.getIdMroRequest()=" + clientSignature.getIdMroRequest());

            } while (cursor.moveToNext());
        }
        Log.d(TAG, "getAllMroRequestSignatures: signatureList=" + clientSignatureList);
        return clientSignatureList;
    }

    // Getting All Signatures
    synchronized public List<ClientSignature> getAllTableSignatures() {
        Log.d(TAG, "getAllTableSignatures");
        List<ClientSignature> clientSignatureList = new ArrayList<>();
        ClientSignature clientSignature;
        Cursor cursor;
        // Select All Query
        String selectQuery = "SELECT * FROM " +  TABLE_SIGNATURE + " ORDER BY " + COL_ID;
        if (sqLiteDatabase.isOpen()){
            cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        }
        else {
            open();
            cursor = sqLiteDatabase.rawQuery(selectQuery, null);
        }

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                clientSignature = cursorToTSignature(cursor);

                // Adding signature to list
                clientSignatureList.add(clientSignature);
                Log.d(TAG, "getAllTableSignatures: signature=" + clientSignature + " cursor.getPosition()=" + cursor.getPosition()
                        + " signature.getIdMroRequest()=" + clientSignature.getIdMroRequest());

            } while (cursor.moveToNext());
        }
        Log.d(TAG, "getAllTableSignatures: signatureList=" + clientSignatureList);
        return clientSignatureList;
    }

    // Updating single signature
    synchronized public int updateSignature(ClientSignature clientSignature) {
        Log.d(TAG, "updateSignature:signature.getId()=" + clientSignature.getId()
                + " signature.getIdMroRequest()=" + clientSignature.getIdMroRequest());

        ContentValues values = new ContentValues();
        values.put(COL_ID, clientSignature.getId());
        values.put(COL_SIGNATURE_FILE, clientSignature.getSignatureFile());
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.update(TABLE_SIGNATURE, values, COL_ID + " = ?",
                    new String[] { String.valueOf(clientSignature.getId()) });
        }
        else {
            open();
            Log.d(TAG, "updateSignature: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.update(TABLE_SIGNATURE, values, COL_ID + " = ?",
                    new String[] { String.valueOf(clientSignature.getId()) });
        }
    }

    // Deleting single signature
    synchronized public int deleteSignature(ClientSignature clientSignature) {
        Log.d(TAG, "deleteSignature");
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.delete(TABLE_SIGNATURE, COL_ID + " = ?",
                    new String[] { String.valueOf(clientSignature.getId()) });
        }
        else {
            open();
            Log.d(TAG, "deleteSignature: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return  sqLiteDatabase.delete(TABLE_SIGNATURE, COL_ID + " = ?",
                    new String[] { String.valueOf(clientSignature.getId()) });
        }
    }

    // Deleting all signatures of MroRequest
    synchronized public int deleteAllMroRequestSignatures(Long MroRequestId) {
        Log.d(TAG, "deleteAllMroRequestSignatures");
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.delete(TABLE_SIGNATURE, COL_ID_MRO_REQUEST + " = ?",
                    new String[] { String.valueOf(MroRequestId) });
        }
        else {
            open();
            Log.d(TAG, "deleteAllMroRequestSignatures: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return  sqLiteDatabase.delete(TABLE_SIGNATURE, COL_ID_MRO_REQUEST + " = ?",
                    new String[] { String.valueOf(MroRequestId) });
        }
    }

    // Getting signatures Count
    synchronized public int getSignaturesCount() {
        Log.d(TAG, "getSignaturesCount");
        String countQuery = "SELECT * FROM " + TABLE_SIGNATURE;
        Cursor cursor = sqLiteDatabase.rawQuery(countQuery, null);
        cursor.close();
        return cursor.getCount();
    }

    synchronized public int deleteAllTableSignatures(){
        Log.d(TAG, "deleteAllTableSignatures");
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_SIGNATURE, null, null);
        }
        else {
            open();
            Log.d(TAG, "deleteAllTableSignatures: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_SIGNATURE, null, null);
        }
    }
}
