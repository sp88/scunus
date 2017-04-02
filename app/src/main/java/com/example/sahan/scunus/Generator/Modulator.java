package com.example.sahan.scunus.Generator;

import android.util.Log;

import com.example.sahan.scunus.Alphabet;
import com.example.sahan.scunus.Constants;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Hex;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class Modulator {

    private double startEndToneDuration = 0.1;  // seconds
    private double dataToneDuration =  0.0115; // seconds
    private byte generatedSnd[];
    private int sampleRate = Constants.SAMPLE_RATE;
    private int idx = 0;
    private HashMap alphabet = new Alphabet().getAlphabet();

    public String toHex(String arg) {
        return String.format("%x", new BigInteger(1, arg.getBytes(StandardCharsets.UTF_8)));
    }

//    private String getModulatedString(String data){

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
//        return toHex(data);
//    }

    void zip(String msg){
        try {
            // Encode a String into bytes
            String inputString = msg;
            byte[] input = inputString.getBytes("UTF-8");

            // Compress the bytes
            byte[] output = new byte[100];
            Deflater compresser = new Deflater();
            compresser.setInput(input);
            compresser.finish();
            int compressedDataLength = compresser.deflate(output);
            compresser.end();
            Log.e("compressed", new String(output));

            // Decompress the bytes
            Inflater decompresser = new Inflater();
            decompresser.setInput(output, 0, compressedDataLength);
            byte[] result = new byte[100];
            int resultLength = decompresser.inflate(result);
            decompresser.end();

            // Decode the bytes into a String
            String outputString = new String(result, 0, resultLength, "UTF-8");
            Log.e("decompressed", outputString);
        } catch(java.io.UnsupportedEncodingException ex) {
            ex.printStackTrace();
        } catch (java.util.zip.DataFormatException ex) {
            ex.printStackTrace();
        }
    }

    byte[] getTone(String msg){
        // calculate samples of full signal
        // 2* as two symbols per character
        // start samples + data samples + end samples = total samples of the signal
        // create full signal array
        final int signalSample =  (int)  ((startEndToneDuration * sampleRate) +
                                    (2 * msg.length() * dataToneDuration * sampleRate) +
                                    (startEndToneDuration * sampleRate));
        generatedSnd = new byte[2 * signalSample];
        Log.e("signalSample", String.valueOf(signalSample));
        Log.e("generatedSnd length", String.valueOf(generatedSnd.length));

        // get modulated string
//        String modulatedString = getModulatedString(msg);
        Hex hex = new Hex();
        Object modulatedString = new char[1];
        try {
            modulatedString = hex.encode(msg);
        } catch (EncoderException e) {
            e.printStackTrace();
        }
        String hexString = new String((char[]) modulatedString);
        Log.e("Hex string", hexString);
        Object b = new byte[1];
        try {
            b = hex.decode(modulatedString);
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        String str = new String((byte [])b, StandardCharsets.UTF_8);
        Log.e("Original string", str);

        idx = 0;
        // create 'start tone'
        fillTone(startEndToneDuration, (Double) alphabet.get("START"));
//        fillTone(startEndToneDuration, 18734.0);

        // fill data tones
        for(char c : (char[]) modulatedString){
//        for(int i=0; i < hexString.length(); i++){
            fillTone(dataToneDuration,
                        (Double) alphabet.get(String.valueOf(c)));
        }

        // create 'end tone'
        fillTone(startEndToneDuration, (Double) alphabet.get("END"));

        return generatedSnd;
    }

    private void fillTone(double duration, double freqOfTone){
        int numSamples = (int) (duration * sampleRate);
        double sample[] = new double[numSamples];
        Log.e("numSamples", String.valueOf(numSamples));
        Log.e("freqOfTone", String.valueOf(freqOfTone));

        // fill out the array
        for (int i = 0; i < numSamples; ++i) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate /freqOfTone));
        }

        // convert to 16 bit pcm sound array
        // assumes the sample buffer is normalised.

        for (final double dVal : sample) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * Short.MAX_VALUE));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSnd[idx++] = (byte) (val & 0x00ff);
            generatedSnd[idx++] = (byte) ((val & 0xff00) >>> 8);
        }
    }

}
