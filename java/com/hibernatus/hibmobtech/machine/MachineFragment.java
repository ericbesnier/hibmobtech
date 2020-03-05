package com.hibernatus.hibmobtech.machine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.equipment.EquipmentCurrent;
import com.hibernatus.hibmobtech.model.Machine;
import com.hibernatus.hibmobtech.mrorequest.MroRequestCurrent;
import com.hibernatus.hibmobtech.site.SiteApiService;
import com.hibernatus.hibmobtech.site.SiteCurrent;
import com.hibernatus.hibmobtech.utils.ColorGenerator;
import com.hibernatus.hibmobtech.view.TextDrawable;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;

// c l a s s   M a c h i n e F r a g m e n t
// -----------------------------------------
public class MachineFragment extends Fragment {
    public static final String TAG = MachineFragment.class.getSimpleName();
    protected String fragmentId;
    protected String parentClass;
    protected Activity activity;

    protected List<Machine> machineList = Collections.emptyList();

    protected View view;
    protected RecyclerView machineRecyclerView;
    public RecyclerViewAdapter recyclerViewAdapter;
    protected FloatingActionButton machineFloatingActionButton;

    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentId = getArguments().getString("fragmentType");
        parentClass = getArguments().getString("parentClass");
        activity = getActivity();
        Log.d(TAG, "onCreateView: fragmentType=" + fragmentId);

        if(SiteCurrent.getInstance().isCurrentSite()){
            view = inflater.inflate(R.layout.machine_recycler_view, container, false);
            findViews();
            setViews();
            recyclerViewAdapter = new RecyclerViewAdapter(
                    activity,
                    SiteCurrent.getInstance().getSiteCurrent().getId(),
                    machineList,
                    fragmentId,
                    parentClass);
            Log.d(TAG, "onCreateView: recyclerViewAdapter=" + recyclerViewAdapter);
            machineRecyclerView.setAdapter(recyclerViewAdapter);
            loadMachine(SiteCurrent.getInstance().getSiteCurrent().getId());
        }
        return view;
    }

    @Override
    public void onStart(){
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume(){
        Log.d(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause(){
        Log.d(TAG, "onPause");
        super.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.d(TAG, "onActivityResult: resultCode=" + resultCode + " requestCode=" + requestCode);

        if (requestCode == HibmobtechApplication.MACHINE_CREATE_REQUEST) {
            loadMachine(SiteCurrent.getInstance().getSiteCurrent().getId());
        }
    }

    // O t h e r s   m e t h o d s
    // ---------------------------

    protected void findViews() {
        machineRecyclerView = (RecyclerView)view.findViewById(R.id.machineRecyclerView);
        machineFloatingActionButton = (FloatingActionButton)view.findViewById(R.id.machineFloatingActionButton);
    }

    protected void setViews() {
        machineRecyclerView.setHasFixedSize(true);
        machineFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                EquipmentCurrent.getInstance().initEquipmentCurrent();
                Intent intent = new Intent(getContext(), MachineCreateActivity.class);
                getActivity().startActivityForResult(intent, HibmobtechApplication.MACHINE_CREATE_REQUEST);
            }
        });
    }

    public void loadMachine(final long siteId){
        Log.d(TAG, "loadMachine");
        machineRecyclerView.setLayoutManager(new LinearLayoutManager(machineRecyclerView.getContext()));
        SiteApiService service = HibmobtechApplication.getRestClient().getSiteService();

        service.getSiteMachines(siteId, new Callback<List<Machine>>() {
            @Override
            public void success(List<Machine> machines, retrofit.client.Response response) {
                if(machines == null) return;

                machineList = machines;

                recyclerViewAdapter = new RecyclerViewAdapter(
                        activity,
                        siteId,
                        machineList,
                        fragmentId,
                        parentClass);
                machineRecyclerView.setAdapter(recyclerViewAdapter);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "failure : " + error.toString());
            }
        });
    }

    // c l a s s   R e c y c l e r V i e w A d a p t e r
    // -------------------------------------------------
    public static class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
        String parentClass;
        String fragmentId;
        long siteId;
        private List<Machine> machineList;
        Activity activity;

        public RecyclerViewAdapter(
                Activity activity,
                long siteId,
                List<Machine> machineList,
                String fragmentId,
                String parentClass) {
            this.siteId = siteId;
            this.machineList = machineList;
            this.fragmentId = fragmentId;
            this.parentClass = parentClass;
            this.activity = activity;
        }

        // c l a s s   V i e w H o l d e r
        // -------------------------------
        public static class ViewHolder extends RecyclerView.ViewHolder {
            public final View view;
            private List<Machine> machineList;
            Activity activity;

            public ImageView machineListRowAvatarImageView;
            public TextView machineListRowReferenceTextView;
            public TextView machineListRowBrandTextView;
            public TextView machineListRowCategoryTextView;
            public TextView machineListRowSerialNumberTextView;
            public TextView machineListRowPurchaseDateTextView;

            public ViewHolder(View view, List<Machine> machineList, Activity activity) {
                super(view);
                this.view = view;
                this.machineList = machineList;
                this.activity = activity;

                machineListRowAvatarImageView = (ImageView) view.findViewById(R.id.machineListRowAvatarImageView);
                machineListRowReferenceTextView = (TextView) view.findViewById(R.id.machineListRowReferenceTextView);
                machineListRowCategoryTextView = (TextView) view.findViewById(R.id.machineListRowCategoryTextView);
                machineListRowBrandTextView = (TextView) view.findViewById(R.id.machineListRowBrandTextView);
                machineListRowSerialNumberTextView = (TextView) view.findViewById(R.id.machineListRowSerialNumberTextView);
                machineListRowPurchaseDateTextView = (TextView) view.findViewById(R.id.machineListRowPurchaseDateTextView);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.machine_list_row, parent, false);
            return new ViewHolder(view, machineList, activity);
        }

        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

            final Machine machine = machineList.get(position);

            // updatedMachine avatar
            String firstLetter;
            if(machine.getEquipment().getCategory() == null){
                firstLetter = "?";
            }
            else {
                firstLetter = String.valueOf(machine.getEquipment().getCategory().charAt(0));
            }
            //Log.i(TAG, "onBindViewHolder: firstLetter=" + firstLetter);
            ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
            int color = generator.getColor(machine.getEquipment().getCategory());
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(firstLetter, color); // radius in px
            viewHolder.machineListRowAvatarImageView.setImageDrawable(drawable);

            // reference equipment
            viewHolder.machineListRowReferenceTextView.setText(machine.getEquipment().getReference());

            // category equipment
            viewHolder.machineListRowCategoryTextView.setText(machine.getEquipment().getCategory());

            // brand equipment
            viewHolder.machineListRowBrandTextView.setText(machine.getEquipment().getBrand());

            // updatedMachine serial number
            if (machine.getSerialNumber() == null) {
                viewHolder.machineListRowSerialNumberTextView.setText("<?>");
            } else {
                viewHolder.machineListRowSerialNumberTextView.setText(machine.getSerialNumber());
            }

            // updatedMachine purchase date
            if(machine.getPurchaseDate() == null){
                viewHolder.machineListRowPurchaseDateTextView.setText("<?>");
            }
            else {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.FRANCE);
                String dateString = sdf.format(machine.getPurchaseDate());
                viewHolder.machineListRowPurchaseDateTextView.setText(dateString);
            }

            viewHolder.view.setClickable(true);
            viewHolder.view.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    //MachineCurrent.getInstance().setIsCurrentMachine(true);
                    MachineCurrent.getInstance().setMachineCurrent(machine);
                    if(parentClass.equals("SiteActivity")) {
                        Intent intent = new Intent(context, MachineActivity.class);
                        context.startActivity(intent);
                    }
                    if(parentClass.equals("MachineAssociateActivity")) {
                        MachineCurrent.getInstance().setMachineCurrent(machine);
                        Log.d(TAG, "MroRequestCurrent.getInstance().setIsMroRequestCheckable(true)");
                        MroRequestCurrent.getInstance().getMroRequestCurrent().setMachine(machine);
                        MroRequestCurrent.getInstance().setIsMroRequestCheckable(true);
                        activity.finish();
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return machineList.size();
        }
    }
}