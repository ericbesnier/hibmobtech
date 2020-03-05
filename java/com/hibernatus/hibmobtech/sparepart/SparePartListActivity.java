package com.hibernatus.hibmobtech.sparepart;

import android.app.SearchableInfo;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;

import com.bleau.hibernatus.mob.util.AndroidUtils;
import com.bleau.hibernatus.mob.util.Page;
import com.bleau.hibernatus.mob.util.PagedListRecyclerView;
import com.bleau.hibernatus.mob.util.StringUtils;
import com.hibernatus.hibmobtech.HibmobtechSearchMenuActivity;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.model.SparePart;
import com.hibernatus.hibmobtech.search.SearchHelper;
import com.hibernatus.hibmobtech.search.SearchableActivity;
import com.hibernatus.hibmobtech.utils.ParentClassUtils;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class SparePartListActivity extends HibmobtechSearchMenuActivity {
    public static final String TAG = SparePartListActivity.class.getSimpleName();
    public static final String ACTIVITY_FILTER = SparePartListActivity.class.getName();

    protected SparePartRecyclerView recyclerView;
    protected SparePartListAdapter sparePartListAdapter;
    private SearchTipThread searchTipThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.spare_part_list_activity);
        initToolBar();
        initDrawer();

        recyclerView = (SparePartRecyclerView) findViewById(R.id.sparePartListActivityRecyclerView);
        recyclerView.setHasFixedSize(true);

        query = getIntent().getStringExtra(SearchableActivity.SEARCH_QUERY);

        recyclerView.setSearchQuery(query);
        Log.i(TAG, "onCreate: query=" + query);

        recyclerView.initParams(30, 5); // endlessscroll
        sparePartListAdapter = new SparePartListAdapter(getApplicationContext(), 100, this);
        recyclerView.setAdapter(sparePartListAdapter);
        if (!StringUtils.isNull(query)) {
            loadSpareParts(query);
        }
        else{
            loadSpareParts("");
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
        hideSoftInput();
        finish();
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
        hideSoftInput();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    public void loadSpareParts(final String query, boolean clearAdapter) {
        Log.d(TAG, "loadSpareParts version swapAdapter: query=" + query);
        recyclerView.loadInitialPage(new PagedListRecyclerView.OnLoadingListener<SparePart>() {
            @Override
            public void start() {
                Log.d(TAG, "START loadEquipments: query=" + query);
                // TODO verifier que cette commande est vraiment utile
                recyclerView.setSearchQuery(query);
            }

            @Override
            public void finish(Page<SparePart> page, Response response) {
                if (page.getNumberOfElements() == 0) {
                    String message = String.format("Aucune pièce détachée ne correspond au critère \"%s\".", query);
                    AndroidUtils.showToast(SparePartListActivity.this, message);
                }
            }

            @Override
            public void error(RetrofitError error) {
                String message = String.format("Erreur de connexion - vérifiez votre login");
                AndroidUtils.showToast(SparePartListActivity.this, message);
            }
        }, clearAdapter); // clear thumbnailCursorRecyclerViewAdapter to reinit list view
    }

    public void loadSpareParts(final String query) {
        Log.d(TAG, "loadSpareParts: query=" + query);
        recyclerView.loadInitialPage(new PagedListRecyclerView.OnLoadingListener<SparePart>() {
            @Override
            public void start() {
                Log.i(TAG, "START loadEquipments: query=" + query);
                // TODO verifier que cette commande est vraiment utile
                recyclerView.setSearchQuery(query);
            }

            @Override
            public void finish(Page<SparePart> page, Response response) {
                if (page.getNumberOfElements() == 0) {
                    String message = String.format("Aucune pièce détachée ne correspond au critère \"%s\".", query);
                    AndroidUtils.showToast(SparePartListActivity.this, message);
                }
            }

            @Override
            public void error(RetrofitError error) {
                String message = String.format("Erreur de connexion - vérifiez votre login");
                AndroidUtils.showToast(SparePartListActivity.this, message);
            }
        });
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode){
        Log.d(TAG, "startActivityForResult : intent.getAction()=" + intent.getAction());
        ParentClassUtils.searchableParentClass = getClass();
        SearchHelper.setActivityFilter(ACTIVITY_FILTER);

        if (Intent.ACTION_SEARCH.equals(intent.getAction()) ||
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH.equals(intent.getAction())) {
            Log.i(TAG, "startActivityForResult : ACTION_SEARCH || ACTION_RECOGNIZE_SPEECH");
            intent.putExtra(SearchableActivity.SEARCH_ACTIVITY, ACTIVITY_FILTER);
        }
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: resultCode=" + resultCode + " requestCode=" + requestCode + " data=" + data);
        Log.d(TAG, "onActivityResult: finish !!!! activity=" + this);

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        boolean returnCode = super.onCreateOptionsMenu(menu);

        searchView = (SearchView) menu.findItem(R.id.searchItemId).getActionView();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setQueryRefinementEnabled(true);
        searchView.setSearchableInfo(searchableInfo);

        query = getIntent().getStringExtra(SearchableActivity.SEARCH_QUERY);
        Log.d(TAG, "onCreateOptionsMenu: query=" + query);

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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "onQueryTextSubmit");
                hideSoftInput();
                return true;
            }

            public boolean onQueryTextChange(String newText) {
                if (newText != null && newText.length() > 0) {
                    Log.d(TAG, "onQueryTextChange: newText=" + newText);
                    currentSearchTip = newText;
                    showSearchTip(newText);
                }
                else {
                    currentSearchTip = "";
                    showSearchTip("");
                }
                return true;
            }
        });
        return returnCode;
    }

    public void showSearchTip(String newText) {
        Log.d(TAG, "showSearchTip: newText=" + newText);

        // execute after 500ms, and when execute, judge current search tip and newText
        searchTipThread = new SearchTipThread(newText);
        query = newText;
        schedule(searchTipThread, 500);
    }

    // c l a s s   S e a r c h T i p T h r e a d
    // -----------------------------------------
    protected class SearchTipThread implements Runnable {

        String newText;

        public SearchTipThread(String newText) {
            this.newText = newText;
        }

        public void run() {
            // keep only one thread to load current search tip, u can getCauses data from network here
            if (newText != null && newText.equals(currentSearchTip)) {
                Log.d(TAG, "SearchTipThread: run=" + newText);
                loadSpareParts(newText, true); // true : clear thumbnailCursorRecyclerViewAdapter to reinit list view
            }
        }
    }
}
