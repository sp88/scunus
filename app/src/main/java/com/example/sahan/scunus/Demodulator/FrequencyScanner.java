package com.example.sahan.scunus.Demodulator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.sahan.scunus.Constants;
import com.example.sahan.scunus.R;

import org.jtransforms.fft.DoubleFFT_1D;


public class FrequencyScanner{//} extends AppCompatActivity {
    private double[] window;
    private AudioRecord record;
    private int bufferSize;
    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode){
//            case Constants.REQUEST_RECORD_AUDIO_PERMISSION:
//                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                break;
//        }
//        if (!permissionToRecordAccepted ){
//            finish();
//        }
//
//    }

    public FrequencyScanner() {
        window = null;

    }


    public void recordSound(){
        bufferSize = AudioRecord.getMinBufferSize(Constants.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        record = new AudioRecord(MediaRecorder.AudioSource.MIC, Constants.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

        Log.e("bufferSize", String.valueOf(bufferSize));
        Log.e("audio record status", String.valueOf(record.getState()));

        if (record.getState() != AudioRecord.STATE_INITIALIZED) {
            Log.e("audio record status", "Audio Record can't initialize!");
            return;
        }

        short[] audioBuffer = new short[bufferSize/2];
        record.startRecording();

        // read from record
        int numberOfShort = record.read(audioBuffer, 0, audioBuffer.length);
        Log.e("read from record", String.valueOf(numberOfShort));

        // extract freq
        final double freq = extractFrequency(audioBuffer, Constants.SAMPLE_RATE);
        Log.e("freq", String.valueOf(freq));

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                TextView text = (TextView) findViewById(R.id.hzTextView);
//                        text.setText(freq + "Hz " );
//            }
//        });

        record.stop();
        record.release();
        record = null;
    }

    /** extract the dominant frequency from 16bit PCM data.
     * @param sampleData an array containing the raw 16bit PCM data.
     * @param sampleRate the sample rate (in HZ) of sampleData
     * @return an approximation of the dominant frequency in sampleData
     */
    public double extractFrequency(short[] sampleData, int sampleRate) {
        /* sampleData + zero padding */
        DoubleFFT_1D fft = new DoubleFFT_1D(sampleData.length + 24 * sampleData.length);
        double[] a = new double[(sampleData.length + 24 * sampleData.length) * 2];

        System.arraycopy(applyWindow(sampleData), 0, a, 0, sampleData.length);
        fft.realForward(a);

        /* find the peak magnitude and it's index */
        double maxMag = Double.NEGATIVE_INFINITY;
        int maxInd = -1;

        for(int i = 0; i < a.length / 2; ++i) {
            double re  = a[2*i];
            double im  = a[2*i+1];
            double mag = Math.sqrt(re * re + im * im);

            if(mag > maxMag) {
                maxMag = mag;
                maxInd = i;
            }
        }

        /* calculate the frequency */
        return (double)sampleRate * maxInd / (a.length / 2);
    }

    /** build a Hamming window filter for samples of a given size
     * See http://www.labbookpages.co.uk/audio/firWindowing.html#windows
     * @param size the sample size for which the filter will be created
     */
    private void buildHammWindow(int size) {
        if(window != null && window.length == size) {
            return;
        }
        window = new double[size];
        for(int i = 0; i < size; ++i) {
            window[i] = .54 - .46 * Math.cos(2 * Math.PI * i / (size - 1.0));
        }
    }

    /** apply a Hamming window filter to raw input data
     * @param input an array containing unfiltered input data
     * @return a double array containing the filtered data
     */
    private double[] applyWindow(short[] input) {
        double[] res = new double[input.length];

        buildHammWindow(input.length);
        for(int i = 0; i < input.length; ++i) {
            res[i] = (double)input[i] * window[i];
        }
        return res;
    }
}
