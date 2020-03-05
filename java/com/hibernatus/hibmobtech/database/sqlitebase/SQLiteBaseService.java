package com.hibernatus.hibmobtech.database.sqlitebase;

/**
 * Created by Eric on 23/01/2016.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

// c l a s s   S Q L i t e B a s e S e r v i c e
// ---------------------------------------------

public class SQLiteBaseService extends Service {
    //public static final String TAG = SQLiteBaseService.class.getSimpleName();
    //static Timer updateDataBasetimer = new Timer();
    //SQLiteBaseDaoFactory SQLiteBaseDaoFactory;

    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------

    @Override
    public void onCreate() {
        //Log.i(TAG, "onCreate");

        /*
         * decommenter la ligne si dessous pour purger la base de données
         */
        //getApplicationContext().deleteDatabase(HibmobtechApplication.getSqliteBaseName());

        ///SQLiteBaseDaoFactory = SQLiteBaseDaoFactory.getInstance(getApplicationContext());
        //updateDataBasetimer.schedule(new DelayedSQLiteBaseTimerTask(getApplicationContext()), 300, 300000);// 300000 is 5min, 30000 is 30s
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //Log.i(TAG, "onStartCommand: flags=" + flags + " startId=" + startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        //Log.i(TAG, "onDestroy");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
