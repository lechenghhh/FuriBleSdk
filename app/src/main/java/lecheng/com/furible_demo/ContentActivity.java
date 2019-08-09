package lecheng.com.furible_demo;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.lecheng.furiblesdk.BluetoothLeActivity;
import com.lecheng.furiblesdk.BluetoothLeService;

import static com.lecheng.furiblesdk.BluetoothLeActivity.EXTRAS_DEVICE_NAME;

/**
 * 用于展示数据的界面，使用时请注册manifest标签
 * 由于需要各种权限，匹配的协议，以及bleservice的支持，因此全部封装在该父类中
 **/

public class ContentActivity extends BluetoothLeActivity {

    private TextView tvDevice, tvGetData;
    private String name, address;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        tvDevice = findViewById(R.id.tvDevice);
        tvGetData = findViewById(R.id.tvGetData);
        tvDevice.setText(name + "-" + address);
    }

    /*当接受到数据后该方法会自动回调，并且只有福日电子秤才有数据 */
    @Override
    public void getData(String s, String s1, String s2, String s3) {
        tvGetData.setText(s);
    }

    @Override
    public void getConnectionState(String s, boolean b) {
    }

    @Override
    public void getConnectionInfo(String name, String address) {
        this.name = name;
        this.address = address;
    }

    @Override
    public void getBluetoothLeService(BluetoothLeService bluetoothLeService) {
    }
}
