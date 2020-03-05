package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.MozaiqueOnPhone;

/**
 * Created by Eric on 20/02/2017.
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;

import com.hibernatus.hibmobtech.HibmobtechOptionsMenuActivity;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.GalleryOnPhone.PicassoGalleryActivity;

import static com.hibernatus.hibmobtech.HibmobtechApplication.PICTURES_GALLERY_REQUEST;


// H i b m o b t e c h P i c a s s o M o z a i q u e G r i d V i e w A c t i v i t y
//
// Activité qui permet l'affichage d'une mozaïque compilant les photos des répertoires
// du téléphone. On y accède depuis la fiche d'intervention en cliquant sur
// le trombone
//
// ----------------------------------------------------------------------------------
public class PicassoThumbnailMozaiqueGridViewActivity extends HibmobtechOptionsMenuActivity {
    public static final String TAG = PicassoThumbnailMozaiqueGridViewActivity.class.getSimpleName();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.hibmobtech_picasso_gridview_activity);
        initToolBar();
        initDrawer();
        GridView gridView = (GridView) findViewById(R.id.hibmobtech_picasso_grid_view);
        gridView.setAdapter(new PicassoThumbnailMozaiqueGridViewAdapter(this));
        gridView.setOnScrollListener(new PicassoScrollListener(this));
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.mainMenuGridViewPictureItem).setVisible(false);
        menu.findItem(R.id.mainMenuGalleryPictureItem).setVisible(true);
        menu.findItem(R.id.mainMenuDeleteItem).setVisible(false);
        menu.findItem(R.id.mainMenuUpdateItem).setVisible(false);
        menu.findItem(R.id.mainMenuCheckItem).setVisible(false);
        menu.findItem(R.id.mainMenuSiteItem).setVisible(false);
        menu.findItem(R.id.mainMenuEquipmentItem).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainMenuGalleryPictureItem:
                Log.d(TAG, "onOptionsItemSelected: mainMenuCheckItem");
                Intent intent = new Intent(getApplicationContext(), PicassoGalleryActivity.class);
                startActivityForResult(intent, PICTURES_GALLERY_REQUEST);

            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: case home: getClass=" + getClass());
                finish();
                break;
            default:
                Log.d(TAG, "onOptionsItemSelected: default");
                break;
        }
        Log.d(TAG, "boolean Return true to consume menu here");
        return true;
    }
}
