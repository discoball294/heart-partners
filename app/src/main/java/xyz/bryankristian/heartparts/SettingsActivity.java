package xyz.bryankristian.heartparts;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import xyz.bryankristian.heartparts.customfonts.MyEditText;
import xyz.bryankristian.heartparts.customfonts.MyRegulerText;
import xyz.bryankristian.heartparts.heartpartners.R;

public class SettingsActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MyRegulerText btnSave;
    MyEditText txtAveraging;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        btnSave = findViewById(R.id.btn_save_settings);
        txtAveraging = findViewById(R.id.txtAveraging);
        txtAveraging.setText(String.valueOf(sharedPreferences.getInt("interval",1)));

        btnSave.setOnClickListener(v -> {
            int interval = Integer.parseInt(txtAveraging.getText().toString().trim());
            editor.putInt("interval",interval);
            editor.apply();
            Toast.makeText(this, ""+sharedPreferences.getInt("interval",1), Toast.LENGTH_SHORT).show();
        });


    }
}
