package com.hibernatus.hibmobtech;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.hibernatus.hibmobtech.database.couchbase.CouchBaseDaoCause;
import com.hibernatus.hibmobtech.database.couchbase.CouchBaseDaoFactory;
import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoMroRequest;
import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoTask;
import com.hibernatus.hibmobtech.model.Cause;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.model.Task;
import com.hibernatus.hibmobtech.mrorequest.MroRequestActivity;
import com.hibernatus.hibmobtech.mrorequest.MroRequestApiService;
import com.hibernatus.hibmobtech.mrorequest.mroRequestSignature.SignatureCacheManager;
import com.hibernatus.hibmobtech.network.retrofitError.DelayedTimerTaskClientSignatureRetrofitErrorManager;
import com.hibernatus.hibmobtech.network.retrofitError.PictureCacheRetrofitErrorHandler;
import com.hibernatus.hibmobtech.observer.ServerDataObservable;
import com.hibernatus.hibmobtech.observer.ServerDataObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TimerTask;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Eric on 23/01/2016.
 */

public class DelayedTimerTask extends TimerTask implements ServerDataObservable {
    public static final String TAG = DelayedTimerTask.class.getSimpleName();

    protected final Context context;
    protected PictureCacheRetrofitErrorHandler pictureCacheRetrofitErrorHandler;
    protected DelayedTimerTaskClientSignatureRetrofitErrorManager delayedTimerTaskClientSignatureRetrofitErrorManager;
    protected SignatureCacheManager signatureCacheManager;
    //protected ServerDataObservable serverDataObservable;
    protected ServerDataObserver serverDataObserver;
    protected Boolean isServerDataChanged;

    // c o n s t r u c t o r
    // ---------------------
    public DelayedTimerTask(Context context) {
        Log.d(TAG, "DelayedTimerTask");
        this.context = context;
        this.pictureCacheRetrofitErrorHandler = new PictureCacheRetrofitErrorHandler(context);
        this.delayedTimerTaskClientSignatureRetrofitErrorManager = new DelayedTimerTaskClientSignatureRetrofitErrorManager(context);
        signatureCacheManager = new SignatureCacheManager(context, delayedTimerTaskClientSignatureRetrofitErrorManager);
/*        serverDataObservableOld = new ServerDataObservableOld();
        serverDataObservableOld.addObserver(serverDataObserverOld);*/
    }

    @Override
    public void register(ServerDataObserver observer) {
        Log.d(TAG, "register: observer=" + observer);

        if(observer == null) throw new NullPointerException("Null Observer");
        if(serverDataObserver == null) serverDataObserver = observer;
    }

    @Override
    public void unregister(ServerDataObserver observer) {
        Log.d(TAG, "unregister: observer=" + observer);
    }

    @Override
    public void notifyObservers() {
        Log.d(TAG, "notifyObservers");
        serverDataObserver.update();
    }

    @Override
    public Object getUpdate(ServerDataObserver observer) {
        Log.d(TAG, "getUpdate");
        return this.isServerDataChanged;
    }

    @Override
    public void run() {
        Log.i(TAG, "Timer Task executing");
        try {
            launchLoading();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    void launchLoading() throws IOException, ClassNotFoundException {
        Log.d(TAG, "launchLoading");

        if(isNetworkAvailable()) {
            // Load Causes
            updateCouchBaseCausesWithDataFromServer("");

            // Load Tasks
            updateSQLiteBaseTasksWithDataFromServer("");

            // Load MroRequests
            notifyUserOfNewRequests(HibmobtechApplication.getDataStore().getUserId());

            // Post Signatures from Cache
            signatureCacheManager.postAllDataBaseSignatureCache();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    // U p d a t e   C a u s e s
    // -------------------------

    public void updateCouchBaseCausesWithDataFromServer(String query) { // insert into CouchBase
        Log.d(TAG, "updateCouchBaseCausesWithDataFromServer: query=" + query);
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        if(HibmobtechApplication.getInstance().isOnline()) {
            service.searchCauses(query, new Callback<List<Cause>>() {
                @Override
                public void success(List<Cause> causes, retrofit.client.Response response) {
                    if (causes == null) return;
                    List<Cause> causeList = causes;
                    Log.i(TAG, "updateCouchBaseCausesWithDataFromServer: causeList=" + causeList);
                    CouchBaseDaoCause causeDAOImplCouchBase = new CouchBaseDaoCause((CouchBaseDaoFactory) HibmobtechApplication.getDaoFactory());
                    causeDAOImplCouchBase.deleteAllCauses();
                    for (Cause cause : causeList) {
                        causeDAOImplCouchBase.addCause(cause);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "failure : " + error.toString());
                }
            });
        }
    }

    // U p d a t e   T a s k s
    // -----------------------

    public void updateSQLiteBaseTasksWithDataFromServer(String query) { // insert into SQLiteBase
        Log.d(TAG, "updateSQLiteBaseTasksWithDataFromServer: query=" + query);
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        if(HibmobtechApplication.getInstance().isOnline()) {
            service.searchTasks(query, new Callback<List<Task>>() {
                @Override
                public void success(List<Task> tasks, Response response) {
                    if (tasks == null) return;
                    List<Task> taskList = tasks;
                    Log.d(TAG, "updateSQLiteBaseTasksWithDataFromServer: taskList=" + taskList);
                    SQLiteBaseDaoTask SQLiteBaseDaoTask = new SQLiteBaseDaoTask(context);
                    SQLiteBaseDaoTask.open();
                    SQLiteBaseDaoTask.deleteAll();
                    for (Task task : taskList) {
                        SQLiteBaseDaoTask.insertTask(task);
                    }
                    SQLiteBaseDaoTask.close();
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "failure : " + error.toString());
                }
            });
        }
    }

    // n o t i f y   U s e r   O f   N e w    R e q u e s t s
    // ------------------------------------------------------

    public void notifyUserOfNewRequests(long userId) {
        Log.d(TAG, "notifyUserOfNewRequests: userId=" + userId);
        MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
        if(HibmobtechApplication.getInstance().isOnline()) {
            service.getMroRequestByStatus(userId, "ASSIGNED, IN_PROGRESS", new Callback<List<MroRequest>>() {
                @Override
                public void success(List<MroRequest> mroRequestServerList, Response response) {
                    if (mroRequestServerList == null) return;
                    Log.d(TAG, "notifyUserOfNewRequests: mroRequestServerList=" + mroRequestServerList);
                    SQLiteBaseDaoMroRequest SQLiteBaseDaoMroRequest = new SQLiteBaseDaoMroRequest(context);
                    SQLiteBaseDaoMroRequest.open();
                    List<MroRequest> mroRequestLocalList = SQLiteBaseDaoMroRequest.getAllMroRequest();
                    compareMroRequestLists(mroRequestLocalList, mroRequestServerList);
                    SQLiteBaseDaoMroRequest.deleteAll();
                    for (MroRequest mroRequest : mroRequestServerList) {
                        SQLiteBaseDaoMroRequest.insertMroRequest(mroRequest);
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Log.e(TAG, "failure : " + error.toString());
                }
            });
        }
    }

    public void compareMroRequestLists(List<MroRequest> mroRequestLocalList, List<MroRequest> mroRequestServerList) {
        Log.d(TAG, "compareMroRequestLists: mroRequestLocalList=" + mroRequestLocalList);
        Log.d(TAG, "compareMroRequestLists: mroRequestServerList=" + mroRequestServerList);

        if (mroRequestLocalList == null || mroRequestServerList == null)return;
        if (!mroRequestLocalList.isEmpty()) {
            List<Long> mroRequestLocalListId = new ArrayList<>();
            for (MroRequest mroRequest : mroRequestLocalList) {
                mroRequestLocalListId.add(mroRequest.getId());
            }
            List<Long> mroRequestServerListId = new ArrayList<>();
            for (MroRequest mroRequest : mroRequestServerList) {
                mroRequestServerListId.add(mroRequest.getId());
            }
            Collection<Long> similar = new HashSet<>(mroRequestServerListId);
            Collection<Long> different = new HashSet<>();
            different.addAll(mroRequestServerListId);
            different.addAll(mroRequestLocalListId);
            similar.retainAll(mroRequestLocalListId);
            different.removeAll(similar);
            Log.d(TAG, "compareMroRequestLists: mroRequestLocalListId=" + mroRequestLocalListId);
            Log.d(TAG, "compareMroRequestLists: mroRequestServerListId=" + mroRequestServerListId);
            Log.d(TAG, "compareMroRequestLists: similar=" + similar);
            Log.d(TAG, "compareMroRequestLists: different=" + different);

            int i = 0;
            if(different == null) return;
            Iterator iterator = different.iterator();
            while (iterator.hasNext()) {
                Long newMroRequestId = (Long) iterator.next();
                Log.d(TAG, "compareMroRequestLists: newMroRequest(" + i + ") id=" + newMroRequestId);
                for (MroRequest mroRequest : mroRequestServerList) {
                    if (mroRequest.getId().equals(newMroRequestId)) {
                        launchNotification(mroRequest);
                    }
                }
                i++;
            }
        }
    }

    public void launchNotification(MroRequest newMroRequest) {
        Log.e(TAG, "launchNotification: newMroRequest.getSite().getName()="
                + newMroRequest.getSite().getName());

        getUpdate(serverDataObserver);
        notifyObservers();

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setLights(Color.MAGENTA, 300, 300)
                .setColor(0xCD358C)
                .setVibrate(new long[]{100, 250})
                .setDefaults(Notification.DEFAULT_SOUND)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_hibmob_logo_bw)
                .setContentTitle("Demande d'intervention No " + newMroRequest.getId().toString())
                .setContentText(newMroRequest.getSite().getName());

        // Creates an explicit intent for an Activity in your app
        Intent intent = new Intent(context, MroRequestActivity.class);

        intent.putExtra(MroRequest.MRO_REQUEST_ID_KEY, newMroRequest.getId());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);


/*        Intent notificationIntent = new Intent(context, MroRequestActivity.class);
        notificationIntent.putExtra(MroRequest.MRO_REQUEST_ID_KEY, newMroRequest.getId());
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(context, notificationIndex, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(getApplicationContext(), notificationTitle, notificationMessage, pendingNotificationIntent);*/


        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MroRequestActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(intent);
        // PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent((int) (long) newMroRequest.getId(), PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to onUpdatedContent the notification later on.
        notificationManager.notify(newMroRequest.getId().intValue(), notificationBuilder.build());
    }
}

