package com.hibernatus.hibmobtech.equipment;

import android.util.Log;

import com.hibernatus.hibmobtech.model.Equipment;

/**
 * Created by Eric on 15/01/2016.
 */
public class EquipmentCurrent {
    public static final String TAG = EquipmentCurrent.class.getSimpleName();
    private Equipment equipmentCurrent;

    private EquipmentCurrent() {}

    private static EquipmentCurrent INSTANCE = new EquipmentCurrent();

    public void initEquipmentCurrent() {
        Log.i(TAG, "initEquipmentCurrent");
        equipmentCurrent = null;
    }

    public boolean isCurrentEquipment() {
        return equipmentCurrent != null;
    }

    public static EquipmentCurrent getInstance()
    {
        return INSTANCE;
    }

    public Equipment getEquipmentCurrent() {
        Log.i(TAG, "getSparePartCurrent");
        if (equipmentCurrent != null) {
            Log.i(TAG, "getSparePartCurrent:currentRequest: id="
                    + equipmentCurrent.getId()
                    + " equipmentCurrent.getDescription="
                    + equipmentCurrent.getDescription());
        }
        return equipmentCurrent;
    }

    public void setEquipmentCurrent(Equipment equipmentCurrent) {
        Log.i(TAG, "setSparePartCurrent");
        this.equipmentCurrent = equipmentCurrent;
        if (equipmentCurrent != null) {
            Log.i(TAG, "setSparePartCurrent:currentRequest: id="
                    + this.equipmentCurrent.getId()
                    + " equipmentCurrent.getDescription="
                    + this.equipmentCurrent.getDescription());
        }
    }
}

