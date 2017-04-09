package com.example.sahan.scunus.Demodulator;


import android.util.Log;
import android.util.SparseArray;

import com.example.sahan.scunus.Alphabet;
import com.example.sahan.scunus.Constants;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class Demodulator {

    public String demodulate(List<Integer> signalList){
        if(signalList.size() == 0){
            Log.e("demodulate", "Signal Bins == 0");
            return "Try Again";
        }
        if(signalList.get(0) == Constants.START_TONE_BIN &&
                signalList.get(signalList.size() - 1) == Constants.END_TONE_BIN) {

            SparseArray binMap = new Alphabet().getBinMap();

            /*
            for(int i = 0; i < binMap.size(); i++) {
                int key = binMap.keyAt(i);
                // get the object by the key.
                Character character = (Character) binMap.get(key);
                Log.e("binmap", Constants.START_TONE_BIN + 1 + i + ": " + character);
            }
            */

            StringBuilder sb = new StringBuilder();
            // convert bin values to hex symbols
//            for (int bin : signalList) {
//                if (bin != Constants.START_TONE_BIN && bin != Constants.END_TONE_BIN) {
//                    sb.append(binMap.get(bin));
//                }
//            }

            int prevTone = 0;
            int count = 0;
            for (int bin : signalList) {
                if (bin > Constants.START_TONE_BIN && bin < Constants.END_TONE_BIN) {
                    // if same tone and the count is less than 4 (0.0115 * 4 = 0.046 MAX)
                    if(prevTone == bin && count < 4){
                        count++;
                    } else if (prevTone == bin && count == 4){
                        // if enough bins are present add to list
                        sb.append(binMap.get(bin));
                        count = 1;
                    }

                } // END (START < bin > END)
                if (prevTone != bin && prevTone != 0) {
                    // if not same tone
                    // if enough bins are present add to list
                    if(count >= 1){
                        sb.append(binMap.get(prevTone));
                    }
                    count = 1;
                }
                prevTone = bin;
            } // END signalList Loop

            // convert hex symbols to alphabetic values
            String hexString = sb.toString();
            Log.e("hexString", hexString);
//            if (hexString.length() % 2 != 0) {
//                Log.e("demodulate", "Record does not have enough info!");
//                return "Try Again";
//            }

            try {
                byte[] bytes = Hex.decodeHex(hexString.toCharArray());
                String msg = new String(bytes, StandardCharsets.UTF_8);
                Log.e("Hex Decode", msg);
                return msg;
            } catch (DecoderException e) {
                Log.e("Hex Decode", "DecoderException");
                return "Try Again";
            }

//                sb = new StringBuilder();
//                for(int i=0; i < hexString.length(); i++){
//
//                }

                // create a string from the retrieved values
//                String actualString = sb.toString();
//                Log.e("demodulate", "Actual Text: " + actualString);


        } else {
            Log.e("demodulate", "Record does not contain START AND END tones!");
        }

        return "Try Again";
    }
}
