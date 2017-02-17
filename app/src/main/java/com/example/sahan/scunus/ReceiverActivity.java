package com.example.sahan.scunus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.sahan.scunus.Sensors.AccelerometerSensorActivity;
import com.example.sahan.scunus.Sensors.ProximitySensorActivity;
import com.example.sahan.scunus.Sensors.SensorListenerImpl;


public class ReceiverActivity extends AppCompatActivity {

    private ProximitySensorActivity proximitySensorActivity;
    private AccelerometerSensorActivity accelerometerSensorActivity;
    private SensorListenerImpl sensorListener;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receiver);
        proximitySensorActivity = new ProximitySensorActivity(this);
        accelerometerSensorActivity = new AccelerometerSensorActivity(this);

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
