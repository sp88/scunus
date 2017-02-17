package com.example.sahan.scunus.Sensors;

import android.content.Context;
import android.hardware.SensorEvent;


public interface ISensorListener {
    void listen(SensorEvent sensorEvent, Context context);
}
