package com.example.sahan.scunus.Sensors;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.os.AsyncTask;
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
    private volatile SamplingLoop samplingLoop;
    private volatile SoundGenerator soundGenerator;
    private PlayerAsync playerAsync;
    private RecorderAsync recorderAsync;

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
                    if(playerAsync == null){
                        playerAsync = new PlayerAsync();
                        playerAsync.execute(new String[]{msg});
                    }
                } else if (Constants.RECEIVER_ACTIVITY.equals(context.getClass().getCanonicalName())) {
                    Log.e("Activity", "Receiver");
//                    new FrequencyScanner().recordSound();
                    if(recorderAsync == null) {
                        recorderAsync = new RecorderAsync();
                        recorderAsync.execute();
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
                Log.e("recorderAsync", String.valueOf(recorderAsync == null));
                if(recorderAsync != null){
                    recorderAsync.cancel(true);
                    recorderAsync = null;
                    Log.e("recorderAsync", "null");
                }
                Log.e("playerAsync", String.valueOf(playerAsync == null));
                if(playerAsync != null){
                    playerAsync.cancel(true);
                    playerAsync = null;
                    Log.e("playerAsync", "null");
                }
            }
        }
    }

    /**
     *
     */
    private class RecorderAsync extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            if(!isCancelled()) {
                samplingLoop = new SamplingLoop();
                samplingLoop.run();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            if(samplingLoop != null) {
                samplingLoop.finish();
                samplingLoop = null;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
            if(samplingLoop != null) {
                samplingLoop.finish();
                samplingLoop = null;
            }
        }
    }

    /**
     *
     */
    private class PlayerAsync extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            if(!isCancelled()) {
                soundGenerator = new SoundGenerator(strings[0]);
                soundGenerator.run();
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            if(soundGenerator != null) {
                soundGenerator.finish();
                soundGenerator = null;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
            if(soundGenerator != null) {
                soundGenerator.finish();
                soundGenerator = null;
            }
        }
    }
}
