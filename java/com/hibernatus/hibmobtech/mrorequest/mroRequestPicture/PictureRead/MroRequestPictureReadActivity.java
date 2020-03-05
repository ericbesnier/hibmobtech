package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.PictureRead;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.hibernatus.hibmobtech.HibmobtechOptionsMenuActivity;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.contentprovider.HibmobtechContentProvider;
import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.mrorequest.MroRequestCurrent;

import static com.hibernatus.hibmobtech.HibmobtechApplication.LOADER_FOR_PICTURES_ID;
import static com.hibernatus.hibmobtech.HibmobtechApplication.PROJECTION_PICTURE;


// c l a s s   M r o R e q u e s t P i c t u r e R e a d A c t i v i t y
//
// Activité permettant d'afficher une photo en pleine page lorqu'on a cliqué
// sur une des vignettes de la fiche d'intervention. Elle gère un viewPager
// permettant de passer d'une photo à l'autre.
//
// ---------------------------------------------------------------------
public class MroRequestPictureReadActivity extends HibmobtechOptionsMenuActivity implements
        MroRequestClickPagerPictureListerner,
        LoaderManager.LoaderCallbacks<Cursor> {
    public static final String TAG = MroRequestPictureReadActivity.class.getSimpleName();

    protected MroRequestPictureReadAdapter mroRequestPictureReadAdapter;
    protected ViewPager mroRequestPictureViewPager;
    protected Menu menu;
    protected MenuItem mainMenuItemCheck;
    protected int currentIndex;
    protected String currentTitle;
    protected static final String SELECTION = SQLiteBaseDaoFactory.COL_ID_MRO_REQUEST + " =?";
    protected int position;
    protected long pictureId;
    protected MroRequest mroRequestCurrent;


    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        setContentView(R.layout.mro_request_picture_read_activity);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.hibmobToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mroRequestPictureViewPager = (ViewPager) findViewById(R.id.mroRequestPictureViewPager);

        Intent intent = getIntent();
        position = intent.getIntExtra("position", 0);
        pictureId = intent.getLongExtra("pictureId", 0);
        Log.d(TAG, "onCreate: position=" + position);

        getSupportLoaderManager().restartLoader(LOADER_FOR_PICTURES_ID, null, this);

        mroRequestPictureReadAdapter = new MroRequestPictureReadAdapter(this, getApplicationContext(), null);
        mroRequestPictureReadAdapter.setMroRequestClickPagerPictureListerner(this);

        mroRequestPictureViewPager.setOnPageChangeListener(new PageChangeListener());
        mroRequestPictureViewPager.setAdapter(mroRequestPictureReadAdapter);
        mroRequestPictureViewPager.setCurrentItem(position);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        this.menu = menu;
        super.onCreateOptionsMenu(menu);
        mainMenuItemCheck = menu.findItem(R.id.mainMenuCheckItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: case home: getClass=" + getClass());
                finish();
                break;
            default:
                Log.d(TAG, "onOptionsItemSelected: default");
                break;
        }
        return true;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(TAG, "onCreateLoader: PROJECTION_PICTURE=" + PROJECTION_PICTURE);
        switch (id) {
            case LOADER_FOR_PICTURES_ID:
                Log.d(TAG, "onCreateLoader: LOADER FOR_PICTURES");

                String[] selectionArgs = { String.valueOf(mroRequestCurrent.getId().toString()) };
                String sortOrder = null;
                CursorLoader cursorLoader = new CursorLoader(
                        this,
                        HibmobtechContentProvider.urlForPicture(),
                        PROJECTION_PICTURE,
                        SELECTION,
                        selectionArgs,
                        sortOrder);
                return cursorLoader;
            default:
                throw new IllegalArgumentException("no id handled!");
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        Log.d(TAG, "onLoadFinished: cursorLoader=" + cursorLoader + " cursor=" + cursor + " cursor.getCount()=" + cursor.getCount()
                +" PROJECTION_PICTURE=" + PROJECTION_PICTURE);

        switch (cursorLoader.getId()) {
            case LOADER_FOR_PICTURES_ID:
                if(cursor != null){
                    mroRequestPictureReadAdapter.swapCursor(cursor);
                    mroRequestPictureViewPager.setCurrentItem(position);
                }
                break;
            default:
                throw new IllegalArgumentException("no cursorLoader id handled!");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Log.d(TAG, "onLoaderReset");
        ((MroRequestPictureReadAdapter) mroRequestPictureViewPager.getAdapter()).swapCursor(null);
    }

    // c l a s s    P a g e C h a n g e L i s t e n e r
    // -------------------------------------------------
    class PageChangeListener extends ViewPager.SimpleOnPageChangeListener
    {
        @Override
        public void onPageSelected(int position){
            super.onPageSelected(position);
            currentIndex = position;
        }
    }

    @Override
    public void change(int position, String title)
    {
        Log.d(TAG, "change: position=" + position + " currentPictureTitle=" + title);
        this.currentTitle = title;
        this.currentIndex = position;
        if (mainMenuItemCheck != null) {
            if (!mainMenuItemCheck.isVisible()) {
                mainMenuItemCheck.setVisible(true);
            }
        }
    }
}
