package com.hibernatus.hibmobtech.database.sqlitebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hibernatus.hibmobtech.model.Equipment;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

// TODO supprimer import java.sql.Date;

/**
 * Created by Eric on 23/01/2016.
 */
public class SQLiteBaseDaoEquipment {
    public static final String TAG = SQLiteBaseDaoEquipment.class.getSimpleName();
    private static final int NUM_COL_ID = 0;
    private static final int NUM_COL_REFERENCE = 1;
    private static final int NUM_COL_CATEGORY = 2;
    private static final int NUM_COL_BRAND = 3;
    private static final int NUM_COL_MODEL = 4;
    private static final int NUM_COL_MODEL_YEAR = 5;
    private static final int NUM_COL_HEATING = 6;
    private static final int NUM_COL_DESCRIPTION = 7;
    private static final int NUM_COL_RELEASE_DATE = 8;
    private static final int NUM_COL_PRICE = 9;
    private static final int NUM_COL_FORMATTED_PRICE = 10;

    private SQLiteDatabase sqLiteDatabase;
    private SQLiteBaseDaoFactory sqLiteBaseDaoFactory;

    private String[] allColumns = {
            SQLiteBaseDaoFactory.COL_ID,
            SQLiteBaseDaoFactory.COL_REFERENCE,
            SQLiteBaseDaoFactory.COL_CATEGORY,
            SQLiteBaseDaoFactory.COL_BRAND,
            SQLiteBaseDaoFactory.COL_MODEL,
            SQLiteBaseDaoFactory.COL_MODEL_YEAR,
            SQLiteBaseDaoFactory.COL_HEATING,
            SQLiteBaseDaoFactory.COL_DESCRIPTION,
            SQLiteBaseDaoFactory.COL_RELEASE_DATE,
            SQLiteBaseDaoFactory.COL_PRICE,
            SQLiteBaseDaoFactory.COL_FORMATTED_PRICE };

    public SQLiteBaseDaoEquipment(Context context){
        Log.i(TAG, "Constructor");
        sqLiteBaseDaoFactory = SQLiteBaseDaoFactory.getInstance(context);

/*        sqLiteBaseDaoFactory = SQLiteBaseDaoFactory.getInstance(context,
                HibmobtechApplication.getSqliteBaseName(), null,
                HibmobtechApplication.getSqliteBaseVersion());*/
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
        Log.i(TAG, "deleteAllCauses");
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_EQUIPMENT, null, null);
        }
        else {
            open();
            Log.d(TAG, "deleteAllCauses: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_EQUIPMENT, null, null);
        }
    }

    synchronized public List<Equipment> getAllEquipments() {
        Log.i(TAG, "getAllEquipments");
        List<Equipment> equipments = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.query(SQLiteBaseDaoFactory.TABLE_EQUIPMENT,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Equipment equipment = cursorToEquipment(cursor);
            equipments.add(equipment);
            cursor.moveToNext();
        }
        cursor.close();
        return equipments;
    }

    synchronized public List<Equipment> getEquipmentsByDescription(String description) {
        Log.i(TAG, "getEquipmentsByDescription: description=" + description);
        List<Equipment> equipments = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from table_equipment where description like ?",
                new String[]{"%" + description + "%"});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Equipment equipment = cursorToEquipment(cursor);
            equipments.add(equipment);
            cursor.moveToNext();
        }
        cursor.close();
        return equipments;
    }

    synchronized private Equipment cursorToEquipment(Cursor cursor){
        Log.i(TAG, "cursorToEquipment: cursor=" + cursor
                + " cursorPosition=" + cursor.getPosition());
        Equipment equipment = new Equipment();
        equipment.setId(Long.valueOf(cursor.getInt(NUM_COL_ID)));
        equipment.setReference(cursor.getString(NUM_COL_REFERENCE));
        equipment.setCategory(cursor.getString(NUM_COL_CATEGORY));
        equipment.setBrand(cursor.getString(NUM_COL_BRAND));
        equipment.setModel(cursor.getString(NUM_COL_MODEL));
        equipment.setModelYear(cursor.getInt(NUM_COL_MODEL_YEAR));
        equipment.setHeating(cursor.getString(NUM_COL_HEATING));
        equipment.setDescription(cursor.getString(NUM_COL_DESCRIPTION));
        equipment.setReleaseDate(Date.valueOf(cursor.getString(NUM_COL_RELEASE_DATE)));
        equipment.setPrice(cursor.getDouble(NUM_COL_PRICE));
        equipment.setFormattedPrice(cursor.getString(NUM_COL_FORMATTED_PRICE));

        Log.i(TAG, "cursorToEquipment: cursorPosition=" + cursor.getPosition() + " equipment=" + equipment.getDescription());
        return equipment;
    }

    synchronized public long insertEquipment(Equipment equipment){
        Log.i(TAG, "insertEquipment: id=" + equipment.getId()
                + " description=" + equipment.getDescription()
                + " category=" + equipment.getCategory());

        ContentValues values = new ContentValues();
        values.put(SQLiteBaseDaoFactory.COL_ID, equipment.getId());
        values.put(SQLiteBaseDaoFactory.COL_REFERENCE, equipment.getReference());
        values.put(SQLiteBaseDaoFactory.COL_CATEGORY, equipment.getCategory());
        values.put(SQLiteBaseDaoFactory.COL_BRAND, equipment.getBrand());
        values.put(SQLiteBaseDaoFactory.COL_MODEL, equipment.getModel());
        values.put(SQLiteBaseDaoFactory.COL_MODEL_YEAR, equipment.getModelYear());
        values.put(SQLiteBaseDaoFactory.COL_HEATING, equipment.getHeating());
        values.put(SQLiteBaseDaoFactory.COL_DESCRIPTION, equipment.getDescription());
        values.put(SQLiteBaseDaoFactory.COL_RELEASE_DATE, equipment.getReleaseDate().toString());
        values.put(SQLiteBaseDaoFactory.COL_PRICE, equipment.getPrice());
        values.put(SQLiteBaseDaoFactory.COL_FORMATTED_PRICE, equipment.getFormattedPrice());
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.insert(SQLiteBaseDaoFactory.TABLE_EQUIPMENT, null, values);
        }
        else {
            open();
            Log.d(TAG, "insertMroRequest: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.insert(SQLiteBaseDaoFactory.TABLE_EQUIPMENT, null, values);
        }
    }

    synchronized public int updateEquipment(int id, Equipment equipment){
        Log.i(TAG, "updateCause: description=" + equipment.getDescription());
        ContentValues values = new ContentValues();
        values.put(SQLiteBaseDaoFactory.COL_ID, equipment.getId());
        values.put(SQLiteBaseDaoFactory.COL_REFERENCE, equipment.getReference());
        values.put(SQLiteBaseDaoFactory.COL_CATEGORY, equipment.getCategory());
        values.put(SQLiteBaseDaoFactory.COL_BRAND, equipment.getBrand());
        values.put(SQLiteBaseDaoFactory.COL_MODEL, equipment.getModel());
        values.put(SQLiteBaseDaoFactory.COL_MODEL_YEAR, equipment.getModelYear());
        values.put(SQLiteBaseDaoFactory.COL_HEATING, equipment.getHeating());
        values.put(SQLiteBaseDaoFactory.COL_DESCRIPTION, equipment.getDescription());
        values.put(SQLiteBaseDaoFactory.COL_RELEASE_DATE, equipment.getReleaseDate().toString());
        values.put(SQLiteBaseDaoFactory.COL_PRICE, equipment.getPrice());
        values.put(SQLiteBaseDaoFactory.COL_FORMATTED_PRICE, equipment.getFormattedPrice());
        return sqLiteDatabase.update(SQLiteBaseDaoFactory.TABLE_EQUIPMENT, values, SQLiteBaseDaoFactory.COL_ID + " = " + id, null);
    }

    synchronized public int removeEquipmentWithID(int id){
        return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_EQUIPMENT, SQLiteBaseDaoFactory.COL_ID + " = " + id, null);
    }

    synchronized public Equipment getEquipmentWithDescription(String description){
        Log.i(TAG, "getEquipmentWithDescription: description=" + description);
        Cursor c = sqLiteDatabase.rawQuery("select * from table_equipment where description = ?", new String[]{description});
        return cursorToEquipment(c);
    }
}
