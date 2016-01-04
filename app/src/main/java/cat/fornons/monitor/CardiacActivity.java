package cat.fornons.monitor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class CardiacActivity extends AppCompatActivity {
    private TextView tvNom, tvAddress;
    private String mDeviceName,mDeviceAddress;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardiac);

        tvNom= (TextView) findViewById(R.id.tvNom);
        tvAddress= (TextView) findViewById(R.id.tvAddress);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);


        tvNom.setText(mDeviceName);
        tvAddress.setText(mDeviceAddress);
    }
}
