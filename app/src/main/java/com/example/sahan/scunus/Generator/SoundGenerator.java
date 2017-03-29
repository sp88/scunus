package com.example.sahan.scunus.Generator;


import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

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


    private void playSound(byte[] generatedSnd){
        final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                Constants.SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, generatedSnd.length,
                AudioTrack.MODE_STREAM);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
        audioTrack.play();
//        audioTrack.stop();
//        mAudioTrack.release();
    }



//    private void playSoundSignal(double frequency, int duration){
//// AudioTrack definition
//        int mBufferSize = AudioTrack.getMinBufferSize(44100,
//                AudioFormat.CHANNEL_OUT_STEREO,
//                AudioFormat.ENCODING_PCM_16BIT);
//
//        AudioTrack mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
//                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
//                mBufferSize, AudioTrack.MODE_STREAM);
//
//        // Sine wave
//        double[] mSound = new double[44100];
//        short[] mBuffer = new short[duration];
//        for (int i = 0; i < mSound.length; i++) {
//            mSound[i] = Math.sin((2.0*Math.PI * i/(44100/frequency)));
//            mBuffer[i] = (short) (mSound[i]*Short.MAX_VALUE);
//        }

//        mAudioTrack.setStereoVolume(AudioTrack.getMaxVolume(), AudioTrack.getMaxVolume());
//        mAudioTrack.play();
//
//        mAudioTrack.write(mBuffer, 0, mSound.length);
//        mAudioTrack.stop();
//        mAudioTrack.release();

//        int count = (int)(44100.0 * 2.0 * (duration / 1000.0)) & ~1;
//        short[] samples = new short[count];
//        for(int i = 0; i < count; i += 2){
//            short sample = (short)(Math.sin(2 * Math.PI * i / (44100.0 / frequency)) * 0x7FFF);
//            samples[i] = sample;
//            samples[i + 1] = sample;
//        }
//        AudioTrack track = new AudioTrack(AudioManager.STREAM_MUSIC, 44100,
//                AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT,
//                count * (Short.SIZE / 8), AudioTrack.MODE_STATIC);
//
//        track.write(samples, 0, count);
//        track.play();
//        track.stop();
//        track.release();
//        Log.e("Finish", "Finish");
//
//    }
}
