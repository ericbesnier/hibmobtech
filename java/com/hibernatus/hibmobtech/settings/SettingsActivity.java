package com.hibernatus.hibmobtech.settings;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import com.hibernatus.hibmobtech.HibmobtechActivity;
import com.hibernatus.hibmobtech.MainActivity;
import com.hibernatus.hibmobtech.R;


/**
 * Created by tgo on 19/11/15.
 */
public class SettingsActivity extends HibmobtechActivity {

    private static final String TAG = SettingsActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new SettingsFragment())
                .commit();
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onResume() {
        super.onResume();
        trackScreen(TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i(TAG, "onOptionsItemSelected: case home: getClass=" + getClass());
                if(getClass().equals(MainActivity.class)){
                    DrawerLayout drawerLayout = null;
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    finish();
                }
                break;
            default:
                return true;
        }
        return true;
    }

}