package com.hibernatus.hibmobtech.equipment;

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
import com.hibernatus.hibmobtech.model.Equipment;
import com.hibernatus.hibmobtech.search.SearchHelper;
import com.hibernatus.hibmobtech.search.SearchableActivity;
import com.hibernatus.hibmobtech.utils.ParentClassUtils;

import retrofit.RetrofitError;
import retrofit.client.Response;

public class EquipmentListActivity extends HibmobtechSearchMenuActivity {
    public static final String TAG = EquipmentListActivity.class.getSimpleName();
    public static final String ACTIVITY_FILTER = EquipmentListActivity.class.getName();

    protected EquipmentRecyclerView recyclerView;
    protected EquipmentListAdapter equipmentListAdapter;
    private SearchTipThread searchTipThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.equipment_list_activity);
        initToolBar();
        initDrawer();

        recyclerView = (EquipmentRecyclerView) findViewById(R.id.equipmentListActivityRecyclerView);
        recyclerView.setHasFixedSize(true);

        query = getIntent().getStringExtra(SearchableActivity.SEARCH_QUERY);
        recyclerView.setSearchQuery(query);
        Log.i(TAG, "onCreate: query=" + query);

        recyclerView.initParams(30, 5); // endlessscroll
        equipmentListAdapter = new EquipmentListAdapter(getApplicationContext(), 100, this);
        recyclerView.setAdapter(equipmentListAdapter);
        if (!StringUtils.isNull(query)) {
            loadEquipments(query);
        }
        else{
            loadEquipments("");
        }
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

    public void loadEquipments(final String query, boolean clearAdapter) {
        Log.i(TAG, "loadEquipments: query=" + query);
        recyclerView.loadInitialPage(new PagedListRecyclerView.OnLoadingListener<Equipment>() {
            @Override
            public void start() {
                Log.i(TAG, "START loadEquipments: query=" + query);
                // TODO verifier que cette commande est vraiment utile
                recyclerView.setSearchQuery(query);
            }

            @Override
            public void finish(Page<Equipment> page, Response response) {
                if (page.getNumberOfElements() == 0) {
                    String message = String.format("Aucun équipement ne correspond au critère \"%s\".", query);
                    AndroidUtils.showToast(EquipmentListActivity.this, message);
                }
            }

            @Override
            public void error(RetrofitError error) {
                String message = String.format("Erreur de connexion - vérifiez votre login");
                AndroidUtils.showToast(EquipmentListActivity.this, message);
            }
        }, clearAdapter); // clear thumbnailCursorRecyclerViewAdapter to reinit list view
    }

    public void loadEquipments(final String query) {
        Log.i(TAG, "loadEquipments: query=" + query);
        recyclerView.loadInitialPage(new PagedListRecyclerView.OnLoadingListener<Equipment>() {
            @Override
            public void start() {
                Log.i(TAG, "START loadEquipments: query=" + query);
                // TODO verifier que cette commande est vraiment utile
                recyclerView.setSearchQuery(query);
            }

            @Override
            public void finish(Page<Equipment> page, Response response) {
                if (page.getNumberOfElements() == 0) {
                    String message = String.format("Aucun équipement ne correspond au critère \"%s\".", query);
                    AndroidUtils.showToast(EquipmentListActivity.this, message);
                }
            }

            @Override
            public void error(RetrofitError error) {
                String message = String.format("Erreur de connexion - vérifiez votre login");
                AndroidUtils.showToast(EquipmentListActivity.this, message);
            }
        });
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode){
        Log.i(TAG, "startActivityForResult : intent.getAction()=" + intent.getAction());
        ParentClassUtils.searchableParentClass = getClass();

        SearchHelper.setActivityFilter(ACTIVITY_FILTER);

        if (Intent.ACTION_SEARCH.equals(intent.getAction()) ||
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH.equals(intent.getAction())) {
            Log.i(TAG, "startActivityForResult : setting search activity");
            intent.putExtra(SearchableActivity.SEARCH_ACTIVITY, ACTIVITY_FILTER);
        }
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: resultCode=" + resultCode + " requestCode=" + requestCode + " data=" + data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean returnCode = super.onCreateOptionsMenu(menu);

        searchView = (SearchView) menu.findItem(R.id.searchItemId).getActionView();
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setQueryRefinementEnabled(true);
        searchView.setSearchableInfo(searchableInfo);

        query = getIntent().getStringExtra(SearchableActivity.SEARCH_QUERY);
        Log.d(TAG, "onCreateOptionsMenu: query=" + query);
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
                loadEquipments(newText, true); // true : clear thumbnailCursorRecyclerViewAdapter to reinit list view
            }
        }
    }
}
