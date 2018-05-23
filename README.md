福日电子秤蓝牙ble Android SDK
===
##1.版本信息##
V1.0
##2.项目依赖##
AndroidStudio->File->New->Import Module...->Sourc Directory:选择FuriBleSdk文件夹->Finish 完成对依赖库的引用
依赖库targetSdkVersion 26
##3.AndroidManifest.xml配置##
```xml
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" /> 
    <uses-permission android:name="android.permission.DEVICE_POWER" />
```
在<application>标签中加入，启用ble服务程序
```xml
    <service
    android:name="com.lecheng.furiblesdk.BluetoothLeService"
    android:enabled="true" />
```
##4.DeviceListActivity.java##
该类主要功能扫描并显示蓝牙设备名称和蓝牙地址组件，选择设备以连接。
使用时请扩展自该类，并对子类注册AndroidMinefest.xml的activity启动标签
##5.BluetoothLeActivity.java##
该类主要功能用于获取连接的设备名称和蓝牙地址，设备连接状态，获取实时的电子秤示数，包括正负值，质量数据，质量稳定/超轻/超重状态，质量单位，断线重连等功能。
使用时请扩展自该类，并对子类注册AndroidMinefest.xml的activity启动标签
####5.1 getData(String weight, String type, String typeName, String unit)获取电子秤数据####
weight:质量(包含符号位)
type:类型 OL对应超重 ST 对应稳定 UN对应普通
typeName:类型名称 OL对应超重 ST 对应稳定 UN对应普通
unit:单位名称 kg/g/磅/斤
####5.2 getConnectionState(String connStateInfo, boolean isConnected)获取连接信息####
connStateInfo:连接信息
isConnected:是否连接
####5.3 getConnectionInfo(String deviceName, String deviceAddress)获取连接信息####
deviceName:设备名称
DeviceAddress:设备Mac地址
####5.4 getBluetoothLeService()获取服务实例BluetoothLeService ####
####5.5 BluetoothLeService####
connect(String deviceAddress)传入地址进行重连操作

