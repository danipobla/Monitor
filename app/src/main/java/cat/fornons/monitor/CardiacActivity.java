package cat.fornons.monitor;

import android.appwidget.AppWidgetManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

public class CardiacActivity extends AppCompatActivity {
    private TextView tvNom, tvAddress,tvCor,tvWidget;
    private Button btStart, btStop;
    private String mDeviceName,mDeviceAddress;
    private BluetoothAdapter mBluetoohAdapter;
    private BluetoothManager mBluetoohManager;
    private BluetoothDevice device;
    private BluetoothGatt mGatt = null;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private SharedPreferences prefs;
    private  String recorda;

    private BluetoothGattCharacteristic characteristic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardiac);

        tvNom= (TextView) findViewById(R.id.tvNom);
        tvWidget= (TextView) findViewById(R.id.tvWidget);

        tvAddress= (TextView) findViewById(R.id.tvAddress);
        tvCor = (TextView) findViewById(R.id.tvCor);
        btStart=(Button) findViewById(R.id.btStartHRM);
        btStop=(Button)findViewById(R.id.btStopHRM);
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGatt == null) {
                    btStart.setVisibility(View.GONE);
                    btStop.setVisibility(View.VISIBLE);
                    mGatt = device.connectGatt(v.getContext(), false, gattCallback);
                }
                mGatt.discoverServices();
            }
        });

        btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGatt != null) {
                    desconnectar();
                }

            }
        });


        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);


        tvNom.setText(mDeviceName);
        tvAddress.setText(mDeviceAddress);
        mBluetoohManager= (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoohAdapter = mBluetoohManager.getAdapter();
        device = mBluetoohAdapter.getRemoteDevice(mDeviceAddress);





    }
    private void desconnectar(){
        if (mGatt!=null) {
            mGatt.disconnect();
            mGatt = null;
        }
        btStart.setVisibility(View.VISIBLE);
        btStop.setVisibility(View.GONE);
        nou_valor("");
    }


    @Override
    protected void onDestroy() {
        desconnectar();
        close();
        super.onDestroy();
    }



    public void close() {
        if (mGatt == null) {
            return;
        }
        mGatt.close();
        mGatt = null;
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    mGatt.connect();
                    mGatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    if (mGatt !=null){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                desconnectar();
                            }
                        });
                    }
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            List<BluetoothGattService> services = gatt.getServices();

            for (BluetoothGattService service : services) {
                if (service.getUuid().equals(UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb"))){
                    characteristic = service.getCharacteristic(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"));

                    gatt.readCharacteristic(characteristic);
                    gatt.setCharacteristicNotification(characteristic, true);
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descriptor);
                }
            }



        }



        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            byte[] data = characteristic.getValue();
            Log.i("Read", data.toString());

        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
             int flag = characteristic.getProperties();
            int format = -1;

            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.i("w", "Heart rate format UINT16.");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.i("w", "Heart rate format UINT8.");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.i("READ", String.format("Received heart rate: %d", heartRate));
            nou_valor(String.valueOf(heartRate));

        }

    };

    public void nou_valor( final String valor){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                tvCor.setText(valor);
                prefs = getSharedPreferences("preferencies", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("hr",valor);
                editor.commit();

                Intent intent = new Intent(getApplication().getApplicationContext(),HRWidget.class );
                intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
                int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), HRWidget.class));
                intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
                sendBroadcast(intent);

            }
        });
    }

}

