package com.example.sahan.scunus.Sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;


public class AccelerometerSensorActivity implements SensorEventListener {
    private Context context;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ISensorListener sensorListener;

    public AccelerometerSensorActivity(Context context) {
        this.context = context;
        // Get an instance of the sensor service, and use that to get an instance of
        // a particular sensor.
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorListener = SensorListenerImpl.getSensorListenerImpl();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
//        Log.e("Accelerometer", "x-"+sensorEvent.values[0]+" y-"+sensorEvent.values[1]+" z-"+sensorEvent.values[2]);
        sensorListener.listen(sensorEvent, context);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        // Do Nothing
        // Override to comply Implementing SensorEventListener
    }

    public void onResume(){
        // Register a listen for the sensor.
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause(){
        // Be sure to unregister the sensor when the activity pauses.
        mSensorManager.unregisterListener(this);
    }
}
