package com.example.klbc.sensors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.File;

/**
 * Created by hp on 4/28/2018.
 */

public class BarometerService extends Service implements SensorEventListener {
    private SensorManager mSensorManager = null;
    private Sensor mBarometer = null;
    private RWFiles rwfile;

    private float lastp = 0;

    private final static String FILE_NAME = "barometer.txt";

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //mBarometer = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mBarometer = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        //register sensors to the sensor manager binding to current service
         boolean found = mSensorManager.registerListener(this, mBarometer, SensorManager.SENSOR_DELAY_UI);

         if(found)
             Toast.makeText(this, "Found", Toast.LENGTH_LONG).show();
         else
         Toast.makeText(this, "Not Found", Toast.LENGTH_LONG).show();

        rwfile = new RWFiles();

        //if file not found
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SensorData/", FILE_NAME);
        if(!file.exists()){
            String headers = "timeStamp, pressure";
            rwfile.write(FILE_NAME, headers, this);
        }
        return START_STICKY;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //read orientation around axis
        float pressure =   sensorEvent.values[0];
        Toast.makeText(this, String.format(", %f", pressure), Toast.LENGTH_LONG).show();
        if ((pressure - lastp)>0.1) {              //intervals to log readings
            lastp = pressure;
            String ss = Long.toString(sensorEvent.timestamp);
            //toast file for debugging
            ss += String.format(", %f", pressure);
             Toast.makeText(this, ss, Toast.LENGTH_LONG).show();
            //rwfile.write(FILE_NAME, ss, this);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    @Override
    public void onDestroy() {
        mSensorManager.unregisterListener(this);
        mSensorManager = null;
        super.onDestroy();
        //SystemClock.sleep(1000);
        stopSelf();
    }
}
