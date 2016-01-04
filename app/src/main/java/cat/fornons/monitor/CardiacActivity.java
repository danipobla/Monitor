package cat.fornons.monitor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.UUID;

public class CardiacActivity extends AppCompatActivity {
    private TextView tvNom, tvAddress;
    private String mDeviceName,mDeviceAddress;
    private BluetoothAdapter mBluetoohAdapter;
    private BluetoothManager mBluetoohManager;
    private BluetoothGatt mGatt = null;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private BluetoothGattCharacteristic characteristic;

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
        mBluetoohManager= (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoohAdapter = mBluetoohManager.getAdapter();
        BluetoothDevice device = mBluetoohAdapter.getRemoteDevice(mDeviceAddress);

        mGatt = device.connectGatt(this, false, gattCallback);
        mGatt.discoverServices();
    }
    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            List<BluetoothGattService> services = gatt.getServices();
            Log.i("onServicesDiscovered", services.toString());
            Log.i("Servei", services.get(2).getCharacteristics().get(0).toString());
            characteristic = services.get(2).getCharacteristic(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"));
            gatt.readCharacteristic(characteristic);
            gatt.setCharacteristicNotification(characteristic, true);
           BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            gatt.writeDescriptor(descriptor);


        }



        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic
                                                 characteristic, int status) {
            byte[] data = characteristic.getValue();
            Log.i("Read", data.toString());
            //gatt.disconnect();

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

        }

    };
}
