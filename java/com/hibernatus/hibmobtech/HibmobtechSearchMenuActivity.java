package com.hibernatus.hibmobtech;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.NavUtils;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.hibernatus.hibmobtech.search.SuggestionProviderHibmobTech;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Eric on 07/12/2015.
 */

// c l a s s   H i b m o b t e c h S e a r c h M e n u A c t i v i t y
// -------------------------------------------------------------------

public class HibmobtechSearchMenuActivity extends HibmobtechActivity {
    public static final String TAG = HibmobtechSearchMenuActivity.class.getSimpleName();

    public SearchView searchView;
    protected ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(10); // schedule executor
    protected String currentSearchTip;
    protected SearchManager searchManager;
    protected Timer timer;
    protected String query; // Optional query

    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate: getComponentName=" + getComponentName());
    }

    @Override
    protected void onStart(){
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart(){
        Log.i(TAG, "onStart");
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        hideSoftInput();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_search_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.searchItemId);
        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.searchItemId).getActionView();
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setQueryRefinementEnabled(true);

        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

            @Override
            public boolean onSuggestionSelect(int position) {
                Log.d(TAG, "onSuggestionSelect: position=" + position);
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Log.d(TAG, "onSuggestionClick: position=" + position);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Log.d(TAG, "onClose");
                // to avoid click x button and the edittext hidden
                return true;
            }
        });

        Log.i(TAG, "onCreateOptionsMenu: ComponentName=" + getComponentName());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clearHistoryItemId:
                clearSavedQueriesForSuggestionsProvider();
                return true;
            case android.R.id.home:
                Log.i(TAG, "onOptionsItemSelected: home=");
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return true;
        }
    }

    // O t h e r s   m e t h o d s
    // ---------------------------

    protected void clearSavedQueriesForSuggestionsProvider() {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                SuggestionProviderHibmobTech.AUTHORITY, SuggestionProviderHibmobTech.MODE);
        suggestions.clearHistory();
    }

    protected void hideSoftInput() {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        Log.d(TAG, "hideSoftInput: view=" + view);
        if (view != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            if(searchView != null)
                searchView.clearFocus();
        }
    }

    public ScheduledFuture<?> schedule(Runnable command, long delayTimeMills) {
        return scheduledExecutor.schedule(command, delayTimeMills, TimeUnit.MILLISECONDS);
    }
}

