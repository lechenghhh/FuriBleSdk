/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lecheng.furiblesdk;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
@SuppressLint("NewApi")
public abstract class DeviceListActivity<T> extends AppCompatActivity {

  
    private BluetoothAdapter mBluetoothAdapter;
    private boolean mScanning;
    private Handler mHandler;
    private static final long SCAN_PERIOD = 16000L;

    private LeScanCallback mLeScanCallback = new LeScanCallback() {
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            DeviceListActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    getLeDevice(device);
                }
            });
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mHandler = new Handler();
        if (!this.getPackageManager().hasSystemFeature("android.hardware.bluetooth_le")) {
            Toast.makeText(this, string.ble_not_supported, Toast.LENGTH_LONG).show();
            this.finish();
        }

        if (VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            ActivityCompat.requestPermissions(this, new String[]{"android.permission.ACCESS_COARSE_LOCATION"}, 10);
        }

        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        this.mBluetoothAdapter = bluetoothManager.getAdapter();
        if (this.mBluetoothAdapter == null) {
            Toast.makeText(this, string.error_bluetooth_not_supported, Toast.LENGTH_LONG).show();
            this.finish();
        }
    }

    protected void scanLeDevice(boolean enable) {
        if (enable) {
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    DeviceListActivity.this.mScanning = false;
                    DeviceListActivity.this.mBluetoothAdapter.stopLeScan(DeviceListActivity.this.mLeScanCallback);
                    DeviceListActivity.this.invalidateOptionsMenu();
                    onScanStop();
                }
            }, SCAN_PERIOD);
            this.mScanning = true;
            this.mBluetoothAdapter.startLeScan(this.mLeScanCallback);
        } else {
            this.mScanning = false;
            this.mBluetoothAdapter.stopLeScan(this.mLeScanCallback);
        }
        this.invalidateOptionsMenu();
    }

    protected abstract void onScanStop();

    public boolean onCreateOptionsMenu(Menu menu) {
//        this.getMenuInflater().inflate(menu.main, menu);
        if (!this.mScanning) {
            menu.findItem(id.menu_stop).setVisible(false);
            menu.findItem(id.menu_scan).setVisible(true);
            menu.findItem(id.menu_refresh).setActionView((View) null);
        } else {
            menu.findItem(id.menu_stop).setVisible(true);
            menu.findItem(id.menu_scan).setVisible(false);
            menu.findItem(id.menu_refresh).setActionView(layout.unit_progress);
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i != id.menu_history) {
            if (i == id.menu_scan) {
                this.scanLeDevice(true);
            } else if (i == id.menu_stop) {
                this.scanLeDevice(false);
            }
        }

        return true;
    }

    public abstract void getLeDevice(BluetoothDevice bluetoothDevice);

    protected void onResume() {
        super.onResume();
        if (!this.mBluetoothAdapter.isEnabled() && !this.mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
            this.startActivityForResult(enableBtIntent, 1);
        }
        this.scanLeDevice(true);
    }

    protected void onPause() {
        super.onPause();
        this.scanLeDevice(false);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
