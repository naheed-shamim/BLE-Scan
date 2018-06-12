package com.naheed.ble_scan;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by Naheed on 6/11/2018.
 */
public class BleDeviceAdapter extends RecyclerView.Adapter<BleDeviceAdapter.BleDeviceViewHolder> {

    private ArrayList<BluetoothDevice> mLeDevices;
    private Context mContext;

    public BleDeviceAdapter(Context context) {
        this.mContext = context;
        mLeDevices = new ArrayList<BluetoothDevice>();
    }

    @NonNull
    @Override
    public BleDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.item_ble_devices,parent, false);
        return new BleDeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BleDeviceViewHolder holder, int position)
    {
        BluetoothDevice bluetoothDevice = mLeDevices.get(position);

        String deviceName = bluetoothDevice.getName();
        String deviceAddress = bluetoothDevice.getAddress();

        if(deviceName != null && deviceName.length() > 0)
            holder.mDeviceNameTxtView.setText(deviceName);
        else
            holder.mDeviceNameTxtView.setText(R.string.unknown_device);

        holder.mDeviceAddrTextView.setText(deviceAddress);
    }


    @Override
    public int getItemCount() {
        return mLeDevices.size();
    }

    public void addDevice(BluetoothDevice device)
    {
        if(!mLeDevices.contains(device))
        {
            mLeDevices.add(device);
//            this.notifyItemRangeChanged(0,mLeDevices.size());
            this.notifyDataSetChanged();
        }
    }


    public void clearLeDevices()
    {
        mLeDevices.clear();
    }

    class BleDeviceViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.device_name_txt_view)
        TextView mDeviceNameTxtView;

        @BindView(R.id.device_address_txt_view)
        TextView mDeviceAddrTextView;

        BleDeviceViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
