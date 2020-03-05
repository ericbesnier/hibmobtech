package com.hibernatus.hibmobtech.about;

/**
 * Created by Eric on 04/04/2016.
 */

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.hibernatus.hibmobtech.HibmobtechActivity;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AboutActivity extends HibmobtechActivity {

    private static final String TAG = AboutActivity.class.getSimpleName();

    TextView packageNameTextView;
    TextView applicationNameTextView;
    TextView versionNumberTextView;
    TextView buildDateTextView;
    TextView deviceIdTextView;
    String androidDeviceId;

    static DateFormat dateFormat = new SimpleDateFormat();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.about_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        applicationNameTextView = (TextView)findViewById(R.id.applicationName_textView);
        packageNameTextView = (TextView)findViewById(R.id.packageName_textView);
        versionNumberTextView = (TextView)findViewById(R.id.versionNumber_textView);
        buildDateTextView = (TextView)findViewById(R.id.buildDate_textView);
        deviceIdTextView = (TextView)findViewById(R.id.deviceId_textView);
        androidDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    protected void onResume() {
        //trackScreen(TAG);
        packageNameTextView.setText(this.getPackageName());
        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo( this.getPackageName(), 0);
            String appName;
            if(applicationInfo != null) {
                appName = (String) getPackageManager().getApplicationLabel(applicationInfo);
            }
            else {
                appName = "(unknown)";
            }
            Log.i(TAG, "onResume: appName=" + appName);
            applicationNameTextView.setText(appName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.toString());
        }


        //try {
            // PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            PackageInfo packageInfo = HibmobtechApplication.getPackageInfo();
            versionNumberTextView.setText(packageInfo.versionName);
            //TextView applicationId = null;
            //applicationId.setText(packageInfo.versionName);
            Date bDate = new Date(packageInfo.lastUpdateTime);
            buildDateTextView.setText(dateFormat.format(bDate));
        /*} catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.toString());
            HibmobtechApplication.getInstance().trackException(e);
        }*/
        deviceIdTextView.setText(androidDeviceId);
        super.onResume();
    }

}

