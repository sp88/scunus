package com.example.sahan.scunus;


import java.util.HashMap;

public class Alphabet {
    private final HashMap<String, Double> hmap = new HashMap<>();

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
    }

    public HashMap getAlphabet(){
        return hmap;
    }
}
