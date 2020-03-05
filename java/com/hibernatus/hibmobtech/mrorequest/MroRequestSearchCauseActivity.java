package com.hibernatus.hibmobtech.mrorequest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bleau.hibernatus.mob.util.StringUtils;
import com.hibernatus.hibmobtech.ActivityRefresher;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.HibmobtechSearchMenuActivity;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.model.Cause;
import com.hibernatus.hibmobtech.model.dao.CauseDao;
import com.hibernatus.hibmobtech.model.dao.DaoFactory;
import com.hibernatus.hibmobtech.search.SearchHelper;
import com.hibernatus.hibmobtech.search.SearchableActivity;
import com.hibernatus.hibmobtech.utils.FileUtils;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by Eric on 09/01/2016.
 */

// c l a s s   M r o R e q u e s t S e a r c h C a u s e A c t i v i t y
// ---------------------------------------------------------------------

public class MroRequestSearchCauseActivity extends HibmobtechSearchMenuActivity implements ActivityRefresher{
    public static final String TAG = MroRequestSearchCauseActivity.class.getSimpleName();
    public static final String ACTIVITY_FILTER = MroRequestSearchCauseActivity.class.getName();
    protected static RecyclerView recyclerView;
    protected String query = null;
    protected List<Cause> mroCauseList = Collections.emptyList();
    protected Activity activity;
    public DaoFactory daoFactory = HibmobtechApplication.getDaoFactory();
    protected SwipeRefreshLayout swipeRefreshLayout;


    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.mro_request_list_activity);
        initToolBar();
        initDrawer();

        setSwipeRefreshLayout();
        recyclerView = (RecyclerView) findViewById(R.id.mroRequestRecyclerView);
        recyclerView.setHasFixedSize(true);
        query = getIntent().getStringExtra(SearchableActivity.SEARCH_QUERY);
        Log.d(TAG, "onCreate: query=" + query);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(),
                mroCauseList, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        if (!StringUtils.isNull(query)) {
            loadCausesFromDataBase(query);
        }
        else{
            loadCausesFromDataBase("");
        }

        Activity currentActivity = ((HibmobtechApplication)this.getApplicationContext()).getCurrentActivity();
        Log.d(TAG, "onCreate: currentActivity=" + currentActivity);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        trackScreen(TAG);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onSearchRequested() {
        query = getIntent().getStringExtra(SearchableActivity.SEARCH_QUERY);
        Log.d(TAG, "onSearchRequested: query=" + query);
        loadCausesFromDataBase(query);
        return super.onSearchRequested();
    }

    @Override
    public void refresh() {
        loadCausesFromDataBase(query);
    }

/*    @Override
    public void startActivity(Intent intent) {
        Log.e(TAG, "startActivity : intent.getAction()=" + intent.getAction());
        SearchHelper.setActivityFilter(ACTIVITY_FILTER);

        if (Intent.ACTION_SEARCH.equals(intent.getAction()) ||
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH.equals(intent.getAction())) {
            intent.putExtra(SearchableActivity.SEARCH_ACTIVITY, ACTIVITY_FILTER);
        }
        super.startActivity(intent);
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.e(TAG, "onActivityResult: resultCode=" + resultCode + " requestCode=" + requestCode);
        Log.e(TAG, "onActivityResult: finish !!!! activity=" + this);
        finish();
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        Log.e(TAG, "startActivityForResult : intent.getAction()=" + intent.getAction() + " requestCode=" + requestCode);
        SearchHelper.setActivityFilter(ACTIVITY_FILTER);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            Log.e(TAG, "Intent.ACTION_SEARCH.equals(intent.getAction())");
            intent.putExtra(SearchableActivity.SEARCH_ACTIVITY, ACTIVITY_FILTER);
        }
        else if (RecognizerIntent.ACTION_RECOGNIZE_SPEECH.equals(intent.getAction())) {
            Log.e(TAG, "RecognizerIntent.ACTION_RECOGNIZE_SPEECH.equals(intent.getAction())");
            intent.putExtra(SearchableActivity.SEARCH_ACTIVITY, ACTIVITY_FILTER);
        }
        super.startActivityForResult(intent, requestCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean returnCode = super.onCreateOptionsMenu(menu);
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //loadMroCausesFromServer(query);
                loadCausesFromDataBase(query);

                return true;
            }

            public boolean onQueryTextChange(String newText) {
                query = newText;
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.d(TAG, "run: newText=" + query);
                        if (!StringUtils.isNull(query)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadCausesFromDataBase(query);
                                }
                            });
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadCausesFromDataBase("");
                                }
                            });
                        }
                    }
                }, 500); // 500ms delay before the timer executes the „run“ method from TimerTask
                return true;
            }
        };
        searchView.setOnQueryTextListener(queryTextListener);
        return returnCode;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void clearSavedQueriesForSuggestionsProvider() {
        super.clearSavedQueriesForSuggestionsProvider();
    }

    // S w i p e   R e f r e s h
    // -------------------------
    void setSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "initSwipeRefreshLayout: swipeRefreshLayout.setRefreshing(true)");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "initSwipeRefreshLayout: onRefresh > swipeRefreshLayout.setRefreshing(true)");
                refreshItems();
            }
        });
    }

    void refreshItems() {
        Log.d(TAG, "refreshItems");
        FileUtils.setRefreshing(swipeRefreshLayout, true);
        loadMroCausesFromServer("");
    }

    // O t h e r s   m e t h o d s
    // ---------------------------

    public void loadCausesFromDataBase(String query) {
        Log.d(TAG, "loadCausesFromDataBase: query=" + query);

        CauseDao causeDao = daoFactory.getCauseDao();
        List<Cause> causeList = causeDao.getCauses(query);
        for (Cause c : causeList){
            Log.d(TAG, "desc=" + c.getDescription());
        }
        mroCauseList = causeList;
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(),
                causeList, MroRequestSearchCauseActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public void loadMroCausesFromServer(String query) {
        Log.i(TAG, "loadMroCausesFromServer: query=" + query);
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
            service.searchCauses(query, new Callback<List<Cause>>() {
                @Override
                public void success(List<Cause> mroCauses, retrofit.client.Response response) {
                    if (mroCauses == null) return;

                    mroCauseList = mroCauses;
                    RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(),
                            mroCauseList, MroRequestSearchCauseActivity.this);
                    recyclerView.setAdapter(recyclerViewAdapter);
                    FileUtils.setRefreshing(swipeRefreshLayout, false);
                }

                @Override
                public void failure(RetrofitError error) {
                    FileUtils.setRefreshing(swipeRefreshLayout, false);
                    setRetrofitError(error);
                }
            });
    }


    // c l a s s   R e c y c l e r V i e w A d a p t e r
    // -------------------------------------------------
    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private final TypedValue typedValue = new TypedValue();
        private int background;
        private List<Cause> mroCauseList;
        Activity activity;

        public RecyclerViewAdapter(Context context, List<Cause> mroCauseList, Activity activity) {
            this.mroCauseList = mroCauseList;
            this.activity = activity;

            Log.d(TAG, "RecyclerViewAdapter: mroCauseList=" + mroCauseList);
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
            background = typedValue.resourceId;
        }

        // @ O v e r r i d e   m e t h o d s
        // ---------------------------------

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, "onCreateViewHolder");
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.mro_request_search_cause_row, parent, false);
            view.setBackgroundResource(background);

            return new ViewHolder(view, mroCauseList, activity);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            final Cause cause = mroCauseList.get(position);
            Log.d(TAG, "onBindViewHolder: position=" + position + " cause.getTitle()=" + cause.getDescription());
            viewHolder.mroCauseDescriptionTv.setText(cause.getDescription());

            viewHolder.view.setClickable(true);
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MroRequestCurrent.getInstance().getMroRequestCurrent().getOperation().getCauses().add(cause);
                    MroRequestCurrent.getInstance().setIsMroRequestCheckable(true);
                    MroRequestCurrent.getInstance().setAdditionalCause(cause);
                    Log.e(TAG, "onBindViewHolder: onClick >> " + position
                            + " << cause.getTitle()=" + cause.getDescription());
                    activity.finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mroCauseList.size();
        }

        // c l a s s   V i e w H o l d e r
        // -------------------------------

        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final View view;
            private final Activity activity;
            public TextView mroCauseDescriptionTv;
            public TextView addIcon;
            private List<Cause> mroCauseList;

            public ViewHolder(View itemView, List<Cause> mroCauseList, Activity activity) {
                super(itemView);
                this.view = itemView;
                this.mroCauseList = mroCauseList;
                this.activity = activity;
                Log.d(TAG, "ViewHolder");
                mroCauseDescriptionTv = (TextView) itemView.findViewById(R.id.mroRequestSearchCauseRowDescriptionTextView);
                addIcon = (TextView) itemView.findViewById(R.id.icon_fa_cross);
                addIcon.setVisibility(itemView.INVISIBLE);
                itemView.setTag(itemView);
            }
        }
    }
}
