package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;

import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID;

/**
 * Created by Eric on 13/01/2017.
 */

// c l a s s   C u r s o r R e c y c l e r V i e w A d a p t e r
//
// -------------------------------------------------------------

public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {
    public static final String TAG = CursorRecyclerViewAdapter.class.getSimpleName();

    protected Context context;
    private Cursor cursor;
    private boolean isDataValid;
    private int rowIdColumn;
    private DataSetObserver dataSetObserver;

    public CursorRecyclerViewAdapter(Context context, Cursor cursor) {
        Log.d(TAG, "CursorRecyclerViewAdapter: cursor=" + cursor);
        if(cursor != null)
            Log.d(TAG, "CursorRecyclerViewAdapter: cursor.getColumnNames()=" + cursor.getColumnNames());
        this.context = context;
        this.cursor = cursor;
        isDataValid = cursor != null;
        rowIdColumn = isDataValid ? this.cursor.getColumnIndex(COL_ID) : -1;
        dataSetObserver = new NotifyingDataSetObserver(this);
        if (this.cursor != null) {
            this.cursor.registerDataSetObserver(dataSetObserver);
        }
    }

    public Cursor getCursor() {
        Log.d(TAG, "getCursor: cursor=" + cursor);
        if(cursor != null)
            Log.d(TAG, "getCursor: cursor.getColumnNames()=" + cursor.getColumnNames());
        return cursor;
    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount");
        if(cursor != null)
            Log.d(TAG, "getItemCount: cursor=" + cursor + " cursor.getColumnNames()=" + cursor.getColumnNames());
        if (isDataValid && cursor != null) {
            Log.d(TAG, "getItemCount: isDataValid && cursor != null : itemCount =" + cursor.getCount());
            return cursor.getCount();
        }
        else
            Log.d(TAG, "getItemCount: data not valid or cursor == null");

        return 0;
    }

    @Override
    public long getItemId(int position) {
        Log.d(TAG, "getItemId: position=" + position + " cursor=" + cursor);
        if(cursor != null)
            Log.d(TAG, "getItemId: cursor.getColumnNames()=" + cursor.getColumnNames());
        if (isDataValid && cursor != null && cursor.moveToPosition(position)) {
            Log.d(TAG, "getItemId: itemId=" + cursor.getLong(rowIdColumn));
            return cursor.getLong(rowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        return null;
    }

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        Log.d(TAG, "onBindViewHolder");
        if (!isDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(viewHolder, cursor);
    }


    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
/*    public void changeCursor(Cursor cursor) {
        Log.d(TAG, "changeCursor: cursor=" + cursor + " cursor.getColumnNames()=" + cursor.getColumnNames());
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }*/


    public Cursor swapCursor(Cursor newCursor) {
        Log.d(TAG, "swapCursor: cursor=" +  cursor + " newCursor=" + newCursor);
        if(cursor != null)
            Log.d(TAG, "swapCursor: cursor.getColumnNames()=" + cursor.getColumnNames());

        if (newCursor == cursor) {
            return null;
        }
        final Cursor oldCursor = cursor;
        if (oldCursor != null && dataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(dataSetObserver);
        }
        cursor = newCursor;
        if (cursor != null) {
            if (dataSetObserver != null) {
                cursor.registerDataSetObserver(dataSetObserver);
            }
            rowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            isDataValid = true;
            notifyDataSetChanged();
        } else {
            rowIdColumn = -1;
            isDataValid = false;
            notifyDataSetChanged();
            //There is no notifyDataSetInvalidated() method in RecyclerView.MroRequestListFragmentPagerAdapter
        }
        return oldCursor;
    }

    public void setDataValid(boolean mDataValid) {
        Log.d(TAG, "setDataValid");
        this.isDataValid = mDataValid;
    }

    // c l a s s   N o t i f y i n g D a t a S e t O b s e r v e r
    // -----------------------------------------------------------

    private class NotifyingDataSetObserver extends DataSetObserver {

        private RecyclerView.Adapter adapter;

        public NotifyingDataSetObserver(RecyclerView.Adapter adapter) {
            Log.d(TAG, "NotifyingDataSetObserver: thumbnailCursorRecyclerViewAdapter="+ adapter);
            this.adapter = adapter;
        }

        @Override
        public void onChanged() {
            super.onChanged();
            Log.d(TAG, "NotifyingDataSetObserver: onChanged");
            ((CursorRecyclerViewAdapter) adapter).setDataValid(true);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            Log.d(TAG, "NotifyingDataSetObserver: onInvalidated");
            super.onInvalidated();
            ((CursorRecyclerViewAdapter) adapter).setDataValid(false);
        }
    }
}
