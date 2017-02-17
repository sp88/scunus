package com.example.sahan.scunus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;


public class SenderActivity extends AppCompatActivity {

    private ProximitySensorActivity proximitySensorActivity;
    private AccelerometerSensorActivity accelerometerSensorActivity;
    public  EditText editText;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sender);
        proximitySensorActivity = new ProximitySensorActivity(this);
        accelerometerSensorActivity = new AccelerometerSensorActivity(this);

        editText = (EditText) findViewById(R.id.editText);

    }

    @Override
    protected void onResume() {
        super.onResume();
        proximitySensorActivity.onResume();
        accelerometerSensorActivity.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        proximitySensorActivity.onPause();
        accelerometerSensorActivity.onPause();
    }
}
