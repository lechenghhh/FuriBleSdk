package com.lecheng.furiblesdk;

/**
 * Created by Cheng on 2018/5/22.
 */

public interface BluetoothLeInterface {
    public void getData(String weight, String type, String typeName, String unit);

    public void getConnectionState(String connStateInfo, boolean isConnected);

    public void getConnectionInfo(String deviceName, String deviceAddress);

    public void getBluetoothLeService(BluetoothLeService bluetoothLeService);

}
