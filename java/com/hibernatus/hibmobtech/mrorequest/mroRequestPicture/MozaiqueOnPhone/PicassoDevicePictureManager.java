package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.MozaiqueOnPhone;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.Date;
import java.util.Vector;

/**
 * Created by Eric on 20/02/2017.
 */

// H i b m o b t e c h P i c a s s o D e v i c e P i c t u r e M a n a g e r
//
// Activité permettant de récupérer les pictures stockées sur le téléphone
// utilisé par PicassoThumbnailMozaiqueGridViewAdapter
//
// ------------------------------------------------------------------------------
public class PicassoDevicePictureManager {
    public static final String TAG = PicassoDevicePictureManager.class.getSimpleName();

    Vector<PicassoPhoneAlbum> picassoPhoneAlbumVector;
    Vector<String> albumsNames;

    public void getPhoneAlbums(Context context , PicassoOnPhonePictureObtained listener ){
        Log.d(TAG, "getPhoneAlbums");

        // Creating vectors to hold the final albums objects and albums names
        picassoPhoneAlbumVector = new Vector<>();
        albumsNames = new Vector<>();

        // which image properties are we querying
        String[] projection = new String[] {
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID
        };

        // content: style URI for the "primary" external storage volume
        Uri images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        // Make the query.
        Cursor cursor = context.getContentResolver().query(images,
                projection, // Which columns to return
                null,       // Which rows to return (all rows)
                null,       // Selection arguments (none)
                null        // Ordering
        );

        if ( cursor != null && cursor.getCount() > 0 ) {
            Log.d(TAG, "getPhoneAlbums: query count=" + cursor.getCount());

            if (cursor.moveToFirst()) {
                String bucketName;
                String data;
                String imageId;
                int bucketNameColumn = cursor.getColumnIndex(
                        MediaStore.Images.Media.BUCKET_DISPLAY_NAME);

                int imageUriColumn = cursor.getColumnIndex(
                        MediaStore.Images.Media.DATA);

                int imageIdColumn = cursor.getColumnIndex(
                        MediaStore.Images.Media._ID );

                do {
                    // Get the field values
                    bucketName = cursor.getString( bucketNameColumn );
                    data = cursor.getString( imageUriColumn );
                    imageId = cursor.getString( imageIdColumn );

                    // Adding a new PhonePhoto object to phonePhotos vector
                    PicassoPhonePicture picassoPhonePicture = new PicassoPhonePicture();
                    picassoPhonePicture.setAlbumName( bucketName );
                    picassoPhonePicture.setPhotoUri( data );
                    //String imageString = data.getData().toString();

                    File file = new File(data);
                    Date lastModDate = new Date(file.lastModified());

/*                    ExifInterface exif = null;
                    try {
                        exif = new ExifInterface(data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String datetime =  exif.getAttribute(ExifInterface.TAG_DATETIME);*/

                    picassoPhonePicture.setTimeStamp(lastModDate.toString());

                    picassoPhonePicture.setId( Integer.valueOf( imageId ) );

                    if ( albumsNames.contains( bucketName ) ) {
                        for ( PicassoPhoneAlbum album : picassoPhoneAlbumVector) {
                            if ( album.getName().equals( bucketName ) ) {
                                album.getAlbumPhotos().add(picassoPhonePicture);
                                Log.d(TAG, "getPhoneAlbums: A photo was added to album => " + bucketName
                                        + " Uri=" + picassoPhonePicture.getPhotoUri());
                                break;
                            }
                        }
                    } else {
                        PicassoPhoneAlbum album = new PicassoPhoneAlbum();
                        Log.d(TAG, "getPhoneAlbums: A new album was created => " + bucketName );
                        album.setId( picassoPhonePicture.getId() );
                        album.setName( bucketName );
                        album.setCoverUri( picassoPhonePicture.getPhotoUri() );
                        album.getAlbumPhotos().add(picassoPhonePicture);
                        Log.d(TAG, "getPhoneAlbums: A photo was added to album => " + bucketName
                                + " Uri=" + picassoPhonePicture.getPhotoUri());

                        picassoPhoneAlbumVector.add( album );
                        albumsNames.add( bucketName );
                    }

                } while (cursor.moveToNext());
            }

            cursor.close();
            listener.onComplete(picassoPhoneAlbumVector);
        } else {
            listener.onError();
        }
    }
}
