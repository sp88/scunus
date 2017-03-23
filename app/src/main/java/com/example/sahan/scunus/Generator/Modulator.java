package com.example.sahan.scunus.Generator;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class Modulator {

    private final int duration = 1; // seconds
    private final int sampleRate = 44100;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    private final double freqOfTone = 18000; // hz
    private final byte generatedSnd[] = new byte[2 * numSamples];
    private final HashMap<String, Integer> hmap = new HashMap<>();

    {
        hmap.put("00", 18200);
        hmap.put("01", 18400);
        hmap.put("10", 18600);
        hmap.put("11", 18800);
    }

    public String getModulatedString(String data){

        byte[] bArray = data.getBytes(StandardCharsets.UTF_8);
        StringBuilder binaryString = new StringBuilder();
        for (byte b: bArray){
            binaryString.append('0');
            binaryString.append(Integer.toBinaryString(b));
        }

        Log.e( "EditText value", binaryString.toString());
//        Log.e( "EditText value", Arrays.toString(data.getBytes(StandardCharsets.UTF_8)) );
        return binaryString.toString();
    }

    byte[] getTone(String msg){
        // get modulated string
        String modulatedString = getModulatedString(msg);

        // create 'start tone'


        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/freqOfTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }

        // create 'end tone'

        return generatedSnd;
    }

}
