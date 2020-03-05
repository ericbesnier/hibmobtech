package com.hibernatus.hibmobtech.machine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.hibernatus.hibmobtech.ActivityCurrent;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.model.Machine;
import com.hibernatus.hibmobtech.site.SiteActivity;

import retrofit.Callback;
import retrofit.RetrofitError;

public class MachineUpdateActivity extends MachineCrudActivity {
    public static final String TAG = "MachineUpdateActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        Machine machineCurrent = MachineCurrent.getInstance().getMachineCurrent();
        super.onCreate(savedInstanceState);
        ActivityCurrent.getInstance().setActivityCurrent(this);
        ActivityCurrent.getInstance().setIsCurrentActivity(true);
        ActivityCurrent.getInstance().setActivityName(HibmobtechApplication.MACHINE_UPDATE_ACTIVITY);

        setMachineView(machineCurrent);
        setEquipmentView(machineCurrent.getEquipment());
        setListeners(machineCurrent);
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

    @Override
    void saveMachine(Long siteId, Machine machine) {
        Log.d(TAG, "saveMachine");
        MachineApiService service = HibmobtechApplication.getRestClient().getMachineService();
        service.createMachine(siteId, machine, new Callback<Machine>() {
            // TODO corriger le pb de updateMachine : Rest error: retrofit.RetrofitError: 400 Bad Request
            // "message":"org.hibernate.TransientPropertyValueException: object references an unsaved transient instance - save the transient instance before flushing : com.bleau.hibernatus.model.Machine.site -> com.bleau.hibernatus.model.Site; nested exception is java.lang.IllegalStateException: org.hibernate.TransientPropertyValueException: object references an unsaved transient instance - save the transient instance before flushing : com.bleau.hibernatus.model.Machine.site -> com.bleau.hibernatus.model.Site","code":0}
            // service.updateMachine(siteId, machine.getId(), machine, new Callback<Machine>() {
            @Override
            public void success(Machine machine, retrofit.client.Response response) {
                if(machine == null) return;

                MachineCurrent.getInstance().setMachineCurrent(machine);
                if(!MachineUpdateActivity.this.isFinishing())
                    Toast.makeText(MachineUpdateActivity.this, "Machine modifiée avec succès", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onClick Save Button: parentActivityName=" + parentActivityName
                        + " MachineAssociateActivity.class.getSimpleName()=" + MachineAssociateActivity.class.getSimpleName());
                setResult(Activity.RESULT_OK);
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "failure : " + error.toString());
                if(!MachineUpdateActivity.this.isFinishing())
                    Toast.makeText(MachineUpdateActivity.this,
                            "Echec de modification de la machine !", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), SiteActivity.class);
                startActivity(intent);
            }
        });
    }
}
