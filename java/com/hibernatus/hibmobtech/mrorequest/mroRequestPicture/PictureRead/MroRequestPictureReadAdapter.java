package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.PictureRead;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.mrorequest.MroRequestCurrent;
import com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.PictureUtils;
import com.hibernatus.hibmobtech.network.LoggingInterceptor;
import com.hibernatus.hibmobtech.network.RestClient;
import com.hibernatus.hibmobtech.network.SessionRequestInterceptor;
import com.hibernatus.hibmobtech.tracking.TrackingApiKeyInterceptor;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.CookieStore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

// c l a s s   m r o R e q u e s t P i c t u r e R e a d A d a p t e r
// -------------------------------------------------------------------

public class MroRequestPictureReadAdapter extends PagerAdapter {
    public static final String TAG = MroRequestPictureReadAdapter.class.getSimpleName();
    protected Activity activity;
    protected LayoutInflater layoutInflater;
    protected Context context;
    protected MroRequestClickPagerPictureListerner mroRequestClickPagerPictureListerner;
    protected MroRequest mroRequestCurrent;
    protected ImageView mroRequestPictureReadImageView;
    protected TextView mroRequestPictureReadTitleTextView;
    protected TextView mroRequestPictureReadTimeTextView;
    protected View mroRequestPictureTouchView;
    protected Cursor cursor;
    protected PictureUtils pictureUtils;
    protected OkHttpClient okHttpClient;
    protected Picasso.Builder picassoBuilder;
    protected int screenWidth;
    protected int screenHeight;

    // C o n s t r u c t o r
    // ---------------------
    public MroRequestPictureReadAdapter(Activity activity, Context context, Cursor cursor) {
        this.activity = activity;
        this.context = context;
        this.cursor = cursor;
        this.mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        pictureUtils = new PictureUtils(context);
        Log.d(TAG, "mroRequestCurrent=" + mroRequestCurrent + " cursor=" + cursor);

        // HTTP client
        okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(60, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(60, TimeUnit.SECONDS);

        // Manage 401 error by retrying the credentials stored in DataStore
        okHttpClient.setAuthenticator(new RestClient.RetryAuthenticator());

        okHttpClient.interceptors().add(new LoggingInterceptor());
        okHttpClient.networkInterceptors().add(new SessionRequestInterceptor());
        okHttpClient.networkInterceptors().add(new TrackingApiKeyInterceptor());

        // Manage cookies in the store
        CookieStore cookieStore = HibmobtechApplication.getCookieStore();
        CookieManager cookieManager = new CookieManager(cookieStore, CookiePolicy.ACCEPT_ALL);
        okHttpClient.setCookieHandler(cookieManager);

        picassoBuilder = new Picasso.Builder(context);
        picassoBuilder.downloader(new OkHttpDownloader(okHttpClient));
        picassoBuilder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                exception.printStackTrace();
            }
        });

        screenWidth = pictureUtils.getScreenWidth();
        screenHeight = pictureUtils.getScreenHeight();
    }

    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------
    @Override
    public int getCount() {
        if(cursor != null){
            //Log.d(TAG, "getCount: cursor=" + cursor + " cursor.getCount()=" + cursor.getCount());
            return cursor.getCount();
        }
        else {
            Log.d(TAG, "getCount: cursor == null !!");
            return 0;
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d(TAG, "instantiateItem: position=" + position);
        layoutInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mroRequestPictureTouchView = layoutInflater.inflate(
                R.layout.mro_request_image_read_activity, container,false);
        if(cursor != null) {
            Log.d(TAG, "instantiateItem: cursor.getCount()=" + cursor.getCount());
            cursor.moveToPosition(position);
            mroRequestPictureReadImageView = (ImageView) mroRequestPictureTouchView.findViewById(
                    R.id.mroRequestImageReadActivityImageView);
            String pictureId = cursor.getString(cursor.getColumnIndex(SQLiteBaseDaoFactory.COL_ID));
            String mroRequestId = cursor.getString(cursor.getColumnIndex(SQLiteBaseDaoFactory.COL_ID_MRO_REQUEST));
            String apiUrl = HibmobtechApplication.getConfig().getApiUrl();
            String url = apiUrl + "/hibbiz-api/api/mro-requests/" + mroRequestId
                    + "/pictures/" + pictureId + "/image";
            Log.d(TAG, "instantiateItem: url=" + url);

            picassoBuilder.build()
                    .load(url)
                    .placeholder(R.drawable.progress_animation)
                    .error(R.mipmap.ic_circle_error)
                    .resize(screenWidth, screenHeight) // resizes the image to these dimensions (in pixel)
                    .centerInside()
                    .into(mroRequestPictureReadImageView);

            mroRequestPictureReadImageView.requestFocus();
            container.addView(mroRequestPictureTouchView);

            Log.d(TAG, "instantiateItem: Picture ID="
                    + cursor.getString(cursor.getColumnIndex(SQLiteBaseDaoFactory.COL_ID)));

            // Set title
            String title = cursor.getString(cursor.getColumnIndex(SQLiteBaseDaoFactory.COL_TITLE));
            Log.d(TAG, "instantiateItem: title=" + title);
            mroRequestPictureReadTitleTextView = (TextView) mroRequestPictureTouchView.findViewById(
                    R.id.mroRequestImageReadActivityTitleTextView);
            mroRequestPictureReadTitleTextView.setText(title);

            // set time
            String time = cursor.getString(cursor.getColumnIndex(SQLiteBaseDaoFactory.COL_TIME));
            SimpleDateFormat mroRequestTimeSdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            if (time != null) {
                long milliSeconds = Long.parseLong(time);
                Date date = new Date(milliSeconds);
                String timeString = mroRequestTimeSdf.format(date);
                Log.d(TAG, "instantiateItem: time=" + time + " timeString=" + timeString);
                mroRequestPictureReadTimeTextView = (TextView) mroRequestPictureTouchView.findViewById(
                        R.id.mroRequestImageReadActivityTimeTextView);
                mroRequestPictureReadTimeTextView.setText(timeString);
            }
            return mroRequestPictureTouchView;
        }
        else{
            Log.d(TAG, "instantiateItem: cursor == null !!");
            return null;
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ScrollView) object);
    }

    // O t h e r s   m e t h o d s
    // ---------------------------

    public void swapCursor(Cursor cursor) {
        if(cursor != null) {
            Log.d(TAG, "swapCursor: cursor.getColumnNames()=" + cursor.getColumnNames());
            this.cursor = cursor;
            notifyDataSetChanged();
        }
    }

    public void setMroRequestClickPagerPictureListerner(MroRequestClickPagerPictureListerner mroRequestClickPagerPictureListerner)
    {
        this.mroRequestClickPagerPictureListerner = mroRequestClickPagerPictureListerner;
    }

    public Cursor getCursor() {
        Log.d(TAG, "getCursor");
        return cursor;
    }
}
