package com.hibernatus.hibmobtech.site;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hibernatus.hibmobtech.ActivityRefresher;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.HibmobtechOptionsMenuActivity;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.contact.ContactFragment;
import com.hibernatus.hibmobtech.invoice.InvoiceFragment;
import com.hibernatus.hibmobtech.machine.MachineFragment;
import com.hibernatus.hibmobtech.model.Site;
import com.hibernatus.hibmobtech.notes.NotesFragment;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;

import static com.hibernatus.hibmobtech.mrorequest.MroRequestActivity.CURRENT_SITE_FRAGMENT;

// c l a s s   S i t e A c t i v i t y
// -----------------------------------
public class SiteActivity extends HibmobtechOptionsMenuActivity implements ActivityRefresher {
    public static final String TAG = SiteActivity.class.getSimpleName();

    protected MachineFragment siteMachineFragment;
    protected ContactFragment siteContactFragment;
    protected NotesFragment siteNotesFragment;
    protected InvoiceFragment siteInvoiceFragment;

    protected Bundle bundleForFragmentContact;
    protected Bundle bundleForFragmentNotes;
    protected Bundle bundleForFragmentMachine;
    protected Bundle bundleForFragmentInvoice;

    protected MenuItem mainMenuItemCheck;
    protected TabLayout tabLayout;
    protected ViewPager viewPager;

    Site currentSite;
    long currentSiteId;
    public int currentSiteFragment;

    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate:");
        Intent intent = getIntent(); // gets the previously created intent
        currentSiteFragment  = intent.getIntExtra(CURRENT_SITE_FRAGMENT, 0);
        setContentView(R.layout.site_activity);
        findViews();
        initToolBar();
        initDrawer();
        initBundles();
        if(SiteCurrent.getInstance().isCurrentSite()){
            currentSite = SiteCurrent.getInstance().getSiteCurrent();
            currentSiteId = currentSite.getId();
            setTitle(currentSite.getName());
            loadSite(currentSiteId);
            Log.i(TAG, "onCreate: currentSite=" + currentSite + " currentSiteId="
                    + currentSiteId + "siteName =" + currentSite.getName());
        }
    }

    @Override
    protected void onRestart(){
        Log.d(TAG, "onRestart");
        super.onRestart();
    }

    @Override
    protected void onStart(){
        Log.d(TAG, "onStart");
        super.onStart();
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
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        trackScreen(TAG);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu);
        mainMenuItemCheck = menu.findItem(R.id.mainMenuCheckItem);
        mainMenuItemCheck.setVisible(false);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: resultCode=" + resultCode + " requestCode=" + requestCode);
        siteMachineFragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void refresh() {
        loadSite(currentSiteId);
    }

    // O t h e r s   m e t h o d s
    // ---------------------------
    public void loadSite(long siteId){
        Log.d(TAG, "loadSite : siteId=" + siteId);

        SiteApiService service = HibmobtechApplication.getRestClient().getSiteService();
        service.getOneSite(siteId, new Callback<Site>() {

            @Override
            public void success(Site site, retrofit.client.Response response) {
                if(site == null) return;

                Log.i(TAG, "onResponse: Nom du siteCurrent=" + site.getName());
                SiteCurrent.getInstance().setSiteCurrent(site);
                setTitle(site.getName());
                setupViewPager(viewPager);
                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void failure(RetrofitError error) {
                setRetrofitError(error);
            }
        });
    }

    public void findViews () {
        Log.i(TAG, "findViews");
        viewPager = (ViewPager) findViewById(R.id.siteViewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());

        siteMachineFragment = new MachineFragment();
        siteContactFragment = new ContactFragment();
        siteNotesFragment = new NotesFragment();
        siteInvoiceFragment = new InvoiceFragment();

        siteMachineFragment.setArguments(bundleForFragmentMachine);
        siteContactFragment.setArguments(bundleForFragmentContact);
        siteNotesFragment.setArguments(bundleForFragmentNotes);
        siteInvoiceFragment.setArguments(bundleForFragmentInvoice);

        adapter.addFragment(siteMachineFragment, "Machine");
        adapter.addFragment(siteContactFragment, "Contact");
        adapter.addFragment(siteNotesFragment, "Note");
        adapter.addFragment(siteInvoiceFragment, "Facture");

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentSiteFragment);
    }

    public void initBundles() {
        bundleForFragmentContact =new Bundle();
        bundleForFragmentContact.putString("fragmentType", "contactFragment");
        bundleForFragmentMachine =new Bundle();
        bundleForFragmentMachine.putString("fragmentType", "machineFragment");
        bundleForFragmentMachine.putString("parentClass", "SiteActivity");
        bundleForFragmentNotes =new Bundle();
        bundleForFragmentNotes.putString("fragmentType", "notesFragment");
        bundleForFragmentInvoice =new Bundle();
        bundleForFragmentInvoice.putString("fragmentType", "invoiceFragment");
    }


    // c l a s s   A d a p t e r
    // -------------------------
    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentArrayList = new ArrayList<>();
        private final List<String> fragmentTitleArrayList = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentArrayList.add(fragment);
            fragmentTitleArrayList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentArrayList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentArrayList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitleArrayList.get(position);
        }
    }
}