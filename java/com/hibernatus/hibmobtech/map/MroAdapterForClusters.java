package com.hibernatus.hibmobtech.map;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.model.Operation;
import com.hibernatus.hibmobtech.mrorequest.MroRequestActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.hibernatus.hibmobtech.model.MroRequest.Status.ASSIGNED;
import static com.hibernatus.hibmobtech.model.MroRequest.Status.FINISHED;
import static com.hibernatus.hibmobtech.model.MroRequest.Status.IN_PROGRESS;
import static com.hibernatus.hibmobtech.model.MroRequest.Status.OPENED;

/**
 * Created by Eric on 14/12/2016.
 */

public class MroAdapterForClusters extends RecyclerView.Adapter<MroAdapterForClusters.ViewHolder>{
    public static final String TAG = MroAdapterForClusters.class.getSimpleName();
    Context context;
    private List<MroRequest> mroRequestList;


    public MroAdapterForClusters(Context context, List<MroRequest> mroRequestList) {
        this.mroRequestList = mroRequestList;
        this.context = context;
        Log.d(TAG, "MroAdapterForClusters: mroRequestList=" + mroRequestList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.map_activity_bottom_sheet_row, parent, false);
        Log.d(TAG, "onCreateViewHolder: view=" + view);
        return new ViewHolder(view, mroRequestList);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Log.d(TAG, "onBindViewHolder:  position=" + position);

        final MroRequest mroRequest = mroRequestList.get(position);
        viewHolder.mapActivityBottomSheetIconImageView.setImageResource(mroRequest.getProfilePhoto());
        viewHolder.mapActivityBottomSheetNameTextView.setText(mroRequest.getName());
        viewHolder.mapActivityBottomSheetIdTextView.setText("Intervention No " + mroRequest.getId().toString());

        Operation operation = mroRequest.getOperation();
        Date date;
        SimpleDateFormat mroRequestDateSdf;
        SimpleDateFormat mroRequestHourSdf = new SimpleDateFormat("HH:mm");
        String mroRequestDateString;
        String mroRequestHourString;

        if(mroRequest.getStatus() == OPENED) {
            mroRequestDateSdf = new SimpleDateFormat("'Ouverte' dd MMM");
            date = mroRequest.getRequestDate();
            if(date != null) {
                mroRequestDateString = mroRequestDateSdf.format(date);
                mroRequestHourString = mroRequestHourSdf.format(date);
                if (mroRequestHourString.equals("00:00")) {
                    viewHolder.mapActivityBottomSheetOperationDateTextView.setText(mroRequestDateString);
                } else {
                    viewHolder.mapActivityBottomSheetOperationDateTextView.setText(mroRequestDateString + " à " + mroRequestHourString);
                }
            }
        }
        else if(mroRequest.getStatus() == ASSIGNED) {
            mroRequestDateSdf = new SimpleDateFormat("'Planifiée' dd MMM");
            if(operation != null) {
                date = operation.getDate();
                if(date != null) {
                    mroRequestDateString = mroRequestDateSdf.format(date);
                    mroRequestHourString = mroRequestHourSdf.format(date);
                    if (mroRequestHourString.equals("00:00")) {
                        viewHolder.mapActivityBottomSheetOperationDateTextView.setText(mroRequestDateString);
                    } else {
                        viewHolder.mapActivityBottomSheetOperationDateTextView.setText(mroRequestDateString + " à " + mroRequestHourString);
                    }
                }
            }
            else{
                viewHolder.mapActivityBottomSheetOperationDateTextView.setText("");
            }
        }
        else if(mroRequest.getStatus() == IN_PROGRESS) {
            mroRequestDateSdf = new SimpleDateFormat("'Démarrée' dd MMM");
            if(operation != null) {
                date = operation.getDate();
                if(date != null) {
                    mroRequestDateString = mroRequestDateSdf.format(date);
                    mroRequestHourString = mroRequestHourSdf.format(date);
                    if (mroRequestHourString.equals("00:00")) {
                        viewHolder.mapActivityBottomSheetOperationDateTextView.setText(mroRequestDateString);
                    } else {
                        viewHolder.mapActivityBottomSheetOperationDateTextView.setText(mroRequestDateString + " à " + mroRequestHourString);
                    }
                }
            }
            else{
                viewHolder.mapActivityBottomSheetOperationDateTextView.setText("");
            }
        }
        else if(mroRequest.getStatus() == FINISHED) {
            mroRequestDateSdf = new SimpleDateFormat("'Terminée' dd MMM");
            if(operation != null) {
                date = operation.getDate();
                if(date != null) {
                    mroRequestDateString = mroRequestDateSdf.format(date);
                    mroRequestHourString = mroRequestHourSdf.format(date);
                    if (mroRequestHourString.equals("00:00")) {
                        viewHolder.mapActivityBottomSheetOperationDateTextView.setText(mroRequestDateString);
                    } else {
                        viewHolder.mapActivityBottomSheetOperationDateTextView.setText(mroRequestDateString + " à " + mroRequestHourString);
                    }
                }
            }
            else{
                viewHolder.mapActivityBottomSheetOperationDateTextView.setText("");
            }
        }
        viewHolder.view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, " onClick: view=" + v);
                Intent intent = new Intent(MroAdapterForClusters.this.context, MroRequestActivity.class);
                Log.d(TAG, "getInfoWindow: onItemClick: mroRequest.getId()=" + mroRequest.getId());
                intent.putExtra(MroRequest.MRO_REQUEST_ID_KEY, mroRequest.getId());
                MroAdapterForClusters.this.context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount:  mroRequestList.size()=" + mroRequestList.size());
        return mroRequestList.size();
    }


    // c l a s s   V i e w H o l d e r
    // -------------------------------
    public class ViewHolder extends RecyclerView.ViewHolder{
        public View view;
        public ImageView mapActivityBottomSheetIconImageView;
        public TextView mapActivityBottomSheetNameTextView;
        public TextView mapActivityBottomSheetIdTextView;
        public TextView mapActivityBottomSheetOperationDateTextView;
        private List<MroRequest> mroRequestList;

        public ViewHolder(View itemView, List<MroRequest> mroRequestList) {
            super(itemView);
            Log.d(TAG, "ViewHolder:  mmroRequestList=" + mroRequestList);

            this.mroRequestList = mroRequestList;
            view = itemView;
            mapActivityBottomSheetIconImageView = (ImageView) view.findViewById(R.id.mapActivityBottomSheetIconImageView);
            mapActivityBottomSheetNameTextView = (TextView) view.findViewById(R.id.mapActivityBottomSheetNameTextView);
            mapActivityBottomSheetIdTextView = (TextView) view.findViewById(R.id.mapActivityBottomSheetIdTextView);
            mapActivityBottomSheetOperationDateTextView = (TextView) view.findViewById(R.id.mapActivityBottomSheetOperationDateTextView);
            itemView.setTag(itemView);
        }
    }
}
