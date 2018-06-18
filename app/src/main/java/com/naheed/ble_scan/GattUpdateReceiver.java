package com.naheed.ble_scan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Naheed on 6/18/2018.
 */



// Handles various events fired by the Service.
// ACTION_GATT_CONNECTED: connected to a GATT server.
// ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
// ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
// ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
//
public class GattUpdateReceiver extends BroadcastReceiver {

    boolean mConnected;
    private GattUpdateListener mGattListener;
//    BleService mBleService;

    public GattUpdateReceiver(boolean mConnected, GattUpdateListener listener) {
        this.mConnected = mConnected;
        mGattListener = listener;
//        mBleService = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();

        switch (action)
        {
            case BleService.ACTION_GATT_CONNECTED:
                mConnected = true;
                mGattListener.updateConnectionState("Connected");
//                updateConnectionState("Connected");
//                    invalidateOptionsMenu();

                break;
            case BleService.ACTION_GATT_DISCONNECTED:
                mConnected = false;
                mGattListener.updateConnectionState("Disconnected");
                mGattListener.clearData();

//                updateConnectionState("Disconnected");
                //                    invalidateOptionsMenu();
//                clearUI();


                break;
            case BleService.ACTION_GATT_SERVICES_DISCOVERED:

                // Show all the supported services and characteristics on the user interface.
//                mGattListener.displayGattAttributes(mBleService.getSupportedGattServices());
                mGattListener.displayGattAttributes();

//                displayGattServices(mBluetoothLeService.getSupportedGattServices());

                break;

            case BleService.ACTION_DATA_AVAILABLE:
                mGattListener.displayBLEData(intent.getStringExtra(BleService.EXTRA_DATA));
//                displayData(intent.getStringExtra(BleService.EXTRA_DATA));
                break;
        }

    }
}
