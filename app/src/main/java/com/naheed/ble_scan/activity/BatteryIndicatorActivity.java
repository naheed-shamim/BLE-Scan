package com.naheed.ble_scan.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.naheed.ble_scan.BleDeviceAdapter;
import com.naheed.ble_scan.R;
import com.naheed.ble_scan.ble.BleService;
import com.naheed.ble_scan.ble.GattUpdateListener;
import com.naheed.ble_scan.ble.GattUpdateReceiver;
import com.naheed.ble_scan.ble.SampleGattAttributes;
import com.naheed.ble_scan.utility.BluetoothUtils;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BatteryIndicatorActivity extends AppCompatActivity implements GattUpdateListener{

    @BindView(R.id.scan_battery_devices_btn)
    Button mScanDevicesBtn;

    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    @BindView(R.id.battery_dev_name)
    TextView mDeviceName;

    @BindView(R.id.battery_dev_address)
    TextView mDeviceAddr;

    @BindView(R.id.connect_to_battery_dev)
    Button mConnectToBatteryDeviceBtn;

    @BindView(R.id.battery_dev_conn_state)
    TextView mConnectionState;

    @BindView(R.id.connect_to_service)
    Button mConnectToService;

    @BindView(R.id.service_type)
    TextView mServiceType;

    @BindView(R.id.battery_level_txt_view)
    TextView mBatteryLvlTextView;

    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BleService mBleService;
    private BluetoothDevice mBluetoothDevice;
    private GattUpdateReceiver mGattReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_indicator);

        ButterKnife.bind(this);

        mGattReceiver = new GattUpdateReceiver(BatteryIndicatorActivity.this);
        checkBleAndEnableBT();

        setClickListeners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mGattReceiver = null;
    }

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            mBleService = ((BleService.LocalBinder) service).getService();
            if (!mBleService.initialize()) {
                Log.e("batteryIndicator", "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBleService.connect(mBluetoothDevice.getAddress());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBleService = null;
        };
    };

    /**
     * if BLE not supported, finish
     * else switch BT on
     */
    private void checkBleAndEnableBT() {

        if (!BluetoothUtils.isBLESupported(this))
            finish();
        else {
            mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter(this);

            if (mBluetoothAdapter != null)
                enableBluetoothIfNot();
        }
    }

    private void enableBluetoothIfNot() {

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mGattReceiver, GattUpdateReceiver.getGattIntentFilter());
        enableBluetoothIfNot();
    }

    private void setClickListeners()
    {
        mScanDevicesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setIndeterminate(true);
                scanLeDevice(true);
            }
        });

        mConnectToBatteryDeviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothDevice != null) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                    Intent gattServiceIntent = new Intent(BatteryIndicatorActivity.this, BleService.class);
                    bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
                }
            }
        });

        mConnectToService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNotifyCharacteristic != null)
                {
                    final int charaProp = mNotifyCharacteristic.getProperties();
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        mBleService.readCharacteristic(mNotifyCharacteristic);
                    }
                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mBleService.setCharacteristicNotification(mNotifyCharacteristic, true);
                    }
                    mConnectToService.setText(R.string.connected);
                    mConnectToService.setEnabled(false);
                }
            }
        });

    }


    private void scanLeDevice(final boolean enable)
    {
        Handler mHandler = new Handler();
        if(enable)
        {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
//                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mScanCallback);
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            }, BluetoothUtils.SCAN_PERIOD);

//            mScanning = true;
//            mBluetoothAdapter.startLeScan(mScanCallback);
//            UUID heartRateUUUID = UUID.fromString(SampleGattAttributes.UUID_HEART_RATE_SERVICE);
            UUID batteryUUID = UUID.fromString(SampleGattAttributes.UUID_BATTERY_SERVICE);
            UUID[] uuids = {batteryUUID};

            mBluetoothAdapter.startLeScan(uuids, mScanCallback);
        }
        else {
//            mScanning = false;
            mBluetoothAdapter.stopLeScan(mScanCallback);
        }
    }


    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setBluetoothDeviceData(device);
                }
            });
        }
    };

    private void setBluetoothDeviceData(BluetoothDevice bluetoothDevice)
    {
        mBluetoothDevice = bluetoothDevice;
        mDeviceName.setText(mBluetoothDevice.getName());
        mDeviceAddr.setText(mBluetoothDevice.getAddress());
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    /*
    Gatt Update Receiver Implementations
    */

    @Override
    public void updateConnectionState(final String connectionState) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(connectionState);
            }
        });

    }

    @Override
    public void displayGattAttributes() {

        List<BluetoothGattService> gattServices = mBleService.getSupportedGattServices();

        if (gattServices == null)
            return;

        String uuid = null;
        String serviceString = "unknown service";
        String charaString = "unknown characteristic";

        for (BluetoothGattService gattService : gattServices) {

            uuid = gattService.getUuid().toString();

            serviceString = SampleGattAttributes.lookup(uuid, serviceString);

            if (serviceString != null)
            {
                List<BluetoothGattCharacteristic> gattCharacteristics =
                        gattService.getCharacteristics();

                for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {

                    HashMap<String, String> currentCharaData = new HashMap<String, String>();
                    uuid = gattCharacteristic.getUuid().toString();
                    charaString = SampleGattAttributes.lookup(uuid, serviceString);

                    if (charaString != null) {
                        mServiceType.setText(charaString);
                    }

                    mNotifyCharacteristic = gattCharacteristic;
//                    return;
                }
            }
        }
    }

    @Override
    public void displayBLEData(String bleData) {

        if (bleData != null) {
            mBatteryLvlTextView.setText(bleData);
        }

    }

    @Override
    public void clearData() {

    }
}
