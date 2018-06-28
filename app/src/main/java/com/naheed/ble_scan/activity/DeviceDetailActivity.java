package com.naheed.ble_scan.activity;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.naheed.ble_scan.R;
import com.naheed.ble_scan.ble.BleService;
import com.naheed.ble_scan.ble.GattUpdateListener;
import com.naheed.ble_scan.ble.GattUpdateReceiver;
import com.naheed.ble_scan.ble.SampleGattAttributes;
import com.naheed.ble_scan.utility.AlertUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceDetailActivity extends AppCompatActivity implements GattUpdateListener {

    private static final String TAG = DeviceDetailActivity.class.getSimpleName();

    @BindView(R.id.device_detail_name_txt_view)
    TextView mDeviceNameTxtView;

    @BindView(R.id.device_detail_address_txt_view)
    TextView mDeviceAddressTxtView;

    @BindView(R.id.connection_state_txt_view)
    TextView mConnectionState;

    @BindView(R.id.data_value_txt_view)
    TextView mDataField;

    @BindView(R.id.gatt_service_list)
    ExpandableListView mGattServicesList;

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";


    private String mDeviceNameStr;
    private String mDeviceAddressStr;

    private boolean mConnected;

    private GattUpdateReceiver mGattUpdateReceiver;
    private BleService mBluetoothLeService;
    private BluetoothGattCharacteristic mNotifyCharacteristic;

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics;// = new ArrayList<>();


    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {

            mBluetoothLeService = ((BleService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddressStr);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        };
    };
    private boolean mScanning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_detail);
        ButterKnife.bind(this);

        getIntentValues();


        connectToDevice();

        Intent gattServiceIntent = new Intent(this, BleService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        mGattUpdateReceiver = new GattUpdateReceiver(DeviceDetailActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mGattUpdateReceiver, GattUpdateReceiver.getGattIntentFilter());

        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddressStr);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.connect, menu);

        if(mConnected) {
            menu.findItem(R.id.menu_disconnect).setVisible(true);
            menu.findItem(R.id.menu_connect).setVisible(false);
        }
        else {
            menu.findItem(R.id.menu_disconnect).setVisible(false);
            menu.findItem(R.id.menu_connect).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_connect:

                connectToDevice();
                break;

            case R.id.menu_disconnect:

                mBluetoothLeService.disconnect();
                break;
        }
        return true;
    }

    private void connectToDevice()
    {
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddressStr);
            mConnected = result;

            if(!mConnected)
                AlertUtils.showShortToast(this, "Couldn't Connect to Device");
            else
                AlertUtils.showShortToast(this, "Trying to Connect");

            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    private void getIntentValues()
    {
        final Intent activityIntent = getIntent();

        mDeviceNameStr = activityIntent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddressStr = activityIntent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        mDeviceNameTxtView.setText(mDeviceNameStr);
        mDeviceAddressTxtView.setText(mDeviceAddressStr);

        setExpandableListClickListener();
    }

    private void setExpandableListClickListener()
    {
        mGattServicesList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                if (mGattCharacteristics != null) {
                    final BluetoothGattCharacteristic characteristic =
                            mGattCharacteristics.get(groupPosition).get(childPosition);

                    final int charaProp = characteristic.getProperties();

                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                        // If there is an active notification on a characteristic, clear
                        // it first so it doesn't update the data field on the user interface.
                        if (mNotifyCharacteristic != null) {
                            mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                            mNotifyCharacteristic = null;
                        }
                        mBluetoothLeService.readCharacteristic(characteristic);
                    }

                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                        mNotifyCharacteristic = characteristic;
                        mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                    }
                    return true;
                }
                return false;
            }
        });

    }


    /*
    GattUpdate Receiver Listener Implementations
     */
    @Override
    public void updateConnectionState(final String connectionState) {

        mConnected = connectionState.equalsIgnoreCase("connected");
        invalidateOptionsMenu();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(connectionState);
            }
        });

    }


    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    @Override
    public void displayGattAttributes() {

        List<BluetoothGattService> gattServices = mBluetoothLeService.getSupportedGattServices();

        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);

        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<>();

        mGattCharacteristics = new ArrayList<>();

        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices)
        {
            HashMap<String, String> currentServiceData = new HashMap<>();
            uuid = gattService.getUuid().toString();

            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =new ArrayList<>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();

            ArrayList<BluetoothGattCharacteristic> characteristics = new ArrayList<>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics)
            {
                characteristics.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<>();
                uuid = gattCharacteristic.getUuid().toString();

                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }

            mGattCharacteristics.add(characteristics);
            gattCharacteristicData.add(gattCharacteristicGroupData);

            SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                    this,
                    gattServiceData,
                    android.R.layout.simple_expandable_list_item_2,
                    new String[] {LIST_NAME, LIST_UUID},
                    new int[] { android.R.id.text1, android.R.id.text2 },
                    gattCharacteristicData,
                    android.R.layout.simple_expandable_list_item_2,
                    new String[] {LIST_NAME, LIST_UUID},
                    new int[] { android.R.id.text1, android.R.id.text2 }
            );
            mGattServicesList.setAdapter(gattServiceAdapter);
        }
    }

    @Override
    public void displayBLEData(String bleData) {

        if (bleData != null)
        {
            mDataField.setText(bleData);
        }
    }


    //Clear the UI and adapter
    @Override
    public void clearData()
    {
        mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        mDataField.setText(R.string.no_data);
    }
}
