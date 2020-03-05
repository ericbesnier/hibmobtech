package com.hibernatus.hibmobtech.site;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.bleau.hibernatus.mob.util.Page;
import com.bleau.hibernatus.mob.util.PagedListRecyclerView;
import com.bleau.hibernatus.mob.util.StringUtils;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.model.Site;

import retrofit.Callback;

/**
 * Created by tgo on 11/12/15.
 */
public class SiteRecyclerView extends PagedListRecyclerView<Site> {

    private static final String TAG = SiteRecyclerView.class.getSimpleName();

    private String query;

    public SiteRecyclerView(Context context) {
        super(context);
    }

    public SiteRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SiteRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setSearchQuery(String query) {
        this.query = query;
    }

    /**
     *
     * @param page
     * @param pageSize
     * @param callback
     */
    @Override
    protected void load(int page, int pageSize, Callback<Page<Site>> callback) {
        Log.i(TAG, "load page=" + page + ", with search=" + query);

        SiteApiService service = HibmobtechApplication.getRestClient().getSiteService();
        if (StringUtils.isNull(query)) {
            service.getSites(page, pageSize, callback);
        }
        else {
            service.searchSites(page, pageSize, query, callback);
        }
    }
}
