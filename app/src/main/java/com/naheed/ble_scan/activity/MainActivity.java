package com.naheed.ble_scan.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcel;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.naheed.ble_scan.BleDeviceAdapter;
import com.naheed.ble_scan.R;
import com.naheed.ble_scan.RecyclerViewClickListener;
import com.naheed.ble_scan.utility.BluetoothUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecyclerViewClickListener {

    @BindView(R.id.ble_devices_recycler_view)
    RecyclerView mRecyclerView;

    private static final int REQUEST_ENABLE_BT = 1;

    private BluetoothAdapter mBluetoothAdapter;
    private BleDeviceAdapter mLeDevicesAdapter;
    private Handler mHandler;
    private boolean mIsScanning;


    private BluetoothAdapter.LeScanCallback mScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDevicesAdapter.addDevice(device);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mHandler = new Handler();

        checkBleAndEnableBT();
    }

    @Override
    protected void onResume() {
        super.onResume();

        enableBluetoothIfNot();

        mLeDevicesAdapter = new BleDeviceAdapter(MainActivity.this, MainActivity.this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRecyclerView.setAdapter(mLeDevicesAdapter);
        shouldScanLeDevice(true);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mIsScanning) {
            menu.findItem(R.id.menu_stop_scan).setVisible(false);
            menu.findItem(R.id.menu_start_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop_scan).setVisible(true);
            menu.findItem(R.id.menu_start_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.layout_indeterminate_progress_bar);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_start_scan:
                mLeDevicesAdapter.clearLeDevices();
                shouldScanLeDevice(true);
                break;
            case R.id.menu_stop_scan:
                shouldScanLeDevice(false);
                break;
        }
        return true;
    }

    /**
     * if BLE not supported, finish
     * else switch BT on
     */
    private void checkBleAndEnableBT() {

        if (!BluetoothUtils.isBLESupported(MainActivity.this))
            finish();
        else {
            mBluetoothAdapter = BluetoothUtils.getBluetoothAdapter(MainActivity.this);

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

    /**
     * Pass
     * @param shouldScan true to Start else false to Stop
     */
    private void startScan(final boolean shouldScan) {
        if (shouldScan) {
            mBluetoothAdapter.startLeScan(mScanCallback);
            mIsScanning = true;
        } else {
            mBluetoothAdapter.stopLeScan(mScanCallback);
            mIsScanning = false;
        }
    }


    /**
     * Scans for LE devices
     * @param enabled if it's true and stops for false
     */
    private void shouldScanLeDevice(final boolean enabled) {

        // Stop the Scan after 10 sec
        if (enabled) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startScan(false);
                    invalidateOptionsMenu();
                }
            }, BluetoothUtils.SCAN_PERIOD);

            startScan(true);
        } else {
            startScan(false);
        }
        invalidateOptionsMenu();
    }


    /*
    Recycler View Click Listeners
    */

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(MainActivity.this, "Item Clicked", Toast.LENGTH_SHORT).show();

        BluetoothDevice device = mLeDevicesAdapter.getDevice(position);

        ParcelUuid[] uuids = device.getUuids();

            if(uuids != null)
        {
        for(ParcelUuid uuid : uuids)
        {
            Log.d("scannedUUID",""+uuid);
        }}

        if (device != null) {
            Intent detailIntent = new Intent(this, DeviceDetailActivity.class);
            detailIntent.putExtra(DeviceDetailActivity.EXTRAS_DEVICE_NAME, device.getName());
            detailIntent.putExtra(DeviceDetailActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());

        if (mIsScanning)
            startScan(false);

            startActivity(detailIntent);
        }
    }

    @Override
    public void onButtonClick(int position, Object object, String keyIdentifier) {

    }
}
