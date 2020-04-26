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
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * For a given BLE device, this Activity provides the user interface to connect,
 * display data, and display GATT services and characteristics supported by the
 * device. The Activity communicates with {@code BluetoothLeService}, which in
 * turn interacts with the Bluetooth LE API.
 */
public abstract class BluetoothLeActivity extends AppCompatActivity {
    private final static String TAG = BluetoothLeActivity.class.getSimpleName();
    /*D/BluetoothGatt: setCharacteristicNotification() - uuid: 0000ffe1-0000-1000-8000-00805f9b34fb enable: true*/
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    //Bluetooth
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;//连接实例
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic, gattCharacteristic2;
    private BluetoothAdapter mBluetoothAdapter;
    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";
    private static final int REQUEST_ENABLE_BT = 1;
    private String mDeviceName, mDeviceAddress;//设备名&设备地址

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();//11-29 lecheng修改
            }
            // Automatically connects to the device upon successful start-up
            // initialization.
            mBluetoothLeService.connect(mDeviceAddress);
            getBluetoothLeService(mBluetoothLeService);//发送给子类获取实例
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    /* Handles various events fired by the Service. ACTION_GATT_CONNECTED: connected to a GATT server. ACTION_GATT_DISCONNECTED: disconnected from a GATT server. ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services. ACTION_DATA_AVAILABLE: received data from the device. This can be a result of read or notification operations.*/
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(getString(R.string.connected), mConnected);//更新蓝牙连接状态
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED
                    .equals(action)) {
                mConnected = false;
                updateConnectionState(getString(R.string.disconnected), mConnected);//更新蓝牙连接状态
                invalidateOptionsMenu();
                mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED
                    .equals(action)) {
                // Show all the supported services and characteristics on the
                // user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                receiveData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    /*If a given GATT characteristic is selected, check for supported features.This sample demonstrates 'Read' and 'Notify' features. See http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete list of supported characteristic features.*/
    private final ExpandableListView.OnChildClickListener servicesListClickListner = new ExpandableListView.OnChildClickListener() {
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            if (mGattCharacteristics != null) {
                final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
                final int charaProp = characteristic.getProperties();
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    // If there is an active notification on a characteristic,
                    // clear
                    // it first so it doesn't update the data field on the user
                    // interface.
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
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        //防止休眠
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /*检查蓝牙功能*/
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
            }
        }
        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        Log.e(TAG,"mDeviceName:" + mDeviceName + "  Mac:" + mDeviceAddress);
        getConnectionInfo(mDeviceName, mDeviceAddress);//获取设备信息
        mGattServicesList = new ExpandableListView(this);
        mGattServicesList.setOnChildClickListener(servicesListClickListner);
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }

    }

    //从蓝牙端获取到的数据
    private void receiveData(String data) {
        Log.e(TAG,"receiveData=" + data);
        try {
            if (data != null) {
                String unit = MyUtils.getWeightUnit(data);//获取单位
                String strWeight = data.substring(6, 14).replace(" ", "");//截取6到14位的重量值
                if (data.indexOf("OL") != -1) {
                    getData(strWeight, "OL", "超重", unit);
                } else if (data.indexOf("ST") != -1) {
                    getData(strWeight, "ST", "稳定", unit);
                } else if (data.indexOf("UN") != -1) {
                    getData(strWeight, "UN", "普通", unit);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            getData("", "err", "解析失败", "err");
        }
    }

    //供子类响应
    public abstract void getData(String weight, String type, String TypeName, String unit);

    //获取设备状态，并供主UI更新
    private void updateConnectionState(final String connStateInfo, final boolean isConnected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //蓝牙连接回掉
                getConnectionState(connStateInfo, isConnected);
            }
        });
    }

    //供子类响应
    public abstract void getConnectionState(String connStateInfo, boolean isConnected);

    //供子类响应，获取设备信息
    public abstract void getConnectionInfo(String deviceName, String deviceAddress);

    //获取连接实例
    public abstract void getBluetoothLeService(BluetoothLeService bluetoothLeService);

    //Demonstrates how to iterate through the supported GATT// Services/Characteristics.//In this sample,we populate the data structure that is bound to the//ExpandableListView,on the UI.
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        Log.e(TAG,"displayGattServices");
        if (gattServices == null)
            return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<BluetoothGattCharacteristic>();

            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                if (gattCharacteristic.getUuid().toString().equals("0000ffe1-0000-1000-8000-00805f9b34fb")) {
                    gattCharacteristic2 = gattCharacteristic;
                }
                charas.add(gattCharacteristic);//model层
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);//VIEW层
            }
            mGattCharacteristics.add(charas);//model层
            gattCharacteristicData.add(gattCharacteristicGroupData);//VIEW层
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this, gattServiceData,
                android.R.layout.simple_expandable_list_item_2, new String[]{
                LIST_NAME, LIST_UUID}, new int[]{android.R.id.text1,
                android.R.id.text2}, gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2, new String[]{
                LIST_NAME, LIST_UUID}, new int[]{android.R.id.text1,
                android.R.id.text2});
        mGattServicesList.setAdapter(gattServiceAdapter);
        Log.e(TAG,"mGattServicesList.setAdapter");

        try {/*以下 先对列表进行隐藏，然后自动选择第5行(p=4)第1个子列(p=0)，用以进行uuid的选择*/
            mGattServicesList.setVisibility(View.GONE);
            if (mGattCharacteristics != null) {//groupPosition=4;childPosition=0;

                final BluetoothGattCharacteristic characteristic = gattCharacteristic2;
//                final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(3).get(0);
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
                    Log.e(TAG,"characteristic=" + characteristic);
                    mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unbindService(mServiceConnection);
            mBluetoothLeService = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        unregisterReceiver(mGattUpdateReceiver);
    }
}
