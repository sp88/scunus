package com.example.sahan.scunus.Generator;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import com.example.sahan.scunus.Constants;

public class SoundGenerator extends Thread {

    private String msg;

    public SoundGenerator(String _msg){
        msg = _msg;
    }

    @Override
    public void run(){
        generateSignal();
    }

    private void generateSignal(){
        //TODO: Implement F.E.C
        // Modulate message
        Modulator modulator = new Modulator();

        // play sound signal
        byte[] genSound = modulator.getTone(msg);
        playSound(genSound);
//        playSoundSignal(18000, 44100*5);
    }

    private AudioTrack audioTrack;
    private void playSound(byte[] generatedSnd){
        try {
            Thread.sleep(500);
            audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    Constants.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                    AudioTrack.MODE_STREAM);
        } catch (IllegalStateException | InterruptedException ie){
            Log.e("SoundGenerator", ie.getMessage());
            return;
        }
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
//        audioTrack.stop();
//        mAudioTrack.release();
    }

    public void finish(){
        if(audioTrack != null){
            audioTrack.stop();
            audioTrack.release();
        }
    }

}
