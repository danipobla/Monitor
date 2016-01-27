package cat.fornons.monitor;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothClass;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Shader;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


public class CardiacActivity extends AppCompatActivity {
    private TextView tvAddress,tvCor;
    private ImageView ivparat,ivbaix,ivmitja,ivalt;
    private String mDeviceAddress;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private int PARAT=1;
    private int CAMINANT=3;
    private int CORRENT=8;
    HRService serviceBinder;
    Intent intent;
    IntentFilter mIntentFilter;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceBinder = ((HRService.mBinder) service).getService();
            serviceBinder.mDeviceAddress = mDeviceAddress;
            startService(intent);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            serviceBinder=null;

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardiac);


        tvAddress= (TextView) findViewById(R.id.tvAddress);
        tvCor = (TextView) findViewById(R.id.tvCor);

        ivparat=(ImageView)findViewById(R.id.ivparat);
        ivbaix=(ImageView)findViewById(R.id.ivbaix);
        ivmitja=(ImageView)findViewById(R.id.ivmitja);
        ivalt=(ImageView)findViewById(R.id.ivalt);

        mDeviceAddress = getIntent().getStringExtra(EXTRAS_DEVICE_ADDRESS);

        tvAddress.setText(mDeviceAddress);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    public void startService(View view){
        intent =new Intent(getBaseContext(), HRService.class);
        intent.putExtra("EXTRAS_DEVICE_ADDRESS",mDeviceAddress);
        startService(intent);

    }

    public void stopService(View view)
    {
        stopService(new Intent(getBaseContext(), HRService.class));
    }

    public void  sendLog(View view) {
        new SendJsonServer().execute();

    }


    @Override
    protected void onResume() {
        super.onResume();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("ACTION_GATT_CONNECTED");
        mIntentFilter.addAction("ACTION_GATT_DISCONNECTED");
        mIntentFilter.addAction("ACTION_SERVICES_DISCOVERED");
        mIntentFilter.addAction("ACTION_DATA_AVAILABLE");
        registerReceiver(intentReceiver, mIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(intentReceiver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.device:
                intent = new Intent(this,MonitorActivity.class);
                startActivity(intent);
                 return true;
            case R.id.user:
                intent = new Intent(this,UserActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private BroadcastReceiver intentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action= intent.getAction();
            if (action.equals("ACTION_DATA_AVAILABLE")){
                tvCor.setText(intent.getStringExtra("valor"));
                int valor= Integer.parseInt(intent.getStringExtra("intensity"));
                if (valor<PARAT) {
                    ivparat.setBackgroundColor(Color.RED);
                    ivbaix.setBackgroundColor(Color.TRANSPARENT);
                    ivmitja.setBackgroundColor(Color.TRANSPARENT);
                    ivalt.setBackgroundColor(Color.TRANSPARENT);
                }else if (valor< CAMINANT){
                    ivparat.setBackgroundColor(Color.TRANSPARENT);
                    ivbaix.setBackgroundColor(Color.RED);
                    ivmitja.setBackgroundColor(Color.TRANSPARENT);
                    ivalt.setBackgroundColor(Color.TRANSPARENT);

                }else if (valor <CORRENT){
                    ivparat.setBackgroundColor(Color.TRANSPARENT);
                    ivbaix.setBackgroundColor(Color.TRANSPARENT);
                    ivmitja.setBackgroundColor(Color.RED);
                    ivalt.setBackgroundColor(Color.TRANSPARENT);
                }else{
                    ivparat.setBackgroundColor(Color.TRANSPARENT);
                    ivbaix.setBackgroundColor(Color.TRANSPARENT);
                    ivmitja.setBackgroundColor(Color.TRANSPARENT);
                    ivalt.setBackgroundColor(Color.RED);
                }

            }
            else if (action.equals("ACTION_GATT_CONNECTED")){
   //             notificacio(action.toString());
            }else if(action.equals("ACTION_GATT_DISCONNECTED")){
   //             notificacio(action.toString());

            }else if(action.equals("ACTION_SERVICE_DISCOVERED")){

            }
        }
    };



    private class SendJsonServer extends AsyncTask <String,Void,String>{

        @Override
        protected String doInBackground(String... params) {
            JSONObject hrm = new JSONObject();
            HttpURLConnection urlConnection = null;
            byte[] data;

            try {
                SharedPreferences userPrefs = getSharedPreferences("cat.fornons.UsingPreferences_preferences", MODE_PRIVATE);
                hrm.put("name",userPrefs.getString("name", "--"));
                hrm.put("age", userPrefs.getString("age", "--"));
                hrm.put("height", userPrefs.getString("height", "--"));
                hrm.put("weight", userPrefs.getString("weight", "--"));


                JSONArray jsonArray = new JSONArray();

                File sdCard = Environment.getExternalStorageDirectory();
                File file = new File (sdCard.getAbsolutePath()+ "/Monitor/monitor.txt");
                FileInputStream fIn = new FileInputStream(file);
                InputStreamReader isr= new InputStreamReader(fIn);
                BufferedReader reader = new BufferedReader(isr);
                String line =reader.readLine();
                while (line != null) {
                    jsonArray.put(new JSONObject(line));
                    line = reader.readLine();

                }
                hrm.put("hrm", jsonArray);

                data = hrm.toString().getBytes();
                //Enviem POST
                URL url = new URL("http://fornons.sytes.net:1234/dades/");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setFixedLengthStreamingMode(data.length);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                writeStream(out, data);


                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return readStream(in);
            }
           catch (Exception e){
                e.printStackTrace();
            }
            finally{
                urlConnection.disconnect();
            }
            return null;
        }
        protected void onProgressUpdate(Integer... progress) {
          //  setProgressPercent(progress[0]);
        }


        protected void onPostExecute(String result) {
            Toast.makeText(getApplicationContext(),result , Toast.LENGTH_SHORT).show();
            Log.i("Insert", result);
        }

    }


    private String writeStream(OutputStream out, byte[] hrm){
        try {
            out.write(hrm);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String readStream(InputStream in){
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder result = new StringBuilder();
        String line;
        try {
            while((line = reader.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}


