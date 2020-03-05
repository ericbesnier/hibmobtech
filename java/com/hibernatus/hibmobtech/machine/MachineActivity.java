package com.hibernatus.hibmobtech.machine;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hibernatus.hibmobtech.ActivityRefresher;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.HibmobtechOptionsMenuActivity;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.model.Machine;
import com.hibernatus.hibmobtech.model.MroRequest;
import com.hibernatus.hibmobtech.model.Site;
import com.hibernatus.hibmobtech.mrorequest.MroRequestActivity;
import com.hibernatus.hibmobtech.mrorequest.MroRequestCurrent;
import com.hibernatus.hibmobtech.network.retrofitError.DisplayRetrofitErrorManager;
import com.hibernatus.hibmobtech.site.SiteCurrent;
import com.hibernatus.hibmobtech.utils.ColorGenerator;
import com.hibernatus.hibmobtech.view.TextDrawable;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

// c l a s s   M a c h i n e A c t i v i t y
// -----------------------------------------
public class MachineActivity extends HibmobtechOptionsMenuActivity implements ActivityRefresher {
    public static final String TAG = MachineActivity.class.getSimpleName();
    public static final String ACTIVITY_FILTER = MachineActivity.class.getName();

    protected List<MroRequest> mroRequestList = Collections.emptyList();

    Activity activity;

    private Machine currentMachine;
    private Long currentMachineId;
    private Site currentSite;
    private Long currentSiteId;

    protected RecyclerView machineMroRequestRecyclerView;
    protected TextView machineSiteNameTextView;
    protected ImageView machineAvatarImageView;
    protected TextView machineReferenceTextView;
    protected TextView machineCategoryTextView;
    protected TextView machineSerialNumberTextView;
    protected TextView machineBrandTextView;
    protected TextView machineInstallDateTextView;
    protected TextView machinePurchaseDateTextView;

    public DisplayRetrofitErrorManager displayRetrofitErrorManager;

    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------


    @Override
    public DisplayRetrofitErrorManager getDisplayRetrofitErrorManager() {
        return displayRetrofitErrorManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);

        displayRetrofitErrorManager = getDisplayRetrofitErrorManager();
        activity = this;
        currentMachine = MachineCurrent.getInstance().getMachineCurrent();
        if(currentMachine != null) currentMachineId = currentMachine.getId();
        else
            Log.e(TAG, "ERROR currentMachine == null ");

        setContentView(R.layout.machine_activity);
        initToolBar();
        initDrawer();
        setRecyclerView();

        currentSite = SiteCurrent.getInstance().getSiteCurrent();
        if(currentSite != null){
            currentSiteId = currentSite.getId();
            loadMachine(currentSiteId);
        }
        else {
            Log.e(TAG, "ERROR onCreate: currentSite == null ");
        }
    }

    @Override
    protected void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        trackScreen(TAG);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
        currentMachine = MachineCurrent.getInstance().getMachineCurrent();
        if(currentMachine != null) {
            setMachineView(currentMachine);
        }
        else {
            Log.d(TAG, "onRestart: currentMachine is NULL");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.mainMenuDeleteItem).setVisible(true);
        menu.findItem(R.id.mainMenuUpdateItem).setVisible(true);
        menu.findItem(R.id.mainMenuCheckItem).setVisible(false);
        menu.findItem(R.id.mainMenuSiteItem).setVisible(false);
        menu.findItem(R.id.mainMenuEquipmentItem).setVisible(false);
        return true;
    }

    @Override
    public void refresh() {
        if(currentSiteId != null)
            loadMachine(currentSiteId);
        else
            Log.e(TAG, "ERROR onCreate: currentSiteId == null ");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i(TAG, "onOptionsItemSelected: case home: getClass=" + getClass());
                finish();
                return true;
            case R.id.mainMenuDeleteItem:
                Log.i(TAG, "onOptionsItemSelected: mainMenuDeleteItem");
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int choice) {
                        switch (choice) {
                            case DialogInterface.BUTTON_POSITIVE:

                                MachineApiService service = HibmobtechApplication.getRestClient().getMachineService();
                                Log.i(TAG, "mainMenuDeleteItem:siteId=" + SiteCurrent.getInstance().getSiteCurrent().getId()
                                        + " machineId=" +  currentMachine.getId());
                                service.deleteMachine(SiteCurrent.getInstance().getSiteCurrent().getId(),
                                        currentMachine.getId(), new Callback<Void>() {

                                            @Override
                                            public void success(Void aVoid, Response response) {
                                                if(!MachineActivity.this.isFinishing())
                                                    Toast.makeText(MachineActivity.this,
                                                            "Suppression de la machine "
                                                                    + currentMachine.getSerialNumber()
                                                                    + " enregistrée avec succès",
                                                            Toast.LENGTH_SHORT).show();
                                                finish();
                                            }

                                            @Override
                                            public void failure(RetrofitError error) {
                                                setRetrofitError(error);

                                            }
                                        });
                                break;
                            case DialogInterface.BUTTON_NEGATIVE:
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(MachineActivity.this);
                if(!this.isFinishing()) {
                    //show dialog
                    builder.setMessage("Voulez-vous supprimer la machine " + currentMachine.getSerialNumber() + " ?")
                            .setPositiveButton("Oui", dialogClickListener)
                            .setNegativeButton("Non", dialogClickListener).show();
                }

                break;
            case R.id.mainMenuUpdateItem:
                Log.i(TAG, "onOptionsItemSelected: mainMenuUpdateItem");
                Intent intent = new Intent(getApplicationContext(), MachineUpdateActivity.class);
                MachineCurrent.getInstance().setMachineCurrent(currentMachine);
                startActivity(intent);
                break;
            default:
                return true;
        }
        return true;
    }

    // O t h e r s   m e t h o d s
    // ---------------------------
    protected void loadMachine(long siteId) {
        Log.i(TAG, "loadMachine");
        MachineApiService service = HibmobtechApplication.getRestClient().getMachineService();
        service.getSiteMachine(siteId, currentMachineId, new Callback<Machine>() {
            @Override
            public void success(Machine machine, retrofit.client.Response response) {
                if(machine == null) return;

                mroRequestList = machine.getMros();
                machineMroRequestRecyclerView.setAdapter(new RecyclerViewAdapter(
                        getApplicationContext(),
                        machine.getMros(), activity));
                Log.i(TAG, "notifyUserOfNewRequests: mroRequestList=" + mroRequestList);

                setMachineView(machine);

                Log.i(TAG, "onCreate: updatedMachine.id=" + currentMachine.getId() + " Nb mros=" + mroRequestList.size());
            }

            @Override
            public void failure(RetrofitError error) {
                setRetrofitError(error);
            }
        });
    }

    protected void setRecyclerView() {
        machineMroRequestRecyclerView = (RecyclerView) findViewById(R.id.machineMroRequestRecyclerView);
        machineMroRequestRecyclerView.setLayoutManager(new LinearLayoutManager(machineMroRequestRecyclerView.getContext()));
        machineMroRequestRecyclerView.setAdapter(new RecyclerViewAdapter(getApplicationContext(),mroRequestList, activity));
    }

    public void setMachineView(Machine machine) {
        // avatar equipment
        String firstLetter;
        if (machine.getEquipment().getCategory() == null) {
            firstLetter = "?";
        } else {
            firstLetter = String.valueOf(machine.getEquipment().getCategory().charAt(0));
        }
        Log.i(TAG, "onBindViewHolder: firstLetter=" + firstLetter);
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color = generator.getColor(machine.getEquipment().getCategory());
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(firstLetter, color); // radius in px
        machineAvatarImageView = (ImageView) findViewById(R.id.machineAvatarImageView);
        machineAvatarImageView.setImageDrawable(drawable);

        // currentSite name
        machineSiteNameTextView = (TextView) findViewById(R.id.machineActivitySiteNameTextView);
        machineSiteNameTextView.setText(machine.getSite().getName());

        // reference equipment
        machineReferenceTextView = (TextView) findViewById(R.id.machineReferenceTextView);
        machineReferenceTextView.setText(machine.getEquipment().getReference());

        // category equipment
        machineCategoryTextView = (TextView) findViewById(R.id.machineCategoryTextView);
        machineCategoryTextView.setText(machine.getEquipment().getCategory());

        // brand equipment
        machineBrandTextView = (TextView) findViewById(R.id.machineBrandTextView);
        machineBrandTextView.setText(machine.getEquipment().getBrand());

        // updatedMachine serial number
        machineSerialNumberTextView = (TextView) findViewById(R.id.machineSerialNumberTextView);
        if (machine.getSerialNumber() == null) {
            machineSerialNumberTextView.setText("<?>");
        } else {
            machineSerialNumberTextView.setText(machine.getSerialNumber());
        }

        // InstallDate
        machineInstallDateTextView = (TextView) findViewById(R.id.machineInstallDateTextView);
        if (machine.getInstallDate() == null) {
            machineInstallDateTextView.setText("<?>");
        } else {
            SimpleDateFormat machineInstallDateSdf = new SimpleDateFormat("dd MMM yyyy");
            String dateString = machineInstallDateSdf.format(machine.getInstallDate());
            machineInstallDateTextView.setText(dateString);
        }

        // PurchaseDate
        machinePurchaseDateTextView = (TextView) findViewById(R.id.machinePurchaseDateTextView);
        if (machine.getPurchaseDate() == null) {
            machinePurchaseDateTextView.setText("<?>");
        } else {
            SimpleDateFormat machinePurchaseDateSdf = new SimpleDateFormat("dd MMM yyyy");
            String dateString = machinePurchaseDateSdf.format(machine.getPurchaseDate());
            machinePurchaseDateTextView.setText(dateString);
        }
    }

    // c l a s s   R e c y c l e r V i e w A d a p t e r
    // -------------------------------------------------
    public static class RecyclerViewAdapter
            extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

        private final TypedValue typedValue = new TypedValue();
        private int background;
        private List<MroRequest> mroRequestList;
        Context context;
        private Activity activity;

        public RecyclerViewAdapter(
                Context context,
                List<MroRequest> mroRequestList, Activity activity) {
            this.activity = activity;
            this.mroRequestList = mroRequestList;
            this.context = context;
            Log.i(TAG, "RecyclerViewAdapter: mroRequestList=" + mroRequestList);
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, typedValue, true);
            background = typedValue.resourceId;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.i(TAG, "onCreateViewHolder");
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.mro_request_list_light_row, parent, false);
            Log.i(TAG, "onCreateViewHolder: view=" + view);
            view.setBackgroundResource(background);
            return new ViewHolder(view, mroRequestList);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
            Log.i(TAG, "onBindViewHolder: position=" + position + " mroRequestList.size()=" + mroRequestList.size());

            final MroRequest mroRequest = mroRequestList.get(position);
            Log.i(TAG, "onBindViewHolder: mroRequestCurrent=" + mroRequest + "mroRequestCurrent.getId()" + mroRequest.getId());
            HibmobtechOptionsMenuActivity act = (HibmobtechOptionsMenuActivity)activity;
            act.setBackgroundColor(mroRequest, viewHolder.mroRequestListLightRowLinearLayout);
            viewHolder.mroRequestListLightId.setText(mroRequest.getId().toString());
            SimpleDateFormat mroRequestDateSdf = new SimpleDateFormat("dd MMM yyyy");
            Date date = mroRequest.getRequestDate();
            if(date != null) {
                String mroRequestDateString = mroRequestDateSdf.format(date);
                viewHolder.mroRequestListLightDate.setText(mroRequestDateString);
            }
            viewHolder.mroRequestListLightNote.setText(mroRequest.getNote());
            viewHolder.mroRequestListLightStatus.setText(mroRequest.getStatus().toString());

            Log.i(TAG, "setOnClickListener: view=" + viewHolder.view);
            viewHolder.view.setClickable(true);

            MroRequest mroRequestCurrent = MroRequestCurrent.getInstance().getMroRequestCurrent();
            Log.d(TAG, "setOnClickListener: mroRequestCurrent=" + mroRequestCurrent);

            if(mroRequestCurrent != null){
                if (mroRequest.getId().equals(MroRequestCurrent.getInstance().getMroRequestCurrent().getId())) {
                    viewHolder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.finish();
                        }
                    });
                }
                else {
                    viewHolder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Context context = v.getContext();
                            Intent intent = new Intent(context, MroRequestActivity.class);

                            Log.d(TAG, "setOnClickListener: mroRequestCurrent.getId()=" + mroRequest.getId());
                            intent.putExtra(MroRequest.MRO_REQUEST_ID_KEY, mroRequest.getId());
                            context.startActivity(intent);
                        }
                    });
                }
            }
            else{
                Log.e(TAG, "setOnClickListener: mroRequestCurrent = null !");
            }

        }

        @Override
        public int getItemCount() {
            return mroRequestList.size();
        }

        // c l a s s   V i e w H o l d e r
        // -------------------------------

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View view;
            public LinearLayout mroRequestListLightRowLinearLayout;
            public TextView mroRequestListLightId;
            public TextView mroRequestListLightDate;
            public TextView mroRequestListLightNote;
            public TextView mroRequestListLightStatus;
            private List<MroRequest> mroRequestList;

            public ViewHolder(View itemView, List<MroRequest> mroRequestList) {
                super(itemView);
                this.view = itemView;
                this.mroRequestList = mroRequestList;
                Log.i(TAG, "ViewHolder: view=" + view);
                mroRequestListLightRowLinearLayout = (LinearLayout) itemView.findViewById(R.id.mroRequestListLightRowLinearLayout);
                mroRequestListLightId = (TextView) itemView.findViewById(R.id.mroRequestListLightId);
                mroRequestListLightDate = (TextView) itemView.findViewById(R.id.mroRequestListLightDate);
                mroRequestListLightNote = (TextView) itemView.findViewById(R.id.mroRequestListLightNote);
                mroRequestListLightStatus = (TextView) itemView.findViewById(R.id.mroRequestListLightStatus);
                itemView.setTag(itemView);
            }
        }
    }
}