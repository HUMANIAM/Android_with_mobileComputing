package com.example.klbc.sensors;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.io.File;


public class Sensors extends AppCompatActivity {
    private Spinner spin;                       //spinner contains sensors

    //store data in this directory path
    public static String path;
    public static File dir;

    //sensors files
    private static final String ACCELEROMETER    = "accelerometer.csv";
    private static final String GYROSCOPE        = "gyroscope.csv";
    private static final String GPS              = "gps.csv";
    private static final String COMPASS          = "compass.csv";
    private static final String WIFI             = "wifi.csv";
    private static final String BAROMETER        = "barometer.csv";

    Button btnsacc, btnsgyro, btnsgps, btnscompass, btnsbarometer;
    Button btneacc, btnegyro, btnegps, btnecompass, btnebarometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensors);

        /*
        * this spinner contains sensors that we will deal with
        * choosing any one of them will show the gathered data file*/

        spin = findViewById(R.id.spinner1);
        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(Sensors.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.mobileSensors));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin.setAdapter(adapter);*/
        spin.setOnItemSelectedListener(new SensorSpinner());

        //create objects of buttons for start and stop buttons
        btnsacc = findViewById(R.id.startAccelerometer);
        btneacc = findViewById(R.id.endAccelerometer);

        btnsgyro = findViewById(R.id.startGyroscope);
        btnegyro = findViewById(R.id.endGyroscope);

        btnsgps = findViewById(R.id.startGps);
        btnegps = findViewById(R.id.endGps);

        btnscompass = findViewById(R.id.startCompass);
        btnecompass = findViewById(R.id.endCompass);

        btnsbarometer = findViewById(R.id.startBarometer);
        btnebarometer = findViewById(R.id.endBarometer);

        //create directory to store data of sensors
        path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/SensorData/";
        dir = new File(path);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    //callback for start and stop accelerometer
    public void onClickAcc(View view) {
        Intent service = new Intent(getApplicationContext(), AccelerometerService.class);
        switch (view.getId()) {

            case R.id.startAccelerometer:
                this.startService(service);
                btnsacc.setEnabled(false);
                btneacc.setEnabled(true);
                break;
            case R.id.endAccelerometer:
                this.stopService(service);
                btnsacc.setEnabled(true);
                btneacc.setEnabled(false);
                break;
        }
    }

    //callback of start and stop of gyroscope
    public void onClickGyro(View view) {
        Intent service = new Intent(getApplicationContext(), GyroscopeService.class);
        switch (view.getId()) {

            case R.id.startGyroscope:
                this.startService(service);
                btnsgyro.setEnabled(false);
                btnegyro.setEnabled(true);
                break;
            case R.id.endGyroscope:
                this.stopService(service);
                btnsgyro.setEnabled(true);
                btnegyro.setEnabled(false);
                break;
        }
    }

    //callback of start and stop of compass
    public void onClickCompass(View view) {
        Intent service = new Intent(getApplicationContext(), CompassService.class);
        switch (view.getId()) {

            case R.id.startCompass:
                this.startService(service);
                btnscompass.setEnabled(false);
                btnecompass.setEnabled(true);
                break;
            case R.id.endCompass:
                this.stopService(service);
                btnscompass.setEnabled(true);
                btnecompass.setEnabled(false);
                break;
        }
    }

    //callback of start and stop of compass
    public void onClickBarometer(View view) {
        Intent service = new Intent(getApplicationContext(), BarometerService.class);
        switch (view.getId()) {

            case R.id.startBarometer:
                this.startService(service);
                btnsbarometer.setEnabled(false);
                btnebarometer.setEnabled(true);
                break;
            case R.id.endBarometer:
                this.stopService(service);
                btnsbarometer.setEnabled(true);
                btnebarometer.setEnabled(false);
                break;
        }
    }

    //callback of start and stop of gps
    public void onClickGps(View view) {
        Intent service = new Intent(getApplicationContext(), GpsService.class);
        switch (view.getId()) {

            case R.id.startGps:
                this.startService(service);
                btnsgps.setEnabled(false);
                btnegps.setEnabled(true);
                break;
            case R.id.endGps:
                this.stopService(service);
                btnsgps.setEnabled(true);
                btnegps.setEnabled(false);
                break;
        }
    }

    //callback for start and stop wifi
    public void onClickWifi(View view) {
        showWifi();
    }

    public void showWifi() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle("Wifi Networks")
                .setMessage("wifi networks")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(R.mipmap.wifi)
                .show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.stopService(new Intent(getApplicationContext(), AccelerometerService.class));
        this.stopService(new Intent(getApplicationContext(), GyroscopeService.class));
        this.stopService(new Intent(getApplicationContext(), CompassService.class));
        this.stopService(new Intent(getApplicationContext(), GpsService.class));
    }



}
