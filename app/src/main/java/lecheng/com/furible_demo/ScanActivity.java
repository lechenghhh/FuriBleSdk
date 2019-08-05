package lecheng.com.furible_demo;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.lecheng.furiblesdk.BluetoothLeActivity;
import com.lecheng.furiblesdk.DeviceListActivity;

/*连接选择界面*/
public class ScanActivity extends DeviceListActivity {

    TextView tvDevice;
    EditText etAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        tvDevice = findViewById(R.id.tvDevice);
        etAddress = findViewById(R.id.etAddress);

        //点击按钮进行设备搜索
        findViewById(R.id.btnScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice(true);
            }
        });

        //选择设备进行连接
        findViewById(R.id.btnConn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScanActivity.this, ContentActivity.class)
                        .putExtra(BluetoothLeActivity.EXTRAS_DEVICE_NAME, "thisDevice")
                        .putExtra(BluetoothLeActivity.EXTRAS_DEVICE_ADDRESS, etAddress.getText().toString().trim()));
            }
        });

        //获取定位权限，蓝牙功能需要定位服务
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
            }
        }
    }

    @Override
    public void getLeDevice(BluetoothDevice bluetoothDevice) {
        //如果获取到空的设备可以自行排除
        tvDevice.append(bluetoothDevice.getName() + "-" + bluetoothDevice.getAddress() + "\n");
    }

    //菜单可能会有问题
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
