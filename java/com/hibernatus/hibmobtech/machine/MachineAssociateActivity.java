package com.hibernatus.hibmobtech.machine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.hibernatus.hibmobtech.ActivityCurrent;
import com.hibernatus.hibmobtech.ActivityRefresher;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.HibmobtechOptionsMenuActivity;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.model.Site;
import com.hibernatus.hibmobtech.site.SiteApiService;
import com.hibernatus.hibmobtech.site.SiteCurrent;

import retrofit.Callback;
import retrofit.RetrofitError;

// c l a s s   M a c h i n e A s s o c i a t e A c t i v i t y
// -----------------------------------------------------------
public class MachineAssociateActivity extends HibmobtechOptionsMenuActivity implements ActivityRefresher{
    public static final String TAG = MachineAssociateActivity.class.getSimpleName();
    protected Bundle bundleForFragmentMachine;
    protected MenuItem mainMenuItemCheck;
    protected  LinearLayout machineAssociateActivityLinearLayout;
    protected Activity parentActivity = ActivityCurrent.getInstance().getActivityCurrent();
    protected MachineFragment siteMachineFragment;
    Site currentSite;
    long currentSiteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: parentActivity=" + parentActivity);
        ActivityCurrent.getInstance().setActivityCurrent(this);
        Activity currentActivity = ActivityCurrent.getInstance().getActivityCurrent();
        Log.d(TAG, "onCreate: currentActivity=" + currentActivity);

        setContentView(R.layout.machine_associate_activity);
        findViews();
        initToolBar();

        if(SiteCurrent.getInstance().isCurrentSite()){
            currentSite = SiteCurrent.getInstance().getSiteCurrent();
            currentSiteId = currentSite.getId();
            setTitle(currentSite.getName());
            loadSite(currentSiteId);
            Log.i(TAG, "onCreate: currentSite=" + currentSite + " currentSiteId=" + currentSiteId + "siteName =" + currentSite.getName());
        }
        initBundles();
    }

    @Override
    protected void onStart(){
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart(){
        Log.i(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    public void onResume() {
        super.onResume();
        trackScreen(TAG);
    }

    @Override
    protected void onPause(){
        Log.i(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
    }



    public void findViews () {
        Log.i(TAG, "findViews");
        machineAssociateActivityLinearLayout = (LinearLayout) findViewById(R.id.machineAssociateActivityLinearLayout);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu);
        mainMenuItemCheck = menu.findItem(R.id.mainMenuCheckItem);
        mainMenuItemCheck.setVisible(false);
        return true;
    }

    public void loadSite(long siteId){
        Log.i(TAG, "loadSite : " + siteId);
        SiteApiService service = HibmobtechApplication.getRestClient().getSiteService();
        service.getOneSite(siteId, new Callback<Site>() {
            @Override
            public void success(Site site, retrofit.client.Response response) {
                if(site == null) return;

                Log.i(TAG, "onResponse: Nom du siteCurrent=" + site.getName());
                SiteCurrent.getInstance().setSiteCurrent(site);
                setTitle(site.getName());
                setupView();
            }

            @Override
            public void failure(RetrofitError error) {
                setRetrofitError(error);
            }
        });
    }

    public void initBundles() {
        bundleForFragmentMachine = new Bundle();
        bundleForFragmentMachine.putString("fragmentType", "machineFragment");
        bundleForFragmentMachine.putString("parentClass", "MachineAssociateActivity");
    }

    private void setupView() {
        Log.d(TAG, "setupView");
        siteMachineFragment = new MachineFragment();
        siteMachineFragment.setArguments(bundleForFragmentMachine);

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.machineAssociateActivityLinearLayout, siteMachineFragment).commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: resultCode=" + resultCode + " requestCode=" + requestCode);
        siteMachineFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void refresh() {
        if(currentSite != null)
            loadSite(currentSiteId);
    }
}