package com.naheed.ble_scan;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.naheed.ble_scan.utility.BluetoothUtils;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void checkBLEandEnableBT()
    {
        // if BLE not supported, finish
        // else switch BT on
        if(!BluetoothUtils.isBLESupported(MainActivity.this))
            finish();
        else
        {
            final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);



        }


    }


}
