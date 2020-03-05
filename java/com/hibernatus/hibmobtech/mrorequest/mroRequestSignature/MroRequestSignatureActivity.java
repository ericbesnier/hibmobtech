package com.hibernatus.hibmobtech.mrorequest.mroRequestSignature;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.HibmobtechOptionsMenuActivity;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.contentprovider.HibmobtechContentProvider;
import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoSignature;
import com.hibernatus.hibmobtech.model.ClientSignature;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.model.SignatureInfos;
import com.hibernatus.hibmobtech.mrorequest.MroRequestApiService;
import com.hibernatus.hibmobtech.mrorequest.MroRequestCurrent;
import com.hibernatus.hibmobtech.mrorequest.mroRequestPicture.PictureUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static com.hibernatus.hibmobtech.HibmobtechApplication.SIGNATURE_WEIGHT;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_ID_MRO_REQUEST;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_RESEND_NUMBER;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_SIGNATURE_FILE;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_SIGNER_NAME;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_SIGNER_ROLE;
import static com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoFactory.COL_TIMESTAMP;

//import static com.hibernatus.hibmobtech.HibmobtechApplication.ACTION_NEW_SIGNATURE;

/**
 * Created by Eric on 09/01/2016.
 */

// M r o S i g n a t u r e A c t i v i t y
// ---------------------------------------
public class MroRequestSignatureActivity extends HibmobtechOptionsMenuActivity {
    //public class MroRequestSignatureActivity extends HibmobtechOptionsMenuActivity implements ActivityRefresher{

    // s t a t i c
    // -----------
    public static final String TAG = MroRequestSignatureActivity.class.getSimpleName();
    public static final String ACTIVITY_FILTER = MroRequestSignatureActivity.class.getName();

    /*
    * Hint to the compressor, 0-100. 0 meaning compress for
    * small size, 100 meaning compress for max quality. Some
    * formats, like PNG which is lossless, will ignore the
    * quality setting
    */
    private static final int QUALITY = 30;
    private static final int RESIZING_REPORT = 2;

    // M e m b e r s
    // -------------

    private PictureUtils pictureUtils;
    private SignaturePad signaturePad;
    private Button clearButton;
    private Button saveButton;
    private EditText mroSignatureActivityLinearLayoutNameEditText;
    private File signatureFile;
    private ImageView signatureImageView;
    private String albumName;
    private String signerName;
    private boolean isNamed = false;
    private SQLiteBaseDaoSignature SQLiteBaseDaoSignature;
    private MroRequest mroRequestCurrent;


    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        this.mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
        pictureUtils = new PictureUtils(getApplicationContext());
        SQLiteBaseDaoSignature = new SQLiteBaseDaoSignature(this);
        setContentView(R.layout.mro_request_signature_activity);
        initToolBar();
        signaturePad = (SignaturePad) findViewById(R.id.signature_pad);
        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onSigned() {
                if (isNamed) {
                    saveButton.setEnabled(true);
                    clearButton.setEnabled(true);
                } else {
                    saveButton.setEnabled(false);
                    clearButton.setEnabled(true);
                }
            }

            @Override
            public void onClear() {
                saveButton.setEnabled(false);
                clearButton.setEnabled(false);
            }
        });

        clearButton = (Button) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signaturePad.clear();
            }
        });

        saveButton = (Button) findViewById(R.id.save_button);
        setSaveButtonOnClickListener();

        mroSignatureActivityLinearLayoutNameEditText = (EditText) findViewById(
                R.id.mroSignatureActivityLinearLayoutNameEditText);
        mroSignatureActivityLinearLayoutNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                signerName = mroSignatureActivityLinearLayoutNameEditText.getText().toString();
                Log.d(TAG, "onCreate: signerName=" + signerName);
                if (signerName.isEmpty()) {
                    isNamed = false;
                } else {
                    isNamed = true;
                }
                if (!signerName.isEmpty() && !signaturePad.isEmpty()) {
                    saveButton.setEnabled(true);
                } else {
                    saveButton.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        });

        Activity currentActivity = ((HibmobtechApplication) this.getApplicationContext()).getCurrentActivity();
        Log.i(TAG, "onCreate: currentActivity=" + currentActivity);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onResume() {
        super.onResume();
        trackScreen(TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        signatureFile = null;
        boolean resultIfExists = false;
        if (isGrantedWriteExternalStorage() && albumName != null) {
            try {
                // Get the directory for the user's public pictures directory.
                signatureFile = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), albumName);
                resultIfExists = signatureFile.mkdirs();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "getAlbumStorageDir: catch (Exception e)");
            }
            if (!resultIfExists) {
                Log.d(TAG, "getAlbumStorageDir: Directory " + signatureFile.getAbsolutePath() + " not created");
            }
        }
    }

/*    @Override
    public void refresh() {
        if(signatureFile != null && signerName != null) {
            TypedFile signatureTypedFile = new TypedFile("multipart/form-data", signatureFile);
            postSignature(signatureTypedFile, signerName);
        }
    }*/

    // O t h e r s   m e t h o d s
    // ---------------------------
    void setSaveButtonOnClickListener() {
        saveButton.setOnClickListener(
                new View.OnClickListener()
                {
                    @Override
                    public void onClick (View view) {
                        Bitmap signatureBitmap = signaturePad.getSignatureBitmap();

                        byte[] compressedSignatureByteArray = pictureUtils.compressBitmapToByteArray(signatureBitmap, 100);
                        ClientSignature clientSignature = null;
                        if (addSignatureToGallery(signatureBitmap)) {

                            // resize signature
                            signatureBitmap = Bitmap.createScaledBitmap(signatureBitmap,
                                    signatureBitmap.getWidth() / RESIZING_REPORT,
                                    signatureBitmap.getHeight() / RESIZING_REPORT, true);

                            // compress signature
                            File imageFile = pictureUtils.convertBitmapToFile(signatureBitmap);
                            File compressedImageFile = pictureUtils.compressImageFileToImageFile(imageFile,
                                    SIGNATURE_WEIGHT);
                            TypedFile imageTypedFile = new TypedFile("image/jpeg", compressedImageFile);

                            //
                            SignatureInfos signatureInfos = new SignatureInfos();
                            signatureInfos.setSignerName(signerName);
                            signatureInfos.setSignerRole("Le client");
                            Date date = new Date();
                            signatureInfos.setTimestamp(date);

                            // / Set signature bean
                            clientSignature = new ClientSignature();
                            clientSignature.setIdMroRequest(mroRequestCurrent.getId());
                            clientSignature.setSignatureFile(compressedSignatureByteArray);
                            clientSignature.setInfos(signatureInfos);

                            // post signature
                            postSignature(imageTypedFile, signerName, clientSignature);
                            launchReturnToDetailActivity();
                        } else {
                            if(!MroRequestSignatureActivity.this.isFinishing())
                                Toast.makeText(MroRequestSignatureActivity.this,
                                        "Echec d'enregistrement de la photo dans la galerie", Toast.LENGTH_SHORT).show();
                        }

                        // insert picture in database
                        insertSignatureDataBase(HibmobtechContentProvider.urlForSignature(), clientSignature);
                    }
                }
        );
    }

    public void insertSignatureDataBase(Uri url, ClientSignature clientSignature) {
        Log.d(TAG, "insertSignatureDataBase");
        ContentValues[] contentValuesArray = new ContentValues[1];
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID_MRO_REQUEST, clientSignature.getIdMroRequest());
        contentValues.put(COL_SIGNATURE_FILE, clientSignature.getSignatureFile());
        contentValues.put(COL_SIGNER_NAME, clientSignature.getInfos().getSignerName());
        contentValues.put(COL_SIGNER_ROLE, clientSignature.getInfos().getSignerRole());
        contentValues.put(COL_TIMESTAMP, clientSignature.getInfos().getTimestamp().toString());
        contentValues.put(COL_RESEND_NUMBER, clientSignature.getResendNumber());
        contentValuesArray[0] = contentValues;
        getContentResolver().bulkInsert(url, contentValuesArray);
        getContentResolver().notifyChange(url, hibmobtechContentObserver);
    }

    synchronized public void postSignature(TypedFile signatureTypedFile, String signerName, final ClientSignature clientSignature) {
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        Log.d(TAG, "postSignature");
        TypedString signerNameString = null;
        try {
            signerNameString = new TypedString(new String (signerName.getBytes ("UTF-8"), "iso-8859-1"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        service.uploadSignatureOld(
                mroRequestCurrent.getId(),
                signerNameString,
                signatureTypedFile,
                new Callback<Void>() {
                    @Override
                    public void success(Void aVoid, Response response) {
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        clientSignature.setResendNumber(1);
                        // insert picture in database in table SignatureCache to resend periodically
                        insertSignatureDataBase(HibmobtechContentProvider.urlForSignatureCache(), clientSignature);
                        setRetrofitError(error);
                    }
                });
    }

    public File getAlbumStorageDir(String albumName) {
        Log.d(TAG, "getAlbumStorageDir");

        File file = null;
        this.albumName = albumName;
        if(isExternalStorageAvailableAndWritable()) {
            boolean resultIfExists = false;
            checkWriteExternalStoragePermission();
            if(isGrantedWriteExternalStorage()) {
                try {
                    // Get the directory for the user's public pictures directory.
                    file = new File(Environment.getExternalStoragePublicDirectory (
                            Environment.DIRECTORY_PICTURES), albumName);
                    // Creates the directory named by this file, creating missing parent directories if necessary.
                    resultIfExists = file.mkdirs();
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "getAlbumStorageDir: catch (Exception e)");
                    //HibmobtechApplication.getInstance().trackException(e);
                }
                if (!resultIfExists) {
                    Log.d(TAG, "getAlbumStorageDir: Directory " + file.getAbsolutePath()
                            + " already existing > not created");
                }
            }
        }
        return file;
    }

    void launchReturnToDetailActivity() {
        Log.d(TAG, "launchReturnToDetailActivity: signatureImageView=" + signatureImageView
                +" signatureFile=" + signatureFile);
        MroRequestCurrent.getInstance().setSignatureImageView(signatureImageView);
        MroRequestCurrent.getInstance().setSignatureFile(signatureFile);
        MroRequestCurrent.getInstance().setIsMroRequestCheckable(true);
        finish();
    }

    public boolean isExternalStorageAvailableAndWritable() {
        String state = Environment.getExternalStorageState();
        boolean returnValue;

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            returnValue = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            returnValue = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            returnValue = false;
        }
        return returnValue;
    }

    public boolean addSignatureToGallery(Bitmap signatureBitmap) {
        Log.e(TAG, "addSignatureToGallery: signatureBitmap=" + signatureBitmap);
        boolean result = false;
        try {
            signatureFile = new File(getAlbumStorageDir("signaturePad"),
                    String.format("Signature_%d.jpg", System.currentTimeMillis()));
            saveBitmapToJpeg(signatureBitmap, signatureFile);
            //Intent newSignatureIntent = new Intent(ACTION_NEW_SIGNATURE);
/*            Uri contentUri = Uri.fromFile(signatureFile);
            newSignatureIntent.setData(contentUri);
            Log.e(TAG, "addSignatureToGallery: sendBroadcast uri="
            + Uri.fromFile(signatureFile) + " intent=" + ACTION_NEW_SIGNATURE);*/
            //Log.e(TAG, "addSignatureToGallery: sendBroadcast");

            //sendBroadcast(newSignatureIntent);

            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "addSignatureToGallery: IOException=" + e);
        }
        return result;
    }

    public void saveBitmapToJpeg(Bitmap bitmap, File photoFile) throws IOException {
        Log.d(TAG, "saveBitmapToJpeg");
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        signatureImageView = new ImageView(getApplicationContext());
        signatureImageView.setImageDrawable(new BitmapDrawable(getResources(), newBitmap));
        OutputStream stream = new FileOutputStream(photoFile);
        newBitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, stream);
        stream.close();
    }
}
