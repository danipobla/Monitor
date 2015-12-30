package cat.fornons.monitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MonitorActivity extends AppCompatActivity {

    private Button btScan,btStop;
    private TextView tvTrobat;
    private int trobat =0;
    private static final String LOG_TAG = "BleCollector";
    private int REQUEST_ENABLE_BT=1;


    private BluetoothAdapter mBleAdapter;
    private BluetoothLeScanner mBleScanner;
    private ScanCallback mScanCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        tvTrobat= (TextView) findViewById(R.id.tvTrobat);
        btScan = (Button) findViewById(R.id.btScan);


        mBleAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();


        if (mBleAdapter == null || !mBleAdapter.isEnabled()){
            Intent enableBtIntent = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }


        mBleScanner = mBleAdapter.getBluetoothLeScanner();
       // ScanSettings mBleSettings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();








        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, final ScanResult result) {
                super.onScanResult(callbackType, result);
                trobat++;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tvTrobat.setText(result.getDevice().getName());
                    //tvTrobat.setText(Integer.toString(trobat));
                    }
                });
                Log.e(LOG_TAG, "Dispositiu Trobat");
            }

            @Override
            public void onBatchScanResults(List<ScanResult> results) {
                super.onBatchScanResults(results);
            }

            @Override
            public void onScanFailed(int errorCode) {
                super.onScanFailed(errorCode);
            }
        };


        btScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBleScanner.startScan(mScanCallback);

            }
        });

        btStop = (Button) findViewById(R.id.btStop);
        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBleScanner.stopScan(mScanCallback);
                trobat=0;
            }
        });





    }


}
