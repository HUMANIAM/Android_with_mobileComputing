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
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.File;

/**
 * Created by hp on 4/28/2018.
 */

public class AccelerometerService extends Service implements SensorEventListener {
    private SensorManager mSensorManager = null;
    private Sensor mAccelerometer = null;
    private final float alpha = 0.9f;
    private RWFiles rwfile;
    long lastUpdate = System.currentTimeMillis();

    private final static String FILE_NAME = "accelerometer.csv";
    private float mAccelCurrent;        // current acceleration including gravity
    private float mAccelLast;           // last acceleration including gravity

    private float[] gravityForce = {0.0f, 0.0f, 9.806f};


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        rwfile = new RWFiles();

        //if file not found
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SensorData/", FILE_NAME);
        if(!file.exists()){
            String headers = "timeStamp, x, y, z, magnitude";
            rwfile.write(FILE_NAME, headers, this);
        }
        return START_STICKY;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        // Isolate the force of gravity with the low-pass filter.
        gravityForce[0] = alpha * gravityForce[0] + (1 - alpha) * sensorEvent.values[0];
        gravityForce[1] = alpha * gravityForce[1] + (1 - alpha) * sensorEvent.values[1];
        gravityForce[2] = alpha * gravityForce[2] + (1 - alpha) * sensorEvent.values[2];

        // Remove the gravity contribution with the high-pass filter.
        float x = sensorEvent.values[0] - gravityForce[0];
        float y = sensorEvent.values[1] - gravityForce[1];
        float z = sensorEvent.values[2] - gravityForce[2];

        mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
        float delta = mAccelCurrent - mAccelLast;
        mAccelLast = mAccelCurrent;

        if (Math.abs(delta)>0.5  &&  (System.currentTimeMillis() - lastUpdate)>100) {   //intervals to log readings
            lastUpdate = System.currentTimeMillis();

            String s = Float.toString(sensorEvent.timestamp) ;                      //append timeStamp
            s += String.format(", %f, %f, %f, %f", x, y, z, mAccelCurrent);         //append current readings

            //toast file for debugging
            String ss = String.format("%f | %f | %f", sensorEvent.values[0], sensorEvent.values[1], sensorEvent.values[2]);
      /*      Toast.makeText(this, ss, Toast.LENGTH_LONG).show();*/
            rwfile.write(FILE_NAME, s, this);
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
