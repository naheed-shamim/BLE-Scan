package com.naheed.ble_scan.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.naheed.ble_scan.R;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void showBatteryScanActivity(View view) {

        Intent batteryScanActivity = new Intent(this, BatteryIndicatorActivity.class);
        startActivity(batteryScanActivity);

    }

    public void showLeDevicesActivity(View view) {

        Intent leScanActivity = new Intent(this, MainActivity.class);
        startActivity(leScanActivity);

    }
}
