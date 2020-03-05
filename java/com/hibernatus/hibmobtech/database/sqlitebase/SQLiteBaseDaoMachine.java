package com.hibernatus.hibmobtech.database.sqlitebase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.hibernatus.hibmobtech.model.Machine;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

// TODO supprimer import java.sql.Date;

/**
 * Created by Eric on 23/01/2016.
 */
public class SQLiteBaseDaoMachine {
    public static final String TAG = SQLiteBaseDaoMachine.class.getSimpleName();


    private static final int NUM_COL_ID = 0;
    private static final int NUM_COL_SERIAL_NUMBER = 1;
    private static final int NUM_COL_EQUIPMENT_ID = 2;
    private static final int NUM_COL_SITE_ID = 3;
    private static final int NUM_COL_PURCHASE_DATE = 4;
    private static final int NUM_COL_PURCHASE_PRICE = 5;
    private static final int NUM_COL_INSTALL_DATE = 6;
    private static final int NUM_COL_COMMENTS = 7;
    private static final int NUM_COL_NUM_MACHINE = 8;

    private SQLiteDatabase sqLiteDatabase;
    private SQLiteBaseDaoFactory sqLiteBaseDaoFactory;

    private String[] allColumns = {
            SQLiteBaseDaoFactory.COL_ID,
            SQLiteBaseDaoFactory.COL_SERIAL_NUMBER,
            SQLiteBaseDaoFactory.COL_EQUIPMENT_ID,
            SQLiteBaseDaoFactory.COL_SITE_ID,
            SQLiteBaseDaoFactory.COL_PURCHASE_DATE,
            SQLiteBaseDaoFactory.COL_PURCHASE_PRICE,
            SQLiteBaseDaoFactory.COL_INSTALL_DATE,
            SQLiteBaseDaoFactory.COL_COMMENT,
            SQLiteBaseDaoFactory.COL_NUM_MACHINE };

    public SQLiteBaseDaoMachine(Context context){
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
        Log.i(TAG, "deleteAllCauses");
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_MACHINE, null, null);
        }
        else {
            open();
            Log.d(TAG, "deleteAllCauses: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_MACHINE, null, null);
        }
    }

    synchronized public List<Machine> getAllMachines() {
        Log.i(TAG, "getAllMachines");
        List<Machine> machines = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.query(SQLiteBaseDaoFactory.TABLE_MACHINE,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Machine machine = cursorToMachine(cursor);
            machines.add(machine);
            cursor.moveToNext();
        }
        cursor.close();
        return machines;
    }

    synchronized public List<Machine> getMachinesBySerialNumber(String serial_number) {
        Log.i(TAG, "getMachinesBySerialNumber: serial_number=" + serial_number);
        List<Machine> machines = new ArrayList<>();

        Cursor cursor = sqLiteDatabase.rawQuery("select * from table_machine where serial_number like ?",
                new String[]{"%" + serial_number + "%"});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Machine machine = cursorToMachine(cursor);
            machines.add(machine);
            cursor.moveToNext();
        }
        cursor.close();
        return machines;
    }

    synchronized private Machine cursorToMachine(Cursor cursor){
        Log.i(TAG, "cursorToMachine: cursor=" + cursor
                + " cursorPosition=" + cursor.getPosition());
        Machine machine = new Machine();
        machine.setId(Long.valueOf(cursor.getInt(NUM_COL_ID)));
        machine.setSerialNumber(cursor.getString(NUM_COL_SERIAL_NUMBER));
        machine.getEquipment().setId(Long.valueOf(cursor.getString(NUM_COL_EQUIPMENT_ID)));
        machine.getSite().setId(Long.valueOf(cursor.getString(NUM_COL_SITE_ID)));
        machine.setPurchaseDate(Date.valueOf(cursor.getString(NUM_COL_PURCHASE_DATE)));
        machine.setPurchasePrice(Double.valueOf(cursor.getString(NUM_COL_PURCHASE_PRICE)));
        machine.setInstallDate(Date.valueOf(cursor.getString(NUM_COL_INSTALL_DATE)));
        machine.setComments(cursor.getString(NUM_COL_COMMENTS));
        machine.setNumMachine(cursor.getString(NUM_COL_NUM_MACHINE));

        Log.i(TAG, "cursorToMachine: cursorPosition=" + cursor.getPosition()
                + " updatedMachine.getSerialNumber()=" + machine.getSerialNumber());
        return machine;
    }

    synchronized public long insertMachine(Machine machine){
        Log.i(TAG, "insertMachine: id=" + machine.getId()
                + " serial_number=" + machine.getSerialNumber()
                + " equipment_id=" + machine.getEquipment().getId()
                + " site_id=" + machine.getSite().getId()
                + " purchase_date=" + machine.getPurchaseDate()
                + " purchase_price=" + machine.getPurchasePrice()
                + " install_date=" + machine.getInstallDate()
                + " comments=" + machine.getComments()
                + " num_machine=" + machine.getNumMachine());

        ContentValues values = new ContentValues();
        values.put(SQLiteBaseDaoFactory.COL_ID, machine.getId());
        values.put(SQLiteBaseDaoFactory.COL_SERIAL_NUMBER, machine.getSerialNumber());
        values.put(SQLiteBaseDaoFactory.COL_EQUIPMENT_ID, machine.getEquipment().getId());
        values.put(SQLiteBaseDaoFactory.COL_SITE_ID, machine.getSite().getId());
        values.put(SQLiteBaseDaoFactory.COL_PURCHASE_DATE, machine.getPurchaseDate().toString());
        values.put(SQLiteBaseDaoFactory.COL_PURCHASE_PRICE, machine.getPurchasePrice());
        values.put(SQLiteBaseDaoFactory.COL_INSTALL_DATE, machine.getInstallDate().toString());
        values.put(SQLiteBaseDaoFactory.COL_COMMENT, machine.getComments());
        values.put(SQLiteBaseDaoFactory.COL_NUM_MACHINE, machine.getNumMachine());
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.insert(SQLiteBaseDaoFactory.TABLE_MACHINE, null, values);
        }
        else {
            open();
            Log.d(TAG, "insertMachine: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.insert(SQLiteBaseDaoFactory.TABLE_MACHINE, null, values);
        }
    }

    synchronized public int updateMachine(int id, Machine machine){
        Log.i(TAG, "updateCause: serial_number=" + machine.getSerialNumber());
        ContentValues values = new ContentValues();
        values.put(SQLiteBaseDaoFactory.COL_ID, machine.getId());
        values.put(SQLiteBaseDaoFactory.COL_SERIAL_NUMBER, machine.getSerialNumber());
        values.put(SQLiteBaseDaoFactory.COL_EQUIPMENT_ID, machine.getEquipment().getId());
        values.put(SQLiteBaseDaoFactory.COL_SITE_ID, machine.getSite().getId());
        values.put(SQLiteBaseDaoFactory.COL_PURCHASE_DATE, machine.getPurchaseDate().toString());
        values.put(SQLiteBaseDaoFactory.COL_PURCHASE_PRICE, machine.getPurchasePrice());
        values.put(SQLiteBaseDaoFactory.COL_INSTALL_DATE, machine.getInstallDate().toString());
        values.put(SQLiteBaseDaoFactory.COL_COMMENT, machine.getComments());
        values.put(SQLiteBaseDaoFactory.COL_NUM_MACHINE, machine.getNumMachine());
        if (sqLiteDatabase.isOpen()){
            return sqLiteDatabase.update(SQLiteBaseDaoFactory.TABLE_MACHINE, values, SQLiteBaseDaoFactory.COL_ID + " = " + id, null);
        }
        else {
            open();
            Log.d(TAG, "updateMachine: sqLiteDatabase is closed, re-getWritableDatabase it !");
            return sqLiteDatabase.update(SQLiteBaseDaoFactory.TABLE_MACHINE, values, SQLiteBaseDaoFactory.COL_ID + " = " + id, null);
        }
    }

    synchronized public int removeMachineWithID(int id){
        return sqLiteDatabase.delete(SQLiteBaseDaoFactory.TABLE_MACHINE, SQLiteBaseDaoFactory.COL_ID + " = " + id, null);
    }

    synchronized public Machine getMachineWithSerialNumber(String serial_number){
        Log.i(TAG, "getMachineWithSerialNumber: serial_number=" + serial_number);
        Cursor c = sqLiteDatabase.rawQuery("select * from table_machine where serial_number = ?", new String[]{serial_number});
        return cursorToMachine(c);
    }
}
