package com.hibernatus.hibmobtech.database.sqlitebase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.model.dao.CauseDao;
import com.hibernatus.hibmobtech.model.dao.DaoFactory;

/**
 * Created by Eric on 23/01/2016.
 */
public class SQLiteBaseDaoFactory extends SQLiteOpenHelper implements DaoFactory {

    protected Context context;
    protected String name;
    protected int version;

    protected SQLiteDatabase sqLiteDatabase;

    private static SQLiteBaseDaoFactory INSTANCE = null;

    public static final String TAG = SQLiteBaseDaoFactory.class.getSimpleName();

    public static final String TABLE_CAUSE = "table_cause";
    public static final String TABLE_TASK = "table_task";
    public static final String TABLE_MRO_REQUEST = "table_mro_request";
    public static final String TABLE_MACHINE = "table_machine";
    public static final String TABLE_EQUIPMENT = "table_equipment";
    public static final String TABLE_TRACKING_LOCATION = "table_tracking_location";
    public static final String TABLE_PICTURE = "table_picture";
    public static final String TABLE_PICTURE_CACHE = "table_picture_cache";
    public static final String TABLE_SIGNATURE = "table_signature";
    public static final String TABLE_SIGNATURE_CACHE = "table_signature_cache";

    public static final String COL_ID = "_id";
    public static final String COL_DESCRIPTION = "description";
    public static final String COL_CATEGORY = "category";
    public static final String COL_SERIAL_NUMBER = "serial_number";
    public static final String COL_EQUIPMENT_ID = "equipment_id";
    public static final String COL_SITE_ID = "site_id";
    public static final String COL_PURCHASE_DATE = "purchase_date";
    public static final String COL_PURCHASE_PRICE = "purchase_price";
    public static final String COL_INSTALL_DATE = "install_date";
    public static final String COL_COMMENT = "comment";
    public static final String COL_NUM_MACHINE = "num_machine";
    public static final String COL_REFERENCE = "reference";
    public static final String COL_BRAND = "brand";
    public static final String COL_MODEL = "model";
    public static final String COL_MODEL_YEAR = "model_year";
    public static final String COL_HEATING = "heating";
    public static final String COL_RELEASE_DATE = "release_date";
    public static final String COL_PRICE = "price";
    public static final String COL_FORMATTED_PRICE = "formatted_price";
    public static final String COL_LATITUDE = "latitude";
    public static final String COL_LONGITUDE = "longitude";
    public static final String COL_TIME = "time";
    public static final String COL_ALTITUDE = "altitude";
    public static final String COL_ACCURACY = "accuracy";
    public static final String COL_SPEED = "speed";
    public static final String COL_BEARING = "bearing";
    public static final String COL_THUMBNAIL = "thumbnail";
    public static final String COL_IMAGE = "image";
    public static final String COL_ID_MRO_REQUEST = "id_mro_request";
    public static final String COL_TITLE = "title";
    public static final String COL_SIGNATURE_FILE = "signature_file";
    public static final String COL_SIGNER_NAME = "signer_name";
    public static final String COL_SIGNER_ROLE = "signer_role";
    public static final String COL_TIMESTAMP = "timestamp";
    public static final String COL_RESEND_NUMBER = "resend_number";





    public static final String CREATE_TABLE_CAUSE = "CREATE TABLE " + TABLE_CAUSE
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_DESCRIPTION + " TEXT NOT NULL, "
            + COL_CATEGORY + " TEXT);";

    public static final String CREATE_TABLE_TASK = "CREATE TABLE " + TABLE_TASK
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_DESCRIPTION + " TEXT NOT NULL, "
            + COL_CATEGORY + " TEXT);";

    public static final String CREATE_TABLE_MRO_REQUEST = "CREATE TABLE " + TABLE_MRO_REQUEST
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY);";

    public static final String CREATE_TABLE_MACHINE = "CREATE TABLE " + TABLE_MACHINE
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_SERIAL_NUMBER + " TEXT, "
            + COL_EQUIPMENT_ID + " TEXT, "
            + COL_SITE_ID + " TEXT, "
            + COL_PURCHASE_DATE + " TEXT, "
            + COL_PURCHASE_PRICE + " TEXT, "
            + COL_INSTALL_DATE + " TEXT, "
            + COL_COMMENT + " TEXT, "
            + COL_NUM_MACHINE + " TEXT);";

    public static final String CREATE_TABLE_EQUIPMENT = "CREATE TABLE " + TABLE_EQUIPMENT
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_REFERENCE + " TEXT, "
            + COL_CATEGORY + " TEXT, "
            + COL_BRAND + " TEXT, "
            + COL_MODEL + " TEXT, "
            + COL_MODEL_YEAR + " INTEGER, "
            + COL_HEATING + " TEXT, "
            + COL_DESCRIPTION + " TEXT, "
            + COL_RELEASE_DATE + " TEXT, "
            + COL_PRICE + " TEXT, "
            + COL_FORMATTED_PRICE + " TEXT);";

    public static final String CREATE_TABLE_TRACKING_LOCATION = "CREATE TABLE " + TABLE_TRACKING_LOCATION
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_LATITUDE + " TEXT, "
            + COL_LONGITUDE + " TEXT, "
            + COL_TIME + " TEXT, "
            + COL_ALTITUDE + " TEXT, "
            + COL_ACCURACY + " TEXT, "
            + COL_SPEED + " TEXT, "
            + COL_BEARING + " TEXT);";

/*    public static final String CREATE_TABLE_PICTURE_CACHE = "CREATE TABLE " + TABLE_PICTURE_CACHE
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_ID_MRO_REQUEST + " INTEGER, "
            + COL_IMAGE + " BLOB, "
            + COL_THUMBNAIL + " BLOB, "
            + COL_TITLE + " TEXT, "
            + COL_DESCRIPTION + " TEXT, "
            + COL_TIME + " INTEGER, "
            + COL_RESEND_NUMBER + " INTEGER);";


    public static final String CREATE_TABLE_PICTURE = "CREATE TABLE " + TABLE_PICTURE
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_ID_MRO_REQUEST + " INTEGER, "
            + COL_IMAGE + " BLOB, "
            + COL_THUMBNAIL + " BLOB, "
            + COL_TITLE + " TEXT, "
            + COL_DESCRIPTION + " TEXT, "
            + COL_TIME + " INTEGER, "
            + COL_RESEND_NUMBER + " INTEGER);";*/

    public static final String CREATE_TABLE_PICTURE_CACHE = "CREATE TABLE " + TABLE_PICTURE_CACHE
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_ID_MRO_REQUEST + " INTEGER, "
            + COL_IMAGE + " BLOB, "
            + COL_THUMBNAIL + " BLOB, "
            + COL_TITLE + " TEXT, "
            + COL_TIME + " INTEGER, "
            + COL_RESEND_NUMBER + " INTEGER);";


    public static final String CREATE_TABLE_PICTURE = "CREATE TABLE " + TABLE_PICTURE
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_ID_MRO_REQUEST + " INTEGER, "
            + COL_IMAGE + " BLOB, "
            + COL_THUMBNAIL + " BLOB, "
            + COL_TITLE + " TEXT, "
            + COL_TIME + " INTEGER, "
            + COL_RESEND_NUMBER + " INTEGER);";

    public static final String CREATE_TABLE_SIGNATURE = "CREATE TABLE " + TABLE_SIGNATURE
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_ID_MRO_REQUEST + " INTEGER, "
            + COL_SIGNATURE_FILE + " BLOB, "
            + COL_SIGNER_NAME + " TEXT, "
            + COL_SIGNER_ROLE + " TEXT, "
            + COL_TIMESTAMP + " TEXT, "
            + COL_RESEND_NUMBER + " INTEGER);";

    public static final String CREATE_TABLE_SIGNATURE_CACHE = "CREATE TABLE " + TABLE_SIGNATURE_CACHE
            + " ("
            + COL_ID + " INTEGER PRIMARY KEY, "
            + COL_ID_MRO_REQUEST + " INTEGER, "
            + COL_SIGNATURE_FILE + " BLOB, "
            + COL_SIGNER_NAME + " TEXT, "
            + COL_SIGNER_ROLE + " TEXT, "
            + COL_TIMESTAMP + " TEXT, "
            + COL_RESEND_NUMBER + " INTEGER);";


    public static final String DROP_TABLE_CAUSE = "DROP TABLE IF EXISTS " + TABLE_CAUSE + ";";
    public static final String DROP_TABLE_TASK = "DROP TABLE IF EXISTS " + TABLE_TASK + ";";
    public static final String DROP_TABLE_MRO_REQUEST = "DROP TABLE IF EXISTS " + TABLE_MRO_REQUEST + ";";
    public static final String DROP_TABLE_MACHINE = "DROP TABLE IF EXISTS " + TABLE_MACHINE + ";";
    public static final String DROP_TABLE_EQUIPMENT = "DROP TABLE IF EXISTS " + TABLE_EQUIPMENT + ";";
    public static final String DROP_TABLE_TRACKING_LOCATION = "DROP TABLE IF EXISTS " + TABLE_TRACKING_LOCATION + ";";
    public static final String DROP_TABLE_PICTURE = "DROP TABLE IF EXISTS " + TABLE_PICTURE + ";";
    public static final String DROP_TABLE_PICTURE_CACHE = "DROP TABLE IF EXISTS " + TABLE_PICTURE_CACHE + ";";
    public static final String DROP_TABLE_SIGNATURE = "DROP TABLE IF EXISTS " + TABLE_SIGNATURE + ";";
    public static final String DROP_TABLE_SIGNATURE_CACHE = "DROP TABLE IF EXISTS " + TABLE_SIGNATURE_CACHE + ";";

    synchronized public static SQLiteBaseDaoFactory getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new SQLiteBaseDaoFactory(context);
        }
        return INSTANCE;
    }

    public SQLiteBaseDaoFactory(Context context) {
        super(context, HibmobtechApplication.getSqliteBaseName(), null, HibmobtechApplication.getSqliteBaseVersion());
        Log.i(TAG, "Constructor");
        this.context = context;
        this.name = HibmobtechApplication.getSqliteBaseName();
        this.version = HibmobtechApplication.getSqliteBaseVersion();
    }

    @Override
    synchronized public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate");
        sqLiteDatabase.execSQL(CREATE_TABLE_CAUSE);
        sqLiteDatabase.execSQL(CREATE_TABLE_TASK);
        sqLiteDatabase.execSQL(CREATE_TABLE_MRO_REQUEST);
        sqLiteDatabase.execSQL(CREATE_TABLE_MACHINE);
        sqLiteDatabase.execSQL(CREATE_TABLE_EQUIPMENT);
        sqLiteDatabase.execSQL(CREATE_TABLE_TRACKING_LOCATION);
        sqLiteDatabase.execSQL(CREATE_TABLE_PICTURE);
        sqLiteDatabase.execSQL(CREATE_TABLE_PICTURE_CACHE);
        sqLiteDatabase.execSQL(CREATE_TABLE_SIGNATURE);
        sqLiteDatabase.execSQL(CREATE_TABLE_SIGNATURE_CACHE);
        this.sqLiteDatabase = sqLiteDatabase;
        HibmobtechApplication.setHibmobtechSQLiteBaseDaoFactory(this);
    }

    @Override
    synchronized public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
        dropAllTables(sqLiteDatabase);
        onCreate(sqLiteDatabase);
    }

    synchronized public void dropAllTables(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "dropAllTables");
        sqLiteDatabase.execSQL(DROP_TABLE_CAUSE);
        sqLiteDatabase.execSQL(DROP_TABLE_TASK);
        sqLiteDatabase.execSQL(DROP_TABLE_MRO_REQUEST);
        sqLiteDatabase.execSQL(DROP_TABLE_MACHINE);
        sqLiteDatabase.execSQL(DROP_TABLE_EQUIPMENT);
        sqLiteDatabase.execSQL(DROP_TABLE_TRACKING_LOCATION);
        sqLiteDatabase.execSQL(DROP_TABLE_PICTURE);
        sqLiteDatabase.execSQL(DROP_TABLE_PICTURE_CACHE);
        sqLiteDatabase.execSQL(DROP_TABLE_SIGNATURE);
        sqLiteDatabase.execSQL(DROP_TABLE_SIGNATURE_CACHE);
    }

    @Override
    public CauseDao getCauseDao() {
        Log.d(TAG, "getCauseDao");
        SQLiteBaseDaoCause sqLiteBaseDaoCause = new SQLiteBaseDaoCause(context);
        return sqLiteBaseDaoCause;
    }
}
