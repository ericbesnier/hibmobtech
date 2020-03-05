package com.hibernatus.hibmobtech.machine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hibernatus.hibmobtech.ActivityCurrent;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.equipment.EquipmentCurrent;
import com.hibernatus.hibmobtech.model.Machine;
import com.hibernatus.hibmobtech.site.SiteActivity;

import retrofit.Callback;
import retrofit.RetrofitError;

public class MachineCreateActivity extends MachineCrudActivity {
    public static final String TAG = MachineCreateActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        Machine machine = MachineCurrent.getInstance().getMachineCurrent();
        if(machine == null){
            machine = new Machine();
            MachineCurrent.getInstance().setMachineCurrent(machine);
        }

        super.onCreate(savedInstanceState);

        ActivityCurrent.getInstance().setActivityCurrent(this);
        ActivityCurrent.getInstance().setIsCurrentActivity(true);
        ActivityCurrent.getInstance().setActivityName(HibmobtechApplication.MACHINE_CREATE_ACTIVITY);

        if(EquipmentCurrent.getInstance() != null){
            setEquipmentView(EquipmentCurrent.getInstance().getEquipmentCurrent());
        }
        else{
            setEquipmentView(machine.getEquipment());
        }
        setMachineView(machine);
        setListeners(machine);
    }

    @Override
    public void onResume() {
        super.onResume();
        trackScreen(TAG);
    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart");
        super.onRestart();
    }

    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    void saveMachine(Long siteId, Machine machine) {
        Log.d(TAG, "saveMachine");
        MachineApiService service = HibmobtechApplication.getRestClient().getMachineService();
        service.createMachine(siteId, machine, new Callback<Machine>() {

            @Override
            public void success(Machine machine, retrofit.client.Response response) {
                if(machine == null) return;

                MachineCurrent.getInstance().setMachineCurrent(machine);
                if(!MachineCreateActivity.this.isFinishing())
                    Toast.makeText(MachineCreateActivity.this, "Machine créée avec succès", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onClick Save Button: parentActivityName=" + parentActivityName
                        + " MachineAssociateActivity.class.getSimpleName()=" + MachineAssociateActivity.class.getSimpleName());
                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "failure : " + error.toString());
                if(!MachineCreateActivity.this.isFinishing())
                    Toast.makeText(MachineCreateActivity.this,
                            "Echec de création de la machine !", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), SiteActivity.class);
                startActivity(intent);
            }
        });
    }
}
