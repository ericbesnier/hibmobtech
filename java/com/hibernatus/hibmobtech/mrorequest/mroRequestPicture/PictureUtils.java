package com.hibernatus.hibmobtech.mrorequest.mroRequestPicture;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.graphics.BitmapCompat;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.model.MroRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit.mime.TypedFile;

import static com.hibernatus.hibmobtech.HibmobtechApplication.DirQualityPath.HDPI;

// c l a s s   P i c t u r e U t i l s
// -----------------------------------

public class PictureUtils {

    // s t a t i c
    // -----------
    public static final String TAG = PictureUtils.class.getSimpleName();

    // M e m b e r s
    // -------------
    private Context context;
    private Long mroRequestId;

    // C o n s t r u c t o r s
    // -----------------------
    public PictureUtils() {
    }

    public PictureUtils(Context context) {
        this.context = context;
    }

    public PictureUtils(Context context, Long mroRequestId) {
        this.context = context;
        this.mroRequestId = mroRequestId;
    }

    // R e s i z i n g   M e t h o d s
    // -------------------------------

    public Bitmap resizeBitmapToBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();
        return resizedBitmap;
    }


    /*
     * usage : mImageView.setImageBitmap(resizeFileToBitmap(pictureHdpiPath, 100, 100));
     * *
     * This method makes it easy to load a bitmap of arbitrarily large size into an ImageView
     * that displays a 100x100 pixel thumbnail
     */
    public static Bitmap resizedImagePathToBitmap(String imagePath, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int imageHeight = options.outHeight;
        int imageWidth = options.outWidth;

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        Log.d(TAG, "resizedImagePathToBitmap: imagePath=" + imagePath + " imageHeight=" + imageHeight
                + " imageWidth=" + imageWidth + " inSampleSize=" + options.inSampleSize);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(imagePath, options);
    }


    // c o m p r e s s i n g   m e t h o d s
    // -------------------------------------

    public File compressImageFileToImageFile(File imageFile, int sizeMax) {
        Log.d(TAG, "compressImageFileToImageFile: AVANT COMPRESSION: imageFile.length()="
                + imageFile.length() + " bytes (" + imageFile.length() / 1024 + " Ko)");
        Bitmap bitmap = convertImageFileToBitmap(imageFile);
        int compress = 100;
        while (imageFile.length() > sizeMax) {
            byte[] byteArray = compressBitmapToByteArray(bitmap, compress);
            imageFile = convertByteArrayToFile(byteArray);
            compress--;
        }

        Log.d(TAG, "compressImageFileToImageFile: APRES COMPRESSION: imageFile.length()="
                + imageFile.length() + " bytes (" + imageFile.length() / 1024 + " Ko)");
        return imageFile;
    }

    // compress a bitmap to a size max with a initial state of compression
    // return a byte[] compressed
    public byte[] compressBitmapToByteArray(Bitmap bitmap, int sizeMax, int initialCompress) {
        byte[] imageByteArray = convertBitmapToByteArray(bitmap);
        int compress = initialCompress;
        while (imageByteArray.length > sizeMax) {
            imageByteArray = compressBitmapToByteArray(bitmap, compress);
            compress--;
        }
        Log.e(TAG, "compressBitmapToByteArray: compress=" + compress + " imageByteArray.length="
                + imageByteArray.length + " bytes (" + imageByteArray.length / 1024 + " Ko)");
        return imageByteArray;
    }

    public byte[] compressBitmapToByteArray(Bitmap bitmap, int compress) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compress, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public File compressBitmapToFile(Bitmap bitmap, int compress) {
        // create a file to write bitmap data
        File f = new File(context.getCacheDir(), "tmp");
        try {
            f.createNewFile();
            //Convert bitmap to byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, compress, bos);
            byte[] bitmapdata = bos.toByteArray();

            //write the bytes in file
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapdata);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f;
    }

    public Bitmap compressBitmapToBitmap(Bitmap bitmap, int compress) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, compress, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        Bitmap compressBitmap = convertByteArrayToBitmap(byteArray);
        Log.d(TAG, "compressBitmap: compress=" + compress
                + " compressBitmap.getByteCount()=" + compressBitmap.getByteCount()
                + " bytes (" + compressBitmap.getByteCount() / 1024 + " Ko)");
        Log.d(TAG, "compressBitmap: compress=" + compress + " imageByteArray.length="
                + byteArray.length + " bytes (" + byteArray.length / 1024 + " Ko)");
        return compressBitmap;
    }

    private Bitmap compressPathToBitmap(Context context, String path, int imageSizeMax) {

        //Uri uri = getImageUri(path);
        Uri uri = Uri.fromFile(new File(path));
        InputStream in;
        try {
            final int IMAGE_MAX_SIZE = imageSizeMax; // 1200000 = 1.2MP
            in = context.getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();


            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) >
                    IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d(TAG, "scale = " + scale + ", orig-width: " + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = context.getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d(TAG, "1th scale operation dimenions - width: " + width + ", height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;

                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d(TAG, "bitmap size - width: " + b.getWidth() + ", height: " +
                    b.getHeight());
            return b;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
            return null;
        }
    }

    // c o n v e r t i n g   m e t h o d s
    // -----------------------------------
    public Bitmap convertImageFileToBitmap(File imageFile) {
        return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
    }

    // Creating bitmap from string path
    //
    public static Bitmap convertImagePathToBitmap(String imagePath){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        options.inJustDecodeBounds = false;
        options.inDither = false;
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inPreferQualityOverSpeed = true;

        return BitmapFactory.decodeFile(imagePath, options);
    }

    public File convertBitmapToFile(Bitmap bitmap) {
        // create a file to write bitmap data
        File file = new File(context.getCacheDir(), "tmp");
        try {
            file.createNewFile();
            //Convert bitmap to byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] bitmapdata = byteArrayOutputStream.toByteArray();

            //write the bytes in file
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bitmapdata);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "convertBitmapToFile: file.length=" + file.length()
                + " bytes (" + file.length() / 1024 + " Ko)");
        return file;
    }

/*    public TypedFile compressBitmapToTypedFile (Bitmap initialBitmap, int sizeMax, int initialCompress) {
        Bitmap bitmap = initialBitmap;

        byte[] byteArray = new byte[0];
        while (BitmapCompat.getAllocationByteCount(bitmap) > sizeMax) {
            Log.e(TAG, "compressBitmapToTypedFile: BitmapCompat.getAllocationByteCount(bitmap)="
                    + BitmapCompat.getAllocationByteCount(bitmap)
                    + " bytes (" + BitmapCompat.getAllocationByteCount(bitmap) / 1024 + " Ko)");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, initialCompress, byteArrayOutputStream);
            byteArray = byteArrayOutputStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
            initialCompress--;
        }

        File file = new File(context.getCacheDir(), "tmp");
        try {
            file.createNewFile();

            //write the bytes in file
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArray);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "compressBitmapToTypedFile: file.length=" + file.length()
                + " bytes (" + file.length() / 1024 + " Ko)");
        TypedFile typedFile = new TypedFile("image/jpeg", file);
        return typedFile;
    }*/

    public File convertByteArrayToFile(byte[] byteArray) {
        File file = new File(context.getCacheDir(), "tmp");
        try {
            file.createNewFile();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(byteArray);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "convertByteArrayToFile: file.length=" + file.length()
                + " bytes (" + file.length() / 1024 + " Ko)");
        return file;
    }

    public byte[] convertBitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    public Bitmap convertByteArrayToBitmap(byte[] byteArray){
        Log.d(TAG, "convertByteArrayToBitmap: byteArray.length="
                + byteArray.length
                + " bytes (" + byteArray.length / 1024 + " Ko)");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        Log.d(TAG, "convertByteArrayToBitmap: BitmapCompat.getAllocationByteCount(bitmap)="
                + BitmapCompat.getAllocationByteCount(bitmap)
                + " bytes (" + BitmapCompat.getAllocationByteCount(bitmap) / 1024 + " Ko)");
        return bitmap;
    }

    // O t h e r s   m e t h o d s
    // ---------------------------

    public void createPictureDirectories (MroRequest mroRequest){
        createPictureDirectory(mroRequest, HDPI);
    }

    private File createPictureDirectory(MroRequest mroRequest, HibmobtechApplication.DirQualityPath dirQualityPath){
        File mroPictureDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES + "/" + mroRequest.getId() + dirQualityPath);
        if (!mroPictureDir.exists()) {
            if (mroPictureDir.mkdirs()) {
                Log.d(TAG, "Successfully created the dir:" + mroPictureDir.getName());
            } else {
                Log.e(TAG, "Failed to create the dir:" + mroPictureDir.getName());
            }
        }
        return mroPictureDir;
    }

    private boolean IsSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());
        return HibmobtechApplication.FILE_EXTN
                .contains(ext.toLowerCase(Locale.getDefault()));
    }

    public static String getFileName(String filePath) {
        if (filePath == null || filePath.length() == 0)
            return "";
        filePath = filePath.replaceAll("[/\\\\]+", "/");
        int len = filePath.length(),
                upCount = 0;
        while (len > 0) {
            //remove trailing separator
            if (filePath.charAt(len - 1) == '/') {
                len--;
                if (len == 0)
                    return "";
            }
            int lastInd = filePath.lastIndexOf('/', len - 1);
            String fileName = filePath.substring(lastInd + 1, len);
            if (fileName.equals(".")) {
                len--;
            } else if (fileName.equals("..")) {
                len -= 2;
                upCount++;
            } else {
                if (upCount == 0)
                    return fileName;
                upCount--;
                len -= fileName.length();
            }
        }
        return "";
    }

    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }



    public int getScreenWidth() {
        int columnWidth;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }

    public int getScreenHeight() {
        int columnHeight;
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnHeight = point.y;
        return columnHeight;
    }

    public ArrayList<String> getFilePathsFromSDCard() {
        ArrayList<String> filePaths = new ArrayList<String>();

        File directory = new File(
                android.os.Environment.getExternalStorageDirectory()
                        + File.separator + HibmobtechApplication.PHOTO_ALBUM);
        if (directory.isDirectory()) {
            File[] listFiles = directory.listFiles();

            if (listFiles.length > 0) {
                for (int i = 0; i < listFiles.length; i++) {
                    String filePath = listFiles[i].getAbsolutePath();
                    if (IsSupportedFile(filePath)) {
                        // Add image path to array list
                        filePaths.add(filePath);
                    }
                }
            } else {
                if(!((Activity) context).isFinishing())
                    Toast.makeText(
                            context,
                            HibmobtechApplication.PHOTO_ALBUM + " is empty. Please load some images in it !",
                            Toast.LENGTH_LONG).show();
            }
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(context);
            alert.setTitle("Error!");
            alert.setMessage(HibmobtechApplication.PHOTO_ALBUM
                    + " directory path is not valid! Please onUpdatedContent the image directory name java class");
            alert.setPositiveButton("OK", null);
            if(!((Activity) context).isFinishing())
                alert.show();
        }
        return filePaths;
    }

    public boolean equalLists(List<String> a, List<String> b) {
        // Check for sizes and nulls
        if ((a.size() != b.size()) || (a == null && b != null) || (a != null && b == null)) {
            return false;
        }

        if (a == null && b == null) return true;

        // Sort and compare the two lists
        Collections.sort(a);
        Collections.sort(b);
        return a.equals(b);
    }

    // Resizing image size from File, return TypedFile jpeg
    //
    // quality : Hint to the compressor, 0-100. 0 meaning compress for small size, 100 meaning compress for max quality. Some
    // maxSize : horizontal or vertical in pixels
    //
    private TypedFile createRequiredSizePictureTypedFile(File pictureFile, int quality, int maxSize, HibmobtechApplication.DirQualityPath dirQualityPath) {
        int fileSize = Integer.parseInt(String.valueOf(pictureFile.length() / 1024));
        Log.d(TAG, "createRequiredSizePictureTypedFile: fileSize=" + fileSize + " KB" + " maxSize=" + maxSize);

        Bitmap bitmap = BitmapFactory.decodeFile(pictureFile.getAbsolutePath());

        if (bitmap == null)
            return null;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        int newHeight;
        int newWidth;

        // vertical picture
        if (height > width) {
            newHeight = maxSize;
            newWidth = (width * maxSize) / height;
        }
        // horizontal picture
        else {
            newWidth = maxSize;
            newHeight = (height * maxSize) / width;
        }
        bitmap = resizeBitmapToBitmap(bitmap, newWidth, newHeight);

        int bitmapByteCount = BitmapCompat.getAllocationByteCount(bitmap);
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();

        Log.d(TAG, "createRequiredSizePictureTypedFile: bitmapByteCount=" + bitmapByteCount
                + " bitmapWidth=" + bitmapWidth
                + " bitmapHeight=" + bitmapHeight);

        try {
            File mroPictureMdpiDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES + "/" + mroRequestId + dirQualityPath);
            if (!mroPictureMdpiDir.exists()) {
                if (mroPictureMdpiDir.mkdirs()) {
                    Log.i(TAG, "Successfully created the dir:" + mroPictureMdpiDir.getName());
                } else {
                    Log.e(TAG, "Failed to create the dir:" + mroPictureMdpiDir.getName());
                }
            }

            String pictureFileName = pictureFile.getName();
            String completePictureMdpiFileName = mroPictureMdpiDir + "/" + pictureFileName;
            File mroPictureMdpiFile = new File(completePictureMdpiFileName);
            if (!mroPictureMdpiFile.exists()) {
                mroPictureMdpiFile.createNewFile();
            }

            FileOutputStream fileOutputStream = new FileOutputStream(mroPictureMdpiFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
            fileOutputStream.flush();

            int mroPictureMdpiFileSize = Integer.parseInt(String.valueOf(mroPictureMdpiFile.length() / 1024));
            Log.d(TAG, "createRequiredSizePictureTypedFile: mroPictureMdpiFileSize=" + mroPictureMdpiFileSize + " KB");

            return new TypedFile("image/jpeg", mroPictureMdpiFile);
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * helper to retrieve the path of an image URI
     */
    public String getPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    public ArrayList<String> getPicturePathListFromDirectory(Long mroRequestId, HibmobtechApplication.DirQualityPath dirQualityPath) {
        ArrayList<String> picturePathList = new ArrayList<String>();
        String nameDir = String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES
                + "/" + mroRequestId + dirQualityPath));

        File directory = new File(nameDir);
        if (directory.isDirectory()) {
            File[] listFiles = directory.listFiles();
            if (listFiles == null) {
                return picturePathList;
            }
            if (listFiles.length > 0) {
                for (File listFile : listFiles) {
                    String filePath = listFile.getAbsolutePath();
                    if (IsSupportedFile(filePath)) {
                        picturePathList.add(filePath);
                    }
                }
            } else {
                Log.w(TAG, nameDir + " is empty");
            }
        } else {
            Log.e(TAG, nameDir + " directory path is not valid !");
        }
        return picturePathList;
    }

    // https://developer.android.com/topic/performance/graphics/load-bitmap.html
    //
    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        Log.d(TAG, "calculateInSampleSize");
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }
}

