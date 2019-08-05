package lecheng.com.furible_demo;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.lecheng.furiblesdk.BluetoothLeActivity;
import com.lecheng.furiblesdk.BluetoothLeService;

import static com.lecheng.furiblesdk.BluetoothLeActivity.EXTRAS_DEVICE_NAME;

public class ContentActivity extends BluetoothLeActivity {

    TextView tv1, tv2;
    String name, address;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        tv1 = findViewById(R.id.tvDevice);
        tv2 = findViewById(R.id.tvContent);
        tv1.setText(name + "-" + address);
    }

    //当接受到数据后该方法会自动回调
    @Override
    public void getData(String s, String s1, String s2, String s3) {
        tv2.setText(s);
    }

    @Override
    public void getConnectionState(String s, boolean b) {
    }

    @Override
    public void getConnectionInfo(String s, String s1) {
        name = s;
        address = s1;
    }

    @Override
    public void getBluetoothLeService(BluetoothLeService bluetoothLeService) {

    }
}
