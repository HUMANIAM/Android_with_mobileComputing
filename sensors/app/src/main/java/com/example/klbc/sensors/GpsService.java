package com.example.klbc.sensors;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import java.io.File;

/**
 * Created by hp on 4/28/2018.
 */

public class GpsService extends Service implements LocationListener {

    private LocationManager locationManager;
    private RWFiles rwfile;
    private final static String FILE_NAME = "gps.csv";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //define location manager to register listeners to gps_location change
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        rwfile = new RWFiles();
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SensorData/", FILE_NAME);


        if (!file.exists()) {
            String headers = "timeStamp, latitude, longitude, altitude";
            rwfile.write(FILE_NAME, headers, this);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(this, "you need permissions from the user", Toast.LENGTH_LONG).show();
        } else {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
        }

        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        String loc = location.getLatitude() + "," + location.getLongitude() + "," + location.getAltitude();
        rwfile.write(FILE_NAME, loc, this);
        Toast.makeText(this, loc, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }

    @Override
    public void onProviderEnabled(String s) { }

    @Override
    public void onProviderDisabled(String s) { }
}