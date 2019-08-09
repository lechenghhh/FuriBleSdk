package lecheng.com.furible_demo;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lecheng.furiblesdk.BluetoothLeActivity;
import com.lecheng.furiblesdk.DeviceListActivity;

/*设备扫描/连接选择界面*/
public class ScanActivity extends DeviceListActivity {

    LinearLayout lvDevices;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        lvDevices = findViewById(R.id.lvDevices);

        //点击按钮进行设备搜索
        findViewById(R.id.btnScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice(true);
            }
        });
    }

    /*开启蓝牙功能*/
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onResume() {
        super.onResume();
        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 检查设备上是否支持蓝牙
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // 为了确保设备上蓝牙能使用, 如果当前蓝牙设备没启用,弹出对话框向用户要求授予权限来启用
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    /*当扫描到设备时，该方法会回调*/
    @Override
    public void getLeDevice(final BluetoothDevice bluetoothDevice) {        //如果获取到空的设备可以自行排除
        TextView tvDevice = new TextView(this);
        tvDevice.setPadding(10, 10, 10, 10);
        tvDevice.setText(bluetoothDevice.getName() + " - " + bluetoothDevice.getAddress());
        tvDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                /*跳转到要展示蓝牙数据的界面*/
                startActivity(new Intent(ScanActivity.this, ContentActivity.class)
                        .putExtra(BluetoothLeActivity.EXTRAS_DEVICE_NAME, bluetoothDevice.getName() + "")
                        .putExtra(BluetoothLeActivity.EXTRAS_DEVICE_ADDRESS, bluetoothDevice.getAddress()));
            }
        });
        lvDevices.addView(tvDevice);
    }

    /*菜单功能在某些版本可能会有异常*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        try {
            super.onCreateOptionsMenu(menu);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
