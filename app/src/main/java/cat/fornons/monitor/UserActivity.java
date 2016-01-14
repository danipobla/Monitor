package cat.fornons.monitor;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class UserActivity extends AppCompatActivity {
    SharedPreferences userPrefs;
    private TextView name;
    private TextView age;
    private TextView height;
    private TextView weight;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        name = (TextView) findViewById(R.id.tvName);
        age = (TextView) findViewById(R.id.tvAge);
        height = (TextView) findViewById(R.id.tvHeight);
        weight = (TextView) findViewById(R.id.tvWeight);
        userPrefs = getSharedPreferences("cat.fornons.UsingPreferences_preferences", MODE_PRIVATE);
        name.setText(userPrefs.getString("name", ""));
        age.setText(userPrefs.getString("age", ""));
        height.setText(userPrefs.getString("height", ""));
        weight.setText(userPrefs.getString("weight", ""));

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void savePref(View view){
        SharedPreferences.Editor userPrefsEditor = userPrefs.edit();
        userPrefsEditor.putString("name",name.getText().toString());
        userPrefsEditor.putString("age",age.getText().toString());
        userPrefsEditor.putString("weight",weight.getText().toString());
        userPrefsEditor.putString("height",height.getText().toString());
        userPrefsEditor.commit();
        finish();
    }

    public void cleanPref(View view){
        name.setText("");
        age.setText("");
        height.setText("");
        weight.setText("");

    }
}
