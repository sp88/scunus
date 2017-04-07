package com.example.sahan.scunus.Arithmetic;/* Copyright 2014 Eddy Xiao <bewantbe@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Activity;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import com.example.sahan.scunus.Constants;
import com.example.sahan.scunus.Demodulator.Demodulator;
import com.example.sahan.scunus.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Read a snapshot of audio data at a regular interval, and compute the FFT
 * @author suhler@google.com
 *         bewantbe@gmail.com
 */

public class SamplingLoop extends Thread {
    private static final int BYTE_OF_SAMPLE = 2;
    private final String TAG = "Arithmetic.SamplingLoop";
    private volatile boolean isRunning = true;
    private volatile boolean isPaused1 = false;
    private STFT stft;   // use with care
    final int RECORDER_AGC_OFF = MediaRecorder.AudioSource.VOICE_RECOGNITION;
    int audioSourceId = RECORDER_AGC_OFF;
    Activity activity;
    private int scunusCounter = 0;
    public SamplingLoop(Context context){ //AnalyzerActivity _activity, AnalyzerParameters _analyzerParam) {
        activity = (Activity) context;
    }

    @Override
    public void run() {
        AudioRecord record;

        int minBytes = AudioRecord.getMinBufferSize(Constants.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        if (minBytes == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "Arithmetic.SamplingLoop::run(): Invalid AudioRecord parameter.\n");
            return;
        }

        /*
          Develop -> Reference -> AudioRecord
             Data should be read from the audio hardware in chunks of sizes
             inferior to the total recording buffer size.
         */
        // Determine size of buffers for AudioRecord and AudioRecord::read()
        int readChunkSize    = Constants.FFT_LENGTH/2;  // /2 due to overlapped analyze window
        readChunkSize        = Math.min(readChunkSize, 2048);  // read in a smaller chunk, hopefully smaller delay
        int bufferSampleSize = Math.max(minBytes / BYTE_OF_SAMPLE, Constants.FFT_LENGTH/2) * 2;
        // tolerate up to about 1 sec.
        bufferSampleSize = (int)Math.ceil(1.0 * Constants.SAMPLE_RATE / bufferSampleSize) * bufferSampleSize;

        // Use the mic with AGC turned off. e.g. VOICE_RECOGNITION for measurement
        // The buffer size here seems not relate to the delay.
        // So choose a larger size (~1sec) so that overrun is unlikely.
        try {
            record = new AudioRecord(RECORDER_AGC_OFF, Constants.SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, BYTE_OF_SAMPLE * bufferSampleSize);
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Fail to initialize recorder.");
            return;
        }
        Log.i(TAG, "com.example.sahan.scunus.Arithmetic.SamplingLoop::Run(): Starting recorder... \n" +
                "  source          : " + /*analyzerParam.getAudioSourceName() +*/ "\n" +
                String.format("  sample rate     : %d Hz (request %d Hz)\n", record.getSampleRate(), Constants.SAMPLE_RATE) +
                String.format("  min buffer size : %d samples, %d Bytes\n", minBytes / BYTE_OF_SAMPLE, minBytes) +
                String.format("  buffer size     : %d samples, %d Bytes\n", bufferSampleSize, BYTE_OF_SAMPLE*bufferSampleSize) +
                String.format("  read chunk size : %d samples, %d Bytes\n", readChunkSize, BYTE_OF_SAMPLE*readChunkSize) +
                String.format("  FFT length      : %d\n", Constants.FFT_LENGTH) +
                String.format("  nFFTAverage     : %d\n", Constants.N_FFT_AVERAGE));

        if (record.getState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "Arithmetic.SamplingLoop::run(): Fail to initialize AudioRecord()");
//            activity.analyzerViews.notifyToast("Fail to initialize recorder.");
            // If failed somehow, leave user a chance to change preference.
            return;
        }

        short[] audioSamples = new short[readChunkSize];
        int numOfReadShort;

        stft = new STFT(Constants.FFT_LENGTH, Constants.SAMPLE_RATE, Constants.WINDOW_FUNCTION);
        stft.setAWeighting(false);


        // Start recording
        try {
            record.startRecording();
        } catch (IllegalStateException e) {
            Log.e(TAG, "Fail to start recording.");
//            activity.analyzerViews.notifyToast("Fail to start recording.");
            return;
        }

        // Main loop
        scunusCounter = 0;
        List<Integer> signalBins = new LinkedList<>();
        while (isRunning) {
            long startTime = System.currentTimeMillis();
            // Read data
            numOfReadShort = record.read(audioSamples, 0, readChunkSize);

            stft.feedData(audioSamples, numOfReadShort);

            // If there is new spectrum data, do plot
            if (stft.nElemSpectrumAmp() >= Constants.N_FFT_AVERAGE) {
                // Update spectrum or spectrogram
                final double[] spectrumDB = stft.getSpectrumAmpDB();
                stft.calculatePeak();
//                if(stft.maxAmpFreq > 17990 && stft.maxAmpFreq < 18010) {
//                    Log.e("max Freq", String.valueOf(stft.maxAmpFreq));
//                    Log.e("max AmpDB", String.valueOf(stft.maxAmpDB));
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView hzTextView = (TextView) activity.findViewById(R.id.hzTextView);
                            hzTextView.setText((int)stft.maxAmpFreq + ": "
                                    + (int)stft.maxAmpDB + ": "
                                    +  20*Math.log10(stft.getRMSFromFT()) + ": "
                                    +  20*Math.log10(stft.getRMS()));
                        }
                    }); // END - runOnUiThread
//                }

                double rms = 20*Math.log10(stft.getRMSFromFT());
                int maxPowerBin = -1;
                double maxPower = spectrumDB[Constants.START_TONE_BIN];

                for(int i = Constants.START_TONE_BIN; i <= Constants.END_TONE_BIN; i++){
                    if( (Math.abs(Math.abs(spectrumDB[i]) - Math.abs(rms)) < 10) &&
                            (spectrumDB[i] >= maxPower)){
                        maxPowerBin = i;
                        maxPower = spectrumDB[i];
                    }
                }

                if(maxPowerBin != -1){
                    Log.e("Bin", String.valueOf(maxPowerBin));
                    signalBins.add(maxPowerBin);
                    scunusCounter++;
                }

            }
            long endTime = System.currentTimeMillis();
            Log.i("startTime", String.format("%d", startTime));
            Log.i("endTime", String.format("%d", endTime));
            Log.i("loop duration", String.format("%d", endTime - startTime));
        } // END - while(isRunning)

//        Log.i(TAG, "Arithmetic.SamplingLoop::Run(): Actual sample rate: " + recorderMonitor.getSampleRate());
        Log.i(TAG, "Arithmetic.SamplingLoop::Run(): Stopping and releasing recorder.");
        record.stop();
        record.release();
        Log.e("ScunusCount", String.valueOf(scunusCounter));

        Demodulator demodulator = new Demodulator();
        demodulator.demodulate(signalBins);

    }


    public void finish() {
        isRunning = false;
        interrupt();
    }
}
