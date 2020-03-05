package com.hibernatus.hibmobtech.equipment;

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
import com.hibernatus.hibmobtech.machine.MachineCurrent;
import com.hibernatus.hibmobtech.model.Equipment;
import com.hibernatus.hibmobtech.utils.ColorGenerator;
import com.hibernatus.hibmobtech.view.TextDrawable;

// c l a s s   E q ui p m e n t L i s t A da p t e r
// -------------------------------------------------
public class EquipmentListAdapter extends PagedListAdapter<Equipment, RecyclerView.ViewHolder> {
    public static final String TAG = EquipmentListAdapter.class.getSimpleName();
    protected static Bundle bundle;
    protected Context context;
    protected Activity activity;

    public EquipmentListAdapter(Context context,int maxSize, Activity activity) {
        super(maxSize);
        this.context = context;
        this.activity = activity;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (viewHolder instanceof EquipmentListViewHolder) {
            Equipment equipment = dataset.get(position);

            EquipmentListViewHolder equipmentViewHolder = (EquipmentListViewHolder) viewHolder;
            equipmentViewHolder.category.setText(equipment.getCategory());
            equipmentViewHolder.brand.setText(equipment.getBrand());
            equipmentViewHolder.reference.setText(equipment.getReference());
            equipmentViewHolder.equipment = equipment;

            if (equipment.getFormattedPrice() != null) {
                equipmentViewHolder.price.setText(equipment.getFormattedPrice() + " €");
            }
            else {
                equipmentViewHolder.price.setText("");
            }

            String firstLetter = (equipment.getCategory() != null) ?
                    String.valueOf(equipment.getCategory().charAt(0)) : "?";

            //Log.d(TAG, "onBindViewHolder: firstLetter=" + firstLetter);
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(equipment.getCategory());
            TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, color);

            equipmentViewHolder.imageView.setImageDrawable(drawable);
        }
        else {
            ((ProgressViewHolder)viewHolder).progressBar.setIndeterminate(true);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        if (viewType == VIEW_ITEM) {
            View itemView = LayoutInflater
                    .from(parent.getContext())
                    .inflate(R.layout.equipment_list_row, parent, false);
            return new EquipmentListViewHolder(context, itemView, activity);
        }
        else {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.progress_bar, parent, false);

            return new PagedListAdapter.ProgressViewHolder(v);
        }
    }

    // c l a s s  E q u i p m e n t L i s t V i e w H o l d e r
    // --------------------------------------------------------
    public static class EquipmentListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // All values are updateCause by onBindViewHolder
        public Equipment equipment;

        Context context;
        Activity activity;
        public ImageView imageView;
        public TextView category;
        public TextView brand;
        public TextView reference;
        public TextView price;

        public EquipmentListViewHolder(Context context, View itemView, Activity activity) {
            super(itemView);
            this.context = context;
            this.activity = activity;
            imageView = (ImageView) itemView.findViewById(R.id.equipmentListRowAvatar);
            category = (TextView)  itemView.findViewById(R.id.equipmentListRowCategoryTextView);
            brand = (TextView)  itemView.findViewById(R.id.equipmentListRowBrandTextView);
            reference = (TextView)  itemView.findViewById(R.id.equipmentListRowReferenceTextView);
            price = (TextView)  itemView.findViewById(R.id.equipmentListRowPriceTextView);

            itemView.setTag(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: adapterPosition=" + getAdapterPosition() + " equipment.getReference()=" + equipment.getReference());
            EquipmentCurrent.getInstance().setEquipmentCurrent(equipment);
            MachineCurrent.getInstance().getMachineCurrent().setEquipment(equipment);
            activity.finish();
        }
    }
}


