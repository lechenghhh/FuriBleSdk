福日电子秤蓝牙ble Android SDK

1 版本信息
	FuriSDK版本:V1.1
	Android targetSdkVersion 26 

2 项目依赖

方式1：(jipack.io方式)

Step 1. Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://www.jitpack.io' }
		}
	}

Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.lechenghhh:FuriBleSdk:v1.0'
	}

方式2：(module方式)

AndroidStudio->File->New->Import Module...->Sourc Directory:选择FuriBleSdk文件夹->Finish 
setting.gradle 文件中加入module文件夹后，在File->Project Structure->app->Dependencies 中添加对sdk文件夹的依赖后，rebuild项目

方式3：(aar文件方式)

将furiblesdk.aar文件放到项目的libs目录下，在app.gradle文件中加入以下配置：

	allprojects {
    		repositories {
       			flatDir {
            			dirs 'libs'
        		}
        	jcenter()
	    	}
	}
	
	dependencies{
		...
		implementation(name: 'furiblesdk', ext: 'aar')
	}
	
3 AndroidManifest.xml权限配置及服务配置
所需权限
```xml
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.WAKE_LOCK" /> 
    <uses-permission android:name="android.permission.DEVICE_POWER" />
    <uses-feature
        android:name="android.hardware.bluetooth_le"
        android:required="true" />
    <uses-permission-sdk-23 android:name="android.permission.ACCESS_COARSE_LOCATION" />
```
在标签中加入，启用ble服务程序
```xml
    <service
    	android:name="com.lecheng.furiblesdk.BluetoothLeService"
    	android:enabled="true" />
```

4 扫描设备列表并连接设备DeviceListActivity与DeviceListSelectActivity皆可

4.1 DeviceListActivity
该类主要功能该类主要功能扫描并显示蓝牙设备名称和蓝牙地址。使用时请扩展自该类，并对子类注册AndroidMinefest.xml的activity启动标签

4.1.1 public void getLeDevices((BluetoothDevice devices)
该方法返回扫描到的设备数据，devices.getName()用于获取设备名称，devices.getAddress()用于获取设备mac地址。
	
4.1.2 选择好设备的名称与地址用startActivity()方法，将getLeDevices()方法中获取的参数并加入intent中，跳转指定的
例:

	startActivity(new Intent(Context, DisplayActivity.class)
		.putExtra(BluetoothLeActivity.EXTRAS_DEVICE_NAME, BluetoothDevice.getName())
		.putExtra(BluetoothLeActivity.EXTRAS_DEVICE_ADDRESS, BluetoothDevice.getAddress());

4.2 DeviceListSelectActivity.java(不推荐)
该类主要功能扫描并显示蓝牙设备名称和蓝牙地址，UI界面已经绘制封装完成，列表中选择设备以连接，menu菜单中有开始扫描和停止扫描的选项。 使用时请扩展自该类，并对子类注册AndroidMinefest.xml的activity启动标签

4.2.1 public String setDisplayActivityPackage()
请重写该方法并返回用于显示数据的activity的包名
注：onCreate不必重写

4.2.2 checkBLEPermissions()调用该方法进行权限申请，若在onResume方法中执行该方法，用户不同意蓝牙权限的话将无法使用功能。

5 BluetoothLeActivity.java
该类主要功能用显示数据：于获取连接的设备名称和蓝牙地址，设备连接状态，获取实时的电子秤示数，包括正负值，质量数据，质量稳定/超轻/超重状态，质量单位，断线重连等功能。 使用时请扩展自该类，并对子类注册AndroidMinefest.xml的activity启动标签 

5.1 getData(String weight, String type, String typeName, String unit)获取电子秤数据
    weight:质量(包含符号位) type:类型 OL对应超重 ST 对应稳定 UN对应普通 typeName:类型名称 OL对应超重 ST 对应稳定 UN对应普通 unit:单位名称 kg/g/磅/斤 

5.2 getConnectionState(String connStateInfo, boolean isConnected)获取连接信息 
    connStateInfo:连接信息 isConnected:是否连接 

5.3 getConnectionInfo(String deviceName, String deviceAddress)获取连接信息 
    deviceName:设备名称 DeviceAddress:设备Mac地址 

5.4 public abstract void getBluetoothLeService()获取服务实例BluetoothLeService

5.5 BluetoothLeService
    该类方法调用connect(String deviceAddress)传入地址进行重连操作
