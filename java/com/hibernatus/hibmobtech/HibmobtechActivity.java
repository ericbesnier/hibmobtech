package com.hibernatus.hibmobtech;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bleau.hibernatus.mob.util.AndroidUtils;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hibernatus.hibmobtech.about.AboutActivity;
import com.hibernatus.hibmobtech.authenticator.LoginActivity;
import com.hibernatus.hibmobtech.dataStore.DataStore;
import com.hibernatus.hibmobtech.machine.MachineActivity;
import com.hibernatus.hibmobtech.machine.MachineApiService;
import com.hibernatus.hibmobtech.machine.MachineCurrent;
import com.hibernatus.hibmobtech.model.Cause;
import com.hibernatus.hibmobtech.model.Machine;
import com.hibernatus.hibmobtech.model.Task;
import com.hibernatus.hibmobtech.network.retrofitError.DisplayRetrofitErrorManager;
import com.hibernatus.hibmobtech.observable.ActivityObservable;
import com.hibernatus.hibmobtech.observable.NetworkObservable;
import com.hibernatus.hibmobtech.observer.ActivityObserver;
import com.hibernatus.hibmobtech.observer.ContentObserverCallback;
import com.hibernatus.hibmobtech.observer.HibmobtechContentObserver;
import com.hibernatus.hibmobtech.observer.NetworkObserver;
import com.hibernatus.hibmobtech.settings.SettingsActivity;
import com.hibernatus.hibmobtech.site.SiteCurrent;
import com.hibernatus.hibmobtech.utils.NotificationUtils;
import com.hibernatus.hibmobtech.zxing.AnyOrientationCaptureActivity;
import com.testfairy.TestFairy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.provider.Settings.Secure.LOCATION_MODE;
import static android.provider.Settings.Secure.LOCATION_MODE_BATTERY_SAVING;
import static android.provider.Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
import static android.provider.Settings.Secure.LOCATION_MODE_OFF;
import static android.provider.Settings.Secure.LOCATION_MODE_SENSORS_ONLY;
import static com.hibernatus.hibmobtech.HibmobtechApplication.CAMERA_PERMISSION_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.CHECK_READ_EXTERNAL_STORAGE_PERMISSION_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.CHECK_WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.LOCATION_PERMISSION_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.READ_LOGS_PERMISSION_REQUEST;

/**
 * Created by Eric on 30/01/2016.
 */

// c l a s s   H i b m o b t e c h A c t i v i t y
// -------------------------------------------------------------------

public class HibmobtechActivity extends AppCompatActivity  implements
        Dialog.OnDismissListener,
        ContentObserverCallback {

    protected static final String TAG = HibmobtechActivity.class.getSimpleName();
    protected DataStore session;
    protected TextView userTextView;
    protected Handler handler;

    protected DisplayRetrofitErrorManager displayRetrofitErrorManager;

    protected boolean isGrantedLocationPermission;
    protected boolean isGrantedReadLogsPermission;
    protected boolean isGrantedCameraPermission;
    protected boolean isGrantedWriteExternalStorage;
    protected boolean isGrantedReadExternalStorage;

    protected ActivityObservable activityObservable;
    protected ActivityObserver activityObserver;
    protected NetworkObserver networkObserver;
    protected NetworkObservable networkObservable;
    public static HibmobtechContentObserver hibmobtechContentObserver;

    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected View headerView;
    protected ActionBar actionBar;
    protected Toolbar toolbar;
    protected Typeface hibmobtechFont;
    protected Menu drawerMenu;
    protected android.app.AlertDialog currentCheckLocationAndNetworkAlertDialog = null;
    protected InputMethodManager inputMethodManager;
    protected ActionBarDrawerToggle actionBarDrawerToggle;
    protected NotificationUtils notificationUtils;

    /** Stores the time (as per SystemClock.uptimeMillis()) of the last
     * call to onResume() in order to filter out touch events which occurred before
     * the activity was visible. */
    protected long resumeTime = 0;

    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        notificationUtils = new NotificationUtils();
        HibmobtechApplication.getInstance().setCurrentActivity(this);

        session = new DataStore(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            Log.i(TAG, "onCreate: User is NOT logged in");
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }

        handler = new Handler();
        hibmobtechFont = HibmobtechApplication.getHibmobtechResources().getHibmobtechFont();

        // Observer on Activity
        activityObserver = new ActivityObserver(this);
        activityObservable = new ActivityObservable();
        activityObservable.setActivity(this);
        activityObservable.setActivityState(ActivityObservable.State.ON_CREATE);
        activityObservable.addObserver(activityObserver);

        // Observer on Network
        networkObservable = new NetworkObservable();
        displayRetrofitErrorManager = new DisplayRetrofitErrorManager(this);
        networkObserver = new NetworkObserver(displayRetrofitErrorManager);
        networkObservable.addObserver(networkObserver);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        activityObservable.setActivity(this);
        activityObservable.setActivityState(ActivityObservable.State.ON_START);
        super.onStart();
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart");
        activityObservable.setActivity(this);
        activityObservable.setActivityState(ActivityObservable.State.ON_RESTART);
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        HibmobtechApplication.getInstance().setCurrentActivity(this);
        activityObservable.setActivity(this);
        activityObservable.setActivityState(ActivityObservable.State.ON_RESUME);

        if (!session.isLoggedIn()) {
            Log.d(TAG, "onCreate: User is NOT logged in");
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        }
        String user = HibmobtechApplication.getDataStore().getUserName();
        Log.d(TAG, "onResume: user=" + user);
        if (user != null) {
            if (userTextView != null) {
                userTextView.setText(user);
                TestFairy.identify(user);
            }
        }

        // Observer on the Content of Uri
        hibmobtechContentObserver = new HibmobtechContentObserver(this);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: this=" + this);
        activityObservable.setActivity(this);
        activityObservable.setActivityState(ActivityObservable.State.ON_PAUSE);

        if ((displayRetrofitErrorManager.getAlertDialog() != null) && displayRetrofitErrorManager.getAlertDialog().isShowing())
            displayRetrofitErrorManager.getAlertDialog().dismiss();
        displayRetrofitErrorManager.setAlertDialog(null);

        if ((displayRetrofitErrorManager.getBottomSheet() != null) && displayRetrofitErrorManager.getBottomSheet().isShowing())
            displayRetrofitErrorManager.getBottomSheet().dismiss();
        displayRetrofitErrorManager.setBottomSheet(null);

        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        activityObservable.setActivity(this);
        activityObservable.setActivityState(ActivityObservable.State.ON_STOP);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        activityObservable.setActivity(this);
        activityObservable.setActivityState(ActivityObservable.State.ON_DESTROY);
        displayRetrofitErrorManager.dismissBottomSheet();
        //unregisterReceiver(gpsSwitchStateReceiver);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: resultCode=" + resultCode + " requestCode=" + requestCode + " data=" + data);
        if (requestCode == HibmobtechApplication.CAUSE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Intent intent = getIntent();
                String causeCode = intent.getStringExtra(Cause.MRO_CAUSE_CODE);
                Log.d(TAG, "onActivityResult: causeCode" + causeCode);
            }
        }
        if (requestCode == HibmobtechApplication.TASK_REQUEST) {
            if (resultCode == RESULT_OK) {
                Intent intent = getIntent();
                String taskCode = intent.getStringExtra(Task.MRO_TASK_CODE);
                Log.d(TAG, "onActivityResult: causeCode" + taskCode);
            }
        }
        if (requestCode == HibmobtechApplication.MACHINE_CREATE_REQUEST) {
            Log.d(TAG, "requestCode == HibmobtechApplication.MACHINE_CREATE_REQUEST");
            Log.d(TAG, "onActivityResult: resultCode=" + resultCode + " requestCode=" + requestCode);
        }
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            if (result != null) {
                if (result.getContents() == null) {
                    Log.d(TAG, "Cancelled scan");
                    if(!this.isFinishing())
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "Scanned: result.getContents()= " + result.getContents());
                    if (result.getRawBytes() != null)
                        Log.d(TAG, "Scanned: result.getRawBytes()= " + result.getRawBytes().toString());
                    Log.d(TAG, "Scanned: result.getBarcodeImagePath()= " + result.getBarcodeImagePath());
                    Log.d(TAG, "Scanned: result.getFormatName()= " + result.getFormatName());
                    Log.d(TAG, "Scanned: result.getErrorCorrectionLevel()= " + result.getErrorCorrectionLevel());
                    Log.d(TAG, "Scanned: result: " + result.toString());
                    checkSerialNumberOnServer(result.getContents());
                }
            } else {
                // This is important, otherwise the result will not be passed to the fragment
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");

        Map<String, Integer> permissionsMap = new HashMap<String, Integer>();
        permissionsMap.put(Manifest.permission.READ_LOGS, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.BATTERY_STATS, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.GET_ACCOUNTS, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.WRITE_CONTACTS, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.READ_CALENDAR, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.WRITE_CALENDAR, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

        for (int i = 0; i < permissions.length; i++) {
            permissionsMap.put(permissions[i], grantResults[i]);
            Log.d(TAG, "onRequestPermissionsResult: requestCode=" + requestCode
                    + " permissions[" + i + "]=" + permissions[i]
                    + " grantResults[" + i + "]=" + grantResults[i]);
        }

        // Check for READ_LOGS
        if (permissionsMap.get(Manifest.permission.READ_LOGS) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: Permissions READ LOGS are OK");
            isGrantedReadLogsPermission = true;
        } else {
            AndroidUtils.showToast(HibmobtechActivity.this, "Permissions READ LOGS are Denied");
            Log.d(TAG, "onRequestPermissionsResult: Permissions READ LOGS are Denied");
            isGrantedReadLogsPermission = false;
        }
        // Check for ACCESS CAMERA
        if (permissionsMap.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: Permissions CAMERA are OK");
            isGrantedCameraPermission = true;
        } else {
            AndroidUtils.showToast(HibmobtechActivity.this, "Permissions CAMERA are Denied");
            Log.d(TAG, "onRequestPermissionsResult: Permissions CAMERA are Denied");
            isGrantedCameraPermission = false;
        }
        // Check for ACCESS LOCATION
        if (permissionsMap.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && permissionsMap.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: Permissions LOCATION are OK");
            isGrantedLocationPermission = true;
        } else {
            AndroidUtils.showToast(HibmobtechActivity.this, "Permissions LOCATION are Denied");
            Log.d(TAG, "onRequestPermissionsResult: Permissions LOCATION are Denied");
            isGrantedLocationPermission = false;
        }
        // Check for WRITE_EXTERNAL_STORAGE
        if (permissionsMap.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: Permissions WRITE_EXTERNAL_STORAGE are OK");
            isGrantedWriteExternalStorage = true;
        } else {
            AndroidUtils.showToast(HibmobtechActivity.this, "Permissions WRITE_EXTERNAL_STORAGE are Denied");
            Log.d(TAG, "onRequestPermissionsResult: Permissions WRITE_EXTERNAL_STORAGE are Denied");
            isGrantedWriteExternalStorage = false;
        }
        // Check for READ_EXTERNAL_STORAGE
        if (permissionsMap.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: Permissions READ_EXTERNAL_STORAGE are OK");
            isGrantedReadExternalStorage = true;
        } else {
            AndroidUtils.showToast(HibmobtechActivity.this, "Permissions READ_EXTERNAL_STORAGE are Denied");
            Log.d(TAG, "onRequestPermissionsResult: Permissions READ_EXTERNAL_STORAGE are Denied");
            isGrantedReadExternalStorage = false;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d(TAG, "onDismiss: currentCheckLocationAndNetworkAlertDialog=" + currentCheckLocationAndNetworkAlertDialog);
        if (dialog == currentCheckLocationAndNetworkAlertDialog)
            currentCheckLocationAndNetworkAlertDialog = null;
    }

    @Override
    public void onUpdatedContent() {
        Log.e(TAG, "onUpdatedContent");
    }

    // T o o l b a r
    // -------------
    protected void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.hibmobToolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        if (getClass().equals(MainActivity.class)) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white_18px);
        }
    }

    // D r a w e r
    // -----------
    protected void initDrawer() {
        Log.d(TAG, "initDrawer");
        drawerLayout = (DrawerLayout) findViewById(R.id.hibmobDrawerLayout);
        navigationView = (NavigationView) findViewById(R.id.hibmobNavigationView);
        drawerMenu = navigationView.getMenu();
        setupDrawerContent(drawerLayout, navigationView);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            boolean closed = true;

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                Log.d(TAG, "onDrawerClosed");
                this.closed = true;
            }

            // Called when a drawer has settled in a completely getWritableDatabase activityObservable.
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                Log.i(TAG, "onDrawerOpened");
                this.closed = false;
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
    }

    protected void setupDrawerContent(final DrawerLayout drawerLayout, NavigationView navigationView) {
        Log.d(TAG, "setupDrawerContent");
        headerView = navigationView.inflateHeaderView(R.layout.navigation_header);
        userTextView = (TextView) headerView.findViewById(R.id.navigationHeaderUserNameTextView);

        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        int itemId = menuItem.getItemId();
                        switch (itemId) {
                            case R.id.homeItemDrawerMenu:
                                Log.d(TAG, "setupDrawerContent: R.id.nav_home=" + R.id.homeItemDrawerMenu);
                                if (getClass().equals(MainActivity.class)) {
                                    drawerLayout.openDrawer(GravityCompat.START);
                                } else {

                                    Intent intent = new Intent(HibmobtechActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    //finish();
                                }
                                break;
                            case R.id.aboutItemDrawerMenu:
                                Intent i = new Intent(getApplicationContext(), AboutActivity.class);
                                startActivity(i);
                                break;
                            case R.id.barcode_scanner:
                                Log.d(TAG, "setupDrawerContent: R.id.barcode_scanner=" + R.id.barcode_scanner);
                                IntentIntegrator integrator = new IntentIntegrator(HibmobtechActivity.this);
                                integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
                                integrator.setOrientationLocked(false);
                                integrator.setBeepEnabled(true);
                                integrator.initiateScan();
                                break;
                            case R.id.logoutItemDrawerMenu:
                                Log.i(TAG, "setupDrawerContent: R.id.logoutItemDrawerMenu=" + R.id.logoutItemDrawerMenu);
                                logout();
                                break;
                            case R.id.settingItemDrawerMenu:
                                Log.d(TAG, "setupDrawerContent: R.id.settingItemDrawerMenu=" + R.id.settingItemDrawerMenu);
                                startActivity(new Intent(HibmobtechActivity.this, SettingsActivity.class));
                                finish();
                                break;
                        }
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }


    /**
     * The server does not return json on logout.
     * In order to getCauses the response body as a string, a converter must be defined in
     * the RestAdapter
     * See: http://stackoverflow.com/questions/21881943/how-can-i-return-string-or-jsonobject-from-asynchronous-callback-using-retrofit
     */
    public void logout() {
        Log.d(TAG, "logout()");
        final String dummy = "Dummy for bug retrofit";
        HibmobtechApplication.getRestClient().getAuthService().logout(dummy, new Callback<Response>() {
            @Override
            public void success(Response s, Response response) {
                if(s == null) return;

                Log.i(TAG, "Logout : status=" + response.getStatus() + ", reason=" + response.getReason());
                HibmobtechApplication.getCookieStore().removeAll();
                HibmobtechApplication.getDataStore().clear();
                Intent ili = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(ili);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Logout: " + error.toString());
                Context context = getApplicationContext();
                Toast toast = Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG);
                if(!((Activity) context).isFinishing())
                    toast.show();
                HibmobtechApplication.getCookieStore().removeAll();
                HibmobtechApplication.getDataStore().clear();
            }
        });
    }

    // L o c a t i o n   P e r m i s s i o n
    // -------------------------------------
    public void checkLocationPermission() {
        Log.d(TAG, "checkLocationPermission");

        final String[] manifestPermissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        // check if the activity has the location permissions
        int hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        Log.d(TAG, "checkLocationPermission: hasAccessFineLocationPermission=" + hasAccessFineLocationPermission);
        Log.d(TAG, "checkLocationPermission: hasAccessCoarseLocationPermission=" + hasAccessCoarseLocationPermission);

        if ((hasAccessFineLocationPermission != PackageManager.PERMISSION_GRANTED)
                && (hasAccessCoarseLocationPermission != PackageManager.PERMISSION_GRANTED)) {

            // Should we show an explanation?
            // shouldShowRequestPermissionRationale returns true if the app has requested this permission previously and the user denied the request.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessageOKCancel("Pour permettre le bon fonctionnement de l'application, vous devez autoriser l'accès à la géolocalisation",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(HibmobtechActivity.this,
                                        manifestPermissions,
                                        LOCATION_PERMISSION_REQUEST);
                            }
                        });
                Log.d(TAG, "checkLocationPermission with explanations: waiting for callback...");
                isGrantedLocationPermission = false;

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        manifestPermissions,
                        LOCATION_PERMISSION_REQUEST);
                Log.d(TAG, "checkLocationPermission without explanations: waiting for callback...");
                isGrantedLocationPermission = false;
            }

        } else {
            Log.d(TAG, "checkLocationPermission: the activity has the location permissions");
            isGrantedLocationPermission = true;
        }
    }


    // L o g s   P e r m i s s i o n
    // -----------------------------
    // TODO l'appel à exec(CMDLINE_GRANTPERMS)ne fonctionne pas
    // par contre, lancée en ligne de commande c'est ok
    //
    public void setReadLogsPermission() {
        String pname = getPackageName();
        String[] CMDLINE_GRANTPERMS = {null};
        CMDLINE_GRANTPERMS[0] = String.format("adb shell pm grant %s android.permission.READ_LOGS", pname);
        Log.d(TAG, "setReadLogsPermission");
        try {
            java.lang.Process p = Runtime.getRuntime().exec(CMDLINE_GRANTPERMS);
            int res = p.waitFor();
            Log.d(TAG, "exec returned: " + res);
            if (res != 0)
                throw new Exception("setReadLogsPermission: cmde " + CMDLINE_GRANTPERMS[0] + " failed !)");
        } catch (Exception e) {
            Log.e(TAG, "setReadLogsPermission: exec(): " + e);
        }
    }

    // TODO à vérifier
    public void checkReadLogsPermission() {
        Log.i(TAG, "checkReadLogsPermission");

        final String[] manifestPermissions = new String[]{Manifest.permission.READ_LOGS};

        // check if the activity has the read logs permissions
        int hasAccessReadLogsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_LOGS);
        Log.d(TAG, "checkReadLogsPermission:hasAccessReadLogsPermission=" + hasAccessReadLogsPermission);

        if ((hasAccessReadLogsPermission != PackageManager.PERMISSION_GRANTED)) {

            // Should we show an explanation?
            // shouldShowRequestPermissionRationale returns true if the app has requested this permission previously and the user denied the request.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_LOGS)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessageOKCancel("Pour permettre le bon fonctionnement de l'application, vous devez autoriser l'accès à la lecture des logs",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(HibmobtechActivity.this,
                                        manifestPermissions,
                                        READ_LOGS_PERMISSION_REQUEST);
                            }
                        });
                Log.d(TAG, "checkReadLogsPermission with explanations: waiting for callback...");
                isGrantedReadLogsPermission = false;

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        manifestPermissions,
                        READ_LOGS_PERMISSION_REQUEST);
                Log.d(TAG, "checkReadLogsPermission without explanations: waiting for callback...");
                isGrantedReadLogsPermission = false;
            }

        } else {
            Log.d(TAG, "checkReadLogsPermission: the activity has the location permissions");
            // Do nothing
            isGrantedReadLogsPermission = true;
        }
    }

    // W r i t e   E x t e r n a l   S t o r a g e   P e r m i s s i o n
    // -----------------------------------------------------------------
    public void checkWriteExternalStoragePermission() {
        Log.i(TAG, "checkWriteExternalStoragePermission");

        final String[] manifestPermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        // check if the activity has the WRITE_EXTERNAL_STORAGE permissions
        int hasAccessWriteExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        Log.d(TAG, "checkWriteExternalStoragePermission:hasAccessWriteExternalStoragePermission=" + hasAccessWriteExternalStoragePermission);

        if ((hasAccessWriteExternalStoragePermission != PackageManager.PERMISSION_GRANTED)) {

            // Should we show an explanation?
            // shouldShowRequestPermissionRationale returns true if the app has requested this permission previously and the user denied the request.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessageOKCancel("Pour permettre le bon fonctionnement de l'application, vous devez autoriser l'accès au système de fichiers",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(HibmobtechActivity.this,
                                        manifestPermissions,
                                        CHECK_WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
                            }
                        });
                Log.d(TAG, "checkWriteExternalStoragePermission with explanations: waiting for callback...");
                isGrantedWriteExternalStorage = false;

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        manifestPermissions,
                        CHECK_WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
                Log.d(TAG, "checkWriteExternalStoragePermission without explanations: waiting for callback...");
                isGrantedWriteExternalStorage = false;
            }

        } else {
            Log.d(TAG, "checkWriteExternalStoragePermission: the activity has the WRITE_EXTERNAL_STORAGE permissions");
            // Do nothing
            isGrantedWriteExternalStorage = true;
        }
    }

    // R e a d   E x t e r n a l   S t o r a g e   P e r m i s s i o n
    // -----------------------------------------------------------------
    public void checkReadExternalStoragePermission() {
        Log.i(TAG, "checkReadExternalStoragePermission");

        final String[] manifestPermissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE};

        // check if the activity has the READ_EXTERNAL_STORAGE permissions
        int hasAccessReadExternalStoragePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Log.d(TAG, "checkReadExternalStoragePermission:hasAccessReadExternalStoragePermission=" + hasAccessReadExternalStoragePermission);

        if ((hasAccessReadExternalStoragePermission != PackageManager.PERMISSION_GRANTED)) {

            // Should we show an explanation?
            // shouldShowRequestPermissionRationale returns true if the app has requested this permission previously and the user denied the request.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessageOKCancel("Pour permettre le bon fonctionnement de l'application, vous devez autoriser l'accès au système de fichiers",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(HibmobtechActivity.this,
                                        manifestPermissions,
                                        CHECK_READ_EXTERNAL_STORAGE_PERMISSION_REQUEST);
                            }
                        });
                Log.d(TAG, "checkReadExternalStoragePermission with explanations: waiting for callback...");
                isGrantedReadExternalStorage = false;

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        manifestPermissions,
                        CHECK_READ_EXTERNAL_STORAGE_PERMISSION_REQUEST);
                Log.d(TAG, "checkReadExternalStoragePermission without explanations: waiting for callback...");
                isGrantedReadExternalStorage = false;
            }

        } else {
            Log.d(TAG, "checkReadExternalStoragePermission: the activity has the READ_EXTERNAL_STORAGE permissions");
            // Do nothing
            isGrantedReadExternalStorage = true;
        }
    }

    // C a m e r a   P e r m i s s i o n
    // ---------------------------------
    public void checkCameraPermission() {
        Log.i(TAG, "checkCameraPermission");

        final String[] manifestPermissions = new String[]{Manifest.permission.CAMERA};

        // check if the activity has the CAMERA permissions
        int hasAccessCameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        Log.d(TAG, "checkCameraPermission:hasAccessCameraPermission=" + hasAccessCameraPermission);

        if ((hasAccessCameraPermission != PackageManager.PERMISSION_GRANTED)) {

            // Should we show an explanation?
            // shouldShowRequestPermissionRationale returns true if the app has requested this permission previously and the user denied the request.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessageOKCancel("Pour permettre le bon fonctionnement de l'application, vous devez autoriser l'accès à l'appareil photo",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(HibmobtechActivity.this,
                                        manifestPermissions,
                                        CAMERA_PERMISSION_REQUEST);
                            }
                        });
                Log.d(TAG, "checkCameraPermission with explanations: waiting for callback...");
                isGrantedCameraPermission = false;

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        manifestPermissions,
                        CAMERA_PERMISSION_REQUEST);
                Log.d(TAG, "checkCameraPermission without explanations: waiting for callback...");
                isGrantedCameraPermission = false;
            }

        } else {
            Log.d(TAG, "checkCameraPermission: the activity has the camera permissions");
            // Do nothing
            isGrantedCameraPermission = true;
        }
    }

    // T r a c k e r   G o o g l e   A n a l y t i c s
    // -----------------------------------------------
    protected Tracker getGoogleAnalyticsTracker() {
        // Obtain the shared Tracker instance.
        HibmobtechApplication application = (HibmobtechApplication) getApplication();
        return application.getDefaultTracker();
    }

    protected void trackScreen(String screenName) {
        Tracker tracker = getGoogleAnalyticsTracker();
        String userName = HibmobtechApplication.getDataStore().getUserName();
        Log.d(TAG, "trackScreen: " + userName + " > screen~" + screenName);
        tracker.setScreenName(userName + " > screen~" + screenName);

        HitBuilders.ScreenViewBuilder screenViewBuilder = new HitBuilders.ScreenViewBuilder();
        screenViewBuilder.setCustomDimension(1, userName);
        tracker.send(screenViewBuilder.build());
    }

    protected void trackEvent(String screenName) {
        Tracker tracker = getGoogleAnalyticsTracker();
        String userName = HibmobtechApplication.getDataStore().getUserName();
        Log.d(TAG, "trackEvent: " + userName + " > screen~" + screenName);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("Action")
                .setAction("Share")
                .build());
    }

    // O t h e r s   m e t h o d s
    // ---------------------------

    public void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        Activity currentActivity = HibmobtechApplication.getInstance().getCurrentActivity();
        if (currentActivity == null) {
            Log.d(TAG, "createAlertDialogOKCancel: currentActivity == null");
            return;
        }
        if (!currentActivity.isFinishing()) {
            new android.support.v7.app.AlertDialog
                    .Builder(currentActivity)
                    .setMessage(message)
                    .setPositiveButton("OK", okListener)
                    .setNegativeButton("Cancel", null)
                    .setOnDismissListener(this)
                    .create()
                    .show();
        } else {
            Log.d(TAG, "showMessageOKCancel: currentActivity.isFinishing()");
            currentActivity.isFinishing();
        }
    }

    public void setRetrofitError(RetrofitError retrofitError) {
        Log.d(TAG, "setRetrofitError: retrofitError" + retrofitError);
        networkObservable.setRetrofitError(retrofitError);
    }

    protected void checkSerialNumberOnServer(final String serialNumber) {
        Log.d(TAG, "checkSerialNumberOnServer: serialNumber= " + serialNumber);
        MachineApiService service = HibmobtechApplication.getRestClient().getMachineService();
        service.getMachine(serialNumber, new Callback<List<Machine>>() {
            @Override
            public void success(final List<Machine> machines, retrofit.client.Response response) {
                if(machines == null) return;

                int sizeOfListMachine = machines.size();
                if (sizeOfListMachine == 0) {
                    if(!HibmobtechActivity.this.isFinishing())
                        Toast.makeText(HibmobtechActivity.this,
                                "Machine " + serialNumber + " non enregistrée", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Machine " + serialNumber + " non enregistrée sur le serveur");
                    return;
                }
                if (sizeOfListMachine == 1) {
                    Log.d(TAG, "Machine " + serialNumber + " enregistrée sur le site " + machines.get(0).getSite().getBusinessName());
                    if(!HibmobtechActivity.this.isFinishing())
                        Toast.makeText(HibmobtechActivity.this,
                                "Machine " + serialNumber + " enregistrée sur le site " + machines.get(0).getSite().getBusinessName(),
                                Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(getApplicationContext(), MachineActivity.class);
                    SiteCurrent.getInstance().setSiteCurrent(machines.get(0).getSite());
                    MachineCurrent.getInstance().setMachineCurrent(machines.get(0));
                    startActivityForResult(intent, HibmobtechApplication.MACHINE_REQUEST);
                    return;
                }
                if (sizeOfListMachine > 1) {

                    String msg = "Données dupliquées ! Machine " + serialNumber + "\nenregistrée sur les sites :\n";
                    for (Machine m : machines) {
                        msg = msg.concat(" - " + m.getSite().getBusinessName());
                        msg = msg.concat("\n");
                    }

                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(HibmobtechActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Attention !");
                    builder.setMessage(msg);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), MachineActivity.class);
                            SiteCurrent.getInstance().setSiteCurrent(machines.get(0).getSite());
                            MachineCurrent.getInstance().setMachineCurrent(machines.get(0));
                            startActivityForResult(intent, HibmobtechApplication.MACHINE_REQUEST);
                        }
                    });

                    Activity currentActivity = HibmobtechApplication.getInstance().getCurrentActivity();
                    if (currentActivity == null) {
                        Log.d(TAG, "checkSerialNumberOnServer: currentActivity == null");
                        return;
                    }
                    if(!currentActivity.isFinishing()) {
                        //show dialog
                        builder.show();
                    } else {
                        Log.d(TAG, "checkSerialNumberOnServer: currentActivity.isFinishing()");
                        currentActivity.isFinishing();
                    }

                    Log.d(TAG, "checkSerialNumberOnServer: Données dupliquées: " + msg);
                    return;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                setRetrofitError(error);
            }
        });
    }

    void getLocationState(boolean withToast) {
        try {
            int locationMode = android.provider.Settings.Secure.getInt(getContentResolver(),
                    LOCATION_MODE);
            switch (locationMode) {
                /**
                 * Reduced power usage, such as limiting the number of GPS updates per hour.
                 */
                case LOCATION_MODE_BATTERY_SAVING:
                    if(withToast)
                        AndroidUtils.showToast(HibmobtechActivity.this,
                                "Localisation démarrée & réduite pour économie d'énergie");
                    HibmobtechApplication.setLocationEnable(true);
                    HibmobtechApplication.getInstance().getSettingsObservable()
                            .setLocationEnabled(true);
                    notificationUtils.launchNotification(this);
                    break;
                /**
                 * Best-effort location computation allowed.
                 */
                case LOCATION_MODE_HIGH_ACCURACY:
                    if(withToast)
                        AndroidUtils.showToast(HibmobtechActivity.this,
                                "Localisation démarrée (réseau & GPS)");
                    HibmobtechApplication.setLocationEnable(true);
                    HibmobtechApplication.getInstance().getSettingsObservable()
                            .setLocationEnabled(true);
                    notificationUtils.launchNotification(this);
                    break;
                /**
                 * Location access disabled.
                 */
                case LOCATION_MODE_OFF:
                    if(withToast)
                        AndroidUtils.showToast(HibmobtechActivity.this,
                                "Localisation arrêtée");
                    HibmobtechApplication.setLocationEnable(false);
                    HibmobtechApplication.getInstance().getSettingsObservable()
                            .setLocationEnabled(false);
                    notificationUtils.cancelNotification(this);
                    break;
                /**
                 * Network Location Provider disabled, but GPS and other sensors enabled.
                 */
                case LOCATION_MODE_SENSORS_ONLY:
                    if(withToast)
                        AndroidUtils.showToast(HibmobtechActivity.this,
                                "Localisation démarrée (GPS seulement, sans réseau)");
                    HibmobtechApplication.setLocationEnable(true);
                    HibmobtechApplication.getInstance().getSettingsObservable()
                            .setLocationEnabled(true);
                    notificationUtils.launchNotification(this);
                    break;
            }

        } catch (android.provider.Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    // G e t t e r s   &   s e t t e r s
    // ---------------------------------
    public boolean isGrantedCameraPermission() {
        return isGrantedCameraPermission;
    }

    public boolean isGrantedWriteExternalStorage() {
        return isGrantedWriteExternalStorage;
    }

    public boolean isGrantedLocationPermission() {
        return isGrantedLocationPermission;
    }

    public void setIsGrantedLocationPermission(boolean isGrantedLocationPermission) {
        this.isGrantedLocationPermission = isGrantedLocationPermission;
    }

    public boolean isGrantedReadLogsPermission() {
        return isGrantedReadLogsPermission;
    }

    public void setGrantedReadLogsPermission(boolean grantedReadLogsPermission) {
        isGrantedReadLogsPermission = grantedReadLogsPermission;
    }

    public DisplayRetrofitErrorManager getDisplayRetrofitErrorManager() {
        return displayRetrofitErrorManager;
    }

    public void setDisplayRetrofitErrorManager(DisplayRetrofitErrorManager displayRetrofitErrorManager) {
        this.displayRetrofitErrorManager = displayRetrofitErrorManager;
    }

    public ActivityObservable getActivityObservable() {
        return activityObservable;
    }

    public void setActivityObservable(ActivityObservable activityObservable) {
        this.activityObservable = activityObservable;
    }

    public Typeface getHibmobtechFont() {
        return hibmobtechFont;
    }

    public void setHibmobtechFont(Typeface hibmobtechFont) {
        this.hibmobtechFont = hibmobtechFont;
    }
}
