package com.hibernatus.hibmobtech;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.hibernatus.hibmobtech.mrorequest.MroRequestListFragment;
import com.hibernatus.hibmobtech.mrorequest.MroRequestsFragment;
import com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.PictureCacheManager;
import com.hibernatus.hibmobtech.receiver.NetworkStateChangeReceiver;
import com.hibernatus.hibmobtech.tracking.HibmobTrackingService;
import com.testfairy.TestFairy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;

import static com.hibernatus.hibmobtech.HibmobtechApplication.TIMER_TASK_DELAY;
import static com.hibernatus.hibmobtech.HibmobtechApplication.TIMER_TASK_DELAY_PERIOD;
import static com.hibernatus.hibmobtech.receiver.NetworkStateChangeReceiver.IS_NETWORK_AVAILABLE;

// c l a s s   M a i n A c t i v i t y
// -----------------------------------

public class MainActivity extends HibmobtechOptionsMenuActivity implements ActivityRefresher {
    public static final String TAG = MainActivity.class.getSimpleName();
    static Timer updateDataBasetimer = new Timer();

    protected TabLayout tabLayout;
    protected ViewPager viewPager;
    protected ArrayList<MroRequestsFragment> fragmentsList;

    // Attention ! l'ordre des 2 tableaux suivants doit être le même !
    protected List<String> fragmentTitleList = Arrays.asList("AFFECTéES", "OUVERTES", "TERMINéES");
    protected List<String> fragmentTypeList = Arrays.asList("ASSIGNED", "OPENED", "FINISHED");

    protected PictureCacheManager pictureCacheManager;
    protected String networkStatus;
    protected boolean isNetworkAvailable;
    protected DelayedTimerTask delayedTimerTask;
    protected BroadcastReceiver gpsSwitchStateReceiver;


    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        // Following broadcast receiver is to listen the Location button toggle state in Android.
        getLocationState(true);
        gpsSwitchStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
                    getLocationState(true);
                }
            }
        };
        registerReceiver(gpsSwitchStateReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

        pictureCacheManager = new PictureCacheManager(this, null);

        // Broadcast Manager to receive notification when network state is changing
        IntentFilter intentFilter = new IntentFilter(NetworkStateChangeReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                if(isNetworkAvailable != MainActivity.this.isNetworkAvailable) {
                    MainActivity.this.isNetworkAvailable = isNetworkAvailable;
                    networkStatus = isNetworkAvailable ? "Connecté au réseau" : "Déconnecté du réseau";
                    Log.d(TAG, "onReceive: receive notification when network state is changing: networkStatus=" + networkStatus);
                    Toast.makeText(MainActivity.this, networkStatus, Toast.LENGTH_SHORT).show();
                    new PostAllDataBasePictureCacheAsyncTask().execute();
                }
            }
        }, intentFilter);

        fragmentsList = new ArrayList<>();
        if (!BuildConfig.DEBUG) {
            TestFairy.begin(this, HibmobtechApplication.TEST_FAIRY_APP_TOKEN);
        }
        launchDelayedTimerTask();
        launchTrackingGoogleApiService();
        setContentView(R.layout.site_activity);
        initToolBar();
        initDrawer();
        setupViewPager();
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);
    }

    class PostAllDataBasePictureCacheAsyncTask extends AsyncTask<Void, Integer, String> {

        // Surcharge de la méthode doInBackground (Celle qui s'exécute dans une Thread à part)
        @Override
        protected String doInBackground(Void... unused) {
            Log.d(TAG, "PostAllDataBasePictureCacheAsyncTask: doInBackground");
            if(isNetworkAvailable){
                try {
                    pictureCacheManager.postAllDataBasePictureCache();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return ("");
        }

        // Surcharge de la méthode onProgressUpdate (s'exécute dans la Thread de l'IHM)
        @Override
        protected void onProgressUpdate(Integer... diff) {
            // Mettre à jour l'IHM
            Log.d(TAG, "PostAllDataBasePictureCacheAsyncTask: onProgressUpdate");
        }

        // Surcharge de la méthode onPostExecute (s'exécute dans la Thread de l'IHM)
        @Override
        protected void onPostExecute(String message) {
            // Mettre à jour l'IHM
            Log.d(TAG, "PostAllDataBasePictureCacheAsyncTask: onPostExecute");
        }
    }

    @Override
    protected void onStart(){
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart(){
        Log.d(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        trackScreen(TAG);
    }

    @Override
    protected void onPause(){
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        unregisterReceiver(gpsSwitchStateReceiver);
        Intent intent = new Intent(this, HibmobTrackingService.class);
        stopService(intent);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu);
        mainMenuCheckItem.setVisible(false);
        mainMenuMapItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns true, then it has handled the app icon touch event
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for(MroRequestsFragment mroRequestsFragment : fragmentsList){
            Log.d(TAG, "onActivityResult: mroRequestsFragment=" + mroRequestsFragment);
            mroRequestsFragment.fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult");
        launchTrackingGoogleApiService();
    }

    @Override
    public void refresh() {
        Log.d(TAG, "refresh");
        for(MroRequestsFragment mroRequestListFragmentTitle : fragmentsList){
            mroRequestListFragmentTitle.fragment.refresh();
        }
    }

    // O t h e r s   m e t h o d s
    // ---------------------------

    public void launchDelayedTimerTask() {
        Log.d(TAG, "launchDelayedTimerTask");
        delayedTimerTask = new DelayedTimerTask(getApplicationContext());

        updateDataBasetimer.schedule(delayedTimerTask, TIMER_TASK_DELAY, TIMER_TASK_DELAY_PERIOD);
    }

    public void launchTrackingGoogleApiService() {
        Log.d(TAG, "launchTrackingGoogleApiService");
        checkLocationPermission();
        if(isGrantedLocationPermission()) { // Permissions locations are OK
            Log.d(TAG, "launchTrackingGoogleApiService: create trackingGoogleApiAsyncTask & let's execute it");

            Intent intent = new Intent(this, HibmobTrackingService.class);
            startService(intent);

/*            IntentFilter filter = new IntentFilter(HibmobWakefulReceiver.PROCESS_RESPONSE);
            filter.addCategory(Intent.CATEGORY_DEFAULT);
            hibmobWakefulReceiver = new HibmobWakefulReceiver();
            registerReceiver(hibmobWakefulReceiver, filter);*/
        }
    }

    private void setupViewPager() {
        Log.d(TAG, "setupViewPager");

        for (int i = 0; i < fragmentTitleList.size(); i++) {
            MroRequestListFragment mroRequestListFragment = new MroRequestListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("FRAGMENT_TYPE", fragmentTypeList.get(i));
            mroRequestListFragment.setArguments(bundle);

            MroRequestsFragment mroRequestsFragment = new MroRequestsFragment(mroRequestListFragment,
                    fragmentTitleList.get(i), fragmentTypeList.get(i));

            // permet de créer le lien entre :
            // . mroRequestListFragment (Observable) qui lance une notification quand l'état des données du serveur a changé
            // . mroRequestListFragment (Observer) qui reçoit la notification et met à journ la liste des MroRequest dans l'IHM
            if(delayedTimerTask != null)
                delayedTimerTask.register(mroRequestListFragment);
            mroRequestListFragment.attachServerDataObservable(delayedTimerTask);

            fragmentsList.add(i, mroRequestsFragment);
        }

        final MroRequestListFragmentPagerAdapter mroRequestListFragmentPagerAdapter = new MroRequestListFragmentPagerAdapter(getSupportFragmentManager(), fragmentsList);

        viewPager = (ViewPager) findViewById(R.id.siteViewPager);
        assert viewPager != null;
        viewPager.setAdapter(mroRequestListFragmentPagerAdapter);
        viewPager.setCurrentItem(0);
        // Attach the page change listener inside the activity
        // This method will be invoked when a new page becomes selected.
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: position=" + position);
/*                viewPager.setAdapter(mroRequestListFragmentPagerAdapter);
                viewPager.setCurrentItem(position);*/
            }

            // This method will be invoked when the current page is scrolled
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.d(TAG, "onPageScrolled: position=" + position);
            }

            // Called when the scroll state changes: SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
            @Override
            public void onPageScrollStateChanged(int state) {
                Log.d(TAG, "onPageScrollStateChanged: state=" + state);
            }


        });
    }

    // c l a s s   A d a p t e r
    // -------------------------
    class MroRequestListFragmentPagerAdapter extends FragmentStatePagerAdapter {

        ArrayList<MroRequestsFragment> fragmentsList;

        public MroRequestListFragmentPagerAdapter(FragmentManager fragmentManager,
                                                  ArrayList<MroRequestsFragment> fragmentsList) {
            super(fragmentManager);
            this.fragmentsList = fragmentsList;
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "MroRequestListFragmentPagerAdapter: getItem: fragment[" + position + "]=" + fragmentsList.get(position).fragment
                    + " title=" + fragmentsList.get(position).title);
/*            MroRequestListFragment mroRequestListFragment = new MroRequestListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("FRAGMENT_TYPE", fragmentTypeList.get(position));
            mroRequestListFragment.setArguments(bundle);
            return mroRequestListFragment;*/
            return fragmentsList.get(position).fragment;
        }

        @Override
        public int getCount() {
            return fragmentsList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsList.get(position).title;
        }

        @Override
        public int getItemPosition(Object object) {
            Log.d(TAG, "getItemPosition");
            return POSITION_NONE;
        }
    }
}
