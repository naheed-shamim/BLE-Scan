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

//    public static void enableBluetooth(Context context, BluetoothAdapter bluetoothAdapter)
//    {
//        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled())
//        {
//            Intent btInent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
////            context.startactivityfor
//        }

    public static boolean isBLESupported(Context context)
    {
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(context, "BLE not supported", Toast.LENGTH_SHORT).show();
//            ((Activity)context).finish();
            return false;
        }
        return true;
    }

}
