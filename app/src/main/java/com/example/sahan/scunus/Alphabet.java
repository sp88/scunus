package com.example.sahan.scunus;


import java.util.HashMap;

public class Alphabet {
    private final HashMap<String, Integer> hmap = new HashMap<>();

    public Alphabet (){
        hmap.put("START", 18002);
        hmap.put("0", 18045);
        hmap.put("1", 18088);
        hmap.put("2", 18131);
        hmap.put("3", 18174);
        hmap.put("4", 18217);
        hmap.put("5", 18260);
        hmap.put("6", 18303);
        hmap.put("7", 18346);
        hmap.put("8", 18389);
        hmap.put("9", 18432);
        hmap.put("a", 18475);
        hmap.put("b", 18519);
        hmap.put("c", 18562);
        hmap.put("d", 18605);
        hmap.put("e", 18648);
        hmap.put("f", 18691);
        hmap.put("END", 18734);
    }

    public HashMap getAlphabet(){
        return hmap;
    }
}
