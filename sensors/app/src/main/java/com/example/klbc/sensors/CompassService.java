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
 * here we try to deduce the orientation of the device relative to the world's frame
 * reference from the read data of accelerometer and magnotemeter by merging and
 * translating
 */

public class CompassService extends Service implements SensorEventListener {
    private SensorManager mSensorManager = null;
    final float[] rotationMatrix = new float[9];
    final float[] orientationAngles = new float[3];
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];
    private Sensor mCompass = null;
    private Sensor mAccelerometer = null;
    private Sensor mMagnotemoter = null;
    private RWFiles rwfile;

    long lastUpdate = System.currentTimeMillis();
    private final static String FILE_NAME = "compass.csv";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //mCompass = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mMagnotemoter = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //register sensors to the sensor manager binding to current service
        //mSensorManager.registerListener(this, mCompass, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mMagnotemoter, SensorManager.SENSOR_DELAY_UI);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);

        rwfile = new RWFiles();

        //if file not found
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SensorData/", FILE_NAME);
        if(!file.exists()){
            String headers = "timeStamp, Azimuth, Pitch, Roll";
            rwfile.write(FILE_NAME, headers, this);
        }
        return START_STICKY;
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
   /*     //read orientation around axis
        float azimuth =   sensorEvent.values[0];
        float pitch   =   sensorEvent.values[1];
        float roll    =   sensorEvent.values[2];

        long timestmp = sensorEvent.timestamp;

        if ((System.currentTimeMillis() - lastUpdate)>100) {              //intervals to log readings
            lastUpdate = System.currentTimeMillis();
            String ss = Long.toString(timestmp);
            //toast file for debugging
            ss += String.format(", %f,  %f, %f", azimuth, pitch, roll);
           // Toast.makeText(this, ss, Toast.LENGTH_LONG).show();
            rwfile.write(FILE_NAME, ss, this);
        }*/

        switch (sensorEvent.sensor.getType()){
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(sensorEvent.values, 0, mAccelerometerReading,
                        0, mAccelerometerReading.length);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(sensorEvent.values, 0, mMagnetometerReading,
                        0, mMagnetometerReading.length);
                break;
        }
        updateOrientationAngles(sensorEvent);
    }

    public void updateOrientationAngles(SensorEvent sensorEvent) {
        // Update rotation matrix, which is needed to update orientation angles.
        mSensorManager.getRotationMatrix(rotationMatrix, null,
                mAccelerometerReading, mMagnetometerReading);

        mSensorManager.getOrientation(rotationMatrix, orientationAngles);

        //read orientation around axis
        float azimuth = orientationAngles[0];
        float pitch = orientationAngles[1];
        float roll = orientationAngles[2];

        long timestmp = sensorEvent.timestamp;
        if ((System.currentTimeMillis() - lastUpdate) > 100) {              //intervals to log readings
            lastUpdate = System.currentTimeMillis();
            String ss = Long.toString(timestmp);
            ss += String.format(", %f,  %f, %f", azimuth, pitch, roll);
            //toast file for debugging
            //ss += String.format(", %f,  %f, %f", azimuth, pitch, roll);
            // Toast.makeText(this, ss, Toast.LENGTH_LONG).show();
            rwfile.write(FILE_NAME, ss, this);
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
