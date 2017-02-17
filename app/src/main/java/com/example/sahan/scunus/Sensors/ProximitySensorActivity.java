package com.example.sahan.scunus.Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class ProximitySensorActivity implements SensorEventListener {

    private Context context;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private ISensorListener sensorListener;

    public ProximitySensorActivity(Context context) {
        this.context = context;
        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorListener = SensorListenerImpl.getSensorListenerImpl();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        sensorListener.listen(sensorEvent, context);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Do Nothing
        // Override to comply Implementing SensorEventListener
    }

    public void onResume(){
        // Register a listen for the sensor.
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause(){
        // Be sure to unregister the sensor when the activity pauses.
        mSensorManager.unregisterListener(this);
    }
}
