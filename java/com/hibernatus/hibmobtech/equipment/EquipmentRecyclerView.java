package com.hibernatus.hibmobtech.equipment;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.bleau.hibernatus.mob.util.Page;
import com.bleau.hibernatus.mob.util.PagedListRecyclerView;
import com.bleau.hibernatus.mob.util.StringUtils;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.model.Equipment;

import retrofit.Callback;

/**
 * Created by tgo on 11/12/15.
 */
public class EquipmentRecyclerView extends PagedListRecyclerView<Equipment> {

    private static final String TAG = EquipmentRecyclerView.class.getSimpleName();

    private String query;

    public EquipmentRecyclerView(Context context) {
        super(context);
    }

    public EquipmentRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EquipmentRecyclerView(Context context, AttributeSet attrs, int defStyle) {
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
    protected void load(int page, int pageSize, Callback<Page<Equipment>> callback) {
        Log.i(TAG, "load page=" + page + ", with search=" + query);

        EquipmentApiService service = HibmobtechApplication.getRestClient().getEquipmentService();
        if (StringUtils.isNull(query)) {
            service.getEquipments(page, pageSize, callback);
        }
        else {
            service.searchEquipments(page, pageSize, query, callback);
        }
    }


}
