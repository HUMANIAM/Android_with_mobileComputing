package com.example.klbc.sensors;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

/**
 * Created by hp on 4/27/2018.
 */

public class SensorSpinner implements AdapterView.OnItemSelectedListener {

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if(pos == 0){

        }else if(pos == 1){
            Toast.makeText(parent.getContext(), "show accelermoter data", Toast.LENGTH_LONG).show();
            Log.d("yes", "world");
        }else if(pos == 2){
            Toast.makeText(parent.getContext(), "show gyroscope data", Toast.LENGTH_LONG).show();
            Log.d("yes", "world");
        }else if(pos == 3){
            Toast.makeText(parent.getContext(), "show gps data", Toast.LENGTH_LONG).show();
            Log.d("yes", "world");
        }else if(pos == 4){
            Toast.makeText(parent.getContext(), "show compass data", Toast.LENGTH_LONG).show();
            Log.d("yes", "world");
        }else {
            Toast.makeText(parent.getContext(), "show barometer data", Toast.LENGTH_LONG).show();
            Log.d("yes", "world");
        }


    }
}
