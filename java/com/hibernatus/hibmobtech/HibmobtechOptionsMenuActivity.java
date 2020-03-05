package com.hibernatus.hibmobtech;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.hibernatus.hibmobtech.equipment.EquipmentListActivity;
import com.hibernatus.hibmobtech.map.MapActivity;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.site.SiteListActivity;

/**
 * Created by Eric on 07/12/2015.
 */

// c l a s s   H i b m o b t e c h O p t i o n s M e n u A c t i v i t y
// ---------------------------------------------------------------------

public class HibmobtechOptionsMenuActivity extends HibmobtechActivity {
    public static final String TAG = HibmobtechOptionsMenuActivity.class.getSimpleName();

    protected MenuItem mainMenuUpdateItem;
    protected MenuItem mainMenuDeleteItem;
    protected MenuItem mainMenuCheckItem;
    protected MenuItem mainMenuStartItem;
    protected MenuItem mainMenuFinishItem;
    protected MenuItem mainMenuMroStartDateItem;
    protected MenuItem mainMenuSiteItem;
    protected MenuItem mainMenuEquipmentItem;
    protected MenuItem mainMenuMapItem;
    protected MenuItem mainMenuPictureItem;
    protected MenuItem mainMenuGridViewPictureItem;
    protected MenuItem mainMenuGalleryPictureItem;

    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate");
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Log.d(TAG, "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.main_menu, menu);
        mainMenuCheckItem = menu.findItem(R.id.mainMenuCheckItem).setVisible(false);
        mainMenuDeleteItem = menu.findItem(R.id.mainMenuDeleteItem).setVisible(false);
        mainMenuUpdateItem = menu.findItem(R.id.mainMenuUpdateItem).setVisible(false);
        mainMenuSiteItem = menu.findItem(R.id.mainMenuSiteItem).setVisible(false);
        mainMenuEquipmentItem = menu.findItem(R.id.mainMenuEquipmentItem).setVisible(false);
        mainMenuFinishItem = menu.findItem(R.id.mainMenuFinishItem).setVisible(false);
        mainMenuStartItem = menu.findItem(R.id.mainMenuStartItem).setVisible(false);
        mainMenuMroStartDateItem = menu.findItem(R.id.mainMenuMroStartDateItem).setVisible(false);
        mainMenuMapItem = menu.findItem(R.id.mainMenuMapItem).setVisible(false);
        mainMenuPictureItem = menu.findItem(R.id.mainMenuPictureItem).setVisible(false);
        mainMenuGridViewPictureItem = menu.findItem(R.id.mainMenuGridViewPictureItem).setVisible(false);
        mainMenuGalleryPictureItem = menu.findItem(R.id.mainMenuGalleryPictureItem).setVisible(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.mainMenuEquipmentItem:
                Log.i(TAG, "onOptionsItemSelected: EquipmentActivity");
                Intent intent_equipment = new Intent(EquipmentListActivity.ACTIVITY_FILTER);
                startActivity(intent_equipment);
                break;
            case R.id.mainMenuSiteItem:
                Log.i(TAG, "onOptionsItemSelected: SiteListActivity");
                Intent intent_site = new Intent(SiteListActivity.ACTIVITY_FILTER);
                startActivity(intent_site);
                break;
            case android.R.id.home:
                Log.i(TAG, "onOptionsItemSelected: case home: getClass=" + getClass());
                NavUtils.navigateUpFromSameTask(HibmobtechOptionsMenuActivity.this);
                return true;
            case R.id.mainMenuMapItem:
                Log.i(TAG, "onOptionsItemSelected: case map: getClass=" + getClass());
                Intent intent_map = new Intent(MapActivity.ACTIVITY_FILTER);
                startActivity(intent_map);
                return true;
            default:
                return true;
        }
        return true;
    }


    // O t h e r s   m e t h o d s
    // ---------------------------

    public void setBackgroundColor(MroRequest mroRequest, LinearLayout linearLayout) {
        if (mroRequest != null && linearLayout != null){
            Log.d(TAG, "setBackgroundColor: mroRequestCurrent.getId()=" + mroRequest.getId()
                    + " mroRequestCurrent.getStatus()=" + mroRequest.getStatus()
                    + " linearLayout=" + linearLayout);

            Resources resources = HibmobtechApplication.getInstance().getResources();
            int openedColor = 0;
            int assignedColor = 0;
            int planedColor = 0;
            int inProgressColor = 0;
            int finishColor = 0;
            int closedColor = 0;
            int canceledColor = 0;
            int defaultColor = 0;

            if(resources != null) {
                openedColor = resources.getColor(R.color.opened);
                assignedColor = resources.getColor(R.color.assigned);
                planedColor = resources.getColor(R.color.planed);
                inProgressColor = resources.getColor(R.color.in_progress);
                finishColor = resources.getColor(R.color.finished);
                closedColor = resources.getColor(R.color.closed);
                canceledColor = resources.getColor(R.color.canceled);
                defaultColor = resources.getColor(R.color.md_white_1000);
            }

            switch (mroRequest.getStatus()) {
                case OPENED:
                    Log.d(TAG, "setHeaderBackgroundColor: OPENED");
                    linearLayout.setBackgroundColor(openedColor);
                    break;
                case ASSIGNED: // operator has been assigned to the job
                    Log.d(TAG, "setHeaderBackgroundColor: ASSIGNED");
                    linearLayout.setBackgroundColor(assignedColor);
                    break;
                case PLANNED: // work is planned
                    Log.d(TAG, "setHeaderBackgroundColor: PLANNED");
                    linearLayout.setBackgroundColor(planedColor);
                    break;
                case IN_PROGRESS: // work on site is on going
                    Log.d(TAG, "setHeaderBackgroundColor: IN_PROGRESS");
                    linearLayout.setBackgroundColor(inProgressColor);
                    break;
                case FINISHED: // work on site is callbackReceived
                    Log.d(TAG, "setHeaderBackgroundColor: FINISHED");
                    linearLayout.setBackgroundColor(finishColor);
                    break;
                case CLOSED: // billing callbackReceived
                    Log.d(TAG, "setHeaderBackgroundColor: CLOSED");
                    linearLayout.setBackgroundColor(closedColor);
                    break;
                case CANCELED:
                    Log.d(TAG, "setHeaderBackgroundColor: CANCELED");
                    linearLayout.setBackgroundColor(canceledColor);
                    break;
                default:
                    Log.d(TAG, "onOptionsItemSelected: default");
                    linearLayout.setBackgroundColor(defaultColor);
                    break;
            }
        }
    }
}
