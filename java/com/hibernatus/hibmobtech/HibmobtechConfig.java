package com.hibernatus.hibmobtech;

/**
 * Created by tgo on 04/11/15.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by tgo on 04/11/15.
 *
 * This class load properties from assets/config.properties
 * Add some props when needed...
 */

// c l a s s   H i b m o b t e c h C o n f i g
// -------------------------------------------

public class HibmobtechConfig {
    final static String TAG = HibmobtechConfig.class.getSimpleName();
    public static final String PREF_HIBBIZ_API_URL = "pref_hibbizApiUrl";
    public static final String PREF_HIBBIZ_API_SURL = "pref_hibbizApiSUrl";

    private Context context;
    String defaultApiUrl;
    String defaultApiSUrl;

    public HibmobtechConfig(Context context) {
        this.context = context;
        init("config.properties");
    }

    private void init(String fileName) {

        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open(fileName);
            Properties props = new Properties();
            props.load(inputStream);
            this.defaultApiUrl = props.getProperty("hibbiz.gwUrl", "http://localhost:9080");
            this.defaultApiSUrl = props.getProperty("hibbiz.gwSUrl", "https://localhost:9080");
        }
        catch (IOException e) {
            Log.e(TAG, e.toString());
            //HibmobtechApplication.getInstance().trackException(e);
        }
    }

    public String getApiUrl() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String apiUrl = pref.getString(PREF_HIBBIZ_API_URL, defaultApiUrl);
        return apiUrl;
    }

    public String getApiSUrl() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String apiUrl = pref.getString(PREF_HIBBIZ_API_SURL, defaultApiSUrl);
        return apiUrl;
    }
}