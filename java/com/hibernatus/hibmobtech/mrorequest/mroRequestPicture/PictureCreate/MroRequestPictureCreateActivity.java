package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.PictureCreate;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hibernatus.hibmobtech.ActivityRefresher;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.HibmobtechOptionsMenuActivity;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.contentprovider.HibmobtechContentProvider;
import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoPicture;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.model.Picture;
import com.hibernatus.hibmobtech.model.PictureInfos;
import com.hibernatus.hibmobtech.mrorequest.MroRequestApiService;
import com.hibernatus.hibmobtech.mrorequest.MroRequestCurrent;
import com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.PictureUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static com.hibernatus.hibmobtech.HibmobtechApplication.PICTURE_HEIGHT;
import static com.hibernatus.hibmobtech.HibmobtechApplication.PICTURE_PATH;
import static com.hibernatus.hibmobtech.HibmobtechApplication.PICTURE_WEIGHT;
import static com.hibernatus.hibmobtech.HibmobtechApplication.THUMBNAIL_HEIGHT;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID_MRO_REQUEST;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_IMAGE;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_THUMBNAIL;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_TIME;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_TITLE;


// c l a s s   M r o R e q u e s t P i c t u r e C r e a t e A c t i v i t y
//
// Activité permettant d'afficher la photo après l'utilisation de l'appareil photo
// du téléphone. Elle permet de saisir le titre et la description et de la valider
// afin de la sauvegarder en base et de la transmettre au serveur
//
// --------------------------------------------------------------------------------

public class MroRequestPictureCreateActivity extends HibmobtechOptionsMenuActivity implements ActivityRefresher{
    public static final String TAG = MroRequestPictureCreateActivity.class.getSimpleName();


    protected PictureUtils pictureUtils;
    public Menu menu;
    protected MenuItem mainMenuItemCheck;
    protected String currentPictureTitle;
    protected ImageView mroRequestPictureUpdateImageView;
    protected EditText mroRequestPictureCreateActivityTitleEditText;
    protected MroRequest mroRequestCurrent;
    protected String imageCurrentHdpiPath;
    private SQLiteBaseDaoPicture SQLiteBaseDaoPicture;

    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        pictureUtils = new PictureUtils(getApplicationContext());
        SQLiteBaseDaoPicture = new SQLiteBaseDaoPicture(this);

        setContentView(R.layout.mro_request_picture_create_activity);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.hibmobToolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Intent intent = getIntent();
        imageCurrentHdpiPath = intent.getStringExtra(PICTURE_PATH);
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();

        // find Title edit text
        mroRequestPictureCreateActivityTitleEditText = (EditText)findViewById(R.id.mroRequestPictureCreateActivityTitleEditText);
        mroRequestPictureCreateActivityTitleEditText.requestFocus();

        // Set mroRequestClickPagerPictureListerner on edit text
        setEditTextListerner();

        // Set the picture
        mroRequestPictureUpdateImageView = (ImageView)findViewById(R.id.mroRequestPictureCreateImageView);
        Bitmap bitmap = BitmapFactory.decodeFile(imageCurrentHdpiPath);
        if(bitmap != null) {
            Log.d(TAG, "onCreate: pictureArrayList=" + imageCurrentHdpiPath);
            int screenWidth = pictureUtils.getScreenWidth();
            int height = bitmap.getHeight();
            int width = bitmap.getWidth();
            int newWidth = screenWidth;
            int newHeight = (height * screenWidth) / width;
            bitmap = pictureUtils.resizeBitmapToBitmap(bitmap, newWidth, newHeight);
            mroRequestPictureUpdateImageView.setImageBitmap(bitmap);
        }
        else{
            Log.e(TAG, "onCreate: bitmap == null");
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu");
        this.menu = menu;
        super.onCreateOptionsMenu(menu);
        mainMenuItemCheck = menu.findItem(R.id.mainMenuCheckItem);
        mainMenuItemCheck.setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Log.d(TAG, "onOptionsItemSelected: case home: getClass=" + getClass());
                finish();
                break;
            case R.id.mainMenuCheckItem:
                Log.d(TAG, "onOptionsItemSelected: mainMenuCheckItem");
                finish();
                new SaveAndPostCurrentPictureAsyncTask().execute();

                break;
            default:
                Log.d(TAG, "onOptionsItemSelected: default");
                break;
        }
        Log.d(TAG, "boolean Return true to consume menu here");
        return true;
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        showAlertDialog();
    }

    @Override
    public void refresh() {
        try {
            saveAndPostCurrentPicture();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // O t h e r s   m e t h o d s
    // ---------------------------
    synchronized void saveAndPostCurrentPicture() throws IOException {
        Log.d(TAG, "saveAndPostCurrentPicture");
        Picture picture = MroRequestCurrent.getInstance().getPicture(imageCurrentHdpiPath);

        // set new size of image
        BitmapFactory.Options optionsImage = new BitmapFactory.Options();
        optionsImage.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageCurrentHdpiPath, optionsImage);
        int heightPicture = optionsImage.outHeight;
        int widthPicture = optionsImage.outWidth;
        int newHeightPicture = PICTURE_HEIGHT;
        int newWidthPicture = (PICTURE_HEIGHT * widthPicture) / heightPicture;
        optionsImage.inJustDecodeBounds = false;
        Bitmap bitmapImage = BitmapFactory.decodeFile(imageCurrentHdpiPath);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapImage, newWidthPicture, newHeightPicture, false);

        // compress image & create pictureTypedFile for downloading to server
        byte[] compressedPictureByteArray = pictureUtils.compressBitmapToByteArray(scaledBitmap, PICTURE_WEIGHT, 80);
        Bitmap compressedPictureBitmap = pictureUtils.convertByteArrayToBitmap(compressedPictureByteArray);
        File compressedPictureFile = pictureUtils.compressBitmapToFile(compressedPictureBitmap, 75);
        Log.d(TAG, "saveAndPostCurrentPicture: size of PictureFile = " + compressedPictureFile.length()/1024 + " Ko");
        TypedFile compressedPictureTypedFile = new TypedFile("image/jpeg", compressedPictureFile);

        //  set image Picture
        picture.setImage(pictureUtils.convertBitmapToByteArray(compressedPictureBitmap));

        // set Thumbnail to save in database
        Bitmap bitmapThumbnail = ThumbnailUtils.extractThumbnail(PictureUtils.convertImagePathToBitmap(imageCurrentHdpiPath)
                , THUMBNAIL_HEIGHT, THUMBNAIL_HEIGHT);
        byte[] thumbnailByteArray = pictureUtils.compressBitmapToByteArray(bitmapThumbnail, 100);
        picture.setThumbnail(thumbnailByteArray);

        // set idMroRequest
        if(mroRequestCurrent != null) picture.setIdMroRequest(mroRequestCurrent.getId());

        // set infos
        PictureInfos pictureInfos = new  PictureInfos();
        pictureInfos.setTime(System.currentTimeMillis());
        if(currentPictureTitle != null)
            pictureInfos.setTitle(currentPictureTitle);
        else {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("'Photo prise le' dd/MM/yyyy 'à' HH:mm:ss");
            pictureInfos.setTitle(simpleDateFormat.format(new Date()));
        }
        picture.setInfos(pictureInfos);

        // insert picture in database
        long pictureId = insertPictureDataBase(HibmobtechContentProvider.urlForPicture(), picture);
        picture.setId(pictureId);

        // post picture to server
        postPictureOld(compressedPictureTypedFile, picture);
        finish();
    }

    public long insertPictureDataBase(Uri url, Picture picture) throws IOException {
        Log.d(TAG, "insertPictureDataBase: url=" + url + " picture.getId()=" + picture.getId());
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TIME, picture.getInfos().getTime());
        contentValues.put(COL_THUMBNAIL, picture.getThumbnail());
        contentValues.put(COL_IMAGE, picture.getImage());
        contentValues.put(COL_ID_MRO_REQUEST, picture.getIdMroRequest());
        contentValues.put(COL_TITLE, picture.getInfos().getTitle());
        Uri uri = getContentResolver().insert(url, contentValues);

        // URL of the newly created row
        Log.d(TAG, "insertPictureDataBase: URL of the newly created row" + uri);
        String id = uri.getLastPathSegment();
        Log.d(TAG, "update: id=" + id );
        getContentResolver().notifyChange(url, hibmobtechContentObserver);
        return Long.valueOf(id).longValue();
    }

    public void updatePictureByIdDataBase(Uri url, Picture picture) throws IOException {
        Log.d(TAG, "updatePictureDataBase: url=" + url + " picture.getId()=" + picture.getId());
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, picture.getId());
        contentValues.put(COL_TIME, picture.getInfos().getTime());
        contentValues.put(COL_THUMBNAIL, picture.getThumbnail());
        contentValues.put(COL_IMAGE, picture.getImage());
        contentValues.put(COL_ID_MRO_REQUEST, picture.getIdMroRequest());
        contentValues.put(COL_TITLE, picture.getInfos().getTitle());
        int numberOfRowsUpdated = getContentResolver().update(url, contentValues, COL_ID + "=?", new String[] {String.valueOf(picture.getId())});

        // URL of the newly updated row
        Log.d(TAG, "updatePictureByIdDataBase: number of rows updated = " + numberOfRowsUpdated);
        getContentResolver().notifyChange(url, hibmobtechContentObserver);
    }

    public void postPictureOld(TypedFile imageTypedFile, final Picture picture) throws IOException {
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        Log.d(TAG, "postPictureOld:POST picture to server");
        String titleString = picture.getInfos().getTitle();
        TypedString titleTypedString = new TypedString(new String (titleString.getBytes ("UTF-8"), "iso-8859-1"));

        service.uploadPictureWithRetrofit19(
                mroRequestCurrent.getId(),
                titleTypedString,
                imageTypedFile,
                new Callback<Picture>() {

                    @Override
                    public void success(Picture pictureResponse, Response response) {
                        Log.d(TAG, "postPictureOld:success: response.getUrl()=" + response.getUrl());
                        Log.d(TAG, "postPictureOld:success: response.getHeaders()=" + response.getHeaders());
                        Log.d(TAG, "postPictureOld:success: response.getBody()=" + response.getBody());
                        String bodyString = new String(((TypedByteArray) response.getBody()).getBytes());
                        Log.d(TAG, "postPictureOld:success: bodyString=" + bodyString);
                        if(pictureResponse != null) {
                            long initialPictureId = picture.getId();
                            long pictureResponseId = pictureResponse.getId();
                            Log.d(TAG, "postPictureOld:success: pictureResponseId=" + pictureResponseId);
                            picture.setId(pictureResponseId);
                            // update picture ID in database
                            try {
                                updatePictureByIdDataBase(HibmobtechContentProvider.urlForPicture(initialPictureId), picture);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if(!MroRequestPictureCreateActivity.this.isFinishing())
                            Toast.makeText(MroRequestPictureCreateActivity.this,
                                    "Photo transmise au serveur avec succès",
                                    Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        try {
                            insertPictureDataBase(HibmobtechContentProvider.urlForPictureCache(), picture);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void setEditTextListerner() {
        mroRequestPictureCreateActivityTitleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "setEditTextListerner: onTextChanged > s=" + s);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "setEditTextListerner: afterTextChanged > editable.toString()=" + editable.toString());
                currentPictureTitle = editable.toString();
                Log.d(TAG, "setEditTextListerner: afterTextChanged currentPictureTitle=" + currentPictureTitle);
                if (mainMenuItemCheck != null) {
                    if (!mainMenuItemCheck.isVisible()) {
                        mainMenuItemCheck.setVisible(true);
                    }
                }
            }
        });
    }

    private void showAlertDialog() {
        Log.d(TAG, "startMroRequest");
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Quitter l'intervention");
        dialog.setMessage("Voulez-vous quitter la photo sans la sauvegarder sur le serveur ?");
        dialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: Oui");
                if(dialog != null) {
                    dialog.dismiss();
                }
                finish();
            }
        });
        dialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "onClick: Non");
                if(dialog != null) {
                    dialog.dismiss();
                }
                return;
            }
        });
        if(!MroRequestPictureCreateActivity.this.isFinishing())
            dialog.show();
    }

    class SaveAndPostCurrentPictureAsyncTask extends AsyncTask<Void, Integer, String> {

        // Surcharge de la méthode doInBackground (Celle qui s'exécute dans une Thread à part)
        @Override
        protected String doInBackground(Void... unused) {
            Log.d(TAG, "SaveAndPostCurrentPictureAsyncTask: doInBackground");

            try {
                saveAndPostCurrentPicture();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            return ("Photo sauvegardée");
        }

        // Surcharge de la méthode onProgressUpdate (s'exécute dans la Thread de l'IHM)
        @Override
        protected void onProgressUpdate(Integer... diff) {
            // Mettre à jour l'IHM
            Log.d(TAG, "SaveAndPostCurrentPictureAsyncTask: onProgressUpdate");
        }

        // Surcharge de la méthode onPostExecute (s'exécute dans la Thread de l'IHM)
        @Override
        protected void onPostExecute(String message) {
            // Mettre à jour l'IHM
            Log.d(TAG, "SaveAndPostCurrentPictureAsyncTask: onPostExecute");

            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }
}
