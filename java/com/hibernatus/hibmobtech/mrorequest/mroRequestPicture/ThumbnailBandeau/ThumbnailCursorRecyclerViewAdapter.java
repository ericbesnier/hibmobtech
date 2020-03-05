package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.ThumbnailBandeau;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.CursorRecyclerViewAdapter;
import com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.PictureRead.MroRequestPictureReadActivity;

/**
 * Created by Eric on 13/01/2017.
 */

public class ThumbnailCursorRecyclerViewAdapter extends CursorRecyclerViewAdapter {
    public static final String TAG = ThumbnailCursorRecyclerViewAdapter.class.getSimpleName();

    Context context;

    public ThumbnailCursorRecyclerViewAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        Log.d(TAG, "ThumbnailCursorRecyclerViewAdapter: cursor=" + cursor);
        this.context = context;
    }

    @Override
    public long getItemId(int position) {
        Log.d(TAG, "getItemId: position=" + position);
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(context).inflate(R.layout.mro_request_thumbnail_image_view, parent, false);
        return new ThumbnailViewHolder(context, view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, Cursor cursor) {
        Log.d(TAG, "onBindViewHolder: viewHolder=" + viewHolder + " cursor=" + cursor);
        ThumbnailViewHolder thumbnailViewHolder = (ThumbnailViewHolder) viewHolder;
        cursor.moveToPosition(cursor.getPosition());
        thumbnailViewHolder.setData(cursor);

        ((ThumbnailViewHolder) viewHolder).imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onBindViewHolder: onClick: viewHolder.getAdapterPosition()=" + viewHolder.getAdapterPosition());
                Intent intent = new Intent(context, MroRequestPictureReadActivity.class);
                intent.putExtra("position", viewHolder.getAdapterPosition());
                intent.putExtra("pictureId", viewHolder.getItemId());
                Log.d(TAG, "onBindViewHolder: onClick: viewHolder.getItemId()=" + viewHolder.getItemId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        int itemCount = super.getItemCount();
        Log.d(TAG, "getItemCount: itemCount=" + itemCount);
        return itemCount;
    }

    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType");
        return 0;
    }
}
