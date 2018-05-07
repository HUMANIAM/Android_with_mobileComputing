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

public class GyroscopeService extends Service implements SensorEventListener {
    private SensorManager mSensorManager = null;
    private Sensor mGyrosensor = null;
    private RWFiles rwfile;
    private float angle = 0.0f;
    float[] dang ={0.0f, 0.0f, 0.0f};

    long lastUpdate = System.currentTimeMillis();
    private final static String FILE_NAME = "gyroscope.csv";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyrosensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mSensorManager.registerListener(this, mGyrosensor, SensorManager.SENSOR_DELAY_UI);
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
        //read rate of rotations around axises without any normalization
        long timestamp = sensorEvent.timestamp;
        float dt = timestamp - lastUpdate;
        lastUpdate = timestamp;

        float roraX = sensorEvent.values[0];        //rate of rotation around x
        float roraY = sensorEvent.values[1];         //rate of rotation around y
        float roraZ = sensorEvent.values[2];          //rate of rotation around z
        dang[0] = dang[0] + roraX*dt;
        dang[1] = dang[1] + roraY*dt;
        dang[2] = dang[2] + roraZ*dt;

        float magdTheta = (float) Math.sqrt((double) (dang[0] * dang[0] + dang[0] * dang[1] + dang[2] * dang[2]));


        if ((System.currentTimeMillis() - lastUpdate)>100) {              //intervals to log readings
            lastUpdate = System.currentTimeMillis();
            String ss = Long.toString(timestamp);
            rwfile.write(FILE_NAME, ss, this);

            //toast file for debugging
            /*ss += String.format(", %f,  %f, %f, %f", roraX, roraY, roraZ, magdTheta);
            Toast.makeText(this, ss, Toast.LENGTH_LONG).show();*/

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
