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
import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoTask;
import com.hibernatus.hibmobtech.model.Task;
import com.hibernatus.hibmobtech.search.SearchHelper;
import com.hibernatus.hibmobtech.search.SearchableActivity;
import com.hibernatus.hibmobtech.utils.FileUtils;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RetrofitError;

import static com.hibernatus.hibmobtech.HibmobtechApplication.OriginOfData.DATABASE;

/**
 * Created by Eric on 09/01/2016.
 */

// c l a s s   M r o R e q u e s t S e a r c h T a s k A c t i v i t y
// -------------------------------------------------------------------

public class MroRequestSearchTaskActivity extends HibmobtechSearchMenuActivity implements ActivityRefresher {
    public static final String TAG = MroRequestSearchTaskActivity.class.getSimpleName();
    public static final String ACTIVITY_FILTER = MroRequestSearchTaskActivity.class.getName();

    protected static RecyclerView recyclerView;

    protected String query = null;
    protected List<Task> mroTaskList = Collections.emptyList();
    protected Activity activity;
    protected SwipeRefreshLayout swipeRefreshLayout;

    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        activity = this;
        setContentView(R.layout.mro_request_list_activity);
        initToolBar();
        initDrawer();

        setSwipeRefreshLayout();
        recyclerView = (RecyclerView) findViewById(R.id.mroRequestRecyclerView);
        recyclerView.setHasFixedSize(true);
        query = getIntent().getStringExtra(SearchableActivity.SEARCH_QUERY);
        Log.i(TAG, "onCreate: query=" + query);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(),
                mroTaskList, this);
        recyclerView.setAdapter(recyclerViewAdapter);

        if (!StringUtils.isNull(query)) {
            loadMroTasks(query);
        }
        else{
            loadMroTasks("");
        }
        Activity currentActivity = ((HibmobtechApplication)this.getApplicationContext()).getCurrentActivity();
        Log.i(TAG, "onCreate: currentActivity=" + currentActivity);
    }

    @Override
    public void onResume() {
        super.onResume();
        trackScreen(TAG);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void clearSavedQueriesForSuggestionsProvider() {
        super.clearSavedQueriesForSuggestionsProvider();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void startActivity(Intent intent) {
        Log.i(TAG, "startActivity : intent.getAction()=" + intent.getAction());
        SearchHelper.setActivityFilter(ACTIVITY_FILTER);

        if (Intent.ACTION_SEARCH.equals(intent.getAction()) ||
                RecognizerIntent.ACTION_RECOGNIZE_SPEECH.equals(intent.getAction())) {
            intent.putExtra(SearchableActivity.SEARCH_ACTIVITY, ACTIVITY_FILTER);
        }
        super.startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        boolean returnCode = super.onCreateOptionsMenu(menu);
        SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadMroTasks(query);
                return true;
            }

            public boolean onQueryTextChange(String newText) {
                query = newText;
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Log.i(TAG, "run: newText=" + query);
                        if (!StringUtils.isNull(query)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadMroTasks(query);
                                }
                            });
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadMroTasks("");
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
    public boolean onSearchRequested() {
        query = getIntent().getStringExtra(SearchableActivity.SEARCH_QUERY);
        Log.i(TAG, "onSearchRequested: query=" + query);
        loadMroTasks(query);
        return super.onSearchRequested();
    }

    @Override
    public void refresh() {
        loadMroTasks(query);
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
        loadMroTasksFromServer("");
    }

    // O t h e r s   m e t h o d s
    // ---------------------------

    public void loadMroTasks(String query) {
        Log.i(TAG, "loadMroTasks: query=" + query);

        if(HibmobtechApplication.dataLoadFrom.equals(DATABASE)){
            loadMroTasksFromDataBase(query);
        }
        else {
            loadMroTasksFromServer(query);
        }
    }

    public void loadMroTasksFromServer(String query) {
        Log.i(TAG, "loadMroTasksFromServer: query=" + query);
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        service.searchTasks(query, new Callback<List<Task>>() {
            @Override
            public void success(List<Task> mroTaskLists, retrofit.client.Response response) {
                if (mroTaskLists == null) return;

                mroTaskList = mroTaskLists;
                RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(),
                        mroTaskList, MroRequestSearchTaskActivity.this);
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

    public void loadMroTasksFromDataBase(String query) {
        Log.i(TAG, "loadMroTasksFromDataBase: query=" + query);

        SQLiteBaseDaoTask SQLiteBaseDaoTask = new SQLiteBaseDaoTask(this);
        SQLiteBaseDaoTask.open();
        List<Task> taskList = SQLiteBaseDaoTask.getTasksByDescription(query);
        for (Task t : taskList){
            Log.i(TAG, "desc=" + t.getDescription());
        }
        mroTaskList = taskList;
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getApplicationContext(),
                taskList, MroRequestSearchTaskActivity.this);
        recyclerView.setAdapter(recyclerViewAdapter);
        SQLiteBaseDaoTask.close();
    }

    // c l a s s   R e c y c l e r V i e w A d a p t e r
    // -------------------------------------------------

    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private final TypedValue typedValue = new TypedValue();
        private int background;
        private List<Task> mroTaskList;
        Activity activity;

        public RecyclerViewAdapter(Context context, List<Task> mroTaskList, Activity activity) {
            this.mroTaskList = mroTaskList;
            this.activity = activity;

            Log.i(TAG, "RecyclerViewAdapter: mroCauseList=" + mroTaskList);
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
            background = typedValue.resourceId;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i(TAG, "onCreateViewHolder");
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.mro_request_search_task_row, parent, false);
            view.setBackgroundResource(background);

            return new ViewHolder(view, mroTaskList, activity);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            final Task task = mroTaskList.get(position);
            Log.i(TAG, "onBindViewHolder: position=" + position + " task.getTitle()=" + task.getDescription());
            viewHolder.mroTaskDescriptionTv.setText(task.getDescription());

            viewHolder.view.setClickable(true);
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MroRequestCurrent.getInstance().getMroRequestCurrent().getOperation().getTasks().add(task);
                    MroRequestCurrent.getInstance().setIsMroRequestCheckable(true);
                    MroRequestCurrent.getInstance().setAdditionalTask(task);
                    Log.d(TAG, "onBindViewHolder: onClick >> " + position + " << task.getTitle()=" + task.getDescription());
                    activity.finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mroTaskList.size();
        }

        // c l a s s   V i e w H o l d e r
        // -------------------------------
        public static class ViewHolder extends RecyclerView.ViewHolder {

            public final View view;
            private final Activity activity;
            public TextView mroTaskDescriptionTv;
            public TextView addIcon;
            private List<Task> mroTaskList;

            public ViewHolder(View itemView, List<Task> mroTaskList, Activity activity) {
                super(itemView);
                this.view = itemView;
                this.mroTaskList = mroTaskList;
                this.activity = activity;
                Log.i(TAG, "ViewHolder");
                mroTaskDescriptionTv = (TextView) itemView.findViewById(R.id.mroRequestTaskRowDescTv);
                addIcon = (TextView) itemView.findViewById(R.id.icon_fa_cross);
                addIcon.setVisibility(itemView.INVISIBLE);
                itemView.setTag(itemView);
            }
        }
    }
}
