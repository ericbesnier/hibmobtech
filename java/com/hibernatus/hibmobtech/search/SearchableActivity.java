package com.hibernatus.hibmobtech.search;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.util.Log;
import android.view.MenuItem;

import com.hibernatus.hibmobtech.HibmobtechSearchMenuActivity;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.utils.FileUtils;
import com.hibernatus.hibmobtech.utils.ParentClassUtils;

import static com.hibernatus.hibmobtech.HibmobtechApplication.SEARCH_REQUEST;

public class SearchableActivity extends HibmobtechSearchMenuActivity {
    public static final String TAG = SearchableActivity.class.getSimpleName();
    public static final String SEARCH_QUERY = "SEARCH_QUERY";
    public static final String SEARCH_ACTIVITY = "SEARCH_ACTIVITY";
    public static final String ACTIVITY_FILTER = SearchableActivity.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Log.d(TAG, "onCreate: intent.getAction()=" + intent.getAction());
        FileUtils.logBundle(TAG, intent.getExtras());
    }

    @Override
    protected void onStart(){
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    protected void onRestart(){
        Log.d(TAG, "onStart");
        super.onRestart();
        finish();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        trackScreen(TAG);
        handleIntent(getIntent());
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
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent: intent=" + intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Log.d(TAG, "handleIntent: intent=" + intent + " getComponentName=" + getComponentName());
        if (Intent.ACTION_SEARCH.equals(intent.getAction())){
            Log.d(TAG, "handleIntent: ACTION_SEARCH");
            final String query = intent.getStringExtra(SearchManager.QUERY);

            savingQueriesForSuggestionsProvider(query);
            // When search called through speech recognition, activity filter value
            // is not passed to intent... we use some helper
            String activity = SearchHelper.getActivityFilter();

            Log.d(TAG, "handleIntent: activity=" + activity + " query=" + query);

            Intent filteredListIntent = new Intent(activity);
            Log.d(TAG, "handleIntent: filteredListIntent=" + filteredListIntent);
            filteredListIntent.putExtra(SEARCH_QUERY, query);
            startActivityForResult(filteredListIntent, SEARCH_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult: resultCode=" + resultCode + " requestCode=" + requestCode);
        Log.e(TAG, "onActivityResult: finish !!!! activity=" + this);
        finish();
    }

    protected void savingQueriesForSuggestionsProvider(String query) {
        Log.d(TAG, "savingQueriesForSuggestionsProvider: query=" + query);
        if(query != null && !"".equals(query)) {
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    SuggestionProviderHibmobTech.AUTHORITY, SuggestionProviderHibmobTech.MODE);
            suggestions.saveRecentQuery(query, null);
        }
    }

    protected void clearSavedQueriesForSuggestionsProvider() {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                SuggestionProviderHibmobTech.AUTHORITY, SuggestionProviderHibmobTech.MODE);
        suggestions.clearHistory();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected: item=" + item);

        switch (item.getItemId()) {
            case R.id.clearHistoryItemId:
                clearSavedQueriesForSuggestionsProvider();
                return true;
            case android.R.id.home:
                Intent parentActivityIntent = new Intent(this, ParentClassUtils.parentClassStack.pop());
                startActivity(parentActivityIntent);
                finish();
                return true;
            default:
                return true;
        }
    }
}