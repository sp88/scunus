package com.example.sahan.scunus;


import android.util.SparseArray;

import java.util.HashMap;

public class Alphabet {
    private final HashMap<String, Double> hmap = new HashMap<>();
    private final SparseArray<Character> binmap = new SparseArray<>();

    public Alphabet (){
        hmap.put("START", 18002.0);
        hmap.put("0", 18045.0);
        hmap.put("1", 18088.0);
        hmap.put("2", 18131.0);
        hmap.put("3", 18174.0);
        hmap.put("4", 18217.0);
        hmap.put("5", 18260.0);
        hmap.put("6", 18303.0);
        hmap.put("7", 18346.0);
        hmap.put("8", 18389.0);
        hmap.put("9", 18432.0);
        hmap.put("a", 18475.0);
        hmap.put("b", 18519.0);
        hmap.put("c", 18562.0);
        hmap.put("d", 18605.0);
        hmap.put("e", 18648.0);
        hmap.put("f", 18691.0);
        hmap.put("END", 18734.0);

        // 48 ASCII is '0', 57 is '9'
        int j = Constants.START_TONE_BIN + 1; // +1 First symbol is == start+1
        for(int i = 48; i<= 57; i++){
            binmap.put( j++ , (char) i);
        }

        // 97 is 'a' and 102 is 'f'
        for(int i = 97; i<= 102; i++){
            binmap.put( j++ , (char) i);
        }
    }

    public HashMap getAlphabet(){
        return hmap;
    }

    public SparseArray getBinMap(){ return binmap;}
}
