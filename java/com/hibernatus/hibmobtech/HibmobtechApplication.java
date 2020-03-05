package com.hibernatus.hibmobtech;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.webkit.WebView;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.hibernatus.hibmobtech.authenticator.PersistentCookieStore;
import com.hibernatus.hibmobtech.dataStore.DataStore;
import com.hibernatus.hibmobtech.database.couchbase.CouchBaseDaoFactory;
import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory;
import com.hibernatus.hibmobtech.model.dao.DaoFactory;
import com.hibernatus.hibmobtech.network.RestClient;
import com.hibernatus.hibmobtech.observable.SettingsObservable;
import com.hibernatus.hibmobtech.observer.SettingsObserver;
import com.squareup.okhttp.OkHttpClient;

import java.net.CookieStore;
import java.util.Arrays;
import java.util.List;

// c l a s s   H i b m o b t e c h A p p l i c a t i o n
// -----------------------------------------------------

public class HibmobtechApplication extends Application {

    // s t a t i c   A C C O U N T
    // ---------------------------
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".db"; // The authority for the sync adapter's content provider
    public static final String ACCOUNT_TYPE = "example.com"; // An account type, in the form of a domain name
    public static final String ACCOUNT = "eric_account"; // The account name

    // s t a t i c   R E Q U E S T
    // ---------------------------
    public static final int CAUSE_REQUEST = 0;
    public static final int TASK_REQUEST = 1;
    public static final int MACHINE_REQUEST = 2;
    public static final int SITE_REQUEST = 3;
    public static final int EQUIPMENT_REQUEST = 4;
    public static final int COMMENT_REQUEST = 6;
    public static final int SETTINGS_REQUEST = 7;
    public static final int MACHINE_ASSOCIATE_REQUEST = 8;
    public static final int TAKE_PICTURE_REQUEST = 9;
    public static final int CREATE_PICTURE_REQUEST = 10;
    public static final int MACHINE_CREATE_REQUEST = 11;
    public static final int CREATE_SIGNATURE_REQUEST = 12;
    public static final int PICTURES_GRID_REQUEST = 13;
    public static final int PICTURES_GALLERY_REQUEST = 14;
    public static final int SPARE_PART_REQUEST = 15;
    public static final int PICASSO_GALLERY_REQUEST = 16;
    public static final int SEARCH_REQUEST = 17;

    // s t a t i c   P E R M I S S I O N   R E Q U E S T
    // -------------------------------------------------
    public static final int CHECK_WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 20;
    public static final int READ_LOGS_PERMISSION_REQUEST = 21;
    public static final int CAMERA_PERMISSION_REQUEST = 22;
    public static final int LOCATION_PERMISSION_REQUEST = 23;
    public static final int CHECK_READ_EXTERNAL_STORAGE_PERMISSION_REQUEST = 24;

    // s t a t i c   T I M E   &   D E L A Y
    // -------------------------------------
    // TODO A MODIFIER AVANT LIVRAISON !!!
    public static long THREE_SEC = 3000; //  3 000 is 3s
    public static long ONE_MIN = 60000; //  60 000 is 60 s -> 1min
    public static long FIVE_MIN = 300000; //  300 000 is 5min
    public static long THIRTY_SEC = 30000; // 30 000 is 30s
    public static long TIMER_TASK_DELAY = THREE_SEC; // amount of time in milliseconds before first execution.
    public static long TIMER_TASK_DELAY_PERIOD = ONE_MIN; // amount of time in milliseconds between subsequent executions

    // s t a t i c   S I Z E
    // -------------------------------------------------
    //public static final int PICTURE_WEIGHT = 1500000; // size max for image to server : 1,5 Mo
    //public static final int PICTURE_WEIGHT = 150000; // size max for image to server : 150 Ko
    public static final int PICTURE_WEIGHT = 100000; // size max for image to server : 100 Ko
    public static int PICTURE_HEIGHT = 1200;
    public static int THUMBNAIL_HEIGHT = 400;
    public static final int PICASSO_PICTURE_WIDTH = 1024/2;
    public static final int PICASSO_PICTURE_HEIGHT = 768/2;
    public static final int SIGNATURE_WEIGHT = 56000; // size max for signature to server : 56 Ko

    // i n t e n t   i t e m   d a t a   n a m e
    // -----------------------------------------
    public static final String PICTURE_PATH = "PICTURE_PATH";

    // O t h e r s   i n t   s t a t i c
    // ---------------------------------------
    public static final int MAX_RESEND = 3; // number of resend for pictures in table picture_cache & signature in table signature_cache
    public static final int NUM_OF_COLUMNS_PER_SCREEN = 3; // Number of columns of Grid View
    public static final int SQLITE_BASE_VERSION = 33;
    public static final int SIZE_OF_PAGE = 50;
    public static final int LOADER_FOR_PICTURES_ID = 0; // id A unique identifier for this loader
    public static final int LOCATION_NOTIFICATION_ID = 0; // An identifier for this location notification unique within the application.

    // O t h e r s   S t r i n g   s t a t i c
    // ---------------------------------------
    public static final String TAG = HibmobtechApplication.class.getSimpleName();
    public static final String TEST_FAIRY_APP_TOKEN = "15f3023b29caa9a048d5959aeb649c2e03b1db50";
    public static final String PHOTO_ALBUM = "NAT";  // SD card image directory
    public static final String SQLITE_BASE_NAME = "hibmobtech.db";
    public static final String COUCH_BASE_NAME = "hibmobtech.cb";
    public static final String UNKNOWN_MACHINE = "Pas de machine associée";
    public static final String MACHINE_CREATE_ACTIVITY = "MACHINE_CREATE_ACTIVITY";
    public static final String MACHINE_UPDATE_ACTIVITY = "MACHINE_UPDATE_ACTIVITY";

    //    s t a t i c   P R O J E C T I O N
    // ------------------------------------
    public static String[] PROJECTION_PICTURE = new String[] {
            SQLiteBaseDaoFactory.COL_ID,
            SQLiteBaseDaoFactory.COL_ID_MRO_REQUEST,
            SQLiteBaseDaoFactory.COL_IMAGE,
            SQLiteBaseDaoFactory.COL_THUMBNAIL,
            SQLiteBaseDaoFactory.COL_TITLE,
            SQLiteBaseDaoFactory.COL_TIME
    };

    // O t h e r s   s t a t i c
    // -------------------------
    public static final List<String> FILE_EXTN = Arrays.asList("jpg", "jpeg", "png"); // supported file formats
    protected static DaoFactory daoFactory;
    protected static PackageInfo packageInfo;
    protected static CookieStore cookieStore; // Store the session cookies (JSESSIONID, XSRF_TOKEN). Allows auto-authentication even if app is closed
    protected static DataStore dataStore; // Store some user data. Not much for the timebeing: only username
    protected static RestClient restClient; // Main REST Api entry point
    protected static HibmobtechConfig config; // App config: Rest endpoint URL
    protected static SQLiteBaseDaoFactory hibmobtechSQLiteBaseDaoFactory;
    protected static HibmobtechApplication hibmobtechApplication;
    protected static int serviceStartId;
    protected static HibmobtechResources hibmobtechResources; // Some shared resources, like TypeFace Awesome fonts
    protected static Activity currentActivity;
    protected static LocationManager locationManager;
    protected static Boolean locationEnable;

    // e n u m
    // -------
    public enum OriginOfData {
        DATABASE, SERVER;
    }

    public enum DirQualityPath {
        HDPI {
            public String toString() {
                return "/Hdpi";
            }
        },
        MDPI {
            public String toString() {
                return "/Mdpi";
            }
        },
        LDPI {
            public String toString() {
                return "/Ldpi";
            }
        }
    }

    // O t h e r s
    // -----------
    protected Tracker tracker;
    protected NotificationManager notificationManager;
    protected SettingsObserver settingsObserver;
    protected SettingsObservable settingsObservable;
    public static OriginOfData dataLoadFrom = OriginOfData.DATABASE;

    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------
    @Override
    public void onCreate(){
        Log.d(TAG, "onCreate: hibmobtechApplication=" + getApplicationContext());
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }

        Stetho.initializeWithDefaults(this);
        OkHttpClient client = new OkHttpClient();
        client.networkInterceptors().add(new StethoInterceptor());

        hibmobtechApplication = this;

        notificationManager = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        locationManager = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        // Observer on Settings
        settingsObservable = new SettingsObservable();
        settingsObserver = new SettingsObserver(getApplicationContext());
        settingsObservable.addObserver(settingsObserver);

        cookieStore = new PersistentCookieStore(getApplicationContext());
        dataStore = new DataStore(getApplicationContext());
        config = new HibmobtechConfig(getApplicationContext());
        restClient = new RestClient(getApplicationContext(), cookieStore);
        hibmobtechResources = new HibmobtechResources(getApplicationContext());
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.toString());
        }

        daoFactory = CouchBaseDaoFactory.getInstance(getApplicationContext());
    }

    // O t h e r s   m e t h o d s
    // ---------------------------

    /**
     * Gets the default {@link Tracker} for this {@link Application}.
     * @return tracker
     */
    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(R.xml.app_tracker);
            // You only need to updateCause User ID on a tracker once. By setting it on the
            // tracker, the ID will be sent with all subsequent hits.
            Long userId = HibmobtechApplication.getDataStore().getUserId();
            tracker.setClientId(String.valueOf(userId));

            // This hit will be sent with the User ID value and be visible in
            // User-ID-enabled views (profiles).
            tracker.send(new HitBuilders.EventBuilder()
                    .setCategory("UX")
                    .setAction("User Sign In")
                    .build());
        }
        return tracker;
    }

    // return true if mobile is online, else return false
    //
    public boolean isOnline() {
        boolean isOnline;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isOnline =  (networkInfo != null && networkInfo.isConnected());
        if(isOnline)
            Log.d(TAG, "!!!! onLine !!!!");
        else
            Log.d(TAG, "!!!! offLine !!!!");
        return isOnline;
    }

    // S t a t i c   m e t h o d s
    // ---------------------------
    public static boolean getLocationEnableOld(){
        Log.d(TAG, "getLocationEnableOld");
        boolean returnValue;
        boolean isGpsProviderEnabled = false;
        boolean isNetworkProviderEnabled = false;
        try {
            isGpsProviderEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex){}
        try {
            isNetworkProviderEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex){}
        if(!isGpsProviderEnabled || !isNetworkProviderEnabled) {
            Log.d(TAG, "getLocationEnableOld: Location Providers are DISABLE");
            HibmobtechApplication.getInstance().getSettingsObservable()
                    .setLocationEnabled(false);
            returnValue = false;
        }
        else{
            Log.d(TAG, "getLocationEnableOld: Location Providers are ENABLE");
            HibmobtechApplication.getInstance().getSettingsObservable()
                    .setLocationEnabled(true);
            returnValue = true;
        }
        return returnValue;
    }

    // s t a t i c  G e t t e r s   &   S e t t e r s
    // ----------------------------------------------
    public static Boolean getLocationEnable() {
        return locationEnable;
    }

    public static void setLocationEnable(Boolean locationEnable) {
        HibmobtechApplication.locationEnable = locationEnable;
    }

    public static void setServiceStartId(int serviceStartId) { HibmobtechApplication.serviceStartId = serviceStartId; }

    public static PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public static synchronized HibmobtechApplication getInstance() {return hibmobtechApplication; }

    public static SQLiteBaseDaoFactory getHibmobtechSQLiteBaseDaoFactory() {return hibmobtechSQLiteBaseDaoFactory;}

    public static void setHibmobtechSQLiteBaseDaoFactory(SQLiteBaseDaoFactory hibmobtechSQLiteBaseDaoFactory) {
        HibmobtechApplication.hibmobtechSQLiteBaseDaoFactory = hibmobtechSQLiteBaseDaoFactory;
    }

    public static int getSqliteBaseVersion() {
        return SQLITE_BASE_VERSION;
    }

    public static String getSqliteBaseName() {
        return SQLITE_BASE_NAME;
    }

    public static String getCouchBaseName() {
        return COUCH_BASE_NAME;
    }

    public static CookieStore getCookieStore() {
        return cookieStore;
    }

    public static HibmobtechConfig getConfig() {
        return config;
    }

    public static DataStore getDataStore() {
        return dataStore;
    }

    public static RestClient getRestClient() {
        return restClient;
    }

    public static HibmobtechResources getHibmobtechResources() { return hibmobtechResources; }

    public static int getSizeOfPage() {
        return SIZE_OF_PAGE;
    }

    public static DaoFactory getDaoFactory() {
        return daoFactory;
    }

    public static int getServiceStartId() {
        return serviceStartId;
    }

    public static LocationManager getLocationManager() {
        return locationManager;
    }

    public static void setLocationManager(LocationManager locationManager) {
        HibmobtechApplication.locationManager = locationManager;
    }

    // O t h e r s   G e t t e r s   &   S e t t e r s
    // -----------------------------------------------
    public Activity getCurrentActivity(){
        Log.d(TAG, "getCurrentActivity:currentActivity=" + currentActivity);
        return currentActivity;
    }

    public void setCurrentActivity(Activity currentActivity){
        Log.d(TAG, "setCurrentActivity:currentActivity=" + currentActivity);
        this.currentActivity = currentActivity;
    }

    public SettingsObserver getSettingsObserver() {
        return settingsObserver;
    }

    public void setSettingsObserver(SettingsObserver settingsObserver) {
        this.settingsObserver = settingsObserver;
    }

    public SettingsObservable getSettingsObservable() {
        return settingsObservable;
    }

    public void setSettingsObservable(SettingsObservable settingsObservable) {
        this.settingsObservable = settingsObservable;
    }

    public NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public void setNotificationManager(NotificationManager notificationManager) {
        this.notificationManager = notificationManager;
    }
}