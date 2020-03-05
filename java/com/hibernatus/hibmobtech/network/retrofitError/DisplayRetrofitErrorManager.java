package com.hibernatus.hibmobtech.network.retrofitError;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.util.Log;

import com.cocosw.bottomsheet.BottomSheet;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.utils.DialogUtils;

import retrofit.RetrofitError;

// c l a s s   D i s p l a y R e t r o f i t E r r o r M a n a g e r
// -----------------------------------------------------------------
public class DisplayRetrofitErrorManager extends RetrofitErrorHandler {

    private static final String TAG = DisplayRetrofitErrorManager.class.getSimpleName();

    private Context context;
    private AlertDialog alertDialog;
    private BottomSheet bottomSheet;
    private BottomSheet finalBottomSheet;
    private DialogUtils dialogUtils;

    // C o n s t r u c t o r s
    //

    public DisplayRetrofitErrorManager(Context context) {
        Log.d(TAG, "Constructor: DisplayRetrofitErrorManager(Context context)");
        this.context = context;
        createAlertDialog(context, "");
        createBottomSheet(context);
    }

    // B o t t o m   S h e e t
    // -----------------------

    public void createBottomSheet(Context context) {
        this.context = context;
        Log.d(TAG, "createShowBottomSheet: context=" + context);
        bottomSheet = new BottomSheet
                .Builder(context, R.style.BottomSheet_StyleDialog)
                .title("Aucune connexion")
                .sheet(R.menu.menu_bottom_sheet)
                .build();
    }

    public void showBottomSheet(final Context context) {
        Log.d(TAG, "showBottomSheet: context=" + context);
        Activity currentActivity = HibmobtechApplication.getInstance().getCurrentActivity();
        if(currentActivity != null){
            if(currentActivity.equals(context)){
                if(bottomSheet == null) {
                    createBottomSheet(context);
                }
                if(!((Activity) context).isFinishing())
                {
                    //show dialog

                    if (!bottomSheet.isShowing()) {
                        Log.d(TAG, "showBottomSheet: affichage du bottom sheet");
                        bottomSheet.show();

                        Log.d(TAG, "showBottomSheet: wait BottomSheet before cancel");
                        Handler handler = new Handler();
                        finalBottomSheet = bottomSheet;
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finalBottomSheet.cancel();
                            }
                        }, 10000);
                    }
                }
            }
        }
    }

    public void dismissBottomSheet() {
        if (bottomSheet != null && bottomSheet.isShowing()) {
            bottomSheet.dismiss();
        }
        if (finalBottomSheet != null && finalBottomSheet.isShowing()) {
            finalBottomSheet.dismiss();
        }
    }

    // A l e r t   D i a l o g
    // -----------------------

    public void  createAlertDialog(Context context, String message) {
        Log.d(TAG, "createAlertDialog: context=" + context);
        this.context = context;
        dialogUtils = new DialogUtils();
        alertDialog = dialogUtils.createAlertDialogOK(message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Log.e(TAG, "createAlertDialog: onClick: onOKListener");
                    }
                });
    }

    public void showAlertDialog(Context context, String message) {
        Log.d(TAG, "showAlertDialog: " + message);
        if(context != null) {
            if(alertDialog == null) {
                createAlertDialog(context, message);
            }
            if(!((Activity) context).isFinishing()) {
                //show dialog
                if (!alertDialog.isShowing()) {
                    Log.d(TAG, "showAlertDialog: affichage de la boîte de dialogue");
                    alertDialog.setMessage(message);
                    alertDialog.show();
                }
            }
        }
    }

    @Override
    public void onNetwork(RetrofitError retrofitError){
        super.onNetwork(retrofitError);
        showBottomSheet(context);
    }

    @Override
    public void onConversion(RetrofitError retrofitError){
        super.onConversion(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void onUnexpected(RetrofitError retrofitError){
        super.onUnexpected(retrofitError);
        //showAlertDialog(context, completeMessage);
        showBottomSheet(context);
    }

    @Override
    public void on400(RetrofitError retrofitError){
        super.on400(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on401(RetrofitError retrofitError){
        super.on401(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on402(RetrofitError retrofitError){
        super.on402(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on403(RetrofitError retrofitError){
        super.on403(retrofitError);
        setFrenchMessage("Erreur réseau limite API quotidienne dépassée");
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on404(RetrofitError retrofitError){
        super.on404(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on405(RetrofitError retrofitError){
        super.on405(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on406(RetrofitError retrofitError){
        super.on406(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on407(RetrofitError retrofitError){
        super.on407(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on408(RetrofitError retrofitError){
        super.on408(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on409(RetrofitError retrofitError){
        super.on409(retrofitError);
        setMessages(retrofitError, "Erreur conflit de données:"
                + CRL + "il existe une version des données de l'intervention plus récente sur le serveur:"
                + CR + "rafraîchissez l'intervention");
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on410(RetrofitError retrofitError){
        super.on410(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on411(RetrofitError retrofitError){
        super.on411(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on412(RetrofitError retrofitError){
        super.on412(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on413(RetrofitError retrofitError){
        super.on413(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on414(RetrofitError retrofitError){
        super.on414(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on415(RetrofitError retrofitError){
        super.on415(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on416(RetrofitError retrofitError){
        super.on416(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on417(RetrofitError retrofitError){
        super.on417(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on500(RetrofitError retrofitError){
        super.on500(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on501(RetrofitError retrofitError){
        super.on501(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on502(RetrofitError retrofitError){
        super.on502(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on503(RetrofitError retrofitError){
        super.on503(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on504(RetrofitError retrofitError){
        super.on504(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void on505(RetrofitError retrofitError){
        super.on505(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void onNoResponse(RetrofitError retrofitError){
        super.onNoResponse(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    @Override
    public void onDefault(RetrofitError retrofitError){
        super.onDefault(retrofitError);
        showAlertDialog(context, completeMessage);
    }

    // g e t t e r s   &   s e t t e r s
    // ---------------------------------


    public AlertDialog getAlertDialog() {
        return alertDialog;
    }

    public void setAlertDialog(AlertDialog alertDialog) {
        this.alertDialog = alertDialog;
    }

    public BottomSheet getBottomSheet() {
        return bottomSheet;
    }

    public void setBottomSheet(BottomSheet bottomSheet) {
        this.bottomSheet = bottomSheet;
    }
}

