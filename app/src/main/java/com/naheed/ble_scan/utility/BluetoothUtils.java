package com.naheed.ble_scan.utility;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

import com.naheed.ble_scan.BleService;
import com.naheed.ble_scan.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Naheed on 6/10/2018.
 */
public class BluetoothUtils {

    private static final String LIST_NAME = "NAME";
    private static final String LIST_UUID = "UUID";

    private static  String unknownServiceString = "Unknown Service";
    private static String unknownCharaString = "Unknown Characteristic";


    public static boolean isBLESupported(Context context) {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "BLE not supported", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public static ArrayList<BluetoothGattCharacteristic> getGattCharacteristicsFor(BluetoothGattService gattService)
    {
        List<BluetoothGattCharacteristic> gattCharacteristicList= gattService.getCharacteristics();

        return new ArrayList<>(gattCharacteristicList);

    }

    public static  ArrayList<HashMap<String, String>> getGattCharacteristicsGroupData(List<BluetoothGattCharacteristic> gattCharacteristics)
    {
        ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<>();

        // Loops through available Characteristics.
        for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
            HashMap<String, String> currentCharaData = new HashMap<String, String>();
            String uuid = gattCharacteristic.getUuid().toString();

            currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
            currentCharaData.put(LIST_UUID, uuid);
            gattCharacteristicGroupData.add(currentCharaData);
        }

        return gattCharacteristicGroupData;
    }

    public static ArrayList<HashMap<String, String>> getAvailableGattServices(List<BluetoothGattService> gattServices)
    {
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<>();
        String uuid = null;

        for(BluetoothGattService gattService : gattServices)
        {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();

            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);
        }
        return gattServiceData;
    }
}
