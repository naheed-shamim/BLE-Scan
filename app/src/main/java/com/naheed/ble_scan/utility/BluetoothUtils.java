package com.naheed.ble_scan.utility;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.Toast;

/**
 * Created by Naheed on 6/10/2018.
 */
public class BluetoothUtils {

    public static int SCAN_PERIOD = 10000;

    public static boolean isBLESupported(Context context)
    {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "BLE not supported", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    /**
     * Pass the activity context as
     * @param context
     * @return instance of Bluetooth adapter if successful else return null
     */
    public static BluetoothAdapter getBluetoothAdapter(Context context)
    {
        BluetoothAdapter bluetoothAdapter = null;

        final BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);

        if(bluetoothManager != null)
            bluetoothAdapter= bluetoothManager.getAdapter();

        return bluetoothAdapter;
    }

    public static void enableBluetoothIfNot()
    {
        boolean isBTEnabled = false;


    }
}
