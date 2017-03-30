package com.example.sahan.scunus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.sahan.scunus.Sensors.AccelerometerSensorActivity;
import com.example.sahan.scunus.Sensors.ProximitySensorActivity;
import com.example.sahan.scunus.Sensors.SensorListenerImpl;



public class ReceiverActivity extends AppCompatActivity {

    private ProximitySensorActivity proximitySensorActivity;
    private AccelerometerSensorActivity accelerometerSensorActivity;
    private SensorListenerImpl sensorListener;
    private TextView hzTextView;
    private TextView msgTextView;

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receiver);
        hzTextView = (TextView) findViewById(R.id.hzTextView);
        msgTextView = (TextView) findViewById(R.id.msgTextView);
        proximitySensorActivity = new ProximitySensorActivity(this);
        accelerometerSensorActivity = new AccelerometerSensorActivity(this);

//        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,4096,0);
//
//        PitchDetectionHandler pdh = new PitchDetectionHandler() {
//
//            @Override
//            public void handlePitch(PitchDetectionResult result,AudioEvent e) {
//                final float pitchInHz = result.getPitch();
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        TextView text = (TextView) findViewById(R.id.hzTextView);
//                        text.setText(pitchInHz + "Hz " );
////                        Log.e("Hz", String.valueOf(pitchInHz));
//                    }
//                });
//            }
//        };
//        AudioProcessor p = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN, 22050, 4096, pdh);
//        dispatcher.addAudioProcessor(p);
//        new Thread(dispatcher,"Audio Dispatcher").start();

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
