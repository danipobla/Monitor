package cat.fornons.monitor;

import android.app.Service;
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
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class HRService extends Service {

    HRMesurent mHRMesurement;
    String mDeviceAddress;
    private BluetoothAdapter mBluetoohAdapter;
    private BluetoothManager mBluetoohManager;
    private BluetoothDevice device;
    private BluetoothGatt mGatt = null;
    private BluetoothGattCharacteristic characteristic;
    OutputStreamWriter osw;
    InputStreamReader isr;
    File file;
    SimpleDateFormat data = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss'Z'", Locale.getDefault());


    private final IBinder binder = new mBinder();

    // public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";


    public class mBinder extends Binder {
        HRService getService() {
            return HRService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();


        if (mGatt == null) {
            mHRMesurement = new HRMesurent();
            mDeviceAddress = intent.getStringExtra("EXTRAS_DEVICE_ADDRESS");
            mBluetoohManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoohAdapter = mBluetoohManager.getAdapter();
            device = mBluetoohAdapter.getRemoteDevice(mDeviceAddress);
            mGatt = device.connectGatt(getBaseContext(), false, gattCallback);
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File directory = new File (sdCard.getAbsolutePath()+ "/Monitor");
                directory.mkdirs();
                file = new File (directory,"monitor.txt");
                FileOutputStream fOut = new FileOutputStream(file);
                osw = new OutputStreamWriter(fOut);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return START_REDELIVER_INTENT;
    }


    @Override
    public void onDestroy() {
        if (mGatt != null) {
            mGatt.disconnect();
            mGatt = null;
            try {
                osw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Toast.makeText(this, "service Destroyed", Toast.LENGTH_SHORT).show();
        try {
            FileInputStream fIn = new FileInputStream(file);
            isr= new InputStreamReader(fIn);
            BufferedReader reader = new BufferedReader(isr);
            String line =reader.readLine();
            while (line != null){
                Log.i("READ", line);
                line =reader.readLine();
            }
            } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.onDestroy();
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
                    broadcastUpdate("ACTION_GATT_CONNECTED");
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    if (mGatt != null) {
                        mGatt.discoverServices();
                    }
                    broadcastUpdate("ACTION_GATT_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {

            List<BluetoothGattService> services = gatt.getServices();

            for (BluetoothGattService service : services) {
                if (service.getUuid().equals(UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb"))) {
                    characteristic = service.getCharacteristic(UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb"));

                    gatt.readCharacteristic(characteristic);
                    gatt.setCharacteristicNotification(characteristic, true);
                    BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    gatt.writeDescriptor(descriptor);
                    broadcastUpdate("ACTION_SERVICE_DISCOVERED");

                }
            }
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            byte[] data = characteristic.getValue();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            int flag = characteristic.getProperties();
            int format;

            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
            }
            final int heartRate = characteristic.getIntValue(format, 1);

            Log.i("READ", String.format("Received heart rate: %d - %s", heartRate, data.format(new Date())));
            nou_valor(String.valueOf(heartRate), data.format(new Date()));

        }

    };

    public void nou_valor(final String valor, final String data) {
        mHRMesurement.setHRM(valor, data);
        Intent intent = new Intent(this, HRWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(getApplicationContext()).getAppWidgetIds(new ComponentName(getApplication(), HRWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        intent.putExtra("valor", valor);
        sendBroadcast(intent);
        broadcastUpdate("ACTION_DATA_AVAILABLE", valor);

        try {
            osw.write(mHRMesurement.getJSON()+System.getProperty("line.separator"));
            osw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("JSON",mHRMesurement.getJSON().toString());
    }


    private void broadcastUpdate(final String action) {
        Intent broadcastIntent = new Intent(action);
        sendBroadcast(broadcastIntent);

    }

    private void broadcastUpdate(final String action, String valor) {
        Intent broadcastIntent = new Intent(action);
        broadcastIntent.putExtra("valor", valor);
        sendBroadcast(broadcastIntent);
    }


}
