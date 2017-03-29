package com.example.sahan.scunus.Sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.util.Log;
import android.widget.EditText;

import com.example.sahan.scunus.Arithmetic.SamplingLoop;
import com.example.sahan.scunus.Constants;
import com.example.sahan.scunus.Demodulator.FrequencyScanner;
import com.example.sahan.scunus.Generator.Modulator;
import com.example.sahan.scunus.Generator.SoundGenerator;
import com.example.sahan.scunus.R;

public class SensorListenerImpl implements ISensorListener {

    private static SensorListenerImpl sensorListener;
    private boolean isNear;
    private float currentAccel;
    private float lastAccel;
    private float mAccel;
    private boolean isReadyToProcess;
    private SamplingLoop samplingLoop;
    private SoundGenerator soundGenerator;

    private SensorListenerImpl() {
        currentAccel = Sensor.TYPE_GRAVITY;
        mAccel = 0.0f;
    }

    public static SensorListenerImpl getSensorListenerImpl() {
        if (sensorListener == null) {
            sensorListener = new SensorListenerImpl();
            return sensorListener;
        }
        return sensorListener;
    }


    @Override
    public void listen(SensorEvent sensorEvent, Context context) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            int x = (int) sensorEvent.values[0];
            int y = (int) sensorEvent.values[1];
            int z = (int) sensorEvent.values[2];

            lastAccel = currentAccel;
            currentAccel = (float) Math.sqrt((x*x + y*y + z*z));
            mAccel = currentAccel - lastAccel;

            // If the other Phone is in proximity and stopped moving, then proceed.
            if(isNear && isReadyToProcess && ( mAccel == 0.0 )){
                // set isReadyToProcess
                isReadyToProcess = false;

//                Log.e("Accelerometer", "x-" + x + " y-" + y + " z-" + z);
//                Log.e("Accel", "" + mAccel);
//                Log.e( "CURRENT Activity", context.getClass().getCanonicalName());

                // Use the GenerateSound class to generate sound from the given text
                if (Constants.SENDER_ACTIVITY.equals(context.getClass().getCanonicalName())) {
                    Activity a = (Activity) context;
                    String msg = ((EditText) a.findViewById(R.id.editText)).getText().toString();
                    Log.e("Activity", "Sender");
                    if(soundGenerator == null){
                        soundGenerator = new SoundGenerator(msg);
                        soundGenerator.start();
                    }
                } else if (Constants.RECEIVER_ACTIVITY.equals(context.getClass().getCanonicalName())) {
                    Log.e("Activity", "Receiver");
//                    new FrequencyScanner().recordSound();
                    if(samplingLoop == null) {
                        samplingLoop = new SamplingLoop(context);
                        samplingLoop.start();
                    }
                }
            }

        } else if (sensorEvent.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (sensorEvent.values[0] == 0.0) {
                Log.e("Proximity", "Near");
                isNear = true;
            } else {
                Log.e("Proximity", "Far");
                isNear = false;
                isReadyToProcess = true;
                if(samplingLoop != null){
                    samplingLoop.finish();
                    samplingLoop = null;
                }
                soundGenerator = null;
            }
        }
    }
}
