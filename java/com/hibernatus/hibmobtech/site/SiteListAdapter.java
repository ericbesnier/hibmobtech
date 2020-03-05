package com.hibernatus.hibmobtech.site;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bleau.hibernatus.mob.util.PagedListAdapter;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.map.MapActivity;
import com.hibernatus.hibmobtech.model.LatLng;
import com.hibernatus.hibmobtech.model.Site;
import com.hibernatus.hibmobtech.utils.ColorGenerator;
import com.hibernatus.hibmobtech.utils.ItemClickListener;
import com.hibernatus.hibmobtech.view.TextDrawable;


public class SiteListAdapter extends PagedListAdapter<Site, RecyclerView.ViewHolder> {
    public static final String TAG = SiteListAdapter.class.getSimpleName();

    private Context context;
    protected Activity activity;

    public SiteListAdapter(Context context, int maxSize, Activity activity) {
        super(maxSize);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof SiteListViewHolder) {
            //Log.i(TAG, "onBindViewHolder: position=" + position + " siteList.size()=" + siteList.size());
            Site site = dataset.get(position);
            SiteListViewHolder siteViewHolder = (SiteListViewHolder)viewHolder;

            siteViewHolder.site = site;
            siteViewHolder.name.setText(site.getName());
            siteViewHolder.address1.setText(site.getAddress().getAdressPart1());
            siteViewHolder.address2.setText(site.getAddress().getAdressPart2());

            String firstLetter = (site.getName() != null) ?
                    String.valueOf(site.getName().charAt(0)) : "?";

            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(firstLetter);
            TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, color);
            siteViewHolder.imageView.setImageDrawable(drawable);


            Log.v(TAG, "onBindViewHolder: nom=" + site.getName()
                    + " Catégorie=" + (site.getCategory() == null ? "null" : site.getCategory().getId()));

            siteViewHolder.setClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                    if (isLongClick) {
                        if(!((Activity) context).isFinishing())
                            Toast.makeText(context, "#"
                                            + position + " - "
                                            + position + " (Long click)",
                                    Toast.LENGTH_SHORT).show();
                    } else {
                        if(!((Activity) context).isFinishing())
                            Toast.makeText(context, "#"
                                            + position + " - "
                                            + position,
                                    Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else {
            ((ProgressViewHolder)viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.site_list_row, parent, false);
            return new SiteListViewHolder(itemView);
        }
        else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_bar, parent, false);

            return new PagedListAdapter.ProgressViewHolder(v);
        }
    }

    public static class SiteListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        public Site site;
        public ImageView imageView;
        public TextView name;
        public TextView address1;
        public TextView address2;
        public ImageView iconMap;
        private ItemClickListener clickListener;

        public SiteListViewHolder(View itemView) {
            super(itemView);
            iconMap = (ImageView) itemView.findViewById(R.id.site_list_row_map_icon);
            imageView = (ImageView) itemView.findViewById(R.id.siteListRowAvatar);
            name = (TextView)  itemView.findViewById(R.id.site_list_row_name_text);
            address1 = (TextView)  itemView.findViewById(R.id.site_list_row_adress1_text);
            address2 = (TextView)  itemView.findViewById(R.id.site_list_row_adress2_text);

            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            iconMap.setOnClickListener(this);
            iconMap.setVisibility(View.GONE);
            name.setOnClickListener(this);
            address1.setOnClickListener(this);
            address2.setOnClickListener(this);
        }

        public void setClickListener(ItemClickListener itemClickListener) {
            this.clickListener = itemClickListener;
        }

        @Override
        public void onClick(View view) {
            Log.i(TAG, "onClick: view.getId()=" + view.getId()
                    + " iconMap.getId()=" + iconMap.getId());

            if (view.getId() == iconMap.getId()){
                Intent siteMapActivity = new Intent(view.getContext(), MapActivity.class);
                //LatLng latLng = SiteUtils.getLatLng(view.getContext(), site);
                LatLng latLng = site.getGeoloc();
                Log.i(TAG, "onClick: latLng.getLat()=" + latLng.getLat()
                        + " latLng.getLng()=" + latLng.getLng());
                siteMapActivity.putExtra("lat", latLng.getLat());
                siteMapActivity.putExtra("long", latLng.getLng());

                view.getContext().startActivity(siteMapActivity);
            }
            else {
                Intent intent = new Intent(view.getContext(), SiteActivity.class);
                SiteCurrent.getInstance().setSiteCurrent(site);
                view.getContext().startActivity(intent);
            }
        }
        @Override
        public boolean onLongClick(View view) {
            clickListener.onClick(view, getPosition(), true);
            SiteCurrent.getInstance().setSiteCurrent(site);
            return true;
        }
    }
}