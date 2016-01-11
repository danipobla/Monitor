package cat.fornons.monitor;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MonitorActivity extends AppCompatActivity {

    private Button btScan,btStop;
    private ListView lvBleDevices;
    private ProgressBar progressbar;
    private static final String LOG_TAG = "BleCollector";
    private int REQUEST_ENABLE_BT=1;
    private BleDeviceListAdapter mBleDeviceListAdapter;
    private boolean mScanning =false;
    private BluetoothAdapter mBleAdapter;
    private BluetoothLeScanner mBleScanner;
    private ScanCallback mScanCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);

        btScan = (Button) findViewById(R.id.btScan);

        btStop = (Button) findViewById(R.id.btStop);
        lvBleDevices = (ListView) findViewById(R.id.lvBleDevices);
        progressbar = (ProgressBar) findViewById(R.id.progressBar);

        mBleDeviceListAdapter = new BleDeviceListAdapter();
        lvBleDevices.setAdapter(mBleDeviceListAdapter);
        lvBleDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(LOG_TAG, "Dispositiu seleccionat");
                final BluetoothDevice device = mBleDeviceListAdapter.getDevice(position);
                if (device==null) return;
                if (mScanning){
                    mBleScanner.stopScan(mScanCallback);
                }
                final Intent intent = new Intent(view.getContext(),CardiacActivity.class);
                intent.putExtra(CardiacActivity.EXTRAS_DEVICE_NAME,device.getName());
                intent.putExtra(CardiacActivity.EXTRAS_DEVICE_ADDRESS,device.getAddress());
                startActivity(intent);

            }
        });

        mBleAdapter = ((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();


        if (mBleAdapter == null || !mBleAdapter.isEnabled()){
            Intent enableBtIntent = new Intent (BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT);
        }


        mBleScanner = mBleAdapter.getBluetoothLeScanner();



        mScanCallback = new ScanCallback() {
            @Override
            public void onScanResult(int callbackType, final ScanResult result) {
                super.onScanResult(callbackType, result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mBleDeviceListAdapter.addDevice(result.getDevice());
                        //}
                        mBleDeviceListAdapter.notifyDataSetChanged();
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
            connect();

            }
        });

        btStop = (Button) findViewById(R.id.btStop);
        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disconnect();
            }
        });
    }

    private void connect(){
        progressbar.setVisibility(View.VISIBLE);
        mBleDeviceListAdapter.clear();
        mBleDeviceListAdapter.notifyDataSetChanged();
        mBleScanner.startScan(mScanCallback);
        mScanning =true;
    }

    private void disconnect() {
        mBleScanner.stopScan(mScanCallback);
        progressbar.setVisibility(View.GONE);
        mScanning=false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        disconnect();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode== REQUEST_ENABLE_BT && resultCode== Activity.RESULT_CANCELED){
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);

    }




    public class BleDeviceListAdapter  extends BaseAdapter {

        private ArrayList<BluetoothDevice> mBleDevices;
        private LayoutInflater mInflator;

        public BleDeviceListAdapter(){
            mBleDevices = new ArrayList<>();
            mInflator=MonitorActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device){
            if (!mBleDevices.contains(device)){
                mBleDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position){
            return mBleDevices.get(position);
        }

        public void clear(){
            mBleDevices.clear();
        }


        @Override
        public int getCount() {
            return mBleDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return mBleDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (convertView == null) {
                convertView = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) convertView.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) convertView.findViewById(R.id.device_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            BluetoothDevice device = mBleDevices.get(position);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());

            return convertView;
        }
    }
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }


}
