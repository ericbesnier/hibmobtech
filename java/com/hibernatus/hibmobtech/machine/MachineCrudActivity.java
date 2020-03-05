package com.hibernatus.hibmobtech.machine;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.hibernatus.hibmobtech.ActivityCurrent;
import com.hibernatus.hibmobtech.HibmobtechApplication;
import com.hibernatus.hibmobtech.HibmobtechOptionsMenuActivity;
import com.hibernatus.hibmobtech.R;
import com.hibernatus.hibmobtech.equipment.EquipmentListActivity;
import com.hibernatus.hibmobtech.model.Equipment;
import com.hibernatus.hibmobtech.model.Machine;
import com.hibernatus.hibmobtech.model.Site;
import com.hibernatus.hibmobtech.site.SiteActivity;
import com.hibernatus.hibmobtech.site.SiteCurrent;
import com.hibernatus.hibmobtech.utils.ActionEditText;
import com.hibernatus.hibmobtech.zxing.AnyOrientationCaptureActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RetrofitError;

// c l a s s   M a c h i n e C r u d A c t i v i t y
// -------------------------------------------------
public abstract class MachineCrudActivity extends HibmobtechOptionsMenuActivity implements View.OnClickListener{
    public static final String TAG = MachineCrudActivity.class.getSimpleName();

    protected TextView machineCrudActivitySiteNameTextView;
    protected EditText machineCrudActivitySerialNumberEditText;
    protected ImageButton machineCrudActivityScannerButton;
    protected Button machineCrudActivityEquipmentButton;
    protected TextView machineCrudActivityReferenceTextView;
    protected TextView machineCrudActivityCategoryTextView;
    protected TextView machineCrudActivityBrandTextView;
    protected EditText machineCrudActivityPurchaseDateEditText;
    protected TextView machineCrudActivityPurchaseDateTextView;
    protected EditText machineCrudActivityInstallDateEditText;
    protected TextView machineCrudActivityInstallDateTextView;
    protected ActionEditText machineCrudActivityCommentsEditText;
    protected Button machineCrudActivitySaveButton;
    protected Button machineCrudActivityCancelButton;

    protected DatePickerDialog purchaseDatePickerDialog;
    protected DatePickerDialog installDatePickerDialog;

    protected SimpleDateFormat dateFormatterForUI;
    protected SimpleDateFormat dateFormatterForJson;

    private Site currentSite;
    private Long currentSiteId;

    String parentActivityName = ActivityCurrent.getInstance().getActivityCurrent().getClass().getSimpleName();

    // @ O v e r r i d e   m e t h o d s
    // ---------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: parentActivity=" + parentActivityName);
        ActivityCurrent.getInstance().setActivityCurrent(this);
        Activity currentActivity = ActivityCurrent.getInstance().getActivityCurrent();
        Log.d(TAG, "onCreate: currentActivity=" + currentActivity);
        if(SiteCurrent.getInstance().isCurrentSite()){
            currentSite = SiteCurrent.getInstance().getSiteCurrent();
            currentSiteId = currentSite.getId();
        }

        Log.i(TAG, "onCreate: currentSite=" + currentSite + " currentSiteId=" + currentSiteId
                + " siteName=" + currentSite.getName());

        dateFormatterForUI = new SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE);
        dateFormatterForJson = new SimpleDateFormat("yyyy-MM-dd", Locale.FRANCE);

        setContentView(R.layout.machine_crud_activity);
        initToolBar();
        initDrawer();
        findViews();
        setViews();
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
    public void onClick(View view) {
        Log.i(TAG, "onClick: view=" + view);
        Machine machine = MachineCurrent.getInstance().getMachineCurrent();
        if (view == machineCrudActivityPurchaseDateTextView) {
            purchaseDatePickerDialog.show();
        } else if(view == machineCrudActivityInstallDateTextView) {
            installDatePickerDialog.show();
        } else if (view == machineCrudActivityScannerButton) {
            IntentIntegrator integrator = new IntentIntegrator(MachineCrudActivity.this);
            integrator.setCaptureActivity(AnyOrientationCaptureActivity.class);
            integrator.setOrientationLocked(false);
            integrator.setBeepEnabled(true);
            integrator.initiateScan();
        } else if (view == machineCrudActivityEquipmentButton) {
            Intent intent_equipment = new Intent(getApplicationContext(), EquipmentListActivity.class);
            startActivityForResult(intent_equipment, HibmobtechApplication.EQUIPMENT_REQUEST);
        } else if (view == machineCrudActivitySaveButton) {
            saveMachine(currentSiteId, machine);
        } else if(view == machineCrudActivityCancelButton) {
            MachineCurrent.getInstance().setMachineCurrent(null);
            Intent intent = new Intent(getApplicationContext(), SiteActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        Log.i(TAG, "onCreateOptionsMenu");
        super.onCreateOptionsMenu(menu);
        MenuItem mainMenuItemCheck = menu.findItem(R.id.mainMenuCheckItem);
        mainMenuItemCheck.setVisible(false);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + " resultCode=" + resultCode);
        Machine machine = MachineCurrent.getInstance().getMachineCurrent();
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            if (result != null) {
                if (result.getContents() == null) {
                    Log.d(TAG, "Cancelled scan");
                    if(!MachineCrudActivity.this.isFinishing())
                        Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                } else {
                    Log.d(TAG, "Scanned: result.getContents()= " + result.getContents());
                    if(result.getRawBytes() != null)
                        Log.d(TAG, "Scanned: result.getRawBytes()= " + result.getRawBytes().toString());
                    Log.d(TAG, "Scanned: result.getBarcodeImagePath()= " + result.getBarcodeImagePath());
                    Log.d(TAG, "Scanned: result.getFormatName()= " + result.getFormatName());
                    Log.d(TAG, "Scanned: result.getErrorCorrectionLevel()= " + result.getErrorCorrectionLevel());
                    Log.d(TAG, "Scanned: result: " + result.toString());
                    if(!MachineCrudActivity.this.isFinishing())
                        Toast.makeText(this, "Résultat du scan : " + result.getContents(), Toast.LENGTH_LONG).show();
                    checkSerialNumberOnServer(machine, result.getContents());
                }
            } else {
                // This is important, otherwise the result will not be passed to the fragment
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
        if (requestCode == HibmobtechApplication.EQUIPMENT_REQUEST) {
            Log.d(TAG, " onActivityResult: requestCode == HibmobtechApplication.EQUIPMENT_REQUEST"
                    + " equipement=" + MachineCurrent.getInstance().getMachineCurrent().getEquipment());
            setEquipmentView(MachineCurrent.getInstance().getMachineCurrent().getEquipment());
        }
    }

    // O t h e r s   m e t h o d s
    // ---------------------------

    abstract void saveMachine(Long siteId, Machine machine);

    public void findViews () {
        Log.i(TAG, "findViews");
        machineCrudActivitySiteNameTextView = (TextView)findViewById(R.id.machineCrudActivitySiteNameTextView);
        machineCrudActivitySerialNumberEditText = (EditText)findViewById(R.id.machineCrudActivitySerialNumberEditText);
        machineCrudActivityScannerButton = (ImageButton)findViewById(R.id.machineCrudActivityScannerButton);
        machineCrudActivityEquipmentButton = (Button)findViewById(R.id.machineCrudActivityEquipmentButton);
        machineCrudActivityReferenceTextView = (TextView)findViewById(R.id.machineCrudActivityReferenceTextView);
        machineCrudActivityCategoryTextView = (TextView)findViewById(R.id.machineCrudActivityCategoryTextView);
        machineCrudActivityBrandTextView = (TextView)findViewById(R.id.machineCrudActivityBrandTextView);
        machineCrudActivityPurchaseDateEditText = (EditText)findViewById(R.id.machineCrudActivityPurchaseDateEditText);
        machineCrudActivityPurchaseDateTextView = (TextView)findViewById(R.id.machineCrudActivityPurchaseDateTextView);
        machineCrudActivityInstallDateEditText = (EditText)findViewById(R.id.machineCrudActivityInstallDateEditText);
        machineCrudActivityInstallDateTextView = (TextView)findViewById(R.id.machineCrudActivityInstallDateTextView);
        machineCrudActivityCommentsEditText = (ActionEditText)findViewById(R.id.machineCrudActivityCommentsEditText);
        machineCrudActivitySaveButton = (Button)findViewById(R.id.machineCrudActivitySaveButton);
        machineCrudActivityCancelButton = (Button)findViewById(R.id.machineCrudActivityCancelButton);
    }

    public void setViews() {
        Log.i(TAG, "setViews");
        machineCrudActivityPurchaseDateTextView.setTypeface(hibmobtechFont);
        machineCrudActivityInstallDateTextView.setTypeface(hibmobtechFont);
        if(currentSite != null)
            machineCrudActivitySiteNameTextView.setText(currentSite.getBusinessName());
    }

    public void setMachineView(Machine machine) {
        Log.i(TAG, "setMachineView: serialNumber=" + machine.getSerialNumber());
        if(machine.getSerialNumber() != null) {
            machineCrudActivitySerialNumberEditText.setText(machine.getSerialNumber());
        }
        if(machine.getPurchaseDate() != null) {
            Date purchaseDate = machine.getPurchaseDate();
            String dateStringForUI = dateFormatterForUI.format(purchaseDate);
            machineCrudActivityPurchaseDateEditText.setText(dateStringForUI);
        }
        if(machine.getInstallDate() != null) {
            Date installDate = machine.getInstallDate();
            String dateStringForUI = dateFormatterForUI.format(installDate);
            machineCrudActivityInstallDateEditText.setText(dateStringForUI);
        }
        if (machine.getComments() != null) {
            machineCrudActivityCommentsEditText.setText(machine.getComments());
        }
    }

    public void setEquipmentView(Equipment equipment) {
        Log.d(TAG, "setEquipmentView");
        if(equipment != null) {
            if (equipment.getReference() != null) {
                machineCrudActivityReferenceTextView.setText(equipment.getReference());
            }
            if (equipment.getCategory() != null) {
                machineCrudActivityCategoryTextView.setText(equipment.getCategory());
            }
            if (equipment.getBrand() != null) {
                machineCrudActivityBrandTextView.setText(equipment.getBrand());
            }
        }
    }

    public void setListeners(Machine machine) {
        Log.i(TAG, "setListeners");
        machineCrudActivityScannerButton.setOnClickListener(this);
        machineCrudActivityEquipmentButton.setOnClickListener(this);
        machineCrudActivitySaveButton.setOnClickListener(this);
        machineCrudActivityCancelButton.setOnClickListener(this);
        setDatePickerListerner(machine);
        setEditTextListerner(machine);
    }

    public void setEditTextListerner(final Machine machine) {
        Log.i(TAG, "setEditTextListerner");
        machineCrudActivitySerialNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                machine.setSerialNumber(machineCrudActivitySerialNumberEditText.getText().toString());
                Log.i(TAG, "afterTextChanged: SerialNumber=" + machine.getSerialNumber());
            }
        });

        machineCrudActivityCommentsEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                machine.setComments(machineCrudActivityCommentsEditText.getText().toString());
            }
        });
    }

    private void setDatePickerListerner(final Machine machine) {
        Log.i(TAG, "setDatePickerListerner");
        machineCrudActivityPurchaseDateTextView.setOnClickListener(this);
        machineCrudActivityInstallDateTextView.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        purchaseDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newPurchaseDate = Calendar.getInstance();
                newPurchaseDate.set(year, monthOfYear, dayOfMonth);
                Date date = newPurchaseDate.getTime();
                String dateStringForUI = dateFormatterForUI.format(date);
                machineCrudActivityPurchaseDateEditText.setText(dateStringForUI);
                machine.setPurchaseDate(date);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        installDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newInstallDate = Calendar.getInstance();
                newInstallDate.set(year, monthOfYear, dayOfMonth);
                Date date = newInstallDate.getTime();
                String dateStringForUI = dateFormatterForUI.format(date);
                machineCrudActivityInstallDateEditText.setText(dateStringForUI);
                machine.setInstallDate(date);
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
    }

    protected void checkSerialNumberOnServer(final Machine machine, final String serialNumber) {
        Log.d(TAG, "checkSerialNumberOnServer: serialNumber= " + serialNumber);
        MachineApiService service = HibmobtechApplication.getRestClient().getMachineService();
        service.getMachine(serialNumber, new Callback<List<Machine>>() {
            @Override
            public void success(final List<Machine> machines, retrofit.client.Response response) {
                if(machines == null) return;

                String msg;
                int sizeOfListMachine = machines.size();

                if (sizeOfListMachine == 0) {
                    Log.d(TAG, "checkSerialNumberOnServer: pas de machine enregistrée avec ce no de série !! serialNumber= " + serialNumber);
                    machine.setSerialNumber(serialNumber);
                    MachineCrudActivity.this.machineCrudActivitySerialNumberEditText.setText(machine.getSerialNumber());
                } else {
                    msg = "Données dupliquées ! Machine " + serialNumber + "\ndéjà enregistrée sur :\n";
                    int i = 0;
                    int indexOfDuplicatedMachineOnSite = 0;

                    for (Machine m : machines) {
                        msg = msg.concat(" - " + m.getSite().getBusinessName());
                        msg = msg.concat("\n");
                        if (m.getSite().getId().equals(currentSiteId)) {
                            indexOfDuplicatedMachineOnSite = i;
                        }
                        i++;
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(MachineCrudActivity.this, R.style.AppCompatAlertDialogStyle);
                    builder.setTitle("Attention !");
                    builder.setMessage(msg);
                    final int finalIndexOfDuplicatedMachineOnSite = indexOfDuplicatedMachineOnSite;
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d(TAG, "getSite().getId()="
                                    + machines.get(finalIndexOfDuplicatedMachineOnSite).getSite().getId()
                                    + " currentSiteId=" + currentSite);
                            if (machines.get(finalIndexOfDuplicatedMachineOnSite).getSite().getId().equals(currentSiteId)) {
                                // at least one updatedMachine already recorded on this currentSite, stop the creation and
                                // let's start detail activity of the last duplicated updatedMachine
                                MachineCurrent.getInstance().setMachineCurrent(machines.get(finalIndexOfDuplicatedMachineOnSite));
                                Intent intent = new Intent(getApplicationContext(), MachineActivity.class);
                                startActivityForResult(intent, HibmobtechApplication.MACHINE_REQUEST);
                            } else {
                                // machines already recorded on server but not in this currentSite,
                                // //continue the creation of updatedMachine
                                machine.setSerialNumber(serialNumber);
                                setMachineView(machine);
                            }
                        }
                    });
                    if(!MachineCrudActivity.this.isFinishing())
                        builder.show();
                    Log.d(TAG, "checkSerialNumberOnServer: Données dupliquées: " + msg);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "failure : " + error.toString());
            }
        });
    }
}
