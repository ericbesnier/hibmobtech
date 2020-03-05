package com.hibernatus.hibmobtech.sparepart;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bleau.hibernatus.mob.util.PagedListAdapter;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.model.SparePart;
import com.hibernatus.hibmobtech.model.SpareParts;
import com.hibernatus.hibmobtech.mrorequest.MroRequestCurrent;
import com.hibernatus.hibmobtech.utils.ColorGenerator;
import com.hibernatus.hibmobtech.view.TextDrawable;

// c l a s s   S p a r e P a r t L i s t A d a p t e r
// ---------------------------------------------------
public class SparePartListAdapter extends PagedListAdapter<SparePart, RecyclerView.ViewHolder> {
    public static final String TAG = SparePartListAdapter.class.getSimpleName();
    protected static Bundle bundle;
    protected Context context;
    protected Activity activity;

    public SparePartListAdapter(Context context, int maxSize, Activity activity) {
        super(maxSize);
        Log.d(TAG, "SparePartListAdapter: maxSize=" + maxSize);

        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        //Log.i(TAG, "onBindViewHolder: viewHolder=" + viewHolder);

        if (viewHolder instanceof SparePartListViewHolder) {
            SparePart sparePart = dataset.get(position);

            SparePartListViewHolder sparePartViewHolder = (SparePartListViewHolder) viewHolder;
            if(sparePartViewHolder.category != null)
                sparePartViewHolder.category.setText(sparePart.getCategory());
            if(sparePartViewHolder.description != null)
                sparePartViewHolder.description.setText(sparePart.getDescription().trim());
            if(sparePartViewHolder.brand != null)
                sparePartViewHolder.brand.setText(sparePart.getBrand());
            if(sparePartViewHolder.reference != null)
                sparePartViewHolder.reference.setText("Réf. " + sparePart.getReference());
            sparePartViewHolder.sparePart = sparePart;

            if (sparePart.getFormattedPrice() != null) {
                sparePartViewHolder.price.setText(sparePart.getFormattedPrice() + " €");
            }
            else {
                sparePartViewHolder.price.setText("");
            }

            String firstLetter = (sparePart.getCategory() != null) ?
                    String.valueOf(sparePart.getCategory().charAt(0)) : "?";

            //Log.d(TAG, "onBindViewHolder: firstLetter=" + firstLetter);
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(sparePart.getCategory());
            TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, color);
            if(sparePartViewHolder.imageView != null)
                sparePartViewHolder.imageView.setImageDrawable(drawable);
        }
        else {
            ((ProgressViewHolder)viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Log.i(TAG, "onCreateViewHolder");

        Context context = parent.getContext();
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.spare_part_list_row, parent, false);
            return new SparePartListViewHolder(context, itemView, activity);
        }
        else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_bar, parent, false);

            return new ProgressViewHolder(v);
        }
    }

    // c l a s s  E q u i p m e n t L i s t V i e w H o l d e r
    // --------------------------------------------------------
    public static class SparePartListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // All values are updateCause by onBindViewHolder
        public SparePart sparePart;

        Context context;
        Activity activity;
        public TextView icon;
        public ImageView imageView;
        public TextView description;
        public TextView category;
        public TextView brand;
        public TextView reference;
        public TextView price;

        public SparePartListViewHolder(Context context, View itemView, Activity activity) {
            super(itemView);
            //Log.i(TAG, "SparePartListViewHolder");

            this.context = context;
            this.activity = activity;
            imageView = (ImageView) itemView.findViewById(R.id.sparePartListRowAvatar);
            description = (TextView)  itemView.findViewById(R.id.sparePartListRowDescriptionTextView);
            category = (TextView)  itemView.findViewById(R.id.sparePartListRowCategoryTextView);
            brand = (TextView)  itemView.findViewById(R.id.sparePartListRowBrandTextView);
            reference = (TextView)  itemView.findViewById(R.id.sparePartListRowReferenceTextView);
            price = (TextView)  itemView.findViewById(R.id.sparePartListRowPriceTextView);

            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "onClick: adapterPosition=" + getAdapterPosition() + " sparePart.getReference()=" + sparePart.getReference());
            SparePartCurrent.getInstance().setSparePartCurrent(sparePart);
            //MachineCurrent.getInstance().getMachineCurrent().setEquipment(sparePart);
            SpareParts spareParts = new SpareParts();
            spareParts.setQuantity(1);
            spareParts.setSparePart(sparePart);
            MroRequestCurrent.getInstance().getMroRequestCurrent().getOperation().getSpareParts().add(spareParts);
            MroRequestCurrent.getInstance().setIsMroRequestCheckable(true);
            MroRequestCurrent.getInstance().setAdditionalSpareParts(spareParts);
            activity.finish();
        }
    }
}


