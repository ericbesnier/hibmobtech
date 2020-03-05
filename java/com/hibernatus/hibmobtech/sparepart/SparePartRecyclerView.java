package com.hibernatus.hibmobtech.sparepart;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.bleau.hibernatus.mob.util.Page;
import com.bleau.hibernatus.mob.util.PagedListRecyclerView;
import com.bleau.hibernatus.mob.util.StringUtils;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.model.SparePart;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;

/**
 * Created by tgo on 11/12/15.
 */
public class SparePartRecyclerView extends PagedListRecyclerView<SparePart> {

    private static final String TAG = SparePartRecyclerView.class.getSimpleName();

    private String query;

    public SparePartRecyclerView(Context context) {
        super(context);
    }

    public SparePartRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SparePartRecyclerView(Context context, AttributeSet attrs, int defStyle) {
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
    protected void load(int page, int pageSize, Callback<Page<SparePart>> callback) {
        Log.i(TAG, "load page=" + page + ", with search=" + query);

        String sort = "description_ASC";
        String newQuery = query.replaceAll(" ", "+");
        Log.d(TAG, "load: query=" + query + " newQuery=" + newQuery);

        SparePartApiService service = HibmobtechApplication.getRestClient().getSparePartService();
        if (StringUtils.isNull(query)) {
            service.getSpareParts(page, pageSize, sort, callback);
        }
        else {
            Map<String,String> map =  new HashMap<>();
            map.put("sdesc", newQuery);
            service.searchSpareParts(page, pageSize, sort, map, callback);
        }
    }


}
