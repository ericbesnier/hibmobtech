package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.GalleryOnPhone;

/**
 * Created by Eric on 20/02/2017.
 */

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.HibmobtechOptionsMenuActivity;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.contentprovider.HibmobtechContentProvider;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.model.Picture;
import com.hibernatus.hibmobtech.model.PictureInfos;
import com.hibernatus.hibmobtech.mrorequest.MroRequestApiService;
import com.hibernatus.hibmobtech.mrorequest.MroRequestCurrent;
import com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.PictureUtils;
import com.hibernatus.hibmobtech.utils.DialogUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static android.content.Intent.ACTION_PICK;
import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
import static com.hibernatus.hibmobtech.HibmobtechApplication.PICASSO_GALLERY_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.PICTURES_GALLERY_REQUEST;
import static com.hibernatus.hibmobtech.HibmobtechApplication.PICTURE_HEIGHT;
import static com.hibernatus.hibmobtech.HibmobtechApplication.PICTURE_WEIGHT;
import static com.hibernatus.hibmobtech.HibmobtechApplication.THUMBNAIL_HEIGHT;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID_MRO_REQUEST;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_IMAGE;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_THUMBNAIL;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_TIME;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_TITLE;
import static com.squareup.picasso.Callback.EmptyCallback;

// H i b m o b t e c h P i c a s s o G a l l e r y A c t i v i t y
//
// Activité qui permet l'affichage des différents répertoires du téléphone
// contenant des photos. On y accède depuis PicassoThumbnailMozaiqueGridViewActivity
// en cliquant sur les 3 "..." en haut à droite de l'activité
//
// ---------------------------------------------------------------------------------------------


public class PicassoGalleryActivity extends HibmobtechOptionsMenuActivity {
    protected static final String TAG = PicassoGalleryActivity.class.getSimpleName();

    protected ImageView imageView;
    protected String imageString;
    protected String datetime;
    protected MroRequest mroRequestCurrent;
    protected PictureUtils pictureUtils;
    protected DialogUtils dialogUtils;
    protected AlertDialog alertDialog;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        dialogUtils = new DialogUtils();
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        pictureUtils = new PictureUtils(this);
        setContentView(R.layout.hibmobtech_picasso_gallery_activity);
        initToolBar();
        initDrawer();
        imageView = (ImageView) findViewById(R.id.hibmobtechPicassoPhotoImageView);
        Intent gallery = new Intent(ACTION_PICK, EXTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICASSO_GALLERY_REQUEST);
    }

    @Override protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            Picasso.with(this).cancelRequest(imageView);
        }
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.mainMenuGridViewPictureItem).setVisible(true);
        menu.findItem(R.id.mainMenuGalleryPictureItem).setVisible(false);
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

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult");
        if (requestCode == PICASSO_GALLERY_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageURI = data.getData();
            String realPathFromURI = getRealPathFromURI(selectedImageURI);
            imageString = data.getData().toString();

            // get last modification date of file picture
            Log.d(TAG, "onActivityResult: imageString=" + imageString + " realPathFromURI=" + realPathFromURI);
            loadSaveAndPostImage(realPathFromURI);
        } else {
            finish();
        }
    }

    public AlertDialog createAlertDialog(String message, final String realPathFromURI) {
        Log.d(TAG, "createAlertDialog");

        return alertDialog = dialogUtils.createAlertDialogOKCancel(message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Log.d(TAG, "createAlertDialog: onOKListener");
                        finish();

                        SaveAndPostPictureInBackground saveAndPostPictureInBackground
                                = new SaveAndPostPictureInBackground(realPathFromURI);
                        saveAndPostPictureInBackground.execute();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Log.d(TAG, "createAlertDialog: onCancelListener");
                    }
                });
    }

    // c l a s s   Sa v e A n d P o s t P i c t u r e I n B a c k g r o u n d
    // ----------------------------------------------------------------------
    private class SaveAndPostPictureInBackground extends AsyncTask<Void, Integer, Void>
    {
        String realPathFromURI;

        public SaveAndPostPictureInBackground(String realPathFromURI) {
            this.realPathFromURI = realPathFromURI;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                saveAndPostPicture(realPathFromURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void loadSaveAndPostImage(final String realPathFromURI) {
        Log.d(TAG, "loadSaveAndPostImage");
        Picasso.with(this).load(imageString).fit().centerInside().into(imageView, new EmptyCallback() {
            @Override public void onSuccess() {
                Log.d(TAG, "loadSaveAndPostImage: onSuccess");
                mainMenuCheckItem.setVisible(true);
                mainMenuCheckItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String message = "Ajouter cette photo à l'intervention ?";
                        alertDialog = createAlertDialog(message, realPathFromURI);
                        if (!alertDialog.isShowing()) {
                            Log.d(TAG, "getView: affichage de la boîte de dialogue");
                            if(!PicassoGalleryActivity.this.isFinishing())
                                alertDialog.show();
                        }
                        return true;
                    }
                });
            }
        });
    }

    void saveAndPostPicture(String imageCurrentHdpiPath) throws IOException {
        Log.d(TAG, "saveAndPostPicture");
        Picture picture = new Picture();

        // set new size of image
        BitmapFactory.Options optionsImage = new BitmapFactory.Options();
        optionsImage.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageCurrentHdpiPath, optionsImage);
        int heightImage = optionsImage.outHeight;
        int widthImage = optionsImage.outWidth;
        int newHeightImage = PICTURE_HEIGHT;
        int newWidthImage = (PICTURE_HEIGHT * widthImage) / heightImage;
        optionsImage.inJustDecodeBounds = false;
        Bitmap bitmapImage = BitmapFactory.decodeFile(imageCurrentHdpiPath);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmapImage, newWidthImage, newHeightImage, false);

        // compress image & create imageTypedFile for downloading to server
        byte[] imageByteArray = pictureUtils.compressBitmapToByteArray(scaledBitmap, PICTURE_WEIGHT, 80);
        Bitmap compressBitmap = pictureUtils.convertByteArrayToBitmap(imageByteArray);
        File imageFile = pictureUtils.convertBitmapToFile(compressBitmap);
        TypedFile imageTypedFile = new TypedFile("image/jpeg", imageFile);

        //  set image Picture
        picture.setImage(pictureUtils.convertBitmapToByteArray(compressBitmap));

        // set Thumbnail to save in database
        Bitmap bitmapThumbnail = ThumbnailUtils.extractThumbnail(PictureUtils.convertImagePathToBitmap(imageCurrentHdpiPath)
                , THUMBNAIL_HEIGHT, THUMBNAIL_HEIGHT);
        byte[] thumbnailByteArray = pictureUtils.compressBitmapToByteArray(bitmapThumbnail, 100);
        picture.setThumbnail(thumbnailByteArray);

        // set idMroRequest
        if(mroRequestCurrent != null) picture.setIdMroRequest(mroRequestCurrent.getId());

        // set infos
        PictureInfos pictureInfos = new  PictureInfos();
        File file = new File(imageCurrentHdpiPath);
        if(file.exists()){
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("'Photo prise le' dd/MM/yyyy 'à' HH:mm:ss");
            ExifInterface exifInterface = new ExifInterface(imageCurrentHdpiPath);
            if(exifInterface != null){
                String dateString = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);
                if(dateString != null) {
                    DateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.FRENCH);
                    Date date = null;
                    try {
                        date = format.parse(dateString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    pictureInfos.setTitle(simpleDateFormat.format(date));
                }
                else{
                    Date date = new Date(file.lastModified());
                    pictureInfos.setTime(date.getTime());
                    pictureInfos.setTitle(simpleDateFormat.format(date));
                    Log.d(TAG, "getView: pas de données exif: date lastModified file=" + simpleDateFormat.format(date));
                }
            }
            else{
                Date date = new Date(file.lastModified());
                pictureInfos.setTime(date.getTime());
                pictureInfos.setTitle(simpleDateFormat.format(date));
                Log.d(TAG, "getView: pas de données exif: date lastModified file=" + simpleDateFormat.format(date));
            }
        }
        picture.setInfos(pictureInfos);

        // insert picture in database
        long pictureId = insertPictureDataBase(HibmobtechContentProvider.urlForPicture(), picture);
        picture.setId(pictureId);

        // post picture to server
        postPictureOld(imageTypedFile, picture);
        finish();
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
                new retrofit.Callback<Picture>() {
                    @Override
                    public void success(Picture pictureResponse, Response response) {
/*                        if(!PicassoGalleryActivity.this.isFinishing())
                            Toast.makeText(PicassoGalleryActivity.this,
                                "Photo transmise au serveur avec succès",
                                Toast.LENGTH_SHORT).show();*/

                        if(pictureResponse != null) {
                            long initialPictureId = picture.getId();
                            long pictureResponseId = pictureResponse.getId();
                            Log.e(TAG, "postPictureOld:success: pictureResponseId=" + pictureResponseId);
                            picture.setId(pictureResponseId);
                            // update picture ID in database
                            try {
                                //updatePictureByIdDataBase(HibmobtechContentProvider.urlForPicture(0), picture);
                                updatePictureByIdDataBase(HibmobtechContentProvider.urlForPicture(initialPictureId), picture);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if(!PicassoGalleryActivity.this.isFinishing())
                            Toast.makeText(PicassoGalleryActivity.this,
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

/*    public void insertPictureDataBase(Uri url, Picture picture) throws IOException {
        Log.d(TAG, "insertPictureDataBase");
        ContentValues[] contentValuesArray = new ContentValues[1];
        ContentValues contentValues = new ContentValues();
        contentValues.put(SQLiteBaseDaoFactory.COL_DESCRIPTION, picture.getInfos().getTitle());
        contentValues.put(SQLiteBaseDaoFactory.COL_TIME, picture.getInfos().getTime());
        contentValues.put(SQLiteBaseDaoFactory.COL_THUMBNAIL, picture.getThumbnail());
        contentValues.put(SQLiteBaseDaoFactory.COL_IMAGE, picture.getImage());
        contentValues.put(SQLiteBaseDaoFactory.COL_ID_MRO_REQUEST, picture.getIdMroRequest());
        contentValues.put(SQLiteBaseDaoFactory.COL_TITLE, picture.getInfos().getTitle());
        contentValuesArray[0] = contentValues;
        getContentResolver().bulkInsert(url, contentValuesArray);
        getContentResolver().notifyChange(url, hibmobtechContentObserver);
    }*/

    public long insertPictureDataBase(Uri url, Picture picture) throws IOException {
        Log.e(TAG, "insertPictureDataBase: url=" + url + " picture.getId()=" + picture.getId());

        ContentValues contentValues = new ContentValues();
        //ontentValues.put(COL_DESCRIPTION, picture.getInfos().getTitle());
        contentValues.put(COL_TIME, picture.getInfos().getTime());
        contentValues.put(COL_THUMBNAIL, picture.getThumbnail());
        contentValues.put(COL_IMAGE, picture.getImage());
        contentValues.put(COL_ID_MRO_REQUEST, picture.getIdMroRequest());
        contentValues.put(COL_TITLE, picture.getInfos().getTitle());
        Uri uri = getContentResolver().insert(url, contentValues);

        // URL of the newly created row
        Log.e(TAG, "insertPictureDataBase: URL of the newly created row" + uri);

        String id = uri.getLastPathSegment();
        Log.e(TAG, "update: id=" + id );

        getContentResolver().notifyChange(url, hibmobtechContentObserver);
        return Long.valueOf(id).longValue();
    }

    public void updatePictureByIdDataBase(Uri url, Picture picture) throws IOException {
        Log.e(TAG, "updatePictureDataBase: url=" + url + " picture.getId()=" + picture.getId());
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, picture.getId());
        //contentValues.put(COL_DESCRIPTION, picture.getInfos().getTitle());
        contentValues.put(COL_TIME, picture.getInfos().getTime());
        contentValues.put(COL_THUMBNAIL, picture.getThumbnail());
        contentValues.put(COL_IMAGE, picture.getImage());
        contentValues.put(COL_ID_MRO_REQUEST, picture.getIdMroRequest());
        contentValues.put(COL_TITLE, picture.getInfos().getTitle());
        int numberOfRowsUpdated = getContentResolver().update(url, contentValues, COL_ID + "=?", new String[] {String.valueOf(picture.getId())});

        // URL of the newly updated row
        Log.e(TAG, "updatePictureByIdDataBase: number of rows updated = " + numberOfRowsUpdated);

        getContentResolver().notifyChange(url, hibmobtechContentObserver);
    }
}
