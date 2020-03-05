package com.hibernatus.hibmobtech.tracking;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.database.sqlitebase.SQLiteBaseDaoTrackingLocation;
import com.hibernatus.hibmobtech.model.TrackingLocation;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.hibernatus.hibmobtech.utils.ThreadUtils.getThreadSignature;

/**
 * Created by Eric on 20/05/2017.
 */

public class HibmobTrackingService extends Service implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    protected static final String TAG = "HibmobTrackingService";
    public static final String REQUEST_STRING = "myRequest from HibmobTrackingService";
    public static final String RESPONSE_STRING = "myResponse fromHibmobTrackingService";
    public static final String RESPONSE_MESSAGE = "myResponseMessage from HibmobTrackingService";

    protected Context context;
    protected SQLiteBaseDaoTrackingLocation sqLiteBaseDaoTrackingLocation;

    // The desired interval for location updates. Inexact. Updates may be more or less frequent
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 15000; // 15s

    // The fastest rate for active location updates. Exact. Updates will never be more frequent than this value
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 10000; // 10s

    protected GoogleApiClient googleApiClient; // Provides the entry point to Google Play services.
    protected LocationRequest locationRequest; // Stores parameters for requests to the FusedLocationProviderApi
    protected Location currentLocation; // Represents a geographical location.
    protected Boolean requestingLocationUpdates; // Tracks the status of the location updates request

    protected String myDeviceId;
    protected boolean currentlyProcessingLocation = false;

    protected PowerManager.WakeLock wakeLock;
    PowerManager powerManager;

    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        super.onCreate();
        this.context = getApplicationContext();

        powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "HibmobTechWakelockTag");
        wakeLock.acquire();

        myDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        requestingLocationUpdates = false;

        // launch the process of building a GoogleApiClient and requesting the LocationServices API.
        buildGoogleApiClient();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
/*        Toast.makeText(getApplicationContext(), "Tracking Service démarré: lancement No " + startId,
                Toast.LENGTH_SHORT).show();*/

        Log.d(TAG, "onStartCommand: Service started: startId=" + startId);

        sqLiteBaseDaoTrackingLocation = new SQLiteBaseDaoTrackingLocation(context);
        if (!currentlyProcessingLocation) {
            currentlyProcessingLocation = true;
            if (isGooglePlayServicesAvailable()) {
                buildAndConnectGoogleApiClient();
            } else {
                Log.e(TAG, "onPreExecute: unable to connect to google play services");
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");

        googleApiClient.disconnect();
        super.onDestroy();
        if(wakeLock != null)
            wakeLock.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // @ O v e r r i d e   m e t h o d s   C o n n e c t i o n C a l l b a c k s
    // -------------------------------------------------------------------------
    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG, "onConnected: Connected to GoogleApiClient");
        // Toast.makeText(getApplicationContext(), "Connecté au service de localisation", Toast.LENGTH_SHORT).show();
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.d(TAG, "onConnectionSuspended");
        Toast.makeText(getApplicationContext(), "Connection à la localisation Google suspendue : tentative de reconnection" ,
                Toast.LENGTH_SHORT).show();
        googleApiClient.connect();
    }

    // @ O v e r r i d e   m e t h o d s   O n C o n n e c t i o n F a i l e d L i s t e n e r
    // ---------------------------------------------------------------------------------------

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        Toast.makeText(getApplicationContext(), "Echec de connection à la localisation Google : " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    // O t h e r s   m e t h o d s
    // ---------------------------
    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "Building GoogleApiClient");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        Log.d(TAG, "createLocationRequest");

        locationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        locationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        locationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates");
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
    }

    // @ O v e r r i d e   m e t h o d s   L o c a t i o n L i s t e n e r
    // -------------------------------------------------------------------
    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        if (location != null) {
            Log.d(TAG, "onLocationChanged: " + getThreadSignature()
                    + " lat= " + location.getLatitude() + " long=" + location.getLongitude()
                    + " accuracy: " + location.getAccuracy());

            // we have our desired accuracy of 500 meters so lets quit this service,
            if (location.getAccuracy() < 1000.0f) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                if(!HibmobtechApplication.getInstance().isOnline()){
                    Log.d(TAG, "onLocationChanged: " + getThreadSignature()
                            + " insert location in db: "
                            + location.getLatitude() + ", " + location.getLongitude());
                    sqLiteBaseDaoTrackingLocation.open();
                    sqLiteBaseDaoTrackingLocation.insertTrackingLocation(Tracking.fromLocation(location));
                }
                else {
                    pushTrackingLocations(location);
                }
            } else {
                Log.e(TAG, "onLocationChanged: location.getAccuracy()=" + location.getAccuracy() + " > 1000 meters => location not pushed");
            }
        }
    }

/*    public boolean isOnline() {
        boolean isOnline;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        isOnline =  (networkInfo != null && networkInfo.isConnected());
        if(isOnline)
            Log.d(TAG, "!!!! onLine !!!!");
        else
            Log.d(TAG, "!!!! offLine !!!!");
        return isOnline;
    }*/

    protected void pushTrackingLocations(Location lastLocation) {
        //Log.e(TAG, "pushTrackingLocations" + getThreadSignature());
        List<TrackingLocation> trackingLocations;
        sqLiteBaseDaoTrackingLocation.open();
        trackingLocations = sqLiteBaseDaoTrackingLocation.getAllTrackingLocations();
        if(trackingLocations == null){
            trackingLocations = new ArrayList<>(1);
        }
        trackingLocations.add(Tracking.fromLocation(lastLocation));
        TrackingSegment trackingSegment = new TrackingSegment(myDeviceId, trackingLocations);
        try {
            pushTrackingSegment(trackingSegment, lastLocation);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void pushTrackingSegment(final TrackingSegment trackingSegment, final Location lastLocation) throws InterruptedException {
        TrackingApiService tracking = HibmobtechApplication.getRestClient().getTrackingService();

        Log.d(TAG, "pushTrackingSegment: " + getThreadSignature() + " push ["
                + trackingSegment.getNumberOfLocations() +"] locations: last:lat=" + lastLocation.getLatitude()
                + " long=" + lastLocation.getLongitude());

        Callback<TrackingResult> callback = new Callback<TrackingResult>() {
            @Override
            public void success(TrackingResult result, Response response) {

                if (result != null && response != null) {
                    Log.d(TAG, "pushTrackingSegment: " + getThreadSignature()
                            + " Success pushed [" + result.getValue() + "/" + trackingSegment.getNumberOfLocations()
                            + "] locations: status=" + response.getStatus());
                } else if (result != null && response == null) {
                    Log.d(TAG, "pushTrackingSegment: " + getThreadSignature()
                            + " Success pushed [" + result.getValue() + "/" + trackingSegment.getNumberOfLocations()
                            + "] locations");
                }

                sqLiteBaseDaoTrackingLocation.open();
                // suppress all, but only, locations send with success
                List<TrackingLocation> trackingLocations = trackingSegment.getLocations();
                for (TrackingLocation loc : trackingLocations) {
                    sqLiteBaseDaoTrackingLocation.deleteTrackingLocation(loc);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Response response = retrofitError.getResponse();
                if (response != null)
                    Log.d(TAG, "pushTrackingSegment: " + getThreadSignature() + " ERROR "
                            + retrofitError + " " + retrofitError.getKind()
                            + " " + response.getStatus());
                else
                    Log.d(TAG, "pushTrackingSegment: " + getThreadSignature() + " ERROR "
                            + retrofitError + " " + retrofitError.getKind());
                Log.d(TAG, "pushTrackingSegment: " + getThreadSignature()
                        + " insert location in db: "
                        + lastLocation.getLatitude() + ", " + lastLocation.getLongitude());
                sqLiteBaseDaoTrackingLocation.open();
                sqLiteBaseDaoTrackingLocation.insertTrackingLocation(Tracking.fromLocation(lastLocation));
            }
        };

        tracking.pushLocations(myDeviceId, trackingSegment, callback);
    }

    private boolean isGooglePlayServicesAvailable() {
        Log.d(TAG, "isGooglePlayServicesAvailable");
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        if (googleAPI.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS) {
            return true;
        } else {
            Log.d(TAG, "checkGoogleApiAvailability: unable to connect to google play services");
            return false;
        }
    }

    private void buildAndConnectGoogleApiClient() {
        Log.d(TAG, "buildAndConnectGoogleApiClient");
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        if (!googleApiClient.isConnected() || !googleApiClient.isConnecting()) {
            googleApiClient.connect();
        }
    }
}
