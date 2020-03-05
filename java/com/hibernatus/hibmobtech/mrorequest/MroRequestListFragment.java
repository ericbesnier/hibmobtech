package com.hibernatus.hibmobtech.mrorequest;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hibernatus.hibmobtech.ActivityRefresher;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.MainActivity;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.model.Machine;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.model.Operation;
import com.hibernatus.hibmobtech.model.Site;
import com.hibernatus.hibmobtech.model.SpareParts;
import com.hibernatus.hibmobtech.mrorequest.mroRequestLoader.MroRequestAssignedLoader;
import com.hibernatus.hibmobtech.mrorequest.mroRequestLoader.MroRequestFinishedLoader;
import com.hibernatus.hibmobtech.mrorequest.mroRequestLoader.MroRequestLoader;
import com.hibernatus.hibmobtech.mrorequest.mroRequestLoader.MroRequestOpenedLoader;
import com.hibernatus.hibmobtech.observer.ServerDataObservable;
import com.hibernatus.hibmobtech.observer.ServerDataObserver;
import com.hibernatus.hibmobtech.receiver.NetworkStateChangeReceiver;
import com.hibernatus.hibmobtech.utils.ColorGenerator;
import com.hibernatus.hibmobtech.utils.FileUtils;
import com.hibernatus.hibmobtech.view.TextDrawable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import static android.app.Activity.RESULT_OK;
import static com.hibernatus.hibmobtech.mrorequest.MroRequestActivity.CURRENT_MRO_REQUEST_POSITION;
import static com.hibernatus.hibmobtech.mrorequest.MroRequestActivity.FRAGMENT_TYPE;
import static com.hibernatus.hibmobtech.receiver.NetworkStateChangeReceiver.IS_NETWORK_AVAILABLE;

// c l a s s   M r o R e q u e s t L i s t F r a g m e n t
// -------------------------------------------------------
public class MroRequestListFragment extends Fragment implements
        ActivityRefresher,
        ServerDataObserver {
    public static final String TAG = MroRequestListFragment.class.getSimpleName();
    public static final String ACTIVITY_FILTER = MroRequestListFragment.class.getName();
    private static final int REQUEST_ITEM_DETAILS = 150;

    protected RecyclerView recyclerView;
    protected RecyclerViewAdapter recyclerViewAdapter;
    protected String query; // Optional query
    protected List<MroRequest> mroRequestList = Collections.emptyList();;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected String fragmentType;
    protected Activity activity;
    protected View view;
    protected MroRequestLoader mroRequestLoader;
    protected Handler handler;
    protected ServerDataObservable serverDataObservable;
    protected String networkStatus;
    protected boolean isNetworkAvailable;


    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentType = getArguments().getString("FRAGMENT_TYPE");
        Log.d(TAG, fragmentType + " onCreateView: FRAGMENT_TYPE=" + fragmentType + "  this=" + this);
        handler = new Handler();
        activity = getActivity();

        // Broadcast Manager to receive notification when network state is changing
        IntentFilter intentFilter = new IntentFilter(NetworkStateChangeReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                boolean isNetworkAvailable = intent.getBooleanExtra(IS_NETWORK_AVAILABLE, false);
                if(isNetworkAvailable != MroRequestListFragment.this.isNetworkAvailable) {
                    MroRequestListFragment.this.isNetworkAvailable = isNetworkAvailable;
                    networkStatus = isNetworkAvailable ? "Connecté au réseau" : "Déconnecté du réseau";
                    Log.d(TAG, "onReceive: receive notification when network state is changing: networkStatus=" + networkStatus);
                    refreshItems();
                }
            }
        }, intentFilter);



        view = inflater.inflate(R.layout.mro_request_list_fragment, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.mroRequestRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new RecyclerViewAdapter(mroRequestList, handler, activity);
        recyclerView.setAdapter(recyclerViewAdapter);

        loadMroRequest();

        setSwipeRefreshLayout();

        return view;
    }

    @Override
    public void onStart(){
        Log.d(TAG, fragmentType + " onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, fragmentType + " onResume");
        super.onResume();
        HibmobtechApplication.getLocationEnable();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, fragmentType + " onActivityResult: resultCode=" + resultCode + " requestCode=" + requestCode);

        if (requestCode == REQUEST_ITEM_DETAILS && data != null) {
            if (resultCode == RESULT_OK) {
                int currentMroRequestPosition = data.getIntExtra(MroRequestActivity.CURRENT_MRO_REQUEST_POSITION, -1);
                if(currentMroRequestPosition == -1) {
                    Log.d(TAG, fragmentType + " onActivityResult: ERROR currentMroRequestPosition == -1");
                    return;
                }
                String receiveFragmentType = data.getStringExtra(MroRequestActivity.FRAGMENT_TYPE);
                MroRequest currentMroRequest = MroRequestCurrent.getInstance().getMroRequestCurrent();
                if(currentMroRequest == null)return;
                MroRequest.Status status = currentMroRequest.getStatus();

                Log.d(TAG, fragmentType + " onActivityResult: currentMroRequestPosition=" + currentMroRequestPosition
                        + " currentMroRequest=" + currentMroRequest
                        + " receiveFragmentType=" + receiveFragmentType
                        + " status=" + status);

                if(fragmentType == null) return;;

                switch (fragmentType) {
                    case "ASSIGNED": // on est dans le fragment des requests ASSIGNED
                        Log.d(TAG, fragmentType + " onActivityResult: from fragment ASSIGNED > status=" + status);
                        switch (status) {
                            case OPENED:
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case ASSIGNED: // operator has been assigned to the job
                                addMroRequestIfDontExist(currentMroRequestPosition, currentMroRequest);
                                updateMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case PLANNED: // work is planned
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case IN_PROGRESS: // work on site is on going
                                updateMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case FINISHED: // work on site is done
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case CLOSED: // billing done
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case CANCELED:
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            default:
                                Log.d(TAG, fragmentType + " setUIState: State unknown !");
                                break;
                        }
                        break;
                    case "OPENED": // on est dans le fragment des requests OPENED
                        Log.d(TAG, fragmentType + " onActivityResult: from fragment OPENED > status=" + status);
                        // case OPENED to ASSIGNED when operator take the request
                        // delete the request from fragment OPENED
                        switch (status) {
                            case OPENED:
                                updateMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case ASSIGNED: // operator has been assigned to the job
                                Log.d(TAG, fragmentType + " onActivityResult: delete the request from fragment OPENED");
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case PLANNED: // work is planned
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case IN_PROGRESS: // work on site is on going
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case FINISHED: // work on site is done
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case CLOSED: // billing done
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case CANCELED:
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            default:
                                Log.d(TAG, fragmentType + " setUIState: State unknown !");
                                break;
                        }
                        break;
                    case "FINISHED": // on est dans le fragment des requests FINISHED
                        Log.d(TAG, fragmentType + " onActivityResult: from fragment FINISHED > status=" + status);
                        // case FINISHED when operator modify the request
                        switch (status) {
                            case OPENED:
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case ASSIGNED: // operator has been assigned to the job
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case PLANNED: // work is planned
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case IN_PROGRESS: // work on site is on going
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case FINISHED: // work on site is done
                                addMroRequestIfDontExist(currentMroRequestPosition, currentMroRequest);
                                updateMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case CLOSED: // billing done
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            case CANCELED:
                                deleteMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
                                break;
                            default:
                                Log.d(TAG, fragmentType + " setUIState: State unknown !");
                                break;
                        }
                        break;
                    default:
                        Log.d(TAG, fragmentType + " onActivityResult: receiveFragmentType unknown !");
                        break;
                }
            }
        }
    }

    @Override
    public void refresh() {
        Log.d(TAG, fragmentType + " refresh");
    }


    // O t h e r s   m e t h o d s
    // ---------------------------

    public void updateMroRequestIfExist(int currentMroRequestPosition, MroRequest currentMroRequest) {
        Log.d(TAG, fragmentType + " updateMroRequestIfExist");
        if(recyclerViewAdapter != null) {
            recyclerViewAdapter.updateMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
        }
        else{
            Log.d(TAG, fragmentType + " onActivityResult: recyclerViewAdapter == null !");
        }
    }

    public void deleteMroRequestIfExist(int currentMroRequestPosition, MroRequest currentMroRequest) {
        Log.d(TAG, fragmentType + " removeMroRequestIfExist");
        if(recyclerViewAdapter != null) {
            recyclerViewAdapter.removeMroRequestIfExist(currentMroRequestPosition, currentMroRequest);
            recyclerViewAdapter.notifyItemChanged(currentMroRequestPosition);
        }
        else{
            Log.d(TAG, fragmentType + " onActivityResult: recyclerViewAdapter == null !");
        }
    }

    public void addMroRequestIfDontExist(int currentMroRequestPosition, MroRequest currentMroRequest) {
        Log.d(TAG, fragmentType + " addMroRequestIfDontExist");
        if(recyclerViewAdapter != null) {
            recyclerViewAdapter.addMroRequestIfDontExist(currentMroRequestPosition, currentMroRequest);
            recyclerViewAdapter.notifyItemChanged(currentMroRequestPosition);
        }
        else{
            Log.d(TAG, fragmentType + " onActivityResult: recyclerViewAdapter == null !");
        }
    }

    void setSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout)view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, fragmentType + " initSwipeRefreshLayout: swipeRefreshLayout.setRefreshing(true)");
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, fragmentType + " initSwipeRefreshLayout: onRefresh > swipeRefreshLayout.setRefreshing(true)");
                refreshItems();
            }
        });
    }

    void refreshItems() {
        Log.d(TAG, fragmentType + " refreshItems");
        FileUtils.setRefreshing(swipeRefreshLayout, true);
        loadMroRequest();
    }

    public void loadMroRequest() {
        Log.d(TAG, fragmentType + " loadMroRequest");
        if(fragmentType == null){
            Log.d(TAG, fragmentType + " loadMroRequest: fragmentType == null  !");
            return;
        }
        if(fragmentType.equals("OPENED")){
            this.mroRequestLoader = new MroRequestOpenedLoader();
        }
        if(fragmentType.equals("ASSIGNED")){
            this.mroRequestLoader = new MroRequestAssignedLoader();
        }
        if(fragmentType.equals("FINISHED")){
            this.mroRequestLoader = new MroRequestFinishedLoader();
        }

        Callback<List<MroRequest>> callback = new Callback<List<MroRequest>>() {
            @Override
            public void success(List<MroRequest> mroRequests, Response response) {

                if(mroRequests == null){
                    Log.e(TAG, fragmentType + " ERROR mroRequests == null");
                    return;
                }
                MroRequestListFragment.this.mroRequestList = mroRequests;

                if (mroRequestList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    Log.d(TAG, fragmentType + " loadMroRequest: success: mroRequestList=" + mroRequestList);

                    recyclerViewAdapter.setRecyclerViewAdapter(mroRequestList,
                            handler,
                            activity);

                    recyclerView.setAdapter(recyclerViewAdapter);
                }
                FileUtils.setRefreshing(swipeRefreshLayout, false);
            }

            @Override
            public void failure(RetrofitError error) {
                FileUtils.setRefreshing(swipeRefreshLayout, false);
                ((MainActivity)activity).setRetrofitError(error);
            }
        };

        mroRequestLoader.loadMroRequest(callback);
    }

    @Override
    public void update() {
        Log.d(TAG, "update");
        refreshItems();
    }

    @Override
    public void attachServerDataObservable(ServerDataObservable observable) {
        Log.d(TAG, "attachServerDataObservable: observable=" + observable);
        this.serverDataObservable = observable;
    }


    // c l a s s   R e c y c l e r V i e w A d a p t e r
    // -------------------------------------------------
    public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        private List<MroRequest> mroRequestList;
        Handler handler;
        private Activity activity;

        public RecyclerViewAdapter(List<MroRequest> mroRequestList, Handler handler, Activity activity) {
            Log.d(TAG, fragmentType + " RecyclerViewAdapter: mroRequestList=" + mroRequestList);
            this.mroRequestList = new ArrayList<>(mroRequestList);
            this.handler = handler;
            this.activity = activity;
        }

        public void setRecyclerViewAdapter(List<MroRequest> mroRequestList, Handler handler, Activity activity) {
            Log.d(TAG, fragmentType + " setRecyclerViewAdapter: mroRequestList=" + mroRequestList);
            this.mroRequestList = new ArrayList<>(mroRequestList);
            this.handler = handler;
            this.activity = activity;
        }

        public void updateMroRequestIfExist(int position, MroRequest mroRequest) {
            Log.d(TAG, fragmentType + " updateMroRequestIfExist: position=" + position + " mroRequest= " + mroRequest.getId());
            int i = 0;
            if(mroRequestList == null)return;
            if(position > mroRequestList.size()-1) return;
            for(MroRequest request : mroRequestList){
                Log.d(TAG, fragmentType + " updateMroRequestIfExist: mroRequest[" + i +"]= " + request.getId());
                if(request.getId().equals(mroRequest.getId())){
                    Log.d(TAG, fragmentType + " updateMroRequestIfExist: request exist > set request in list");
                    mroRequestList.set(position, mroRequest);
                    notifyItemChanged(position);
                    break;
                }
                i++;
            }
        }

        public void removeMroRequestIfExist(int position, MroRequest mroRequest) {
            Log.d(TAG, fragmentType + " removeMroRequestIfExist: mroRequest= " + mroRequest.getId());
            int i = 0;
            Iterator<MroRequest> iterator = mroRequestList.iterator();
            while (iterator.hasNext()) {
                MroRequest request = iterator.next();
                Log.d(TAG, fragmentType + " removeMroRequestIfExist: mroRequest[" + i + "]= " + request.getId());
                if(request.getId().equals(mroRequest.getId())){
                    Log.d(TAG, fragmentType + " removeMroRequestIfExist: request exist > remove request from list");
                    iterator.remove();
                    notifyItemRemoved(position);
                    //this line below gives you the animation and also updates the
                    //list items after the deleted item
                    notifyItemRangeChanged(position, getItemCount());
                    break;
                }
                i++;
            }
        }

        public void addMroRequestIfDontExist(int position, MroRequest mroRequest) {
            Log.d(TAG, fragmentType + " addMroRequestIfDontExist");
            if (mroRequest == null)return;
            Log.d(TAG, fragmentType + " addMroRequestIfDontExist: mroRequest= " + mroRequest.getId());
            int i = 0;
            if (mroRequestList == null)return;
            for(MroRequest request : mroRequestList){
                Log.d(TAG, fragmentType + " addMroRequestIfDontExist: mroRequest[" + i +"]= " + request.getId());
                if(request.getId().equals(mroRequest.getId())){
                    return;
                }
                i++;
            }
            Log.d(TAG, fragmentType + " updateMroRequestIfExist: request does not exist > add request on list");
            mroRequestList.add(0, mroRequest);
            notifyItemInserted(position);
            //this line below gives you the animation and also updates the
            //list items after the deleted item
            notifyItemRangeChanged(position, getItemCount());
        }

        // c l a s s   V i e w H o l d e r
        // --------------------------------

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View view;
            private List<MroRequest> mroRequestList;
            Handler handler;
            Activity activity;

            public LinearLayout mroRequestHeaderLinearLayout3;
            public LinearLayout mroRequestHeaderDateLinearLayout;
            public ImageView mroRequestWarningInvoiceImageViewIconError;

            // Mro Request
            public TextView mroRequestHeaderOperationDateTextView;
            protected TextView mroRequestInfoCircleIcon;
            public TextView mroRequestHeaderSiteNameTextView;
            public TextView mroRequestHeaderAdress1TextView;
            public TextView mroRequestHeaderAdress2TextView;
            public TextView mroRequestHeaderReferenceTextView;
            public LinearLayout mroRequestListRowSymptomLinearLayout;
            public TextView mroRequestListRowSymptomTextView;

            // Machines
            public LinearLayout machineListRowLinearLayout2;
            public LinearLayout machineListRowLinearLayout3;
            public ImageView machineListRowAvatarImageView;
            public LinearLayout machineListRowLeftVerticalLinearLayout;
            public LinearLayout machineListRowRightVerticalLinearLayout;
            public TextView machineListRowReferenceTextView;
            public TextView machineListRowCategoryTextView;
            public TextView machineListRowBrandTextView;
            public TextView machineListRowTitleSerialNumberTextView;
            public TextView machineListRowTitlePurchaseDateTextView;
            public TextView machineListRowSerialNumberTextView;
            public TextView machineListRowPurchaseDateTextView;

            // C o n s t r u t o r   V i e w H o l d e r
            // -----------------------------------------
            public ViewHolder(View view, List<MroRequest> mroRequestList, Handler handler, Activity activity) {
                super(view);
                this.view = view;
                this.mroRequestList = mroRequestList;
                this.handler = handler;
                this.activity = activity;
                Log.d(TAG, fragmentType + " ViewHolder");

                mroRequestHeaderLinearLayout3 = (LinearLayout) view.findViewById(R.id.mroRequestHeaderLinearLayout3);

                // Header
                mroRequestHeaderDateLinearLayout = (LinearLayout) view.findViewById(R.id.mroRequestHeaderDateLinearLayout);
                mroRequestWarningInvoiceImageViewIconError = (ImageView) view.findViewById(R.id.mroRequestWarningInvoiceImageViewIconError);
                mroRequestWarningInvoiceImageViewIconError.setVisibility(View.GONE);

                // Site
                mroRequestHeaderSiteNameTextView = (TextView) view.findViewById(R.id.mroRequestSiteNameTextView);
                mroRequestHeaderAdress1TextView = (TextView) view.findViewById(R.id.mroRequestSiteAdress1TextView);
                mroRequestHeaderAdress2TextView = (TextView) view.findViewById(R.id.mroRequestSiteAdress2TextView);

                // MroRequest
                mroRequestHeaderOperationDateTextView = (TextView) view.findViewById(R.id.mroRequestHeaderOperationDateTextView);
                mroRequestInfoCircleIcon = (TextView)view.findViewById(R.id.icon_fa_info_circle);
                mroRequestInfoCircleIcon.setVisibility(View.GONE);

                mroRequestHeaderReferenceTextView = (TextView) view.findViewById(R.id.mroRequestReferenceTextView);
                mroRequestListRowSymptomLinearLayout = (LinearLayout) view.findViewById(R.id.mroRequestListRowSymptomLinearLayout);
                mroRequestListRowSymptomTextView = (TextView) view.findViewById(R.id.mroRequestListRowSymptomTextView);

                // Machine
                machineListRowLinearLayout2 = (LinearLayout) view.findViewById(R.id.machineListRowLinearLayout2);
                machineListRowLinearLayout3 = (LinearLayout) view.findViewById(R.id.machineListRowLinearLayout3);
                machineListRowAvatarImageView = (ImageView) view.findViewById(R.id.machineListRowAvatarImageView);
                machineListRowLeftVerticalLinearLayout = (LinearLayout) view.findViewById(R.id.machineListRowLeftVerticalLinearLayout);
                machineListRowRightVerticalLinearLayout = (LinearLayout) view.findViewById(R.id.machineListRowRightVerticalLinearLayout);
                machineListRowReferenceTextView = (TextView) view.findViewById(R.id.machineListRowReferenceTextView);
                machineListRowCategoryTextView = (TextView) view.findViewById(R.id.machineListRowCategoryTextView);
                machineListRowBrandTextView = (TextView) view.findViewById(R.id.machineListRowBrandTextView);
                machineListRowTitleSerialNumberTextView = (TextView) view.findViewById(R.id.machineListRowTitleSerialNumberTextView);
                machineListRowTitlePurchaseDateTextView = (TextView) view.findViewById(R.id.machineListRowTitlePurchaseDateTextView);
                machineListRowSerialNumberTextView = (TextView) view.findViewById(R.id.machineListRowSerialNumberTextView);
                machineListRowPurchaseDateTextView = (TextView) view.findViewById(R.id.machineListRowPurchaseDateTextView);
            }
        }

        // @ O v e r r i d e   m e t h o d s   c l a s  s   V i e w H o l d e r
        // --------------------------------------------------------------------

        @Override
        public int getItemCount() {
            Log.d(TAG, fragmentType + " getItemCount: mroRequestList.size()=" + mroRequestList.size());
            return mroRequestList.size();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d(TAG, fragmentType + " onCreateViewHolder");
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.mro_request_list_full_row, parent, false);
            return new ViewHolder(view, mroRequestList, handler, activity);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            Log.d(TAG, fragmentType + " onBindViewHolder: position=" + position + " mroRequestList.size()=" + mroRequestList.size());
            final MroRequest mroRequest = mroRequestList.get(position);

            // Header Background
            setHeaderBackgroundColor(mroRequest, viewHolder);

            // Site
            Site site = mroRequest.getSite();
            viewHolder.mroRequestHeaderSiteNameTextView.setText(site.getName());
            viewHolder.mroRequestHeaderAdress1TextView.setText(site.getAddress().getAdressPart1());
            viewHolder.mroRequestHeaderAdress2TextView.setText(site.getAddress().getAdressPart2());

            Log.d(TAG, fragmentType + " onBindViewHolder: mroRequest.getMroType()=" + mroRequest.getMroType());

            switch (mroRequest.getMroType()) {

                case PREVISIT:
                    setPrevisitMroRequest(mroRequest, viewHolder);
                    break;
                case INSTALLATION:
                    setInstallationMroRequest(mroRequest, viewHolder);
                    break;
                case MAINTENANCE:
                    setMaintenanceMroRequest(mroRequest, viewHolder);
                    break;
                case DELIVERY:
                    setDeliveryMroRequest(mroRequest, viewHolder);
                    break;
                default:
                    Log.e(TAG, fragmentType + " onBindViewHolder: mroRequest.getMroType() == ERROR Default");
                    break;
            }

            viewHolder.mroRequestHeaderReferenceTextView.setText(String.valueOf(mroRequest.getId()));
            viewHolder.mroRequestListRowSymptomLinearLayout.setVisibility(View.GONE);
            viewHolder.mroRequestListRowSymptomTextView.setText(mroRequest.getNote());

            // Machine
            setMachineListRow(mroRequest.getMachine(), viewHolder);

            Log.d(TAG, fragmentType + " setOnClickListener: view=" + viewHolder.view);
            viewHolder.view.setClickable(true);
            viewHolder.view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Log.d(TAG, fragmentType + " onClick: view=" + v);
                    final Context context = v.getContext();
                    if(mroRequest != null) {
                        MroRequestCurrent.getInstance().setMroRequestCurrent(mroRequest);
                    }
                    MroRequestApiService service = HibmobtechApplication.getRestClient().getMroRequestService();
                    service.getMroRequest(MroRequestCurrent.getInstance().getMroRequestCurrent()
                            .getId(), new Callback<MroRequest>() {
                        @Override
                        public void success(MroRequest mroRequest, retrofit.client.Response response) {
                            if (mroRequest != null) {
                                MroRequestCurrent.getInstance().setMroRequestCurrent(mroRequest);
                            }
                            Log.d(TAG, fragmentType + " onClick: getMroRequestCurrent().getId()="
                                    + MroRequestCurrent.getInstance().getMroRequestCurrent().getId()
                                    + " SpareParts="
                                    + MroRequestCurrent.getInstance().getMroRequestCurrent().getOperation().getSpareParts());
                            for (SpareParts spareParts : MroRequestCurrent.getInstance().getMroRequestCurrent().getOperation().getSpareParts()) {
                                Log.d(TAG, fragmentType + " onClick: SpareParts="
                                        + spareParts.getSparePart().getDescription());
                            }

                            Intent intent = new Intent(context, MroRequestActivity.class);
                            //intent.putExtra(MroRequest.MRO_REQUEST_ID_KEY, mroRequest.getEntityVersion());
                            intent.putExtra(CURRENT_MRO_REQUEST_POSITION, position);
                            intent.putExtra(FRAGMENT_TYPE, fragmentType);
                            activity.startActivityForResult(intent, REQUEST_ITEM_DETAILS);
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(TAG, fragmentType + " failure : " + error.toString());
                            ((MainActivity) activity).setRetrofitError(error);
                        }
                    });
                }
            });
        }

        // P r e v i s i t M r o R e q u e s t
        // -------------------------------------------

        void setPrevisitMroRequest(MroRequest mroRequest, ViewHolder viewHolder){
            setPrevisitBackgroundColor(viewHolder);
            setMroRequestMessageAndDateWithStatus(mroRequest, viewHolder);
        }

        void setPrevisitBackgroundColor(ViewHolder viewHolder) {
            Log.d(TAG, fragmentType + " setPrevisitBackgroundColor");
            int previsitColor = HibmobtechApplication.getInstance().getResources().getColor(R.color.previsit);
            viewHolder.machineListRowLinearLayout2.setBackgroundColor(previsitColor);
            viewHolder.machineListRowLinearLayout3.setVisibility(View.GONE);
            viewHolder.mroRequestHeaderLinearLayout3.setBackgroundColor(previsitColor);
        }

        void setDateMroRequest(MroRequest mroRequest, ViewHolder viewHolder, String message){
            Log.d(TAG, fragmentType + " setDateMroRequest");
            if(mroRequest != null) {
                SimpleDateFormat mroRequestDateSdf;
                SimpleDateFormat mroRequestHourSdf = new SimpleDateFormat("HH:mm");
                String mroRequestDateString;
                String mroRequestHourString;
                Date date = null;
                Operation operation = mroRequest.getOperation();;

                switch (mroRequest.getStatus()) {
                    case OPENED:
                        if(operation != null) {
                            date = operation.getDate(); // date de planification !
                        }
                        if(date == null)
                            date = mroRequest.getRequestDate(); // date de creation de la demande
                        break;
                    case ASSIGNED:
                        if (operation != null) {
                            date = operation.getDate(); // date de planification !
                        }
                        break;
                    case IN_PROGRESS:
                        if(operation != null) {
                            date = operation.getMroStartDate();
                        }
                        break;
                    case FINISHED:
                        if(operation != null) {
                            date = operation.getMroFinishDate();
                        }
                        break;
                    default:
                        Log.e(TAG, fragmentType + " setDateMroRequest: mroRequest.getStatus() == ERROR Default");
                        break;
                }

                mroRequestDateSdf = new SimpleDateFormat(message);

                if (date != null) {
                    mroRequestDateString = mroRequestDateSdf.format(date);
                    mroRequestHourString = mroRequestHourSdf.format(date);
                    if (mroRequestHourString.equals("00:00")) {
                        viewHolder.mroRequestHeaderOperationDateTextView.setText(mroRequestDateString);
                    } else {
                        viewHolder.mroRequestHeaderOperationDateTextView.setText(mroRequestDateString + " à " + mroRequestHourString);
                    }
                }
/*            if(operation != null) {
                Date date = operation.getDate();
                if (date != null) {
                    mroRequestDateString = mroRequestDateSdf.format(date);
                    mroRequestHourString = mroRequestHourSdf.format(date);
                    if (mroRequestHourString.equals("00:00")) {
                        viewHolder.mroRequestHeaderOperationDateTextView.setText(mroRequestDateString);
                    } else {
                        viewHolder.mroRequestHeaderOperationDateTextView.setText(mroRequestDateString + " à " + mroRequestHourString);
                    }
                }
            }*/
            }
        }



        // I n s t a l l a t i o n M r o R e q u e s t
        // -------------------------------------------

        void setInstallationMroRequest(MroRequest mroRequest, ViewHolder viewHolder){
            setInstallationBackgroundColor(viewHolder);
            setMroRequestMessageAndDateWithStatus(mroRequest, viewHolder);
        }

        void setInstallationBackgroundColor(ViewHolder viewHolder) {
            Log.d(TAG, fragmentType + " setInstallationBackgroundColor");
            int installationColor = HibmobtechApplication.getInstance().getResources().getColor(R.color.installation);
            viewHolder.machineListRowLinearLayout2.setBackgroundColor(installationColor);
            viewHolder.machineListRowLinearLayout3.setVisibility(View.GONE);
            viewHolder.mroRequestHeaderLinearLayout3.setBackgroundColor(installationColor);
        }


        // M a i n t e n a n c e M r o R e q u e s t
        // -------------------------------------------

        void setMaintenanceMroRequest(MroRequest mroRequest, ViewHolder viewHolder){
            setMaintenanceBackgroundColor(viewHolder);
            setMroRequestMessageAndDateWithStatus(mroRequest, viewHolder);
        }

        void setMaintenanceBackgroundColor(ViewHolder viewHolder) {
            Log.d(TAG, fragmentType + " setDeliveryBackgroundColor");
            int maintenanceColor = HibmobtechApplication.getInstance().getResources().getColor(R.color.md_white_1000);
            viewHolder.machineListRowLinearLayout2.setBackgroundColor(maintenanceColor);
            viewHolder.machineListRowLinearLayout3.setVisibility(View.VISIBLE);
            viewHolder.mroRequestHeaderLinearLayout3.setBackgroundColor(maintenanceColor);
        }


        // D e l i v e r y M r o R e q u e s t
        // -------------------------------------------

        void setDeliveryMroRequest(MroRequest mroRequest, ViewHolder viewHolder){
            setDeliveryBackgroundColor(viewHolder);
            setMroRequestMessageAndDateWithStatus(mroRequest, viewHolder);
        }

        void setDeliveryBackgroundColor(ViewHolder viewHolder) {
            Log.d(TAG, fragmentType + " setDeliveryBackgroundColor");
            int deliveryColor = HibmobtechApplication.getInstance().getResources().getColor(R.color.delivery);
            viewHolder.machineListRowLinearLayout2.setBackgroundColor(deliveryColor);
            viewHolder.machineListRowLinearLayout3.setVisibility(View.GONE);
            viewHolder.mroRequestHeaderLinearLayout3.setBackgroundColor(deliveryColor);
        }


        // O t h e r s   m e t h o d s   c l a s s   V i e w H o l d e r
        // --------------------------------------------------------------------

        void setMroRequestMessageAndDateWithStatus(MroRequest mroRequest, ViewHolder viewHolder){
            switch (mroRequest.getStatus()){
                case OPENED:
                    setDateMroRequest(mroRequest, viewHolder, "'Ouverte' dd MMM");
                    break;
                case ASSIGNED:
                    setDateMroRequest(mroRequest, viewHolder, "'Planifiée' dd MMM");
                    break;
                case IN_PROGRESS:
                    setDateMroRequest(mroRequest, viewHolder, "'Démarrée' dd MMM");
                    break;
                case FINISHED:
                    setDateMroRequest(mroRequest, viewHolder, "'Terminée' dd MMM");
                    break;
                default:
                    Log.e(TAG, fragmentType + " onBindViewHolder: mroRequest.getStatus() == ERROR Default");
                    break;
            }
        }

        void setHeaderBackgroundColor(MroRequest mroRequest, ViewHolder viewHolder) {
            Log.d(TAG, fragmentType + " setHeaderBackgroundColor: mroRequestCurrent.getId()=" + mroRequest.getId()
                    + " mroRequestCurrent.getStatus()=" + mroRequest.getStatus());
            int openedColor = HibmobtechApplication.getInstance().getResources().getColor(R.color.opened);
            int assignedColor = HibmobtechApplication.getInstance().getResources().getColor(R.color.assigned);
            int planedColor = HibmobtechApplication.getInstance().getResources().getColor(R.color.planed);
            int inProgressColor = HibmobtechApplication.getInstance().getResources().getColor(R.color.in_progress);
            int finishColor = HibmobtechApplication.getInstance().getResources().getColor(R.color.finished);
            int closedColor = HibmobtechApplication.getInstance().getResources().getColor(R.color.closed);
            int canceledColor = HibmobtechApplication.getInstance().getResources().getColor(R.color.canceled);
            int defaultColor = HibmobtechApplication.getInstance().getResources().getColor(R.color.md_white_1000);

            switch (mroRequest.getStatus()) {
                case OPENED:
                    Log.d(TAG, fragmentType + " setHeaderBackgroundColor: OPENED");
                    viewHolder.mroRequestHeaderDateLinearLayout.setBackgroundColor(openedColor);
                    break;
                case ASSIGNED: // operator has been assigned to the job
                    Log.d(TAG, fragmentType + " setHeaderBackgroundColor: ASSIGNED");
                    viewHolder.mroRequestHeaderDateLinearLayout.setBackgroundColor(assignedColor);
                    break;
                case PLANNED: // work is planned
                    Log.d(TAG, fragmentType + " setHeaderBackgroundColor: PLANNED");
                    viewHolder.mroRequestHeaderDateLinearLayout.setBackgroundColor(planedColor);
                    break;
                case IN_PROGRESS: // work on site is on going
                    Log.d(TAG, fragmentType + " setHeaderBackgroundColor: IN_PROGRESS");
                    viewHolder.mroRequestHeaderDateLinearLayout.setBackgroundColor(inProgressColor);
                    break;
                case FINISHED: // work on site is done
                    Log.d(TAG, fragmentType + " setHeaderBackgroundColor: FINISHED");
                    viewHolder.mroRequestHeaderDateLinearLayout.setBackgroundColor(finishColor);
                    break;
                case CLOSED: // billing done
                    Log.d(TAG, fragmentType + " setHeaderBackgroundColor: CLOSED");
                    viewHolder.mroRequestHeaderDateLinearLayout.setBackgroundColor(closedColor);
                    break;
                case CANCELED:
                    Log.d(TAG, fragmentType + " setHeaderBackgroundColor: CANCELED");
                    viewHolder.mroRequestHeaderDateLinearLayout.setBackgroundColor(canceledColor);
                    break;
                default:
                    Log.d(TAG, fragmentType + " onOptionsItemSelected: default");
                    viewHolder.mroRequestHeaderDateLinearLayout.setBackgroundColor(defaultColor);
                    break;
            }
        }

        Machine createUnknownMachine(){
            Log.d(TAG, fragmentType + " createUnknownMachine");
            Machine machine = new Machine();
            machine.getEquipment().setReference(HibmobtechApplication.UNKNOWN_MACHINE);
            machine.getEquipment().setBrand("<?>");
            machine.getEquipment().setCategory("<?>");
            machine.getEquipment().setDescription("<?>");
            machine.getEquipment().setFormattedPrice("<?>");
            machine.getEquipment().setHeating("<?>");
            machine.getEquipment().setModel("<?>");
            machine.setComments("<?>");
            machine.setNumMachine("<?>");
            machine.setSerialNumber("<?>");
            return machine;
        }

        void setMachineVisibility(ViewHolder viewHolder, int visibility){
            Log.d(TAG, fragmentType + " setMachineVisibility");
            //viewHolder.machineListRowReferenceTextView.setVisibility(visibility);
            viewHolder.machineListRowCategoryTextView.setVisibility(visibility);
            viewHolder.machineListRowBrandTextView.setVisibility(visibility);
            viewHolder.machineListRowTitleSerialNumberTextView.setVisibility(visibility);
            viewHolder.machineListRowSerialNumberTextView.setVisibility(visibility);
            viewHolder.machineListRowTitlePurchaseDateTextView.setVisibility(visibility);
            viewHolder.machineListRowPurchaseDateTextView.setVisibility(visibility);
        }

        void setUnknownMachine(Machine machine, ViewHolder viewHolder){
            Log.d(TAG, fragmentType + " setUnknownMachine");
            viewHolder.machineListRowRightVerticalLinearLayout.setVisibility(View.GONE);
            viewHolder.machineListRowReferenceTextView.setText(machine.getEquipment().getReference());
            setMachineVisibility(viewHolder, View.GONE);
        }

        void setMachineView(Machine machine, ViewHolder viewHolder){
            viewHolder.machineListRowRightVerticalLinearLayout.setVisibility(View.VISIBLE);
            if(machine.getEquipment() == null){
                Log.d(TAG, fragmentType + " setMachineView: machine.getEquipment() == null !");
                if(machine.getEquipment().getReference() == null){
                    Log.d(TAG, fragmentType + " setMachineView: machine.getEquipment().getReference() == null !");
                }
            }
            Log.d(TAG, fragmentType + " setMachineView: reference=" + machine.getEquipment().getReference());

            // reference equipment
            viewHolder.machineListRowReferenceTextView.setVisibility(View.VISIBLE);
            viewHolder.machineListRowReferenceTextView.setText(machine.getEquipment().getReference());

            // category equipment
            viewHolder.machineListRowCategoryTextView.setVisibility(View.VISIBLE);
            if (machine.getEquipment().getCategory() == null) {
                viewHolder.machineListRowCategoryTextView.setText("<?>");
            } else {
                viewHolder.machineListRowCategoryTextView.setText(machine.getEquipment().getCategory());
            }

            // brand equipment
            viewHolder.machineListRowBrandTextView.setVisibility(View.VISIBLE);
            if (machine.getEquipment().getBrand() == null) {
                viewHolder.machineListRowBrandTextView.setText("<?>");
            } else {
                viewHolder.machineListRowBrandTextView.setText(machine.getEquipment().getBrand());
            }

            // updatedMachine serial number
            viewHolder.machineListRowTitleSerialNumberTextView.setVisibility(View.VISIBLE);
            viewHolder.machineListRowSerialNumberTextView.setVisibility(View.VISIBLE);
            if (machine.getSerialNumber() == null) {
                viewHolder.machineListRowSerialNumberTextView.setText("<?>");
            } else {
                viewHolder.machineListRowSerialNumberTextView.setText(machine.getSerialNumber());
            }

            // updatedMachine purchase date
            viewHolder.machineListRowTitlePurchaseDateTextView.setVisibility(View.VISIBLE);
            viewHolder.machineListRowPurchaseDateTextView.setVisibility(View.VISIBLE);
            if (machine.getPurchaseDate() == null) {
                viewHolder.machineListRowPurchaseDateTextView.setText("<?>");
            } else {
                SimpleDateFormat machinePurchaseDateSdf = new SimpleDateFormat("dd MMM yyyy", Locale.FRANCE);
                String dateString = machinePurchaseDateSdf.format(machine.getPurchaseDate());
                viewHolder.machineListRowPurchaseDateTextView.setText(dateString);
            }
        }

        void setMachineListRow(Machine machine, ViewHolder viewHolder){
            Log.d(TAG, fragmentType + " setMachineListRow");
            if (machine == null) { // machine doesn't exist
                Log.d(TAG, fragmentType + " setMachineListRow: machine == null");
                machine = createUnknownMachine();
                setUnknownMachine(machine, viewHolder);
            }
            else if(machine.getEquipment().getReference().equals(HibmobtechApplication.UNKNOWN_MACHINE)){ // machine exist but is unknown
                Log.d(TAG, fragmentType + " setMachineListRow: UNKNOWN_MACHINE");
                setUnknownMachine(machine, viewHolder);
            }
            else { // machine is known
                Log.d(TAG, fragmentType + " setMachineListRow: MACHINE Known");
                setMachineView(machine, viewHolder);
            }

            // avatar equipment
            String firstLetter;
            if (machine.getEquipment().getCategory() != null) {
                if (machine.getEquipment().getCategory().equals("<?>")) {
                    firstLetter = "?";
                } else {
                    firstLetter = String.valueOf(machine.getEquipment().getCategory().charAt(0));
                }
            } else {
                firstLetter = "?";
            }

            Log.d(TAG, fragmentType + " setMachineListRow: firstLetter=" + firstLetter);
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            int color = generator.getColor(machine.getEquipment().getCategory());
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(firstLetter, color); // radius in px
            viewHolder.machineListRowAvatarImageView.setImageDrawable(drawable);
        }
    }
}