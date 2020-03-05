package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.MozaiqueOnPhone;

/**
 * Created by Eric on 20/02/2017.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hibernatus.hibmobtech.HibmobtechApplication;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static android.widget.ImageView.ScaleType.CENTER_CROP;
import static com.hibernatus.hibmobtech.HibmobtechActivity.hibmobtechContentObserver;
import static com.hibernatus.hibmobtech.HibmobtechApplication.PICASSO_PICTURE_HEIGHT;
import static com.hibernatus.hibmobtech.HibmobtechApplication.PICASSO_PICTURE_WIDTH;
import static com.hibernatus.hibmobtech.HibmobtechApplication.PICTURE_HEIGHT;
import static com.hibernatus.hibmobtech.HibmobtechApplication.PICTURE_WEIGHT;
import static com.hibernatus.hibmobtech.HibmobtechApplication.THUMBNAIL_HEIGHT;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID_MRO_REQUEST;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_IMAGE;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_THUMBNAIL;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_TIME;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_TITLE;

final class PicassoThumbnailMozaiqueGridViewAdapter extends BaseAdapter {
    protected static final String TAG = PicassoThumbnailMozaiqueGridViewAdapter.class.getSimpleName();

    protected final Context context;
    protected final List<PicassoPhonePicture> phonePhotoArrayList = new ArrayList<>();

    protected int size = (int) Math.ceil(Math.sqrt(PICASSO_PICTURE_WIDTH * PICASSO_PICTURE_HEIGHT));
    protected MroRequest mroRequestCurrent;
    protected PictureUtils pictureUtils;
    protected DialogUtils dialogUtils;
    protected AlertDialog alertDialog;

    public PicassoThumbnailMozaiqueGridViewAdapter(final Context context) {
        Log.d(TAG, "PicassoThumbnailMozaiqueGridViewAdapter");
        this.context = context;
        dialogUtils = new DialogUtils();
        mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        pictureUtils = new PictureUtils(context);

        PicassoDevicePictureManager picassoDevicePictureManager = new PicassoDevicePictureManager();
        picassoDevicePictureManager.getPhoneAlbums(context, new PicassoOnPhonePictureObtained() {
            @Override
            public void onComplete(Vector<PicassoPhoneAlbum> albums) {
                Log.d(TAG, "onComplete");

                for(PicassoPhoneAlbum picassoPhoneAlbum : albums) {
                    Log.d(TAG, "getPhoneAlbums: phoneAlbum.getName()=" +  picassoPhoneAlbum.getName());
                    Vector<PicassoPhonePicture> albumPhoto = picassoPhoneAlbum.getAlbumPhotos();
                    for(PicassoPhonePicture picassoPhonePicture : albumPhoto) {
                        Collections.addAll(phonePhotoArrayList, picassoPhonePicture);
                    }
                }

                Collections.sort(phonePhotoArrayList);

            }

            @Override
            public void onError() {
                Log.d(TAG, "getPhoneAlbums ERROR");
            }
        });
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        SquaredPictureView view = (SquaredPictureView) convertView;
        if (view == null) {
            view = new SquaredPictureView(context);
            view.setScaleType(CENTER_CROP);
        }

        // Get the image URL for the current position.
        final String pictureUrlString = getItem(position);
        Log.e(TAG, "getView: size=" + size + " realPathFromURI=" + pictureUrlString);

        File f = new File(pictureUrlString);
/*        Picasso.Builder builder = new Picasso.Builder(parent.getContext());
        builder.listener(new Picasso.Listener()
        {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception)
            {
                Log.e(TAG, "onImageLoadFailed: uri=" + uri
                        + " exception=" + exception);
                exception.printStackTrace();
            }
        });*/
/*        builder.build()
                .load(f)
                .transform(new BitmapTransform(PICASSO_PICTURE_WIDTH, PICASSO_PICTURE_HEIGHT))
                .skipMemoryCache()
                .resize(size, size)
                .placeholder(R.drawable.progress_animation)
                .error(R.mipmap.ic_circle_error)
                .tag(context)
                .into(view);*/

/*        builder.build()
                .load(f)
                .transform(new BitmapTransform(PICASSO_PICTURE_WIDTH, PICASSO_PICTURE_HEIGHT))
                .resize(size, size)
                .placeholder(R.drawable.progress_animation)
                .error(R.mipmap.ic_circle_error)
                .tag(context)
                .into(view);*/

        Picasso.with(context)
                .load(f)
                .transform(new BitmapTransform(PICASSO_PICTURE_WIDTH, PICASSO_PICTURE_HEIGHT))
                .resize(size, size)
                .placeholder(R.drawable.progress_animation)
                .error(R.mipmap.ic_circle_error)
                .tag(context)
                .into(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "getView: onClick");
                String message = "Ajouter cette photo à l'intervention ?";
                if (context != null) {
                    alertDialog = createAlertDialog(message, pictureUrlString);
                    if (!alertDialog.isShowing()) {
                        Log.d(TAG, "getView: affichage de la boîte de dialogue");
                        if(!((Activity) context).isFinishing())
                            alertDialog.show();
                    }
                }
            }
        });
        return view;
    }

    public AlertDialog createAlertDialog(String message, final String pictureUrlString) {
        Log.d(TAG, "createAlertDialog");

        return alertDialog = dialogUtils.createAlertDialogOKCancel(message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Log.d(TAG, "createAlertDialog: onOKListener");
                        ((Activity)context).finish();

                        SaveAndPostPictureInBackground saveAndPostPictureInBackground = new SaveAndPostPictureInBackground(pictureUrlString);
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

    // c l a s s   S a v e A n d P o s t P i c t u r e I n B a c k g r o u n d
    // -----------------------------------------------------------------------
    private class SaveAndPostPictureInBackground extends AsyncTask<Void, Integer, Void>
    {
        String pictureUrlString;

        public SaveAndPostPictureInBackground(String pictureUrlString) {
            this.pictureUrlString = pictureUrlString;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... values){
            super.onProgressUpdate(values);
            // Mise à jour de la ProgressBar
            // mProgressBar.setProgress(values[0]);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                saveAndPostCurrentPicture(pictureUrlString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            //Toast.makeText(context, "Le traitement asynchrone est terminé", Toast.LENGTH_LONG).show();
        }
    }

    void saveAndPostCurrentPicture(String imageCurrentHdpiPath) throws IOException {
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
        if(bitmapImage == null)return;
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

                        if(pictureResponse != null) {
                            long initialPictureId = picture.getId();
                            long pictureResponseId = pictureResponse.getId();
                            Log.e(TAG, "postPictureOld:success: pictureResponseId=" + pictureResponseId);
                            picture.setId(pictureResponseId);
                            // update picture ID in database
                            try {
                                updatePictureByIdDataBase(HibmobtechContentProvider.urlForPicture(initialPictureId), picture);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        ((Activity)context).finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        try {
                            picture.setResendNumber(1);
                            insertPictureDataBase(HibmobtechContentProvider.urlForPictureCache(), picture);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public long insertPictureDataBase(Uri url, Picture picture) throws IOException {
        Log.e(TAG, "insertPictureDataBase: url=" + url + " picture.getId()=" + picture.getId());

        ContentValues contentValues = new ContentValues();
        //contentValues.put(COL_DESCRIPTION, picture.getInfos().getTitle());
        contentValues.put(COL_TIME, picture.getInfos().getTime());
        contentValues.put(COL_THUMBNAIL, picture.getThumbnail());
        contentValues.put(COL_IMAGE, picture.getImage());
        contentValues.put(COL_ID_MRO_REQUEST, picture.getIdMroRequest());
        contentValues.put(COL_TITLE, picture.getInfos().getTitle());
        Uri uri = context.getContentResolver().insert(url, contentValues);

        // URL of the newly created row
        Log.e(TAG, "insertPictureDataBase: URL of the newly created row" + uri);

        String id = uri.getLastPathSegment();
        Log.e(TAG, "update: id=" + id );

        context.getContentResolver().notifyChange(url, hibmobtechContentObserver);
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
        int numberOfRowsUpdated = context.getContentResolver().update(url, contentValues, COL_ID + "=?", new String[] {String.valueOf(picture.getId())});

        // URL of the newly updated row
        Log.e(TAG, "updatePictureByIdDataBase: number of rows updated = " + numberOfRowsUpdated);

        context.getContentResolver().notifyChange(url, hibmobtechContentObserver);
    }

    @Override
    public int getCount() {
        return phonePhotoArrayList.size();
    }

    @Override
    public String getItem(int position) {
        Log.d(TAG, "getItem"
                + " phonePhotoArrayList.get(position).getId() =" + phonePhotoArrayList.get(position).getId()
                + " phonePhotoArrayList.get(position).getTimeStamp()=" + phonePhotoArrayList.get(position).getTimeStamp());

        return phonePhotoArrayList.get(position).getPhotoUri();
    }
    @Override public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static boolean isAirplaneModeOn(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return Settings.System.getInt(context.getContentResolver(),
                    Settings.System.AIRPLANE_MODE_ON, 0) != 0;
        } else {
            return Settings.Global.getInt(context.getContentResolver(),
                    Settings.Global.AIRPLANE_MODE_ON, 0) != 0;
        }
    }

}

