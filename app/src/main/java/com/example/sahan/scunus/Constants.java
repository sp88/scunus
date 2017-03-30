package com.example.sahan.scunus;

public class Constants {
    public static final int SAMPLE_RATE = 44100;
    public static final int FFT_LENGTH = 1024;
    public static final int N_FFT_AVERAGE = 1;
    public static final double VALUE_MAX = 32767.0;   // Maximum signal value
    public static final int START_TONE_BIN = 418; // 418	44100	1024	 18,001.76
    public static final int END_TONE_BIN = 435; // 435	44100	1024	 18,733.89
    public static final String WINDOW_FUNCTION = "Hanning";
    public static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    public static final String SENDER_ACTIVITY = "com.example.sahan.scunus.SenderActivity";
    public static final String RECEIVER_ACTIVITY = "com.example.sahan.scunus.ReceiverActivity";
}
