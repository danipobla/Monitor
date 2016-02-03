package cat.fornons.monitor;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    EditText username,password;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.etNom);
        password = (EditText) findViewById(R.id.etPasword);

    }

    public void login(View view) {
        JSONObject user = new JSONObject();
        try {
            user.put("password", password.getText());
            user.put("username", username.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }


        new LoginJsonServer().execute(user);



    }


    private class LoginJsonServer extends AsyncTask<JSONObject, Void, String> {

        @Override
        protected String doInBackground(JSONObject... params) {
            HttpURLConnection urlConnection = null;
            byte[] data;

            try {

                data = params[0].toString().getBytes();

                URL url = new URL("http://fornons.sytes.net:1234/login/");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setFixedLengthStreamingMode(data.length);

                OutputStream out = new BufferedOutputStream(urlConnection.getOutputStream());
                writeStream(out, data);

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                return readStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            //  setProgressPercent(progress[0]);
        }


        protected void onPostExecute(String result) {
            if (result.equals("NO")){
                Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent(getBaseContext(), CardiacActivity.class);
                startActivity(intent);

            }
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