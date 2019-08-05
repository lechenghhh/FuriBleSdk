package lecheng.com.furible_demo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.lecheng.furiblesdk.BluetoothLeActivity;
import com.lecheng.furiblesdk.DeviceListActivity;
import com.lecheng.furiblesdk.DeviceListSelectActivity;

/*连接选择界面*/
public class ScanActivity extends DeviceListActivity {

    TextView tv1;
    EditText etAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv1 = findViewById(R.id.tv1);
        etAddress = findViewById(R.id.etAddress);
        findViewById(R.id.btnConn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ScanActivity.this, ContentActivity.class)
                        .putExtra(BluetoothLeActivity.EXTRAS_DEVICE_NAME, "thisDevice")
                        .putExtra(BluetoothLeActivity.EXTRAS_DEVICE_ADDRESS, etAddress.getText().toString().trim()));
            }
        });
        findViewById(R.id.btnScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanLeDevice(true);
            }
        });
//获取定位权限
if (Build.VERSION.SDK_INT >= 23) {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 10);
    }
}
    }

    @Override
    public void getLeDevice(BluetoothDevice bluetoothDevice) {
        tv1.append(bluetoothDevice.getName() + "-" + bluetoothDevice.getAddress() + "\n");
    }

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
