package com.hibernatus.hibmobtech.mrorequest;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bleau.hibernatus.mob.util.AndroidUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hibernatus.hibmobtech.ActivityCurrent;
import com.hibernatus.hibmobtech.ActivityRefresher;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.HibmobtechOptionsMenuActivity;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.contentprovider.HibmobtechContentProvider;
import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoPicture;
import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoSignature;
import com.hibernatus.hibmobtech.equipment.EquipmentCurrent;
import com.hibernatus.hibmobtech.machine.MachineActivity;
import com.hibernatus.hibmobtech.machine.MachineAssociateActivity;
import com.hibernatus.hibmobtech.machine.MachineCurrent;
import com.hibernatus.hibmobtech.model.AccessInfos;
import com.hibernatus.hibmobtech.model.Cause;
import com.hibernatus.hibmobtech.model.ClientSignature;
import com.hibernatus.hibmobtech.model.HibmobtechAddress;
import com.hibernatus.hibmobtech.model.Invoice;
import com.hibernatus.hibmobtech.model.Machine;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.model.Operation;
import com.hibernatus.hibmobtech.model.Operator;
import com.hibernatus.hibmobtech.model.Picture;
import com.hibernatus.hibmobtech.model.PictureInfos;
import com.hibernatus.hibmobtech.model.Problem;
import com.hibernatus.hibmobtech.model.SignatureInfos;
import com.hibernatus.hibmobtech.model.Site;
import com.hibernatus.hibmobtech.model.SpareParts;
import com.hibernatus.hibmobtech.model.Task;
import com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.MozaiqueOnPhone.PicassoThumbnailMozaiqueGridViewActivity;
import com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.PictureCreate.MroRequestPictureCreateActivity;
import com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.PictureUtils;
import com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.ThumbnailBandeau.ItemOffsetDecoration;
import com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.ThumbnailBandeau.ThumbnailCursorRecyclerViewAdapter;
import com.hibernatus.hibmobtech.mrorequest.mroRequestSignature.MroRequestSignatureActivity;
import com.hibernatus.hibmobtech.site.SiteActivity;
import com.hibernatus.hibmobtech.site.SiteApiService;
import com.hibernatus.hibmobtech.site.SiteCurrent;
import com.hibernatus.hibmobtech.sparepart.SparePartListActivity;
import com.hibernatus.hibmobtech.utils.ColorGenerator;
import com.hibernatus.hibmobtech.utils.FileUtils;
import com.hibernatus.hibmobtech.view.TextDrawable;
import com.hibernatus.hibmobtech.view.WrapListView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.hibernatus.hibmobtech.HibmobtechApplication.CAUSE_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.COMMENT_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.CREATE_PICTURE_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.CREATE_SIGNATURE_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.DirQualityPath.HDPI;
import static com.hibernatus.hibmobtech.HibmobtechApplication.LOADER_FOR_PICTURES_ID;
import static com.hibernatus.hibmobtech.HibmobtechApplication.MACHINE_ASSOCIATE_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.MACHINE_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.PICTURES_GRID_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.PICTURE_PATH;
import static com.hibernatus.hibmobtech.HibmobtechApplication.PROJECTION_PICTURE;
import static com.hibernatus.hibmobtech.HibmobtechApplication.SITE_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.SPARE_PART_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.TAKE_PICTURE_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.TASK_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.THUMBNAIL_HEIGHT;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID_MRO_REQUEST;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_IMAGE;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_SIGNATURE_FILE;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_SIGNER_NAME;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_SIGNER_ROLE;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_THUMBNAIL;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_TIME;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_TIMESTAMP;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_TITLE;
import static com.hibernatus.hibmobtech.model.MroRequest.Status.IN_PROGRESS;

// c l a s s   M r o R e q u e s t   A c t i v i t y
// -------------------------------------------------
public class MroRequestActivity extends HibmobtechOptionsMenuActivity
        implements OnMapReadyCallback,
        AppBarLayout.OnOffsetChangedListener,
        ActivityRefresher,
        LoaderManager.LoaderCallbacks<Cursor> {

    // static
    public static final String TAG = MroRequestActivity.class.getSimpleName();
    public static final String ACTIVITY_FILTER = MroRequestActivity.class.getName();
    public static final String CURRENT_MRO_REQUEST_POSITION = "CURRENT_MRO_REQUEST_POSITION";
    public static final String CURRENT_SITE_FRAGMENT = "CURRENT_SITE_FRAGMENT";
    public static final String FRAGMENT_TYPE = "FRAGMENT_TYPE";
    private static final String SELECTION = COL_ID_MRO_REQUEST + " =?";
    protected static final int ZOOM_LEVEL = 13;

    // MapFragment
    protected MapFragment mroRequestActivityMap;
    protected int mapHeight;

    // AppBar
    protected AppBarLayout mroRequestActivityAppBarLayout;

    // SwipeRefresh
    protected SwipeRefreshLayout swipeRefreshLayout;

    // FloatingActionButton
    protected FloatingActionButton mroRequestFloatingActionButton;

    // Site
    protected LinearLayout mroRequestHeaderDateLinearLayout;
    protected LinearLayout mroRequestHeaderLinearLayout;
    protected TextView mroRequestSiteNameTextView;
    protected TextView mroRequestSiteAdress1TextView;
    protected TextView mroRequestSiteAdress2TextView;

    // Header
    protected TextView mroRequestHeaderOperationDateTextView;
    protected TextView mroRequestReferenceTextView;
    protected TextView mroRequestInfoCircleIcon;
    protected ImageView mroRequestWarningInvoiceImageViewIconError;

    // Machine
    protected LinearLayout machineListRowLinearLayout;
    protected LinearLayout machineListRowThirdLinearLayout;
    protected ImageView machineListRowAvatarImageView;
    protected TextView machineListRowReferenceTextView;
    protected TextView machineListRowCategoryTextView;
    protected TextView machineListRowBrandTextView;
    protected TextView machineListRowTitleSerialNumberTextView;
    protected TextView machineListRowTitlePurchaseDateTextView;
    protected TextView machineListRowSerialNumberTextView;
    protected TextView machineListRowPurchaseDateTextView;
    protected Machine machine;
    protected Button editMachineButton;

    // Notes
    protected TextView mroRequestRowNoteRequestText;

    // Comment
    protected TextView mroRequestCommentTextView;
    protected TextView mroRequestCommentPencilIcon;

    // Operator
    protected WrapListView mroRequestOperatorWrapListView;

    // Problem
    protected WrapListView mroRequestProblemWrapListView;

    // Cause
    protected LinearLayout mroRequestCauseLinearLayout;
    protected TextView mroRequestCausePencilIcon;

    // Task
    protected LinearLayout mroRequestTaskLinearLayout;
    protected TextView mroRequestTaskPencilIcon;

    // SparePart
    protected LinearLayout mroRequestSparePartLinearLayout;
    protected TextView mroRequestSparePartPencilIcon;

    // Picture
    protected GridView mroRequestPictureGridView;
    protected ThumbnailCursorRecyclerViewAdapter thumbnailCursorRecyclerViewAdapter;
    protected RecyclerView mroRequestThumbnailRecyclerView;
    protected TextView mroRequestRowCameraTitleIcon;
    protected TextView mroRequestRowPaperClipTitleIcon;
    protected PictureUtils pictureUtils;
    protected String hdpiDirPath;
    protected String currentPictureHdpiPath;

    // Signature
    protected LinearLayout mroRequestSignatureGlobalLinearLayout;
    protected TextView mroRequestSignatureTitleIconEyeTextView;
    protected TextView mroRequestSignatureTitleIconPencilTextView;
    protected ImageView signatureImageView;
    protected LinearLayout mroRequestSignerLinearLayout;
    protected TextView signerNameTextView;
    protected TextView signerRoleTextView;
    protected TextView timestampTextView;
    protected boolean isSignatureVisible = false;

    // QuotationRequest
    protected CheckBox mroRequestQuotationRequestedCheckBox;

    // BottomSheet
    protected BottomSheetBehavior bottomSheetBehavior;
    protected View bottomSheet;
    protected TextView mroRequestAccessInfosContactNameTextView;
    protected TextView mroRequestAccessInfosCodeTextView;
    protected TextView mroRequestAccessInfosTimeTextView;
    protected TextView mroRequestAccessInfosNoteTextView;

    // Others
    protected MroRequest mroRequestCurrent;
    protected Long siteId;
    protected Site siteCurrent;
    protected boolean isMroRequestFinishedOnUpdate = false;
    protected int position;
    protected String fragmentId;
    protected MroRequestApiService mroRequestApiService;
    protected Handler handler;


    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mro_request_activity);
        ActivityCurrent.getInstance().setActivityCurrent(this);
        checkWriteExternalStoragePermission();
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        initViews();
        Intent intent = getIntent(); // gets the previously created intent
        position = intent.getIntExtra(CURRENT_MRO_REQUEST_POSITION, -1);
        fragmentId = intent.getStringExtra(FRAGMENT_TYPE);
        Long mroRequestId = intent.getLongExtra(MroRequest.MRO_REQUEST_ID_KEY, -1);

        Log.d(TAG, "onCreate: mroRequestCurrent=" + mroRequestCurrent + " mroRequestId=" + mroRequestId);
        // case intent from MachineActivity - historic : si l'id est renseigné on load la request
        if(mroRequestId != -1){
            loadMroRequestById(mroRequestId);
        } else { // sinon, on utilise la request current
            mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
            setSiteParams();
            setViews();
        }
        deleteAllTablePictures();
        getThumbnailsFromServer();
        deleteAllTableSignature();
        getSignatureFromServer();
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        Log.d(TAG, "onRestart");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent != null){
            siteCurrent = mroRequestCurrent.getSite();
            siteId = siteCurrent.getId();
            setUIState();
        }
        else{
            Log.e(TAG, "onRestart: mroRequestCurrent is NULL");
        }
    }

    @Override
    public void onResume(){
        Log.d(TAG, "onResume");
        super.onResume();
        mroRequestActivityAppBarLayout =(AppBarLayout)findViewById(R.id.mroRequestActivityAppBarLayout);
        if(mroRequestActivityAppBarLayout != null)
            mroRequestActivityAppBarLayout.addOnOffsetChangedListener(this);
        trackScreen(TAG);
        getContentResolver().registerContentObserver(HibmobtechContentProvider.urlForPicture(),
                true, hibmobtechContentObserver);
    }

    @Override
    protected void onPause(){
        super.onPause();
        Log.d(TAG, "onPause");
        mroRequestActivityAppBarLayout =(AppBarLayout)findViewById(R.id.mroRequestActivityAppBarLayout);
        if(mroRequestActivityAppBarLayout != null)
            mroRequestActivityAppBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        getContentResolver().unregisterContentObserver(hibmobtechContentObserver);

    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i){
        if(swipeRefreshLayout == null)return;
        if(i == 0){
            swipeRefreshLayout.setEnabled(true);
        } else {
            swipeRefreshLayout.setEnabled(false);
        }
    }

    @Override
    public void onMapReady(GoogleMap map){
        if(siteCurrent == null)return;
        if(siteCurrent.getGeoloc()== null
                || siteCurrent.getGeoloc().getLat()== 0
                || siteCurrent.getGeoloc().getLng()== 0){
            Log.d(TAG, "onMapReady: siteCurrent.getGeoloc()== null, let's call geocoder !");
            try {
                siteCurrent.setGeoloc(getApplicationContext());
            } catch(IOException e){
                e.printStackTrace();
            }
        }

        com.google.android.gms.maps.model.LatLng latLngGoogle
                = new com.google.android.gms.maps.model.LatLng(siteCurrent.getGeoloc().getLat(),
                siteCurrent.getGeoloc().getLng());
        Log.d(TAG, "onMapReady: latLngGoogle.latitude="
                + latLngGoogle.latitude + " latLngGoogle.longitude=" + latLngGoogle.longitude);

        map.addMarker(new MarkerOptions()
                .position(latLngGoogle)
                .title(siteCurrent.getName()));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngGoogle, ZOOM_LEVEL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        Log.d(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu);
        setUIState();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.mainMenuCheckItem:
                Log.d(TAG, "onOptionsItemSelected: mainMenuCheckItem");
                saveMroRequestCurrent();
                break;
            case R.id.mainMenuUpdateItem:
                Log.d(TAG, "onOptionsItemSelected: mainMenuUpdateItem");
                isMroRequestFinishedOnUpdate = true;
                setUIState();
                break;
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: case home: getClass=" + getClass());
                if(MroRequestCurrent.getInstance().isMroRequestCheckable()){
                    showAlertDialog();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(CURRENT_MRO_REQUEST_POSITION, position);
                    intent.putExtra(FRAGMENT_TYPE, fragmentId);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                break;
            default:
                Log.d(TAG, "onOptionsItemSelected: default");
                break;
        }
        Log.d(TAG, "boolean Return true to consume menu here");
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        Log.d(TAG, "onRequestPermissionsResult: requestCode=" + requestCode);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == HibmobtechApplication.CHECK_WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST
                && isGrantedWriteExternalStorage == true){
            Log.d(TAG, "onRequestPermissionsResult: requestCode == CHECK_WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST");
            initThumbnailsView();
        }
        if(requestCode == HibmobtechApplication.CAMERA_PERMISSION_REQUEST && isGrantedCameraPermission == true){
            Log.d(TAG, "onRequestPermissionsResult: requestCode == HCAMERA_PERMISSION_REQUEST");
            dispatchTakePictureIntent();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        Log.d(TAG, "onActivityResult: resultCode=" + resultCode + " requestCode=" + requestCode);
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "onActivityResult: mroRequestCurrent is NULL");
            return;
        }
        if(requestCode == COMMENT_REQUEST){
            String currentNote = mroRequestCurrent.getOperation().getNote();
            mroRequestCurrent.getOperation().setNote(currentNote);
            mroRequestCommentTextView.setText(mroRequestCurrent.getOperation().getNote());
            MroRequestCurrent.getInstance().setIsMroRequestCheckable(true);
            mainMenuCheckItem.setVisible(true);
            setCommentView();
        }
        else if(requestCode == TAKE_PICTURE_REQUEST && resultCode == RESULT_OK){
            Log.d(TAG, "onActivityResult: TAKE_PICTURE_REQUEST > resultCode=" + resultCode);
            if(currentPictureHdpiPath != null){
                addPictureToMroRequestCurrent(currentPictureHdpiPath);
                Intent intent = new Intent(this, MroRequestPictureCreateActivity.class);
                intent.putExtra(PICTURE_PATH, currentPictureHdpiPath);
                startActivityForResult(intent, CREATE_PICTURE_REQUEST);
            }
            else{
                Log.e(TAG, "onActivityResult: currentPictureHdpiPath == null");
            }
        }
        else if(requestCode == MACHINE_ASSOCIATE_REQUEST){
            Log.d(TAG, "onActivityResult: MACHINE_ASSOCIATE_REQUEST > resultCode=" + resultCode);
            Machine machineCurrent = MachineCurrent.getInstance().getMachineCurrent();
            if(machineCurrent != null){
                if(machineCurrent.getEquipment()!= null){
                    Log.d(TAG, "onActivityResult: machineCurrent=" + machineCurrent.getEquipment().getReference()
                            + " No" + machineCurrent.getId());
                    MroRequestCurrent.getInstance().setIsMroRequestCheckable(true);
                }
            }
        }
        else if(requestCode == CREATE_SIGNATURE_REQUEST){
            Log.d(TAG, "onActivityResult: CREATE_SIGNATURE_REQUEST > resultCode=" + resultCode);
            setSignatureView();
        }
        else if(requestCode == MACHINE_REQUEST){
            Log.d(TAG, "onActivityResult: MACHINE_REQUEST > resultCode=" + resultCode);
            setMachineView();
        }
        else if(requestCode == CAUSE_REQUEST){
            Log.d(TAG, "onActivityResult: CAUSE_REQUEST > resultCode=" + resultCode);
            setCausesView();
        }
        else if(requestCode == TASK_REQUEST){
            Log.d(TAG, "onActivityResult: TASK_REQUEST > resultCode=" + resultCode);
            setTasksView();
        }
        else if(requestCode == SPARE_PART_REQUEST){
            Log.d(TAG, "onActivityResult: SPARE_PART_REQUEST > resultCode=" + resultCode);
            setSparePartsView();
        }
        else {
            Log.d(TAG, "onActivityResult: UNKNOWN > resultCode="
                    + resultCode + "requestCode=" + requestCode);
        }
    }

    @Override
    public void onBackPressed(){
        Log.d(TAG, "onBackPressed");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "onBackPressed: mroRequestCurrent is NULL");
            return;
        }
        if(MroRequestCurrent.getInstance().isMroRequestCheckable()){
            showAlertDialog();
        } else {
            Intent intent = new Intent();
            intent.putExtra(CURRENT_MRO_REQUEST_POSITION, position);
            intent.putExtra(FRAGMENT_TYPE, fragmentId);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void refresh(){
        loadMroRequest();
    }

    // L o a d e r < C u r s o r >
    // ---------------------------
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args){
        Log.d(TAG, "onCreateLoader: PROJECTION_PICTURE=" + PROJECTION_PICTURE);
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null)throw new NoSuchElementException("mroRequestCurrent is null");
        switch(id){
            case LOADER_FOR_PICTURES_ID:
                String[] selectionArgs = { String.valueOf(mroRequestCurrent.getId().toString())};
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
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
        Log.d(TAG, "onLoadFinished: cursorLoader=" + cursorLoader
                + " cursor=" + cursor + " cursor.getCount()=" + cursor.getCount()
                +" PROJECTION_PICTURE=" + PROJECTION_PICTURE);

        switch(cursorLoader.getId()){
            case LOADER_FOR_PICTURES_ID:
                if(mroRequestThumbnailRecyclerView != null){
                    Cursor adapterCursor =((ThumbnailCursorRecyclerViewAdapter)mroRequestThumbnailRecyclerView.getAdapter())
                            .getCursor();
                    Log.d(TAG, "onLoadFinished: adapterCursor=" + adapterCursor);
                    ((ThumbnailCursorRecyclerViewAdapter)mroRequestThumbnailRecyclerView.getAdapter())
                            .swapCursor(cursor);
                }
                break;
            default:
                throw new IllegalArgumentException("no cursorLoader id handled!");
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        Log.d(TAG, "onLoaderReset");
        if(mroRequestThumbnailRecyclerView != null){
            ((ThumbnailCursorRecyclerViewAdapter)mroRequestThumbnailRecyclerView.getAdapter())
                    .swapCursor(null);
        }
    }

    /*
     * this is a callback method for a content observer
     * runs NOT in UI thread
     */
    @Override
    public void onUpdatedContent(){
        super.onUpdatedContent();
        this.runOnUiThread(new Runnable(){
            @Override
            public void run(){
                Log.d(TAG, "onUpdatedContent: restartLoader !!! ");
                getSupportLoaderManager().restartLoader(LOADER_FOR_PICTURES_ID, null,
                        MroRequestActivity.this);
            }
        });
    }

    // s e t V i e w s   m e t h o d s
    // -------------------------------
    protected void initViews(){
        Log.d(TAG, "initViews");
        initToolBar();
        initDrawer();
        initThumbnailsView();
        initSwipeRefreshLayout();
    }

    public void setViews(){
        Log.d(TAG, "setViews");
        setMapView();
        setHeaderView();
        setSiteView();
        setMachineView();
        setNoteView();
        setOperatorsView();
        setProblemsView();
        setCausesView();
        setTasksView();
        setSparePartsView();
        setCommentView();
        setSignatureView();
        setQuotationRequestedCheckBox();
    }


    // P i c t u r e s
    // ---------------
    protected void deleteAllTablePictures(){
        Log.d(TAG, "deleteAllTablePictures");

        // delete all pictures in table_picture
        SQLiteBaseDaoPicture sqLiteBaseDaoPicture = new SQLiteBaseDaoPicture(getApplicationContext());
        sqLiteBaseDaoPicture.open();
        sqLiteBaseDaoPicture.deleteAllTablePictures();
        sqLiteBaseDaoPicture.close();
    }

    protected void getThumbnailsFromServer(){
        Log.d(TAG, "getThumbnailsFromServer");

        mroRequestApiService = HibmobtechApplication.getRestClient().getMroRequestService();

        // get list pictures from server
        if(mroRequestCurrent == null)return;
        mroRequestApiService.getMroRequestPicturesList(mroRequestCurrent.getId(),
                new Callback<List<Picture>>(){
                    @Override
                    public void success(List<Picture> pictures, Response response){
                        if(pictures == null)return;

                        for(final Picture picture:pictures){
                            Log.d(TAG, "getThumbnailsFromServer: mroRequestId=" + mroRequestCurrent.getId()
                                    + " pictureId=" + picture.getId());
                            getThumbnailFromServer(picture);
                        }
                    }

                    @Override
                    public void failure(RetrofitError error){
                        Log.d(TAG, "getThumbnailsFromServer:getMroRequestPicturesList:ERROR=", error);
                    }
                });
    }

    public void getThumbnailFromServer(final Picture picture){
        mroRequestApiService.getMroRequestThumbnail(mroRequestCurrent.getId(), picture.getId(),
                new Callback<Response>(){
                    @Override
                    public void success(Response result, Response response){
                        if(result == null)return;

                        Log.d(TAG, "getThumbnailsFromServer:getMroRequestThumbnail: success !!!: result="
                                + result + " response=" + response);
                        try {
                            Bitmap initialBitmapThumbnail = BitmapFactory.decodeStream(result.getBody().in());

                            /// resize the initial bitmap to have a square thumbnail
                            Bitmap bitmapThumbnail = ThumbnailUtils.extractThumbnail(initialBitmapThumbnail
                                    , THUMBNAIL_HEIGHT, THUMBNAIL_HEIGHT);
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bitmapThumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                            byte[] thumbnailByteArray = bos.toByteArray();

                            // set PictureInfos
                            PictureInfos pictureInfos = new PictureInfos();
                            //pictureInfos.setTitle(picture.getInfos().getTitle());
                            pictureInfos.setTitle(picture.getInfos().getTitle());
                            pictureInfos.setTime(picture.getInfos().getTime());

                            Picture pict = new Picture();
                            pict.setId(picture.getId());
                            pict.setIdMroRequest(mroRequestCurrent.getId());
                            pict.setInfos(pictureInfos);

                            // set Thumbnail
                            pict.setThumbnail(thumbnailByteArray);

                            // insert the picture in the table picture
                            insertPictureDataBase(HibmobtechContentProvider.urlForPicture(), pict);
                        } catch(IOException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error){
                        Log.d(TAG, "getThumbnailsFromServer:getMroRequestImageFromPicture:ERROR", error);
                    }
                });
    }

    public void insertPictureDataBase(Uri url, Picture picture)throws IOException {
        Log.d(TAG, "insertPictureDataBase");
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, picture.getId());
        contentValues.put(COL_TIME, picture.getInfos().getTime());
        contentValues.put(COL_THUMBNAIL, picture.getThumbnail());
        contentValues.put(COL_IMAGE, picture.getImage());
        contentValues.put(COL_ID_MRO_REQUEST, picture.getIdMroRequest());
        contentValues.put(COL_TITLE, picture.getInfos().getTitle());
        getContentResolver().insert(url, contentValues);
        getContentResolver().notifyChange(url, hibmobtechContentObserver);
        getSupportLoaderManager().restartLoader(LOADER_FOR_PICTURES_ID, null, this);
    }

    protected void initThumbnailsView(){
        Log.d(TAG, "initThumbnailsView");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent != null){
            if(pictureUtils == null)pictureUtils = new PictureUtils(this, mroRequestCurrent.getId());
            pictureUtils.createPictureDirectories(mroRequestCurrent);
            if(isGrantedWriteExternalStorage == true){
                hdpiDirPath = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES
                        + "/" + mroRequestCurrent.getId()+ HDPI));
            }
            initGridThumbnailView();
        } else {
            Log.d(TAG, "initMroRequestCurrentPictures: mroRequestCurrent is NULL");
        }
    }

    public void initGridThumbnailView(){
        Log.d(TAG, "initGridThumbnailView: mroRequestPictureGridView=" + mroRequestPictureGridView);
        if(mroRequestPictureGridView == null){
            initHeaderGridThumbnailView();
            StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(1,
                    StaggeredGridLayoutManager.HORIZONTAL);
            mroRequestThumbnailRecyclerView =(RecyclerView)findViewById(R.id.mroRequestThumbnailRecyclerView);
            mroRequestThumbnailRecyclerView.setLayoutManager(gridLayoutManager);
            mroRequestThumbnailRecyclerView.addItemDecoration(new ItemOffsetDecoration(this, R.dimen.item_offset));
            mroRequestThumbnailRecyclerView.setHasFixedSize(true);
            thumbnailCursorRecyclerViewAdapter = new ThumbnailCursorRecyclerViewAdapter(this, null);
            mroRequestThumbnailRecyclerView.setAdapter(thumbnailCursorRecyclerViewAdapter);
            getSupportLoaderManager().restartLoader(LOADER_FOR_PICTURES_ID, null, this);
        }
    }

    public void initHeaderGridThumbnailView(){
        Log.d(TAG, "initHeaderGridThumbnailView: mroRequestRowCameraTitleIcon=" + mroRequestRowCameraTitleIcon);
        if(mroRequestRowCameraTitleIcon == null){
            mroRequestRowCameraTitleIcon =(TextView)findViewById(R.id.icon_fa_camera);
            mroRequestRowCameraTitleIcon.setTypeface(hibmobtechFont);
            mroRequestRowCameraTitleIcon.setClickable(true);
            mroRequestRowCameraTitleIcon.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    checkCameraPermission();
                    checkWriteExternalStoragePermission();
                    if(isGrantedCameraPermission == true && isGrantedWriteExternalStorage == true){
                        dispatchTakePictureIntent();
                    }
                }
            });
        }
        if(mroRequestRowPaperClipTitleIcon == null){
            mroRequestRowPaperClipTitleIcon =(TextView)findViewById(R.id.icon_fa_paperclip);
            mroRequestRowPaperClipTitleIcon.setTypeface(hibmobtechFont);
            mroRequestRowPaperClipTitleIcon.setClickable(true);
            mroRequestRowPaperClipTitleIcon.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    checkReadExternalStoragePermission();
                    if(isGrantedReadExternalStorage == true){
                        Intent intent = new Intent(getApplicationContext(),
                                PicassoThumbnailMozaiqueGridViewActivity.class);
                        startActivityForResult(intent, PICTURES_GRID_REQUEST);
                    }
                }
            });
        }
    }

    public void addPictureToMroRequestCurrent(String pictureHdpiPath){
        Log.d(TAG, "addPictureToMroRequestCurrent");
        Picture picture = new Picture();
        picture.setPathHdpi(pictureHdpiPath);

        ArrayList<Picture> pictureArrayList = mroRequestCurrent.getOperation().getPictures();
        if(pictureArrayList == null){
            pictureArrayList = new ArrayList<>();
        }
        pictureArrayList.add(picture);
        mroRequestCurrent.getOperation().setPictures(pictureArrayList);
        Log.d(TAG, "addPictureToMroRequestCurrent: mroRequestCurrent.getOperation().getPictures()="
                + mroRequestCurrent.getOperation().getPictures());
    }

    private void dispatchTakePictureIntent(){
        Log.d(TAG, "dispatchTakePictureIntent");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if(takePictureIntent.resolveActivity(getPackageManager())!= null){
            // Create the File where the photo should go
            File pictureHdpiFile = null;
            try {
                pictureHdpiFile = createPictureHdpiFile();
            } catch(IOException ex){
                // Error occurred while creating the File
                Log.e(TAG, "dispatchTakePictureIntent: IOException ex=" + ex);
            }
            // Continue only if the File was successfully created
            if(pictureHdpiFile != null){
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(pictureHdpiFile));
                startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST);
            }
        }
    }

    private File createPictureHdpiFile()throws IOException {
        Log.d(TAG, "createPictureHdpiFile");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "createPictureHdpiFile: mroRequestCurrent is NULL");
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String pictureFileName = mroRequestCurrent.getId()+ "_" + timeStamp;
        String completePictureHdpiFileName = hdpiDirPath + "/" + pictureFileName + ".jpg";
        Log.d(TAG, "createPictureHdpiFile: completePictureHdpiFileName=" + completePictureHdpiFileName);

        File pictureHdpiFile = new File(completePictureHdpiFileName);
        if(!pictureHdpiFile.exists()){
            /**
             * Creates a new, empty file on the file system according to the path
             * information stored in this file. This method returns true if it creates
             * a file, false if the file already existed. Note that it returns false
             * even if the file is not a file(because it's a directory, say).
             * */
            boolean ret = pictureHdpiFile.createNewFile();
            if(ret == true){
                Log.d(TAG, "createPictureHdpiFile: new file created !!");
            }
            else{
                Log.d(TAG, "createPictureHdpiFile: file already existed !!!");

            }
        }

        // Save a file: path for use with ACTION_VIEW intents
        currentPictureHdpiPath = pictureHdpiFile.getAbsolutePath();
        return pictureHdpiFile;
    }

    // M a p   v i e w
    // ---------------
    public void setMapView(){
        mroRequestActivityMap =(MapFragment)getFragmentManager()
                .findFragmentById(R.id.mroRequestActivityMap);
        if(mroRequestActivityMap != null){
            mroRequestActivityMap.getMapAsync(this);
        } else {
            Log.d(TAG, "setMapView: mroRequestActivityMap == null !");
        }
    }

    // H e a d e r   v i e w
    // ---------------------
    public void setHeaderView(){
        mroRequestHeaderDateLinearLayout = (LinearLayout) findViewById(R.id.mroRequestHeaderDateLinearLayout);
        setBackgroundColor(mroRequestCurrent, mroRequestHeaderDateLinearLayout);
        setHeaderDateView();
        setAccessInfosView();
        setMroRequestNumber();
    }

    public void setHeaderDateView(){
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent != null) {
            Site site = mroRequestCurrent.getSite();
            if(site != null) {
                final Long siteId = site.getId();
                if(siteId != null) {
                    setInvoiceWarning(siteId);
                    mroRequestHeaderLinearLayout = (LinearLayout) findViewById(R.id.mroRequestHeaderLinearLayout3);
                    mroRequestHeaderLinearLayout.setClickable(true);
                    mroRequestHeaderLinearLayout.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "setHeaderView: currentSiteId=" + siteId);
                            SiteCurrent.getInstance().setSiteCurrent(siteCurrent);
                            Context context = v.getContext();
                            Intent intent = new Intent(context, SiteActivity.class);
                            startActivityForResult(intent, SITE_REQUEST);
                        }
                    });
                }
            }

            // MroRequest
            mroRequestHeaderOperationDateTextView = (TextView) findViewById(R.id.mroRequestHeaderOperationDateTextView);
            Operation operation = mroRequestCurrent.getOperation();
            Date date;
            SimpleDateFormat mroRequestDateSdf;
            SimpleDateFormat mroRequestHourSdf = new SimpleDateFormat("HH:mm");
            String mroRequestDateString;
            String mroRequestHourString;

            if (operation != null) {
                date = operation.getDate();
                if (date == null) date = new Date();
                mroRequestDateSdf = new SimpleDateFormat("dd MMM");
                mroRequestDateString = mroRequestDateSdf.format(date);
                mroRequestHourString = mroRequestHourSdf.format(date);
                Log.d(TAG, "setHeaderView: " + mroRequestDateString + " à " + mroRequestHourString);
            }
            switch (mroRequestCurrent.getStatus()) {
                case OPENED:
                    Log.d(TAG, "setHeaderView: OPENED");
                    setDateTextView(mroRequestCurrent, "'Ouverte' dd MMM", mroRequestHeaderOperationDateTextView);
                    break;
                case ASSIGNED: // operator has been assigned to the job
                    Log.d(TAG, "setHeaderView: ASSIGNED");
                    setDateTextView(mroRequestCurrent, "'Planifiée' dd MMM", mroRequestHeaderOperationDateTextView);
                    break;
                case PLANNED: // work is planned
                    Log.d(TAG, "setHeaderView: PLANNED");
                    break;
                case IN_PROGRESS: // work on site is on going
                    Log.d(TAG, "setHeaderView: IN_PROGRESS");
                    setDateTextView(mroRequestCurrent, "'Démarrée' dd MMM", mroRequestHeaderOperationDateTextView);
                    break;
                case FINISHED: // work on site is callbackReceived
                    Log.d(TAG, "setHeaderView: FINISHED");
                    setDateTextView(mroRequestCurrent, "'Terminée' dd MMM", mroRequestHeaderOperationDateTextView);
                    break;
                case CLOSED: // billing callbackReceived
                    Log.d(TAG, "setHeaderView: CLOSED");
                    break;
                case CANCELED:
                    Log.d(TAG, "setHeaderView: CANCELED");
                    break;
                default:
                    Log.e(TAG, "setHeaderView: State unknown !");
                    break;
            }
        }
    }

    public void setInvoiceWarning(Long siteId){
        Log.d(TAG, "setInvoiceWarning: siteId=" + siteId);
        mroRequestWarningInvoiceImageViewIconError =(ImageView)findViewById(R.id.mroRequestWarningInvoiceImageViewIconError);
        mroRequestWarningInvoiceImageViewIconError.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "setInvoiceWarning: onClick");
                AndroidUtils.showToast(MroRequestActivity.this, "Facture(s)impayée(s)");
            }
        });

        mroRequestWarningInvoiceImageViewIconError.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, SiteActivity.class);
                intent.putExtra(CURRENT_SITE_FRAGMENT, 3);
                startActivityForResult(intent, SITE_REQUEST);
                return true;
            }
        });

        loadUnpaidInvoice(siteId);
    }

    public void loadUnpaidInvoice(long siteId){
        Log.d(TAG, "loadUnpaidInvoice");
        SiteApiService service = HibmobtechApplication.getRestClient().getSiteService();

        service.getSiteUnpaidInvoices(siteId, "unpaid", new Callback<List<Invoice>>() {
            @Override
            public void success(List<Invoice> invoices, retrofit.client.Response response) {
                if (invoices == null) return;

                Log.d(TAG, "loadInvoice: success: invoices=" + invoices);
                if(mroRequestWarningInvoiceImageViewIconError != null) {
                    if (invoices.size() > 0) {
                        mroRequestWarningInvoiceImageViewIconError.setImageResource(R.mipmap.ic_error);
                    } else {
                        mroRequestWarningInvoiceImageViewIconError.setVisibility(View.INVISIBLE);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "failure : " + error.toString());
            }
        });
    }

    public void setMroRequestNumber(){
        Log.d(TAG, "setMroRequestNumber");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent != null) {
            mroRequestReferenceTextView = (TextView) findViewById(R.id.mroRequestReferenceTextView);
            mroRequestReferenceTextView.setText(String.valueOf(mroRequestCurrent.getId()));
        }
    }

    void setDateTextView(MroRequest mroRequest, String message, TextView textView){
        Log.d(TAG, "setDateMroRequest");
        if(mroRequest != null) {
            SimpleDateFormat mroRequestDateSdf;
            SimpleDateFormat mroRequestHourSdf = new SimpleDateFormat("HH:mm");
            String mroRequestDateString;
            String mroRequestHourString;
            Date date = null;
            Operation operation = mroRequest.getOperation();
            ;

            switch (mroRequest.getStatus()) {
                case OPENED:
                    if (operation != null) {
                        date = operation.getDate(); // date de planification !
                    }
                    if (date == null)
                        date = mroRequest.getRequestDate(); // date de creation de la demande
                    break;
                case ASSIGNED:
                    if (operation != null) {
                        date = operation.getDate(); // date de planification !
                    }
                    break;
                case IN_PROGRESS:
                    if (operation != null) {
                        date = operation.getMroStartDate();
                    }
                    break;
                case FINISHED:
                    if (operation != null) {
                        date = operation.getMroFinishDate();
                    }
                    break;
                default:
                    Log.e(TAG, "setDateMroRequest: mroRequest.getStatus() == ERROR Default");
                    break;
            }

            mroRequestDateSdf = new SimpleDateFormat(message);

            if (date != null) {
                mroRequestDateString = mroRequestDateSdf.format(date);
                mroRequestHourString = mroRequestHourSdf.format(date);
                if (mroRequestHourString.equals("00:00")) {
                    textView.setText(mroRequestDateString);
                } else {
                    textView.setText(mroRequestDateString + " à " + mroRequestHourString);
                }
            }
        }
    }

    public void setSiteView(){
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent != null) {
            mroRequestSiteNameTextView = (TextView) findViewById(R.id.mroRequestSiteNameTextView);
            Site site = mroRequestCurrent.getSite();
            if(site != null) {
                mroRequestSiteNameTextView.setText(site.getName());
                HibmobtechAddress address = site.getAddress();
                if(address != null) {
                    mroRequestSiteAdress1TextView = (TextView) findViewById(R.id.mroRequestSiteAdress1TextView);
                    mroRequestSiteAdress1TextView.setText(address.getAdressPart1());

                    mroRequestSiteAdress2TextView = (TextView) findViewById(R.id.mroRequestSiteAdress2TextView);
                    mroRequestSiteAdress2TextView.setText(address.getAdressPart2());
                }
            }
        }
    }


    // A c c e s s   I n f o s   V i e w
    // ---------------------------------
    public void setAccessInfosView(){
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setAccessInfosView: mroRequestCurrent is NULL");
            return;
        }
        AccessInfos accessInfos = MroRequestCurrent.getInstance().getMroRequestCurrent().getAccessInfos();
        mroRequestAccessInfosContactNameTextView =(TextView)findViewById(R.id.mroRequestAccessInfosContactNameTextView);
        mroRequestAccessInfosCodeTextView =(TextView)findViewById(R.id.mroRequestAccessInfosCodeTextView);
        mroRequestAccessInfosTimeTextView =(TextView)findViewById(R.id.mroRequestAccessInfosTimeTextView);
        mroRequestAccessInfosNoteTextView =(TextView)findViewById(R.id.mroRequestAccessInfosNoteTextView);

        if(accessInfos != null){
            Log.d(TAG, "setAccessInfosView: accessInfos.getContactName()=" + accessInfos.getContactName());
            Log.d(TAG, "setAccessInfosView: accessInfos.getNote()=" + accessInfos.getNote());
            mroRequestAccessInfosContactNameTextView.setText(accessInfos.getContactName());
            mroRequestAccessInfosCodeTextView.setText(accessInfos.getCode());
            mroRequestAccessInfosTimeTextView.setText(accessInfos.getTime());
            mroRequestAccessInfosNoteTextView.setText(accessInfos.getNote());
        } else {
            mroRequestAccessInfosContactNameTextView.setText("");
            mroRequestAccessInfosCodeTextView.setText("");
            mroRequestAccessInfosTimeTextView.setText("");
            mroRequestAccessInfosNoteTextView.setText("");
        }

        // mapActivityBottomSheet
        bottomSheet = findViewById(R.id.mroRequestBottomSheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback(){
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState){
                if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    //mapActivityBottomSheet.requestLayout();
                    bottomSheet.invalidate();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset){
                // React to dragging events
            }
        });

        mroRequestInfoCircleIcon =(TextView)findViewById(R.id.icon_fa_info_circle);
        mroRequestInfoCircleIcon.setTypeface(hibmobtechFont);
        mroRequestInfoCircleIcon.setClickable(true);

        // button: the view you want to enlarge hit area
        final View parent =(View)mroRequestInfoCircleIcon.getParent();
        parent.post(new Runnable(){
            public void run(){
                final Rect rect = new Rect();
                mroRequestInfoCircleIcon.getHitRect(rect);
                rect.top -= 100;    // increase top hit area
                rect.left -= 100;   // increase left hit area
                rect.bottom += 100; // increase bottom hit area
                rect.right += 100;  // increase right hit area
                parent.setTouchDelegate(new TouchDelegate(rect, mroRequestInfoCircleIcon));
            }
        });
        mroRequestInfoCircleIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "setAccessInfosView");
                if(bottomSheetBehavior.getState()== BottomSheetBehavior.STATE_COLLAPSED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
                if(bottomSheetBehavior.getState()== BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }

    // M a c h i n e   V i e w
    // -----------------------
    public void setMachineView(){
        Log.d(TAG, "setMachineView");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setMachineView: mroRequestCurrent is NULL");
            return;
        }
        machineListRowLinearLayout =(LinearLayout)findViewById(R.id.machineListRowLinearLayout2);
        machineListRowThirdLinearLayout =(LinearLayout)findViewById(R.id.machineListRowThirdLinearLayout);
        machineListRowAvatarImageView =(ImageView)findViewById(R.id.machineListRowAvatarImageView);
        machineListRowReferenceTextView =(TextView)findViewById(R.id.machineListRowReferenceTextView);
        machineListRowCategoryTextView =(TextView)findViewById(R.id.machineListRowCategoryTextView);
        machineListRowBrandTextView =(TextView)findViewById(R.id.machineListRowBrandTextView);
        machineListRowTitleSerialNumberTextView =(TextView)findViewById(R.id.machineListRowTitleSerialNumberTextView);
        machineListRowTitlePurchaseDateTextView =(TextView)findViewById(R.id.machineListRowTitlePurchaseDateTextView);
        machineListRowSerialNumberTextView =(TextView)findViewById(R.id.machineListRowSerialNumberTextView);
        machineListRowPurchaseDateTextView =(TextView)findViewById(R.id.machineListRowPurchaseDateTextView);

        machine = mroRequestCurrent.getMachine();
        if(machine == null){
            createUnknownMachine();
        }
        if(machine.getEquipment()!= null){
            if(machine.getEquipment().getReference().equals(HibmobtechApplication.UNKNOWN_MACHINE)){
                setUnknownMachineView();
            } else {
                setKnownMachineView(machine);
            }
        }
    }

    public void createUnknownMachine(){
        Log.d(TAG, "createUnknownMachine");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "createUnknownMachine: mroRequestCurrent is NULL");
            return;
        }
        machine = new Machine();
        machine.setComments("<?>");
        machine.setNumMachine("<?>");
        machine.setSerialNumber("<?>");
        machine.getEquipment().setBrand("<?>");
        machine.getEquipment().setCategory("<?>");
        machine.getEquipment().setDescription("<?>");
        machine.getEquipment().setFormattedPrice("<?>");
        machine.getEquipment().setHeating("<?>");
        machine.getEquipment().setModel("<?>");
        machine.getEquipment().setReference(HibmobtechApplication.UNKNOWN_MACHINE);
        mroRequestCurrent.setMachine(machine);
    }

    public void setUnknownMachineView(){
        Log.d(TAG, "setUnknownMachineView");
        if(machine.getEquipment()!= null)
            machineListRowReferenceTextView.setText(machine.getEquipment().getReference());
        machineListRowCategoryTextView.setVisibility(View.GONE);
        machineListRowBrandTextView.setVisibility(View.GONE);
        machineListRowTitleSerialNumberTextView.setVisibility(View.GONE);
        machineListRowTitlePurchaseDateTextView.setVisibility(View.GONE);
        machineListRowSerialNumberTextView.setVisibility(View.GONE);
        machineListRowPurchaseDateTextView.setVisibility(View.GONE);
        setAvatarEquipment();
        createEditMachineButton();
    }

    public void createEditMachineButton(){
        Log.d(TAG, "createEditMachineButton");
        if(editMachineButton == null){
            editMachineButton = new Button(this);
        }
        editMachineButton.setBackgroundResource(R.color.edit_button);
        editMachineButton.setTextColor(Color.WHITE);
        editMachineButton.setText("Editer");
        if(editMachineButton.getParent()== null)
            machineListRowThirdLinearLayout.addView(editMachineButton);
        editMachineButton.setVisibility(View.VISIBLE);

        editMachineButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Log.d(TAG, "setMachineView: onClick: view=" + view);
                Intent intent = new Intent(getApplicationContext(), MachineAssociateActivity.class);
                startActivityForResult(intent, MACHINE_ASSOCIATE_REQUEST);
            }
        });
    }

    public void deleteEditMachineButton(){
        Log.d(TAG, "deleteEditMachineButton");
        if(editMachineButton != null){
            if(editMachineButton.getParent()!= null)
                ((ViewGroup)editMachineButton.getParent()).removeView(editMachineButton);
        }
    }

    public void setKnownMachineView(Machine machine){
        Log.d(TAG, "setKnownMachineView");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setKnownMachineView: mroRequestCurrent is NULL");
            return;
        }
        machineListRowCategoryTextView.setVisibility(View.VISIBLE);
        machineListRowBrandTextView.setVisibility(View.VISIBLE);
        machineListRowTitleSerialNumberTextView.setVisibility(View.VISIBLE);
        machineListRowTitlePurchaseDateTextView.setVisibility(View.VISIBLE);
        machineListRowSerialNumberTextView.setVisibility(View.VISIBLE);
        machineListRowPurchaseDateTextView.setVisibility(View.VISIBLE);

        Log.d(TAG, "setMachineView: machineListRowThirdLinearLayout.removeView(editMachineButton)");
        if(editMachineButton != null && machineListRowThirdLinearLayout != null)
            machineListRowThirdLinearLayout.removeView(editMachineButton);

        if(machine.getEquipment()!= null){
            // reference equipment
            machineListRowReferenceTextView.setText(machine.getEquipment().getReference());

            // category equipment
            machineListRowCategoryTextView.setText(machine.getEquipment().getCategory());

            // brand equipment
            machineListRowBrandTextView.setText(machine.getEquipment().getBrand());
        }
        // updatedMachine serial number
        if(machine.getSerialNumber()== null){
            machineListRowSerialNumberTextView.setText("<?>");
        } else {
            machineListRowSerialNumberTextView.setText(machine.getSerialNumber());
        }

        // updatedMachine purchase date
        if(machine.getPurchaseDate()== null){
            machineListRowPurchaseDateTextView.setText("<?>");
        } else {
            SimpleDateFormat machinePurchaseDateSdf = new SimpleDateFormat("dd MMM yyyy", Locale.FRANCE);
            String dateString = machinePurchaseDateSdf.format(machine.getPurchaseDate());
            machineListRowPurchaseDateTextView.setText(dateString);
        }

        setAvatarEquipment();
        setOnClickMachine();
    }

    public void setAvatarEquipment(){
        Log.d(TAG, "setAvatarEquipment");
        String firstLetter;
        if(machine.getEquipment()!= null){
            if(machine.getEquipment().getCategory()== null || machine.getEquipment()
                    .getCategory().equals("<?>")){
                firstLetter = "?";
            } else {
                firstLetter = String.valueOf(machine.getEquipment().getCategory().charAt(0));
            }
            Log.d(TAG, "onBindViewHolder: firstLetter=" + firstLetter);
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            int color = generator.getColor(machine.getEquipment().getCategory());
            TextDrawable drawable = TextDrawable.builder().buildRound(firstLetter, color); // radius in px
            machineListRowAvatarImageView.setImageDrawable(drawable);
        }
    }

    public void setOnClickMachine(){
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setOnClickMachine: mroRequestCurrent is NULL");
            return;
        }
        if(machine.getEquipment()!= null){
            if(machine.getEquipment().getReference().equals(HibmobtechApplication.UNKNOWN_MACHINE)){
                machineListRowLinearLayout.setClickable(false);
            } else {
                machineListRowLinearLayout.setClickable(true);
                machineListRowLinearLayout.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        MachineCurrent.getInstance().setMachineCurrent(mroRequestCurrent.getMachine());
                        SiteCurrent.getInstance().setSiteCurrent(siteCurrent);
                        EquipmentCurrent.getInstance()
                                .setEquipmentCurrent(mroRequestCurrent.getMachine().getEquipment());
                        Context context = v.getContext();
                        Intent intent = new Intent(context, MachineActivity.class);
                        startActivityForResult(intent, MACHINE_REQUEST);
                    }
                });
            }
        }
    }

    // N o t e   V i e w
    // -----------------
    public void setNoteView(){
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setNoteView: mroRequestCurrent is NULL");
            return;
        }
        mroRequestRowNoteRequestText =(TextView)findViewById(R.id.mroRequestNoteTextView);
        if(mroRequestCurrent.getNote()!= null){
            mroRequestRowNoteRequestText.setText(mroRequestCurrent.getNote());
            mroRequestRowNoteRequestText.setVisibility(View.VISIBLE);
        }
        else{
            mroRequestRowNoteRequestText.setVisibility(View.GONE);
        }
    }

    // C o m m e n t   V i e w
    // -----------------------
    public void setCommentView(){
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setCommentView: mroRequestCurrent is NULL");
            return;
        }
        mroRequestCommentTextView =(TextView)findViewById(R.id.mroRequestCommentTextView);
        if(mroRequestCurrent.getOperation().getNote()!= null){
            mroRequestCommentTextView.setText(mroRequestCurrent.getOperation().getNote());
            mroRequestCommentTextView.setVisibility(View.VISIBLE);
        }
        else{
            mroRequestCommentTextView.setVisibility(View.GONE);
        }
        mroRequestCommentPencilIcon =(TextView)findViewById(R.id.icon_fa_pencil_comment);
        mroRequestCommentPencilIcon.setTypeface(hibmobtechFont);
        mroRequestCommentPencilIcon.setClickable(true);
        mroRequestCommentPencilIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "setOnClickComment");
                Context context = v.getContext();
                Intent intent = new Intent(context, MroRequestCommentActivity.class);
                startActivityForResult(intent, COMMENT_REQUEST);
            }
        });
    }

    // O p e r a t o r s   V i e w
    // ---------------------------
    public void setOperatorsView(){
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setOperatorsView: mroRequestCurrent is NULL");
            return;
        }
        Set<Operator> operators = mroRequestCurrent.getOperation().getOperators();
        Iterator<Operator> iteratorOperator = operators.iterator();
        List<String> operatorList = new ArrayList<>();
        int i = 0;
        while(iteratorOperator.hasNext()){
            Operator op = iteratorOperator.next();
            Log.d(TAG, "setOperatorsView: op.getTitle()=" + op.getName());
            operatorList.add(op.getName());
            Log.d(TAG, "setOperatorsView: operatorList(" + i + ")=" + operatorList.get(i));
            i++;
        }
        ArrayAdapter<String> adapterOperator = new ArrayAdapter<>(this,
                R.layout.mro_request_operator_list_row,
                R.id.mroRequestOperatorTextView,
                operatorList);
        // Operators
        mroRequestOperatorWrapListView =(WrapListView)findViewById(R.id.mroRequestOperatorWrapListView);
        mroRequestOperatorWrapListView.setAdapter(adapterOperator);
    }

    // P r o b l e m s   V i e w
    // -------------------------
    public void setProblemsView(){
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setProblemsView: mroRequestCurrent is NULL");
            return;
        }
        Set<Problem> problems = mroRequestCurrent.getProblems();
        Iterator<Problem> iteratorProblem = problems.iterator();
        List<String> pbList = new ArrayList<>();
        int j = 0;
        while(iteratorProblem.hasNext()){
            Problem pb = iteratorProblem.next();
            Log.d(TAG, "setProblemsView: pb.getTitle()=" + pb.getDescription());
            pbList.add(pb.getDescription());
            Log.d(TAG, "setProblemsView: pbList(" + j + ")=" + pbList.get(j));
            j++;
        }
        ArrayAdapter<String> adapterProblem = new ArrayAdapter<>(this,
                R.layout.mro_request_problem_list_row,
                R.id.mroRequestProblemTextView,
                pbList);

        // Problems
        if(mroRequestProblemWrapListView == null){
            mroRequestProblemWrapListView =(WrapListView)findViewById(R.id.mroRequestProblemWrapListView);
        } else {
            mroRequestProblemWrapListView.invalidate();
        }
        mroRequestProblemWrapListView.setAdapter(adapterProblem);
    }

    // C a u s e s   V i e w
    // ---------------------
    public void setCausesView(){
        Long userId = HibmobtechApplication.getDataStore().getUserId();
        Log.d(TAG, "setCausesView:userId=" + userId);
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setCausesView: mroRequestCurrent is NULL");
            return;
        }

        Set<Cause> mroCauseSet = mroRequestCurrent.getOperation().getCauses();
        // Causes
        mroRequestCauseLinearLayout =(LinearLayout)findViewById(R.id.mroRequestCauseLinearLayout);
        mroRequestCauseLinearLayout.removeAllViews();
        mroRequestCausePencilIcon =(TextView)findViewById(R.id.icon_fa_pencil_cause);
        mroRequestCausePencilIcon.setTypeface(hibmobtechFont);

        for(Cause cause : mroCauseSet){
            Log.d(TAG, "setCausesView: cause.getId()=" + cause.getId());
            final LinearLayout causeAdditionalHorizontalLinearLayout = new LinearLayout(getApplicationContext());
            causeAdditionalHorizontalLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LayoutInflater inflater = LayoutInflater.from(MroRequestActivity.this);
            View rowInflatedView = inflater.inflate(R.layout.mro_request_search_cause_row, null);
            TextView mroRequestSearchCauseRowDescriptionTextView =(TextView)rowInflatedView
                    .findViewById(R.id.mroRequestSearchCauseRowDescriptionTextView);

            mroRequestSearchCauseRowDescriptionTextView.setText(cause.getDescription());
            LinearLayout.LayoutParams headParams =(LinearLayout.LayoutParams)mroRequestCauseLinearLayout
                    .getLayoutParams();
            headParams.weight = 10.0f;
            rowInflatedView.setLayoutParams(headParams);

            TextView deleteIcon =(TextView)rowInflatedView.findViewById(R.id.icon_fa_cross);
            deleteIcon.setTypeface(hibmobtechFont);
            causeAdditionalHorizontalLinearLayout.addView(rowInflatedView);

            deleteIcon.setClickable(true);
            deleteIcon.setTag(cause);
            deleteIcon.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    removeCauseOnCurrentRequest((Cause)v.getTag());
                    mroRequestCauseLinearLayout.removeView(causeAdditionalHorizontalLinearLayout);
                }
            });

            mroRequestCauseLinearLayout.addView(causeAdditionalHorizontalLinearLayout);
            if(mroRequestCurrent.getStatus().equals(IN_PROGRESS)){
                deleteIcon.setVisibility(View.VISIBLE);
            } else {
                deleteIcon.setVisibility(View.INVISIBLE);
            }
            Set<Operator> operators = mroRequestCurrent.getOperation().getOperators();
            Iterator<Operator> iteratorOperator = operators.iterator();
            while(iteratorOperator.hasNext()){
                Operator operator = iteratorOperator.next();
                Log.d(TAG, "setCausesView: operator.getName()=" + operator.getName()
                        + " operator.getId()=" + operator.getId());

                // cas de l'historique des interventions d'une machine:
                // l'operateur courant n'est pas celui  à qui est affecté l'intervention
                if(!userId.equals(operator.getId())){
                    deleteIcon.setVisibility(View.INVISIBLE);
                }
            }
            Cause additionalCause = MroRequestCurrent.getInstance().getAdditionalCause();
            if(cause.equals(additionalCause)){
                hightLightBackground(causeAdditionalHorizontalLinearLayout);
                MroRequestCurrent.getInstance().setAdditionalCause(null);
            }
        }
        setOnClickCause();
    }

    public void setOnClickCause(){
        mroRequestCausePencilIcon.setClickable(true);
        mroRequestCausePencilIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "setOnClickCause");
                Context context = v.getContext();
                Intent intent = new Intent(context, MroRequestSearchCauseActivity.class);
                startActivityForResult(intent, CAUSE_REQUEST);
            }
        });
    }

    public void removeCauseOnCurrentRequest(Cause cause){
        Log.d(TAG, "removeCauseOnCurrentRequest: description=" + cause.getDescription());
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "removeCauseOnCurrentRequest: mroRequestCurrent is NULL");
            return;
        }
        mroRequestCurrent.getOperation().getCauses().remove(cause);
        MroRequestCurrent.getInstance().setIsMroRequestCheckable(true);
        setUIState();
    }

    // T a s k s   V i e w
    // -------------------
    public void setTasksView(){
        Long userId = HibmobtechApplication.getDataStore().getUserId();
        Log.d(TAG, "setTasksView:userId=" + userId);
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setTasksView: mroRequestCurrent is NULL");
            return;
        }

        mroRequestTaskLinearLayout =(LinearLayout)findViewById(R.id.mroRequestTaskLinearLayout);
        mroRequestTaskLinearLayout.removeAllViews();
        mroRequestTaskPencilIcon =(TextView)findViewById(R.id.icon_fa_pencil_task);
        mroRequestTaskPencilIcon.setTypeface(hibmobtechFont);

        Set<Task> mroTaskSet = mroRequestCurrent.getOperation().getTasks();
        for(Task task : mroTaskSet){
            Log.d(TAG, "setTasksView: task.getId()=" + task.getId());
            final LinearLayout taskAdditionalHorizontalLinearLayout = new LinearLayout(getApplicationContext());
            taskAdditionalHorizontalLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LayoutInflater inflater = LayoutInflater.from(MroRequestActivity.this);
            View rowInflatedView = inflater.inflate(R.layout.mro_request_search_task_row, null);
            TextView mroRequestTaskRowDescTv =(TextView)rowInflatedView.findViewById(R.id.mroRequestTaskRowDescTv);

            mroRequestTaskRowDescTv.setText(task.getDescription());
            LinearLayout.LayoutParams headParams =(LinearLayout.LayoutParams)mroRequestTaskLinearLayout
                    .getLayoutParams();
            headParams.weight = 10.0f;
            rowInflatedView.setLayoutParams(headParams);

            TextView deleteIcon =(TextView)rowInflatedView.findViewById(R.id.icon_fa_cross);
            deleteIcon.setTypeface(hibmobtechFont);
            taskAdditionalHorizontalLinearLayout.addView(rowInflatedView);

            deleteIcon.setClickable(true);
            deleteIcon.setTag(task);
            deleteIcon.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    removeTaskOnCurrentRequest((Task)v.getTag());
                    mroRequestTaskLinearLayout.removeView(taskAdditionalHorizontalLinearLayout);
                }
            });

            mroRequestTaskLinearLayout.addView(taskAdditionalHorizontalLinearLayout);
            if(mroRequestCurrent.getStatus().equals(IN_PROGRESS)){
                deleteIcon.setVisibility(View.VISIBLE);
            } else {
                deleteIcon.setVisibility(View.INVISIBLE);
            }

            Set<Operator> operators = mroRequestCurrent.getOperation().getOperators();
            Iterator<Operator> iteratorOperator = operators.iterator();
            while(iteratorOperator.hasNext()){
                Operator operator = iteratorOperator.next();
                Log.d(TAG, "setTasksView: operator.getName()=" + operator.getName()
                        + " operator.getId()=" + operator.getId());

                // cas de l'historique des interventions d'une machine :
                // l'operateur courant n'est pas celui  à qui est affecté l'intervention
                if(!userId.equals(operator.getId())){
                    deleteIcon.setVisibility(View.INVISIBLE);
                }
            }

            Task additionalTask = MroRequestCurrent.getInstance().getAdditionalTask();
            if(task.equals(additionalTask)){
                hightLightBackground(taskAdditionalHorizontalLinearLayout);
                MroRequestCurrent.getInstance().setAdditionalTask(null);
            }
        }
        setOnClickTask();
    }

    public void setOnClickTask(){
        mroRequestTaskPencilIcon.setClickable(true);
        mroRequestTaskPencilIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "setOnClickTask");
                Context context = v.getContext();
                Intent intent = new Intent(context, MroRequestSearchTaskActivity.class);
                startActivityForResult(intent, TASK_REQUEST);
            }
        });
    }

    public void removeTaskOnCurrentRequest(Task task){
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "removeTaskOnCurrentRequest: mroRequestCurrent is NULL");
            return;
        }
        Log.d(TAG, "removeTaskOnCurrentRequest: description=" + task.getDescription());
        mroRequestCurrent.getOperation().getTasks().remove(task);
        MroRequestCurrent.getInstance().setIsMroRequestCheckable(true);
        setUIState();
    }

    // S p a r e - P a r t s   V i e w
    // -------------------------------
    public void setSparePartsView(){
        Long userId = HibmobtechApplication.getDataStore().getUserId();
        Log.d(TAG, "setSparePartsView:userId=" + userId);
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setSparePartsView: mroRequestCurrent is NULL");
            return;
        }

        mroRequestSparePartLinearLayout =(LinearLayout)findViewById(R.id.mroRequestSparePartLinearLayout);
        mroRequestSparePartLinearLayout.removeAllViews();
        mroRequestSparePartPencilIcon =(TextView)findViewById(R.id.icon_fa_pencil_spare_part);
        mroRequestSparePartPencilIcon.setTypeface(hibmobtechFont);


        Set<SpareParts> sparePartsSet = mroRequestCurrent.getOperation().getSpareParts();
        Log.d(TAG, "setSparePartsView: sparePartsSet=" + sparePartsSet);

        for(final SpareParts spareParts : sparePartsSet){
            final LinearLayout sparePartAdditionalHorizontalLinearLayout = new LinearLayout(getApplicationContext());
            sparePartAdditionalHorizontalLinearLayout.setOrientation(LinearLayout.HORIZONTAL);

            LayoutInflater inflater = LayoutInflater.from(MroRequestActivity.this);
            View rowInflatedView = inflater.inflate(R.layout.mro_request_search_spare_part_row, null);
            TextView mroRequestSparePartRowDescTv =(TextView)rowInflatedView
                    .findViewById(R.id.mroRequestSearchSparePartRowDescriptionTextView);

            mroRequestSparePartRowDescTv.setText(spareParts.getSparePart().getDescription());
            LinearLayout.LayoutParams headParams =(LinearLayout.LayoutParams)mroRequestSparePartLinearLayout
                    .getLayoutParams();
            headParams.weight = 10.0f;
            rowInflatedView.setLayoutParams(headParams);

            sparePartAdditionalHorizontalLinearLayout.addView(rowInflatedView);

            MroRequest.Status status = mroRequestCurrent.getStatus();

            final EditText numberOfSparePart =(EditText)rowInflatedView
                    .findViewById(R.id.mroRequestSearchSparePartRowEditText);
            numberOfSparePart.setText(String.valueOf(spareParts.getQuantity()));
            numberOfSparePart.setSelection(numberOfSparePart.getText().length());

            if(status.equals(IN_PROGRESS)|| isMroRequestFinishedOnUpdate == true){
                Log.d(TAG, "setSparePartsView:: numberOfSparePart.setClickable(true)");
                numberOfSparePart.setClickable(true);
                setOnClickNumberOfSparePart(spareParts, numberOfSparePart);
            } else {
                Log.d(TAG, "setSparePartsView: numberOfSparePart.setEnabled(false)");
                numberOfSparePart.setEnabled(false);
            }

            TextView deleteIcon =(TextView)rowInflatedView.findViewById(R.id.icon_fa_cross);
            deleteIcon.setTypeface(hibmobtechFont);
            if(status.equals(IN_PROGRESS)|| isMroRequestFinishedOnUpdate == true){
                deleteIcon.setVisibility(View.VISIBLE);
                setOnClickDeleteIcon(spareParts, deleteIcon, sparePartAdditionalHorizontalLinearLayout);
            } else {
                deleteIcon.setVisibility(View.INVISIBLE);
            }

            Set<Operator> operators = mroRequestCurrent.getOperation().getOperators();
            Log.d(TAG, "setSparePartsView:: operators=" + operators);

            if(!operators.isEmpty()){
                Iterator<Operator> iteratorOperator = operators.iterator();
                while(!iteratorOperator.hasNext()){
                    Operator operator = iteratorOperator.next();
                    Log.d(TAG, "setSparePartsView:: operator.getName()=" + operator.getName()
                            + " operator.getId()=" + operator.getId());

                    // cas de l'historique des interventions d'une machine :
                    // l'operateur courant n'est pas celui  à qui est affecté l'intervention
                    if(!userId.equals(operator.getId())){
                        deleteIcon.setVisibility(View.INVISIBLE);
                    }
                }
            }
            mroRequestSparePartLinearLayout.addView(sparePartAdditionalHorizontalLinearLayout);
            SpareParts additionalSpareParts = MroRequestCurrent.getInstance().getAdditionalSpareParts();
            if(spareParts.equals(additionalSpareParts)){
                hightLightBackground(sparePartAdditionalHorizontalLinearLayout);
                MroRequestCurrent.getInstance().setAdditionalSpareParts(null);
            }
        }
        setOnClickSparePart();
    }

    public void setOnClickDeleteIcon(final SpareParts spareParts, TextView deleteIcon,
                                     final LinearLayout sparePartAdditionalHorizontalLinearLayout){
        deleteIcon.setClickable(true);
        deleteIcon.setTag(spareParts);
        deleteIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                removeSparePartsOnCurrentRequest((SpareParts)v.getTag());
                mroRequestSparePartLinearLayout.removeView(sparePartAdditionalHorizontalLinearLayout);
            }
        });
    }

    public void setOnClickNumberOfSparePart(final SpareParts spareParts, final EditText numberOfSparePart){
        numberOfSparePart.addTextChangedListener(new TextWatcher(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count){
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after){
            }

            @Override
            public void afterTextChanged(Editable s){
                if(!numberOfSparePart.getText().toString().equals("")){
                    spareParts.setQuantity(Integer.parseInt(numberOfSparePart.getText().toString()));
                }
                if(mainMenuCheckItem != null){
                    if(!mainMenuCheckItem.isVisible()){
                        mainMenuCheckItem.setVisible(true);
                    }
                }
            }
        });

        numberOfSparePart.setOnEditorActionListener(new TextView.OnEditorActionListener(){
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
                boolean returnValue = true;
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    String strUserName = numberOfSparePart.getText().toString();
                    if(TextUtils.isEmpty(strUserName)){
                        numberOfSparePart.setError("Veuillez saisir un nombre de pièces détachées");
                        returnValue = true;
                    } else {
                        returnValue = false;
                    }
                }
                return returnValue;
            }
        });
    }

    public void setOnClickSparePart(){
        mroRequestSparePartPencilIcon.setClickable(true);
        mroRequestSparePartPencilIcon.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.d(TAG, "setOnClickSparePart");
                Context context = v.getContext();
                Intent intent = new Intent(context, SparePartListActivity.class);
                startActivityForResult(intent, SPARE_PART_REQUEST);
            }
        });
    }

    public void removeSparePartsOnCurrentRequest(SpareParts spareParts){
        Log.d(TAG, "removeSparePartsOnCurrentRequest:deleteCause description="
                + spareParts.getSparePart().getDescription());
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "removeSparePartsOnCurrentRequest: mroRequestCurrent is NULL");
            return;
        }
        mroRequestCurrent.getOperation().getSpareParts().remove(spareParts);
        MroRequestCurrent.getInstance().setIsMroRequestCheckable(true);
        setUIState();
    }

    // d o w n l o a d   s i g n a t u r e
    // -----------------------------------
    protected void deleteAllTableSignature(){
        Log.d(TAG, "deleteAllTableSignature");

        // delete all signatures in table_signature
        SQLiteBaseDaoSignature sqLiteBaseDaoSignature = new SQLiteBaseDaoSignature(getApplicationContext());
        sqLiteBaseDaoSignature.open();
        sqLiteBaseDaoSignature.deleteAllTableSignatures();
        sqLiteBaseDaoSignature.close();
    }

    protected void getSignatureFromServer(){
        Log.d(TAG, "getSignatureFromServer");

        mroRequestApiService = HibmobtechApplication.getRestClient().getMroRequestService();

        if(mroRequestCurrent != null){
            // get signature from server
            mroRequestApiService.getMroRequestSignature(mroRequestCurrent.getId(), new Callback<ClientSignature>(){
                @Override
                public void success(ClientSignature clientSignature, Response response){
                    if(clientSignature == null)return;
                    Log.d(TAG, "getSignatureFromServer: SUCCESS ! mroRequestCurrent.getId()=" + mroRequestCurrent.getId()
                            + " clientSignature=" + clientSignature
                            + " clientSignature.getId()=" + clientSignature.getId()
                            + " clientSignature.getIdMroRequest()=" + clientSignature.getIdMroRequest()
                            + " clientSignature.getParentKey()=" + clientSignature.getParentKey()
                            + " clientSignature.getInfos().getSignerName()=" + clientSignature.getInfos().getSignerName()
                            + " clientSignature.getInfos().getSignerRole()=" + clientSignature.getInfos().getSignerRole()
                            + " clientSignature.getInfos().getTimestamp()=" + clientSignature.getInfos().getTimestamp());

                    getMroRequestImageFromSignature(clientSignature);
                }

                @Override
                public void failure(RetrofitError error){
                    if(error != null)
                        Log.e(TAG, "getSignaturesFromServer: ERROR=", error);
                    else
                        Log.e(TAG, "getSignaturesFromServer: ERROR !");

                }
            });
        }
        else{
            Log.e(TAG, "getSignatureFromServer: ERROR mroRequestCurrent == null");
        }
    }

    public void getMroRequestImageFromSignature(final ClientSignature clientSignature){
        if(clientSignature == null)return;
        Log.d(TAG, "getMroRequestImageFromSignature: clientSignature=" + clientSignature
                + " clientSignature.getId()=" + clientSignature.getId()
                + " clientSignature.getIdMroRequest()=" + clientSignature.getIdMroRequest());

        if(mroRequestCurrent != null){
            mroRequestApiService.getMroRequestImageFromSignature(mroRequestCurrent.getId(),
                    "jpeg", new Callback<Response>(){
                        @Override
                        public void success(Response result, Response response){
                            if(result == null)return;

                            Log.d(TAG, "getMroRequestImageFromSignature:SUCCESS ! result="
                                    + result + " response=" + response);
                            try {
                                Bitmap bitmapImage = BitmapFactory.decodeStream(result.getBody().in());
                                Log.d(TAG, "getMroRequestImageFromSignature: bitmapImage.getByteCount()="
                                        + bitmapImage.getByteCount());

                                byte[] imageByteArray = pictureUtils.convertBitmapToByteArray(bitmapImage);
                                Log.d(TAG, "getMroRequestImageFromSignature: imageByteArray.length=" + imageByteArray.length);

                                // set SignatureInfos
                                SignatureInfos signatureInfos = new SignatureInfos();
                                signatureInfos.setSignerName(clientSignature.getInfos().getSignerName());
                                signatureInfos.setSignerRole(clientSignature.getInfos().getSignerRole());
                                signatureInfos.setTimestamp(clientSignature.getInfos().getTimestamp());

                                // set Signature
                                ClientSignature signature = new ClientSignature();
                                signature.setInfos(signatureInfos);
                                signature.setIdMroRequest(mroRequestCurrent.getId());
                                signature.setSignatureFile(imageByteArray);
                                Log.d(TAG, "getMroRequestImageFromSignature: signature=" + signature
                                        + " signature.getId()=" + signature.getId()
                                        + " signature.getIdMroRequest()=" + signature.getIdMroRequest()
                                        + " signature.getSignatureFile()=" + signature.getSignatureFile());

                                insertSignatureDataBase(HibmobtechContentProvider.urlForSignature(), signature);
                                setSignatureView();
                            } catch(IOException e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error){
                            Log.d(TAG, "getMroRequestImageFromSignature:ERROR !!!", error);
                        }
                    });
        }
        else{
            Log.e(TAG, "getMroRequestImageFromSignature: ERROR mroRequestCurrent == null");
        }
    }

    public void insertSignatureDataBase(Uri url, ClientSignature clientSignature){
        Log.d(TAG, "insertSignatureDataBase: pictureUrlString=" + url);
        ContentValues[] contentValuesArray = new ContentValues[1];
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_MRO_REQUEST, clientSignature.getIdMroRequest());
        contentValues.put(COL_SIGNATURE_FILE, clientSignature.getSignatureFile());
        contentValues.put(COL_SIGNER_NAME, clientSignature.getInfos().getSignerName());
        contentValues.put(COL_SIGNER_ROLE, clientSignature.getInfos().getSignerRole());
        contentValues.put(COL_TIMESTAMP, clientSignature.getInfos().getTimestamp().toString());
        contentValuesArray[0] = contentValues;
        getContentResolver().bulkInsert(url, contentValuesArray);
        getContentResolver().notifyChange(url, hibmobtechContentObserver);
    }

    // S i g n a t u r e   V i e w
    // ---------------------------
    public void setSignatureView(){
        Log.d(TAG, "setSignatureView");
        mroRequestSignatureGlobalLinearLayout =(LinearLayout)findViewById(R.id.mroRequestSignatureGlobalLinearLayout);

        mroRequestSignatureTitleIconPencilTextView =(TextView)findViewById(R.id.icon_fa_pencil_signature);
        mroRequestSignatureTitleIconPencilTextView.setTypeface(hibmobtechFont);
        mroRequestSignatureTitleIconPencilTextView.setClickable(true);

        mroRequestSignatureTitleIconEyeTextView =(TextView)findViewById(R.id.icon_fa_eye);
        mroRequestSignatureTitleIconEyeTextView.setTypeface(hibmobtechFont);
        mroRequestSignatureTitleIconEyeTextView.setClickable(true);

        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.e(TAG, "setSignatureView: mroRequestCurrent is NULL");
            return;
        }
        // on recupere la signature dans la database
        SQLiteBaseDaoSignature SQLiteBaseDaoSignature = new SQLiteBaseDaoSignature(getApplicationContext());
        SQLiteBaseDaoSignature.open();
        List<ClientSignature> allClientSignatures = SQLiteBaseDaoSignature
                .getAllMroRequestSignatures(mroRequestCurrent.getId());
        ClientSignature clientSignature = null;

        // TODO Solution provisoire
        // on parcourt la liste de toutes les signatures de la mroRequest et on garde la dernière
        for(ClientSignature s : allClientSignatures){
            Log.d(TAG, "setSignatureView: s.getId()=" + s.getId()
                    + " s.getIdMroRequest()=" + s.getIdMroRequest()
                    + " s.getInfos().getSignerName()=" + s.getInfos().getSignerName()
                    + " s.getInfos().getSignerRole()=" + s.getInfos().getSignerRole()
                    + " s.getInfos().getTimestamp()=" + s.getInfos().getTimestamp()
                    + " s.getSignatureFile().length=" + s.getSignatureFile().length);
            if(s.getIdMroRequest().equals(mroRequestCurrent.getId())){
                clientSignature = s;
            }
        }

        SQLiteBaseDaoSignature.close();

        mroRequestSignerLinearLayout =(LinearLayout)findViewById(R.id.mroRequestSignerLinearLayout);
        signerNameTextView =(TextView)findViewById(R.id.signerNameTextView);
        signerRoleTextView =(TextView)findViewById(R.id.signerRoleTextView);
        timestampTextView =(TextView)findViewById(R.id.timestampTextView);
        signatureImageView =(ImageView)findViewById(R.id.signatureImageView);

        if(clientSignature != null){
            Log.d(TAG, "setSignatureView: signature != null");

            mroRequestSignatureTitleIconEyeTextView.setVisibility(View.VISIBLE);

            mroRequestSignerLinearLayout.setVisibility(View.GONE);

            signerNameTextView.setText(clientSignature.getInfos().getSignerName());
            signerNameTextView.setVisibility(View.GONE);

            signerRoleTextView.setText(clientSignature.getInfos().getSignerRole());
            signerRoleTextView.setVisibility(View.GONE);

            Date date = clientSignature.getInfos().getTimestamp();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/mm/yyyy HH:mm");
            String dateString = simpleDateFormat.format(date);
            timestampTextView.setText("Le " + dateString);
            timestampTextView.setVisibility(View.GONE);

            // on decode byte[] to bitmap pour créer l'imageView à partir de la bitmap
            Bitmap signatureBitmap = BitmapFactory.decodeByteArray(clientSignature.getSignatureFile(),
                    0, clientSignature.getSignatureFile().length);

            assert signatureImageView != null;
            signatureImageView.setImageBitmap(signatureBitmap);
            signatureImageView.setVisibility(View.GONE);
        } else {
            Log.d(TAG, "setSignatureView: signature == null");
            mroRequestSignatureTitleIconEyeTextView.setVisibility(View.INVISIBLE);
            mroRequestSignerLinearLayout.setVisibility(View.GONE);
            signerNameTextView.setVisibility(View.GONE);
            signerRoleTextView.setVisibility(View.GONE);
            timestampTextView.setVisibility(View.GONE);
            signatureImageView.setVisibility(View.GONE);
        }
        setOnClickIconEyeSignature();
        setOnClickIconPencilSignature(signatureImageView);
    }

    public void setOnClickIconEyeSignature(){
        if(signatureImageView != null){
            mroRequestSignatureTitleIconEyeTextView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(!MroRequestActivity.this.isSignatureVisible){ // la signature n'est pas visible, on l'affiche
                        Log.d(TAG, "onClick: mroRequestSignatureTitleIconEyeTextView > isSignatureVisible=false");
                        mroRequestSignerLinearLayout.setVisibility(View.VISIBLE);
                        signerNameTextView.setVisibility(View.VISIBLE);
                        signerRoleTextView.setVisibility(View.VISIBLE);
                        timestampTextView.setVisibility(View.VISIBLE);
                        timestampTextView.setFocusable(true);
                        timestampTextView.setFocusableInTouchMode(true);
                        timestampTextView.requestFocus();
                        MroRequestActivity.this.isSignatureVisible = true;
                        signatureImageView.setVisibility(View.VISIBLE);
                    } else { // La signature est visible, on la fait disparaitre
                        Log.d(TAG, "onClick: mroRequestSignatureTitleIconEyeTextView > isSignatureVisible=true");
                        mroRequestSignerLinearLayout.setVisibility(View.GONE);
                        signerNameTextView.setVisibility(View.GONE);
                        signerRoleTextView.setVisibility(View.GONE);
                        timestampTextView.setVisibility(View.GONE);
                        signatureImageView.setVisibility(View.GONE);
                        MroRequestActivity.this.isSignatureVisible = false;
                    }
                }
            });
        }
    }

    public void setOnClickIconPencilSignature(final ImageView signatureImageView){
        mroRequestSignatureTitleIconPencilTextView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Log.d(TAG, "setOnClickIconPencilSignature: onClick");
                Intent intent = new Intent(v.getContext(), MroRequestSignatureActivity.class);
                startActivityForResult(intent, CREATE_SIGNATURE_REQUEST);
            }
        });
    }

    // l o a d   M r o R e q u e s t
    // -----------------------------
    public void loadMroRequestById(long mroRequestId){
        Log.d(TAG, "loadMroRequestById: mroRequestId=" + mroRequestId);
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        service.getMroRequest(mroRequestId, new Callback<MroRequest>() {
            @Override
            public void success(MroRequest mroRequest, retrofit.client.Response response) {
                if (mroRequest != null) {
                    mroRequestCurrent = mroRequest;
                    MroRequestCurrent.getInstance().setMroRequestCurrent(mroRequest);
                }
                setSiteParams();
                setViews();
            }

            @Override
            public void failure(RetrofitError error) {
                FileUtils.setRefreshing(swipeRefreshLayout, false);
                setRetrofitError(error);
            }
        });
    }

    public void loadMroRequest(){
        Log.d(TAG, "loadMroRequest");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.e(TAG, "loadMroRequest: mroRequestCurrent is NULL");
            return;
        }
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        service.getMroRequest(mroRequestCurrent.getId(), new Callback<MroRequest>() {
            @Override
            public void success(MroRequest mroRequest, retrofit.client.Response response) {
                if (mroRequest != null) {
                    MroRequestCurrent.getInstance().setMroRequestCurrent(mroRequest);
                    mroRequestCurrent = mroRequest;
                }
                setViews();
                FileUtils.setRefreshing(swipeRefreshLayout, false);
            }

            @Override
            public void failure(RetrofitError error) {
                FileUtils.setRefreshing(swipeRefreshLayout, false);
                setRetrofitError(error);
            }
        });
    }

    // S w i p e   R e f r e s h
    // -------------------------
    void initSwipeRefreshLayout(){
        if(mroRequestActivityMap != null){
            mapHeight = mroRequestActivityMap.getView().getMeasuredHeight();
            Log.d(TAG, "initSwipeRefreshLayout: mapHeight=" + mapHeight);
        }
        swipeRefreshLayout =(SwipeRefreshLayout)findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.post(new Runnable(){
            @Override
            public void run(){
                Log.d(TAG, "initSwipeRefreshLayout: swipeRefreshLayout.setRefreshing(false)");
                swipeRefreshLayout.setRefreshing(false);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh(){
                Log.d(TAG, "initSwipeRefreshLayout: onRefresh > swipeRefreshLayout.setRefreshing(true)");
                refreshItems();
            }
        });
    }

    void refreshItems(){
        Log.d(TAG, "refreshItems");
        FileUtils.setRefreshing(swipeRefreshLayout, true);
        // TODO TESTER QUE LES PHOTOS / SIGNATURE / MRO SONT SUR LE SERVEUR
        if(!MroRequestCurrent.getInstance().isMroRequestCheckable()) {
            Log.d(TAG, "refreshItems: données sauvegardées sur le serveur, let's refresh !");
            loadMroRequest();
            //deleteAllTablePictures();
            getThumbnailsFromServer();
            //deleteAllTableSignature();
            getSignatureFromServer();
        }
        else
            Log.d(TAG, "refreshItems: données sauvegardées sur le serveur, don't refresh !!");

    }

    // S t a r t   &   s t o p   M r o R e q u e s t
    // ---------------------------------------------
    void startMroRequest(){
        Log.d(TAG, "startMroRequest");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "startMroRequest: mroRequestCurrent is NULL");
            return;
        }
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        service.startMroRequest(mroRequestCurrent.getId(),
                MroRequestCurrent.getInstance().getMroRequestCurrent(), new Callback<MroRequest>() {

                    @Override
                    public void success(MroRequest request, retrofit.client.Response response) {
                        if (request == null) return;

                        Log.i(TAG, "success: retrofit.client.Response=" + response);
                        MroRequestCurrent.getInstance().setIsMroRequestCheckable(false);
                        Operator operator = request.getOperation().getMroOperator();
                        MroRequestCurrent.getInstance().getMroRequestCurrent()
                                .getOperation().setMroOperator(operator);
                        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
                        mroRequestCurrent.setStatus(request.getStatus());
                        Date mroRequestStartDate = request.getOperation().getMroStartDate();
                        Log.d(TAG, "startMroRequest: mroRequestStartDate=" + mroRequestStartDate);
                        mroRequestCurrent.getOperation().setMroStartDate(mroRequestStartDate);
                        mroRequestCurrent.setEntityVersion(request.getEntityVersion());
                        setUIState();
                        setHeaderView();
                        AndroidUtils.showToast(MroRequestActivity.this,
                                "Démarrage de l'intervention réalisée avec succès");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        FileUtils.setRefreshing(swipeRefreshLayout, false);
                        setRetrofitError(error);
                    }
                });
    }

    synchronized void saveAndFinishMroRequest(){
        Log.d(TAG, "saveAndFinishMroRequest");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "saveAndFinishMroRequest: mroRequestCurrent is NULL");
            return;
        }
        machine = mroRequestCurrent.getMachine();
        if(machine != null){
            if(machine.getEquipment()!= null){
                if(machine.getEquipment().getReference().equals(HibmobtechApplication.UNKNOWN_MACHINE)){
                    MroRequestCurrent.getInstance().getMroRequestCurrent().setMachine(null);
                }
            }
        }
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        service.putMroRequest(mroRequestCurrent.getId(), mroRequestCurrent, new Callback<MroRequest>() {

            @Override
            public void success(MroRequest mroRequest, retrofit.client.Response response) {
                if (mroRequest == null) return;
                Log.i(TAG, "success: retrofit.client.Response=" + response);
                MroRequestCurrent.getInstance().setIsMroRequestCheckable(false);
                MroRequestCurrent.getInstance().getMroRequestCurrent()
                        .setEntityVersion(mroRequest.getEntityVersion());
                mainMenuCheckItem.setVisible(false);
                finishMroRequest();
                setHeaderView();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "failure : " + error.toString());
                Response response = error.getResponse();
                FileUtils.setRefreshing(swipeRefreshLayout, false);
                if (response != null) {
                    Log.d(TAG, "response != null");

                    if (response.getStatus() == 409) { // 409 Conflict
                        Log.d(TAG, "response.getStatus()== 409");

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int choice) {
                                switch (choice) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        MroRequestApiService service = HibmobtechApplication.getRestClient()
                                                .getMroRequestService();
                                        Long mroRequestId = MroRequestCurrent.getInstance()
                                                .getMroRequestCurrent().getId();
                                        service.getMroRequest(mroRequestId, new Callback<MroRequest>() {
                                            @Override
                                            public void success(MroRequest mroRequest,
                                                                retrofit.client.Response response) {
                                                if (mroRequest != null) {
                                                    MroRequestCurrent.getInstance()
                                                            .setMroRequestCurrent(mroRequest);
                                                }
                                                finish();
                                                startActivity(getIntent());
                                            }

                                            @Override
                                            public void failure(RetrofitError error) {
                                                setRetrofitError(error);
                                            }
                                        });
                                        break;
                                }
                            }
                        };
                        AlertDialog.Builder builder = new AlertDialog.Builder(MroRequestActivity.this);
                        if (!MroRequestActivity.this.isFinishing())
                            builder.setMessage("Attention ! cette intervention vient d'être modifiée par un autre utilisateur, veuillez la ressaisir")
                                    .setPositiveButton("OK", dialogClickListener).show();
                    } else {
                        Log.d(TAG, "response.getStatus()!= 409: Déconnecté du réseau");

                        setRetrofitError(error);
                        AndroidUtils.showToast(MroRequestActivity.this, "Déconnecté du réseau");
                    }
                } else {
                    Log.d(TAG, "response == null: Déconnecté du réseau");
                    AndroidUtils.showToast(MroRequestActivity.this, "Déconnecté du réseau");
                }
            }
        });
    }

    synchronized void finishMroRequest(){
        Log.d(TAG, "finishMroRequest");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "finishMroRequest: mroRequestCurrent is NULL");
            return;
        }
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        service.finishMroRequest(mroRequestCurrent.getId(),
                MroRequestCurrent.getInstance().getMroRequestCurrent(), new Callback<MroRequest>() {

                    @Override
                    public void success(MroRequest mroRequest, retrofit.client.Response response) {
                        if (mroRequest == null) return;
                        Log.i(TAG, "success: retrofit.client.Response=" + response);

                        mroRequestCurrent.setStatus(mroRequest.getStatus());
                        mroRequestCurrent.setEntityVersion(mroRequest.getEntityVersion());
                        MroRequestCurrent.getInstance().setIsMroRequestCheckable(false);
                        AndroidUtils.showToast(MroRequestActivity.this,
                                "Fin de l'intervention enregistrée avec succès");
                        setUIState();
                        setHeaderView();
                        Intent intent = new Intent();
                        intent.putExtra(CURRENT_MRO_REQUEST_POSITION, position);
                        intent.putExtra(FRAGMENT_TYPE, fragmentId);
                        setResult(RESULT_OK, intent);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        setRetrofitError(error);
                    }
                });
    }

    void takeMroRequest(){
        Log.d(TAG, "takeMroRequest");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "takeMroRequest: mroRequestCurrent is NULL");
            return;
        }
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        service.takeMroRequest(mroRequestCurrent.getId(), mroRequestCurrent, new Callback<MroRequest>() {

            @Override
            public void success(MroRequest mroRequest, retrofit.client.Response response) {
                if (mroRequest == null) return;

                Log.i(TAG, "success: retrofit.client.Response=" + response);
                mroRequestCurrent.setStatus(mroRequest.getStatus());
                mroRequestCurrent.setEntityVersion(mroRequest.getEntityVersion());
                AndroidUtils.showToast(MroRequestActivity.this,
                        "Affectation de l'intervention réalisé avec succès");
                setUIState();
                setHeaderView();
                Intent intent = new Intent();
                intent.putExtra(CURRENT_MRO_REQUEST_POSITION, position);
                intent.putExtra(FRAGMENT_TYPE, fragmentId);
                setResult(RESULT_OK, intent);
            }

            @Override
            public void failure(RetrofitError error) {
                FileUtils.setRefreshing(swipeRefreshLayout, false);
                setRetrofitError(error);
            }
        });
    }

    synchronized void saveMroRequestCurrent(){
        Log.d(TAG, "saveMroRequestCurrent");
        mainMenuCheckItem.setVisible(false);
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "saveMroRequestCurrent: mroRequestCurrent is NULL");
            return;
        }
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        MroRequest mroRequest = mroRequestCurrent;

        if(machine != null){
            if(machine.getEquipment()!= null){
                if(machine.getEquipment().getReference().equals(HibmobtechApplication.UNKNOWN_MACHINE)){
                    MroRequestCurrent.getInstance().getMroRequestCurrent().setMachine(null);
                }
            }
        }
        service.putMroRequest(mroRequest.getId(), mroRequest, new Callback<MroRequest>() {

            @Override
            public void success(MroRequest mroRequest, retrofit.client.Response response) {
                if (mroRequest == null) return;

                Log.i(TAG, "success: retrofit.client.Response=" + response);
                MroRequestCurrent.getInstance().setIsMroRequestCheckable(false);
                MroRequestCurrent.getInstance().getMroRequestCurrent()
                        .setEntityVersion(mroRequest.getEntityVersion());
                AndroidUtils.showToast(MroRequestActivity.this, "Modification enregistrée avec succès");
                if (isMroRequestFinishedOnUpdate == true) {
                    isMroRequestFinishedOnUpdate = false;
                }
            }

            @Override
            public void failure(RetrofitError error) {
                FileUtils.setRefreshing(swipeRefreshLayout, false);
                setRetrofitError(error);
            }
        });
    }


    // O t h e r s   m e t h o d s
    // ---------------------------
    protected void setSiteParams(){
        Log.d(TAG, "setSiteParams");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent != null) {
            siteCurrent = mroRequestCurrent.getSite();
            siteId = siteCurrent.getId();
            SiteCurrent.getInstance().setSiteCurrent(siteCurrent);
        }
    }

    private void showAlertDialog(){
        Log.d(TAG, "showAlertDialog");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "showAlertDialog: mroRequestCurrent is NULL");
            return;
        }
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Quitter l'intervention");
        dialog.setMessage("Voulez-vous quitter l'intervention sans la sauvegarder ?");
        dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                Log.d(TAG, "onClick: Oui");
                if(dialog != null){
                    dialog.dismiss();
                }
                mroRequestCurrent.setMachine(null);
                isMroRequestFinishedOnUpdate = false;
                finish();
            }
        });
        dialog.setNegativeButton("Non", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which){
                Log.d(TAG, "onClick: Non");
                if(dialog != null){
                    dialog.dismiss();
                }
                return;
            }
        });
        dialog.show();
    }

    protected void hightLightBackground(final LinearLayout linearLayout){
        Log.d(TAG, "hightLightBackground");
        handler = new Handler();
        (new Thread(){
            @Override
            public void run(){
                for(int i = 100; i < 255; i++){
                    final int finalI = i;
                    final int finalI1 = i;
                    final int finalI2 = i;
                    handler.post(new Runnable(){
                        public void run(){
                            linearLayout.setBackgroundColor(Color.argb(255, finalI, finalI1, finalI2));
                        }
                    });
                    // next will pause the thread for some time
                    try {
                        sleep(10);
                    } catch(Exception e){
                        Log.e("setCausesView: Except=", e.toString());
                        break;
                    }
                }
            }
        }).start();
    }

    // U I   S t a t e
    // ---------------
    public void setUIState(){
        Log.d(TAG, "setUIState");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setUIState: mroRequestCurrent is NULL");
            return;
        }
        mroRequestFloatingActionButton =(FloatingActionButton)findViewById(R.id.mroRequestFloatingActionButton);
        setOnClickFloatingActionButton();
        setBackgroundColor(mroRequestCurrent, mroRequestHeaderDateLinearLayout);

        switch(mroRequestCurrent.getMroType()){
            case PREVISIT:
                setUIStatePrevisit();
                break;
            case INSTALLATION:
                setUIStateInstallation();
                break;
            case MAINTENANCE:
                setUIStateMaintenance();
                break;
            case DELIVERY:
                setUIStateDelivery();
                break;
            default:
                Log.e(TAG, "setUIState: mroRequest.getMroType()== ERROR Default");
                break;
        }

        MroRequest.Status status = mroRequestCurrent.getStatus();
        switch(status){
            case OPENED:
                setUIStateOpen();
                break;
            case ASSIGNED: // operator has been assigned to the job
                setUIStateAssigned();
                break;
            case PLANNED: // work is planned
                setUIStatePlanned();
                break;
            case IN_PROGRESS: // work on site is on going
                setUIStateInProgress();
                break;
            case FINISHED: // work on site is callbackReceived
                setUIStateFinished();
                break;
            case CLOSED: // billing callbackReceived
                setUIStateClosed();
                break;
            case CANCELED:
                break;
            default:
                Log.d(TAG, "setUIStateDelivery: State unknown !");
                break;
        }
        setHeaderView();
    }

    public void setUIStateMaintenance(){
        Log.d(TAG, "setUIStateMaintenance");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setUIStateMaintenance: mroRequestCurrent is NULL");
            return;
        }
        mroRequestFloatingActionButton =(FloatingActionButton)findViewById(R.id.mroRequestFloatingActionButton);
        setOnClickFloatingActionButton();
        Log.d(TAG, "setUIStateMaintenance: setBackgroundColor");
        setBackgroundColor(mroRequestCurrent, mroRequestHeaderDateLinearLayout);
    }

    public void setUIStatePrevisit(){
        Log.d(TAG, "setUIStatePrevisit");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setUIStatePrevisit: mroRequestCurrent is NULL");
            return;
        }
        mroRequestFloatingActionButton =(FloatingActionButton)findViewById(R.id.mroRequestFloatingActionButton);
        setOnClickFloatingActionButton();
        setBackgroundColor(mroRequestCurrent, mroRequestHeaderDateLinearLayout);

        LinearLayout machineListRowLinearLayout3;
        machineListRowLinearLayout3 =(LinearLayout)findViewById(R.id.machineListRowLinearLayout3);
        machineListRowLinearLayout3.setVisibility(View.GONE);

        LinearLayout mroRequestProblemLinearLayout;
        mroRequestProblemLinearLayout =(LinearLayout)findViewById(R.id.mroRequestProblemLinearLayout);
        mroRequestProblemLinearLayout.setVisibility(View.GONE);

        LinearLayout mroRequestCauseLinearLayout;
        mroRequestCauseLinearLayout =(LinearLayout)findViewById(R.id.mroRequestCauseLinearLayout1);
        mroRequestCauseLinearLayout.setVisibility(View.GONE);
        View mroRequestCauseView;
        mroRequestCauseView = findViewById(R.id.mroRequestCauseView);
        mroRequestCauseView.setVisibility(View.GONE);

        LinearLayout mroRequestTaskLinearLayout;
        mroRequestTaskLinearLayout =(LinearLayout)findViewById(R.id.mroRequestTaskLinearLayout1);
        mroRequestTaskLinearLayout.setVisibility(View.GONE);
        View mroRequestTaskView;
        mroRequestTaskView = findViewById(R.id.mroRequestTaskView);
        mroRequestTaskView.setVisibility(View.GONE);

        LinearLayout mroRequestSparePartLinearLayout;
        mroRequestSparePartLinearLayout =(LinearLayout)findViewById(R.id.mroRequestSparePartLinearLayout1);
        mroRequestSparePartLinearLayout.setVisibility(View.GONE);
        View mroRequestSparePartView;
        mroRequestSparePartView = findViewById(R.id.mroRequestSparePartView);
        mroRequestSparePartView.setVisibility(View.GONE);

        LinearLayout mroRequestQuotationRequestedGlobalLinearLayout;
        mroRequestQuotationRequestedGlobalLinearLayout =(LinearLayout)findViewById(R.id.mroRequestQuotationRequestedGlobalLinearLayout);
        mroRequestQuotationRequestedGlobalLinearLayout.setVisibility(View.GONE);

    }

    public void setUIStateInstallation(){
        Log.d(TAG, "setUIStateInstallation");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setUIStateInstallation: mroRequestCurrent is NULL");
            return;
        }
        mroRequestFloatingActionButton =(FloatingActionButton)findViewById(R.id.mroRequestFloatingActionButton);
        setOnClickFloatingActionButton();
        setBackgroundColor(mroRequestCurrent, mroRequestHeaderDateLinearLayout);

        LinearLayout machineListRowLinearLayout3;
        machineListRowLinearLayout3 =(LinearLayout)findViewById(R.id.machineListRowLinearLayout3);
        machineListRowLinearLayout3.setVisibility(View.GONE);

        LinearLayout mroRequestProblemLinearLayout;
        mroRequestProblemLinearLayout =(LinearLayout)findViewById(R.id.mroRequestProblemLinearLayout);
        mroRequestProblemLinearLayout.setVisibility(View.GONE);

        LinearLayout mroRequestCauseLinearLayout;
        mroRequestCauseLinearLayout =(LinearLayout)findViewById(R.id.mroRequestCauseLinearLayout1);
        mroRequestCauseLinearLayout.setVisibility(View.GONE);
        View mroRequestCauseView;
        mroRequestCauseView = findViewById(R.id.mroRequestCauseView);
        mroRequestCauseView.setVisibility(View.GONE);

        LinearLayout mroRequestTaskLinearLayout;
        mroRequestTaskLinearLayout =(LinearLayout)findViewById(R.id.mroRequestTaskLinearLayout1);
        mroRequestTaskLinearLayout.setVisibility(View.GONE);
        View mroRequestTaskView;
        mroRequestTaskView = findViewById(R.id.mroRequestTaskView);
        mroRequestTaskView.setVisibility(View.GONE);
    }

    public void setUIStateDelivery(){
        Log.d(TAG, "setUIStateDelivery");
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setUIStateDelivery: mroRequestCurrent is NULL");
            return;
        }

        LinearLayout mroRequestPictureLinearLayout;
        mroRequestPictureLinearLayout =(LinearLayout)findViewById(R.id.mroRequestPictureLinearLayout);
        mroRequestPictureLinearLayout.setVisibility(View.GONE);

        mroRequestFloatingActionButton =(FloatingActionButton)findViewById(R.id.mroRequestFloatingActionButton);
        setOnClickFloatingActionButton();
        setBackgroundColor(mroRequestCurrent, mroRequestHeaderDateLinearLayout);

        LinearLayout machineListRowLinearLayout3;
        machineListRowLinearLayout3 =(LinearLayout)findViewById(R.id.machineListRowLinearLayout3);
        machineListRowLinearLayout3.setVisibility(View.GONE);

        LinearLayout mroRequestProblemLinearLayout;
        mroRequestProblemLinearLayout =(LinearLayout)findViewById(R.id.mroRequestProblemLinearLayout);
        mroRequestProblemLinearLayout.setVisibility(View.GONE);

        LinearLayout mroRequestCauseLinearLayout;
        mroRequestCauseLinearLayout =(LinearLayout)findViewById(R.id.mroRequestCauseLinearLayout1);
        mroRequestCauseLinearLayout.setVisibility(View.GONE);
        View mroRequestCauseView;
        mroRequestCauseView = findViewById(R.id.mroRequestCauseView);
        mroRequestCauseView.setVisibility(View.GONE);

        LinearLayout mroRequestTaskLinearLayout;
        mroRequestTaskLinearLayout =(LinearLayout)findViewById(R.id.mroRequestTaskLinearLayout1);
        mroRequestTaskLinearLayout.setVisibility(View.GONE);
        View mroRequestTaskView;
        mroRequestTaskView = findViewById(R.id.mroRequestTaskView);
        mroRequestTaskView.setVisibility(View.GONE);
    }

    public void setUIStateOpen(){
        Log.d(TAG, "setUIStateOpen");
        if(mainMenuCheckItem != null){
            mainMenuCheckItem.setVisible(false);
            mainMenuCheckItem.setCheckable(false);
        }
        if(mainMenuUpdateItem != null)
            mainMenuUpdateItem.setVisible(false);
        if(mainMenuMroStartDateItem != null){
            mainMenuMroStartDateItem.setVisible(false);
            mainMenuMroStartDateItem.setVisible(true);
            switch(mroRequestCurrent.getMroType()){
                case PREVISIT:
                    mainMenuMroStartDateItem.setTitle("Visite préventive Ouverte, à prendre...");
                    break;
                case INSTALLATION:
                    mainMenuMroStartDateItem.setTitle("Installation Ouverte, à prendre...");
                    break;
                case MAINTENANCE:
                    mainMenuMroStartDateItem.setTitle("Maintenance SAV Ouverte, à prendre...");
                    break;
                case DELIVERY:
                    mainMenuMroStartDateItem.setTitle("Livraison Ouverte, à prendre...");
                    break;
                default:
                    Log.e(TAG, "setUIStateOpen: mroRequest.getMroType()== ERROR Default");
                    break;
            }
        }
        deleteEditMachineButton();
        setIconsUpdateVisibility(View.INVISIBLE);
        mroRequestFloatingActionButton.setBackgroundTintList(getResources().getColorStateList(R.color.get_assigned));
        mroRequestFloatingActionButton.setImageDrawable(getResources().getDrawable(R.mipmap.ic_plus));
        if(mroRequestQuotationRequestedCheckBox != null)
            mroRequestQuotationRequestedCheckBox.setClickable(false);
    }

    public void setUIStatePlanned(){
        Log.d(TAG, "setUIStatePlanned");
        if(mainMenuCheckItem != null){
            mainMenuCheckItem.setVisible(false);
            mainMenuCheckItem.setCheckable(false);
        }
        if(mainMenuUpdateItem != null)
            mainMenuUpdateItem.setVisible(false);
        if(mainMenuMroStartDateItem != null){
            mainMenuMroStartDateItem.setVisible(true);
            switch(mroRequestCurrent.getMroType()){
                case PREVISIT:
                    mainMenuMroStartDateItem.setTitle("Visite préventive Planifiée, à prendre...");
                    break;
                case INSTALLATION:
                    mainMenuMroStartDateItem.setTitle("Installation Planifiée, à prendre...");
                    break;
                case MAINTENANCE:
                    mainMenuMroStartDateItem.setTitle("Maintenance SAV Planifiée, à prendre...");
                    break;
                case DELIVERY:
                    mainMenuMroStartDateItem.setTitle("Livraison Planifiée, à prendre...");
                    break;
                default:
                    Log.e(TAG, "setUIStatePlanned: mroRequest.getMroType()== ERROR Default");
                    break;
            }
        }
        deleteEditMachineButton();
        setIconsUpdateVisibility(View.INVISIBLE);
        mroRequestFloatingActionButton.setVisibility(View.VISIBLE);
        if(mroRequestQuotationRequestedCheckBox != null)
            mroRequestQuotationRequestedCheckBox.setClickable(false);
    }

    public void setUIStateAssigned(){
        Log.d(TAG, "setUIStateAssigned");
        if(mainMenuCheckItem != null){
            mainMenuCheckItem.setVisible(false);
            mainMenuCheckItem.setCheckable(false);
        }
        if(mainMenuUpdateItem != null)
            mainMenuUpdateItem.setVisible(false);
        if(mainMenuMroStartDateItem != null){
            mainMenuMroStartDateItem.setVisible(true);
            switch(mroRequestCurrent.getMroType()){
                case PREVISIT:
                    mainMenuMroStartDateItem.setTitle("Visite préventive Planifiée, à démarrer...");
                    break;
                case INSTALLATION:
                    mainMenuMroStartDateItem.setTitle("Installation Planifiée, à démarrer...");
                    break;
                case MAINTENANCE:
                    mainMenuMroStartDateItem.setTitle("Maintenance SAV Planifiée, à démarrer...");
                    break;
                case DELIVERY:
                    mainMenuMroStartDateItem.setTitle("Livraison Planifiée, à démarrer...");
                    break;
                default:
                    Log.e(TAG, "setUIStateAssigned: mroRequest.getMroType()== ERROR Default");
                    break;
            }
        }
        deleteEditMachineButton();
        setIconsUpdateVisibility(View.INVISIBLE);
        mroRequestFloatingActionButton.setBackgroundTintList(getResources().getColorStateList(R.color.get_in_progress));
        mroRequestFloatingActionButton.setImageDrawable(getResources().getDrawable(R.mipmap.ic_play_arrow));
        if(mroRequestQuotationRequestedCheckBox != null)
            mroRequestQuotationRequestedCheckBox.setClickable(false);
    }

    public void setUIStateInProgress(){
        Long userId = HibmobtechApplication.getDataStore().getUserId();
        Log.d(TAG, "setUIStateInProgress:userId=" + userId);
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setUIStateInProgress: mroRequestCurrent is NULL");
            return;
        }
        Operator mroOperator = mroRequestCurrent.getOperation().getMroOperator();
        if(mroOperator == null){
            Log.d(TAG, "setUIStateInProgress: mroOperator is NULL");
            return;
        }
        Long mroOperatorId = mroOperator.getId();
        if(mroOperatorId == null){
            Log.d(TAG, "setUIStateInProgress: mroOperatorId is NULL");
            return;
        }

        // la modification n'est possible que pour l'operator ayant démarré l'intervention
        if(userId.equals(mroOperatorId)){
            if(MroRequestCurrent.getInstance().isMroRequestCheckable()){
                if(mainMenuCheckItem != null){
                    mainMenuCheckItem.setVisible(true);
                    mainMenuCheckItem.setCheckable(true);
                }
            } else {
                if(mainMenuCheckItem != null){
                    mainMenuCheckItem.setVisible(false);
                    mainMenuCheckItem.setCheckable(false);
                }
            }
            if(mainMenuUpdateItem != null)
                mainMenuUpdateItem.setVisible(false);
            if(mainMenuMroStartDateItem != null)
                mainMenuMroStartDateItem.setVisible(true);

            switch(mroRequestCurrent.getMroType()){
                case PREVISIT:
                    mainMenuMroStartDateItem.setTitle("Visite préventive Démarrée");
                    break;
                case INSTALLATION:
                    mainMenuMroStartDateItem.setTitle("Installation Démarrée");
                    break;
                case MAINTENANCE:
                    mainMenuMroStartDateItem.setTitle("Maintenance SAV Démarrée");
                    break;
                case DELIVERY:
                    mainMenuMroStartDateItem.setTitle("Livraison Démarrée");
                    break;
                default:
                    Log.e(TAG, "setUIStateInProgress: mroRequest.getMroType()== ERROR Default");
                    break;
            }
            setMachineView();
            setIconsUpdateVisibility(View.VISIBLE);
            mroRequestFloatingActionButton.setBackgroundTintList(getResources().getColorStateList(R.color.get_finished));
            mroRequestFloatingActionButton.setImageDrawable(getResources().getDrawable(R.mipmap.ic_stop));
            if(mroRequestQuotationRequestedCheckBox != null)
                mroRequestQuotationRequestedCheckBox.setClickable(true);
        } else {
            if(mainMenuCheckItem != null){
                mainMenuCheckItem.setVisible(false);
                mainMenuCheckItem.setCheckable(false);
            }
            if(mainMenuUpdateItem != null)
                mainMenuUpdateItem.setVisible(false);
            if(mainMenuMroStartDateItem != null)
                mainMenuMroStartDateItem.setVisible(true);

            switch(mroRequestCurrent.getMroType()){
                case PREVISIT:
                    mainMenuMroStartDateItem.setTitle("Visite préventive Démarrée");
                    break;
                case INSTALLATION:
                    mainMenuMroStartDateItem.setTitle("Installation Démarrée");
                    break;
                case MAINTENANCE:
                    mainMenuMroStartDateItem.setTitle("Maintenance SAV Démarrée");
                    break;
                case DELIVERY:
                    mainMenuMroStartDateItem.setTitle("Livraison Démarrée");
                    break;
                default:
                    Log.e(TAG, "setUIStateInProgress: mroRequest.getMroType()== ERROR Default");
                    break;
            }

            setMachineView();
            setIconsUpdateVisibility(View.GONE);
            mroRequestFloatingActionButton.setVisibility(View.GONE);
            if(mroRequestQuotationRequestedCheckBox != null)
                mroRequestQuotationRequestedCheckBox.setClickable(false);
        }
    }

    public void setUIStateFinished(){
        Log.d(TAG, "setUIStateFinished");
        Long userId = HibmobtechApplication.getDataStore().getUserId();
        Log.d(TAG, "setUIStateFinished:userId=" + userId);
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.e(TAG, "setUIStateFinished: mroRequestCurrent is NULL");
            return;
        }
        Operator mroOperator = mroRequestCurrent.getOperation().getMroOperator();
        if(mroOperator == null){
            Log.e(TAG, "setUIStateFinished: mroOperator is NULL");
            return;
        }
        Long mroOperatorId = mroOperator.getId();
        if(mroOperatorId == null){
            Log.e(TAG, "setUIStateFinished: mroOperatorId is NULL");
            return;
        }

        // la modification n'est possible que pour l'operator ayant démarré l'intervention
        if(userId.equals(mroOperatorId)){
            Log.d(TAG, "setUIStateFinished: userId = mroOperatorId");

            if(mainMenuUpdateItem != null)mainMenuUpdateItem.setVisible(true);
            if(isMroRequestFinishedOnUpdate){
                Log.d(TAG, "setUIStateFinished: isMroRequestFinishedOnUpdate == true");

                if(mainMenuCheckItem != null){
                    if(MroRequestCurrent.getInstance().isMroRequestCheckable()){
                        mainMenuCheckItem.setVisible(true);
                        mainMenuCheckItem.setCheckable(true);
                    } else {
                        mainMenuCheckItem.setVisible(false);
                        mainMenuCheckItem.setCheckable(false);
                    }
                }
                if(mainMenuMroStartDateItem != null)mainMenuMroStartDateItem.setVisible(false);
                setIconsUpdateVisibility(View.VISIBLE);
                setMachineView();
                if(mroRequestQuotationRequestedCheckBox != null)
                    mroRequestQuotationRequestedCheckBox.setClickable(true);
            } else {
                Log.d(TAG, "setUIStateFinished: isMroRequestFinishedOnUpdate == false");

                if(mainMenuCheckItem != null){
                    mainMenuCheckItem.setVisible(false);
                    mainMenuCheckItem.setCheckable(false);
                }
                if(mainMenuMroStartDateItem != null)
                    mainMenuMroStartDateItem.setVisible(true);

                setIconsUpdateVisibility(View.INVISIBLE);
                deleteEditMachineButton();
                if(mroRequestQuotationRequestedCheckBox != null)
                    mroRequestQuotationRequestedCheckBox.setClickable(false);
            }
            if(mroRequestFloatingActionButton != null)
                mroRequestFloatingActionButton.setVisibility(View.GONE);

            switch(mroRequestCurrent.getMroType()){
                case PREVISIT:
                    mainMenuMroStartDateItem.setTitle("Visite préventive Terminée");
                    break;
                case INSTALLATION:
                    mainMenuMroStartDateItem.setTitle("Installation Terminée");
                    break;
                case MAINTENANCE:
                    mainMenuMroStartDateItem.setTitle("Maintenance SAV Terminée");
                    break;
                case DELIVERY:
                    mainMenuMroStartDateItem.setTitle("Livraison Terminée");
                    break;
                default:
                    Log.e(TAG, "setUIStateFinished: mroRequest.getMroType()== ERROR Default");
                    break;
            }
        } else {
            Log.d(TAG, "setUIStateFinished: userId != mroOperatorId");

            if(mainMenuUpdateItem != null)mainMenuUpdateItem.setVisible(false);
            if(mainMenuCheckItem != null)mainMenuCheckItem.setCheckable(false);
            if(mainMenuMroStartDateItem != null)mainMenuMroStartDateItem.setVisible(false);
            setIconsUpdateVisibility(View.GONE);
            setMachineView();

            switch(mroRequestCurrent.getMroType()){
                case PREVISIT:
                    mainMenuMroStartDateItem.setTitle("Visite préventive Terminée");
                    break;
                case INSTALLATION:
                    mainMenuMroStartDateItem.setTitle("Installation Terminée");
                    break;
                case MAINTENANCE:
                    mainMenuMroStartDateItem.setTitle("Maintenance SAV Terminée");
                    break;
                case DELIVERY:
                    mainMenuMroStartDateItem.setTitle("Livraison Terminée");
                    break;
                default:
                    Log.e(TAG, "setUIStateFinished: mroRequest.getMroType()== ERROR Default");
                    break;
            }
            setIconsUpdateVisibility(View.INVISIBLE);
            deleteEditMachineButton();
            if(mroRequestQuotationRequestedCheckBox != null)mroRequestQuotationRequestedCheckBox.setClickable(false);
        }
        mroRequestFloatingActionButton.setVisibility(View.GONE);
    }

    public void setUIStateClosed(){
        Log.d(TAG, "setUIStateClosed");
        if(mainMenuCheckItem != null){
            mainMenuCheckItem.setVisible(false);
            mainMenuCheckItem.setCheckable(false);
        }
        if(mainMenuUpdateItem != null)mainMenuUpdateItem.setVisible(false);
        if(mainMenuMroStartDateItem != null)mainMenuMroStartDateItem.setVisible(false);
        deleteEditMachineButton();
        setIconsUpdateVisibility(View.INVISIBLE);
        mroRequestFloatingActionButton.setVisibility(View.GONE);
        if(mroRequestQuotationRequestedCheckBox != null)mroRequestQuotationRequestedCheckBox.setClickable(false);
    }

    public void setIconsUpdateVisibility(int visibility){
        Log.d(TAG, "setIconsUpdateVisibility:visibility=" + visibility);
        if(mroRequestCommentPencilIcon != null)mroRequestCommentPencilIcon.setVisibility(visibility);
        if(mroRequestCausePencilIcon != null)mroRequestCausePencilIcon.setVisibility(visibility);
        if(mroRequestTaskPencilIcon != null)mroRequestTaskPencilIcon.setVisibility(visibility);
        if(mroRequestSparePartPencilIcon != null)mroRequestSparePartPencilIcon.setVisibility(visibility);
        if(mroRequestRowCameraTitleIcon != null)mroRequestRowCameraTitleIcon =(TextView)findViewById(R.id.icon_fa_camera);
        if(mroRequestRowCameraTitleIcon != null)mroRequestRowCameraTitleIcon.setTypeface(hibmobtechFont);
        if(mroRequestRowCameraTitleIcon != null)mroRequestRowCameraTitleIcon.setVisibility(visibility);
        if(mroRequestRowPaperClipTitleIcon != null)mroRequestRowPaperClipTitleIcon =(TextView)findViewById(R.id.icon_fa_paperclip);
        if(mroRequestRowPaperClipTitleIcon != null)mroRequestRowPaperClipTitleIcon.setTypeface(hibmobtechFont);
        if(mroRequestRowPaperClipTitleIcon != null)mroRequestRowPaperClipTitleIcon.setVisibility(visibility);
        if(mroRequestSignatureTitleIconPencilTextView != null)mroRequestSignatureTitleIconPencilTextView.setVisibility(visibility);

    }

    // F l o a t i n g   A c t i o n   B u t t o n
    // -------------------------------------------
    public void setOnClickFloatingActionButton(){
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null){
            Log.d(TAG, "setOnClickFloatingActionButton: mroRequestCurrent is NULL");
            return;
        }
        mroRequestFloatingActionButton.setClickable(true);
        mroRequestFloatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                MroRequest.Status status = mroRequestCurrent.getStatus();
                Log.d(TAG, "setOnClickFloatingActionButton: status=" + status);
                switch(status){
                    case OPENED:
                        setOnClickFloatingActionButtonOpen();
                        break;
                    case ASSIGNED: // operator has been assigned to the job
                        setOnClickFloatingActionButtonAssigned();
                        break;
                    case PLANNED: // work is planned
                        break;
                    case IN_PROGRESS: // work on site is on going
                        setOnClickFloatingActionButtonInProgress();
                        break;
                    case FINISHED: // work on site is callbackReceived
                        setUIStateFinished();
                        break;
                    case CLOSED: // billing callbackReceived
                        setUIStateClosed();
                        break;
                    case CANCELED:
                        break;
                    default:
                        Log.d(TAG, "setUIState: State unknown !");
                        break;
                }
            }
        });
    }

    public void setOnClickFloatingActionButtonOpen(){
        Log.d(TAG, "setOnClickFloatingActionButtonOpen");
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int choice){
                switch(choice){
                    case DialogInterface.BUTTON_POSITIVE:
                        takeMroRequest();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(MroRequestActivity.this);
        if(!MroRequestActivity.this.isFinishing())
            builder.setMessage("Voulez-vous prendre cette intervention ?")
                    .setPositiveButton("Oui", dialogClickListener)
                    .setNegativeButton("Non", dialogClickListener).show();
    }

    public void setOnClickFloatingActionButtonAssigned(){
        Log.d(TAG, "setOnClickFloatingActionButtonAssigned");
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int choice){
                switch(choice){
                    case DialogInterface.BUTTON_POSITIVE:
                        startMroRequest();
                        mroRequestFloatingActionButton
                                .setBackgroundTintList(getResources().getColorStateList(R.color.get_in_progress));
                        mroRequestFloatingActionButton
                                .setImageDrawable(getResources().getDrawable(R.mipmap.ic_play_arrow));
                        setMachineView();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(MroRequestActivity.this);
        if(!MroRequestActivity.this.isFinishing())
            builder.setMessage("Voulez-vous démarrer l'intervention ?")
                    .setPositiveButton("Oui", dialogClickListener)
                    .setNegativeButton("Non", dialogClickListener).show();
    }

    public void setOnClickFloatingActionButtonInProgress(){
        Log.d(TAG, "setOnClickFloatingActionButtonInProgress");
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int choice){
                switch(choice){
                    case DialogInterface.BUTTON_POSITIVE:
                        mroRequestFloatingActionButton.setVisibility(View.GONE);
                        new SaveAndFinishMroRequestAsyncTask().execute();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(MroRequestActivity.this);
        if(!MroRequestActivity.this.isFinishing())
            builder.setMessage("Voulez-vous terminer l'intervention ?")
                    .setPositiveButton("Oui", dialogClickListener)
                    .setNegativeButton("Non", dialogClickListener).show();

    }

    public void setQuotationRequestedCheckBox(){
        mroRequestQuotationRequestedCheckBox =(CheckBox)findViewById(R.id.mroRequestQuotationRequestedCheckBox);
        mroRequestQuotationRequestedCheckBox.setChecked(false);
        MroRequest mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        if(mroRequestCurrent == null)return;
        Boolean isQuotationRequested = mroRequestCurrent.getQuotationRequested();
        if(isQuotationRequested == null)return;
        if(isQuotationRequested){
            Log.d(TAG, "setQuotationRequestedCheckBox: Devis demandé : OUI");
            mroRequestQuotationRequestedCheckBox.setChecked(true);
        }
        else {
            Log.d(TAG, "setQuotationRequestedCheckBox: Devis demandé : NON");
            mroRequestQuotationRequestedCheckBox.setChecked(false);

        }
    }
    public void onCheckboxClicked(View view){
        // Is the view now checked?
        boolean checked =((CheckBox)view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()){
            case R.id.mroRequestQuotationRequestedCheckBox:
                MroRequestCurrent.getInstance().setIsMroRequestCheckable(true);
                setUIState();
                if(checked){
                    Log.d(TAG, "onCheckboxClicked: Devis demandé : OUI");
                    MroRequestCurrent.getInstance().getMroRequestCurrent().setQuotationRequested(true);
                }
                else {
                    Log.d(TAG, "onCheckboxClicked: Devis demandé : NON");
                    MroRequestCurrent.getInstance().getMroRequestCurrent().setQuotationRequested(false);
                }
                break;
        }
    }

    // c l a s s   S a v e A n d F i n i s h M r o R e q u e s t A s y n c T a s k
    //
    // ---------------------------------------------------------------------------
    class SaveAndFinishMroRequestAsyncTask extends AsyncTask<Void, Integer, String> {

        // Surcharge de la méthode doInBackground(Celle qui s'exécute dans une Thread à part)
        @Override
        protected String doInBackground(Void... unused){
            Log.d(TAG, "SaveAndFinishMroRequestAsyncTask: doInBackground");
            saveAndFinishMroRequest();
            return("doInBackground finished");
        }

        // Surcharge de la méthode onProgressUpdate(s'exécute dans la Thread de l'IHM)
        @Override
        protected void onProgressUpdate(Integer... diff){
            // Mettre à jour l'IHM
            Log.d(TAG, "SaveAndFinishMroRequestAsyncTask: onProgressUpdate");
        }

        // Surcharge de la méthode onPostExecute(s'exécute dans la Thread de l'IHM)
        @Override
        protected void onPostExecute(String message){
            // Mettre à jour l'IHM
            Log.d(TAG, "SaveAndFinishMroRequestAsyncTask: onPostExecute" + message);
        }
    }
}
