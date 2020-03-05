package com.hibernatus.hibmobtech.map;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.LayerDrawable;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.achartengine.ChartFactory;
import com.hibernatus.hibmobtech.achartengine.DefaultRenderer;
import com.hibernatus.hibmobtech.achartengine.GraphicalView;
import com.hibernatus.hibmobtech.achartengine.SimpleSeriesRenderer;
import com.hibernatus.hibmobtech.bottomsheet.HibmobtechBottomSheet;
import com.hibernatus.hibmobtech.model.CategorySeries;
import com.hibernatus.hibmobtech.model.HibmobtechAddress;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.model.Operation;
import com.hibernatus.hibmobtech.mrorequest.MroRequestActivity;
import com.hibernatus.hibmobtech.mrorequest.MroRequestCurrent;
import com.hibernatus.hibmobtech.mrorequest.mroRequestLoader.MroRequestAssignedLoader;
import com.hibernatus.hibmobtech.mrorequest.mroRequestLoader.MroRequestLoader;
import com.hibernatus.hibmobtech.mrorequest.mroRequestLoader.MroRequestOpenedLoader;
import com.hibernatus.hibmobtech.utils.DialogUtils;
import com.hibernatus.hibmobtech.utils.NotificationUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static com.hibernatus.hibmobtech.model.MroRequest.Status.ASSIGNED;
import static com.hibernatus.hibmobtech.model.MroRequest.Status.FINISHED;
import static com.hibernatus.hibmobtech.model.MroRequest.Status.IN_PROGRESS;
import static com.hibernatus.hibmobtech.model.MroRequest.Status.OPENED;

// c l a s s   M a p A c t i v i t y
// ---------------------------------
public class MapActivity extends FragmentActivity implements
        OnMapReadyCallback,
        DialogInterface.OnDismissListener,
        ClusterManager.OnClusterClickListener<MroRequest>,
        ClusterManager.OnClusterInfoWindowClickListener<MroRequest>,
        ClusterManager.OnClusterItemClickListener<MroRequest>,
        ClusterManager.OnClusterItemInfoWindowClickListener<MroRequest> {

    public static final String TAG = MapActivity.class.getSimpleName();
    public static final String ACTIVITY_FILTER = MapActivity.class.getName();

    protected static final int MAX_NUMBER_OF_MARKERS = 100;
    //protected static final int CLUSTER_ICON_SIZE = 350;
    protected static final int CLUSTER_ICON_SIZE = 250;
    protected static final int MARKER_ICON_SIZE = 90;
    protected static final float COORDINATE_OFFSET = 0.000025f;
    protected static final int NORMAL_ZOOM_LEVEL = 13;
    protected static final int MAX_ZOOM_LEVEL = 18;
    protected static final int ANIMATION_DURATION = 1;
    protected static final int PADDING = 200; // offset from edges of the googleMap in pixels

    protected GoogleMap googleMap;
    protected MroRequestLoader mroRequestAssignedLoader;
    protected MroRequestLoader mroRequestOpenedLoader;
    protected List<MroRequest> mroRequestAssignedList = Collections.emptyList();
    protected List<MroRequest> mroRequestOpenedList = Collections.emptyList();
    protected HashMap<String, String> openedMarkerLocationHashMap;
    protected HashMap<String, String> assignedMarkerLocationHashMap;
    protected ClusterManager<MroRequest> clusterManager;
    protected CameraPosition previousCameraPosition = null;
    protected MroRequest clickedMroRequest;
    protected Cluster<MroRequest> clickedMroRequestCluster;
    protected boolean isGrantedLocationPermission;
    protected android.app.AlertDialog currentCheckLocationAndNetworkAlertDialog = null;
    protected DialogUtils dialogUtils;
    protected NotificationUtils notificationUtils;
    protected AlertDialog alertDialog;
    protected CoordinatorLayout mapActivityCoordinatorLayout;
    protected View mapActivityBottomSheet;
    protected HibmobtechBottomSheet bottomSheetBehavior;

    private LinearLayoutManager linearLayoutManager;

    @Override
    public boolean onClusterClick(Cluster<MroRequest> cluster) {
        Log.d(TAG, "onClusterClick");
        return false;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<MroRequest> cluster) {
        Log.d(TAG, "onClusterInfoWindowClick");
    }

    @Override
    public boolean onClusterItemClick(MroRequest mroRequest) {
        Log.d(TAG, "onClusterItemClick");
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(MroRequest mroRequest) {
        Log.d(TAG, "onClusterItemInfoWindowClick");
        if(clickedMroRequest != null) {
            MroRequestCurrent.getInstance().setMroRequestCurrent(clickedMroRequest);
        }
        startActivity(new Intent(getApplicationContext(), MroRequestActivity.class));
    }


    // p r i v a t e   c l a s s   M r o R e q u e s t C l u s t e r R e n d e r e r
    // -----------------------------------------------------------------------------
    private class MroRequestClusterRenderer extends DefaultClusterRenderer<MroRequest> {
        public final String TAG = MroRequestClusterRenderer.class.getSimpleName();
        private final IconGenerator mIconGenerator;

        public MroRequestClusterRenderer() {
            super(getApplicationContext(), googleMap, clusterManager);
            Log.d(TAG, "MroRequestClusterRenderer");
            mIconGenerator = new IconGenerator(getApplicationContext());
        }

        @Override
        protected boolean shouldRenderAsCluster(com.google.maps.android.clustering.Cluster cluster) {
            Log.d(TAG, "shouldRenderAsCluster: cluster=" + cluster + " cluster.getSize())" + cluster.getSize());

            return cluster.getSize() > 1; // Always render clusters > 1
        }

        @Override
        protected void onBeforeClusterItemRendered(MroRequest mroRequest, MarkerOptions markerOptions) {
            int height = MARKER_ICON_SIZE;
            int width = MARKER_ICON_SIZE;
            Bitmap bm = BitmapFactory.decodeResource(getResources(), mroRequest.profilePhoto);
            Bitmap smallMarker = Bitmap.createScaledBitmap(bm, width, height, false);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
        }

        private LayerDrawable makeClusterBackground() {
            return null;
        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MroRequest> cluster, MarkerOptions markerOptions) {
            Log.d(TAG, "onBeforeClusterRendered");

            ArrayList<MroRequest> mroRequests = (ArrayList<MroRequest>) cluster.getItems();

            double assigned = 0;
            double in_progress = 0;
            double opened = 0;
            for (MroRequest mro : mroRequests) {
                if(mro.getStatus().equals(ASSIGNED)) {
                    assigned++;
                }
                else if (mro.getStatus().equals(IN_PROGRESS)){
                    in_progress++;
                }
                else if (mro.getStatus().equals(OPENED)){
                    opened++;
                }
            }
            Log.d(TAG, "onBeforeClusterRendered : ASSIGNED Percentage=" + (assigned / mroRequests.size() )*100);
            Log.d(TAG, "onBeforeClusterRendered : IN_PROGRESS Percentage=" + (in_progress / mroRequests.size() )*100);
            Log.d(TAG, "onBeforeClusterRendered : OPENED Percentage=" + (opened / mroRequests.size() )*100);


            BitmapDescriptor descriptor = null;
            String[] code = new String[] {"Affectées", "Démarrées", "Ouvertes"};

            double[] distribution = {
                    assigned,
                    in_progress,
                    opened
            };

            int orange = getResources().getColor(R.color.color_assigned);
            int green = getResources().getColor(R.color.color_in_progress);
            int red = getResources().getColor(R.color.color_opened);

            // Color of each Pie Chart Sections
            int[] colors = { orange, green, red };

            CategorySeries distributionSeries = new CategorySeries("");
            for(int i=0 ;i < distribution.length;i++){
                // Adding a slice with its values and name to the Pie Chart
                distributionSeries.add(code[i], distribution[i]);
            }

            DefaultRenderer defaultRenderer  = new DefaultRenderer();
            defaultRenderer.setDisplayValues(true);
            defaultRenderer.setChartTitle(String.valueOf(""));

            defaultRenderer.setDisplayLegend(false);

            for (int i = 0; i < distribution.length; i++){
                SimpleSeriesRenderer seriesRenderer = new SimpleSeriesRenderer();

                Locale locale = Locale.getDefault();
                NumberFormat percentageFormat  = NumberFormat.getIntegerInstance(locale);
                percentageFormat.setMaximumFractionDigits(0);
                seriesRenderer.setChartValuesFormat(percentageFormat );
                seriesRenderer.setColor(colors[i]);
                defaultRenderer.addSeriesRenderer(seriesRenderer);
            }

            GraphicalView graphicalView = ChartFactory.getPieChartView(getApplicationContext(), distributionSeries, defaultRenderer);
            Log.d(TAG, "onBeforeClusterRendered: graphicalView=" + graphicalView);
            descriptor = BitmapDescriptorFactory.fromBitmap(loadBitmapFromView(graphicalView));
            markerOptions.icon(descriptor);
        }
    }

    public static Bitmap loadBitmapFromView(View v) {
        Log.d(TAG, "onBeforeClusterRendered: v=" + v);
        Bitmap b = Bitmap.createBitmap(CLUSTER_ICON_SIZE, CLUSTER_ICON_SIZE, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        v.draw(c);
        return b;
    }


    //  p r i v a t e   c l a s s   I n f o W i n d o w A d a p t e r F o r C l u s t e r s
    // ------------------------------------------------------------------------------------
    private class InfoWindowAdapterForClusters implements GoogleMap.InfoWindowAdapter {
        public final String TAG = InfoWindowAdapterForClusters.class.getSimpleName();

        public InfoWindowAdapterForClusters() {
        }

        @Override
        public View getInfoWindow(final Marker marker) {
            Log.d(TAG, "getInfoWindow");
            List<MroRequest> mroRequests = (List<MroRequest>) clickedMroRequestCluster.getItems();

            Collections.sort(mroRequests, new Comparator<MroRequest>() {
                public int compare(MroRequest o1, MroRequest o2) {
                    return o1.getId().compareTo(o2.getId());
                }
            });

            Collections.sort(mroRequests, new Comparator<MroRequest>() {
                public int compare(MroRequest o1, MroRequest o2) {
                    return o1.getStatus().compareTo(o2.getStatus());
                }
            });

            MroAdapterForClusters adapter = new MroAdapterForClusters(MapActivity.this, mroRequests);
            RecyclerView listView = (RecyclerView) mapActivityBottomSheet.findViewById(R.id.mapActivityBottomSheetListView);
            listView.setLayoutManager(linearLayoutManager);
            listView.setAdapter(adapter);


            if (bottomSheetBehavior.getState() == HibmobtechBottomSheet.STATE_COLLAPSED) {
                Log.d(TAG, "OnClusterClickListener: onClusterClick: BottomSheetBehavior.STATE_COLLAPSED > let's STATE_ANCHOR_POINT");
                bottomSheetBehavior.setState(HibmobtechBottomSheet.STATE_ANCHOR_POINT);
            }
            if (bottomSheetBehavior.getState() == HibmobtechBottomSheet.STATE_EXPANDED) {
                Log.d(TAG, "OnClusterClickListener: onClusterClick: BottomSheetBehavior.STATE_EXPANDED > let's STATE_ANCHOR_POINT");
                bottomSheetBehavior.setState(HibmobtechBottomSheet.STATE_ANCHOR_POINT);
            }

            listView.requestLayout();
            adapter.notifyDataSetChanged();
            return null;
        }


        @Override
        public View getInfoContents(Marker marker) {
            Log.d(TAG, "getInfoContents");
            return null;
        }
    }

    //  p r i v a t e   c l a s s   I n f o W i n d o w A d a p t e r F o r I t e m s
    // ------------------------------------------------------------------------------------
    private class InfoWindowAdapterForItems implements GoogleMap.InfoWindowAdapter,
            GoogleMap.OnInfoWindowClickListener{
        public final String TAG = InfoWindowAdapterForItems.class.getSimpleName();

        private final View view;

        InfoWindowAdapterForItems() {
            view = getLayoutInflater().inflate(R.layout.map_activity_info_window, null);
        }

        @Override
        public View getInfoContents(Marker marker) {
            Log.d(TAG, "getInfoWindow");
            TextView mroRequestInfoWindowName = ((TextView) view.findViewById(R.id.mapActivityWindowNameTextView));
            TextView mroRequestInfoWindowAdress = ((TextView) view.findViewById(R.id.mapActivityInfoWindowAdressTextView));
            TextView mroRequestInfoWindowId = ((TextView) view.findViewById(R.id.mapActivityInfoWindowIdTextView));
            TextView mapActivityOperationDateTextView = ((TextView) view.findViewById(R.id.mapActivityOperationDateTextView));

            mroRequestInfoWindowName.setText(clickedMroRequest.getName());
            HibmobtechAddress add = clickedMroRequest.getSite().getAddress();
            String lineSep = System.getProperty("line.separator");
            String address = add.getAdressPart1() + lineSep + add.getAdressPart2();
            mroRequestInfoWindowAdress.setText(address);
            String mroRequestString = "Intervention No " + clickedMroRequest.getId().toString();
            mroRequestInfoWindowId.setText(mroRequestString);
            mroRequestInfoWindowId.setClickable(true);

            Operation operation = clickedMroRequest.getOperation();
            Date date;
            SimpleDateFormat mroRequestDateSdf;
            SimpleDateFormat mroRequestHourSdf = new SimpleDateFormat("HH:mm");
            String mroRequestDateString;
            String mroRequestHourString;

            if(clickedMroRequest.getStatus() == OPENED) {
                mroRequestDateSdf = new SimpleDateFormat("'Ouverte' dd MMM");
                date = clickedMroRequest.getRequestDate();
                if(date != null) {
                    mroRequestDateString = mroRequestDateSdf.format(date);
                    mroRequestHourString = mroRequestHourSdf.format(date);
                    if (mroRequestHourString.equals("00:00")) {
                        mapActivityOperationDateTextView.setText(mroRequestDateString);
                    } else {
                        mapActivityOperationDateTextView.setText(mroRequestDateString + " à " + mroRequestHourString);
                    }
                }
            }
            else if(clickedMroRequest.getStatus() == ASSIGNED) {
                mroRequestDateSdf = new SimpleDateFormat("'Planifiée' dd MMM");
                if(operation != null) {
                    date = operation.getDate();
                    if(date != null) {
                        mroRequestDateString = mroRequestDateSdf.format(date);
                        mroRequestHourString = mroRequestHourSdf.format(date);
                        if (mroRequestHourString.equals("00:00")) {
                            mapActivityOperationDateTextView.setText(mroRequestDateString);
                        } else {
                            mapActivityOperationDateTextView.setText(mroRequestDateString + " à " + mroRequestHourString);
                        }
                    }
                }
                else{
                    mapActivityOperationDateTextView.setText("");
                }
            }
            else if(clickedMroRequest.getStatus() == IN_PROGRESS) {
                mroRequestDateSdf = new SimpleDateFormat("'Démarrée' dd MMM");
                if(operation != null) {
                    date = operation.getDate();
                    if (date != null) {
                        mroRequestDateString = mroRequestDateSdf.format(date);
                        mroRequestHourString = mroRequestHourSdf.format(date);
                        if (mroRequestHourString.equals("00:00")) {
                            mapActivityOperationDateTextView.setText(mroRequestDateString);
                        } else {
                            mapActivityOperationDateTextView.setText(mroRequestDateString + " à " + mroRequestHourString);
                        }
                    } else {
                        mapActivityOperationDateTextView.setText("");
                    }
                }
            }
            else if(clickedMroRequest.getStatus() == FINISHED) {
                mroRequestDateSdf = new SimpleDateFormat("'Terminée' dd MMM");
                if(operation != null) {
                    date = operation.getDate();
                    if (date != null) {
                        mroRequestDateString = mroRequestDateSdf.format(date);
                        mroRequestHourString = mroRequestHourSdf.format(date);
                        if (mroRequestHourString.equals("00:00")) {
                            mapActivityOperationDateTextView.setText(mroRequestDateString);
                        } else {
                            mapActivityOperationDateTextView.setText(mroRequestDateString + " à " + mroRequestHourString);
                        }
                    } else {
                        mapActivityOperationDateTextView.setText("");
                    }
                }
            }
            return view;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            Log.d(TAG, "getInfoContents");
            return null; // set default InfoWindow
        }

        @Override
        public void onInfoWindowClick(Marker marker) {
            Log.d(TAG, "onInfoWindowClick");
        }
    }


    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        dialogUtils = new DialogUtils();
        notificationUtils = new NotificationUtils();
        linearLayoutManager = new LinearLayoutManager(this);
        mroRequestAssignedLoader = new MroRequestAssignedLoader();
        mroRequestOpenedLoader = new MroRequestOpenedLoader();
        setContentView(R.layout.map_activity);
        setCheckBox();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        openedMarkerLocationHashMap = new HashMap<>();
        assignedMarkerLocationHashMap = new HashMap<>();
        setBottomSheet();
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready for use.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        Log.d(TAG, "onMapReady");
        googleMap = map;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setMapToolbarEnabled(true);

        // Creating cluster manager object.
        // http://stackoverflow.com/questions/25968486/how-to-add-info-window-for-clustering-marker-in-android/25969059#25969059

        clusterManager = new ClusterManager<>(this, googleMap);
        //NonHierarchicalDistanceBasedAlgorithm algorithm = new NonHierarchicalDistanceBasedAlgorithm<>();
        HibmobtechNonHierarchicalDistanceBasedAlgorithm algorithm = new HibmobtechNonHierarchicalDistanceBasedAlgorithm<>();
        //GridBasedAlgorithm algorithm = new GridBasedAlgorithm<>();
        clusterManager.setAlgorithm(algorithm);
        clusterManager.setRenderer(new MapActivity.MroRequestClusterRenderer());

        clusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(new InfoWindowAdapterForClusters());
        clusterManager.getMarkerCollection().setOnInfoWindowAdapter(new InfoWindowAdapterForItems());

        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterInfoWindowClickListener(this);
        clusterManager.setOnClusterItemClickListener(this);
        clusterManager.setOnClusterItemInfoWindowClickListener(this);

        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MroRequest>() {
            @Override
            public boolean onClusterClick(Cluster<MroRequest> cluster) {
                Log.d(TAG, "OnClusterClickListener: onClusterClick");
                clickedMroRequestCluster = cluster;
                return false;
            }
        });

        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MroRequest>() {
            @Override
            public boolean onClusterItemClick(MroRequest mroRequest) {
                Log.d(TAG, "OnClusterItemClickListener: onClusterItemClick");
                clickedMroRequest = mroRequest;
                return false;
            }
        });

        //googleMap.setMinZoomPreference(8);
        //googleMap.setMaxZoomPreference(8);
        googleMap.setInfoWindowAdapter(clusterManager.getMarkerManager());
        googleMap.setOnInfoWindowClickListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                CameraPosition position = googleMap.getCameraPosition();
                if (previousCameraPosition == null || previousCameraPosition.zoom != position.zoom) {
                    previousCameraPosition = googleMap.getCameraPosition();

                    Log.d(TAG, "onCameraIdle: onCameraIdle > let's clusterManager.cluster() !");
                    clusterManager.cluster();
                }
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick: latLng=" + latLng.latitude + "-" + latLng.longitude);
            }
        });

        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d(TAG, "setOnMapLongClickListener LONG CLICK ! latLng=" + latLng.latitude + "-" + latLng.longitude);
                googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
/*                googleMap.animateCamera(CameraUpdateFactory.zoomBy((float) 2,
                        googleMap.getProjection().toScreenLocation(latLng)), ANIMATION_DURATION, null);*/
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(11), ANIMATION_DURATION, null);

            }
        });

        checkLocationPermission();
        if (isGrantedLocationPermission) {
            setMyLocationOk();
        } else {
            Log.d(TAG, "isGrantedLocationPermission == false !");
        }

        loadAndDisplayAssignedMroRequest();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult");

        Map<String, Integer> permissionsMap = new HashMap<String, Integer>();
        permissionsMap.put(Manifest.permission.READ_LOGS, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.BATTERY_STATS, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.GET_ACCOUNTS, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.READ_CONTACTS, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.WRITE_CONTACTS, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.READ_CALENDAR, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.WRITE_CALENDAR, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.ACCESS_NETWORK_STATE, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
        permissionsMap.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);

        for (int i = 0; i < permissions.length; i++) {
            permissionsMap.put(permissions[i], grantResults[i]);
            Log.d(TAG, "onRequestPermissionsResult: requestCode=" + requestCode
                    + " permissions[" + i + "]=" + permissions[i]
                    + " grantResults[" + i + "]=" + grantResults[i]);
        }
        // Check for ACCESS LOCATION
        if (permissionsMap.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && permissionsMap.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: Permissions LOCATION are OK");
            isGrantedLocationPermission = true;
            setMyLocationOk();
        }

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(dialog == currentCheckLocationAndNetworkAlertDialog)
            currentCheckLocationAndNetworkAlertDialog = null;
    }


    // L o c a t i o n   P e r m i s s i o n
    // -------------------------------------

    void setMyLocationOk() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        this.googleMap.setMyLocationEnabled(true);
        this.googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Log.d(TAG, "onMapReady: onMyLocationButtonClick");
                List<MroRequest> mroRequestList = new ArrayList<>();
                mroRequestList.addAll(mroRequestAssignedList);
                mroRequestList.addAll(mroRequestOpenedList);
                LatLngBounds.Builder builder = LatLngBounds.builder();
                int counter = 0;
                for (MroRequest mroRequest : mroRequestList) {
                    if(mroRequest.getSite().getGeoloc() != null) {
                        Double lat = mroRequest.getSite().getGeoloc().getLat();
                        Double lng = mroRequest.getSite().getGeoloc().getLng();
                        builder.include(new LatLng(lat, lng));
                        counter++;
                    }
                }

                if(counter == 0){
                    builder.include(new LatLng(48.8153847, 2.1901497000000063)); // Levon, Chaville
                }

                if(HibmobtechApplication.getLocationEnable()) {
                    Location myLocation = googleMap.getMyLocation();
                    if (myLocation != null) {
                        LatLng googleLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
                        builder.include(googleLatLng);
                        refreshClusterManager(builder);
                    } else {
                        Log.e(TAG, "myLocation = null !");
                    }
                }
                else {
                    Log.d(TAG, "onMyLocationButtonClick: Localisation désactivée !!!");
                    String message = "Pour permettre le bon fonctionnement de l'application, vous devez activer la localisation";

                    alertDialog = createAlertDialog(message);
                    if (!alertDialog.isShowing()) {
                        Log.d(TAG, "onMyLocationButtonClick: affichage de la boîte de dialogue");
                        if(!MapActivity.this.isFinishing())
                            alertDialog.show();
                    }
                }
                return true;
            }
        });
    }

    public void checkLocationPermission() {
        Log.d(TAG, "checkLocationPermission");

        final String[] manifestPermissions = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        // check if the activity has the location permissions
        int hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        Log.d(TAG, "checkLocationPermission: hasAccessFineLocationPermission=" + hasAccessFineLocationPermission);
        Log.d(TAG, "checkLocationPermission: hasAccessCoarseLocationPermission=" + hasAccessCoarseLocationPermission);

        if((hasAccessFineLocationPermission != PackageManager.PERMISSION_GRANTED)
                && (hasAccessCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) ){

            // Should we show an explanation?
            // shouldShowRequestPermissionRationale returns true if the app has requested this permission previously and the user denied the request.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                showMessageOKCancel("Pour permettre le bon fonctionnement de l'application, vous devez autoriser l'accès à la géolocalisation",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MapActivity.this,
                                        manifestPermissions,
                                        HibmobtechApplication.LOCATION_PERMISSION_REQUEST);
                            }
                        });
                Log.d(TAG, "checkLocationPermission with explanations: waiting for callback...");
                isGrantedLocationPermission = false;

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        manifestPermissions,
                        HibmobtechApplication.LOCATION_PERMISSION_REQUEST);
                Log.d(TAG, "checkLocationPermission without explanations: waiting for callback...");
                isGrantedLocationPermission = false;
            }

        } else {
            Log.d(TAG, "checkLocationPermission: the activity has the location permissions");
            //AndroidUtils.showToast(HibmobtechActivity.this, "the activity has the location permissions");
            isGrantedLocationPermission = true;
        }
    }

    public void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        Activity currentActivity = HibmobtechApplication.getInstance().getCurrentActivity();
        if (currentActivity == null) {
            Log.d(TAG, "createAlertDialogOKCancel: currentActivity == null");
            return;
        }
        if(!MapActivity.this.isFinishing()){
            new android.support.v7.app.AlertDialog
                    .Builder(currentActivity)
                    .setMessage(message)
                    .setPositiveButton("OK", okListener)
                    .setNegativeButton("Cancel", null)
                    .setOnDismissListener(this)
                    .create()
                    .show();
        }
        else {
            Log.d(TAG, "showMessageOKCancel: currentActivity.isFinishing()");
            currentActivity.isFinishing();
        }
    }

    public AlertDialog createAlertDialog(String message) {
        Log.d(TAG, "createAlertDialog");

        return alertDialog = dialogUtils.createAlertDialogOK(this, message,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        Log.d(TAG, "onClick: onOKListener");
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
    }

    // O t h e r s   m e t h o d s
    // ---------------------------

    public void setBottomSheet() {
        Log.d(TAG, "setBottomSheet");
        mapActivityCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.mapActivityCoordinatorLayout);
        mapActivityBottomSheet = mapActivityCoordinatorLayout.findViewById(R.id.mapActivityBottomSheet);
        bottomSheetBehavior = HibmobtechBottomSheet.from(mapActivityBottomSheet);
        bottomSheetBehavior.addBottomSheetCallback(new HibmobtechBottomSheet.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                Log.d(TAG, "setBottomSheet: onStateChanged: newState=" + newState);

                switch (newState) {
                    case HibmobtechBottomSheet.STATE_COLLAPSED:
                        // this collapsed state is the default and shows just a portion of the layout
                        // along the bottom. The height can be controlled with the app:behavior_peekHeight
                        // attribute (defaults to 0)
                        Log.d(TAG, "setBottomSheet: onStateChanged: BottomSheetBehavior.STATE_COLLAPSED");
                        break;
                    case HibmobtechBottomSheet.STATE_DRAGGING:
                        // the intermediate state while the user is directly dragging
                        // the bottom sheet up or down
                        Log.d(TAG, "setBottomSheet: onStateChanged: BottomSheetBehavior.STATE_DRAGGING");
                        break;
                    case HibmobtechBottomSheet.STATE_EXPANDED:
                        // the fully expanded state of the bottom sheet, where either the whole bottom sheet
                        // is visible (if its height is less than the containing CoordinatorLayout)
                        // or the entire CoordinatorLayout is filled
                        Log.d(TAG, "setBottomSheet: onStateChanged: BottomSheetBehavior.STATE_EXPANDED");
                        bottomSheet.requestLayout();
                        bottomSheet.invalidate();
                        break;
                    case HibmobtechBottomSheet.STATE_ANCHOR_POINT:
                        Log.d(TAG, "setBottomSheet: onStateChanged: BottomSheetBehavior.STATE_ANCHOR_POINT");
                        break;
                    case HibmobtechBottomSheet.STATE_HIDDEN:
                        // disabled by default (and enabled with the app:behavior_hideable attribute),
                        // enabling this allows users to swipe down on the bottom sheet
                        // to completely hide the bottom sheet
                        Log.d(TAG, "setBottomSheet: onStateChanged: BottomSheetBehavior.STATE_HIDDEN");
                        break;
                    default:
                        // STATE_SETTLING: that brief time between when the View is released
                        // and settling into its final position
                        break;
                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                // Log.d(TAG, "setBottomSheet: onSlide: slideOffset=" + slideOffset);
                // React to dragging events
            }
        });
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        Log.d(TAG, "setListViewHeightBasedOnChildren: onStateChanged: listView=");

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, CoordinatorLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    public void loadAndDisplayAssignedMroRequest() {
        Log.d(TAG, "loadAndDisplayAssignedMroRequest");
        Callback<List<MroRequest>> callback = new Callback<List<MroRequest>>() {
            @Override
            public void success(List<MroRequest> mroRequests, Response response) {
                if(mroRequests == null) return;

                mroRequestAssignedList = mroRequests;

                for(MroRequest mroRequest : mroRequests){
                    if(mroRequest.getSite().getGeoloc() != null) {
                        String mroRequestId = mroRequest.getId().toString();
                        String position = String.valueOf(mroRequest.getSite().getGeoloc().getLat())
                                + ","
                                + String.valueOf(mroRequest.getSite().getGeoloc().getLng());
                        assignedMarkerLocationHashMap.put(mroRequestId, position);
                        Log.d(TAG, "loadAndDisplayAssignedMroRequest: lat=" + mroRequest.getSite().getGeoloc().getLat()
                                + " lng=" + mroRequest.getSite().getGeoloc().getLng());
                    }
                }
                setAssignedMroRequests();
                displayAllMroRequests();
                clusterManager.cluster();
            }


            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Erreur Retrofit: error=" + error);
            }
        };
        mroRequestAssignedLoader.loadMroRequest(callback);
    }

    public void loadAssignedMroRequest() {
        Log.d(TAG, "loadAssignedMroRequest");
        Callback<List<MroRequest>> callback = new Callback<List<MroRequest>>() {
            @Override
            public void success(List<MroRequest> mroRequests, Response response) {
                if(mroRequests == null) return;

                mroRequestAssignedList = mroRequests;
                for(MroRequest mroRequest : mroRequests){
                    if(mroRequest.getSite().getGeoloc() != null) {
                        String mroRequestId = mroRequest.getId().toString();
                        String position = String.valueOf(mroRequest.getSite().getGeoloc().getLat())
                                + ","
                                + String.valueOf(mroRequest.getSite().getGeoloc().getLng());
                        assignedMarkerLocationHashMap.put(mroRequestId, position);
                        Log.d(TAG, "loadAndDisplayAssignedMroRequest: lat=" + mroRequest.getSite().getGeoloc().getLat()
                                + " lng=" + mroRequest.getSite().getGeoloc().getLng());
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "Erreur Retrofit: error=" + error);
            }
        };
        mroRequestAssignedLoader.loadMroRequest(callback);
    }

    public void setAssignedMroRequests() {
        Log.d(TAG, "setAssignedMroRequests");
        for (int i = 0; i < mroRequestAssignedList.size(); i++) {
            MroRequest mroRequest = mroRequestAssignedList.get(i);
            if(mroRequest.getSite().getGeoloc() != null) {
                LatLng latLng = coordinateForMarker(mroRequest.getSite().getGeoloc().getLat(),
                        mroRequest.getSite().getGeoloc().getLng());
                String position = latLng.latitude + "," + latLng.longitude;
                assignedMarkerLocationHashMap.put(mroRequest.getId().toString(), position);
                mroRequest.setLatLng(latLng);
                mroRequest.setName(mroRequest.getSite().getName());
                if (mroRequest.getStatus() == MroRequest.Status.ASSIGNED) {
                    mroRequest.setProfilePhoto(R.drawable.ic_ambulance_orange_mdpi);
                } else {
                    mroRequest.setProfilePhoto(R.drawable.ic_ambulance_green_mdpi);
                }
                mroRequestAssignedList.set(i, mroRequest);
            }
        }
    }

    public void loadAndDisplayOpenedMroRequest() {
        Log.d(TAG, "loadOpenedMroRequest");
        Callback<List<MroRequest>> callback = new Callback<List<MroRequest>>() {
            @Override
            public void success(List<MroRequest> mroRequests, Response response) {
                if(mroRequests == null) return;

                mroRequestOpenedList = mroRequests;
                for(MroRequest mroRequest : mroRequests){
                    String mroRequestId = mroRequest.getId().toString();
                    if(mroRequest.getSite().getGeoloc() != null) {
                        String position = String.valueOf(mroRequest.getSite().getGeoloc().getLat())
                                + ","
                                + String.valueOf(mroRequest.getSite().getGeoloc().getLng());
                        openedMarkerLocationHashMap.put(mroRequestId, position);
                        Log.d(TAG, "loadAndDisplayOpenedMroRequest: lat=" + mroRequest.getSite().getGeoloc().getLat()
                                + " lng=" + mroRequest.getSite().getGeoloc().getLng());
                    }
                }
                setOpenedMroRequests();
                displayAllMroRequests();
                clusterManager.cluster();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "Erreur Retrofit: error=" + error);
            }
        };
        mroRequestOpenedLoader.loadMroRequest(callback);
    }

    public void setOpenedMroRequests() {
        Log.d(TAG, "setOpenedMroRequests");
        for (int i = 0; i < mroRequestOpenedList.size(); i++) {
            MroRequest mroRequest = mroRequestOpenedList.get(i);
            if(mroRequest.getSite().getGeoloc() != null) {
                LatLng latLng = coordinateForMarker(mroRequest.getSite().getGeoloc().getLat(),
                        mroRequest.getSite().getGeoloc().getLng());
                String position = latLng.latitude + "," + latLng.longitude;
                openedMarkerLocationHashMap.put(mroRequest.getId().toString(), position);
                mroRequest.setLatLng(latLng);
                mroRequest.setName(mroRequest.getSite().getName());
                mroRequest.setProfilePhoto(R.drawable.ic_ambulance_red_mdpi);
                mroRequestOpenedList.set(i, mroRequest);
            }
        }
    }

    private void displayAllMroRequests() {
        Log.d(TAG, "displayAllMroRequests");
        List<MroRequest> mroRequestList = new ArrayList<>();
        mroRequestList.addAll(mroRequestAssignedList);
        mroRequestList.addAll(mroRequestOpenedList);
        LatLngBounds.Builder builder = LatLngBounds.builder();
        clusterManager.clearItems();
        int counter = 0;
        for(MroRequest mroRequest : mroRequestList) {
            if(mroRequest.getLatLng() != null) {
                clusterManager.addItem(mroRequest);
                Double lat = mroRequest.getLatLng().latitude;
                Double lng = mroRequest.getLatLng().longitude;
                Log.d(TAG, "displayAllMroRequests: name==" + mroRequest.getName() + " id=" + mroRequest.getId()
                        + " lat=" + lat + " lng=" + lng);
                builder.include(new LatLng(lat, lng));
                counter++;
            }
        }
        if(counter == 0){
            builder.include(new LatLng(48.8153847, 2.1901497000000063)); // Levon, Chaville
        }
        refreshClusterManager(builder);
    }

    private void displayMroRequests(List<MroRequest> mroRequestList) {
        Log.d(TAG, "displayMroRequests");
        LatLngBounds.Builder builder = LatLngBounds.builder();
        clusterManager.clearItems();

        int counter = 0;
        for(MroRequest mroRequest : mroRequestList) {
            Log.d(TAG, "displayMroRequests: mroRequest=" + mroRequest);
            if(mroRequest.getLatLng() != null) {
                clusterManager.addItem(mroRequest);

                LatLng point = new LatLng(mroRequest.getSite().getGeoloc().getLat(), mroRequest.getSite().getGeoloc().getLng());
                builder.include(point);
                counter++;
            }
        }
        if(counter == 0){
            builder.include(new LatLng(48.8153847, 2.1901497000000063)); // Levon, Chaville
        }
        refreshClusterManager(builder);
    }

    private LatLngBounds adjustBoundsForMinimumLatitudeDegrees(LatLngBounds bounds, double minLatitudeDegrees) {
        Log.d(TAG, "adjustBoundsForMinimumLatitudeDegrees");
        LatLng sw = bounds.southwest;
        LatLng ne = bounds.northeast;
        double visibleLatitudeDegrees = Math.abs(sw.latitude - ne.latitude);
        if (visibleLatitudeDegrees < minLatitudeDegrees) {
            LatLng center = bounds.getCenter();
            sw = new LatLng(center.latitude - (minLatitudeDegrees / 2), sw.longitude);
            ne = new LatLng(center.latitude + (minLatitudeDegrees / 2), ne.longitude);
            bounds = new LatLngBounds(sw, ne);
        }
        return bounds;
    }

    private void refreshClusterManager(LatLngBounds.Builder builder) {
        Log.d(TAG, "refreshClusterManager");
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(NORMAL_ZOOM_LEVEL), ANIMATION_DURATION, null);
        LatLngBounds latLngBounds = builder.build();
        LatLngBounds minLatLngBounds = adjustBoundsForMinimumLatitudeDegrees(latLngBounds, 0.5);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(minLatLngBounds, PADDING));
    }

    public void setCheckBox() {
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    Log.d(TAG, "CheckBox is checked !");
                    if(mroRequestOpenedList.size() == 0){
                        loadAndDisplayOpenedMroRequest();
                    }
                    else{
                        displayAllMroRequests();
                    }
                } else {
                    Log.d(TAG, "CheckBox is not checked !");
                    displayMroRequests(mroRequestAssignedList);
                }
            }
        });
    }

    // Check if any marker is displayed on given coordinate. If yes then decide
    // another appropriate coordinate to display this marker. It returns an
    // array with latitude(at index 0) and longitude(at index 1).
    private LatLng coordinateForMarker(double latitude, double longitude) {
        Log.d(TAG, "Avant: coordinateForMarker: latitude=" + latitude + " longitude=" + longitude);

        String[] location = new String[2];

        for (int i = 0; i <= MAX_NUMBER_OF_MARKERS; i++) {

            if (mapAlreadyHasMarkerForLocation((latitude + i
                    * COORDINATE_OFFSET)
                    + "," + (longitude + i * COORDINATE_OFFSET))) {

                // If i = 0 then below if condition is same as upper one. Hence, no need to execute below if condition.
                if (i == 0)
                    continue;

                if (mapAlreadyHasMarkerForLocation((latitude - i
                        * COORDINATE_OFFSET)
                        + "," + (longitude - i * COORDINATE_OFFSET))) {

                    continue;

                } else {
                    location[0] = latitude - (i * COORDINATE_OFFSET) + "";
                    location[1] = longitude - (i * COORDINATE_OFFSET) + "";
                    break;
                }

            } else {
                location[0] = latitude + (i * COORDINATE_OFFSET) + "";
                location[1] = longitude + (i * COORDINATE_OFFSET) + "";
                break;
            }
        }
        LatLng latLng = new LatLng(Double.parseDouble(location[0]), Double.parseDouble(location[1]));
        Log.d(TAG, "Après: coordinateForMarker: latitude=" + Double.parseDouble(location[0]) + " longitude=" + Double.parseDouble(location[1]));

        return latLng;
    }

    // Return whether marker with same location is already on map
    private boolean mapAlreadyHasMarkerForLocation(String location) {
        Log.d(TAG, "mapAlreadyHasMarkerForLocation: location=" + location);
        HashMap<String, String> initialMarkerLocationHashMap = new HashMap<>();
        initialMarkerLocationHashMap.putAll(assignedMarkerLocationHashMap);
        initialMarkerLocationHashMap.putAll(openedMarkerLocationHashMap);
        for(Map.Entry<String, String> e : initialMarkerLocationHashMap.entrySet()) {
            String key = e.getKey();
            String value = e.getValue();

            Log.d(TAG, "mapAlreadyHasMarkerForLocation: key=" + key + " value=" + value);
        }

        boolean returnValue = initialMarkerLocationHashMap.containsValue(location);
        Log.d(TAG, "mapAlreadyHasMarkerForLocation: returnValue=" + returnValue);

        return (initialMarkerLocationHashMap.containsValue(location));
    }
}