package com.example.sahan.scunus.Generator;

import android.util.Log;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Modulator {

    private final int duration = 1; // seconds
    private final int sampleRate = 44100;
    private final int numSamples = duration * sampleRate;
    private final double sample[] = new double[numSamples];
    private final double freqOfTone = 18130; // hz
    private final byte generatedSnd[] = new byte[2 * numSamples];

    public String toHex(String arg) {
        return String.format("%x", new BigInteger(1, arg.getBytes(StandardCharsets.UTF_8)));
    }

    private String getModulatedString(String data){

//        byte[] bArray = data.getBytes(StandardCharsets.UTF_8);
//        StringBuilder binaryString = new StringBuilder();
//        for (byte b: bArray){
//            binaryString.append('0');
//            binaryString.append(Integer.toBinaryString(b));
//        }
//
//        Log.e( "EditText value", binaryString.toString());
////        Log.e( "EditText value", Arrays.toString(data.getBytes(StandardCharsets.UTF_8)) );
//        return binaryString.toString();
        return toHex(data);
    }

    byte[] getTone(String msg){
        // get modulated string
//        String modulatedString = getModulatedString(msg);
        Hex hex = new Hex();
        Object modulatedString = new char[1];
        try {
            modulatedString = hex.encode(msg);
        } catch (EncoderException e) {
            e.printStackTrace();
        }
        Log.e("Hex string", new String((char[]) modulatedString));
        Object b = new byte[1];
        try {
            b = hex.decode(modulatedString);
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        String str = new String((byte [])b, StandardCharsets.UTF_8);
        Log.e("Original string", str);

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
