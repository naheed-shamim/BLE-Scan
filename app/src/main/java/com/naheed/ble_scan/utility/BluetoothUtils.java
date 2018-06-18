package com.naheed.ble_scan.utility;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * Created by Naheed on 6/10/2018.
 */
public class BluetoothUtils {

    public static boolean isBLESupported(Context context)
    {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "BLE not supported", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
