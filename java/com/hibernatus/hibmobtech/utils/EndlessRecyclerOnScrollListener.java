package com.hibernatus.hibmobtech.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    public static final String TAG = "EndlessRecyclerOnScroll";

    private int previousTotalItemCount = 0; // The total number of items in the dataset after the last load
    private boolean loading = false; // True if we are still waiting for the last updateCause of data to load.
    private int VISIBLE_THRESHOLD = 1; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem;
    int visibleItemCount;
    int totalItemCount;
    private int currentPage = 0;
    private LinearLayoutManager linearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.linearLayoutManager = linearLayoutManager;
    }

    void logScroll(String prefix, int dx, int dy) {

        Log.i(TAG, prefix +": loading=" + loading
                + " dx="+dx
                + " dy="+dy
                + " currentPage=" + currentPage
                + " totalItemCount=" + totalItemCount
                + " visibleItemCount=" + visibleItemCount
                + " firstVisibleItem=" + firstVisibleItem
                + " previousTotalItemCount=" + previousTotalItemCount);

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = linearLayoutManager.getItemCount();
        firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

        if (totalItemCount == visibleItemCount) {
            return;
        }

        if (!loading && (totalItemCount < previousTotalItemCount)) {
            logScroll("AAA", dx, dy);
            //this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) {
                this.loading = true;
            }
        }
        if (loading) {
            logScroll("BBB", dx, dy);
            // check if current total is greater than previous (diff should be greater than 1, for considering placeholder)
            // and if current total is equal to the total in server
            if (totalItemCount > previousTotalItemCount) {
                Log.i(TAG, "loading is finish : onUpdatedContent the current page number and total item count");
                loading = false;
                previousTotalItemCount = totalItemCount;
                currentPage++;
            }
        }

        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + VISIBLE_THRESHOLD)) {
            // End has been reached
            Log.i(TAG, "need to reload some more data");
            logScroll("CCC", dx, dy);

            loading = true;
            onLoadMore(currentPage + 1);
            //previousTotalItemCount = totalItemCount;
        }
    }

    public void onLoadingStarted() {
        Log.i(TAG, "onLoadingStarted: loading = true");
        loading = true;
    }

    public void onLoadingFinished() {
        Log.i(TAG, "onLoadingFinished: loading = false");
        loading = false;
    }

    public abstract void onLoadMore(int current_page);
}

