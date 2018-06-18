package com.naheed.ble_scan;

import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * Created by Naheed on 6/18/2018.
 */
public interface GattUpdateListener {

    void updateConnectionState(String connectionState);
    void displayGattAttributes();
    void displayBLEData(String bleData);
    void clearData();
}
